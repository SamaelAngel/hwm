package com.cont.hwm;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.huawei.hwmbiz.eventbus.LoginResult;
import com.huawei.hwmconf.presentation.error.ErrorMessageFactory;
import com.huawei.hwmconf.sdk.model.conf.entity.ConfInfo;
import com.huawei.hwmconf.sdk.util.Utils;
import com.huawei.hwmfoundation.HwmContext;
import com.huawei.hwmfoundation.callback.HwmCallback;
import com.huawei.hwmfoundation.callback.HwmCancelableCallBack;
import com.huawei.hwmfoundation.utils.GsonUtil;

import java.util.ArrayList;
import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/** HwmPlugin */
public class HwmPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity

  private MethodChannel channel;
  private Activity activity;
  private Application application;

  private static final String CHANNEL_HWM = "com.cont.hwm/hwm";


  @Override
  public void onAttachedToActivity(@NonNull final ActivityPluginBinding binding) {
    this.activity = binding.getActivity();
    this.application = this.activity.getApplication();
  }
  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }
  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }
  @Override
  public void onDetachedFromActivity() {

  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hwm");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    HwmContext.getInstance().runOnMainThread(() -> {
      methodCall(call,result);
    });
  }
  public void methodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.contains("hwm")) {
      Info info = GsonUtil.fromJson((String)call.arguments,Info.class);
      List<People> callList = info.getCallList();
      String meetingTitle = info.getMeetingTitle();//会议标题

      String isVideo = info.getIsVideo();//是否是视频会议 1是 0不是
      String needPassword = info.getNeedPassword();//是否需要密码 1需要 0不需要
      String meetingId = info.getMeetingId();//会议id,加入会议会用到
      String password = info.getPassword();//会议密码 加入带密码会议时需要
      String userName = info.getUserName();//用户名称 注册时需要
      String userId = info.getUserId();//用户id 注册时需要
      String startTime = info.getStartTime();//会议开始时间 预约会议时需要
      String timeDur = info.getTimeDur();//会议持续时间 预约会议时需要
//      int callType = Integer.parseInt(call.argument("callType"));//呼叫类型  0：呼叫通话，callList只能有一个对象， 1：发起会议，2：加入会议，3：登陆，4登出，5注册,6:预约会议。


      if (call.method.equals("hwmcall")) {
        HWMHelp.HwyCallSomeOne(callList.get(0), isVideo.equals("1"), this.application, new HwmCancelableCallBack<Void>() {
          @Override
          public void onSuccess(Void r) {
            Log.i("HWMSDKstartCall", "call success");
            toast("呼叫成功");
          }

          @Override
          public void onFailed(int retCode, String desc) {
            Log.i("HWMSDKstartCall", "呼叫失败：" + retCode + ", desc: " + desc);
            toast("呼叫失败：" + retCode + ", desc: " + desc);

          }

          @Override
          public void onCancel() {
            Log.i("HWMSDKcreateConf", "呼叫失败");

          }

        });
      } else if (call.method.equals("hwmaddmeeting")) {
        HWMHelp.createContWithList(callList, isVideo.equals("1"), meetingTitle, needPassword.endsWith("1"), application, new HwmCancelableCallBack<ConfInfo>() {
          @Override
          public void onSuccess(ConfInfo ret) {
            Log.i("HWMSDKcreateConf", "创建会议成功");
            toast("创建会议成功");
          }

          @Override
          public void onFailed(int retCode, String desc) {
            String err = ErrorMessageFactory.create(retCode);
            if (TextUtils.isEmpty(err)) {
              err = "";
            }
            Log.i("HWMSDKcreateConf", "创建会议失败: " + err);
            toast("创建会议失败: " + err);

          }

          @Override
          public void onCancel() {
            Log.i("HWMSDKcreateConf", "创建会议失败: ");
          }
        });
      } else if (call.method.equals("hwmjoinmeeting")) {
        HWMHelp.HwyJoinMeeting(meetingId, password, application, new HwmCancelableCallBack<Void>() {
          @Override
          public void onCancel() {
            Log.i("HWMSDKjoinConf", "加入会议失败: ");

          }

          @Override
          public void onFailed(int i, String s) {
            String err = ErrorMessageFactory.create(i);
            if (TextUtils.isEmpty(err)) {
              err = Utils.getResContext().getString(R.string.hwmconf_join_fail_tip);
            }
            Log.i("HWMSDKjoinConf", "加入会议失败: " + err);
            toast("加入会议失败: " + err);

          }

          @Override
          public void onSuccess(Void aVoid) {
            Log.i("HWMSDKjoinConf", "加入会议成功");
            toast("加入会议成功");

          }
        });
      } else if (call.method.equals("hwmlogin")) {
        People people = new People();
        people.setId(userId);
        people.setName(userName);
        HWMHelp.login(people, this.application, new HwmCallback<LoginResult>() {
          @Override
          public void onFailed(int i, String s) {
            HwmContext.getInstance().runOnMainThread(() -> {
              Log.i("HWMSDKLOGIN", "登录错误：" + i + s);
              toast("登录错误：" + i + s);

            });
          }

          @Override
          public void onSuccess(LoginResult loginResult) {
            if (loginResult != null) {
              Log.i("HWMSDKLOGIN", "登录成功" + userId);
              toast("登录成功");

            } else {
              Log.i("HWMSDKLOGIN", "已登录");
              toast("已登录");

            }
          }
        });
      } else if (call.method.equals("hwmlogout")) {
        HWMHelp.logOutHwy(this.application, new HwmCallback<Void>() {
          @Override
          public void onFailed(int i, String s) {
            Log.i("HWMSDKOutLog", "登出失败 : " + i + "; " + s);
            toast("登出失败 : " + i + "; " + s);

          }

          @Override
          public void onSuccess(Void aVoid) {
            Log.i("HWMSDKOutLog", "登出成功");
            toast("登出成功");
          }
        });
      } else if (call.method.equals("hwminit")) {
//        HWMHelp.init(application);

      }else if (call.method.equals("hwmordermeeting")) {
        HWMHelp.orderContWithList(callList,application,meetingTitle,startTime,timeDur,new HwmCallback<ConfInfo>() {
          @Override
          public void onFailed(int i, String s) {
            String err = ErrorMessageFactory.create( i);
            if (TextUtils.isEmpty(err)) {
              err = Utils.getApp().getString(R.string.hwmconf_book_conf_fail);
            }
            toast("预约会议失败");
          }

          @Override
          public void onSuccess(ConfInfo confInfo) {
            toast("预约会议成功");
          }
        });

      }

    }else  if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }



  }
  private void setRxJavaErrorHandler() {
    RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
      @Override
      public void accept(Throwable throwable) throws Exception {
        Log.d("ss", "throw test");
      }
    });
  }
  private void toast(String text){
    Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  class Info{

    private int callType;
    private List<People> callList;
    private String meetingTitle;
    private String isVideo;
    private String needPassword;
    private String meetingId;
    private String password;
    private String userName;
    private String userId;
    private String startTime;
    private String timeDur;


    public int getCallType() {
      return callType;
    }

    public void setCallType(int callType) {
      this.callType = callType;
    }

    public List<People> getCallList() {
      return callList;
    }

    public void setCallList(List<People> callList) {
      this.callList = callList;
    }

    public String getMeetingTitle() {
      return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
      this.meetingTitle = meetingTitle;
    }

    public String getIsVideo() {
      return isVideo;
    }

    public void setIsVideo(String isVideo) {
      this.isVideo = isVideo;
    }

    public String getNeedPassword() {
      return needPassword;
    }

    public void setNeedPassword(String needPassword) {
      this.needPassword = needPassword;
    }

    public String getMeetingId() {
      return meetingId;
    }

    public void setMeetingId(String meetingId) {
      this.meetingId = meetingId;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getUserName() {
      return userName;
    }

    public void setUserName(String userName) {
      this.userName = userName;
    }

    public String getUserId() {
      return userId;
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public String getStartTime() {
      return startTime;
    }

    public void setStartTime(String startTime) {
      this.startTime = startTime;
    }

    public String getTimeDur() {
      return timeDur;
    }

    public void setTimeDur(String timeDur) {
      this.timeDur = timeDur;
    }
  }

  class People{
    private String name;
    private String id;
    private String phone;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getPhone() {
      return phone;
    }

    public void setPhone(String phone) {
      this.phone = phone;
    }
  }
}
