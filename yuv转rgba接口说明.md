rgb 转yuv 数据接口 ，外部实现即可。参数list数组按序放入转换类型，原始数据宽度，高度，不可放错顺序，返回捕获到的RGB原始数据，使用请参考范例

```
public interface UcloudRTCDataReceiver {

    //0-3 表示转换类型
    //4-7 表示rgba_stride的宽度的倍数
    //8-11 表示yuv_stride宽度移位数
    //12-15 表示uv左移位数

    public static final int I420_TO_ABGR = 0x01001040;
    public static final int I420_TO_RGBA = 0x01001041;


    void onRecevieRGBAData(ByteBuffer rgbBuffer,int width ,int height);

    //sdk会调用这块复用内存
    ByteBuffer getCacheBuffer();
}
```



使用范例，RoomActivity
```
  //for callback
                            UCloudRtcSdkSurfaceVideoView videoViewCallBack = new UCloudRtcSdkSurfaceVideoView(getApplicationContext());
                            videoViewCallBack.setFrameCallBack(new UcloudRTCDataReceiver() {
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
                                public ByteBuffer getCacheBuffer() {
                                    if(cache == null){
                                        //根据需求来，设置最大的可能用到的buffersize，后续回调会复用这块内存
                                        int size = 4096*2160*4;
                                        cache = ByteBuffer.allocateDirect(size);
                                    }
                                    cache.clear();
                                    return cache;
                                }
                            });
                            videoViewCallBack.init(false);
                            sdkEngine.startRemoteView(info, videoViewCallBack);
```



```
 		这里注释掉，就会采用上面扩展的方式来接收数据
 		//                        if (mVideoAdapter != null) {
//                            mVideoAdapter.addStreamView(mkey, vinfo, info);
//                        }
//                        if (vinfo != null && videoView != null) {
//                            sdkEngine.startRemoteView(info, videoView);
//                            videoView.refreshRemoteOp(View.VISIBLE);
//                        }
```