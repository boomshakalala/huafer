package com.huapu.huafen.activity;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.OrderDetailBean;
import com.huapu.huafen.beans.RefundLabel;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.events.OrderDetailRequestEvent;
import com.huapu.huafen.events.RefundFinishEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;
import com.upoc.numberpicker.NumberPicker;

import de.greenrobot.event.EventBus;

/**
 * 填写退款信息
 * @author liang_xs
 *
 */
public class OrderRefundEditActivity extends BaseActivity {
	private View layoutRefundMode, layoutRefundReasonChoose;
	private EditText etRefundMoney;
	private TextView tvRefundMode, tvRefundCount;
	private TextView tvReason;
	private EditText etRefundReason;
	private List<RefundLabel> list = new ArrayList<RefundLabel>();
	private long orderId;
	/** 1,仅退款，2,退货退款*/
	private int refundType;
	private String refundMoney;
	private int refundPrice;
	private int refundPostAge;
	private Dialog dialog;
	private int select = -1;
	private int selectType = -1;
	/** 0,买家，1,卖家*/
	private int orderType;
	private long refundId;
	private TextView tvReasonTitle;
	private TextView tvRefundTip;
	private TextView tvRefundMoneyTip;
	private View layoutRefund;
	private String[] tips = {"仅退款", "退货退款"};
	private boolean isCanBeModifyRefund;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_refund_edit);
		Intent intent= getIntent();
		Bundle bundle = intent.getExtras();
		if(bundle == null){
			return;
		}
		orderId = bundle.getLong(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
		LogUtil.e("OrderRefundEditActivity","orderId:"+orderId);
		refundType = bundle.getInt(MyConstants.EXTRA_ORDER_REFUND_TYPE, 1);
		refundPrice = bundle.getInt(MyConstants.EXTRA_ORDER_REFUND_PRICE, 1);
		refundPostAge = bundle.getInt(MyConstants.EXTRA_ORDER_REFUND_POST_AGE, 1);
		orderType = bundle.getInt(MyConstants.EXTRA_ORDER_TARGET, 0);
		isCanBeModifyRefund = bundle.getBoolean(MyConstants.EXTRA_CAN_MODIFY_REFUND);
		refundId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_REFUND_ID, 0);

		initView();
		int price = refundPrice + refundPostAge;
		String priceDesc = String.format(getResources().getString(R.string.refund_price_desc),price,refundPostAge);
		tvRefundMoneyTip.setText(priceDesc);
		if(refundType == 1) {
			tvRefundMode.setText("仅退款");
			tvRefundTip.setText("未收到货，或与卖家协商仅退款");
		} else if(refundType == 2) {
			tvRefundMode.setText("退货退款");
			tvRefundTip.setText("已收到货，需要退还已收到的货物");
		}

		switch (orderType) {
		case 0:
			setTitleString("申请退款");
			tvReasonTitle.setText("退款理由");
			etRefundReason.setHint("退款说明");
			startRequestForGetRefundReasonList();
			break;

		case 1:
			setTitleString("拒绝退款");
			tvReasonTitle.setText("拒绝理由");
			etRefundReason.setHint("拒绝说明");
			layoutRefund.setVisibility(View.GONE);
			startRequestForGetRefuseReasoList();
			break;
		}
		
	}


	@Override
	public void initTitleBar() {
		getTitleBar().
				setTitle("申请退款").
				setRightText("提交", new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(list.size() < 0 || select < 0) {
							toast("请选择理由");
							return;
						}
						switch (orderType) {
							case 0:
								refundMoney = etRefundMoney.getText().toString();
								if(TextUtils.isEmpty(refundMoney)) {
									toast("请输入退款金额");
									return;
								}
								int price;
								try {
									Number number = NumberFormat.getNumberInstance(Locale.FRENCH).parse(refundMoney);
									price= number.intValue();
								} catch (ParseException e) {
									price = 0;
								}
								if(price == 0) {
									toast("最小金额1元");
									return;
								}
								int maxPrice = refundPrice + refundPostAge;
								if(price > maxPrice) {
									toast("申请最大金额为" + maxPrice);
									return;
								}

								String refundReason = etRefundReason.getText().toString();

								if(TextUtils.isEmpty(refundReason)){
									toast("请填写退款说明");
									return;
								}
								startRequestForApplyRefund();
								break;

							case 1:
								if(TextUtils.isEmpty(etRefundReason.getText().toString())) {
									toast("请填写拒绝说明");
									return;
								}
								startRequestForRefuseRefund();
								break;
						}
					}
				});
	}

	private void initView() {

		layoutRefundMode = findViewById(R.id.layoutRefundMode);
		layoutRefundReasonChoose = findViewById(R.id.layoutRefundReasonChoose);
		layoutRefund = findViewById(R.id.layoutRefund);
		tvRefundMode = (TextView) findViewById(R.id.tvRefundMode);
		tvRefundCount = (TextView) findViewById(R.id.tvRefundCount);
		tvReason = (TextView) findViewById(R.id.tvReason);
		tvReasonTitle = (TextView) findViewById(R.id.tvReasonTitle);
		tvRefundTip = (TextView) findViewById(R.id.tvRefundTip);
		tvRefundMoneyTip = (TextView) findViewById(R.id.tvRefundMoneyTip);

		etRefundMoney = (EditText) findViewById(R.id.etRefundMoney);
		etRefundReason = (EditText) findViewById(R.id.etRefundReason);
		etRefundReason.addTextChangedListener(new TextWatcher() {  
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
		        String content = etRefundReason.getText().toString();  
		        tvRefundCount.setText(content.length() + "/"  
		                + "200");  
		    }  
		  
		});
		if(isCanBeModifyRefund){
			layoutRefundMode.setOnClickListener(this);
		}else{
			layoutRefundMode.setOnClickListener(null);
		}
		layoutRefundReasonChoose.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.layoutRefundMode:
			showReasonTypeDialog(tips);
			break;
		case R.id.layoutRefundReasonChoose:
			if(list != null) {
				String[] reason = new String[list.size()];
				for(int i = 0; i < list.size(); i++) {
					reason[i] = list.get(i).getRefundContent();
				}
				showReasonDialog(reason);
			}
			break;
		}
	}
	

	/**
	 * 拒绝退款
	 * @param 
	 */
	private void startRequestForRefuseRefund(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("refundId", String.valueOf(refundId));
		params.put("refundLabelId", String.valueOf(list.get(select).getRefundLabelId())); 
		if(!TextUtils.isEmpty(etRefundReason.getText().toString())) {
			params.put("refundContent", etRefundReason.getText().toString());
		}
		LogUtil.i("liang", "拒绝退款params:" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.REFUSEREFUND, params,
				new StringCallback() {

					@Override
					public void onError(Request request, Exception e) {
						ProgressDialog.closeProgress();
					}

					@Override
					public void onResponse(String response) {
						ProgressDialog.closeProgress();
						LogUtil.i("liang", "拒绝退款:"+response);
						JsonValidator validator = new JsonValidator();
						boolean isJson = validator.validate(response);
						if(!isJson) {
							return;
						}
						try {
							BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
							if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
								if(!TextUtils.isEmpty(baseResult.obj)) {
									OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
									OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
									oEvent.isUpdate = true;
									EventBus.getDefault().post(oEvent);
									RefundFinishEvent event = new RefundFinishEvent();
									event.isFinish = true;
									EventBus.getDefault().post(event);
									finish();
								}
							} else {
								CommonUtils.error(baseResult, OrderRefundEditActivity.this, "");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				});
	}
	

	/**
	 * 获取退款理由标签
	 * @param 
	 */
	private void startRequestForGetRefundReasonList(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(OrderRefundEditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		LogUtil.i("liang", "退款理由标签params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.GETREFUNDREASONLIST, params,
				new StringCallback() {
			
			@Override
			public void onError(Request request, Exception e) {
				ProgressDialog.closeProgress();
			}
			
			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "退款理由标签:"+response);
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(!TextUtils.isEmpty(baseResult.obj)) {
							list = ParserUtils.parserRefundLabelListData(baseResult.obj);
						}
					} else {
						CommonUtils.error(baseResult, OrderRefundEditActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			
		});
	}
	
	

	/**
	 * 申请退款
	 * @param 
	 */
	private void startRequestForApplyRefund(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(OrderRefundEditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("orderId", String.valueOf(orderId));
		params.put("refundLabelId", String.valueOf(list.get(select).getRefundLabelId()));
		if(tvRefundMode.getText().toString().equals("仅退款")) {
			params.put("refundType", String.valueOf(1));
		} else if(tvRefundMode.getText().toString().equals("退货退款")) {
			params.put("refundType", String.valueOf(2));
		}
		params.put("refundMoney", String.valueOf(refundMoney));
		if(!TextUtils.isEmpty(etRefundReason.getText().toString())) {
			params.put("refundContent", etRefundReason.getText().toString());
		}
		LogUtil.i("liang", "申请退款params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.APPLYREFUND, params,
				new StringCallback() {
			
			@Override
			public void onError(Request request, Exception e) {
				ProgressDialog.closeProgress();
			}
			
			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "申请退款:"+response);
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(!TextUtils.isEmpty(baseResult.obj)) {
							OrderDetailBean bean = ParserUtils.parserOrderDetailData(baseResult.obj);
							OrderDetailRequestEvent oEvent = new OrderDetailRequestEvent();
							oEvent.isUpdate = true;
							EventBus.getDefault().post(oEvent);
							finish();
						}
					} else {
						CommonUtils.error(baseResult, OrderRefundEditActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			
		});
	}
	private void showReasonTypeDialog(final String[] reason) {
		dialog = new Dialog(OrderRefundEditActivity.this, R.style.ClassCount);
		dialog.setContentView(R.layout.dialog_refund_reason);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
		dialogWindow.setWindowAnimations(R.style.shareDialog);
		dialog.show();
		final NumberPicker numberPickerReason = (NumberPicker) dialog.findViewById(R.id.numberPickerReason);
		numberPickerReason.setDisplayedValues(reason);
		numberPickerReason.setMaxValue(reason.length - 1);
		numberPickerReason.setMinValue(0);
		numberPickerReason.setValue(0);
		// 设置是否可以来回滚动
		numberPickerReason.setWrapSelectorWheel(false);
		dialog.findViewById(R.id.dialog_cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.findViewById(R.id.dialog_submit).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				selectType = numberPickerReason.getValue();
				tvRefundMode.setText(reason[selectType]);
				if(selectType == 0) {
					tvRefundTip.setText("未收到货，或与卖家协商仅退款");
				} else if(selectType == 1){
					tvRefundTip.setText("已收到货，需要退还已收到的货物");
				}
			}
		});
	}
	
	private void showReasonDialog(String[] reason) {
		dialog = new Dialog(OrderRefundEditActivity.this, R.style.ClassCount);
		dialog.setContentView(R.layout.dialog_refund_reason);
		Window dialogWindow = dialog.getWindow();
		dialogWindow.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);
		dialogWindow.setWindowAnimations(R.style.shareDialog);
		dialog.show();
		final NumberPicker numberPickerReason = (NumberPicker) dialog.findViewById(R.id.numberPickerReason);
		numberPickerReason.setDisplayedValues(reason);
		numberPickerReason.setMaxValue(reason.length - 1);
		numberPickerReason.setMinValue(0);
		numberPickerReason.setValue(0);
		// 设置是否可以来回滚动
		numberPickerReason.setWrapSelectorWheel(false);
		dialog.findViewById(R.id.dialog_cancle).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		});
		dialog.findViewById(R.id.dialog_submit).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				select = numberPickerReason.getValue();
				tvReason.setText(list.get(select).getRefundContent());
			}
		});
	}
	

	/**
	 * 获取卖家拒绝退款理由标签
	 * @param 
	 */
	private void startRequestForGetRefuseReasoList(){
		if(!CommonUtils.isNetAvaliable(this)) {
			toast("请检查网络连接");
			return;
		}
		ProgressDialog.showProgress(OrderRefundEditActivity.this);
		HashMap<String, String> params = new HashMap<String, String>();
		LogUtil.i("liang", "卖家拒绝退款理由标签params：" + params.toString());
		OkHttpClientManager.postAsyn(MyConstants.GETREFUSEREASO, params,
				new StringCallback() {
			
			@Override
			public void onError(Request request, Exception e) {
				ProgressDialog.closeProgress();
			}
			
			@Override
			public void onResponse(String response) {
				ProgressDialog.closeProgress();
				LogUtil.i("liang", "卖家拒绝退款理由标签:"+response);
				JsonValidator validator = new JsonValidator();
				boolean isJson = validator.validate(response);
				if(!isJson) {
					return;
				}
				try {
					BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
					if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
						if(!TextUtils.isEmpty(baseResult.obj)) {
							list = ParserUtils.parserRefundLabelListData(baseResult.obj);
						}
					} else {
						CommonUtils.error(baseResult, OrderRefundEditActivity.this, "");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
			
		});
	}
	
	
}
