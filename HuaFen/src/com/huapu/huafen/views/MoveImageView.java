package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.huapu.huafen.utils.CommonUtils;

/**
 * 在界面中可拖拽的imageview
 * @author liangxs
 *
 */
public class MoveImageView extends ImageView {

	private float lastX = 0;
	private float lastY = 0;
	private float downX, downY;

	private int screenWidth = 0;
	private int screenHeight = 0;
	private int clickDistanceGap = 5;
	public static int DEFAULT_WIDTH = 60;
	public static int DEFAULT_HEIGHT = 60;

	public MoveImageView(final Context context) {
		this(context, null);
	}
	public MoveImageView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		clickDistanceGap *= this.getResources().getDisplayMetrics().density;
		clickDistanceGap *= clickDistanceGap;
		post(new Runnable() {

			public void run() {
				init((Activity) context);
			}
		});
	}

	private void init(Activity activity) {
		Rect rect = new Rect();
		Window window = activity.getWindow();
		getWindowVisibleDisplayFrame(rect);
		// 状态栏的高度
		int statusBarHeight = rect.top;
		// 标题栏跟状态栏的总体高度
		int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		// 标题栏的高度：用上面的值减去状态栏的高度及为标题栏高度
		int titleBarHeight = contentViewTop - statusBarHeight;
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels - contentViewTop;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setSelected(true);
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			downX = event.getRawX();
			downY = event.getRawY();
			break;

		case MotionEvent.ACTION_MOVE:
			float dx = event.getRawX() - lastX;
			float dy = event.getRawY() - lastY;

			float left = getLeft() + dx;
			float top = getTop() + dy;
			float right = getRight() + dx;
			float bottom = getBottom() + dy;
			// 设置不能出界
			if (left < 0) {
				left = 0;
				right = left + getWidth();
			}

			if (right > screenWidth) {
				right = screenWidth;
				left = right - getWidth();
			}

			if (top < 0) {
				top = 0;
				bottom = top + getHeight();
			}

			if (bottom > screenHeight) {
				bottom = screenHeight;
				top = bottom - getHeight();
			}
//			layout((int) left, (int) top, (int) right, (int) bottom);
			lastX = event.getRawX();
			lastY = event.getRawY();
			params.topMargin = (int) top;
			params.leftMargin = (int) left;
			setLayoutParams(params);
			break;
		case MotionEvent.ACTION_UP:
			setSelected(false);
			if (lastX >= screenWidth / 2) { // 吸边
//				layout((int) screenWidth - getWidth(), (int) getTop(),
//						(int) screenWidth, (int) getBottom());
				params.gravity = Gravity.RIGHT;
			} else {
//				layout((int) 0, (int) getTop(), (int) getWidth(),
//						(int) getBottom());
				params.gravity = Gravity.LEFT;
			}
			params.topMargin = getTop();
			setLayoutParams(params);
			// 判断是不是没有移动，而是点击事件
			if (Math.pow(event.getRawX() - downX, 2)
					+ Math.pow(event.getRawY() - downY, 2) <= clickDistanceGap) {
				imageClickListener.onImageClick();
			}
			break;

		}

		return true;
	}

	public void setOnImageClickListener(ImageClickListener l) {
		imageClickListener = l;
	}

	private ImageClickListener imageClickListener;

	public interface ImageClickListener {
		public void onImageClick();
	}

}