# 不开启优化，因为android的dex并不像Java虚拟机需要optimize(优化)和preverify(预检)两个步骤。
-dontoptimize
#不使用大小写混合类名，注意，windows用户必须为ProGuard指定该选项，
#因为windows对文件的大小写是不敏感的，也就是比如a.java和A.java会认为是同一个文件。
#如果不这样做并且你的项目中有超过26个类的话，那么ProGuard就会默认混用大小写文件名，导致class文件相互覆盖
-dontusemixedcaseclassnames
#指定不去忽略非公共库的类和成员
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers


#抛出异常时保留代码行号,方便抛出异常时定位问题
-keepattributes SourceFile,LineNumberTable
#重命名抛出异常时的文件名称，方便抛出异常时定位问题
-renamesourcefileattribute SourceFile
# 混淆后就会生成映射文件
# 包含有类名->混淆后类名的映射关系
# 然后可以使用printmapping指定映射文件的名称
-verbose


# 不做预校验，preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可加快混淆速度
-dontpreverify


# dump.txt文件列出apk包内所有class的内部结构
-dump class_files.txt
# seeds.txt文件列出未混淆的类和成员
-printseeds seeds.txt
# usage.txt文件列出从apk中删除的代码
-printusage usage.txt
# mapping.txt文件列出混淆前后的映射
-printmapping mapping.txt


#忽略library里面非public修饰的类。
#library里的非公开类是不能被程序使用的，忽略掉这些类可以加快混淆速度。
#但是请注意，有一种特殊情况：有些人编写的代码与类库中的类在同一个包下，而且对该包的非public类进行了使用，在这种情况下，就不能使用该选项了。
#-skipnonpubliclibraryclasses


# 保留注解，因为注解是通过反射机制来实现的
-keepattributes *Annotation*
# 保留js接口
-keepattributes *JavascriptInterface*
# 保留exception
-keepattributes Exceptions
# 保留内部类
-keepattributes InnerClasses
#保留泛型
-keepattributes Signature


#保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}


#保留枚举类
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留Parcelable序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


# 保留资源应用名
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 忽略support包下的警告
-dontwarn android.support.**
# 保留support包下的动画
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
}
#保留系统类库
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


#保留华为sdk相关
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}


#OKhttp 混淆配置
#https://github.com/square/okhttp/blob/5fe3cc2d089810032671d6135ad137af6f491d28/README.md#proguard
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase


#Eventbus 混淆配置
#http://greenrobot.org/eventbus/documentation/proguard/
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


# Only required if you use AsyncExecutor
#-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}


#配置HME java不混淆,C++ 会调用，HMW-Video.jar
-keep class com.huawei.media.audio.AudioDeviceAndroid { *;}
-keep class com.huawei.media.audio.AudioDeviceAndroidService { *;}
-keep class com.huawei.media.audio.JNIAudioDeviceImpl { *;}
-keep class com.huawei.media.audio.JNIAudioDeviceMeetingImpl { *;}
-keep class com.huawei.media.audio.JNIAudioDeviceRtcImpl { *;}
-keep class com.huawei.media.video.Camera2Characteristic { *;}
-keep class com.huawei.media.video.CaptureCapabilityAndroid { *;}
-keep class com.huawei.media.video.DeviceInfo { *;}
-keep class com.huawei.media.video.H264Decoder { *;}
-keep class com.huawei.media.video.HmeDefinitions { *;}
-keep class com.huawei.media.video.JNIBridge { *;}
-keep class com.huawei.media.video.JNIBridgeImpl { *;}
-keep class com.huawei.media.video.JNIMeetingImpl { *;}
-keep class com.huawei.media.video.JNIRtcImpl { *;}
-keep class com.huawei.media.video.KirinMediaCodecEncoder { *;}
-keep class com.huawei.media.video.LogFile { *;}
-keep class com.huawei.media.video.MediaCodecDecoder { *;}
-keep class com.huawei.media.video.MediaCodecEncoder { *;}
-keep class com.huawei.media.video.ScreenCaptureImageActivity { *;}
-keep class com.huawei.media.video.SurfaceEncoder { *;}
-keep class com.huawei.media.video.VideoCapture { *;}
-keep class com.huawei.media.video.VideoCapture2Android { *;}
-keep class com.huawei.media.video.VideoCaptureAndroid { *;}
-keep class com.huawei.media.video.VideoCaptureDeviceInfo { *;}
-keep class com.huawei.media.video.VideoCaptureDeviceInfoAndroid { *;}
-keep class com.huawei.media.video.VideoRender { *;}
-keep class com.huawei.media.video.VideoRenderNoGLES { *;}
-keep class com.huawei.media.video.ViEAndroidGLES20 { *;}
-keep class com.huawei.media.video.ViERenderer { *;}
-keep class com.huawei.media.video.ViESurfaceRenderer { *;}
-keep class com.huawei.media.video.VtNativeDecoder { *;}


#配置TUP java 不混淆，c++ 会调用
-keep class tupsdk.Tupmedia { *;} #TupCall.jar
-keep class com.huawei.media.data.Conference { *;}  #TupConf.jar

-keep class imssdk.** { *;}

# rtc sdk 混淆
-keep class com.huawei.rtc.**{*;}

-keep class com.huawei.allplatform.**{*;}

## native sdk 混淆
-keep class com.huawei.hwmsdk.**{*;}

# WebViewInterface 不能混淆
-keepclassmembers class com.huawei.hwmclink.jsbridge.model.GHConfigModel {
  public *;
}

# API不能被混淆
-keep public class com.huawei.**.*Api { *;}
-keep class com.huawei.cloudlink.openapi.api.ICloudLinkOpenApi{*;}
-keep class com.huawei.cloudlink.openapi.api.impl.CloudLinkOpenApiImpl{*;}
-keep class com.huawei.hwmbiz.IBizOpenApi{*;}
-keep class com.huawei.hwmbiz.impl.BizOpenApiImpl{*;}


#micro service 不能混淆,有反射调用，参考华为云配置
-keep public class * extends com.huawei.hwmfoundation.microservice.HCMicroService
-keep public class * extends com.mapp.hcmobileframework.microapp.HCMicroApplicationDelegate
-keep public class * implements com.huawei.hwmclink.jsbridge.bridge.** {*;}


#继承自AbsCache 的类，要保留构造函数，否则构造函数会被优化掉,这里保留所有的构造函数，不论有没有在使用
-keepclassmembers class * {
    <init>(...);
}
#RXJava 不能被混淆，因为内部有hook操作
-keep class io.reactivex.**{*;}
#自定义注解不能被混淆
-keep public class com.huawei.hwmbiz.aspect.CheckToken
-keep public class com.huawei.hwmfoundation.hook.annotation.TimeConsume
-keep public class com.huawei.hwmfoundation.hook.annotation.HookDisable
-keep public class com.huawei.hwmfoundation.hook.annotation.HookNotNeeded


#glide 不混淆
-keep class com.github.bumptech.glide.**{*;}
#需要用json转换生成javabean 不混淆
-keep public class * extends com.huawei.hwmfoundation.base.BaseModel{*;}


#HMS SDK不可混淆
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}


#Androidx 混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
#路由表不能混淆，启动会从固定package下汇总路由表
-keep class com.huawei.cloudlink.router.routermap.** {*;}


# crashreport混淆
-keep public class com.huawei.crashreport.**{*;}


# IMSDK 混淆
-keep class com.huawei.imsdk.ECSProxy {*;}
-keep class com.huawei.imsdk.msg.** {*;}

# HWMUserState用户状态不混淆
-keep public class com.huawei.cloudlink.openapi.model.HWMUserState{*;}

# netty不混淆，投影要用到-keep class io.netty.**{*;}

# demo代码不用混淆
-keep class com.huawei.hwmdemo.** { *;}
