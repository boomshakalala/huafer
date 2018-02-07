package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.R;
import com.huapu.huafen.alipay.AliPayHelper;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.ConversationEvent;
import com.huapu.huafen.events.FinishEvent;
import com.huapu.huafen.events.LoginEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActivityUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import de.greenrobot.event.EventBus;

/**
 * 登录页面
 *
 * @author liang_xs
 */
public class LoginActivity extends BaseActivity {
    private Button btnWeibo, btnWechat;
    private TextView tvBtnWechat;
    private TextView tvBtnAlipay;
    private TextView tvBtnPhone;
    private TextView tvClause;
    private CheckBox checkBox;
    private TextView back;


    private String WEB_RETURN_STRING = "cancled";
    private String FROM_WEB = "";
    private int flags;

    // pulllistview
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EventBus.getDefault().register(this);

        if (getIntent().hasExtra("FROM_WEB")) {
            FROM_WEB = getIntent().getStringExtra("FROM_WEB");
        }
        // 初始化ShareSDK
        ShareSDK.initSDK(this);
        ActivityUtil.addActivities(this);
        flags = getIntent().getFlags();
        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(FinishEvent event) {
        if (event != null && event.isFinish) {
            if (FROM_WEB.equals("FROM_WEB")) {
                Intent intent = new Intent();
                WEB_RETURN_STRING = "success";
                intent.putExtra("result", WEB_RETURN_STRING);
                setResult(RESULT_OK, intent);
            }
            if (flags == MyConstants.FROM_FLAGS_VERIFIED && CommonPreference.isLogin() && !CommonPreference.getUserInfo().hasVerified && CommonPreference.getUserInfo().getUserLevel() != 3) {
                Intent intent = new Intent(LoginActivity.this, VerifiedActivity.class);
                startActivity(intent);
            }
            finish();
        }
        if (event != null && event.isPhone) {
            tvBtnPhone.setVisibility(View.GONE);
        }
    }

    private void initView() {
//        getTitleBar().setTitle("登录").setOnLeftButtonClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        back = (TextView) findViewById(R.id.back);
        tvBtnWechat = (TextView) findViewById(R.id.tvBtnWechat);
        tvBtnAlipay = (TextView) findViewById(R.id.tvBtnAlipay);
        tvBtnPhone = (TextView) findViewById(R.id.tvbtnPhone);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        tvClause = (TextView) findViewById(R.id.tvClause);

        // btnWeibo.setOnClickListener(this);
        tvBtnWechat.setOnClickListener(this);
        tvBtnAlipay.setOnClickListener(this);
        tvBtnPhone.setOnClickListener(this);
        tvClause.setOnClickListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        tvClause.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void onBackPressed() {
        ConversationEvent event = new ConversationEvent();
        event.isFinish = true;
        EventBus.getDefault().post(event);

        if (FROM_WEB.equals("FROM_WEB")) {
            Intent intent = new Intent();
            intent.putExtra("result", WEB_RETURN_STRING);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleBarLeft:
                onBackPressed();
                break;

            // case R.id.btnWeibo:
            // ShareLoginUtil.shareLogin(LoginActivity.this, SinaWeibo.NAME);
            // break;

            case R.id.tvBtnWechat:
                if (!checkBox.isChecked()) {
                    toast("请先同意花粉儿条款与规范");
                    return;
                }
                shareLogin(LoginActivity.this, Wechat.NAME);
                break;
            case R.id.tvbtnPhone:
                if (!checkBox.isChecked()) {
                    toast("请先同意花粉儿条款与规范");
                    return;
                }
                Intent intent1 = new Intent(this, PhoneLoginActivity.class);
                intent1.addFlags(flags);
                startActivity(intent1);
                break;
            case R.id.tvBtnAlipay:
                if (!checkBox.isChecked()) {
                    toast("请先同意花粉儿条款与规范");
                    return;
                }
                authAliPayLogin();
                break;
            case R.id.tvClause:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WEBVIEW_ZHUCE);
                intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "注册条款");
                startActivity(intent);
                break;
        }
    }

    /**
     * @Description: 第三方登录
     */
    private void shareLogin(final Activity activity, final String name) {
        ProgressDialog.showProgress(this);
        //      startRequestForUserLoginData(activity, "1", "o2NPGvisqesbbW6C-SoNqlNarUeg", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "爵色","0");
        //		startRequestForUserLoginData(activity, "1", "o2NPGvntgWc39ZWL5RGLxhmFKvvA", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "成龙","0");
        //		startRequestForUserLoginData(activity, "1", "o2NPGvkHL0YvHAuU_XZjLStFKYkw", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "达达","0");
        //		startRequestForUserLoginData(activity, "1", "o2NPGvkjiwXEF2K-vNjKmlQrzFcw", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "狮子王","0");
        //		startRequestForUserLoginData(activity, "1", "o2NPGvgHo0Xnb_vwl-dQ1b1Wfgso", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "孙晨","0");
        //      startRequestForUserLoginData(activity, "1", "o2NPGvty7JBWJPMAm4apF29E4ZCc", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "张玉龙","0");
        //      startRequestForUserLoginData(activity, "1", "o2NPGvty7JBWJPMAm4apF29E4ZCc", "http://tva4.sinaimg.cn/crop.0.0.640.640.1024/9632347bjw8f01xh8w9dkj20hs0hsaah.jpg", "张玉龙","0");

        final Platform platform = ShareSDK.getPlatform(activity, name);
        if (platform != null) {
            if (platform.isValid()) {
                platform.removeAccount(true);
                ShareSDK.removeCookieOnAuthorize(true);
            }
            platform.SSOSetting(false); // 设置false表示使用SSO授权方式
            platform.showUser(null);// 执行登录，登录后在回调里面获取用户资料
            platform.setPlatformActionListener(new PlatformActionListener() {

                @Override
                public void onError(Platform arg0, final int arg1, final Throwable arg2) {
                    LogUtil.i("liang", "onError: " + arg2.toString());
                    WEB_RETURN_STRING = "failed";
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ProgressDialog.closeProgress();
                            if (platform != null) {
                                if (platform.isValid()) {
                                    platform.removeAccount(true);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onComplete(final Platform platform, int action, HashMap<String, Object> arg2) {
                    LogUtil.i("liang", "onComplete: " + action);
                    if (action == Platform.ACTION_USER_INFOR) {
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                taskOtherLogin(activity, platform);
                            }
                        });
                    }
                }

                @Override
                public void onCancel(Platform arg0, int arg1) {
                    activity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            toast("取消登录");
                            WEB_RETURN_STRING = "cancled";
                            ProgressDialog.closeProgress();
                        }
                    });
                    if (platform != null) {
                        if (platform.isValid()) {
                            platform.removeAccount(true);
                        }
                    }
                }
            });
        }
    }

    private void authAliPayLogin() {
        AliPayHelper helper = new AliPayHelper(this);
        helper.authV2(new AliPayHelper.OnAliPayAuthCompleteListener() {

            @Override
            public void onComplete(String aliId, String authCode) {
                startRequestForAlipayUserLoginData(aliId, authCode);
            }
        });

    }

    /**
     * @Description: 清空USERID
     */
    private void shareLogout(Platform platform) {
        if (platform != null) {
            if (platform.isValid()) {
                platform.removeAccount(true);
            }
        }
    }

    private void taskOtherLogin(final Activity activity, Platform platform) {
        PlatformDb platDB = platform.getDb();// 获取数平台数据DB
        // 通过DB获取各种数据
        final String uId = platDB.getUserId();
        String uIcon = platDB.getUserIcon();
        String uName = platDB.getUserName();
        String sex = platDB.getUserGender();
        String strType = platform.getName();
        // FileUtils.writeString2File("userId:" + strUserId + "name:" + strType
        // + "gender:" + strGender, "/sdcard/shareLogin/", "qqSex.txt");
        if (strType.equals(SinaWeibo.NAME)) {
            strType = "2";
        } else if (strType.equals(Wechat.NAME)) {
            strType = "1";
        }
        if (!TextUtils.isEmpty(sex)) {
            if (sex.equals("m")) {
                sex = "1";
            } else if (sex.equals("f")) {
                sex = "0";
            }
        } else {
            sex = "0";
        }
        if (!TextUtils.isEmpty(uName)) {
            uName = uName.replaceAll(" ", "");
        }
        startRequestForUserLoginData(activity, "1", uId, uIcon, uName, sex);
        shareLogout(platform);
    }

    private void startRequestForUserLoginData(final Activity activity, final String type, final String uId,
                                              final String uIcon, final String uName, final String sex) {
        startRequestForUserLogin(activity, type, uId, uIcon, uName, sex);
    }


    private void startRequestForAlipayUserLoginData(final String aliId, final String authCode) {
        startRequestForAlipayUserLogin(aliId, authCode);
    }


    /**
     * 登录
     *
     * @param activity
     * @param type     短信验证类型 1:用户注册 2:密码找回 3:修改密码 4:微信换绑定
     * @param uId      微信id
     * @param uIcon
     * @param uName
     * @param sex
     */
    private void startRequestForUserLogin(final Activity activity, final String type, final String uId,
                                          final String uIcon, final String uName, final String sex) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(MyConstants.REQUEST_VER, CommonUtils.getAppVersionName());
        params.put("uid", uId);
        params.put(MyConstants.TYPE, type);
        LogUtil.i("liang", "登录params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.USERLOGIN, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
                WEB_RETURN_STRING = "failed";
                LogUtil.i("liang", "登录onError:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "登录:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    WEB_RETURN_STRING = "failed";
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                            CommonPreference.setPhone(userInfo.getPhone());
                            JSONObject object = JSON.parseObject(baseResult.obj);
                            String accessSecret = object.getString("accessSecret");
                            if (!TextUtils.isEmpty(accessSecret)) {
                                CommonPreference.setAccessSecret(accessSecret);
                            }

                            String accessToken = object.getString("accessToken");
                            if (!TextUtils.isEmpty(accessToken)) {
                                CommonPreference.setAccessToken(accessToken);
                            }
                            startRequestForUploadPushDevice();
                            LoginEvent event = new LoginEvent();
                            event.isLogin = true;
                            EventBus.getDefault().post(event);
                            WEB_RETURN_STRING = "success";
                            if (FROM_WEB.equals("FROM_WEB")) {
                                Intent intent = new Intent();
                                intent.putExtra("result", WEB_RETURN_STRING);
                                setResult(RESULT_OK, intent);
                            }
                            LogUtil.i("LoginActivity", "已发送登录event");
                            if (flags == MyConstants.FROM_FLAGS_VERIFIED && CommonPreference.isLogin() && !CommonPreference.getUserInfo().hasVerified && CommonPreference.getUserInfo().getUserLevel() != 3) {
                                Intent intent = new Intent(LoginActivity.this, VerifiedActivity.class);
                                startActivity(intent);
                            }
                            activity.finish();
                        }
                    } else if (baseResult.code == ParserUtils.RESPONSE_WECHAT_UID_UNEXIST) {
                        Intent intent = new Intent(activity, BindPhoneActivity.class);
                        intent.addFlags(flags);
                        intent.putExtra(MyConstants.EXTRA_TYPE, "1");
                        intent.putExtra(MyConstants.EXTRA_FLAGID, uId);
                        intent.putExtra(MyConstants.EXTRA_WECHAT_ICON, uIcon);
                        intent.putExtra(MyConstants.EXTRA_WECHAT_NAME, uName);
                        intent.putExtra(MyConstants.EXTRA_WECHAT_SEX, sex);
                        activity.startActivity(intent);
                    } else {
                        WEB_RETURN_STRING = "failed";
                        CommonUtils.error(baseResult, activity, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestForAlipayUserLogin(final String aliId, final String authCode) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(MyConstants.REQUEST_VER, CommonUtils.getAppVersionName());
        params.put("aliId", aliId);
        params.put("authCode", authCode);
        params.put(MyConstants.TYPE, "2");
        LogUtil.e("liang", "登录params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.USERLOGIN, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                WEB_RETURN_STRING = "failed";
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "登录:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    WEB_RETURN_STRING = "failed";
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {

                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                            CommonPreference.setUserInfo(userInfo);
                            CommonPreference.setPhone(userInfo.getPhone());
                            JSONObject object = JSON.parseObject(baseResult.obj);
                            String accessSecret = object.getString("accessSecret");
                            if (!TextUtils.isEmpty(accessSecret)) {
                                CommonPreference.setAccessSecret(accessSecret);
                            }

                            String accessToken = object.getString("accessToken");
                            if (!TextUtils.isEmpty(accessToken)) {
                                CommonPreference.setAccessToken(accessToken);
                            }


                            startRequestForUploadPushDevice();

                            LoginEvent event = new LoginEvent();
                            event.isLogin = true;
                            EventBus.getDefault().post(event);
                            WEB_RETURN_STRING = "success";
                            if (FROM_WEB.equals("FROM_WEB")) {
                                Intent intent = new Intent();
                                intent.putExtra("result", WEB_RETURN_STRING);
                                setResult(RESULT_OK, intent);
                            }
                            LogUtil.i("LoginActivity", "阿里登录已发送event" + flags);
                            if (flags == MyConstants.FROM_FLAGS_VERIFIED && CommonPreference.isLogin() && !CommonPreference.getUserInfo().hasVerified && CommonPreference.getUserInfo().getUserLevel() != 3) {
                                Intent intent = new Intent(LoginActivity.this, VerifiedActivity.class);
                                startActivity(intent);
                            }
                            LoginActivity.this.finish();
                        }
                    } else if (baseResult.code == ParserUtils.RESPONSE_ALIPAY_UID_UNEXIST) {
                        UserInfo userInfo = ParserUtils.parserUserInfoData(baseResult.obj);
                        Intent intent = new Intent(LoginActivity.this, BindPhoneActivity.class);
                        intent.addFlags(flags);
                        intent.putExtra(MyConstants.EXTRA_TYPE, "2");
                        intent.putExtra(MyConstants.EXTRA_FLAGID, aliId);
                        intent.putExtra(MyConstants.EXTRA_WECHAT_ICON, userInfo.getUserIcon());
                        intent.putExtra(MyConstants.EXTRA_WECHAT_NAME, userInfo.getUserName());
                        intent.putExtra(MyConstants.EXTRA_WECHAT_SEX, String.valueOf(userInfo.getUserSex()));
                        LoginActivity.this.startActivity(intent);
                    } else {
                        WEB_RETURN_STRING = "failed";
                        CommonUtils.error(baseResult, LoginActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //上传推送deviceId
    private void startRequestForUploadPushDevice() {
        if (!CommonUtils.isNetAvaliable(this)) {
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("aliyunDeviceId", CommonPreference.getStringValue(CommonPreference.ALI_PUSH_TOKEN, ""));
        LogUtil.e("lalo", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.UPLOAD_PUSH_DEVICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("laloErrorApp", "startRequestForUploadPushDevice:" + e.toString());
            }


            @Override
            public void onResponse(String response) {
                LogUtil.e("laloResponseApp", "startRequestForUploadPushDevice:" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {

                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("lalo", "startRequestForUploadPushDevice crash:" + e.getMessage());
                }
            }
        });

    }
}
