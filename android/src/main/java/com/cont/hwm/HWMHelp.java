package com.cont.hwm;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.huawei.cloudlink.openapi.HWMSdk;
import com.huawei.cloudlink.openapi.OpenSDKConfig;
import com.huawei.cloudlink.openapi.api.param.CallParam;
import com.huawei.cloudlink.openapi.api.param.ConfType;
import com.huawei.cloudlink.openapi.api.param.CreateConfParam;
import com.huawei.cloudlink.openapi.api.param.JoinConfParam;
import com.huawei.cloudlink.openapi.api.param.PasswordJoinConfParam;
import com.huawei.cloudlink.openapi.api.param.RandomJoinConfParam;
import com.huawei.hwmbiz.HWMBizSdk;
import com.huawei.hwmbiz.eventbus.LoginResult;
import com.huawei.hwmbiz.login.api.AppIdAuthParam;
import com.huawei.hwmconf.presentation.error.ErrorMessageFactory;
import com.huawei.hwmconf.sdk.model.conf.entity.AttendeeModel;
import com.huawei.hwmconf.sdk.model.conf.entity.BookConfParam;
import com.huawei.hwmconf.sdk.model.conf.entity.ConfInfo;
import com.huawei.hwmconf.sdk.model.conf.entity.JoinConfPermissionType;
import com.huawei.hwmconf.sdk.model.conf.entity.MeetingType;
import com.huawei.hwmconf.sdk.util.BaseDateUtil;
import com.huawei.hwmconf.sdk.util.Utils;
import com.huawei.hwmfoundation.HwmContext;
import com.huawei.hwmfoundation.callback.HwmCallback;
import com.huawei.hwmfoundation.callback.HwmCancelableCallBack;
import com.huawei.hwmlogger.HCLog;
import com.huawei.hwmsdk.jni.HwmNativeSDK;
import com.huawei.hwmsdk.jni.HwmSDK;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class HWMHelp {

    public static void createContWithList(List<HwmPlugin.People> list, boolean isVideo, String title, boolean needPassword, Application application, HwmCancelableCallBack<ConfInfo> callBack){

        List<AttendeeModel> attendeeList = new ArrayList<>();
        for (HwmPlugin.People people:list){
            AttendeeModel model = AttendeeModel.buildAttendeeByThirdUserId(people.getId(),people.getName());
            attendeeList.add(model);
        }

        CreateConfParam createConfParam = new CreateConfParam()
                .setSubject(title)
                .setConfType(isVideo ? ConfType.CONF_VIDEO_AND_DATA : ConfType.CONF_AUDIO_AND_DATA)
                .setVmrId("")//是否使用个人会议id
                .setJoinConfRestrictionType(JoinConfPermissionType.valueOf(0))// （0-所有人，2-企业内，3-会议邀请人员）");
                .setMicOn(true)
                .setCameraOn(true)
                .setNeedPassword(needPassword)
                .setRecordOn(false)
                .setAutoRecord(false)
                .setAttendeeMembers(attendeeList);

        HWMSdk.getOpenApi(application).createConf(createConfParam,callBack);

    }



    public static void HwyCallSomeOne(HwmPlugin.People people, boolean isVideo,Application application,HwmCancelableCallBack callBack) {

        String thirdAccountId = people.getId();
        String nickName = people.getName();

        CallParam callParam = new CallParam()
                .setVideo(isVideo);

        if (!TextUtils.isEmpty(thirdAccountId)) {
            //如果是appid的方式登录，可以通过第三方通讯录的联系人id进行呼叫
            callParam.setThirdAccountId(thirdAccountId);
        }

        if (!TextUtils.isEmpty(nickName)) {
            callParam.setNickName(nickName);
        }

        HwmContext.getInstance().runOnMainThread(() -> {
            HWMSdk.getOpenApi(application).startCall(callParam, callBack);
        });
    }


    public static void HwyJoinMeeting(String meetingId,String password,Application application,HwmCancelableCallBack callBack) {

        JoinConfParam joinConfParam;
        if (TextUtils.isEmpty(password)){
            joinConfParam = new RandomJoinConfParam()
                    .setRandom(newNonce())
                    .setConfId(meetingId)
                    .setCameraOn(true)
                    .setMicOn(true);
        }else {
            joinConfParam = new PasswordJoinConfParam()
                    .setPassword(password)
                    .setConfId(meetingId)
                    .setCameraOn(true)
                    .setMicOn(true);
        }

        HWMSdk.getOpenApi(application).joinConf(joinConfParam, callBack );

    }

    public static void logOutHwy(Application application,HwmCallback callback) {
        HWMSdk.getOpenApi(application).logout(callback);

    }

    public static void orderContWithList(List<HwmPlugin.People> list, Application application, String title, String startDate, String timedurStr, HwmCallback<ConfInfo> callback) {

        List<AttendeeModel> attendeeList = new ArrayList<>();
        for (HwmPlugin.People entity:list){

            AttendeeModel model = AttendeeModel.buildAttendeeByThirdUserId(entity.getId(),entity.getName());
            attendeeList.add(model);
        }

        JoinConfPermissionType permissionType = JoinConfPermissionType.valueOf(0);//（0-所有人，2-企业内，3-会议邀请人员）"
        BookConfParam bookConfParam = new BookConfParam()
                .setConfSubject(title)
                .setStartTime(BaseDateUtil.dateToTimeStamp(startDate, BaseDateUtil.FMT_YMDHMS))
                .setTimeZone(56)
                .setDuration(Integer.parseInt(timedurStr))
                .setConfType( MeetingType.CONF_VIDEO)
                .setVmrIdFlag(false)
                .setVmrId("")
                .setNeedConfPwd(false)
                .setJoinConfRestrictionType(permissionType)
                .setRecordOn(false)
                .setAutoRecord(false)
                .setMailOn(false)
                .setSmsOn(false)
                .setEmailCalenderOn(false)
                .setAttendees(attendeeList);
        HWMBizSdk.getBizOpenApi().bookConf(bookConfParam,callback);
    }

    public static void init(Application application) {
        OpenSDKConfig sdkConfig = new OpenSDKConfig(application)
                .setAppId("685a5a67c3a74afaaa12dffae3c86e5e") //向会议服务器申请的appid
                .setNeedConfChat("true".equals(BuildConfig.needConfChat))
                .setNeedScreenShare("true".equals(BuildConfig.needScreenShare))
                .setNeedFeedback(false);
        HWMSdk.init(application, sdkConfig);
        Log.e("", "dpsdpsdps HwmInit" );

    }
    public static void login(HwmPlugin.People people, Application application, HwmCallback callback){


        String appId = "685a5a67c3a74afaaa12dffae3c86e5e";
        String appKey = "Rf42REBd68naRyF2";
        String corpId = "";//企业id
        String userId = people.getId();
        String userName = people.getName();
        String userPhone = people.getPhone();

        String nonce = newNonce();
        long expireTime = 0;

        String signature = newSignature(appId, corpId, userId, expireTime, nonce, appKey);

        AppIdAuthParam appIdAuthParam = new AppIdAuthParam();
        appIdAuthParam.setExpireTime(expireTime);
        appIdAuthParam.setNonce(nonce);
        appIdAuthParam.setCorpId(corpId);
        appIdAuthParam.setThirdUserId(userId);
        appIdAuthParam.setSignature(signature);
        appIdAuthParam.setUserName(userName);
        appIdAuthParam.setUserPhone(userPhone);


        HWMSdk.getOpenApi(application).loginByAppId(appIdAuthParam, callback);

    }


    private static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0;i < length;i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String newNonce() {
        return getRandomString(32);
    }
    public static String newSignature(String appId, String corpId, String userId, long expireTime, String nonce, String appKey) {

        String temp = null;

        if (TextUtils.isEmpty(corpId)) {
            temp = appId + ":" + userId + ":" + expireTime + ":" + nonce;
        } else {
            temp = appId + ":" + corpId + ":" + userId + ":" + expireTime + ":" + nonce;
        }
        String temp1 = "";
        try {
            temp1 = encode(temp, appKey);
        } catch (Exception e) {
        }
        return temp1;
    }

    /**
     * 16进制0~F的字符数组
     */
    private final static char[] DIGEST_ARRAYS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /*
     * bytesToHex()，用来把一个byte类型数组转换成十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            hexStr.append(DIGEST_ARRAYS[bytes[i] >>> 4 & 0X0F]);
            hexStr.append(DIGEST_ARRAYS[bytes[i] & 0X0F]);
        }

        return hexStr.toString();
    }

    static String encode(String data, String key) {
        byte[] hashByte;
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256HMAC.init(secretKey);

            hashByte = sha256HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }

        return bytesToHex(hashByte);
    }
}
