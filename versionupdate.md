# Android SDK 版本说明

## 2.0.8
更新于2021-10-22

1.优化获取内存数值，采用getTotalPss。

2.解决进程名长度超15个字符时，cpu占用率无法获取的问题。

3.修改480p到帧率和码率，降低时延。

4.添加动态申请READ_PHONE_STATE权限，用于判断5g网络类型(api29以上支持)。

## 2.0.7
更新并发布maven于2021-10-14

1.surfaceView渲染器优化，解决在ASPECT_FIT模式下，由于view尺寸比例和视频尺寸比例一样导致的界面不刷新问题。

2.增加支持上报应用cpu和内存使用率

3.修复setPushOrientation接口的参数类型错误问题。

## 2.0.6
更新并发布maven于2021-9-30

1.增加静音本地和远端屏幕流的音效。

2.修改分辨率时，码率也动态调整。

3.支持远端流多view渲染。

4.解决先预览后发布情况下，断线重连没有重新渲染的问题。

5.解决egl内存用完程序崩溃的问题。

6.合并webrtc修改，解决frameBuffer满了以后播放卡死的问题。

7.实现客户端上报实时上下行状态。

8.日志中增加线程号。

9.解决断线重连后，后摄变成前摄的问题。

10.硬件加速开启情况下，关闭硬编码，规避某项机顶盒设备硬编有引用泄漏的问题。

11.降低音量的回调频率。

12.android api23以下并且外部视频输入时，camera的权限检查，默认返回true。

13.增加指定cameraId切换功能。

14.优化surfaceView刷新机制。

15.增加初始化之前，指定摄像头设置功能。

16.解决重连成功后，订阅流的mute状态没有同步的问题。

17.支持自定义视频参数设置(分辨率，帧率和码率)。

## 2.0.4
更新于2021-8-4

1.解决本地停止本地渲染会造成内存泄漏问题。

2.解决在connecting状态下调用startLocalRender无效，造成本地回显黑屏的问题。

3.实现部分设备切换前后摄时，跳过同侧其他Camera。

4.支持离开频道后继续保持本地回显的功能。

5.解决单音频发布时，权限检测错误导致无法发布的问题。

## 2.0.3
更新于2021-7-13

1.支持控制拉流音量的功能。

2.解决获取帧的头包错误导致崩溃问题。

3.修复订阅回调中获取mute状态不正确的问题。

## 2.0.2
更新于2021-7-2

1.修复部分使用camera1的机型，翻转前后摄像头失效的问题。

2.支持后摄闪光灯开关功能。

3.支持远端和本地镜像功能。

## 2.0.1
更新于2021-6-18

1.修复部分使用camera1的机型，切换摄像头时会崩溃的问题

2.增加硬件编码开关控制，并追加支持amlogic芯片的硬件编解码。

3.解决上下麦偶现native崩溃的问题。

## 2.0.0
更新于2021-5-27

1.sdk结构变动。

2.增加unPublishOnly接口，用于只取消发布不停止预览

3.修正部分类名、方法名、参数名等，具体修改内容如下

UCloudRtcSdkEngine接口：

UCloudRtcSdkEngine.destory->UCloudRtcSdkEngine.destroy

UCloudRtcSdkEnv类：

UCloudRtcSdkEnv.setTokenSeckey->UCloudRtcSdkEnv.setTokenSecKey

UCloudRtcSdkMode枚举：

UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL->UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIAL

UCloudRtcSdkSurfaceVideoView.RemoteOpTrigger接口：

onRemoteAudio(View v, UCloudRtcSdkSurfaceVideoView parent)->onRemoteAudio(View v, SurfaceViewGroup parent)

onRemoteVideo(View v, UCloudRtcSdkSurfaceVideoView parent)->onRemoteVideo(View v, SurfaceViewGroup parent)

UCloudRtcSdkSurfaceVideoView类：

UCloudRtcSdkSurfaceVideoView.init()增加一个参数，类型为UCloudRtcRenderView

UCloudRtcSdkEventListener接口：

onSendRTCStats->onSendRTCStatus

onRemoteRTCStats->onRemoteRTCStatus

## 1.9.8
发布于2021-5-20

1.解决syncroom消息会取消订阅的问题。

2.解决UCloudRtcRenderView在父控件变化时，尺寸出错的问题。

3.解决在rejoin流程中，syncroom消息内缺少muteaudio和mutevideo的问题

4.增加硬件编解码。

## 1.9.6
发布于2021-4-15

1.解决子布局切换时没有重新layout的问题。

2.设置pub权限时也能订阅流。

3.本地发布回调参数内加入当前的hasVideo和hasAudio状态。

4.aar包发布到mavenCentral公共仓库中。

## 1.9.3
发布于2021-3-18

1.实现自定义前台通知接口。

2.修复蓝牙耳机连接时，切换回其他app没有声音的问题。

3.修复本地渲染设置成UCLOUD_RTC_SDK_SCALE_ASPECT_FIT不起作用的问题。

4.支持按输出模式剪裁视频数据

## 1.9.1
发布于2021-3-4

1.优化混音推流。

2.增加syncroom消息。


## 1.9.0
发布于2021-1-28

1.解决一些混音相关的问题。

2.修正横屏背景图被拉伸的问题。

3.UCloudRTCLiveActivity改为根据重力感应旋转屏幕方向。

4.混音增加支持aac格式文件。

## 1.8.7
发布于2020-12-31

1.支持拷贝assets目录下文件。

2.实现远端动态混音功能。

3.自定义推视频流增加支持竖屏显示和镜像功能。

4.增加支持x86和x86_64架构。

## 1.8.6
发布于2020-12-10

1.增加支持本地播放wav格式文件。

2.解决低采样率音乐播放有杂音的问题。

3.优化前后摄切换，解决部分设备多摄像头切换问题。

4.优化textureview大小窗切换。

5.优化断线重连流程。


## 1.8.3
发布于2020-11-20

1.修复屏幕共享崩溃问题，并增加屏幕流分辨率。

2.增加textureview多本地渲染

3.解决取消发布屏幕流时，摄像头流显示被释放的问题。

4.使用ffmpeg进行音频重采样。

5.解决部分设备4g连接问题。

6.修改转推和录制流程。

7.完善connecting状态下的自动重连机制。

8.解决setScreenProfile设置无效问题。

9.远端设备断网时，网络质量回调接口上报unknown状态。

## 1.8.1
发布于2020-11-6

1.新增播放音频文件功能。

2.修复横屏模式下，开启镜像崩溃的问题

3.修复部分手机横屏颠倒问题。

4.修复设备没有录音/摄像机权限时，高版本Android手机推流会崩溃的问题。

5.预览前增加Camera权限检测。

6.修复插拔耳机前后，声音通道切换不一致的问题。

## 1.8.0
发布于2020-9-18

1.自定义扩展输入增加支持nv12,argb,rgb565,rgb24格式。

2.纠正自定义扩展i420和nv21颜色失真的问题。

3.解决x86平台初始化sdk时会引起app崩溃的问题。

4.Demo程序实现发布前预览功能。

## 1.7.9
该版本发布于2020-8-27
更新于2020-9-7

1.增加弱网切换功能。

2.增加动态切换回显分辨率。

3.默认启用软件回声消除和噪声抑制

4.匹配android 10 以上，增加 urtclogutil 29及以上的兼容。

5.断网重连后，sdk内部自动渲染本地摄像头画面。

6.增加UVCCamera库，支持外接摄像头（gitlab:5cba356d187cba266815a43063df0629aa0adfc1）。


## 1.7.8
该版本发布于2020-7-20
2020-7-21更新

1.修改startRemoteView逻辑。

2.修改leaveroom释放逻辑。

3.合入webrtc官方改动（https://chromium.googlesource.com/webm/libvpx/+/343352b556f5f61833174c08a35d697d280933e3），解决长时间720p视频播放时会闪退的问题。

4.注释部分native层打印。

5.sdk修改混淆规则，避免混淆内部类。

6.增加接口用于控制本地音频的录制和播放。

7.更新优化界面（2020/7/21）


## 1.7.7
该版本发布于2020-7-2

1.解决传入Ip地址前两位大于等于128会报错的问题

2.保持io.crossbar.autobahn不混淆

3.增加plc、speech_expand等音频相关指标

4.修改mix相关api的实现方法，把json封装到接口内部

5.修改postString中异常捕获类型

6.初始化ip地址并增加tryCatch,保证连接服务器过程中不停止

7.streamst移除时，当前user还存在其他stream的话，不删除user

8.解决断线前是mute状态时，重连后无法恢复mute的问题

9.修改取消订阅bug

10.deviceinfo的base64加密选项改为NO_WRAP

11.在业务层和信令层调用joinRoom时，增加判断条件authInfo里的内容是否为空


## 1.7.5
该版本发布于2020-4-28

1.增加调整录音音量 arm7 arm8

2.增加网络质量回调

3.增加maven 仓库

4.增加修改混流接口

5.修改镜像功能，前置摄像头镜像

## 1.7.3版
该版本发布于2020-4-5，sdk 20120511 1.7.3

* 增加android 4.3 的兼容
* 增加本地渲染view，远端view首帧渲染接口回调
* 增加本地渲染view，远端view截屏回调
* 增加URTCRenderView的单纯surfaceview控件
* 增加TextureView渲染控件支持
* 增加本地录音重采样回调
* 增加后台和锁屏的操作接口，可以用来暂停和恢复音视频模块
* 完善断线重连机制
* 增加cdn转推部分接口

## 1.6.2版

该版本发布于2019-12-6，sdk 345a7545 1.6.2    

* 录像增加mainviewuid
* 增加重连次数和私有化部署相关接口


## 1.6.1版

该版本发布于2019-12-3，sdk bfafbf3c 1.6.1    

* 扩展录像功能，支持图片水印，文字水印，多种布局等，优化移动端录制视频旋转问题
* 限制扩展输入模式分辨率为720P，增加非指定分辨率输入的缩放
* 增加远端近端截图功能
* 修复已知bug


## 1.5.9版

该版本发布于2019-11-14，sdk c83b3733 1.5.9    

* 增加外部输入扩展 rgba系列 转yuv，外部输出扩展 yuv转rgb
* 修复bug



## 1.5.6版

该版本发布于2019-11-4，sdk 9548693d 1.5.6  

* sdk 修改日志上报无效值 -1
* 过滤无效lostpre 

## 1.5.5版

该版本发布于2019-10-25，sdk dc5b6ccc 1.5.5   

* sdk 修改录像功能，更改sdk文档
* sdk 增加日志上报中pull的streamid 和 userid

## 1.4.0版

该版本发布于2019-10-8，08530576 sdk   

* sdk 修改录像功能
* sdk 增加部分日志上报代码，包括状态信息中的cpu 和 memory ，其余上报日志开关未打开
* sdk 修复切换App 过一阵显示黑屏
* app 配合sdk 修改黑屏，修改录屏兼容性

## 1.3.5版

该版本发布于2019-9-19，e81a594c    

* SDK增加客户端混音功能 支持16位 大小的样本 格式类的音频MP3。
* SDK增加日志上报URL的动态修改

## 1.3.4版

该版本发布于2019-8-23，942b3670    

* SDK增加录像功能，支持640*480,720P,1080P

## 1.2.0版

该版本发布于2019-8-8，e40a90ce    

* 完成新引擎架构调整

## 1.1.1版

该版本发布于2019-7-11，db8ac850    

* 增加大班课小班课功能
* 增加切屏功能
* 修改bug

### 1.0.1版

该版本发布于2019-7-5，c5d06613   

* 修改bug 接收sdp answer streamid写死

## 1.0.1版

该版本发布于2019-7-4。   


* 修改SDK类名前缀统一采用UcloudRtcSdk开头
* 修改SDK Enum变量前缀 UCLOUD_RTC_SDK
* 修改包名 com.ucloudrtclib.sdkengine

## 1.0.0 版本 

该版本发布于2019-7-2。    

* 基本的音视频通话功能	
* 支持内置音视频采集的常见功能	
* 支持静音关闭视频功能	
* 支持视频尺寸的配置(180P - 720P)	
* 支持自动重连	
* 支持丰富的消息回调	
* 支持纯音频互动	
* 支持视频的大小窗口切换	
* 支持获取视频房间统计信息（帧率、码率、丢包率等）	
* 支持编码镜像功能		
* 支持屏幕录制功能
* 支持自动手动订阅 自动手动发布
* 支持权限（上行/下行/全部）控制
* 支持音量提示
* 支持获取sdk版本

