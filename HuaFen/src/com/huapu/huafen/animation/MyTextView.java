package com.huapu.huafen.animation;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyTextView extends TextView{

	private int maxWidth;
	private int offsetRight;
	public MyTextView(Context context) {
		super(context, null);
	}
	public MyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		init();
	}
	
	
	private void init(){
		setCompoundDrawablesWithIntrinsicBounds(R.drawable.btn_title_search_pink, 0, 0, 0);
		int left = CommonUtils.dp2px(20);
		int top = CommonUtils.dp2px(5);
		int margin = CommonUtils.dp2px(2);
		setPadding(left, top, left, top);
		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)getLayoutParams();
		layoutParams.setMargins(margin, 0, margin, 0);
	}
	
	public void setOffsetRight(int offsetRight){
		this.offsetRight = offsetRight;
	}
	
	public void setSize(float size){
		int currentWidth = getWidth();
		if(maxWidth < currentWidth){
			//获取最大宽度，为了避免改变宽度之后，无法还原
			maxWidth = currentWidth;
		}
		LayoutParams params = getLayoutParams();
		params.width = Math.round(maxWidth * size);
		setLayoutParams(params);
		int startX = maxWidth - params.width;
		if(startX > maxWidth - offsetRight){
			//设置偏移量，与末尾的圆圈中心对齐
			startX = maxWidth - offsetRight;
		}
		setX(startX);
	}
	
}
