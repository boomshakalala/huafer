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
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * 重新绑定手机号
 *
 * @author liang_xs
 */
public class PhoneRebindActivity extends BaseActivity implements OnClickListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_rebind);
        Intent intent = getIntent();
        if (intent.hasExtra(MyConstants.EXTRA_PHONE_NUMBER)) {
            phoneNum = intent.getStringExtra(MyConstants.EXTRA_PHONE_NUMBER);
        }
        if (TextUtils.isEmpty(phoneNum)) {
            final TextDialog dialog = new TextDialog(this);
            dialog.setContentText("手机号不存在");
            dialog.setCancelable(false);
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.show();
            return;
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
                tvCantReceive.setEnabled(true);
            }
        };

    }

    private void initView() {
        setTitleString("绑定手机号");
        llRebind = (LinearLayout) findViewById(R.id.llRebind);
        llEditPhone = (LinearLayout) findViewById(R.id.llEditPhone);
        btnChangePhone = (Button) findViewById(R.id.btnChangePhone);
        btnChangePhone.setOnClickListener(this);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText("当前手机号：" + phoneNum);
        etPhoneNum = (EditText) findViewById(R.id.etPhoneNum);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);
        btnVerificationCode = (Button) findViewById(R.id.btnVerificationCode);
        btnVerificationCode.setOnClickListener(this);
        tvCantReceive = (TextView) findViewById(R.id.tvCantReceive);
        speechTextTips = (TextView) findViewById(R.id.speechTextTips);
        btnSureToBind = (Button) findViewById(R.id.btnSureToBind);
        btnSureToBind.setOnClickListener(this);
        tvCantReceive.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangePhone:
                llRebind.setVisibility(View.GONE);
                llEditPhone.setVisibility(View.VISIBLE);
                break;
            case R.id.btnVerificationCode:
                strMobile = etPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(strMobile)) {
                    toast("手机号为空");
                    return;
                }
                speechType = "";
                startRequestForGettingCheckCode(strMobile, "");
                break;
            case R.id.tvCantReceive:
                strMobile = etPhoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(strMobile)) {
                    toast("手机号为空");
                    return;
                }
                speechType = "2";
                startRequestForGettingCheckCode(strMobile, "2");
//			TipDialog dialog = new TipDialog(PhoneRebindActivity.this);
//			dialog.show();
                break;
            case R.id.btnSureToBind:
                if (checkRegister()) {
                    startRequestForRebindingPhone();
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

    private void startRequestForRebindingPhone() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phoneCode", MD5.Md5(strMobile + strVerificationCode));
        params.put("phone", strMobile);
        OkHttpClientManager.postAsyn(MyConstants.REBIND_PHONE_NUMBER, params, new OkHttpClientManager.StringCallback() {

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
                        JSONObject object = JSON.parseObject(baseResult.obj).getJSONObject("userInfo");
                        String newPhoneNum = object.getString("phone");
                        Intent intent = new Intent();
                        intent.putExtra(MyConstants.EXTRA_PHONE_NUMBER, newPhoneNum);
                        intent.setClass(PhoneRebindActivity.this, PhoneFinishActivity.class);
                        startActivity(intent);

                        Intent data = new Intent();
                        data.putExtra(MyConstants.EXTRA_NEW_PHONE_NUM, newPhoneNum);
                        setResult(RESULT_OK, data);

//						RebindSucEvent event = new RebindSucEvent();
//						event.isRebindSuc = true;
//						EventBus.getDefault().post(event);

                        finish();
                    } else {
                        CommonUtils.error(baseResult, PhoneRebindActivity.this, "");
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
    private void startRequestForGettingCheckCode(String strMobile, String type) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", strMobile);
        params.put("type", "11");

        if (!TextUtils.isEmpty(type)) {
            params.put("smsType", "2");
        }
        OkHttpClientManager.postAsyn(MyConstants.GETCHECKCODE, params, new OkHttpClientManager.StringCallback() {

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
                        ToastUtil.toast(PhoneRebindActivity.this, "手机号已被注册");
                    } else {
                        CommonUtils.error(baseResult, PhoneRebindActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
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

}
