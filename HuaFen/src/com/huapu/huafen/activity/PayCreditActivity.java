package com.huapu.huafen.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.MineFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.weixin.WeChatPayHelper;
import com.squareup.okhttp.Request;

import java.util.HashMap;
/**
 * 
 * 交纳信用金
 *
 */
public class PayCreditActivity extends BaseActivity {

	private Button btnCheckedWeChat,btnCheckedZFB;
	private TextView tvBtnPayXIN;
	private String order_no;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_credit);
		
		intViews();
	}

	private void intViews() {
		setTitleString("交纳信用金");
		btnCheckedWeChat = (Button) findViewById(R.id.btnCheckedWeChat);
		btnCheckedZFB = (Button) findViewById(R.id.btnCheckedZFB);
		tvBtnPayXIN = (TextView) findViewById(R.id.tvBtnPayXIN);
		//微信支付
		btnCheckedWeChat.setOnClickListener(this);
		//支付宝
		btnCheckedZFB.setOnClickListener(this);
		tvBtnPayXIN.setOnClickListener(this);
		btnCheckedWeChat.setSelected(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCheckedZFB:
			btnCheckedZFB.setSelected(true);
			btnCheckedWeChat.setSelected(false);
			break;
		case R.id.btnCheckedWeChat:
			btnCheckedWeChat.setSelected(true);
			btnCheckedZFB.setSelected(false);
			break;
		case R.id.tvBtnPayXIN:
			startRequestForCreditPay();
			break;
		}
	}
	

	/**
	 * 支付信用金
	 * @param 
	 */
	private void startRequestForCreditPay(){
		tvBtnPayXIN.setOnClickListener(null);
		if(!CommonUtils.isNetAvaliable(this)) {
			tvBtnPayXIN.setOnClickListener(PayCreditActivity.this);
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(PayCreditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		LogUtil.i("liang", "支付信用金params:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.CREDITPAY, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
						tvBtnPayXIN.setOnClickListener(PayCreditActivity.this);
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "支付信用金:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								if(!TextUtils.isEmpty(baseResult.obj)) {
									String credential = JSON.parseObject(baseResult.obj).getString("credential");
									order_no = JSON.parseObject(credential).getString("order_no");
									WeChatPayHelper.createPayment(PayCreditActivity.this, credential);
								}
							} else {
								CommonUtils.error(baseResult, PayCreditActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}


				});
	}


	/**
	 * 支付成功后通知服务器
	 * @param 
	 */
	private void startRequestForPaySuccess(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(PayCreditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("orderNo", order_no);
		LogUtil.i("liang", "支付成功通知服务器params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.CREDITPAYSUCCESS, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "支付成功后通知服务器:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								CommonPreference.setBooleanValue(CommonPreference.USER_CREDIT, true);
								if(MineFragment.mineFragment != null && MineFragment.mineFragment.tvXINMoney != null) {
									MineFragment.mineFragment.tvXINMoney.setVisibility(View.VISIBLE);
									MineFragment.mineFragment.userInfo.setHasCredit(true);
								}
								if(CreditActivity.mActivity != null) {
									CreditActivity.mActivity.finish();
								}
								finish();
							} else {
								CommonUtils.error(baseResult, PayCreditActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}


				});
	}
	

	/**
	 * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
	 * 最终支付成功根据异步通知为准
	 */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == RESULT_OK) {
    		//支付页面返回处理
            if (requestCode == WeChatPayHelper.REQUEST_CODE_PAYMENT) {
            	tvBtnPayXIN.setOnClickListener(this);
                if (resultCode == Activity.RESULT_OK) {
                    String result = data.getExtras().getString("pay_result");
                    /* 处理返回值
                     * "success" - payment succeed
                     * "fail"    - payment failed
                     * "cancel"  - user canceld
                     * "invalid" - payment plugin not installed
                     */
                    if(result.equals("success")) {
                    	startRequestForPaySuccess();
                    } else {
                    	 String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                         String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
//                         showMsg(result, errorMsg, extraMsg);
                    }
                }
            }
    	}
    }
    

    public void showMsg(String title, String msg1, String msg2) {
    	String str = title;
    	if (null !=msg1 && msg1.length() != 0) {
    		str += "\n" + msg1;
    	}
    	if (null !=msg2 && msg2.length() != 0) {
    		str += "\n" + msg2;
    	}
    	AlertDialog.Builder builder = new Builder(this);
    	builder.setMessage(str);
    	builder.setTitle("提示");
    	builder.setPositiveButton("OK", null);
    	builder.create().show();
    }
	
}
