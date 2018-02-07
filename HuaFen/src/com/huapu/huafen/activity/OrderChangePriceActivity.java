package com.huapu.huafen.activity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

/**
 * 修改订单价格
 * @author liang_xs
 *
 */
public class OrderChangePriceActivity extends BaseActivity {
	private EditText etPrice, etPostage;
	private long orderId;
	private int price, postage;
	private boolean isFreeDelivery;
	private String mPrice, mPostage;
	private int campaignId;
	private TextView tvCampaignTip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_change_price);
		initView();
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
			orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0L);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_PRICE)) {
			price = getIntent().getIntExtra(MyConstants.EXTRA_ORDER_PRICE, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_POSTAGE)) {
			postage = getIntent().getIntExtra(MyConstants.EXTRA_ORDER_POSTAGE, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_FREEDELIVERY)) {
			isFreeDelivery = getIntent().getBooleanExtra(MyConstants.EXTRA_ORDER_FREEDELIVERY, false);
		}
		if(getIntent().hasExtra(MyConstants.CAMPAIGN_ID)) {
			campaignId = getIntent().getIntExtra(MyConstants.CAMPAIGN_ID, 0);
		}
		if(campaignId > 0) {
			etPrice.setEnabled(false);
			tvCampaignTip.setVisibility(View.VISIBLE);
		}
		if(isFreeDelivery) {
			etPostage.setEnabled(false);
			etPostage.setHint("包邮的物品不能修改邮费了哦");
		}
		etPrice.setText(String.valueOf(price));
		etPrice.setSelection(String.valueOf(price).length());
		etPostage.setText(String.valueOf(postage));
		etPostage.setSelection(String.valueOf(postage).length());
	}
	private void initView() {
		getTitleBar().
				setTitle("修改价格").
				setRightText("提交", new OnClickListener() {

					@Override
					public void onClick(View v) {
						mPrice = etPrice.getText().toString().trim();
						mPostage = etPostage.getText().toString().trim();
						if(TextUtils.isEmpty(mPrice)) {
							toast("请输入商品售价");
							return;
						}
						int price;
						try {
							Number number = NumberFormat.getNumberInstance(Locale.FRENCH).parse(mPrice);
							price= number.intValue();
						} catch (ParseException e) {
							price = 0;
						}
						if(price == 0) {
							toast("售价最小金额1元");
							return;
						}
						if(isFreeDelivery) {
							mPostage = "0";
						} else {
							if(TextUtils.isEmpty(mPostage)) {
								toast("请输入邮费");
								return;
							}
						}
						startRequestForChangeOrderPrice();
					}
				});
		etPrice = (EditText) findViewById(R.id.etPrice);
		etPostage = (EditText) findViewById(R.id.etPostage);
		tvCampaignTip = (TextView) findViewById(R.id.tvCampaignTip);
	}

	

	/**
	 * 修改订单价格
	 * @param 
	 */
	private void startRequestForChangeOrderPrice(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("orderId", String.valueOf(orderId));
		params.put("orderPrice", mPrice);
		params.put("orderPostage", mPostage);
		LogUtil.i("liang", "修改订单价格params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.CHANGEORDERPRICE, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "修改订单价格:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								toast("提交成功");
								setResult(RESULT_OK);
								finish();
							} else {
								CommonUtils.error(baseResult, OrderChangePriceActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}


				});
	}
	
}
