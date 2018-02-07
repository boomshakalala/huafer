package com.huapu.huafen.views;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DelTextView extends TextView {
	private boolean isShowDel = false;
	private int bgRes = R.drawable.text_pink_round_bg;

	public DelTextView(Context context) {
		super(context, null);
	}
	
	public DelTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private void init(){
		setBackgroundResource(bgRes);
		if(isShowDel()) {
			setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_cancel, 0);
		}
		setTextColor(Color.WHITE);
		int left = CommonUtils.dp2px(10);
		int top = CommonUtils.dp2px(5);
		int margin = CommonUtils.dp2px(2);
		setPadding(left, top, left, top);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)getLayoutParams();
		layoutParams.setMargins(margin, 0, margin, 0);
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		init();
	}

	public boolean isShowDel() {
		return isShowDel;
	}

	public void setShowDel(boolean isShowDel) {
		this.isShowDel = isShowDel;
	}

	public int getBgRes() {
		return bgRes;
	}

	public void setBgRes(int bgRes) {
		this.bgRes = bgRes;
	}
	
}
