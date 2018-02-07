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
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ClassificationGridAdapter;
import com.huapu.huafen.adapter.ClassificationListAdapter;
import com.huapu.huafen.adapter.GridAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.MyInfoBean;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.callbacks.AppBarStateChangeListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.PriceEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LocationHelper;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.ClassFilterView;
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
 * 商品搜索
 */
public class SearchGoodsListActivity extends BaseActivity implements
        FilterSelectView.OnCheckedChangedListener,
        CompoundButton.OnCheckedChangeListener,
        LoadMoreWrapper.OnLoadMoreListener {

    private final static String TAG = SearchGoodsListActivity.class.getSimpleName();
    private static final long ANIMATION_DURATION = 10L;
    private final static int COVERT_REGIONS = 0x4321;
    @BindView(R2.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R2.id.llFilterLayout)
    LinearLayout llFilterLayout;
    @BindView(R2.id.llFilters)
    LinearLayout llFilters;
    @BindView(R2.id.flContainer)
    FrameLayout flContainer;
    @BindView(R2.id.filterRegionView)
    FilterRegionView filterRegionView;
    @BindView(R2.id.classFilterView)
    ClassFilterView classFilterView;
    @BindView(R2.id.filterSortView)
    FilterSortView filterSortView;
    @BindView(R2.id.filterView)
    FilterView filterView;
    @BindView(R2.id.blankSpace)
    View blankSpace;
    @BindView(R2.id.filterSelectView)
    FilterSelectView filterSelectView;
    @BindView(R2.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.chbOrderBy)
    CheckBox chbOrderBy;
    @BindView(R2.id.loadingStateView)
    HLoadingStateView loadingStateView;
    @BindView(R2.id.appBar)
    AppBarLayout appBar;

    @BindView(R2.id.noDataRecycleView)
    RecyclerView noDataRecycleView;
    private HashMap<String, String> params = new HashMap<>();
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private ClassificationListAdapter classificationListAdapter;
    private ClassificationGridAdapter classificationGridAdapter; // 搜索列表
    private int page;
    private View gridLoadMoreLayout;
    private View listLoadMoreLayout;
    private boolean isExpanded = true;
    private FilterView.STATE state = FilterView.STATE.SEARCH_RESULT;
    private String keyword;
    private boolean filterSelected = false;
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
    private PopupWindow morePopupWindow;

    private GridAdapter gridAdapter; // 推荐列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods_result_new);
        EventBus.getDefault().register(this);
        initParams();
        initView();
        startLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initParams() {
        keyword = getIntent().getStringExtra(MyConstants.EXTRA_SEARCH_KEYWORD);
        if (keyword != null) {

        }
        params.put("keyword", keyword);
        LogUtil.e(TAG, String.format("param == %1s", params.toString()));
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

        LocationHelper.startLocation(new LocationHelper.OnLocationListener() {
            @Override
            public void onLocationComplete(LocationData locationData) {
                if (keyword == null) {
                    String city = locationData.city;
                    setSelectTitle(city, 0);
                }
            }

            @Override
            public void onLocationFailed() {

            }
        });


        setSelectTitle("综合", 2);
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
        classificationGridAdapter.setSearchQuery(keyword);
        classificationListAdapter.setSearchQuery(keyword);

        boolean isCheck = CommonPreference.getBooleanValue(MyConstants.SEARCH_LIST_LAYOUT, false);
        if (isCheck) {
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(classificationListAdapter.getWrapperAdapter());
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(classificationGridAdapter.getWrapperAdapter());
        }

        GridLayoutManager layoutManager = new GridLayoutManager(noDataRecycleView.getContext(), 2, GridLayoutManager.VERTICAL, false);
        noDataRecycleView.setLayoutManager(layoutManager);
        gridAdapter = new GridAdapter(this);
        noDataRecycleView.setAdapter(gridAdapter.getWrapperAdapter());

        initGridAdapter();
        initListAdapter();

        chbOrderBy.setChecked(isCheck);


    }

    private void initListAdapter() {
        listLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.search_empty);
        classificationListAdapter.setEmptyView(viewEmpty);
        classificationListAdapter.setOnLoadMoreListener(this);
    }

    private void initGridAdapter() {

        gridLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.search_empty);
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

        CommonPreference.setBooleanValue(MyConstants.SEARCH_LIST_LAYOUT, isCheck);
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


        //分类
        ArrayList<Cat> cats = CommonUtils.getCats();
        classFilterView.setData(cats);

        classFilterView.setOnItemDataSelect(new ClassFilterView.OnItemDataSelect() {

            @Override
            public void onDataSelected(int[] catId, String text) {
                params.put("cat1", catId[0] + "");
                params.put("cat2", catId[1] + "");
                setSelectTitle(text, 1);
                hideAnimation(true);


            }
        });


        //排序

        filterSortView.setOnItemDataSelect(new FilterSortView.OnItemDataSelect() {

            @Override
            public void onDataSelected(SortEntity data) {
//                params.put("orderBy", data.id + "");
//                setSelectTitle(data.name, 2);
//                hideAnimation(true);
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
        filterView.initState();

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
        recyclerView.scrollToPosition(0);

    }

    private void setSelectTitleColor(int colorRes, int index) {
        SelectButton btn1 = filterSelectView.findButtonByIndex(index);
        if (btn1 != null) {
            btn1.setTextColor(colorRes);
        }
        recyclerView.scrollToPosition(0);
    }

    /**
     * 显示筛选项动画
     */
    private void showAnimation() {
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

//                filterSelectView.clearChecked();
                int childCount = filterSelectView.getChildCount();

                if (childCount == 6) {
                    filterSelectView.clearTwoChecked(-1);
                } else if (childCount == 5) {
                    filterSelectView.clearOneChecked(-1);
                }


                hideFilterLayout();


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
            LogUtil.d("danielluan", "inhere.");

        }
    }

    @Override
    public void onBackPressed() {
        if (llFilterLayout.getVisibility() == View.VISIBLE) {
            hideAnimation(false);
        } else {
            setResult(RESULT_OK);
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
                    showAnimation();
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

                    showAnimation();
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
                break;
            default:
                break;
        }
    }

    private void hideFilterLayout() {
        llFilterLayout.setVisibility(View.GONE);
        filterSelected = false;
        titleBar.getTitleTextRight().setTextColor(filterView.isEmpty ? getResources().getColor(R.color.text_color) : getResources().getColor(R.color.base_pink));
    }

    private void startLoading() {
        page = 0;
        doRequest(LOADING);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
    }

    private void refresh() {
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
        OkHttpClientManager.postAsyn(MyConstants.SEARCH_GOODS_LIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(SearchGoodsListActivity.this, "请检查网络连接");
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
                            if (null != result.getGoodsList() && result.getGoodsList().size() > 0) {
                                ptrFrameLayout.setVisibility(View.VISIBLE);
                                noDataRecycleView.setVisibility(View.GONE);
                                initData(result, extra);
                            } else {
                                ptrFrameLayout.setVisibility(View.GONE);
                                noDataRecycleView.setVisibility(View.VISIBLE);
                                MyInfoBean myInfoBean = JSON.parseObject(baseResult.obj, MyInfoBean.class);
                                View goodsHeader = LayoutInflater.from(SearchGoodsListActivity.this).inflate(R.layout.layout_goods_search_nodata, noDataRecycleView, false);
                                TextView textView = (TextView) goodsHeader.findViewById(R.id.titleInfo);
                                textView.setText("-没有找到相关宝贝，为你推荐-");
                                gridAdapter.removeHeaders();
                                gridAdapter.addHeaderView(goodsHeader);
                                gridAdapter.setRecTraceId(myInfoBean.recTraceId);
                                gridAdapter.setData(myInfoBean.recItems);
                            }
                        }
                    } else {
                        // TODO: 17/12/22
                        CommonUtils.error(baseResult, SearchGoodsListActivity.this, "");
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
        View searchTitle;
        boolean isSameCity = mIntent.getBooleanExtra("sameCity", false);
        if (isSameCity) {
            searchTitle = LayoutInflater.from(this).inflate(R.layout.search_rect, null);
        } else {
            searchTitle = LayoutInflater.from(this).inflate(R.layout.search_rect_left, null);
        }

        TextView textView = (TextView) searchTitle.findViewById(R.id.tv);
        if (!TextUtils.isEmpty(keyword)) {
            textView.setText(keyword);
        }

//        titleBar.setUnRead(MyConstants.UNREAD_ORDER_COUNT + MyConstants.UNREAD_PRIVATE_COUNT + MyConstants.UNREAD_SYSTEM_COUNT + MyConstants.UNREAD_COMMENT_MSG_COUNT);
        searchTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (keyword != null) {
                    finish();
                } else {
                    Intent intent = new Intent(SearchGoodsListActivity.this, SearchActivity.class);
                    startActivity(intent);
                }
                //同城专区变更

            }
        });

        titleBar.setTitle(searchTitle);
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
                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
                    filterRegionView.setVisibility(View.GONE);
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.VISIBLE);
                    showAnimation();
                } else {
                    titleBar.getTitleTextRight().setTextColor(filterView.isEmpty ? getResources().getColor(R.color.text_color) : getResources().getColor(R.color.base_pink));
                    hideAnimation(false);
                }
            }
        });
    }

    public void onEventMainThread(final Object obj) {
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

}
