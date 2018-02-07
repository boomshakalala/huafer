package com.huapu.huafen.activity;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
/**
 *
 * 信用金
 *
 */
public class CreditActivity extends BaseActivity {
	public static CreditActivity mActivity;
	private View layoutXIN;
	private TextView tvBtnPayXIN;
	private UserInfo userInfo;
	private View layoutUnCredit, layoutCredit;
	private TextDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit);
		mActivity = this;
		if(getIntent().hasExtra(MyConstants.EXTRA_CREDIT)) {
			userInfo = (UserInfo) getIntent().getSerializableExtra(MyConstants.EXTRA_CREDIT);
		}
		intViews();
		if(userInfo.getHasCredit()) {
			layoutUnCredit.setVisibility(View.GONE);
			layoutCredit.setVisibility(View.VISIBLE);
			tvBtnPayXIN.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
			tvBtnPayXIN.setTextColor(getResources().getColor(R.color.base_pink));
			tvBtnPayXIN.setText("申请解冻信用金");
		} else {
			layoutUnCredit.setVisibility(View.VISIBLE);
			layoutCredit.setVisibility(View.GONE);
			tvBtnPayXIN.setBackgroundResource(R.drawable.text_pink_round_bg);
			tvBtnPayXIN.setText("点亮信用金");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mActivity = null;
	}

	private void intViews() {
		setTitleString("信用金");
		tvBtnPayXIN = (TextView) findViewById(R.id.tvBtnPayXIN);
		layoutXIN = findViewById(R.id.layoutXIN);
		layoutUnCredit = findViewById(R.id.layoutUnCredit);
		layoutCredit = findViewById(R.id.layoutCredit);

		tvBtnPayXIN.setOnClickListener(this);
		layoutXIN.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent ;
		switch (v.getId()) {
			case R.id.tvBtnPayXIN:
				if(userInfo.getHasCredit()) { // 如果用户已开启，则请求解除，否则跳转到支付
					startRequestForCanUnbind();
				} else {
					intent = new Intent(CreditActivity.this, PayCreditActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.layoutXIN:
				intent = new Intent(this, WebViewActivity.class);
				intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.XINYONGJIN);
				intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "信用金说明");
				startActivity(intent);
				break;
		}
	}


	/**
	 * 是否能解除信用金
	 */
	private void startRequestForCanUnbind() {
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(this);
		HashMap<String, String> params = new HashMap<String, String>();
		OkHttpClientManager.postAsyn(MyConstants.CANUNBIND, params,
				new StringCallback() {

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
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								dialog = new TextDialog(CreditActivity.this, false);
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
										Intent intent = new Intent(CreditActivity.this, ThawCreditActivity.class);
										startActivity(intent);
									}
								});
								dialog.show();
							} else {
								dialog = new TextDialog(CreditActivity.this, false);
								dialog.setTitleText("温馨提示");
								dialog.setContentText("持续30天才可以解冻哦");
								dialog.setLeftText("确定");
								dialog.setLeftCall(new DialogCallback() {

									@Override
									public void Click() {
										dialog.dismiss();
									}
								});
								dialog.show();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				});
	}
}
