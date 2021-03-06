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

import static com.huapu.huafen.R.id.tvDiscount;

public class CommonTagView extends LinearLayout {

	private TextView tvIsFreeDelivery;
	private TextView tvIsNew;
	private TextView tvDiscount;

	public CommonTagView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public CommonTagView(Context context) {
		super(context, null);
		initView();
	}

	public void setData(GoodsData info){
		if(info == null){
			return ;
		}
		if(info.getIsFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(info.getIsNew() == 1) {
			tvIsNew.setVisibility(VISIBLE);
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(info.getDiscount() >0&&info.getDiscount()<10) {
			tvDiscount.setVisibility(VISIBLE);
			tvDiscount.setText(String.valueOf(info.getDiscount())+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}
	}
	public void setData(GoodsInfo info){
		if(info == null){
			return ;
		}
		if(info.getIsFreeDelivery()) {
			tvIsFreeDelivery.setVisibility(View.VISIBLE);
		} else {
			tvIsFreeDelivery.setVisibility(View.GONE);
		}
		if(info.getIsNew() == 1) {
			tvIsNew.setVisibility(VISIBLE);
		} else {
			tvIsNew.setVisibility(GONE);
		}

		if(info.getDiscount() >0&&info.getDiscount()<10) {
			tvDiscount.setVisibility(VISIBLE);
			tvDiscount.setText(String.valueOf(info.getDiscount())+"折");
		} else {
			tvDiscount.setVisibility(GONE);
		}
	}
	private void initView(){
		LayoutInflater inflater=(LayoutInflater) getContext().
				getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.common_tag_layout,this);
		tvIsFreeDelivery=(TextView) findViewById(R.id.tvIsFreeDelivery);
		tvIsNew=(TextView) findViewById(R.id.tvIsNew);
		tvDiscount = (TextView) findViewById(R.id.tvDiscount);
	}

}
