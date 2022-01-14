package com.urtcdemo.custom;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.faceunity.nama.FURenderer;
import com.faceunity.nama.IFURenderer;
import com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine;
import com.ucloudrtclib.sdkengine.openinterface.UCloudRTCDataProvider;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * 用户自定义数据采集及数据处理，接入 faceunity 美颜贴纸
 *
 * @author Richie on 2019.12.20
 */
public class RtcCameraRenderer implements Camera.PreviewCallback, UCloudRTCDataProvider {
    private static final String TAG = "CameraRenderer";
    private static final int DEFAULT_CAMERA_WIDTH = 1280;
    private static final int DEFAULT_CAMERA_HEIGHT = 720;
    private static final int PREVIEW_BUFFER_COUNT = 3;
    private Activity mActivity;
    private Camera mCamera;
    private byte[][] mPreviewCallbackBuffer;
    public int mCameraWidth = DEFAULT_CAMERA_WIDTH;
    public int mCameraHeight = DEFAULT_CAMERA_HEIGHT;
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraOrientation = 270;
    private int mCameraTextureId;
    private SurfaceTexture mSurfaceTexture;
    private boolean mIsPreviewing;
    private Handler mBackgroundHandler;
    private Handler mPosterHandler;
    private byte[] mReadbackByte;
    private int mSkippedFrames;
    private FURenderer mFURenderer;
    private UCloudRtcSdkEngine mRtcSdkEngine;
    private final Object syncObject = new Object();

    public RtcCameraRenderer(Activity activity, UCloudRtcSdkEngine sdkEngine, FURenderer.OnDebugListener listener) {
        mActivity = activity;
        mRtcSdkEngine = sdkEngine;
        FURenderer.setup(activity);

        mFURenderer = new FURenderer.Builder(activity)
                .setCreateEglContext(true)
                .setInputTextureType(IFURenderer.INPUT_TEXTURE_EXTERNAL_OES)
                .setRunBenchmark(true)
                .setOnDebugListener(listener)
                .build();
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }

    public void onResume() {
        if(mBackgroundHandler == null){
            startBackgroundThread();
        }
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onResume background thread run camera "+ mCamera);
                mFURenderer.onSurfaceCreated();
                if(mCamera == null){
//                    mCameraTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
//                    openCamera(mCameraFacing);
//                    startPreview();
                }
            }
        });
    }

    public void onPause() {
        if (mBackgroundHandler == null) {
            return;
        }
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
//                releaseCamera();
//                if (mCameraTextureId > 0) {
//                    GLES20.glDeleteTextures(1, new int[]{mCameraTextureId}, 0);
//                    mCameraTextureId = 0;
//                }
//                mFURenderer.onSurfaceDestroyed();
            }
        });
//        stopBackgroundThread();
    }

    public void onDestroy() {
        if (mBackgroundHandler == null) {
            return;
        }
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
//                releaseCamera();
//                if (mCameraTextureId > 0) {
//                    GLES20.glDeleteTextures(1, new int[]{mCameraTextureId}, 0);
//                    mCameraTextureId = 0;
//                }
                mFURenderer.onSurfaceDestroyed();
            }
        });
        stopBackgroundThread();
    }

    /**
     * 切换相机
     */
    public void switchCamera() {
        Log.d(TAG, "switchCamera: ");
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean isFront = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT;
                mCameraFacing = isFront ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT;
                releaseCamera();
                mSkippedFrames = 3;
                openCamera(mCameraFacing);
                startPreview();
                mFURenderer.onCameraChanged(mCameraFacing, mCameraOrientation);
                if (mFURenderer.getMakeupModule() != null) {
                    mFURenderer.getMakeupModule().setIsMakeupFlipPoints(mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : 1);
                }
            }
        });
    }

    private int limit = 1;
    private int limitPreview = 1;
    private boolean mHasData = false;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        synchronized (syncObject){
            mCamera.addCallbackBuffer(data);
            mSurfaceTexture.updateTexImage();
            long start = System.nanoTime();

            mFURenderer.onDrawFrameDualInput(data, mCameraTextureId, mCameraWidth, mCameraHeight,
                    mReadbackByte, mCameraHeight, mCameraWidth);
            if(!mHasData){
                mHasData = true;
            }
//            if (limitPreview++ < 4) {
//                String name = "/mnt/sdcard/urtcyuv/"+System.currentTimeMillis() +".yuv";
//                String dirName = "/mnt/sdcard/urtcyuv";
//                File dir = new File(dirName);
//                if(!dir.exists()){
//                    dir.mkdirs();
//                }
//                File file = new File(name);
//                try {
//                    FileOutputStream out = new FileOutputStream(file);
//                    out.write(mReadbackByte);
//                    out.flush();
//                    Log.d(TAG, "write yuv: " + name);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }

                long time = System.nanoTime() - start;
        }
    }

    private void openCamera(int cameraFacing) {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            int numCameras = Camera.getNumberOfCameras();
            if (numCameras <= 0) {
                throw new RuntimeException("No cameras");
            }
            for (int i = 0; i < numCameras; i++) {
                Camera.getCameraInfo(i, info);
                if (info.facing == cameraFacing) {
                    cameraId = i;
                    mCamera = Camera.open(i);
                    mCameraFacing = cameraFacing;
                    break;
                }
            }
            if (mCamera == null) {
                cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                Camera.getCameraInfo(cameraId, info);
                mCamera = Camera.open(cameraId);
                mCameraFacing = cameraId;
            }

            mCameraOrientation = info.orientation;
            CameraUtils.setCameraDisplayOrientation(mActivity, cameraId, mCamera);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            int[] size = CameraUtils.choosePreviewSize(parameters, mCameraWidth, mCameraHeight);
            mCameraWidth = size[0];
            mCameraHeight = size[1];
            mCamera.setParameters(parameters);
            Log.d(TAG, "openCamera. facing: " + (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK
                    ? "back" : "front") + ", orientation:" + mCameraOrientation + ", cameraWidth:" + mCameraWidth
                    + ", cameraHeight:" + mCameraHeight);
        } catch (Exception e) {
            Log.e(TAG, "openCamera: ", e);
            releaseCamera();
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity, "打开相机失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startPreview() {
        if (mCameraTextureId <= 0 || mCamera == null || mIsPreviewing) {
            return;
        }
        try {
            mCamera.stopPreview();
            mHasData = false;
            if (mPreviewCallbackBuffer == null) {
                mPreviewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight
                        * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
            }
            if (mReadbackByte == null) {
                mReadbackByte = new byte[mCameraWidth * mCameraHeight * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8];
            }
            mCamera.setPreviewCallbackWithBuffer(this);
            for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++) {
                mCamera.addCallbackBuffer(mPreviewCallbackBuffer[i]);
            }
            mSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
            mIsPreviewing = true;
            Log.d(TAG, "startPreview: cameraTexId:" + mCameraTextureId);
        } catch (Exception e) {
            Log.e(TAG, "startPreview: ", e);
        }
    }

    private void releaseCamera() {
        Log.d(TAG, "releaseCamera()");
        try {
            mIsPreviewing = false;
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            }
            if (mSurfaceTexture != null) {
                mSurfaceTexture.release();
                mSurfaceTexture = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "releaseCamera: ", e);
        }
    }

    private void startBackgroundThread() {
        mBackgroundHandler = new Handler();
        HandlerThread posterThread = new HandlerThread("poster", Process.THREAD_PRIORITY_BACKGROUND);
        posterThread.start();
        mPosterHandler = new Handler(posterThread.getLooper());
    }

    private void stopBackgroundThread() {
        mBackgroundHandler.getLooper().quitSafely();
        mBackgroundHandler = null;

        mPosterHandler.getLooper().quitSafely();
        mPosterHandler = null;
    }

    private ByteBuffer mCacheBuffer;

    private int time = 1;
    @Override
    public ByteBuffer provideRGBData(List<Integer> params) {

        if (mReadbackByte == null || !mHasData) {
            Log.d(TAG, "provideRGBData: no data yet");
            return null;
        }

        if (mCacheBuffer == null) {
            mCacheBuffer = mRtcSdkEngine.getNativeOpInterface().createNativeByteBuffer(mReadbackByte.length);
        } else {
            mCacheBuffer.clear();
        }
        params.add(UCloudRTCDataProvider.NV21);
        params.add(mCameraHeight);
        params.add(mCameraWidth);
        synchronized (syncObject){
            mCacheBuffer.put(mReadbackByte);
            mCacheBuffer.position(0);
//            if (limit++ < 4) {
//                String name = "/mnt/sdcard/urtcyuv/cache_"+System.currentTimeMillis() +".yuv";
//                String dirName = "/mnt/sdcard/urtcyuv";
//                File dir = new File(dirName);
//                if(!dir.exists()){
//                    dir.mkdirs();
//                }
//                File file = new File(name);
//                RandomAccessFile store = null;
//                try {
//                    if(!file.exists()){
//                        file.createNewFile();
//                    }
//                     store = new RandomAccessFile(name, "rw");
//                    // getting FileChannel from file
//                    FileChannel channel = store.getChannel();
//                    Log.d(TAG, "cache position: " + mCacheBuffer.position());
////                    mCacheBuffer.flip();
//                    channel.write(mCacheBuffer);
//                    Log.d(TAG, "write yuv_cache: " + name);
//                    store.close();
//                }catch (Exception e){
//                    e.printStackTrace();
//                    if(store!= null){
//                        try {
//                            store.close();
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            }

        }
        return mCacheBuffer;
    }

    @Override
    public void releaseBuffer() {
        if (mCacheBuffer != null) {
            mRtcSdkEngine.getNativeOpInterface().releaseNativeByteBuffer(mCacheBuffer);
            mCacheBuffer = null;
        }
    }
}
