package com.huapu.huafen.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * @author liang_xs
 *
 */
public class OrderMemoEditActivity extends BaseActivity {
	private TextView tvInputCount;
	private EditText etMemo;
	private String memo;
	private long orderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_memo);
		initView();
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_MEMO_EDIT)) {
			memo = getIntent().getStringExtra(MyConstants.EXTRA_ORDER_MEMO_EDIT);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
			orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
		}
		if(!TextUtils.isEmpty(memo)) {
			etMemo.setText(memo);
			etMemo.setSelection(memo.length());
		}
	}

	private void initView() {
		getTitleBar().
				setTitle("订单备注").
				setRightText("保存", new OnClickListener() {

					@Override
					public void onClick(View v) {
						String memo = etMemo.getText().toString().trim();
						if(TextUtils.isEmpty(memo)) {
							toast("请输入文字");
							return;
						}
						memo = memo.replaceAll("\\n", "");
						memo = memo.replaceAll("\\t", "");
						if(orderId == 0) {
							actionForResult(memo);
						} else {
							startRequestForUpdateMemo(memo);
						}
					}
				});
		tvInputCount = (TextView) findViewById(R.id.tvInputCount);
		etMemo = (EditText) findViewById(R.id.etMemo);
		etMemo.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
										  int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
									  int count) {
				String content = etMemo.getText().toString();
				tvInputCount.setText(content.length() + "/"
						+ "100");
			}

		});
		etMemo.setFocusable(true);
		etMemo.setFocusableInTouchMode(true);
		etMemo.requestFocus();
//		CommonUtils.showKeyBoard(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		etMemo.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager inputManager = (InputMethodManager)etMemo.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if(inputManager != null) {
					inputManager.showSoftInput(etMemo, 0);
				}
			}
		}, 500);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(etMemo.getWindowToken(), 0);
		}
	}



	private void actionForResult(String memo) {
		Intent intent;
		intent = new Intent();
		intent.putExtra(MyConstants.EXTRA_ORDER_MEMO_EDIT, memo);
		setResult(RESULT_OK, intent);
		finish();
	}


	/**
	 * 修改订单备注
	 * @param
	 */
	private void startRequestForUpdateMemo(final String memo){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(OrderMemoEditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("orderId", String.valueOf(orderId));
		params.put("orderMemo", memo);
		LogUtil.i("liang", "修改备注params:"+params.toString());
		OkHttpClientManager.postAsyn(MyConstants.CHANGEORDERMEMO, params,
				new OkHttpClientManager.StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "修改备注:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								actionForResult(memo);
							} else {
								CommonUtils.error(baseResult, OrderMemoEditActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}

}
