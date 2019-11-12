rgb 转yuv 数据接口 ，外部实现即可。参数list数组按序放入转换类型，原始数据宽度，高度，不可放错顺序，返回捕获到的RGB原始数据，使用请参考范例

```
public interface UcloudRTCDataProvider {
    int RGBA_TO_I420 = 16781376;
    int ABGR_TO_I420 = 16781377;
    int BGRA_TO_I420 = 16781378;
    int ARGB_TO_I420 = 16781379;
    int RGB24_TO_I420 = 16781364;
    int RGB565_TO_I420 = 16781349;
    
    //
    ByteBuffer provideRGBData(List<Integer> var1);
}
```



使用范例，RoomActivity
```
//设置捕获模式
//        UCloudRtcSdkEnv.setRGBCaptureMode(
//                UcloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        //rgb数据捕获
        UCloudRtcSdkEnv.setRGBCaptureMode(
                UcloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_RGB);

//生产者消费者队列
private Queue<RGBSourceData> mQueue = new LinkedList();

//生产者往队列中push数据
TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                    synchronized (mUCloudRTCDataProvider){
                        RGBSourceData sourceData;
                        Bitmap bitmap = null;
                        int type;
                        if(mPictureFlag < 25){
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.timg,options);
                            type = UcloudRTCDataProvider.RGB565_TO_I420;
                        }
                        else{
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.timg2,options);
                            type = UcloudRTCDataProvider.RGBA_TO_I420;
                        }

                        if(++mPictureFlag >50)
                            mPictureFlag = 0;
                        sourceData = new RGBSourceData(bitmap,bitmap.getWidth(),bitmap.getHeight(),type);
                        mQueue.add(sourceData);
                    }

            }
        };
        if(UCloudRtcSdkEnv.getCaptureMode() == UcloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_RGB){
            mTimer.schedule(timerTask,0,40);
            UCloudRtcSdkEngine.onRGBCaptureResult(mUCloudRTCDataProvider);
        }

```



```
 		//sdk作为消费者消费数据
     private UcloudRTCDataProvider mUCloudRTCDataProvider = new UcloudRTCDataProvider() {
        private ByteBuffer cacheBuffer;
        private RGBSourceData rgbSourceData;

        @Override
        public ByteBuffer provideRGBData(List<Integer> params) {
            rgbSourceData = mQueue.poll();
            if(rgbSourceData == null){
                return null;
            }else{
                params.add(rgbSourceData.getType());
                params.add(rgbSourceData.getWidth());
                params.add(rgbSourceData.getHeight());
                if(cacheBuffer == null){
                    cacheBuffer = sdkEngine.getNativeOpInterface().
                            createNativeByteBuffer(4096*2160*4);
                }else{
                    cacheBuffer.clear();
                }
                cacheBuffer.limit(rgbSourceData.getWidth()*rgbSourceData.getHeight()*4);
//                ByteBuffer buffer = sdkEngine.getNativeOpInterface().
//                        createNativeByteBuffer(bitmap.getWidth()*bitmap.getHeight()*4);
                rgbSourceData.getSrcData().copyPixelsToBuffer(cacheBuffer);
                rgbSourceData.getSrcData().recycle();
                return cacheBuffer;
            }
        }
        //释放数据
        public void releaseBuffer(){
            if(rgbSourceData != null && !rgbSourceData.getSrcData().isRecycled()){
                rgbSourceData.getSrcData().recycle();
            }
            if(cacheBuffer != null){
                sdkEngine.getNativeOpInterface().realeaseNativeByteBuffer(cacheBuffer);
            }
        }
    };
```