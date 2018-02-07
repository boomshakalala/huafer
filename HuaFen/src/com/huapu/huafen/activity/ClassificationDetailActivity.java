package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ClassificationDiscountAdapter;
import com.huapu.huafen.adapter.ClassificationGridAdapter;
import com.huapu.huafen.adapter.ClassificationListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.ClassificationResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.callbacks.AppBarStateChangeListener;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.PriceEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.ClassificationSecondView;
import com.huapu.huafen.views.FilterRegionView;
import com.huapu.huafen.views.FilterSelectView;
import com.huapu.huafen.views.FilterSortView;
import com.huapu.huafen.views.FilterView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.huapu.huafen.views.SelectButton;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 分类页？
 */
public class ClassificationDetailActivity extends BaseActivity implements
        FilterSelectView.OnCheckedChangedListener,
        CompoundButton.OnCheckedChangeListener,
        LoadMoreWrapper.OnLoadMoreListener {

    private final static String TAG = ClassificationDetailActivity.class.getSimpleName();
    private static final long ANIMATION_DURATION = 10L;
    private final static int COVERT_REGIONS = 0x4321;
    public boolean showClass = false;
    @BindView(R.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R.id.llFilterLayout)
    LinearLayout llFilterLayout;
    @BindView(R.id.llFilters)
    LinearLayout llFilters;
    @BindView(R.id.flContainer)
    FrameLayout flContainer;
    @BindView(R.id.filterRegionView)
    FilterRegionView filterRegionView;
    @BindView(R.id.classFilterView)
    ClassificationSecondView classFilterView;
    @BindView(R.id.filterSortView)
    FilterSortView filterSortView;
    @BindView(R.id.filterView)
    FilterView filterView;
    @BindView(R.id.blankSpace)
    View blankSpace;
    @BindView(R.id.filterSelectView)
    FilterSelectView filterSelectView;
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.chbOrderBy)
    CheckBox chbOrderBy;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.llDiscount)
    LinearLayout llDiscount;
    @BindView(R.id.discountFilter)
    RecyclerView discountFilter;
    private ClassificationResult.Opt opt;
    private HashMap<String, String> params = new HashMap<>();
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private ClassificationListAdapter classificationListAdapter;
    private ClassificationGridAdapter classificationGridAdapter;
    private String key;
    private long firstId;
    private long secondId;
    private int page;
    private View gridLoadMoreLayout;
    private View listLoadMoreLayout;
    private boolean isExpanded = true;
    private FilterView.STATE state = FilterView.STATE.SEARCH_RESULT;
    private Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == COVERT_REGIONS) {
                List<FilterAreaData> data = (List<FilterAreaData>) msg.obj;
                filterRegionView.setData(data);
            }
            return true;
        }
    });
    private boolean filterSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().hasExtra("SHOWCLASS")) {
            showClass = getIntent().getBooleanExtra("SHOWCLASS", false);
        }
        setContentView(R.layout.activity_classification_detail_new);
        EventBus.getDefault().register(this);
        key = mIntent.getStringExtra("key");
        opt = (ClassificationResult.Opt) mIntent.getSerializableExtra(MyConstants.EXTRA_OPTION);

        if (TextUtils.isEmpty(key) || opt == null) {
            //TODO
            return;
        }

        initParams();
        initView();
        startLoading();
    }

    private void initParams() {
        try {
            firstId = Long.parseLong(key);
        } catch (NumberFormatException e) {
            LogUtil.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        if ("grid".equals(opt.type)) {
            try {
                firstId = Long.parseLong(opt.note);
                secondId = Long.parseLong(opt.target);
            } catch (NumberFormatException e) {
                LogUtil.e(TAG, e.getMessage());
                e.printStackTrace();
            }

            setSelectTitle(opt.title, 1);

            if (getIntent().hasExtra("FROM_PAGE")) {
                if ("NEW_VIP".equals(getIntent().getStringExtra("FROM_PAGE"))) {
                    if (ActionConstants.OPEN_CATS_NEW_ZONE.equals(opt.action)) {
                        state = FilterView.STATE.NEW;
                        params.put("brandnew", "1");
                    } else if (ActionConstants.OPEN_CATS_VIP_ZONE.equals(opt.action)) {
                        state = FilterView.STATE.CLASSIFICATION_VIP;
                        params.put("vipGoods", "1");
                    }
                }

            }


        } else if ("brand_grid".equals(opt.type)) {
            if (!TextUtils.isEmpty(opt.target)) {
                params.put("brandId", opt.target);
            }
            secondId = 0;
        } else if ("image_grid".equals(opt.type)) {
            try {
                firstId = Long.parseLong(opt.target);
            } catch (NumberFormatException e) {
                LogUtil.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            secondId = 0;

            if (ActionConstants.OPEN_CATS_DISCOUNT_ZONE.equals(opt.action)) {
                state = FilterView.STATE.SEARCH_RESULT;
                params.put("discount", "1,2,3,4,5");
                AppBarLayout.LayoutParams filterParams = (AppBarLayout.LayoutParams) llFilters.getLayoutParams();
                filterParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                llDiscount.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                discountFilter.setLayoutManager(linearLayoutManager);
                ClassificationDiscountAdapter classificationDiscountAdapter = new ClassificationDiscountAdapter(this);
                classificationDiscountAdapter.setOnDiscountClickListener(new ClassificationDiscountAdapter.OnDiscountClickListener() {

                    @Override
                    public void onDiscountResponse(String discount) {
                        params.put("discount", discount);
                        refresh();
                    }
                });
                discountFilter.setAdapter(classificationDiscountAdapter);
            } else if (ActionConstants.OPEN_CATS_NEW_ZONE.equals(opt.action)) {
                state = FilterView.STATE.NEW;
                params.put("brandnew", "1");
            } else if (ActionConstants.OPEN_CATS_VIP_ZONE.equals(opt.action)) {
                state = FilterView.STATE.CLASSIFICATION_VIP;
                params.put("vipGoods", "1");
                params.put("starGoods", "1");
            }
        }

        params.put("cat1", String.valueOf(firstId));
        params.put("cat2", String.valueOf(secondId));
        LogUtil.e(TAG, String.format("param ==%1s", params.toString()));

        LogUtil.d("danielluan", String.format("param ==%1s", params.toString()));
    }

    private boolean isShowAge() {
        if (firstId == 17 ||
                firstId == 18 ||
                firstId == 19 ||
                firstId == 21 ||
                firstId == 22 ||
                firstId == 25 ||
                firstId == 26 ||
                firstId == 27 ||
                firstId == 28 ||
                firstId == 29 ||
                firstId == 30 ||
                firstId == 31 ||
                secondId == 1010 ||
                secondId == 1020 ||
                secondId == 1030 ||
                secondId == 1040 ||
                secondId == 1050 ||
                secondId == 1220 ||
                secondId == 1230 ||
                secondId == 1610 ||
                secondId == 1620 ||
                secondId == 1630 ||
                secondId == 1640 ||
                secondId == 1650 ||
                secondId == 1660 ||
                secondId == 2010 ||
                secondId == 2030
                ) {
            return false;
        }

        return true;
    }

    private void initView() {
        initTitle();
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                isExpanded = state == State.EXPANDED;
            }
        });
        initFilters();
        initRecyclerView();
        initCheckButton();
    }

    private void initCheckButton() {
        chbOrderBy.setOnCheckedChangeListener(this);
    }

    private void initRecyclerView() {
        ptrFrameLayout.buildPtr(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int index = manager.findFirstCompletelyVisibleItemPosition();
                return isExpanded && index == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        classificationGridAdapter = new ClassificationGridAdapter(this);
        classificationListAdapter = new ClassificationListAdapter(this);

        boolean isCheck = CommonPreference.getBooleanValue(MyConstants.CLASSIFICATION_LAYOUT, false);
        if (isCheck) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(classificationListAdapter.getWrapperAdapter());
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(classificationGridAdapter.getWrapperAdapter());
        }

        initGridAdapter();
        initListAdapter();

        chbOrderBy.setChecked(isCheck);

    }

    private void initListAdapter() {
        listLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_filter);
        if ("brand_grid".equals(opt.type)) {
            View listHeader = LayoutInflater.from(this).inflate(R.layout.classification_brand_header, recyclerView, false);
            SimpleDraweeView header = (SimpleDraweeView) listHeader.findViewById(R.id.brandHeader);
            header.setImageURI(opt.image);
            //ImageLoader.(this).loadImage(header, opt.image, R.drawable.default_pic, R.drawable.default_pic, ScalingUtils.ScaleType.FIT_CENTER);
            TextView brandTitle = (TextView) listHeader.findViewById(R.id.brandTitle);
            TextView tvContent = (TextView) listHeader.findViewById(R.id.tvContent);
            if (!TextUtils.isEmpty(opt.title)) {
                brandTitle.setText(opt.title);
            }
            if (!TextUtils.isEmpty(opt.note)) {
                tvContent.setText(opt.note);
            }
            classificationListAdapter.addHeaderView(listHeader);
        }
        classificationListAdapter.setEmptyView(viewEmpty);
        classificationListAdapter.setOnLoadMoreListener(this);
    }

    private void initGridAdapter() {

        gridLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_filter);
        if ("brand_grid".equals(opt.type)) {
            View gridHeader = LayoutInflater.from(this).inflate(R.layout.classification_brand_header, recyclerView, false);
            SimpleDraweeView header = (SimpleDraweeView) gridHeader.findViewById(R.id.brandHeader);
            header.setImageURI(opt.image);
            //ImageLoader.(this).loadImage(header, opt.image, R.drawable.default_pic, R.drawable.default_pic);
            TextView brandTitle = (TextView) gridHeader.findViewById(R.id.brandTitle);
            TextView tvContent = (TextView) gridHeader.findViewById(R.id.tvContent);
            if (!TextUtils.isEmpty(opt.title)) {
                brandTitle.setText(opt.title);
            }
            if (!TextUtils.isEmpty(opt.note)) {
                tvContent.setText(opt.note);
            }
            classificationGridAdapter.addHeaderView(gridHeader);
        }
        classificationGridAdapter.setEmptyView(viewEmpty);
        classificationGridAdapter.setOnLoadMoreListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        if (llFilterLayout.getVisibility() == View.VISIBLE) {
            hideAnimation(false);
        }
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int index = manager.findFirstVisibleItemPosition();
        if (isCheck) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(classificationListAdapter.getWrapperAdapter());
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(classificationGridAdapter.getWrapperAdapter());
        }
        recyclerView.scrollToPosition(index);

        CommonPreference.setBooleanValue(MyConstants.CLASSIFICATION_LAYOUT, isCheck);
    }

    private void initFilters() {
        filterSelectView.setOnCheckedChangedListener(this);

        //地区
        filterRegionView.setOnItemDataSelect(new FilterRegionView.OnItemDataSelect() {

            @Override
            public void onDataSelected(int[] result, String text) {
                LogUtil.e("onDataSelected", "(" + result[0] + "," + result[1] + "," + result[2] + ")");
                params.put("province", result[0] + "");
                params.put("city", result[1] + "");
                params.put("district", result[2] + "");
                setSelectTitle(text, 0);
                hideAnimation(true);
            }
        });

        new Thread(new Runnable() {

            @Override
            public void run() {
                List<FilterAreaData> data = CommonUtils.covertRegions();
                Message msg = Message.obtain();
                msg.what = COVERT_REGIONS;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        }).start();

//        if (showClass) {
//            Logger.e("this is run");
//            classFilterView.setVisibility(View.VISIBLE);
//        } else {
//            Logger.e("this is run");
//            classFilterView.setVisibility(View.GONE);
//        }

        //分类
        classFilterView.setOnItemClickListener(new ClassificationSecondView.OnItemClickListener() {

            @Override
            public void onItemClick(Cat cat) {
                params.put("cat2", String.valueOf(cat.getCid()));
                setSelectTitle(cat.getName(), 1);
                hideAnimation(true);
            }
        });

        List<Cat> cats = CommonUtils.getSecondCats(firstId, secondId);

        classFilterView.setData(cats);

        //排序

        filterSortView.setOnItemDataSelect(new FilterSortView.OnItemDataSelect() {

            @Override
            public void onDataSelected(SortEntity data) {
                params.put("orderBy", data.id + "");
                setSelectTitle(data.name, 2);
                hideAnimation(true);
            }
        });

        //筛选
        filterView.setOnConfirmButtonClick(new FilterView.OnConfirmButtonClick() {

            @Override
            public void onClick(Map<String, String> paramsMap) {
                LogUtil.e("OnFilterConfirm", paramsMap);
                params.putAll(paramsMap);
                hideAnimation(true);
            }
        });

        filterView.setState(state);
        filterView.setAge(isShowAge());

        if (getIntent().hasExtra("FROM_PAGE") && "NEW_VIP".equals(getIntent().getStringExtra("FROM_PAGE"))) {
            filterView.initStateNew();
        } else {
            filterView.initState();
        }

        blankSpace.setOnClickListener(this);

        filterView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void setSelectTitle(String title, int index) {
        SelectButton btn1 = filterSelectView.findButtonByIndex(index);
        if (btn1 != null) {
            if ("全部分类".equals(title)) {
                btn1.setTextName("全部");
            } else {
                btn1.setTextName(title);
            }
        }
    }

    private void setSelectTitleColor(int colorRes, int index) {
        SelectButton btn1 = filterSelectView.findButtonByIndex(index);
        if (btn1 != null) {
            btn1.setTextColor(colorRes);
        }
    }

    /**
     * 显示筛选项动画
     */
    private void showAnimation(boolean judge) {
        if (llFilterLayout.getVisibility() != View.VISIBLE) {
            llFilterLayout.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(//
                    Animation.RELATIVE_TO_SELF, 0f, //
                    Animation.RELATIVE_TO_SELF, 0f,//
                    Animation.RELATIVE_TO_SELF, -1f,//
                    Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(ANIMATION_DURATION);
            ta.setInterpolator(new Interpolator() {
                public float getInterpolation(float input) {
                    setCoverViewBackground(llFilterLayout, input);
                    return input;
                }
            });

            flContainer.startAnimation(ta);
        }
        if (judge) {
            if (!filterView.isEmpty) {
                titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
            } else {
                titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_black));
            }
        }
    }

    private void hideFilterLayout() {
        llFilterLayout.setVisibility(View.GONE);
        filterSelected = false;
        titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
    }

    private void hideAnimation(final boolean doRequest) {
        filterSelected = false;
        titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
        llFilterLayout.setVisibility(View.VISIBLE);
        TranslateAnimation ta = new TranslateAnimation(//
                Animation.RELATIVE_TO_SELF, 0f, //
                Animation.RELATIVE_TO_SELF, 0f,//
                Animation.RELATIVE_TO_SELF, 0f,//
                Animation.RELATIVE_TO_SELF, -1f);
        ta.setDuration(ANIMATION_DURATION);
        ta.setInterpolator(new Interpolator() {
            public float getInterpolation(float input) {
                setCoverViewBackground(llFilterLayout, 1 - input);
                return input;
            }
        });
        ta.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                llFilterLayout.setVisibility(View.GONE);
                if (doRequest) {
                    refresh();
                }

                int childCount = filterSelectView.getChildCount();

                if (childCount == 6) {
                    filterSelectView.clearTwoChecked(-1);
                } else if (childCount == 5) {
                    filterSelectView.clearOneChecked(-1);
                }


                if (!filterView.isEmpty) {
                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
                } else {
                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_black));
                }


            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
        flContainer.startAnimation(ta);
    }

    private void setCoverViewBackground(LinearLayout llFilter, float slideOffset) {
        if (slideOffset < 0 || slideOffset > 1) {
            slideOffset = 0;
        }
        final int baseAlpha = (0x99000000 & 0xff000000) >>> 24;
        final int img = (int) (baseAlpha * slideOffset);
        final int color = img << 24 | 0x99000000 & 0xffffff;
        llFilter.setBackgroundColor(color);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.blankSpace) {
            hideAnimation(false);
        } else if (v.getId() == R.id.llTextSearch) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (llFilterLayout.getVisibility() == View.VISIBLE) {
            hideAnimation(false);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onChecked(FilterSelectView v, SelectButton button) {
        int position = button.index;
        boolean isCheck = button.isCheck();

        switch (position) {
            case 0:
                if (isCheck) {
                    filterRegionView.setVisibility(View.VISIBLE);
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.GONE);
                    filterRegionView.resetState();

                    showAnimation(true);
                } else {
                    hideAnimation(false);
                }
                break;
            case 1:
                if (isCheck) {
                    filterRegionView.setVisibility(View.GONE);
                    classFilterView.setVisibility(View.VISIBLE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.GONE);

                    showAnimation(true);
                } else {
                    hideAnimation(false);
                }
                break;
            case 2:
//                if (isCheck) {
//                    filterRegionView.setVisibility(View.GONE);
//                    classFilterView.setVisibility(View.GONE);
//                    filterSortView.setVisibility(View.VISIBLE);
//                    filterView.setVisibility(View.GONE);
//                    showAnimation();
//                } else {
//                    hideAnimation(false);
//                }
                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                params.put("orderBy", "0");
                refresh();
                break;
            case 3:
//                if (isCheck) {
//                    filterRegionView.setVisibility(View.GONE);
//                    classFilterView.setVisibility(View.GONE);
//                    filterSortView.setVisibility(View.GONE);
//                    filterView.setVisibility(View.VISIBLE);
//                    showAnimation();
//                } else {
//                    hideAnimation(false);
//                }
                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                params.put("orderBy", "-2");
                refresh();
                break;
            case 4:
                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                params.put("orderBy", "-3");
                refresh();
                break;
            case 5:
//                hideFilterLayout();
//                if (isCheck) {
//                    if (isHigh) {
//                        params.put("orderBy", "-1");
//                        isHigh = false;
//                    } else {
//                        params.put("orderBy", "1");
//                        isHigh = true;
//                    }
//
//                }
//
//                refresh();
                break;
            default:
                break;
        }
    }

    private void startLoading() {
        page = 0;
        doRequest(LOADING);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
    }

    private void refresh() {
        recyclerView.scrollToPosition(0);
        page = 0;
        doRequest(REFRESH);
    }

    private void loadMore() {
        doRequest(LOAD_MORE);
    }

    private void doRequest(final String extra) {
        params.put("page", page + "");
        LocationData locationData = CommonPreference.getLocalData();
        if (locationData != null) {
            params.put("lng", locationData.gLng + "");
            params.put("lat", locationData.gLat + "" + "");
        }
        LogUtil.e(TAG, "params:" + params.toString());

        if (getIntent().hasExtra("FROM_PAGE")) {
            if ("NEW_VIP".equals(getIntent().getStringExtra("FROM_PAGE"))) {
                if (ActionConstants.OPEN_CATS_VIP_ZONE.equals(opt.action)) {
                    if (filterView.getCheckBox().isChecked()) {
                        params.remove("vipGoods");
                    } else {
                        params.put("vipGoods", "1");
                        params.put("starGoods", "1");
                    }
                }
            }

        }


        OkHttpClientManager.postAsyn(MyConstants.SEARCH_GOODS_LIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(ClassificationDetailActivity.this, "请检查网络连接");
                LogUtil.e(TAG, "classificationDetail error:" + e.toString());
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                } else if (extra.equals(LOAD_MORE)) {

                }
            }


            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "classificationDetail response:" + response);
                // 调用刷新完成
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                } else if (extra.equals(LOAD_MORE)) {

                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            CommodityListResult result = JSON.parseObject(baseResult.obj, CommodityListResult.class);
                            initData(result, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, ClassificationDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(CommodityListResult result, String extra) {
        if (result.getPage() == 0) {
            classificationGridAdapter.setLoadMoreView(null);
            classificationListAdapter.setLoadMoreView(null);
        } else {
            classificationGridAdapter.setLoadMoreView(gridLoadMoreLayout);
            classificationListAdapter.setLoadMoreView(listLoadMoreLayout);
        }

        page++;

        ArrayList<Commodity> list = result.getGoodsList();
        ArrayList<Commodity> var1 = null;
        ArrayList<Commodity> var2 = null;
        if (list != null) {
            var1 = (ArrayList<Commodity>) list.clone();
            var2 = (ArrayList<Commodity>) list.clone();
        }

        if (extra.equals(LOADING)) {
            classificationGridAdapter.setData(var1);
            classificationListAdapter.setData(var2);
        } else if (extra.equals(REFRESH)) {
            classificationGridAdapter.setData(var1);
            classificationListAdapter.setData(var2);
        } else if (LOAD_MORE.equals(extra)) {
            if (var1 == null) {
                var1 = new ArrayList<>();
            }

            if (var2 == null) {
                var2 = new ArrayList<>();
            }
            classificationGridAdapter.addData(var1);
            classificationListAdapter.addData(var2);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    private void initTitle() {
        View searchTitle = LayoutInflater.from(this).inflate(R.layout.search_rect, null);
        LinearLayout llTextSearch = (LinearLayout) searchTitle.findViewById(R.id.llTextSearch);
        llTextSearch.setOnClickListener(this);
        titleBar.setTitle(searchTitle);
//        titleBar.setUnRead(MyConstants.UNREAD_ORDER_COUNT + MyConstants.UNREAD_PRIVATE_COUNT + MyConstants.UNREAD_SYSTEM_COUNT + MyConstants.UNREAD_COMMENT_MSG_COUNT);
//        titleBar.setOnRightButtonClickListener(R.drawable.gray_point, new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                initPopMore(v);
//            }
//        });
        titleBar.setRightText("筛选", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterSelected = !filterSelected;
                if (filterSelected) {
                    filterSelectView.clearTwoChecked(-1);
                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
                    filterRegionView.setVisibility(View.GONE);
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.VISIBLE);
                    showAnimation(false);
                } else {
                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
                    hideAnimation(false);
                }
            }
        });
    }


    public void onEventMainThread(final Object obj) {
        LogUtil.e(TAG, "onEventMainThread" + obj);
        if (obj == null) {
            return;
        }
        if (obj instanceof PriceEvent) {
            PriceEvent priceEvent = (PriceEvent) obj;
            if (priceEvent.getState() == 1) {
                hideFilterLayout();
                params.put("orderBy", "1");
                refresh();
            } else if (priceEvent.getState() == 0) {
                hideFilterLayout();
                params.put("orderBy", "-1");
                refresh();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
