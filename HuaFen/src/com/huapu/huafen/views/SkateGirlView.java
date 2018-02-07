package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.huapu.huafen.R;

public class SkateGirlView extends View{

	private Bitmap bitmap;
	private int measuredWidth;
	private int measuredHeight;
	private float mCurrentProgress;
	private Paint mPaint;

	public SkateGirlView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SkateGirlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SkateGirlView(Context context) {
		super(context);
		init();
	}
	private void init(){
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.skate_girl);

		mPaint = new Paint();

	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}
	//测量宽度
	private int measureWidth(int widthMeasureSpec){
		int result = 0;
		int size = MeasureSpec.getSize(widthMeasureSpec);
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		if (MeasureSpec.EXACTLY == mode) {
			result = size;
		}else {
			result = bitmap.getWidth();
			if (MeasureSpec.AT_MOST == mode) {
				result = Math.min(result, size);
			}
		}
		return result;
	}

	private int measureHeight(int heightMeasureSpec){
		int result = 0;
		int size = MeasureSpec.getSize(heightMeasureSpec);
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		if (MeasureSpec.EXACTLY == mode) {
			result = size;
		}else {
			result = bitmap.getHeight();
			if (MeasureSpec.AT_MOST == mode) {
				result = Math.min(result, size);
			}
		}
		return result;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		measuredWidth = w;
		measuredHeight = h;

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();
		canvas.drawBitmap(bitmap, 0,measuredHeight*(1-mCurrentProgress),mPaint);
		canvas.restore();
	}


	public void setCurrentProgress(float currentProgress){
		this.mCurrentProgress = currentProgress;
		postInvalidate();
	}
}
