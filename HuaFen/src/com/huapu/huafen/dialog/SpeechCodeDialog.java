package com.huapu.huafen.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.OrdersListAdapter;
import com.huapu.huafen.album.utils.MD5;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.VerifyCodeData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.SecurityPasswordEditText;
import com.huapu.huafen.views.VerificationCodeInput;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 2017/6/10.
 */

public class SpeechCodeDialog extends Dialog {

    @BindView(R.id.verificationCodeInput)
    VerificationCodeInput verificationCodeInput;

    @BindView(R.id.btnVerificationCode)
    TextView btnVerificationCode;

    @BindView(R.id.codeSendTo)
    TextView codeSendTo;

    @BindView(R.id.speechTextTips)
    TextView speechTextTips;

    @BindView(R.id.speechText)
    TextView speechText;

    @BindView(R.id.security_linear)
    SecurityPasswordEditText editSecurityCode;
    private Activity activity;
    private long orderId;

    private String mobile;

    private CountDownTimer mDownTimer;

    private OrdersListAdapter orderListAdapter = null;

    private CountDownTimer speechDownTimer;


    public SpeechCodeDialog(@NonNull Context context, long orderId, String mobile) {
        super(context, R.style.DialogText);
        activity = (Activity) context;
        this.orderId = orderId;
        this.mobile = mobile;
        setCanceledOnTouchOutside(false);

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
                speechText.setEnabled(true);
            }
        };

    }


    public void setOrderListAdapter(OrdersListAdapter orderListAdapter) {
        this.orderListAdapter = orderListAdapter;
    }

    private void btnRepeatSet(boolean b, long millisUntilFinished) {
        if (b) {
            btnVerificationCode.setTextColor(Color.parseColor("#FF6677"));
            btnVerificationCode.setText("获取");
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_speech_text);
        ButterKnife.bind(this);

        codeSendTo.setText("验证码发送至：" + mobile);

        verificationCodeInput.setOnCompleteListener(new VerificationCodeInput.Listener() {
            @Override
            public void onComplete(String content) {
                startRequestForCheckCode(content);
            }
        });

        editSecurityCode.setSecurityEditCompileListener(new SecurityPasswordEditText.SecurityEditCompileListener() {
            @Override
            public void onNumCompleted(String content) {
                Logger.e("get password:" + content);
                startRequestForCheckCode(content);
            }
        });

        startRequestForGetCode("");
    }

    private void startRequestForCheckCode(String content) {
        if (!CommonUtils.isNetAvaliable(activity)) {
            ToastUtil.toast(activity, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(activity);
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("code", MD5.Md5(content));
        params.put("orderId", String.valueOf(orderId));
        params.put("type", "9");
        OkHttpClientManager.postAsyn(MyConstants.CHECKCODE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        Logger.e("code success");
                        SpeechCodeDialog.this.dismiss();

                        if(onReceiptFaceToFaceListener!=null){
                            onReceiptFaceToFaceListener.onReceiptFace2Face();
                        }
                        if (null != orderListAdapter) {
                            orderListAdapter.sureReceivedGoods();
                        }
                    } else {
                        editSecurityCode.clearText();
                        CommonUtils.error(baseResult, activity, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
    }

    public interface OnReceiptFaceToFaceListener{
        void onReceiptFace2Face();
    }

    private OnReceiptFaceToFaceListener onReceiptFaceToFaceListener;

    public void setOnReceiptFaceToFaceListener(OnReceiptFaceToFaceListener onReceiptFaceToFaceListener) {
        this.onReceiptFaceToFaceListener = onReceiptFaceToFaceListener;
    }

    private void startRequestForGetCode(final String speechType) {
        if (!CommonUtils.isNetAvaliable(activity)) {
            ToastUtil.toast(activity, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(activity);
        ArrayMap<String, String> params = new ArrayMap<>();
        params.put("type", "9");
        params.put("orderId", String.valueOf(orderId));
        if (speechType.equals("2")) {
            params.put("smsType", speechType);
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
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ToastUtil.toast(activity, "验证码已发送");
                        if (speechType.equals("2")) {
                            speechText.setText("重新发送");
                            speechText.setTextColor(activity.getResources().getColor(R.color.text_color_gray));
                            speechTextTips.setVisibility(View.VISIBLE);
                            speechText.setEnabled(false);
                            speechDownTimer.start();
                        } else {
                            btnVerificationCode.setEnabled(false);
                            mDownTimer.start();
                        }
                    } else {
                        CommonUtils.error(baseResult, activity, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @OnClick({R.id.closeDialog, R.id.btnVerificationCode, R.id.speechText})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.closeDialog:
                this.dismiss();
                break;
            case R.id.btnVerificationCode:
                startRequestForGetCode("");
                break;
            case R.id.speechText:
                startRequestForGetCode("2");
                break;
        }
    }

    private void startRequestForGetCheckCode() {
        if (!CommonUtils.isNetAvaliable(activity)) {
            ToastUtil.toast(activity, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(activity);
        HashMap<String, String> params = new HashMap<>();
        params.put("type", "9");
        params.put("orderId", String.valueOf(orderId));
        LogUtil.i("liang", "获取手机验证码：" + params.toString());
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
                    VerifyCodeData codeData = JSON.parseObject(baseResult.obj, VerifyCodeData.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ToastUtil.toast(activity, "验证码已发送");
//                        btnVerificationCode.setEnabled(false);
//                        mDownTimer.start();
                    } else {
                        CommonUtils.error(baseResult, activity, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
