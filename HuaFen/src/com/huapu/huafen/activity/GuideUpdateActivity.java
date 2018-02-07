package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.events.PushEvent;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.LogUtil;

import de.greenrobot.event.EventBus;

/**
 * 更新导航页
 * @author liang_xs
 *
 */
public class GuideUpdateActivity extends BaseActivity {

	private ViewPager mViewPager;
	private int[] imgRes;
	private ImageView[] views;
	private Button experience;
	private String extraMap;
	/** 点点 */
//	private ImageView[] dots;
	/** 滑动点点所在布局 */
//	private LinearLayout container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		EventBus.getDefault().register(this);
		Intent intent = getIntent();
		if(intent.hasExtra(MyConstants.EXTRA_MAP)){
			extraMap = intent.getStringExtra(MyConstants.EXTRA_MAP);
		}
		initView();
		
//		int dotsSize = 4;
//		dots = new ImageView[dotsSize];
//		for (int i = 0; i < dotsSize; i++) {
//			ImageView iv = new ImageView(this);
//
//			dots[i] = iv;
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//					CommonUtils.dp2px(8), CommonUtils.dp2px(8));// 设置点点大小
//			params.leftMargin = 20;// 设置点点间的间距
//			iv.setLayoutParams(params);
//			if (i == 0) {
//				iv.setBackgroundResource(R.drawable.vp_white_point);
//			} else {
//				iv.setBackgroundResource(R.drawable.vp_gray_point);
//			}
//			container.addView(iv);
//		}
//		
		
		mViewPager.setAdapter(new GuidePagerAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				for (int i = 0; i < views.length; i++) {
					if (arg0 == views.length - 1) {
//						experience.setVisibility(View.VISIBLE);
//						experience.setOnClickListener(new goMainListener());
						views[arg0].setOnClickListener(new goMainListener());
					} else {
//						experience.setVisibility(View.GONE);
						views[i].setOnClickListener(null);
					}
					
//					if (i == arg0) {
//						dots[i].setImageDrawable(getResources().getDrawable(
//								R.drawable.vp_white_point));
//					} else {
//						dots[i].setImageDrawable(getResources().getDrawable(
//								R.drawable.vp_gray_point));
//					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void initView() {
		mViewPager = (ViewPager) findViewById(R.id.first_viewpager);
		imgRes = new int[] { R.drawable.guide_update_01, R.drawable.guide_update_02,R.drawable.guide_update_03};
		views = new ImageView[imgRes.length];
		for (int i = 0; i < imgRes.length; i++) {
			ImageView image = new ImageView(this);
			// 避免OOM
			ImageUtils.showImage(GuideUpdateActivity.this, image, imgRes[i]);
			views[i] = image;
		}
//		container = (LinearLayout) findViewById(R.id.container);
		experience = (Button) findViewById(R.id.immediately_experience);
	}

	private class goMainListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(GuideUpdateActivity.this, MainActivity.class);
			intent.putExtra(MyConstants.EXTRA_MAP,extraMap);
			startActivity(intent);
			finish();
		}

	}

	private class GuidePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return views.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(views[position]);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(views[position]);
			return views[position];
		}
	}

	public void onEventMainThread(final Object obj) {

		if(obj == null) {
			return;
		}
		if(obj instanceof PushEvent){
			LogUtil.e("onEventMainThread..","PushEvent");
			PushEvent event = (PushEvent) obj;
			extraMap=event.extraMap;
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}