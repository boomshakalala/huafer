package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.huapu.huafen.R;

public class RapidView extends View {
	private Context mContext = null;
	private NinePatchDrawable localBitmap = null;
	private static final int SPACE_RIGHT = 5;
	private Align align = Align.CENTER;
	public static final int count = 26;

	private boolean isTouch = false;

	public RapidView(Context context) {
		super(context);
		this.mContext = context;
		localBitmap = ((NinePatchDrawable) mContext.getResources().getDrawable(R.drawable.contact_bg_rapidview));
	}

	public RapidView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		localBitmap = ((NinePatchDrawable) mContext.getResources().getDrawable(R.drawable.contact_bg_rapidview));
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}
	
	public RapidView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		localBitmap = ((NinePatchDrawable) mContext.getResources().getDrawable(R.drawable.contact_bg_rapidview));
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		paint.setColor(0xff888888);
		paint.setStyle(Style.FILL);

		float h = getHeight() - 10;
		float textSize = h * 2 / 3 / count;
		paint.setTextSize(textSize);
		paint.setTextAlign(align);

		float spaceH = (h - count * textSize) / (count);

		if (isTouch) {
			localBitmap.setBounds(new Rect(getWidth() - (int) textSize - 2 * 3 - SPACE_RIGHT, 0, getWidth() - SPACE_RIGHT, getHeight()));
			localBitmap.draw(canvas);
		}

		String drawText = null;
		for (int i = 0; i < count; i++) {
//			if (i == 0) {
//				drawText = "GPS";
//			}
////			else if (i == 1) {
////				drawText = "热门";
////			}
////			else if (i == count - 1) {
////				drawText = "";
////			}
//			else {
//				drawText = String.valueOf((char) ('A' + (i - 2)));
//			}
			drawText = String.valueOf((char) ('A' + (i)));
			canvas.drawText(drawText, getWidth() - textSize / 2 - 3 - SPACE_RIGHT, (i + 1) * (textSize + spaceH), paint);
		}

		if (isTouch) {
			invalidate(new Rect(getWidth() - (int) textSize - 2 * 3 - SPACE_RIGHT, 0, getWidth() - SPACE_RIGHT, getHeight()));
		}
	}

	public void setTouch(boolean isTouch) {
		this.isTouch = isTouch;
	}
}