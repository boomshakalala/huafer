package com.huapu.huafen.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.huapu.huafen.R;
import com.huapu.huafen.banner.ClassBannerHolder;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;

public class ClassBannerView extends LinearLayout implements OnClickListener, OnItemClickListener {

	private String TAG = ClassBannerView.class.getSimpleName();
	private TextView tvTitle;
	private TextView tvRight;
	private ConvenientBanner<CampaignBanner> banner;
	private ArrayList<CampaignBanner> mBanners;
	private float mDownX;
	private float mDownY;
	private boolean isHiddenTitle = true;
	private LinearLayout llTitle;

	//TODO 内部轮播图高
	private Height height =Height.getDefault();
	public enum Height{
		NORMAL_HEIGHT,
		SMALL_HEIGHT,
		;

		public static Height getDefault(){
			return NORMAL_HEIGHT;
		}

	}

	public ClassBannerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public void setBannerHeight(Height height){
		this.height = height;
		//TODO 设置轮播图的高
	}

	public ClassBannerView(Context context) {
		super(context);
		initView();
	}

	public void startTurning(long autoTurningTime){
		banner.startTurning(autoTurningTime);
	}
	public void setBanners(ArrayList<CampaignBanner> banners) {
		this.mBanners = banners;
		if(!isHiddenTitle){
			CampaignBanner item = mBanners.get(0);
			tvTitle.setText(item.getTitle());
			tvRight.setText(item.getNote());
		}

		if (!ArrayUtil.isEmpty(banners)) {
			// 加载网络数据
			banner.setPages(new CBViewHolderCreator<ClassBannerHolder>() {
				@Override
				public ClassBannerHolder createHolder() {
					return new ClassBannerHolder();
				}

			}, mBanners);
			if(banners.size()>1){
				banner.setPointViewVisible(true);
				banner.setPageIndicator(new int[] { R.drawable.ic_page_indicator,
						R.drawable.ic_page_indicator_focused });
			}else{
				banner.setPointViewVisible(false);
			}


			banner.setOnItemClickListener(this);

			banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageScrolled(int i, float v, int i1) {

				}

				@Override
				public void onPageSelected(int position) {
					if(!isHiddenTitle&&position>=0){
						CampaignBanner item = mBanners.get(position);
						tvTitle.setText(item.getTitle());
						tvRight.setText(item.getNote());
					}

				}

				@Override
				public void onPageScrollStateChanged(int i) {

				}
			});
		}
	}
	
	private void initView() {
		setOrientation(LinearLayout.VERTICAL);
		View root = LayoutInflater.from(getContext()).inflate(R.layout.class_banner_layout, this);
		llTitle = (LinearLayout) root.findViewById(R.id.llTitle);
		tvTitle = (TextView) root.findViewById(R.id.tvTitle);
		tvRight = (TextView) root.findViewById(R.id.tvRight);

		if(isHiddenTitle){
			llTitle.setVisibility(View.GONE);
		}else{
			llTitle.setVisibility(View.VISIBLE);
		}
		banner = (ConvenientBanner) root.findViewById(R.id.banner);
		tvRight.setOnClickListener(this);
	}

	
	

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		if(action==MotionEvent.ACTION_DOWN){
			mDownX=ev.getX();
			mDownY=ev.getY();
			getParent().requestDisallowInterceptTouchEvent(true);
		}else if(action==MotionEvent.ACTION_MOVE){
			if(Math.abs(ev.getX()-mDownX)>Math.abs(ev.getY()-mDownY)){
				getParent().requestDisallowInterceptTouchEvent(true);
			}else{
				getParent().requestDisallowInterceptTouchEvent(false);
			}
		}else if(action==MotionEvent.ACTION_UP||action==MotionEvent.ACTION_CANCEL){
			getParent().requestDisallowInterceptTouchEvent(false);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public void onClick(View v) {
		if(v==tvRight){
			onItemClick(banner.getCurrentItem());
		}
	}

	@Override
	public void onItemClick(int position) {
		CampaignBanner item = mBanners.get(position);
		if(item == null) {
			return;
		}
		ActionUtil.dispatchAction(getContext(), item.getAction(), item.getTarget());
	}
	
	public void startTurning(){
		banner.startTurning(3000);
	}
	
	public void stopTurning(){
		banner.stopTurning();
	}

	public void setCanLoop(boolean canLoop){
		banner.setCanLoop(canLoop);
	}
}
