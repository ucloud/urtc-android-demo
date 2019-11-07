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
 		//普通摄像头捕获方式,不支持切换
//        UCloudRtcSdkEnv.setRGBCaptureMode(
//                UcloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_LOCAL);
        //rgb数据捕获
        UCloudRtcSdkEnv.setRGBCaptureMode(
                UcloudRtcSdkCaptureMode.UCLOUD_RTC_CAPTURE_MODE_RGB);
        UCloudRtcSdkEngine.onRGBCaptureResult(new UcloudRTCDataProvider() {

            @Override
            public ByteBuffer provideRGBData(List<Integer> params) {
                Bitmap bitmap = null;
                if(mPictureFlag < 25){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.timg,options);
                    params.add(UcloudRTCDataProvider.RGB565_TO_I420);
                }
                else{
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.timg2,options);
                    params.add(UcloudRTCDataProvider.RGBA_TO_I420);
                }

                if(++mPictureFlag >50)
                mPictureFlag = 0;

                params.add(bitmap.getWidth());
                params.add(bitmap.getHeight());
                ByteBuffer buffer = sdkEngine.getNativeOpInterface().
                        createNativeByteBuffer(bitmap.getWidth()*bitmap.getHeight()*4);
                bitmap.copyPixelsToBuffer(buffer);
                return buffer;
            }
        });
```