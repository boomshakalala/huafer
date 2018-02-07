package com.huapu.huafen.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommodityAdapter;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.Region;
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
import com.huapu.huafen.views.FilterRegionView;
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
 * Created by admin on 2016/9/24.
 */
public class VIPRegionActivity extends BaseActivity
                                                    implements
                                                    OnItemClickListener,
                                                    FilterSelectView.OnCheckedChangedListener,
                                                    LoadMoreWrapper.OnLoadMoreListener {

    private static final long ANIMATION_DURATION = 10L;
    private PullToRefreshRecyclerView ptrRecycler;
    private int page;
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

    private FilterRegionView filterRegionView;//地区
    private ClassFilterView classFilterView;//分类
    private FilterSortView filterSortView;//排序
    private FilterView filterView;//筛选
    private View blankSpace;
    private ImageView indexBall;

    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == COVERT_REGIONS){
                List<FilterAreaData> data = (List<FilterAreaData>) msg.obj;
                filterRegionView.setData(data);
            }
            return true;
        }
    });
    private boolean isTopVisible;


    private void initFilters(){
        llFilterLayout = (LinearLayout) findViewById(R.id.llFilterLayout);
        flContainer = (FrameLayout) findViewById(R.id.flContainer);

        filterRegionView = (FilterRegionView) findViewById(R.id.filterRegionView);
        classFilterView = (ClassFilterView) findViewById(R.id.classFilterView);
        filterSortView = (FilterSortView) findViewById(R.id.filterSortView);
        filterView = (FilterView) findViewById(R.id.filterView);
        blankSpace = findViewById(R.id.blankSpace);

        //地区
        filterRegionView.setOnItemDataSelect(new FilterRegionView.OnItemDataSelect() {

            @Override
            public void onDataSelected(int[] result, String text) {
                LogUtil.e("onDataSelected","(" +result[0]+","+result[1]+","+result[2]+")");
                params.put("province",result[0]+"");
                params.put("city",result[1]+"");
                params.put("district",result[2]+"");
                setSelectTitle(text,0);
                hideAnimation(true);
            }
        });

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<FilterAreaData> data = covertRegions(CommonPreference.getRegions());
                Message msg = Message.obtain();
                msg.what = COVERT_REGIONS;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        }).start();

        //分类

        classFilterView.setOnItemDataSelect(new ClassFilterView.OnItemDataSelect() {

            @Override
            public void onDataSelected(int[] catId, String text) {
                params.put("cat1",catId[0]+"");
                params.put("cat2",catId[1]+"");
                setSelectTitle(text,1);
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
                setSelectTitle(data.name,2);
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

        filterView.setState(FilterView.STATE.CLASSIFICATION_VIP);
        filterView.initState();

        blankSpace.setOnClickListener(this);

        filterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_region_layout);
        initViews();
        initBanner();
        startRequestForRecommendList();
    }

    // 开始自动翻页(广告轮播)
    @Override
    public void onResume() {
        super.onResume();
        CommonUtils.setAutoLoop(vipGoodsBanner,bannerView);
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
        setTitleString("VIP专区");
        filterLayout = findViewById(R.id.filterLayout);
        //title下方的筛选bar
        filterSelectView = (FilterSelectView)findViewById(R.id.filterSelectView);
        filterSelectView.setOnCheckedChangedListener(this);

        ptrRecycler = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(manager);
        listAdapter = new CommodityAdapter(VIPRegionActivity.this);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecycler.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_filter);
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

    private void startRequestForRecommendList() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        HashMap<String, String> param = new HashMap<String, String>();

        param.put("userLevel", "2,3");
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
                        CommonUtils.error(baseResult, VIPRegionActivity.this, "");
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
                            showIndexBall();
                            isTopVisible = true;
                        }else{
                            hideIndexBall();
                            isTopVisible = false;
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
            headerFilterView = LayoutInflater.from(this).inflate(R.layout.layout_filter, ptrRecycler.getRefreshableView(), false);
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

    private final static int COVERT_REGIONS = 0x4321;

    private List<FilterAreaData> covertRegions(ArrayList<Region> regions) {
        if(regions==null){
            regions = new ArrayList<Region>();
        }
        Region region = new Region();
        region.setDid(0);
        region.setName("全国");

        for (Region r : regions) {
            if (r.getDc() == 1) {//
                ArrayList<District> dists = r.getDistricts();
                District district = new District();
                district.setDid(0);
                String ss = getResources().getString(R.string.all);
                String name = String.format(ss, r.getName());
                district.setName(name);
                dists.add(0,district);
            } else {
                ArrayList<City> cities = r.getCities();
                if(cities == null){
                    cities=new ArrayList<City>();
                }
                City city = new City();
                city.setDid(0);
                String ss = getResources().getString(R.string.all);
                String name = String.format(ss, r.getName());
                city.setName(name);
                cities.add(0,city);
                for(City c:cities){
                    ArrayList<District> dists = c.getDistricts();
                    if(dists!=null){
                        District district = new District();
                        district.setDid(0);
                        String res = getResources().getString(R.string.all);
                        String dName = String.format(res, c.getName());
                        district.setName(dName);
                        dists.add(0,district);
                    }

                }
            }
        }

        regions.add(0,region);

        String json = JSON.toJSONString(regions);
        List<FilterAreaData> list = JSON.parseArray(json, FilterAreaData.class);
        FilterAreaData filterAreaData =findLocationCityByFilterAreaData(list);
        if(filterAreaData!=null){
            list.add(0,filterAreaData);
        }

        return list;
    }

    private FilterAreaData findLocationCityByFilterAreaData(List<FilterAreaData> list){
        LocationData locationData = CommonPreference.getLocalData();
        FilterAreaData locationCity = null;
        if(locationData!=null){
            String locCity = locationData.city;
            for (FilterAreaData r : list) {
                if (r.getDc() == 1) {
                    if(r.getName().equals(locCity)){
                        locationCity = new FilterAreaData();
                        locationCity.setDistricts(r.getDistricts());
                        locationCity.setDid(r.getDid());
                        locationCity.setName(r.getName());
                        locationCity.setDc(r.getDc());
                        locationCity.isShowLocationIcon = true;
                        break;
                    }
                } else {
                    ArrayList<FilterAreaData> cities = r.getCities();
                    if(cities!=null){
                        for(FilterAreaData c:cities){
                            if(c.getName().equals(locCity)){
                                locationCity = new FilterAreaData();
                                locationCity.setCities(c.getDistricts());
                                locationCity.setDid(c.getDid());
                                locationCity.setName(c.getName());
                                locationCity.setDc(c.getDc());
                                locationCity.isLocationCity = true;
                                locationCity.provinceDid = r.getDid();
                                locationCity.isShowLocationIcon = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return locationCity;
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

    private BannerData vipGoodsBanner = CommonPreference.getVipGoodsBanner();
    private ArrayList<CampaignBanner> publicBanners;
    private void initBanner() {
        if(vipGoodsBanner != null) {
            publicBanners = vipGoodsBanner.getBanners();
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
                CommonUtils.setAutoLoop(vipGoodsBanner,bannerView);
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
        LocationData locationData = CommonPreference.getLocalData();
        if(locationData!=null){
            params.put("lng", locationData.gLng+"");
            params.put("lat", locationData.gLat+""+"");
        }
        params.put("vipGoods", "1");
        params.put("starGoods","1");
        params.put("pageNum", "12");
        LogUtil.i("lalo", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.VIP, params, new OkHttpClientManager.StringCallback() {

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
                        CommonUtils.error(baseResult, VIPRegionActivity.this, "");
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
    public void onChecked(FilterSelectView v, SelectButton button) {

        int position = button.index;
        boolean isCheck = button.isCheck();

        switch (position){
            case 0:
                if(isCheck){
                    filterRegionView.setVisibility(View.VISIBLE);
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.GONE);
                    filterRegionView.resetState();
                    showAnimation();
                }else{
                    hideAnimation(false);
                }
                break;
            case 1:
                if(isCheck){
                    filterRegionView.setVisibility(View.GONE);
                    classFilterView.setVisibility(View.VISIBLE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.GONE);
                    classFilterView.resetState();
                    showAnimation();
                }else{
                    hideAnimation(false);
                }
                break;
            case 2:
                if(isCheck){
                    filterRegionView.setVisibility(View.GONE);
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.VISIBLE);
                    filterView.setVisibility(View.GONE);
                    showAnimation();
                }else{
                    hideAnimation(false);
                }
                break;
            case 3:
                if(isCheck){
                    filterRegionView.setVisibility(View.GONE);
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
                    setSelectTitleColor(getResources().getColor(R.color.base_pink),3);
                }else{
                    setSelectTitleColor(getResources().getColor(R.color.text_color_gray),3);
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

    private void refresh() {
        page = 0;
        startRequestCampaigns(REFRESH);
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
