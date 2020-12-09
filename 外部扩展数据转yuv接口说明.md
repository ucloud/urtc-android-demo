外部扩展数据转yuv 数据接口 ，外部实现即可。参数list数组按序放入转换类型，原始数据宽度，高度，不可放错顺序，返回捕获到的原始外部数据，使用请参考范例

```
public interface UcloudRTCDataProvider {

    //0-3 表示转换类型
    //4-7 表示rgba_stride的宽度的倍数
    //8-11 表示yuv_stride宽度移位数
    //12-15 表示uv左移位数
    public static final int RGBA_TO_I420 = 0x01001040;
    public static final int ABGR_TO_I420 = 0x01001041;
    public static final int BGRA_TO_I420 = 0x01001042;
    public static final int ARGB_TO_I420 = 0x01001043;
    public static final int RGB24_TO_I420 = 0x01001034;
    public static final int RGB565_TO_I420 = 0x01001025;
    public static final int NV21 = 0x01001090;
    public static final int I420 = 0x01001099;

    /**
     * 供sdk采集外部数据进行推流的方法，由sdk 使用者来提供数据，具体使用方式请参考rgb转yuv使用说明
     * @param params  需要传入3个参数 ，参数顺序不可颠倒，参数1：类型，例如RGBA_TO_I420，参数2：宽 参数3： 高
     * @return  扩展推流的原始rgb数据或者yuv420p，NV21
     */
    ByteBuffer provideRGBData(List<Integer> params);


    /**
     * 释放申请的buffer
     */
    void releaseBuffer();
}

```
设置外部采集模式参数

```
 //设置sdk 外部扩展模式及其采集的帧率，同时sdk内部会自动调整初始码率和最小码率
 //扩展模式只支持720p的分辨率及以下，若要自定义更高分辨率，请联系Ucloud商务定制，否则sdk会抛出异常，终止运行。
 sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.UCLOUD_RTC_SDK_VIDEO_PROFILE_EXTEND.extendParams(30,640,480));
// sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.matchValue(mVideoProfile));
//设置捕获模式，二选一
//        UCloudRtcSdkEnv.setRGBCaptureMode(
//        UcloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        //外部采集数据捕获，与普通捕获模式二选一
//        UCloudRtcSdkEnv.setCaptureMode(
//                UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND);
```



调用范例，1.8.0版本后已经支持本地外接USB摄像头，具体内容请参考demo源码内UCloudRTCLiveActivity，需要根据自己的实际情况来，范例只是做个参考
范例的本地USB输入视频格式是RGB565，另外还支持n12,n21,I420等常用视频数据格式。
```
    //扩展USB摄像头方式
    UCloudRtcSdkEnv.setCaptureMode(UCloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_EXTEND);
    //把接口实现提供给sdk
    UCloudRtcSdkEngine.onRGBCaptureResult(mUCloudRTCDataProvider);    
    //生产者消费者队列用于缓存外部摄像数据
    private ArrayBlockingQueue<ByteBuffer> mQueueByteBuffer = new ArrayBlockingQueue(8);
    
    //USB摄像头状态监控
    mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);
    //回调接口实现
    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Log.v(TAG, "onAttach:");
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "USB摄像头已连接");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUSBMonitor != null ) {
                            if (mUSBMonitor.getDeviceCount() > 0) {
                                mUSBMonitor.requestPermission(device);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            Log.v(TAG, "onConnect:");
            synchronized (mSync) {
                if (mUVCCamera != null) {
                    mUVCCamera.destroy();
                }
                isActive = isPreview = false;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        //final UVCCamera camera = initUVCCamera(ctrlBlock);
                        mUVCCamera = initUVCCamera(ctrlBlock);
                        isActive = true;
                        isPreview = true;
                    }
                }
            });
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            Log.v(TAG, "onDisconnect:");
            // XXX you should check whether the comming device equal to camera device that currently using
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    synchronized (mSync) {
                        if (mUVCCamera != null) {
                            mUVCCamera.stopPreview();
                            mUVCCamera.close();
                            mUVCCamera.destroy();
/*                            if (mPreviewSurface != null) {
                                mPreviewSurface.release();
                                mPreviewSurface = null;
                            }*/
                            isActive = isPreview = false;
                        }
                    }
                }
            });
        }

        @Override
        public void onDetach(final UsbDevice device) {
            Log.v(TAG, "onDetach:");
            ToastUtils.shortShow(UCloudRTCLiveActivity.this, "USB摄像头被移除");
        }

        @Override
        public void onCancel(final UsbDevice device) {
        }
    };
    
    //初始化摄像头，此方法仅适用于普通USB外接摄像头
    private UVCCamera initUVCCamera(USBMonitor.UsbControlBlock ctrlBlock) {
        Log.d(TAG, "initUVCCamera-----mVideoProfileSelect:" + mVideoProfileSelect + " width:" + UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getWidth()
                + " height:" + UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getHeight());
        final UVCCamera camera = new UVCCamera();
        camera.open(ctrlBlock);
        camera.setPreviewSize(
                UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getWidth(),
                UCloudRtcSdkVideoProfile.matchValue(mVideoProfileSelect).getHeight(),
                UVCCamera.FRAME_FORMAT_YUYV
        );

        SurfaceTexture surfaceTexture = mLocalVideoView.getSurfaceTexture();
        // Start preview to external GL texture
        // NOTE : this is necessary for callback passed to [UVCCamera.setFrameCallback]
        // to be triggered afterwards
        camera.startPreview();

        camera.setFrameCallback(new IFrameCallback() {
            @Override
            public void onFrame(ByteBuffer frame) {
                createFrameByteBuffer(frame); // 视频数据帧保存到缓存队列
            }
        },UVCCamera.PIXEL_FORMAT_RGB565);// UVCCamera视频输入格式
        return camera;
    }
...    
    //生产者往队列中push数据的方法
    private void createFrameByteBuffer(ByteBuffer frame){
        try {
            if(frame != null){
                //add videoSource
                mQueueByteBuffer.offer(frame, 1, TimeUnit.SECONDS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //外置数据输入监听，sdk作为消费者消费数据
    private UCloudRTCDataProvider mUCloudRTCDataProvider = new UCloudRTCDataProvider() {
        private ByteBuffer cacheBuffer;
        private ByteBuffer videoSourceData;

        @Override
        public ByteBuffer provideRGBData(List<Integer> params) {
            videoSourceData = mQueueByteBuffer.poll();
            if (videoSourceData == null) {
                //Log.d("UCloudRTCLiveActivity", "provideRGBData: " + null);
                return null;
            } else {
                //Log.d("UCloudRTCLiveActivity", "provideRGBData: ! = null");
                // 视频数据格式
                params.add(UCloudRTCDataProvider.RGB565_TO_I420); // RGB565转I420
                // 视频宽度
                params.add(640);
                // 视频高度
                params.add(480);
                if (cacheBuffer == null) {
                    cacheBuffer = sdkEngine.getNativeOpInterface().
                            createNativeByteBuffer(1280 * 720 * 4);
                } else {
                    cacheBuffer.clear();
                }
                cacheBuffer = videoSourceData;
                videoSourceData = null;
                //Log.d("UCloudRTCLiveActivity", "provideRGBData finish" + Thread.currentThread());
                cacheBuffer.position(0);
                return cacheBuffer;
            }
        }

        @Override
        public void releaseBuffer() {
            if(videoSourceData != null){
                videoSourceData = null;
            }
            if(cacheBuffer != null){
                sdkEngine.getNativeOpInterface().realeaseNativeByteBuffer(cacheBuffer);
                cacheBuffer = null;
            }
        }
    };
```
