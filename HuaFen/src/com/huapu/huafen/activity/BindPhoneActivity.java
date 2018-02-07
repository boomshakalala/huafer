package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.album.utils.MD5;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.beans.VerifyCodeData;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.FinishEvent;
import com.huapu.huafen.events.LoginEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * 绑定手机
 *
 * @author liang_xs
 */
public class BindPhoneActivity extends BaseActivity {
    private static final int HANDLER_SMS_BODY = 1001;
    private String userIcon = "";
    private String userName = "";
    private String userSex = "";
    private String bindType = "";
    private String flagId = "";
    private EditText etMobile, etVerificationCode;
    private String strMobile, strVerificationCode;
    private TextView tvBtnBind;
    private Button btnVerificationCode;
    private VerifyCodeData codeData;
    private CountDownTimer mDownTimer;
    private TextView tvTip;

    private String speechType = "";

    private TextView speechTextTips;

    private CountDownTimer speechDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_bind_phone);
        if (getIntent().hasExtra(MyConstants.EXTRA_WECHAT_ICON)) {
            userIcon = getIntent().getStringExtra(MyConstants.EXTRA_WECHAT_ICON);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_WECHAT_NAME)) {
            userName = getIntent().getStringExtra(MyConstants.EXTRA_WECHAT_NAME);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_WECHAT_SEX)) {
            userSex = getIntent().getStringExtra(MyConstants.EXTRA_WECHAT_SEX);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_TYPE)) {
            bindType = getIntent().getStringExtra(MyConstants.EXTRA_TYPE);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_FLAGID)) {
            flagId = getIntent().getStringExtra(MyConstants.EXTRA_FLAGID);
        }
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
                tvTip.setEnabled(true);
            }
        };

    }

    /*
     * (non-Javadoc)
     *
     * @see activity.BaseActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(FinishEvent event) {
        if (event != null && event.isFinish) {
            finish();
        }
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

    private void initView() {
        setTitleString("绑定手机");
        etMobile = (EditText) findViewById(R.id.etMobile);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        btnVerificationCode = (Button) findViewById(R.id.btnVerificationCode);
        tvBtnBind = (TextView) findViewById(R.id.tvBtnBind);
        tvTip = (TextView) findViewById(R.id.tvTip);
        speechTextTips = (TextView) findViewById(R.id.speechTextTips);
        btnVerificationCode.setOnClickListener(this);
        tvBtnBind.setOnClickListener(this);
        tvTip.setOnClickListener(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBtnBind:
                if (checkRegister()) {
                    initCodeData();
                }
                break;

            case R.id.tvTip:
//			TipDialog dialog = new TipDialog(BindPhoneActivity.this);
//			dialog.show();
                strMobile = etMobile.getText().toString().trim();
                if (TextUtils.isEmpty(strMobile)) {
                    toast("手机号为空");
                    return;
                }
                speechType = "2";
                startRequestForGetCheckCode(strMobile);
                break;

            case R.id.btnVerificationCode:
                strMobile = etMobile.getText().toString().trim();
                if (TextUtils.isEmpty(strMobile)) {
                    toast("手机号为空");
                    return;
                }
                speechType = "";
                startRequestForGetCheckCode(strMobile);
                break;

        }
    }


    private void initCodeData() {
        if (codeData == null) {
            toast("数据异常，请返回重试");
            return;
        }
        if (codeData.regCode == 1) {
            startRequestForCheckcode(1);
        } else {
            final TextDialog dialog = new TextDialog(BindPhoneActivity.this, false);
            if (codeData.regCode == 2) {
                dialog.setContentText("该号码已绑定了微信账号，要继续关联支付宝吗");
            } else {
                dialog.setContentText("该号码已绑定了支付宝账号，要继续关联微信吗");
            }

            dialog.setLeftText("取消");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightText("确定");
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    startRequestForCheckcode(2);

                }
            });
            dialog.show();
        }
    }

    /**
     * 验证验证码
     */
    private void startRequestForCheckcode(final int type) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(BindPhoneActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", strMobile);
        params.put("code", MD5.Md5(strMobile + strVerificationCode));
        OkHttpClientManager.postAsyn(MyConstants.CHECKCODE, params, new StringCallback() {

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
                        if (type == 1) { // 注册

                            Intent intent = new Intent(BindPhoneActivity.this, EditNameActivity.class);
                            //TODO
                            intent.putExtra(MyConstants.TYPE, bindType);
                            intent.putExtra(MyConstants.EXTRA_WECHAT_NAME, userName);
                            intent.putExtra(MyConstants.EXTRA_WECHAT_ICON, userIcon);
                            intent.putExtra(MyConstants.EXTRA_FLAGID, flagId);
                            intent.putExtra(MyConstants.EXTRA_WECHAT_SEX, userSex);
                            intent.putExtra(MyConstants.EXTRA_REGISTER_PHONE, strMobile);
                            intent.putExtra(MyConstants.EXTRA_REGISTER_CODE, strVerificationCode);
                            startActivity(intent);
                        } else if (type == 2) {
                            startToBind(codeData.regCode);
                        }

                    } else {
                        CommonUtils.error(baseResult, BindPhoneActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    /**
     * 获取手机验证码
     *
     * @param strMobile
     */
    private void startRequestForGetCheckCode(String strMobile) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(BindPhoneActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", strMobile);
        params.put("type", "1");
        params.put("classify", bindType);
        if (!TextUtils.isEmpty(speechType)) {
            params.put("smsType", speechType);
        }
        OkHttpClientManager.postAsyn(MyConstants.GETCHECKCODE, params, new StringCallback() {


            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "获取手机验证码:" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    codeData = JSON.parseObject(baseResult.obj, VerifyCodeData.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        toast("验证码正在努力奔向你的手机");

                        if (speechType.equals("2")) {
                            tvTip.setText("重新发送");
                            tvTip.setTextColor(getResources().getColor(R.color.text_color_gray));
                            speechTextTips.setVisibility(View.VISIBLE);
                            tvTip.setEnabled(false);
                            speechDownTimer.start();
                        } else {
                            btnVerificationCode.setEnabled(false);
                            mDownTimer.start();
                        }

                    } else {
                        CommonUtils.error(baseResult, BindPhoneActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void startToBind(int regCode) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(BindPhoneActivity.this);
        HashMap<String, String> params = new HashMap<String, String>();
        if (regCode == 3) {
            params.put("type", "1");
            params.put("uid", flagId);
        } else if (regCode == 2) {
            params.put("type", "2");
            params.put("aliId", flagId);
        }
        Set<Entry<String, String>> entrySet = params.entrySet();
        Iterator<Entry<String, String>> it = entrySet.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Entry<String, String> set = it.next();
            sb.append(set.getKey()).append('=').append(set.getValue()).append(" , ");
        }
        LogUtil.i("liang", "登录绑定params：" + params);
        OkHttpClientManager.postAsyn(MyConstants.BIND_ACCOUNT, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "登录绑定：" + response);
                ProgressDialog.closeProgress();
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
                            try {
                                String accessSecret = object.getString("accessSecret");
                                if (!TextUtils.isEmpty(accessSecret)) {
                                    CommonPreference.setAccessSecret(accessSecret);
                                }

                                String accessToken = object.getString("accessToken");
                                if (!TextUtils.isEmpty(accessToken)) {
                                    CommonPreference.setAccessToken(accessToken);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            startRequestForUploadPushDevice();
                            LoginEvent event1 = new LoginEvent();
                            event1.isLogin = true;
                            EventBus.getDefault().post(event1);
                            FinishEvent event = new FinishEvent();
                            event.isFinish = true;
                            EventBus.getDefault().post(event);
                        }
                    } else {
                        CommonUtils.error(baseResult, BindPhoneActivity.this, "");
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

    private boolean checkRegister() {
        strMobile = etMobile.getText().toString().trim();
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

}
