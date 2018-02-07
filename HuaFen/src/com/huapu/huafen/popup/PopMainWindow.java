package com.huapu.huafen.popup;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.JoinBtn;
import com.huapu.huafen.utils.CommonUtils;

public class PopMainWindow extends PopupWindow implements OnClickListener {

	private String TAG = PopMainWindow.class.getSimpleName();

	private Activity mContext;
	private RelativeLayout rlRoot;
	private int mWidth;
	private int mHeight;
	private int statusBarHeight ;
	private Bitmap mBitmap= null;
	private Bitmap overlay = null;
	
	private Handler mHandler = new Handler();
	private LinearLayout layoutPop;
	private LinearLayout layoutArticle;
	private LinearLayout layoutCampaign;
	private ImageView ivCampaign;
	private TextView tvNote;

	public PopMainWindow(Activity context) {
		mContext = context;
	}

	public void init() {
		final RelativeLayout layout = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.pop_main_window, null);
		setContentView(layout);

		ImageView close= (ImageView)layout.findViewById(R.id.center_music_window_close);
		layoutPop = (LinearLayout) layout.findViewById(R.id.layoutPop);
		layoutArticle = (LinearLayout) layout.findViewById(R.id.layoutArticle);
		layoutCampaign = (LinearLayout) layout.findViewById(R.id.layoutCampaign);
		ivCampaign = (ImageView) layout.findViewById(R.id.ivCampaign);
		tvNote = (TextView) layout.findViewById(R.id.tvNote);

		rlRoot = (RelativeLayout) layout.findViewById(R.id.rlRoot);
		rlRoot.setOnClickListener(this);
		close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isShowing()) {
					closeAnimation(layoutPop);
				}
			}

		});

		Rect frame = new Rect();
		mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		statusBarHeight = frame.top;
		DisplayMetrics metrics = new DisplayMetrics();
		mContext.getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		mWidth = metrics.widthPixels;
		mHeight = metrics.heightPixels;
		
		setWidth(mWidth);
		setHeight(mHeight);

	}
	
	private Bitmap blur() {
		if (null != overlay) {
			return overlay;
		}
		long startMs = System.currentTimeMillis();

		View view = mContext.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache(true);
		mBitmap = view.getDrawingCache();

		float scaleFactor = 8;
		float radius = 10;
		int width = mBitmap.getWidth();
		int height =  mBitmap.getHeight();

		overlay = Bitmap.createBitmap((int) (width / scaleFactor),(int) (height / scaleFactor), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(overlay);
		canvas.scale(1 / scaleFactor, 1 / scaleFactor);
		Paint paint = new Paint();
		paint.setFlags(Paint.FILTER_BITMAP_FLAG);
		canvas.drawBitmap(mBitmap, 0, 0, paint);

		overlay = FastBlur.doBlur(overlay, (int) radius, true);
		Log.i(TAG, "blur time is:"+(System.currentTimeMillis() - startMs));
		view.destroyDrawingCache();
		return overlay;
	}
	
	private Animation showAnimation1(final View view, int fromY , int toY) {
		AnimationSet set = new AnimationSet(true);
		TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
		go.setDuration(300);
		TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
		go1.setDuration(100);
		go1.setStartOffset(250);
		set.addAnimation(go1);
		set.addAnimation(go);

		set.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

		});
		return set;
	}
	

	public void showMoreWindow(View anchor) {


		showAnimation(layoutPop);
//		setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
		setOutsideTouchable(true);
		setFocusable(true);
		showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
	}

	private void showAnimation(ViewGroup layout){
		for(int i=0;i<layout.getChildCount();i++){
			final View child = layout.getChildAt(i);
			child.setOnClickListener(this);
			if(child.getVisibility() == View.GONE){
				break;
			}

			child.setVisibility(View.INVISIBLE);
			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					child.setVisibility(View.VISIBLE);
					ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
					fadeAnim.setDuration(300);
					KickBackAnimator kickAnimator = new KickBackAnimator();
					kickAnimator.setDuration(150);
					fadeAnim.setEvaluator(kickAnimator);
					fadeAnim.start();
				}
			}, i * 50);
		}
		
	}

	private void closeAnimation(ViewGroup layout){
		for(int i=0;i<layout.getChildCount();i++){
			final View child = layout.getChildAt(i);
			child.setOnClickListener(this);
			if(child.getVisibility() == View.GONE){
				break;
			}

			mHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					child.setVisibility(View.VISIBLE);
					ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 900);
					fadeAnim.setDuration(200);
					KickBackAnimator kickAnimator = new KickBackAnimator();
					kickAnimator.setDuration(100);
					fadeAnim.setEvaluator(kickAnimator);
					fadeAnim.start();
					fadeAnim.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animator animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animator animation) {
							child.setVisibility(View.INVISIBLE);
							dismiss();
						}
						
						@Override
						public void onAnimationCancel(Animator animation) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}, (layout.getChildCount()-i-1) * 30);
			
//			if(child.getId() == R.id.layoutCamera){
//				mHandler.postDelayed(new Runnable() {
//
//					@Override
//					public void run() {
//						dismiss();
//					}
//				}, (layout.getChildCount()-i) * 30 + 80);
//			}
		}
		
	}

	public void setOnPopViewClickListener(OnPopViewClickListener listener){
		this.mListener = listener;
	}

	@Override
	public void onClick(final View v) {
		if (isShowing()) {
			closeAnimation(layoutPop);
		}
		if(mListener!=null){
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mListener.onPopViewClick(v);
				}
			},150);

		}

	}

	private OnPopViewClickListener mListener;

	public interface OnPopViewClickListener{
		void onPopViewClick(View v);
	}


	public void destroy() {
		if (null != overlay) {
			overlay.recycle();
			overlay = null;
			System.gc();
		}
		if (null != mBitmap) {
			mBitmap.recycle();
			mBitmap = null;
			System.gc();
		}
	}

	public void setLayoutCampaign(JoinBtn btn,Bitmap bmp){
		BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),bmp);
		ivCampaign.setBackground(drawable);
		tvNote.setText(btn.getTitle());
	}

	public void setCampaignVisible(boolean visible){
		if(visible){
			layoutCampaign.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutArticle.getLayoutParams();
			params.bottomMargin = CommonUtils.dp2px(200f);
		}else{
			layoutCampaign.setVisibility(View.GONE);
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutArticle.getLayoutParams();
			params.bottomMargin = CommonUtils.dp2px(100f);
		}

	}

}
