package com.huapu.huafen.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.MyFragmentPagerAdapter;
import com.huapu.huafen.banner.AccordionTransformer;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.fragment.FollowGoodsFragment;
import com.huapu.huafen.fragment.FollowPersonalFragment;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.scrollablelayoutlib.ScrollableLayout;
import com.huapu.huafen.scrollablelayoutlib.ScrollableLayout.OnScrollListener;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.ClassBannerView;
import com.huapu.huafen.views.TitleBarNew;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * @ClassName: FollowPagerActivity
 * @Description: 关注
 * @author liang_xs
 * @date 2017-01-16
 */
public class FollowPagerActivity extends BaseActivity {

	public ViewPager viewPager;
	private View layoutHeader;
	//一块滚动的布局(下拉除外)
	public ScrollableLayout mScrollLayout;
	//推荐商品的Fragment
	public FollowPersonalFragment followPersonalFragment;
	public FollowGoodsFragment followGoodsFragment;
	//下拉刷新
	public PtrFrameLayout mPtrFrame;
	//广告轮播翻转效果(任取其中一种)
	private ArrayList<String> transformerList = new ArrayList<String>();
	//广告轮播
	private ClassBannerView classBanner;
	private ImageView indexBall;
	private boolean isSearshExpand = true;
	private BannerData bannerData;
	private boolean isFirstRequestFollow = true;
	public int pageType = 0;
	private Button btnBack;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_follow_pager);
		initView();
		initBanners();
	}

	private void initBanners(){
		bannerData = CommonPreference.getFollowingBanner();
		int height =0 ;
		if(bannerData !=null&&!ArrayUtil.isEmpty(bannerData.getBanners())){
			ArrayList<CampaignBanner> banners = bannerData.getBanners();
			classBanner.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) classBanner.getLayoutParams();
			layoutParams.height = CommonUtils.getMyHeight();
			height = layoutParams.height;
			classBanner.setLayoutParams(layoutParams);
			classBanner.setBanners(banners);
			CommonUtils.setAutoLoop(bannerData, classBanner);
		}else{
			classBanner.setVisibility(View.GONE);
		}
		LayoutParams params = layoutHeader.getLayoutParams();
		params.height = height;
		layoutHeader.setLayoutParams(params);
	}

	private void showIndexBall(){
		indexBall.setVisibility(View.VISIBLE);
		ObjectAnimator animator = ObjectAnimator.ofFloat(indexBall,"translationY",CommonUtils.getScreenHeight(),0.0f);
		animator.setDuration(500);
		animator.start();
	}

	private void hideIndexBall(){
		indexBall.setVisibility(View.VISIBLE);
		ObjectAnimator animator = ObjectAnimator.ofFloat(indexBall,"translationY",0.0f,CommonUtils.getScreenHeight());
		animator.addListener(new Animator.AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				indexBall.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		animator.setDuration(500);
		animator.start();

	}

	private TitleBarNew titleBar;
	private RadioButton rb1, rb2;
	private RadioGroup rg;
	private View llMoveLine;
	private void initView() {
		titleBar = (TitleBarNew) findViewById(R.id.titleBar);
		titleBar.setAlpha(0.0f);
		final View titleLayout = LayoutInflater.from(this).inflate(R.layout.comment_title_view,null,false);
		rb1 = (RadioButton) titleLayout.findViewById(R.id.rb1);
		rb2 = (RadioButton) titleLayout.findViewById(R.id.rb2);
		rg = (RadioGroup) titleLayout.findViewById(R.id.rg);
		llMoveLine = titleLayout.findViewById(R.id.llMoveLine);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.rb1:
						ObjectAnimator animator1 = ObjectAnimator.ofFloat(llMoveLine,"translationX",0.0f);
						animator1.setDuration(120);
						animator1.start();
						viewPager.setCurrentItem(0);
						break;
					case R.id.rb2:
						ObjectAnimator animator2 = ObjectAnimator.ofFloat(llMoveLine, "translationX", 0.0f, rg.getWidth() / 2);
						animator2.setDuration(120);
						animator2.start();
						viewPager.setCurrentItem(1);
						break;
				}
			}
		});
		titleLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llMoveLine.getLayoutParams();
				params.width = rg.getWidth() / 2;
				llMoveLine.setLayoutParams(params);
				llMoveLine.layout(rg.getLeft(), llMoveLine.getTop(), rg.getLeft() + rg.getWidth() / 2, llMoveLine.getBottom());
				rg.check(R.id.rb1);
				titleLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		titleBar.setTitle(titleLayout);
		btnBack = (Button) findViewById(R.id.btnBack);

		btnBack.setOnClickListener(this);
		indexBall = (ImageView) findViewById(R.id.ivBall);
		indexBall.setOnClickListener(this);
		viewPager = (ViewPager) findViewById(R.id.vpGoods);
		layoutHeader = findViewById(R.id.layoutHeader);
		// 头部广告位
		classBanner = (ClassBannerView) findViewById(R.id.classBanner);
		transformerList.add(AccordionTransformer.class.getSimpleName());
		// 整个滚动区域布局，下拉刷新布局除外
		mScrollLayout = (ScrollableLayout) findViewById(R.id.scrollableLayout);
		mScrollLayout.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(int currentY, int maxY) {

				if (currentY < maxY && !isSearshExpand) {
					isSearshExpand = true;
					hideIndexBall();
				} else if (currentY >= maxY && isSearshExpand) {
					isSearshExpand = false;
					showIndexBall();
				}
			}
		});
		// 下拉刷新
		setupPullToRefresh();
		// 水平滚动的ViewPager
		PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerTabStrip);
		initFragmentPager(viewPager, pagerSlidingTabStrip, mScrollLayout);
	}

	public void initFragmentPager(final ViewPager viewPager, PagerSlidingTabStrip pagerSlidingTabStrip, final ScrollableLayout mScrollLayout) {
		final ArrayList<ScrollAbleFragment> fragmentList = new ArrayList<ScrollAbleFragment>();
		followPersonalFragment = FollowPersonalFragment.newInstance();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isFirst",true);
		followGoodsFragment = FollowGoodsFragment.newInstance(bundle);
		fragmentList.add(followGoodsFragment);
		fragmentList.add(followPersonalFragment);

		final List<String> titleList = new ArrayList<String>();
		titleList.add("店主更新");
		titleList.add("关注");

		viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleList));
		mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(0));
		pagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#FFFFFF"));
		pagerSlidingTabStrip.setViewPager(viewPager);
		pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int i, float v, int i2) {

			}

			@Override
			public void onPageSelected(int i) {
				Log.e("onPageSelected", "page:" + i);
				if(i == 0){
					rg.check(R.id.rb1);
					pageType = i;
					/** 标注当前页面 **/
					mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(i));
//					if(isFirstRequestFollow) {
//						isFirstRequestFollow = false;
//						followGoodsFragment.startRequestForFollowing(LOADING);
//					}
					if(isSearshExpand) {
						followGoodsFragment.listViewToTop();
					}
				}else{
					rg.check(R.id.rb2);
					pageType = i;
					/** 标注当前页面 **/
					mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(i));
					if(isSearshExpand) {
						followPersonalFragment.listViewToTop();
					}
				}
			}

			@Override
			public void onPageScrollStateChanged(int i) {

			}
		});
		viewPager.setCurrentItem(0);
		followPersonalFragment.setOnFollowPersonalPullListener(new FollowPersonalFragment.OnFollowPersonalPullListener() {
			@Override
			public void onRecommendPull() {
				if (mPtrFrame != null) {
					mPtrFrame.refreshComplete();
				}
			}
		});
		followGoodsFragment.setOnFollowGoodsPullListener(new FollowGoodsFragment.OnFollowGoodsPullListener() {
			@Override
			public void onRecommendPull() {
				if (mPtrFrame != null) {
					mPtrFrame.refreshComplete();
				}
			}
		});
	}

	public void setupPullToRefresh() {
		mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.rotate_header_web_view_frame);
		mPtrFrame.setPtrHandler(new PtrHandler() {
			@Override
			public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
				return mScrollLayout.canPtr();
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mScrollLayout, header);
			}

			@Override
			public void onRefreshBegin(PtrFrameLayout frame) {
				if(pageType == 0) {
					followGoodsFragment.startRequestForFollowing(REFRESH);
				} else if(pageType == 1) {
					followPersonalFragment.startRequestForFansList(REFRESH);
				}
//            	long systemTime = System.currentTimeMillis() / 1000;
//            	if(systemTime - requestTime < 30) {
//            		mPtrFrame.postDelayed(new Runnable() {
//    					@Override
//    					public void run() {
//    						if (mPtrFrame != null) {
//    							mPtrFrame.refreshComplete();
//    						}
//    					}
//    				}, 2000);
//            	} else {
//                	requestTime = System.currentTimeMillis() / 1000;
//            		if(pageType == 0) {
//    	        		followPersonalFragment.startRequestForGetRecommendData();
//    	        	} else if(pageType == 1) {
//    	        		followGoodsFragment.startRequestForGetFollowData();
//    	        	}
//            	}

//                mPtrFrame.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                    	if(mOnChangeListener != null) {
//    						mOnChangeListener.refreshListview();
//    					}
//                   // 	recommendList.getData();
//                        mPtrFrame.refreshComplete();
//                    }
//                }, 100);
			}
		});

		// the following are default settings
		mPtrFrame.setResistance(1.7f);
		mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
		mPtrFrame.setDurationToClose(200);
		mPtrFrame.setDurationToCloseHeader(300);
		// default is false
		mPtrFrame.setPullToRefresh(false);
		// default is true
		mPtrFrame.setKeepHeaderWhenRefresh(true);
		// 进入页面就自动刷新
//        mPtrFrame.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mPtrFrame.autoRefresh();
//            }
//        }, 100);

	}



	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.ivBall:
				if(pageType == 0) {
					mScrollLayout.scrollTo(0, 0);
					followGoodsFragment.listViewToTop();
				} else if(pageType == 1) {
					mScrollLayout.scrollTo(0, 0);
					followPersonalFragment.listViewToTop();
				}
				break;
			default:
				break;
		}
	}

	// 开始自动翻页(广告轮播)
	@Override
	public void onResume() {
		super.onResume();
		CommonUtils.setAutoLoop(bannerData, classBanner);
	}

	//停止自动翻页(广告轮播)
	@Override
	public void onPause() {
		super.onPause();
		CommonUtils.stopLoop(classBanner);
	}
}
