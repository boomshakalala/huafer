package com.huapu.huafen.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.utils.CommonUtils;

public class CommonPricePostage extends LinearLayout {

	private TextView tvIsFreeDelivery;
	private View layoutTip;
	private CommonPriceView cpvPrice;

	public CommonPricePostage(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CommonPricePostage(Context context) {
		super(context, null);
		initView();
	}

	public void setData(GoodsData info){
		if(info == null){
			return ;
		}
		cpvPrice.setData(info);
		int countTips = 0;
		if(info.getIsFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}
	public void setData(GoodsInfo info){
		if(info == null){
			return ;
		}
		cpvPrice.setData(info);
		int countTips = 0;
		if(info.getIsFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
			countTips = 1;
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		LayoutParams nameParams = (LayoutParams) cpvPrice.getLayoutParams();
		LayoutParams layoutParams = (LayoutParams) layoutTip.getLayoutParams();
		int right ;
		int left ;
		switch (countTips) {
			case 0:
				right = CommonUtils.dp2px(0);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(0);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 1:
				right = CommonUtils.dp2px(40);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-40);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
			case 2:
				right = CommonUtils.dp2px(80);
				nameParams.rightMargin = right;
				cpvPrice.setLayoutParams(nameParams);
				left = CommonUtils.dp2px(-80);
				layoutParams.leftMargin = left;
				layoutTip.setLayoutParams(layoutParams);
				break;
		}
	}
	private void initView(){
		LayoutInflater inflater=(LayoutInflater) getContext().
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.common_price_postage_layout,this,true);
		tvIsFreeDelivery=(TextView) findViewById(R.id.tvIsFreeDelivery);
		layoutTip = findViewById(R.id.layoutTip);
		cpvPrice = (CommonPriceView) findViewById(R.id.cpvPrice);
	}

}
