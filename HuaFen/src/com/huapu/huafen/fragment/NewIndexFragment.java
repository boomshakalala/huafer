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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
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
import com.huapu.huafen.animation.ViewUpSearch;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.pages.HomeRefreshBean;
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
import com.huapu.huafen.views.IconRecycleView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
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
public class NewIndexFragment extends BaseFragment implements OnClickListener {

    public int pageType = 0;

    private View view;
    //一块滚动的布局(下拉除外)

    //下拉刷新
    public PtrFrameLayout mPtrFrame;

    public ScrollableLayout mScrollLayout;

    //推荐、关注
    public ViewPager vpGoods;
    PagerSlidingTabStrip pagerSlidingTabStrip;
    //推荐商品的Fragment
    public RecommendListFragment recommendFragment;
    public FollowGoodsFragment followGoodsFragment;

    //search
    private TextView tvRecommend, tvNearby, tvFollow;
    private View commendLine, nearbyLine, followLine;
    private ViewUpSearch search;
    private boolean isSearchExpand = true;


    //homeBanner1
    private ConvenientBanner homeBanner1;
    private BannerData bannerData1;

    //六大专区
    private RecyclerView recyclerGallery;

    //明星也在玩
    private IconRecycleView star;

    //vip精品
    private ConvenientBanner homeBanner2;
    private BannerData bannerData2;
    private IconRecycleView vip;

    //花语广场
    private ConvenientBanner homeBanner3;
    private BannerData bannerData3;
    private IconRecycleView poems;

    //快速置顶
    private ImageView indexBall;
    //开发选项
    private ImageView devBall;

    //请求参数
    private HashMap<String, String> params = new HashMap<>();

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_index, container, false);
        return view;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
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

    private void initView() {
        configDebug();

        setupPullToRefresh();

        mScrollLayout = (ScrollableLayout) view.findViewById(R.id.scrollableLayout);
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

        vpGoods = (ViewPager) view.findViewById(R.id.vpGoods);
        pagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.pagerStrip);
        recommendFragment = RecommendListFragment.newInstance();
        followGoodsFragment = FollowGoodsFragment.newInstance();
        recommendFragment.setOnRecommendPullListener(new RecommendListFragment.OnRecommendPullListener() {

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
        initFragmentPager(vpGoods, mScrollLayout);

        search = (ViewUpSearch) view.findViewById(R.id.search);
        View circle = search.findViewById(R.id.circle);
        circle.setOnClickListener(this);
        View round = search.findViewById(R.id.round);
        round.setOnClickListener(this);
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


        homeBanner1 = (ConvenientBanner) view.findViewById(R.id.banner1);
        homeBanner1.setPageIndicator(new int[]{R.drawable.shape_circle_white,R.drawable.shape_circle_pink});
        homeBanner1.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);

        recyclerGallery = (RecyclerView) view.findViewById(R.id.recyclerGallery);
        ViewUtil.setOffItemAnimator(recyclerGallery);

        star = (IconRecycleView) view.findViewById(R.id.star);

        homeBanner2 = (ConvenientBanner) view.findViewById(R.id.banner2);
        LinearLayout.LayoutParams homeBanner2Params = (LinearLayout.LayoutParams) homeBanner2.getLayoutParams();
        homeBanner2Params.height = (int) (CommonUtils.getScreenWidth() / 3.4);
        vip = (IconRecycleView) view.findViewById(R.id.vip);

        homeBanner3 = (ConvenientBanner) view.findViewById(R.id.banner3);
        LinearLayout.LayoutParams homeBanner3Params = (LinearLayout.LayoutParams) homeBanner3.getLayoutParams();
        homeBanner3Params.height = (int) (CommonUtils.getScreenWidth() / 3.4);



        indexBall = (ImageView) view.findViewById(R.id.ivBall);
        indexBall.setOnClickListener(this);
        poems = (IconRecycleView) view.findViewById(R.id.poems);
        requestHomeList("");

    }


    public void initFragmentPager(final ViewPager viewPager, final ScrollableLayout mScrollLayout) {
        final ArrayList<ScrollAbleFragment> fragmentList = new ArrayList<ScrollAbleFragment>();
        fragmentList.add(recommendFragment);
        fragmentList.add(followGoodsFragment);

        List<String> titleList = new ArrayList<String>();
        titleList.add("推荐");
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
                } else if (i == 1) {
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
                if (pageType == 0) {
                    recommendFragment.startRequestForGetRecommendData();
                } else if (pageType == 1) {
                    followGoodsFragment.startRequestForGetRecommendData();
                }
            }
        });

        ViewUtil.setPtrFrameLayout(mPtrFrame);
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
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "classificationDetail response:" + response);
                // 调用刷新完成
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
        HomeRefreshBean homeresult = JSON.parseObject(result.obj, HomeRefreshBean.class);
        updateBanners(homeresult);
        updateShowcases(homeresult);
        updateStar(homeresult);
        updateVip(homeresult);
        updatePoems(homeresult);

    }

    private void updateShowcases(HomeRefreshBean homeresult) {

        ArrayList<CampaignBanner> showcases = homeresult.getShowcases();
        if (!ArrayUtil.isEmpty(showcases)) {
            GridLayoutManager GridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerGallery.setLayoutManager(GridLayoutManager);
            GalleryAdapter adapter = new GalleryAdapter(getContext());
            recyclerGallery.setAdapter(adapter);
            adapter.setData(showcases);
        } else {
            recyclerGallery.setVisibility(View.GONE);
        }
    }

    private void updateBanners(HomeRefreshBean result) {
        bannerData1 = result.getHomeBanner1();
        bannerData2 = result.getHomeBanner2();
        bannerData3 = result.getHomeBanner3();
        ViewUtil.updateBanner(homeBanner1,bannerData1);
        ViewUtil.updateBanner(homeBanner2,bannerData2);
        ViewUtil.updateBanner(homeBanner3,bannerData3);
    }


    private void updateVip(HomeRefreshBean homeresult) {
        Uri uri = Uri.parse("res://" + getActivity().getPackageName() +
                "/" + R.drawable.main_page_vip);
        vip.setLefeImage(uri.toString());

        vip.setListenner(new IconRecycleView.LeftImageOnClickListenner() {
            @Override
            public void OnImageClick(SimpleDraweeView sdv) {
                ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_VIP_REGION, "");
        }
        });
        IconRecycleView.VipAdapter adapter = vip.new VipAdapter(getActivity());

        if (homeresult.getVip() != null && homeresult.getVip().size()>0) {
            adapter.setData(homeresult.getVip());
            vip.setAdapter(adapter.getWrapperAdapter());
            vip.setVisibility(View.VISIBLE);
        } else {
            vip.setVisibility(View.GONE);
        }



    }



    private void updatePoems(HomeRefreshBean homeresult) {
        Uri uri = Uri.parse("res://" + getActivity().getPackageName() +
                "/" + R.drawable.main_page_poems);
        poems.setLefeImage(uri.toString());

        poems.setListenner(new IconRecycleView.LeftImageOnClickListenner() {
            @Override
            public void OnImageClick(SimpleDraweeView sdv) {
                ActionUtil.dispatchAction(getContext(), ActionConstants.OPEN_ARTICLE_SQUARE, "");
            }
        });
        IconRecycleView.PoemAdapter adapter = poems.new PoemAdapter(getActivity());

        if (homeresult.getPoems() != null && homeresult.getPoems().size()>0) {
            adapter.setData(homeresult.getPoems());
            poems.setAdapter(adapter.getWrapperAdapter());
            poems.setVisibility(View.VISIBLE);
        } else {
            poems.setVisibility(View.GONE);
        }

    }

    private void updateStar(HomeRefreshBean homeresult) {

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

        if (homeresult.getStars() != null && homeresult.getStars().size() > 0) {
            adapter.setData(homeresult.getStars());
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

        CommonUtils.setAutoLoop(bannerData1,homeBanner1);
        CommonUtils.setAutoLoop(bannerData2,homeBanner2);
        CommonUtils.setAutoLoop(bannerData2,homeBanner2);

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
