# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
#指定压缩级别
-optimizationpasses 5
# 表示混淆时不使用大小写混合类名
-dontusemixedcaseclassnames
# 表示不跳过library中的非public的类
-dontskipnonpubliclibraryclasses
# 打印混淆的详细信息
-verbose

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
# 表示不进行校验,这个校验作用 在java平台上的
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-ignorewarnings

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.app.Service

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
    @android.support.annotation.Keep <init>(...);
}

# 引入依赖包rt.jar（jdk路径）
#-libraryjars F:/java_8_101/jre/lib/rt.jar
# 引入依赖包android.jar(android SDK路径)
#-libraryjars ~/Library/Android/sdk/platforms/android-25/android.jar
# 如果用到Appcompat包，需要引入
#-libraryjars /xxx/xxx/xx/xxx/MyApplication/library-banner/build/intermediates/exploded-aar/com.android.support/appcompat-v7/24.1.1/jars/classes.jar
#-libraryjars /xx/xx/xx/xx/MyApplication/library-banner/build/intermediates/exploded-aar/com.android.support/support-v4/24.1.1/jars/classes.jar

#保证是独立的jar,没有任何项目引用,如果不写就会认为我们所有的代码是无用的,从而把所有的代码压缩掉,导出一个空的jar
-dontshrink
#保护泛型
-keepattributes Signature
###-optimizations !code/simplification/cast,!field/*,!class/merging/*

-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }
-keep class com.ucloudrtclib.sdkengine.**{*;}
-keep class com.ucloudrtclib.sdkengine.define.*{*;}
-keep enum com.ucloudrtclib.sdkengine.define.*{*;}
-keep class com.ucloudrtclib.monitor.**{*;}
-keepclassmembers class com.ucloudrtclib.sdkengine.UCloudRtcSdkEnv {
    public static <methods>;
}

-keepclassmembers interface com.ucloudrtclib.sdkengine.UCloudRtcSdkEngine {
    public <methods>;
    public static <methods>;
}
#保护类名和类内所有成员
-keep class org.webrtc.** {
    *;
}

-keep class com.ucloud.business.im.sdkengine.**{*;}
-keep class com.ucloud.business.im.sdkengine.define.*{*;}
-keep enum com.ucloud.business.im.sdkengine.define.*{*;}
-keep class com.ucloud.business.base.UcloudBusSdkEventListener{*;}
-keep class com.ucloud.business.base.UCloudBusVirtualEngine{*;}
-keep class com.ucloud.business.base.UcloudBusSdkEngineImpl{*;}
-keep class com.ucloud.business.im.logicengine.UcloudIMSdkEngineImpl{*;}


-keepclassmembers interface com.ucloud.business.im.sdkengine.UcloudIMSdkEngine {
    public <methods>;
    public static <methods>;
}

-keepclassmembers interface com.ucloud.business.im.sdkengine.UcloudIMSdkEventListener {
    public <methods>;
    public static <methods>;
}

-keepclassmembers interface com.ucloud.business.base.UcloudBusSdkEventListener {
    public <methods>;
    public static <methods>;
}

#-dontwarn com.alibaba.fastjson.**
#-keep class com.alibaba.fastjson.**{*; }
#
#-dontwarn io.crossbar.autobahn.**
#-keep class io.crossbar.autobahn.**{*; }
#
#-dontwarn de.tavendo.autobahn.**
#-keep class de.tavendo.autobahn.**{*; }



-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

