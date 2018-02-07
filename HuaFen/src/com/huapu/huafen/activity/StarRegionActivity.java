package com.huapu.huafen.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommodityAdapter;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.CampaignData;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.ClassBannerView;
import com.huapu.huafen.views.ClassFilterView;
import com.huapu.huafen.views.FilterSelectView;
import com.huapu.huafen.views.FilterSortView;
import com.huapu.huafen.views.FilterView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.huapu.huafen.views.RecommendedUserView;
import com.huapu.huafen.views.SelectButton;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/12/6.
 */
public class StarRegionActivity extends BaseActivity
                                                        implements
                                                        OnItemClickListener,
                                                        PopupWindow.OnDismissListener,
                                                        FilterSelectView.OnCheckedChangedListener,
                                                        LoadMoreWrapper.OnLoadMoreListener {

    private static final long ANIMATION_DURATION = 10L;
    private PullToRefreshRecyclerView ptrRecycler;
    private int page;
    private ArrayList<CampaignData> goodsList = new ArrayList<CampaignData>();
    private String mRequestUrl;
    //广告轮播
    private ClassBannerView bannerView;
    private View header;
    private View layoutBanner;
    private View filterLayout;
    private FilterSelectView filterSelectViewHeader;
    private FilterSelectView filterSelectView;
    private HashMap<String, String> params = new HashMap<String, String>();
    private CommodityAdapter listAdapter;
    private View footerView;
    private View headerFilterView;
    private LinearLayout llFilterLayout;
    private FrameLayout flContainer;

    private ClassFilterView classFilterView;//分类
    private FilterSortView filterSortView;//排序
    private FilterView filterView;//筛选
    private View blankSpace;
    private ImageView indexBall;
    private boolean isTopVisible;

    private void initFilters(){
        llFilterLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flContainer = (FrameLayout) findViewById(R.id.flContainer);

        classFilterView = (ClassFilterView) findViewById(R.id.classFilterView);
        filterSortView = (FilterSortView) findViewById(R.id.filterSortView);
        filterView = (FilterView) findViewById(R.id.filterView);
        filterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        blankSpace = findViewById(R.id.blankSpace);

        //分类

        classFilterView.setOnItemDataSelect(new ClassFilterView.OnItemDataSelect() {

            @Override
            public void onDataSelected(int[] catId, String text) {
                params.put("cat1",catId[0]+"");
                params.put("cat2",catId[1]+"");
                setSelectTitle(text,0);
                hideAnimation(true);
            }
        });

        ArrayList<Cat> cats = CommonPreference.getCats();
        if(cats!=null){
            Cat cat = new Cat();
            cat.setName("全部分类");
            cat.setCid(0);
            cats.add(0,cat);
        }

        classFilterView.setData(cats);

        //排序

        filterSortView.setOnItemDataSelect(new FilterSortView.OnItemDataSelect() {

            @Override
            public void onDataSelected(SortEntity data) {
                params.put("orderBy",data.id+"");
                setSelectTitle(data.name,1);
                hideAnimation(true);
            }
        });

        //筛选
        filterView.setOnConfirmButtonClick(new FilterView.OnConfirmButtonClick() {

            @Override
            public void onClick(Map<String, String> paramsMap) {
                LogUtil.e("OnFilterConfirm",paramsMap);
                params.putAll(paramsMap);
                hideAnimation(true);
            }
        });

        blankSpace.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_region_layout);
        Intent intent = getIntent();
        if(intent.hasExtra(MyConstants.CAMPAIGN_URL)){
            mRequestUrl = intent.getStringExtra(MyConstants.CAMPAIGN_URL);
        }

        initViews();
        initBanner();
        startRequestForRecommendList();
    }

    // 开始自动翻页(广告轮播)
    @Override
    public void onResume() {
        super.onResume();
        CommonUtils.setAutoLoop(starGoodsBanner,bannerView);
    }

    //停止自动翻页(广告轮播)
    @Override
    public void onPause() {
        super.onPause();
        CommonUtils.stopLoop(bannerView);
    }


    @Override
    public void onTitleBarDoubleOnClick() {
        if(ptrRecycler != null) {
            ptrRecycler.getRefreshableView().scrollToPosition(0);
        }
    }

    private void initViews() {

        setTitleString("明星专区");
        filterLayout = findViewById(R.id.filterLayout);
        //title下方的筛选bar
        filterSelectView = (FilterSelectView)findViewById(R.id.filterSelectView);
        filterSelectView.setOnCheckedChangedListener(this);

        ptrRecycler = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(manager);
        listAdapter = new CommodityAdapter(StarRegionActivity.this);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecycler.getRefreshableView(),false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_star_region);
        listAdapter.setEmptyView(viewEmpty);

        ptrRecycler.setAdapter(listAdapter.getWrapperAdapter());

        ptrRecycler.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {

            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refresh();
            }
        });

        footerView =LayoutInflater.from(this).inflate(R.layout.load_layout, ptrRecycler.getRefreshableView(), false);

        footerView.setBackgroundColor(Color.parseColor("#FFFFFF"));

        listAdapter.setOnLoadMoreListener(this);

        initFilters();
        indexBall = (ImageView) findViewById(R.id.ivBall);
        indexBall.setOnClickListener(this);
    }

    private void refresh() {
        page = 0;
        startRequestCampaigns(REFRESH);
    }

    private void startRequestForRecommendList() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        HashMap<String, String> param = new HashMap<String, String>();
        param.put("userLevel", "3");
        param.put("pageNum", "10");
        LogUtil.i("lalo", "params:" + param.toString());
        OkHttpClientManager.postAsyn(MyConstants.RECOMMEND_USER_LIST, param, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                initHeaders(null);
            }


            @Override
            public void onResponse(String response) {
                UserListResult result = null;
                try {
                    LogUtil.e("lalo", "campaign onResponse:" + response.toString());
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            result = JSON.parseObject(baseResult.obj, UserListResult.class);

                        }
                    } else {
                        CommonUtils.error(baseResult, StarRegionActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                initHeaders(result);
            }
        });
    }

    private void initHeaders(UserListResult result) {
        if(result!=null&&!ArrayUtil.isEmpty(result.getUsers())){
            View recommendHeader = LayoutInflater.from(this).inflate(R.layout.recommend_user_header, ptrRecycler.getRefreshableView(), false);
            RecommendedUserView recommendedUserView = (RecommendedUserView) recommendHeader.findViewById(R.id.recommendedUserView);
            recommendedUserView.setData(result.getUsers());
            recommendedUserView.setType(RecommendedUserView.TYPE.STAR);
            listAdapter.addHeaderView(recommendHeader);
        }
        initFilterViews();

        int headerCounts = listAdapter.getHeadersCount();
        if(headerCounts==0){
            filterLayout.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ptrRecycler.getLayoutParams();
            layoutParams.topMargin = CommonUtils.dp2px(41);
        }else{
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ptrRecycler.getLayoutParams();
            layoutParams.topMargin = 0;
        }

        ptrRecycler.getRefreshableView().setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int headerCounts = listAdapter.getHeadersCount();
                RecyclerView.LayoutManager manager = ptrRecycler.getRefreshableView().getLayoutManager();
                if(manager instanceof LinearLayoutManager){
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) manager;
                    int index = linearLayoutManager.findFirstVisibleItemPosition();
                    if(headerCounts>0){
                        if(index >= headerCounts-1){
                            filterLayout.setVisibility(View.VISIBLE);
                            isTopVisible = true;
                            showIndexBall();
                        }else{
                            filterLayout.setVisibility(View.GONE);
                            isTopVisible = false;
                            hideIndexBall();
                        }
                    }else{
                        if(index >0){
                            isTopVisible = true;
                            showIndexBall();
                        }else{
                            isTopVisible = false;
                            hideIndexBall();
                        }
                    }


                }
            }
        });

        startRequestCampaigns(REFRESH);
    }


    //初始化筛选bar
    private void initFilterViews(){
        //RecyclerView上方的筛选bar（HeaderView）
        if(listAdapter.getHeadersCount()!=0){
            headerFilterView = LayoutInflater.from(this).inflate(R.layout.layout_filter_star, ptrRecycler.getRefreshableView(), false);
            filterSelectViewHeader = (FilterSelectView)headerFilterView.findViewById(R.id.filterSelectView);
            filterSelectViewHeader.setOnCheckedChangedListener(new FilterSelectView.OnCheckedChangedListener() {

                @Override
                public void onChecked(FilterSelectView v, final SelectButton button) {

                    int pos = ptrRecycler.getRefreshableView().getChildAdapterPosition(headerFilterView);

                    smoothMoveToPosition(pos);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            filterLayout.setVisibility(View.VISIBLE);
                            filterSelectView.findButtonByIndex(button.index).performClick();
                        }
                    },488);
                }
            });

            listAdapter.addHeaderView(headerFilterView);
        }

    }

    private void smoothMoveToPosition(int position) {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) ptrRecycler.getRefreshableView().getLayoutManager();
        int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
        if(position <= lastPosition){
            int top = linearLayoutManager.getChildAt(position - firstPosition).getTop();
            ptrRecycler.getRefreshableView().smoothScrollBy(0, top);
        }
    }

    private void showIndexBall(){
        if(indexBall.getVisibility() == View.GONE){
            indexBall.setVisibility(View.VISIBLE);
            ObjectAnimator animator = ObjectAnimator.ofFloat(indexBall,"translationY",CommonUtils.getScreenHeight(),0.0f);
            animator.setDuration(500);
            animator.start();
        }

    }

    private void hideIndexBall(){
        if(indexBall.getVisibility() == View.VISIBLE){
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


    }


    private void setSelectTitle(String title,int index){
        SelectButton btn1 = filterSelectView.findButtonByIndex(index);
        if(btn1!=null){
            btn1.setTextName(title);
        }
        if(filterSelectViewHeader!=null){
            SelectButton btn2 = filterSelectViewHeader.findButtonByIndex(index);
            if(btn2!=null){
                btn2.setTextName(title);
            }
        }
    }

    private void setSelectTitleColor(int colorRes,int index){
        SelectButton btn1 = filterSelectView.findButtonByIndex(index);
        if(btn1!=null){
            btn1.setTextColor(colorRes);
        }
        if(filterSelectViewHeader!=null){
            SelectButton btn2 = filterSelectViewHeader.findButtonByIndex(index);
            if(btn2!=null){
                btn2.setTextColor(colorRes);
            }
        }
    }


    private BannerData starGoodsBanner = CommonPreference.getStarGoodsBanner();
    private ArrayList<CampaignBanner> publicBanners;
    private void initBanner() {
        if(starGoodsBanner != null) {
            publicBanners = starGoodsBanner.getBanners();
            if(!ArrayUtil.isEmpty(publicBanners)) {
                header = LayoutInflater.from(this).inflate(R.layout.view_headview_banner, ptrRecycler.getRefreshableView(),false);
                bannerView = (ClassBannerView) header.findViewById(R.id.bannerView);
                layoutBanner = header.findViewById(R.id.layoutBanner);
                int width = CommonUtils.getScreenWidth();
                int height = CommonUtils.getMyHeight();
                LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                        width, height);
                // 设置banner高度
                layoutBanner.setLayoutParams(localLayoutParams);
                bannerView.setBanners(publicBanners);
                listAdapter.addHeaderView(header);
                CommonUtils.setAutoLoop(starGoodsBanner,bannerView);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTitleBarLeft:
                onBackPressed();
                break;
            case R.id.ivBall:
                ptrRecycler.getRefreshableView().scrollToPosition(0);
                break;
            case R.id.blankSpace:
                hideAnimation(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(llFilterLayout.getVisibility() == View.VISIBLE){
            hideAnimation(false);
        }else{
            super.onBackPressed();
        }
    }


    private void startRequestCampaigns(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        if(page == 0) {
            cacheMap = new HashMap<Integer, Commodity>();
        }

        params.put("page", page + "");
        params.put("pageNum", "12");
        params.put("vipGoods", "0");
        params.put("starGoods","1");
        LocationData locationData = CommonPreference.getLocalData();
        if(locationData!=null){
            params.put("lng", locationData.gLng+"");
            params.put("lat", locationData.gLat+""+"");
        }
        params.put("pageNum", "12");
        LogUtil.i("lalo", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.STAR, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("lalo", "campaignError:" + e.toString());
                ptrRecycler.onRefreshComplete();

            }


            @Override
            public void onResponse(String response) {
                // 调用刷新完成

                try {
                    ptrRecycler.onRefreshComplete();
                    LogUtil.e("lalo", "campaign onResponse:" + response.toString());
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            CommodityListResult result = JSON.parseObject(baseResult.obj, CommodityListResult.class);
                            initData(result,extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, StarRegionActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initData(CommodityListResult result, String extra){
        if(result.getPage()==0){//load完毕
            listAdapter.setLoadMoreView(null);
        }else{
            listAdapter.setLoadMoreView(footerView);
        }
        page++;

        List<Commodity> campaigns = result.getGoodsList();
        if(!ArrayUtil.isEmpty(campaigns)){
            campaigns = delRepetition(campaigns);
        }
        if(REFRESH.equals(extra)){//刷新
            listAdapter.setData(campaigns);
        }else if(LOAD_MORE.equals(extra)){//加载更多
            if(campaigns == null){
                campaigns = new ArrayList<>();
            }
            listAdapter.addData(campaigns);
        }

    }

    @Override
    public void onItemClick(int position) {
        CampaignBanner item = publicBanners.get(position);
        if(item == null) {
            return;
        }
        ActionUtil.dispatchAction(this, item.getAction(), item.getTarget());
    }


    @Override
    public void onDismiss() {
        //此处不能调换顺序，调换顺序就不好使，我也不知道为什么...
        if(filterSelectViewHeader!=null){
            filterSelectViewHeader.clearChecked();
        }
        filterSelectView.clearChecked();

    }


    @Override
    public void onChecked(FilterSelectView v, SelectButton button) {

        int position = button.index;
        boolean isCheck = button.isCheck();

        switch (position){
            case 0:
                if(isCheck){
                    classFilterView.setVisibility(View.VISIBLE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.GONE);
                    classFilterView.resetState();
                    showAnimation();
                }else{
                    hideAnimation(false);
                }
                break;
            case 1:
                if(isCheck){
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.VISIBLE);
                    filterView.setVisibility(View.GONE);
                    classFilterView.resetState();
                    showAnimation();
                }else{
                    hideAnimation(false);
                }
                break;
            case 2:
                if(isCheck){
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.VISIBLE);
                    showAnimation();
                }else{
                    hideAnimation(false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoadMoreRequested() {
        startRequestCampaigns(LOAD_MORE);
    }

    private void hideAnimation(final boolean doRequest) {
        llFilterLayout.setVisibility(View.VISIBLE);
        TranslateAnimation ta = new TranslateAnimation(//
                Animation.RELATIVE_TO_SELF, 0f, //
                Animation.RELATIVE_TO_SELF, 0f,//
                Animation.RELATIVE_TO_SELF, 0f,//
                Animation.RELATIVE_TO_SELF, -1f);
        ta.setDuration(ANIMATION_DURATION);
        ta.setInterpolator(new Interpolator() {
            public float getInterpolation(float input) {
                setCoverViewBackground(llFilterLayout,1-input);
                return input;
            }
        });
        ta.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }
            public void onAnimationEnd(Animation animation) {
                llFilterLayout.setVisibility(View.GONE);
                if(doRequest){
                    refresh();
                }
                if(filterSelectViewHeader!=null){
                    filterSelectViewHeader.clearChecked();
                }

                filterSelectView.clearChecked();

                if(!filterView.isEmpty){
                    setSelectTitleColor(getResources().getColor(R.color.base_pink),2);
                }else{
                    setSelectTitleColor(getResources().getColor(R.color.text_color_gray),2);
                }

                if(headerFilterView!=null){
                    headerFilterView.setVisibility(View.VISIBLE);
                    if(isTopVisible){
                        filterLayout.setVisibility(View.VISIBLE);
                    }else{
                        filterLayout.setVisibility(View.GONE);
                    }
                }


            }
            public void onAnimationRepeat(Animation animation) {

            }
        });
        flContainer.startAnimation(ta);
    }
    /**
     * 显示筛选项动画
     */
    private void showAnimation() {
        if (llFilterLayout.getVisibility() != View.VISIBLE) {
            if(headerFilterView!=null){
                headerFilterView.setVisibility(View.GONE);
            }

            llFilterLayout.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(//
                    Animation.RELATIVE_TO_SELF, 0f, //
                    Animation.RELATIVE_TO_SELF, 0f,//
                    Animation.RELATIVE_TO_SELF, -1f,//
                    Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(ANIMATION_DURATION);
            ta.setInterpolator(new Interpolator() {
                public float getInterpolation(float input) {
                    setCoverViewBackground(llFilterLayout,input);
                    return input;
                }
            });
            flContainer.startAnimation(ta);
        }
    }
    /**
     * 设置浮层的背景
     * @param slideOffset
     */
    private void setCoverViewBackground(LinearLayout llFilter,float slideOffset) {
        if (slideOffset < 0 || slideOffset > 1) {
            slideOffset = 0;
        }
        final int baseAlpha = (0x99000000 & 0xff000000) >>> 24;
        final int img = (int) (baseAlpha * slideOffset);
        final int color = img << 24 | 0x99000000 & 0xffffff;
        llFilter.setBackgroundColor(color);
    }

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == REQUEST_CODE_FOR_GOODS_DETAIL){
                if(data!=null){
                    int position = data.getIntExtra("position", -1);
                    int type = data.getIntExtra("type",0);
                    if(position!=-1&&type!=0){
                        listAdapter.updateLikeState(position,type);
                    }

                }
            }
        }
    }


    private Map<Integer, Commodity> cacheMap = new HashMap<Integer, Commodity>();
    /**
     * @Description: 去重
     */
    private ArrayList<Commodity> delRepetition(List<Commodity> list){
        ArrayList<Commodity> newList = new ArrayList<Commodity>();
        if(cacheMap != null && cacheMap.size() > 0) {
            for (int i = 0; i < list.size(); i++){
                Commodity javaBean = cacheMap.get(list.get(i).getGoodsData().getGoodsId());
                if(javaBean == null){
                    cacheMap.put(list.get(i).getGoodsData().getGoodsId(), list.get(i));
                    LogUtil.d("liangxs", "add");
                }else {
                    LogUtil.d("liangxs", "remove");
                    list.remove(i);
                    i--;
                }
            }
            newList.addAll(list);
        } else {
            for(Commodity bean : list) {
                cacheMap.put(bean.getGoodsData().getGoodsId(), bean);
            }
            newList.addAll(list);
        }
        return newList;
    }
}
