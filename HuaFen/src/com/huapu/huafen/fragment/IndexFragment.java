package com.huapu.huafen.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.MainActivity;
import com.huapu.huafen.activity.MontageActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReleaseActivity;
import com.huapu.huafen.activity.SearchActivity;
import com.huapu.huafen.activity.ServerActivity;
import com.huapu.huafen.adapter.GalleryAdapter;
import com.huapu.huafen.adapter.MyFragmentPagerAdapter;
import com.huapu.huafen.animation.OfAnimation;
import com.huapu.huafen.animation.ViewUpSearch;
import com.huapu.huafen.banner.AccordionTransformer;
import com.huapu.huafen.banner.ClassBannerHolder;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.HomeResult;
import com.huapu.huafen.beans.HomeVipResult;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.scrollablelayoutlib.PagerSlidingTabStrip;
import com.huapu.huafen.scrollablelayoutlib.ScrollableLayout;
import com.huapu.huafen.scrollablelayoutlib.ScrollableLayout.OnScrollListener;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Config;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.ClassBannerView;
import com.huapu.huafen.views.CoverFlowView;
import com.huapu.huafen.views.HomeTitleBar;
import com.huapu.huafen.views.IconRecycleView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
import com.huapu.huafen.views.TitleRecycleView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static com.huapu.huafen.amzing.AmazingAdapter.TAG;

/**
 * @author chen_hao
 * @ClassName: IndexFragment
 * @Description: 首页
 * @date 2016-05-26
 */
public class IndexFragment extends BaseFragment implements OnClickListener {

    private View view;
    private FrameLayout flBanner;
    
    public ViewPager vpGoods;
    private View layoutHeader;
    //一块滚动的布局(下拉除外)
    public ScrollableLayout mScrollLayout;
    //推荐商品的Fragment
    public RecommendListFragment recommendFragment;
    // public NearListFragment nearListFragment;
    public FollowGoodsFragment followGoodsFragment;
    //下拉刷新
    public PtrFrameLayout mPtrFrame;

    //广告轮播翻转效果(任取其中一种)
    private ArrayList<String> transformerList = new ArrayList<>();

    private TextView tvRecommend, tvNearby, tvFollow;
    private View commendLine, nearbyLine, followLine;

    private ViewUpSearch search;
    private boolean isSearchExpand = true;
    public int pageType = 0;

    private TextView tvIndex;
    private TextView tvCount;
    private LinearLayout llIndex;

    //广告轮播
    private ConvenientBanner homeBanner1;
    private ArrayList<CampaignBanner> banners1 = new ArrayList<CampaignBanner>();
    //广告轮播
    private ClassBannerView homeBanner2;
    private ClassBannerView homeBanner1_5;
    private ArrayList<CampaignBanner> banners2 = new ArrayList<CampaignBanner>();
    private ArrayList<CampaignBanner> banners1_5 = new ArrayList<CampaignBanner>();

    private RecyclerView recyclerGallery;
    private ArrayList<CampaignBanner> showcases;
    private BannerData bannerData1;
    private BannerData bannerData2;
    private BannerData bannerData1_5;

    private ImageView indexBall;
    private ImageView devBall;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    private IconRecycleView oneyuan;
    private IconRecycleView star;

    private TitleRecycleView vip;
    private TitleRecycleView subtle;
    private CoverFlowView poems;    // 花语广场

    private HashMap<String, String> params = new HashMap<>();

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_index, container, false);

        return view;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initBanners();
    }

    private void showIndexBall() {
        indexBall.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(indexBall, View.TRANSLATION_Y, CommonUtils.getScreenHeight(), 0.0f);
        animator.setDuration(500);
        animator.start();
    }

    private void hideIndexBall() {
        indexBall.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(indexBall, View.TRANSLATION_Y, 0.0f, CommonUtils.getScreenHeight());
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

    private void configDebug() {

        devBall = (ImageView) view.findViewById(R.id.devBall);

        if (Config.DEBUG) {
            devBall.setVisibility(View.VISIBLE);
        } else {
            devBall.setVisibility(View.GONE);
        }

        devBall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), ServerActivity.class));
            }
        });

        devBall.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getContext(), ReleaseActivity.class);
                intent.putExtra(MyConstants.EXTRA_ALBUM_FROM_MAIN, "1");
                intent.putExtra(MyConstants.IS_AUCTION, 1);
                intent.putExtra(MyConstants.DRAFT_TYPE, 4);
                startActivity(intent);
                return false;
            }
        });
    }

    private void initBanners() {

        requestHomeList("");

        bannerData1 = CommonPreference.getHomeBanners1();
        if (bannerData1 != null && !ArrayUtil.isEmpty(bannerData1.getBanners())) {
            banners1 = bannerData1.getBanners();
            flBanner.setVisibility(View.VISIBLE);
            if (banners1.size() > 1) {
                llIndex.setVisibility(View.VISIBLE);
                tvCount.setText(" of " + banners1.size());
            } else {
                llIndex.setVisibility(View.GONE);
            }
            homeBanner1.setPages(new CBViewHolderCreator<ClassBannerHolder>() {
                @Override
                public ClassBannerHolder createHolder() {
                    return new ClassBannerHolder();
                }

            }, banners1);

            homeBanner1.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    CampaignBanner ca = banners1.get(position);
                    ActionUtil.dispatchAction(getContext(), ca.getAction(), ca.getTarget());
                }
            });

            homeBanner1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int position) {
                    tvIndex.setText(String.valueOf(position + 1));
                    float cX = tvIndex.getWidth() / 2.0f;
                    float cY = tvIndex.getHeight() / 2.0f;
                    OfAnimation animation = new OfAnimation(cX, cY, OfAnimation.ROTATE_INCREASE);
                    animation.setDuration(500);
                    tvIndex.startAnimation(animation);
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
            CommonUtils.setAutoLoop(bannerData1, homeBanner1);
        } else {
            flBanner.setVisibility(View.GONE);
        }

        showcases = CommonPreference.getShowcases();
        if (!ArrayUtil.isEmpty(showcases)) {
            GridLayoutManager GridLayoutManager = new GridLayoutManager(getContext(), 2);
            //GridLayoutManager.setOrientation(GridLayoutManager);
            recyclerGallery.setLayoutManager(GridLayoutManager);
            GalleryAdapter adapter = new GalleryAdapter(getContext());
            recyclerGallery.setAdapter(adapter);
            adapter.setData(showcases);
        } else {
            recyclerGallery.setVisibility(View.GONE);
        }

        bannerData1_5 = CommonPreference.getHomeBanners1_5();
        if (bannerData1_5 != null && !ArrayUtil.isEmpty(bannerData1_5.getBanners())) {
            banners1_5 = bannerData1_5.getBanners();
            homeBanner1_5.setVisibility(View.VISIBLE);
            homeBanner1_5.setBanners(banners1_5);
            CommonUtils.setAutoLoop(bannerData1_5, homeBanner1_5);
        } else {
            homeBanner1_5.setVisibility(View.GONE);
        }

        bannerData2 = CommonPreference.getHomeBanners2();
        if (bannerData2 != null && !ArrayUtil.isEmpty(bannerData2.getBanners())) {
            banners2 = bannerData2.getBanners();
            homeBanner2.setVisibility(View.VISIBLE);
            homeBanner2.setBanners(banners2);
            CommonUtils.setAutoLoop(bannerData2, homeBanner2);
        } else {
            homeBanner2.setVisibility(View.GONE);
        }
    }

    private void initView() {
        configDebug();
        indexBall = (ImageView) view.findViewById(R.id.ivBall);
        indexBall.setOnClickListener(this);
        vpGoods = (ViewPager) view.findViewById(R.id.vpGoods);
        layoutHeader = view.findViewById(R.id.layoutHeader);
        llIndex = (LinearLayout) view.findViewById(R.id.llIndex);
        tvIndex = (TextView) view.findViewById(R.id.tvIndex);
        tvCount = (TextView) view.findViewById(R.id.tvCount);
        flBanner = (FrameLayout) view.findViewById(R.id.flBanner);
        // 头部广告位
        homeBanner1 = (ConvenientBanner) view.findViewById(R.id.banner1);

        FrameLayout.LayoutParams homeBanner1Params = (FrameLayout.LayoutParams) homeBanner1.getLayoutParams();
        homeBanner1Params.height = (int) (CommonUtils.getScreenWidth() / 2.2f);
        /*recyclerGallery = (HRecyclerView)view.findViewById(R.id.recyclerGallery);*/
        recyclerGallery = (RecyclerView) view.findViewById(R.id.recyclerGallery);

        ViewUtil.setOffItemAnimator(recyclerGallery);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerGallery.getLayoutParams();
        params.height = (int) ((CommonUtils.getScreenWidth() / 2) / 2.68f * 2 + CommonUtils.dp2px(1f));
        homeBanner2 = (ClassBannerView) view.findViewById(R.id.banner2);
        LinearLayout.LayoutParams homeBanner2Params = (LinearLayout.LayoutParams) homeBanner2.getLayoutParams();
        homeBanner2Params.height = (int) (CommonUtils.getScreenWidth() / 3.4);

        homeBanner1_5 = (ClassBannerView) view.findViewById(R.id.banner1_5);
        LinearLayout.LayoutParams homeBanner1_5Params = (LinearLayout.LayoutParams) homeBanner1_5.getLayoutParams();
        homeBanner1_5Params.height = (int) (CommonUtils.getScreenWidth() / 6.26f);

        transformerList.add(AccordionTransformer.class.getSimpleName());
        // 整个滚动区域布局，下拉刷新布局除外
        mScrollLayout = (ScrollableLayout) view.findViewById(R.id.scrollableLayout);
        search = (ViewUpSearch) view.findViewById(R.id.search);
        View circle = search.findViewById(R.id.circle);
        circle.setOnClickListener(this);
        View round = search.findViewById(R.id.round);
        round.setOnClickListener(this);

        oneyuan = (IconRecycleView) view.findViewById(R.id.oneyuan);
        star = (IconRecycleView) view.findViewById(R.id.star);
        vip = (TitleRecycleView) view.findViewById(R.id.vip);
        subtle = (TitleRecycleView) view.findViewById(R.id.subtle);
        poems = (CoverFlowView) view.findViewById(R.id.poems);

        // 标题栏上的推荐按钮
        tvRecommend = (TextView) search.findViewById(R.id.tvRecommend);
        tvNearby = (TextView) search.findViewById(R.id.tvNearby);
        tvFollow = (TextView) search.findViewById(R.id.tvFollow);

        tvRecommend.setOnClickListener(this);
        tvNearby.setOnClickListener(this);
        tvFollow.setOnClickListener(this);

        commendLine = search.findViewById(R.id.commendLine);
        nearbyLine = search.findViewById(R.id.nearbyLine);
        followLine = search.findViewById(R.id.followLine);

        search.layoutSearch.setAlpha(0.0f);
        search.llContainer.setVisibility(View.GONE);

        mScrollLayout.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(int currentY, int maxY) {
                if (currentY < maxY - CommonUtils.dp2px(45.0f) && !isSearchExpand) {
                    // 搜索动画展开
                    search.updateShow(true);
                    isSearchExpand = true;
                    hideIndexBall();
                } else if (currentY >= maxY - CommonUtils.dp2px(45.0f) && isSearchExpand) {
                    search.updateShow(false);
                    isSearchExpand = false;
                    showIndexBall();
                }
                if (search != null) {
                    if (currentY < CommonUtils.getHomeBnnerHeight() - CommonUtils.dp2px(45.0f)) {
                        float alpha = ((float) currentY) / (float) (CommonUtils.getHomeBnnerHeight() - CommonUtils.dp2px(45.0f));
                        search.layoutSearch.setAlpha(alpha);
                    } else {
                        search.layoutSearch.setAlpha(1.0f);
                    }
                }
            }
        });

        // 水平滚动的ViewPager
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.pagerStrip);
        recommendFragment = RecommendListFragment.newInstance();
        //nearListFragment = NearListFragment.newInstance();
        followGoodsFragment = FollowGoodsFragment.newInstance();
        initFragmentPager(vpGoods, mScrollLayout);

        // 下拉刷新
        setupPullToRefresh();
        recommendFragment.setOnRecommendPullListener(new RecommendListFragment.OnRecommendPullListener() {

            @Override
            public void onRecommendPull() {
                if (mPtrFrame != null) {
                    mPtrFrame.refreshComplete();
                }
            }
        });

		/*nearListFragment.setOnRecommendPullListener(new NearListFragment.OnRecommendPullListener() {
            @Override
			public void onRecommendPull() {
				if (mPtrFrame != null) {
					mPtrFrame.refreshComplete();
				}
			}
		});*/

        followGoodsFragment.setOnFollowGoodsPullListener(new FollowGoodsFragment.OnFollowGoodsPullListener() {
            @Override
            public void onRecommendPull() {
                if (mPtrFrame != null) {
                    mPtrFrame.refreshComplete();
                }
            }
        });
    }

    public void initFragmentPager(final ViewPager viewPager, final ScrollableLayout mScrollLayout) {
        final ArrayList<ScrollAbleFragment> fragmentList = new ArrayList<ScrollAbleFragment>();
        fragmentList.add(recommendFragment);
        //fragmentList.add(nearListFragment);
        fragmentList.add(followGoodsFragment);

        List<String> titleList = new ArrayList<String>();
        titleList.add("推荐");
        //titleList.add("附近");
        titleList.add("关注");

        viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList, titleList));
        viewPager.setOffscreenPageLimit(titleList.size());
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
                // if (i == 2 && !CommonPreference.isLogin()) {//如果选择了关注，且没登录  //-------------------------------------------------
                if (i == 1 && !CommonPreference.isLogin()) {//如果选择了关注，且没登录
                    ActionUtil.loginAndToast(getActivity());
                    pagerSlidingTabStrip.setCurrentTab(pageType);
                    return;
                }

                pageType = i;
                /** 标注当前页面 **/
                mScrollLayout.getHelper().setCurrentScrollableContainer(fragmentList.get(i));
                if (i == 0) {
                    tvNearby.setTextColor(getResources().getColor(R.color.white_50));
                    tvFollow.setTextColor(getResources().getColor(R.color.white_50));
                    nearbyLine.setVisibility(View.INVISIBLE);
                    followLine.setVisibility(View.INVISIBLE);

                    tvRecommend.setTextColor(getResources().getColor(R.color.white));
                    commendLine.setVisibility(View.VISIBLE);
                    if (isSearchExpand) {
                        recommendFragment.listViewToTop();
                    }
                }/*else if(i ==1){
                    tvRecommend.setTextColor(getResources().getColor(R.color.white_50));
					tvFollow.setTextColor(getResources().getColor(R.color.white_50));
					commendLine.setVisibility(View.INVISIBLE);
					followLine.setVisibility(View.INVISIBLE);

					tvNearby.setTextColor(getResources().getColor(R.color.white));
					nearbyLine.setVisibility(View.VISIBLE);
					if(isSearchExpand) {
						nearListFragment.listViewToTop();
					}
				}*/ else if (i == 1) {
                    tvRecommend.setTextColor(getResources().getColor(R.color.white_50));
                    tvNearby.setTextColor(getResources().getColor(R.color.white_50));
                    commendLine.setVisibility(View.INVISIBLE);
                    nearbyLine.setVisibility(View.INVISIBLE);

                    tvFollow.setTextColor(getResources().getColor(R.color.white));
                    followLine.setVisibility(View.VISIBLE);
                    if (isSearchExpand) {
                        followGoodsFragment.listViewToTop();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        pagerSlidingTabStrip.setOnTabClickListener(new PagerSlidingTabStrip.OnTabClickListener() {

            @Override
            public boolean onTabClick(int position) {
                boolean flag = position == 2 && !CommonPreference.isLogin();
                if (flag) {
                    ActionUtil.loginAndToast(getActivity());
                }
                return flag;
            }
        });
        viewPager.setCurrentItem(0);
    }

    public void setupPullToRefresh() {
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_web_view_frame);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mScrollLayout.canPtr();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                initBanners();
                if (pageType == 0) {
                    recommendFragment.startRequestForGetRecommendData();
                }/* else if(pageType == 1){
                    nearListFragment.startRequestForGetRecommendData();
				}*/ else if (pageType == 1) {
                    followGoodsFragment.startRequestForGetRecommendData();
                }
            }
        });

        ViewUtil.setPtrFrameLayout(mPtrFrame);
        //mPtrFrame.setHeaderView(header);
        //mPtrFrame.addPtrUIHandler(header);
        ((PtrAnimationBackgroundHeader) mPtrFrame.getHeaderView()).setHandler(new PtrAnimationBackgroundHeader.IHandler() {

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
                if (search != null) {
                    search.setVisibility(View.GONE);
                }
            }

            @Override
            public void onUIReset(PtrFrameLayout frame) {
                if (search != null) {
                    search.setVisibility(View.VISIBLE);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(search, View.ALPHA, 0.0F, 1.0F);
                    animator.setDuration(500);
                    animator.start();
                }
            }
        });
    }

    private void requestHomeList(final String extra) {

        LogUtil.d("danielluan", "params" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.HOME_LIST, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(getActivity(), "请检查网络连接");
                LogUtil.e(TAG, "classificationDetail error:" + e.toString());
//                if (extra.equals(LOADING)) {
//                    //loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
//                } else if (extra.equals(REFRESH)) {
//                    //mPtrFrame.refreshComplete();
//                } else if (extra.equals(LOAD_MORE)) {
//
//                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "classificationDetail response:" + response);
                // 调用刷新完成
//                if (extra.equals(LOADING)) {
//                    //loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
//                } else if (extra.equals(REFRESH)) {
//                    mPtrFrame.refreshComplete();
//                } else if (extra.equals(LOAD_MORE)) {
//
//                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {

                            LogUtil.d("danielluanl", "baseResult" + baseResult.obj.toString());
                            updateData(baseResult);
                        }
                    } else {
                        CommonUtils.error(baseResult, getActivity(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateData(BaseResult result) {
        HomeResult homeresult = JSON.parseObject(result.obj, HomeResult.class);
        updateOneYuan(homeresult);
        updateStar(homeresult);
        updateVip(homeresult);
        updatePoems(homeresult);
        updateSubtle(homeresult);

    }

    private void updateOneYuan(HomeResult homeresult) {


        Uri uri = Uri.parse("res://" + getActivity().getPackageName() +
                "/" + R.drawable.main_page_oneyuan);
        oneyuan.setLefeImage(uri.toString());
        oneyuan.setListenner(new IconRecycleView.LeftImageOnClickListenner() {
            @Override
            public void OnImageClick(SimpleDraweeView sdv) {

                ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_ONE_REGION, "");

            }
        });
        IconRecycleView.OneYuanAdapter adapter = oneyuan.new OneYuanAdapter(getActivity());

        if (homeresult.oneYuan != null && homeresult.oneYuan.size() > 0) {

            adapter.setListenner(new IconRecycleView.ItemOnClickListenner() {
                @Override
                public void OnItemClick(Item item) {
                    ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_GOODS_DETAIL, item.item.goodsId + "");
                }
            });
            adapter.setData(homeresult.oneYuan);
            oneyuan.setAdapter(adapter.getWrapperAdapter());
            oneyuan.setVisibility(View.VISIBLE);
        } else {
            oneyuan.setVisibility(View.GONE);
        }
    }

    private void updateVip(HomeResult homeresult) {

        vip.setTitleimage(R.drawable.main_page_vip);
        vip.setIndicator(R.drawable.purple_more);
        vip.setListenner(new TitleRecycleView.TitleClickListenner() {
            @Override
            public void OnTitleClick(Object o) {
                ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_VIP_REGION, "");
            }
        });
        TitleRecycleView.TitleAdapter adapter = vip.new TitleAdapter(getActivity());
        if (homeresult.vip != null && homeresult.vip.size() > 0) {
            adapter.setData(homeresult.vip);
            adapter.setOnclickListener(new TitleRecycleView.ItemOnclickListener() {
                @Override
                public void OnItemClick(HomeResult.CatData data) {
                    requestHomeVip(data.cid);
                }
            });
            vip.setTitleAdapter(adapter);
            HomeResult.CatData cd = homeresult.vip.get(0);
            cd.select = true;
            requestHomeVip(cd.cid);
            vip.setVisibility(View.VISIBLE);
        } else {
            vip.setVisibility(View.GONE);
        }
    }

    private void updateVipHome(BaseResult result) {
        HomeVipResult homeVipResult = JSON.parseObject(result.obj, HomeVipResult.class);

        if (homeVipResult.list != null) {
            TitleRecycleView.VipAdapter adapter = vip.new VipAdapter(getActivity());
            if (homeVipResult.list != null) {
                adapter.setData(homeVipResult.list);
                adapter.setListener(new TitleRecycleView.ItemClickListener() {
                    @Override
                    public void OnItemClick(Item item) {
                        ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_GOODS_DETAIL, item.item.goodsId + "");
                    }

                    @Override
                    public void OnItemClick(Object item) {

                    }
                });
                vip.setContentAdapter(adapter.getWrapperAdapter());
            }
        }
    }

    private void updateSubtle(HomeResult homeresult) {

        subtle.setTitleimage(R.drawable.main_page_subtle);
        subtle.setIndicator(R.drawable.blue_more);
        subtle.hideTag();
        TitleRecycleView.SubtleAdapter adapter = subtle.new SubtleAdapter(getActivity());
        if (homeresult.vols != null && homeresult.vols.size() > 0) {

            if (homeresult.vols.size() <= 4) {
                subtle.hideMore();
            } else {
                subtle.setListenner(new TitleRecycleView.TitleClickListenner() {
                    @Override
                    public void OnTitleClick(Object o) {
                        String target = "";
                        if (Config.DEBUG) {
                            target = "https://i-t.huafer.cc/s";
                        } else {
                            target = "https://i.huafer.cc/s";
                        }
                        ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_WEB_VIEW, target);

                    }
                });
            }
            adapter.setData(homeresult.vols);
            adapter.setListener(new TitleRecycleView.ItemClickListener() {

                @Override
                public void OnItemClick(Item item) {

                }

                @Override
                public void OnItemClick(Object item) {
                    HomeResult.ActionData actiondata = (HomeResult.ActionData) item;
                    ActionUtil.dispatchAction(getContext(), actiondata.action, actiondata.target);

                }
            });
            subtle.setContentAdapter(adapter);
            subtle.setVisibility(View.VISIBLE);
        } else {
            subtle.setVisibility(View.GONE);
        }
    }

    private void updatePoems(HomeResult homeresult) {

        poems.setTitleimage(R.drawable.main_page_huayu);
        poems.setIndicator(R.drawable.pink_more);
        poems.setListenner(new HomeTitleBar.TitleClickListenner() {
            @Override
            public void OnTitleClick(Object o) {
                ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_ARTICLE_SQUARE, "");
            }
        });

        CoverFlowView.CoverPageAdapter cp = poems.new CoverPageAdapter();
        if (homeresult.poems != null) {

            if (homeresult.poems.size() > 0) {
                cp.setData(homeresult.poems);
                cp.setListener(new CoverFlowView.PageClickListener() {
                    @Override
                    public void pageOnclick(Object o) {
                        HomeResult.ActionData actionData = (HomeResult.ActionData) o;
                        ActionUtil.dispatchAction(getContext(), actionData.action, actionData.target);
                    }
                });
                poems.setPageAdapter(cp);
                poems.setCurentItem(cp.pointer);
                poems.setVisibility(View.VISIBLE);
            } else {
                poems.setVisibility(View.GONE);
            }
        }
    }

    private void updateStar(HomeResult homeresult) {

        Uri uri = Uri.parse("res://" + getActivity().getPackageName() +
                "/" + R.drawable.main_page_star);
        star.setLefeImage(uri.toString());

        star.setListenner(new IconRecycleView.LeftImageOnClickListenner() {
            @Override
            public void OnImageClick(SimpleDraweeView sdv) {
                ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_STAR_USER_LIST, "");
            }
        });
        IconRecycleView.StarAdapter adapter = star.new StarAdapter(getActivity());

        if (homeresult.stars != null && homeresult.stars.size() > 0) {
            adapter.setData(homeresult.stars);
            adapter.setListenner(new IconRecycleView.ItemOnClickListenner() {
                @Override
                public void OnItemClick(Item item) {

                    long uid = CommonPreference.getUserId();
                    if (item != null) {
                        UserData userInfo = item.user;
                        if (userInfo != null) {
                            uid = userInfo.getUserId();
                        }
                    }
                    Intent intent = new Intent(getActivity(), PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, uid);
                    startActivity(intent);

                }
            });
            star.setAdapter(adapter.getWrapperAdapter());
            star.setVisibility(View.VISIBLE);
        } else {
            star.setVisibility(View.GONE);
        }
    }

    private void requestHomeVip(final String cat) {

        params.put("cat", cat);
        LogUtil.d("danielluan", "params" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.HOME_VIP, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(getActivity(), "请检查网络连接");
                LogUtil.e(TAG, "classificationDetail error:" + e.toString());
//                if (extra.equals(LOADING)) {
//                    //loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
//                } else if (extra.equals(REFRESH)) {
//                    //mPtrFrame.refreshComplete();
//                } else if (extra.equals(LOAD_MORE)) {
//
//                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "classificationDetail response:" + response);
                // 调用刷新完成
//                if (extra.equals(LOADING)) {
//                    //loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
//                } else if (extra.equals(REFRESH)) {
//                    mPtrFrame.refreshComplete();
//                } else if (extra.equals(LOAD_MORE)) {
//
//                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            //updateData(baseResult, extra);
                            updateVipHome(baseResult);

                        }
                    } else {
                        CommonUtils.error(baseResult, getActivity(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.round:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.circle:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;

            case R.id.tvRecommend:
                LogUtil.i("liang", "点击推荐");
                vpGoods.setCurrentItem(0);
                pageType = 0;

                tvNearby.setTextColor(getResources().getColor(R.color.white_50));
                tvFollow.setTextColor(getResources().getColor(R.color.white_50));
                nearbyLine.setVisibility(View.INVISIBLE);
                followLine.setVisibility(View.INVISIBLE);

                tvRecommend.setTextColor(getResources().getColor(R.color.white));
                commendLine.setVisibility(View.VISIBLE);
                break;
        /*case R.id.tvNearby:
            LogUtil.i("liang", "点击附近");
			vpGoods.setCurrentItem(1);
			pageType = 1;
			tvRecommend.setTextColor(getResources().getColor(R.color.white_50));
			tvFollow.setTextColor(getResources().getColor(R.color.white_50));
			commendLine.setVisibility(View.INVISIBLE);
			followLine.setVisibility(View.INVISIBLE);
			tvNearby.setTextColor(getResources().getColor(R.color.white));
			nearbyLine.setVisibility(View.VISIBLE);
			break;*/

            case R.id.tvFollow:
                LogUtil.i("liang", "点击关注");
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                vpGoods.setCurrentItem(1);
                pageType = 1;
                tvRecommend.setTextColor(getResources().getColor(R.color.white_50));
                tvNearby.setTextColor(getResources().getColor(R.color.white_50));
                commendLine.setVisibility(View.INVISIBLE);
                nearbyLine.setVisibility(View.INVISIBLE);

                tvFollow.setTextColor(getResources().getColor(R.color.white));
                followLine.setVisibility(View.VISIBLE);
                break;
            case R.id.ivBall:
                ((MainActivity) getActivity()).tabHome.performClick();
                break;
            default:
                break;
        }
    }

    // 开始自动翻页(广告轮播)
    @Override
    public void onResume() {
        super.onResume();

        CommonUtils.setAutoLoop(bannerData1, homeBanner1);
        CommonUtils.setAutoLoop(bannerData2, homeBanner2);
        if (CommonPreference.getIntValue(MyConstants.MAIN_GUIDE_TIPS, 0) == 0) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(getActivity(), MontageActivity.class);
                    intent.putExtra(MyConstants.EXTRA_MONTAGE, MyConstants.MAIN_GUIDE_TIPS);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);

                }
            }, 1000);
        }
    }

    //停止自动翻页(广告轮播)
    @Override
    public void onPause() {
        super.onPause();
        CommonUtils.stopLoop(homeBanner1);
        CommonUtils.stopLoop(homeBanner2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
