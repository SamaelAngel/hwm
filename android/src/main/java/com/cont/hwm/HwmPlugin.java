package com.cont.hwm;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.huawei.hwmbiz.eventbus.LoginResult;
import com.huawei.hwmconf.presentation.error.ErrorMessageFactory;
import com.huawei.hwmconf.sdk.model.conf.entity.ConfInfo;
import com.huawei.hwmconf.sdk.util.Utils;
import com.huawei.hwmfoundation.HwmContext;
import com.huawei.hwmfoundation.callback.HwmCallback;
import com.huawei.hwmfoundation.callback.HwmCancelableCallBack;

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
    if (call.method.equals("hwm")) {
      int callType = Integer.parseInt(call.argument("callType"));//呼叫类型  0：呼叫通话，callList只能有一个对象， 1：发起会议，2：加入会议，3：登陆，4登出，5注册。
      String callListStr = call.argument("callList");//呼叫对象list   必传字段有name，id， 不必须字段有phone
      List<People> callList = new ArrayList<>();
      People p = new People();
      p.setName("陈龙");
      p.setId("cl");
      callList.add(p);
//      if (!TextUtils.isEmpty(callListStr)){
//        callList = GsonUtil.fromJson(callListStr, new TypeToken<List<People>>() {}.getType());
//      }
//
      String meetingTitle = call.argument("meetingTitle");//会议标题

      String isVideo = call.argument("isVideo");//是否是视频会议 1是 0不是
      String needPassword = call.argument("needPassword");//是否需要密码 1需要 0不需要
      String meetingId = call.argument("meetingId");//会议id,加入会议会用到
      String password = call.argument("password");//会议密码 加入带密码会议时需要
      String userName = call.argument("userName");//用户名称 注册时需要
      String userId = call.argument("userId");//用户id 注册时需要


      if (callType == 0) {
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
      } else if (callType == 1) {
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
      } else if (callType == 2) {
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
      } else if (callType == 3) {
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
      } else if (callType == 4) {
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
      } else if (callType == 5) {
        HWMHelp.init(application);

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
