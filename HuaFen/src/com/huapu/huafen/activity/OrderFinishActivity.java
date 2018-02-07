package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.facebook.drawee.view.SimpleDraweeView;
/**
 * 订单完成
 * @author liang_xs
 *
 */
public class OrderFinishActivity extends BaseActivity {
	private String proId = "";
	// pulllistview
	private int price = 123;
	private SimpleDraweeView ivHeader;
	private TextView tvName, tvConsignName, tvConsignPhone, tvConsignAddress, tvBrand,  tvTitle, tvPrice;
	private TextView tvBtnCall, tvBtnFaHuo, tvBtnDetails;
	private View layoutBtnCall, layoutBtnFaHuo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_finish);
		if(getIntent().hasExtra(MyConstants.EXTRA_GOODS_DETAIL_ID)) {
			proId = getIntent().getStringExtra(MyConstants.EXTRA_GOODS_DETAIL_ID);
		}
		toast("支付完成界面");
		initView();
	}

	@SuppressLint("WrongViewCast")
	private void initView() {
		setTitleString("支付完成");
		layoutBtnCall = findViewById(R.id.layoutBtnCall);
		layoutBtnFaHuo = (TextView) findViewById(R.id.layoutBtnFaHuo);
		tvBtnDetails = (TextView) findViewById(R.id.tvBtnDetails);
		tvName = (TextView) findViewById(R.id.tvName);
		tvConsignName = (TextView) findViewById(R.id.tvConsignName);
		tvConsignAddress = (TextView) findViewById(R.id.tvConsignAddress);
		tvBrand = (TextView) findViewById(R.id.tvBrand);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvPrice = (TextView) findViewById(R.id.tvPrice);
		
		tvBtnCall.setOnClickListener(this);
		tvBtnFaHuo.setOnClickListener(this);
		tvBtnDetails.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent ;
		switch (v.getId()) {
		case R.id.tvBtnDetails:
			intent = new Intent(OrderFinishActivity.this, OrderDetailActivity.class);
			intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, proId);
			startActivity(intent);
			break;
		case R.id.layoutBtnCall:
			break;
		case R.id.layoutBtnFaHuo:
			break;

		}
	}
}
