package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.R;
import com.huapu.huafen.album.utils.MD5;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.FinishEvent;
import com.huapu.huafen.events.LoginEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActivityUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 手机登录
 *
 * @author xk
 */
public class PhoneLoginActivity extends BaseActivity implements OnClickListener {
    public static final int EVENT_REFRESH_LANGUAGE = 0x132;
    private LinearLayout llRebind;
    private LinearLayout llEditPhone;
    private Button btnChangePhone;
    private String phoneNum;
    private TextView tvPhoneNumber;
    private EditText etPhoneNum;
    private EditText etVerificationCode;
    private Button btnVerificationCode;
    private TextView tvCantReceive;
    private Button btnSureToBind;
    private CountDownTimer mDownTimer;
    private String strMobile;
    private String strVerificationCode;
    private TextView speechTextTips;
    private String speechType = "";

    private CountDownTimer speechDownTimer;
    private int flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ActivityUtil.addActivities(this);
        flags = getIntent().getFlags();
        initView();

        mDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                btnRepeatSet(false, millisUntilFinished);
            }

            @Override
            public void onFinish() {
                btnRepeatSet(true, 0);
            }
        };

        speechDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                speechTextTips.setVisibility(View.INVISIBLE);
                tvCantReceive.setEnabled(true);
            }
        };

    }

    private void initView() {
        setTitleString("手机登录");
        llEditPhone = (LinearLayout) findViewById(R.id.llEditPhone);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        btnVerificationCode = (Button) findViewById(R.id.btnVerificationCode);
        btnVerificationCode.setOnClickListener(this);
        tvCantReceive = (TextView) findViewById(R.id.tvCantReceive);
        btnSureToBind = (Button) findViewById(R.id.btnSureToBind);
        speechTextTips = (TextView) findViewById(R.id.speechTextTips);
        btnSureToBind.setOnClickListener(this);
        tvCantReceive.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerificationCode:
                strMobile = etPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(strMobile)) {
                    toast("手机号为空");
                    return;
                }
                speechType = "";
                startRequestForGettingCheckCode(strMobile, speechType);
                break;
            case R.id.tvCantReceive:
                strMobile = etPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(strMobile)) {
                    toast("手机号为空");
                    return;
                }
                speechType = "2";
                startRequestForGettingCheckCode(strMobile, speechType);
//                TipDialog dialog = new TipDialog(PhoneLoginActivity.this);
//                dialog.show();
                break;
            case R.id.btnSureToBind:
                if (checkRegister()) {
                    startRequestForCheckcode();
                }
                break;
        }
    }


    private boolean checkRegister() {
        strMobile = etPhoneNum.getText().toString().trim();
        strVerificationCode = etVerificationCode.getText().toString().trim();
        if (TextUtils.isEmpty(strMobile)) {
            toast("手机号码为空");
            return false;
        }
        if (TextUtils.isEmpty(strVerificationCode)) {
            toast("验证码为空");
            return false;
        }
        return true;
    }


    /**
     * 获取手机验证码
     *
     * @param strMobile
     */
    private void startRequestForGettingCheckCode(String strMobile, final String speechType) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        params.put("phone", strMobile);
        params.put("type", "13");
        if (!TextUtils.isEmpty(speechType)) {
            params.put("smsType", speechType);
        }

        OkHttpClientManager.postAsyn(MyConstants.GETCHECKCODE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                Logger.e("get response:" + response);
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "获取手机验证码:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    //					codeData = JSON.parseObject(baseResult.obj, VerifyCodeData.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        toast("验证码正在努力奔向你的手机");
                        if (speechType.equals("2")) {
                            tvCantReceive.setText("重新发送");
                            tvCantReceive.setTextColor(getResources().getColor(R.color.text_color_gray));
                            speechTextTips.setVisibility(View.VISIBLE);
                            tvCantReceive.setEnabled(false);
                            speechDownTimer.start();
                        } else {
                            btnVerificationCode.setEnabled(false);
                            mDownTimer.start();
                        }


                    } else if (baseResult.code == ParserUtils.RESPONSE_PHONE_HAS_BEEN_REG) {
                        ToastUtil.toast(PhoneLoginActivity.this, "手机号已被注册");
                    } else if (baseResult.code == ParserUtils.RESPONSE_PHONE_BINDS_PHONE) {
                        showDialog();
                    } else {
                        CommonUtils.error(baseResult, PhoneLoginActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


    public void showDialog() {
        final TextDialog dialog = new TextDialog(this);
        dialog.setContentText("该手机号未绑定花粉儿帐户");
        dialog.setTvTextDialog("请先使用微信或支付宝进行登录并绑定手机号");
        dialog.setCancelable(false);
        dialog.setLeftText("取消");
        dialog.setLeftCall(new DialogCallback() {

            @Override
            public void Click() {
                dialog.dismiss();
                finish();
            }
        });
        dialog.setRightText("确定");
        dialog.setRightCall(new DialogCallback() {

            @Override
            public void Click() {
                finish();
                FinishEvent event = new FinishEvent();
                event.isPhone = true;
                EventBus.getDefault().post(event);
            }
        });
        dialog.show();
        return;
    }


    protected void btnRepeatSet(boolean b, long millisUntilFinished) {
        if (b) {
            /*btnVerificationCode.setText("再次获取验证码");
            btnVerificationCode.setEnabled(true);
            btnVerificationCode.setBackgroundResource(R.drawable.text_phone_color);*/
            btnVerificationCode.setTextColor(Color.parseColor("#FF6677"));
            btnVerificationCode.setText("获取验证码");
            btnVerificationCode.setEnabled(true);
            btnVerificationCode.setBackgroundResource(R.drawable.text_phone_color);
        } else {
            long time = millisUntilFinished / 1000;
            if (time < 10) {
                btnVerificationCode.setText("0" + time + "s");
            } else {
                btnVerificationCode.setText(time + "s");
            }
            btnVerificationCode.setTextColor(Color.GRAY);
            btnVerificationCode.setEnabled(false);
            btnVerificationCode.setGravity(Gravity.CENTER);
            btnVerificationCode.setBackgroundResource(R.drawable.btn_enable_shape);
            /*btnVerificationCode.setBackgroundResource(R.drawable.identifying_check);*/
        }
    }


    /**
     * 验证验证码
     */
    private void startRequestForCheckcode() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", strMobile);
        params.put("code", MD5.Md5(strMobile + strVerificationCode));
        OkHttpClientManager.postAsyn(MyConstants.CHECKCODE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "验证验证码:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //验证码正确直接跳转登陆
                        startRequestForPhoneUserLogin(strMobile);
                    } else {
                        CommonUtils.error(baseResult, PhoneLoginActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }


    private void startRequestForPhoneUserLogin(final String phone) {
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put(MyConstants.TYPE, "3");
        LogUtil.e("liang", "登录params：" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.USERLOGIN, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "登录:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
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
                            if (flags == MyConstants.FROM_FLAGS_VERIFIED && CommonPreference.isLogin() && !CommonPreference.getUserInfo().hasVerified && CommonPreference.getUserInfo().getUserLevel() != 3) {
                                Intent intent = new Intent(PhoneLoginActivity.this, VerifiedActivity.class);
                                startActivity(intent);
                            }
                            ActivityUtil.finishActivity();
                        }
                    } else {
                        CommonUtils.error(baseResult, PhoneLoginActivity.this, "");
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
