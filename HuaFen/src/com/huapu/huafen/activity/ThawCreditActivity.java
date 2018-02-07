package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.album.utils.MD5;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.dialog.TipDialog;
import com.huapu.huafen.fragment.MineFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * 解冻信用金
 */
public class ThawCreditActivity extends BaseActivity {
    private TextView tvPhoneNum;
    private Button btnGetVerificationcode;
    private EditText etVerificationCode;
    private TextView tvBtnThawCredit;
    private TextView tvTip;
    private TextDialog dialog;
    private CountDownTimer mDownTimer;
    private String phone;
    private static final int HANDLER_SMS_BODY = 1001;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SMS_BODY:
                    String strCode = (String) msg.obj;
                    etVerificationCode.setText(strCode);
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thaw_credit);
        initViews();
        startRequestForGetUserPhone();
    }

    private void initViews() {
        setTitleString("解冻信用金");
        tvPhoneNum = (TextView) findViewById(R.id.tvPhoneNum);
        btnGetVerificationcode = (Button) findViewById(R.id.btnGetVerificationcode);
        tvBtnThawCredit = (TextView) findViewById(R.id.tvBtnThawCredit);
        tvTip = (TextView) findViewById(R.id.tvTip);
        etVerificationCode = (EditText) findViewById(R.id.etVerificationCode);


        tvBtnThawCredit.setOnClickListener(this);
        tvTip.setOnClickListener(this);


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
    }

    protected void btnRepeatSet(boolean b, long millisUntilFinished) {
        if (b) {
            /*btnGetVerificationcode.setText("再次获取验证码");
			btnGetVerificationcode.setEnabled(true);
			btnGetVerificationcode.setBackgroundResource(R.drawable.text_pink_round_bg);*/
            btnGetVerificationcode.setText("");
            btnGetVerificationcode.setEnabled(true);
            btnGetVerificationcode.setBackgroundResource(R.drawable.identifying);
        } else {
            long time = millisUntilFinished / 1000;
            if (time < 10) {
                btnGetVerificationcode.setText("0" + time + "s");
            } else {
                btnGetVerificationcode.setText(time + "s");
            }
            btnGetVerificationcode.setEnabled(false);
            btnGetVerificationcode.setGravity(Gravity.CENTER);
			/*btnGetVerificationcode.setBackgroundResource(R.drawable.btn_enable_shape);*/
            btnGetVerificationcode.setBackgroundResource(R.drawable.identifying_check);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetVerificationcode:
                if (TextUtils.isEmpty(phone)) {
                    toast("数据异常，请退出重试");
                    return;
                }
                startRequestForGetCheckCode();
                break;
            case R.id.tvTip:
                TipDialog tipDialog = new TipDialog(ThawCreditActivity.this);
                tipDialog.show();
                break;
            case R.id.tvBtnThawCredit:
                if (TextUtils.isEmpty(etVerificationCode.getText().toString().trim())) {
                    toast("请输入验证码");
                    return;
                }
                if (TextUtils.isEmpty(phone)) {
                    toast("数据异常，请退出重试");
                    return;
                }
                dialog = new TextDialog(ThawCreditActivity.this, false);
                dialog.setContentText("您确定解冻信用金吗？");
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
                        startRequestForCheckcode();
                    }
                });
                dialog.show();
                break;
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
        params.put("phone", phone);
        params.put("code", MD5.Md5(phone + etVerificationCode.getText().toString().trim()));
        OkHttpClientManager.postAsyn(MyConstants.CHECKCODE, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i("liang", "验证验证码:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            ProgressDialog.closeProgress();
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                startRequestForThawCreditMoney();
                            } else {
                                ProgressDialog.closeProgress();
                                CommonUtils.error(baseResult, ThawCreditActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                });
    }


    /**
     * 解冻信用金
     */
    private void startRequestForThawCreditMoney() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        LogUtil.i("liang", "解冻信用金params" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.THAWCREDITMONEY, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        LogUtil.i("liang", "解冻信用金:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                Intent intent = new Intent(ThawCreditActivity.this, SuccessThawActivity.class);
                                startActivity(intent);
                                CommonPreference.setBooleanValue(CommonPreference.USER_CREDIT, false);
                                if (CreditActivity.mActivity != null) {
                                    CreditActivity.mActivity.finish();
                                }
                                if (MineFragment.mineFragment != null && MineFragment.mineFragment.tvXINMoney != null) {
                                    MineFragment.mineFragment.tvXINMoney.setVisibility(View.GONE);
                                    MineFragment.mineFragment.userInfo.setHasCredit(false);
                                }
                                finish();
                            } else {
                                CommonUtils.error(baseResult, ThawCreditActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                });
    }

    /**
     * 获取手机验证码
     */
    private void startRequestForGetCheckCode() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("type", "2");
        OkHttpClientManager.postAsyn(MyConstants.GETCHECKCODE, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        // TODO Auto-generated method stub
                        LogUtil.i("liang", "获取手机验证码:" + response);
                        ProgressDialog.closeProgress();
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                toast("验证码正在努力奔向你的手机");
                                btnGetVerificationcode.setEnabled(false);
                                mDownTimer.start();
                            } else {
                                CommonUtils.error(baseResult, ThawCreditActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }


    /**
     * 获取手机号
     */
    private void startRequestForGetUserPhone() {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();
        OkHttpClientManager.postAsyn(MyConstants.GETUSERPHONE, params,
                new StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                    }

                    @Override
                    public void onResponse(String response) {
                        ProgressDialog.closeProgress();
                        // TODO Auto-generated method stub
                        LogUtil.i("liang", "获取手机号:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                phone = JSON.parseObject(baseResult.obj).getString("phone");
                                if (!TextUtils.isEmpty(phone)) {
                                    if (phone.length() == 11) {
                                        String strPhone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
                                        tvPhoneNum.setText(strPhone);
                                    }
                                    btnGetVerificationcode.setOnClickListener(ThawCreditActivity.this);
                                }
                            } else {
                                CommonUtils.error(baseResult, ThawCreditActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
    }
}
