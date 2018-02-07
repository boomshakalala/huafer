package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;

/**
 * 选择退款
 * @author liang_xs
 *
 */
public class OrderRefundChooseActivity extends BaseActivity {
	private View layoutRefundGoods, layoutRefundMoney;
	private long orderId;
	/** 1,仅退款，2,退货退款*/
	private long refundId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_refund_choose);
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_DETAIL_ID)) {
			orderId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, 0);
		}
		if(getIntent().hasExtra(MyConstants.EXTRA_ORDER_REFUND_ID)) {
			refundId = getIntent().getLongExtra(MyConstants.EXTRA_ORDER_REFUND_ID, 0);
		}
		initView();

	}

	private void initView() {
		setTitleString("申请退款");
		layoutRefundGoods = findViewById(R.id.layoutRefundGoods);
		layoutRefundMoney = findViewById(R.id.layoutRefundMoney);
		layoutRefundMoney.setOnClickListener(this);
		layoutRefundGoods.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.btnTitleLeft:
			finish();
			break;
		}
	}
	

	
}
