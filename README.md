# 1 描述
URTCAndroid 是UCloud推出的一款适用于android平台的实时音视频 SDK，支持android5.0及以上系统，提供了音视频通话基础功能，提供灵活的接口，支持高度定制以及二次开发。

# 2 功能列表

## 2.1 基本功能
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
* 支持大班小班切换功能

* 自定义的外部输入和输出扩展接口

## 2.3 文档地址
* sdk通用功能使用请参考 https://docs.ucloud.cn/urtc/sdk/index

* android api 文档 请参考 demo 所附 的ucloud_rtc_android_api_xxx.zip  javadoc文档

* 快速使用只阐述了最基本的demo使用方式，更多详细的功能使用请参考

  https://docs.ucloud.cn/urtc/sdk/index 中的常用功能列表 以及 demo 所附URTC Android_master.docx 文档

# 3 方案优势

* 利用边缘节点就近接入
* 可用性99.99%
* 智能链路调度
* 自有骨干专线+Internet传输优化
* 数据报文AES加密传输
* 全API开放调度能力
* 端到端链路质量探测
* 多点接入线路容灾
* 抗丢包视频30% 音频70%
* 国内平均延时低于75ms 国际平均延时低于200ms

# 4 应用场景

## 4.1 主播连麦

支持主播之间连麦一起直播，带来与传统单向直播不一样的体验
48KHz 采样率、全频带编解码以及对音乐场景的特殊优化保证观众可以听到最优质的声音

## 4.2 视频会议
小范围实时音视频互动，提供多种视频通话布局模板，更提供自定义布局方式，保证会议发言者互相之间的实时性，提升普通观众的观看体验

## 4.3 泛文娱
### 4.3.1 一对一社交
客户可以利用UCloud实时音视频云实现 QQ、微信、陌陌等社交应用的一对一视频互动
### 4.3.2 狼人杀游戏
支持15人视频通话，玩家可在游戏中选择只开启语音或同时开启音视频

## 4.4 在线教育
支持自动和手动发布订阅视频流，方便实现课堂虚拟分组概念，同时支持根据角色设置流权限，保证课程秩序

## 4.5 在线客服
线上开展音视频对话，对客户的资信情况进行审核，方便金融科技企业实现用户在线签约、视频开户验证以及呼叫中心等功能
提供云端存储空间及海量数据的处理能力，提供高可用的技术和高稳定的平台

# 5 快速使用

- ### APP_ID&APP_KEY

demo运行支持 两种token模式，测试模式，正式模式，通过sdk环境变量来控制

```
//测试模式
UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL);
//正式模式
UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_NORMAL);
```

测试模式下适合快速浏览开发 demo功能，此模式下引用的 sdk 根据APP_ID & APP_KEY 自动生成 测试token，因此运行在测试模式下的话需要先配置下CommonUtils.java 文件中 APP_ID & APP_KEY 字段，APP_ID & APP_KEY 字段的获取请参考 https://docs.ucloud.cn/urtc/quick 

正式模式下的 token 一般由sdk使用方的 业务服务端生成，生成算法请参考

https://docs.ucloud.cn/urtc/sdk/token

- ### 引擎环境初始化

主要配置日志等级，android context，sdkmode
~~~
public class UCloudRtcApplication extends Application {
    @Override
	public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: " + this);
        if (TextUtils.equals(getCurrentProcessName(this), getPackageName())) {
            init();//判断成功后才执行初始化代码
        }
    }

	private void init(){
		sContext = this;
		//初始化sdk环境
		UCloudRtcSdkEnv.initEnv(getApplicationContext());
		//打印日志到logcat
		UCloudRtcSdkEnv.setWriteToLogCat(true);
		//开启log上报
		UCloudRtcSdkEnv.setLogReport(true);
		//设置log级别
		UCloudRtcSdkEnv.setLogLevel(UCloudRtcSdkLogLevel.UCLOUD_RTC_SDK_LogLevelInfo);
		//设置sdk模式（测试模式）
		UCloudRtcSdkEnv.setSdkMode(UCloudRtcSdkMode.UCLOUD_RTC_SDK_MODE_TRIVAL);
		//重连次数
		UCloudRtcSdkEnv.setReConnectTimes(60);
		//设置测试模式的用户私有秘钥
		UCloudRtcSdkEnv.setTokenSeckey(CommonUtils.APP_KEY);
	}
}
~~~
- ### 继承实现UCloudRtcSdkEventListener 实现事件处理

~~~
UCloudRtcSdkEventListener eventListener = new UCloudRtcSdkEventListener() {
    @Override
    public void onServerDisconnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.shortShow(RoomActivity.this, " 服务器已断开");
                stopTimeShow();
                onMediaServerDisconnect() ;
            }
        });
    }

    @Override
    public void onJoinRoomResult(int code, String msg, String roomid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 0) {
                    ToastUtils.shortShow(RoomActivity.this, " 加入房间成功");
                    startTimeShow();
                }else {
                    ToastUtils.shortShow(RoomActivity.this, " 加入房间失败 "+
                            code +" errmsg "+ msg);
                    Intent intent = new Intent(RoomActivity.this, ConnectActivity.class);
                    onMediaServerDisconnect() ;
                    startActivity(intent) ;
                    finish();
                }

            }
        });
    }
~~~
- ### 获取SDK 引擎 并进行基础配置

~~~
sdkEngine = UCloudRtcSdkEngine.createEngnine(eventListener) ;
sdkEngine.setAudioOnlyMode(true) ; // 设置纯音频模式
sdkEngine.configLocalCameraPublish(false) ; // 设置摄像头是否发布
sdkEngine.configLocalAudioPublish(true) ; // 设置音频是否发布，用于让sdk判断自动发布的媒体类型
sdkEngine.configLocalScreenPublish(false) ; // 设置桌面是否发布，作用同上
sdkEngine.setStreamRole(UCloudRtcSdkStreamRole.UCloudRtc_SDK_STREAM_ROLE_BOTH);// 流权限
sdkEngine.setAutoPublish(true) ; // 是否自动发布
sdkEngine.setAutoSubscribe(true) ;// 是否自动订阅
sdkEngine.setVideoProfile(UCloudRtcSdkVideoProfile.matchValue(mVideoProfile)) ;// 摄像头输出等级
~~~

- ### 加入房间

```
UCloudRtcSdkAuthInfo info = new UCloudRtcSdkAuthInfo();
info.setAppId(mAppid);
info.setToken(mRoomToken);
info.setRoomId(mRoomid);
info.setUId(mUserid);
Log.d(TAG, " roomtoken = " + mRoomToken);
sdkEngine.joinChannel(info);
```



