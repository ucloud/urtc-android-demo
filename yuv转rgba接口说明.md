sdk 内部拉流数据的yuv 转rgb 数据接口 ，用来实现扩展的外部数据接收，自定义渲染。

```
public interface UcloudRTCDataReceiver {

    //0-3 表示转换类型
    //4-7 表示rgba_stride的宽度的倍数
    //8-11 表示yuv_stride宽度移位数
    //12-15 表示uv左移位数

    public static final int I420_TO_ABGR = 0x01001040;
    public static final int I420_TO_RGBA = 0x01001041;

    /**
     * 经过转换后输出到外部扩展的数据
     * @param rgbBuffer 输出的数据
     * @param width 输出数据的宽
     * @param height 输出数据的高
     */
    void onRecevieRGBAData(ByteBuffer rgbBuffer,int width ,int height);

    /**
     * 提供给sdk转换器的回调，告诉sdk需要转换成什么数据格式
     * @return
     */
    int getType();

    /**
     * sdk需要一块内存来缓存转换后的数据
     * @return 存放转换数据的buffer
     */
    ByteBuffer getCacheBuffer();

    /**
     * 释放缓存
     */
    void releaseBuffer();
}

```

使用方式,打开外部输出方式，注释掉默认输出(显示到列表)方式
```
 //外部扩展输出，和默认输出二选一
                            UCloudRtcSdkSurfaceVideoView videoViewCallBack = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                            videoViewCallBack.setFrameCallBack(mUcloudRTCDataReceiver);
                            videoViewCallBack.init(false);
                            sdkEngine.startRemoteView(info, videoViewCallBack);

  //默认输出，和外部输出代码二选一
//                        if (mVideoAdapter != null) {
//                            mVideoAdapter.addStreamView(mkey, vinfo, info);
//                        }
//                        if (vinfo != null && videoView != null) {
//                            sdkEngine.startRemoteView(info, videoView);
//                            videoView.refreshRemoteOp(View.VISIBLE);
//                        }
                            

```
App调用者实现的接收数据的对象，只是一个参考，具体实现根据调用方的具体需求而来
```
      private UcloudRTCDataReceiver mUcloudRTCDataReceiver = new UcloudRTCDataReceiver() {
        private int limit = 0;
        private ByteBuffer cache;
        @Override
        public void onRecevieRGBAData(ByteBuffer rgbBuffer, int width, int height) {
            final Bitmap bitmap = Bitmap.createBitmap(width * 1, height * 1, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(rgbBuffer);
            String name = "/mnt/sdcard/yuvrgba"+ limit+".jpg";
            if (limit++ < 5) {
                File file = new File(name);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                        out.flush();
                        out.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int getType() {
            return UcloudRTCDataReceiver.I420_TO_ABGR;
        }

        @Override
        public ByteBuffer getCacheBuffer() {
            if(cache == null){
               //根据需求来，设置最大的可能用到的buffersize，后续回调会复用这块存
                int size = 4096*2160*4;
                cache = sdkEngine.getNativeOpInterface().
                        createNativeByteBuffer(4096*2160*4);
            }
            cache.clear();
            return cache;
        }

        @Override
        public void releaseBuffer() {
            if(cache != null)
            sdkEngine.getNativeOpInterface().realeaseNativeByteBuffer(cache);
            cache = null;
        }
    };
```
