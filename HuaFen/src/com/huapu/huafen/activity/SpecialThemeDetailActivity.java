package com.huapu.huafen.activity;

/**
 * Created by danielluan on 2017/9/22.
 */

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
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
import com.huapu.huafen.adapter.VolumnGridAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.IconFilter;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.beans.VolumnResult;
import com.huapu.huafen.callbacks.AppBarStateChangeListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.MessagePrivateCountEvent;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.events.PriceEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.ClassificationSecondViewNew;
import com.huapu.huafen.views.FeatureGridView;
import com.huapu.huafen.views.FeatureTitleBar;
import com.huapu.huafen.views.FilterIconView;
import com.huapu.huafen.views.FilterMenuView;
import com.huapu.huafen.views.FilterRegionView;
import com.huapu.huafen.views.FilterSelectView;
import com.huapu.huafen.views.FilterSortView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.huapu.huafen.views.SelectButton;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by admin on 2017/4/12.
 */

public class SpecialThemeDetailActivity extends BaseActivity implements
        FilterSelectView.OnCheckedChangedListener,
        CompoundButton.OnCheckedChangeListener,
        LoadMoreWrapper.OnLoadMoreListener {
    private final static String TAG = SpecialThemeDetailActivity.class.getSimpleName();
    private static final long ANIMATION_DURATION = 10L;
    private final static int COVERT_REGIONS = 0x4321;

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
    ClassificationSecondViewNew classFilterView;
    @BindView(R.id.filterSortView)
    FilterSortView filterSortView;
    @BindView(R.id.filterView)
    FilterMenuView filterMenuView;
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
    @BindView(R.id.iconfilterview)
    FilterIconView iconViewFilter;

    FeatureGridView featureGridView;
    private HashMap<String, String> paramsVolumn = new HashMap<>();
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private VolumnGridAdapter VolumnGridAdapter;
    private int page;
    private View gridLoadMoreLayout;
    private boolean isExpanded = true;

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
    ArrayList<VolumnResult.filterMenuData> menudata = null;
    private boolean isfirstopenmenu = true;
    private boolean isfirstenter = true;
    private boolean showIconView;
    private String config = "";
    private String targetType = "";
    private String targetId = "";
    private String keywords="";

    FeatureTitleBar alltitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specialtheme_detail);
        EventBus.getDefault().register(this);
        initParams();
        initView();
        startLoading();

    }

    private void initParams() {
        isfirstopenmenu = true;
        isfirstenter = true;
        showIconView = false;
        String json = mIntent.getStringExtra("Schizodata");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            config = jsonObject.getJSONObject("config").toString();
            targetType = jsonObject.optString("targetType");
            targetId = jsonObject.optString("targetId");
            keywords = jsonObject.optString("keywords");

            LogUtil.d("danielluan", "js " + jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initView() {
        initTitle();
        appBar.addOnOffsetChangedListener(new AppBarStateChangeListener() {

            @Override
            public void onStateChanged(AppBarLayout appBarLayout, AppBarStateChangeListener.State state) {
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
        VolumnGridAdapter = new VolumnGridAdapter(this);
        boolean isCheck = CommonPreference.getBooleanValue(MyConstants.CLASSIFICATION_LAYOUT, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(VolumnGridAdapter.getWrapperAdapter());
        initGridAdapter();
        chbOrderBy.setChecked(isCheck);

    }


    private void requestVolumn(final String extra) {

        String s = "{\"pageTitle\":\"悦诗风吟\",\"filterBarAlpha\":[2110,2220,1130,1140],\"brandId\":1121,\"showBrandSection\":true,\"agesOpts\":[{\"title\":\"0~3个月\",\"value\":\"1,2\"},{\"title\":\"3~6个月\",\"value\":\"1,2\"},{\"title\":\"6~12个月\",\"value\":\"1,2\"}]}";
        String vip = "{\"pageTitle\":\"悦诗风吟\",\"filterBarAlpha\":[2110,2220,1130,1140],\"brandId\":422,\"featuredSection\":\"vip\",\"showBrandSection\":true,\"agesOpts\":[{\"title\":\"0~3个月\",\"value\":\"1,2\"},{\"title\":\"3~6个月\",\"value\":\"1,2\"},{\"title\":\"6~12个月\",\"value\":\"1,2\"}]}";
        JSONObject config = null;
        try {
            config = new JSONObject(vip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(keywords))
            paramsVolumn.put("keywords",keywords);
        paramsVolumn.put("targetType", this.targetType);
        paramsVolumn.put("targetId", this.targetId);
        paramsVolumn.put("page", page + "");
        if (!TextUtils.isEmpty(this.config)) {
            paramsVolumn.put("config", this.config);
        }
        LogUtil.d("danielluan", "params" + paramsVolumn.toString());

        OkHttpClientManager.postAsyn(MyConstants.VOLUMN_LIST, paramsVolumn, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(SpecialThemeDetailActivity.this, "请检查网络连接");
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
                            updateData(baseResult, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, SpecialThemeDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void updatediscount(VolumnResult result) {

        if (result.showFilterBarDiscount) {
            llDiscount.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            discountFilter.setLayoutManager(linearLayoutManager);
            ClassificationDiscountAdapter classificationDiscountAdapter = new ClassificationDiscountAdapter(this);
            classificationDiscountAdapter.setOnDiscountClickListener(new ClassificationDiscountAdapter.OnDiscountClickListener() {

                @Override
                public void onDiscountResponse(String discount) {
                    paramsVolumn.put("discount", discount);
                    refresh();
                }
            });
            discountFilter.setAdapter(classificationDiscountAdapter);
        }

    }

    private void updateData(BaseResult baseResult, String extras) {
        VolumnResult result = JSON.parseObject(baseResult.obj, VolumnResult.class);
        if (result == null) {
            return;
        }
        setTitleName(result.pageTitle);
        if (isfirstenter) {
            //update filterBar
            updateFilterBar(result);
            updatediscount(result);
            menudata = result.filterMenu;
            if (menudata == null) {
                titleBar.setRightText("", null);
            }
            // update filterBarAlpha
            VolumnGridAdapter.removeHeaders();
            updateFilterBarAlpha(result);
            updateBrand(result);
            initFeatureSection(result);
            isfirstenter = false;
        }

        if (page == 0) {
            updateFeatureSection(result);
        }
        initAndAddData(result, extras);
    }

    private void initFeatureSection(final VolumnResult result) {
        if (result.featuredSection != null) {
            featureGridView = new FeatureGridView(this);
            featureGridView.setTitle(result.featuredSection.title);
            featureGridView.setSubTitle(result.featuredSection.content);
            featureGridView.getTitleBar().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (result.featuredSection.moreBtn != null) {
                        String confgs = result.featuredSection.moreBtn.config;
                        String targettype = result.featuredSection.moreBtn.targetType;
                        String targetid = result.featuredSection.moreBtn.targetId;
                        JSONObject jsonObject = new JSONObject();

                        try {
                            JSONObject config = new JSONObject(confgs);
                            jsonObject.put("config", config);
                            jsonObject.put("targetType", targettype);
                            jsonObject.put("targetId", targetid);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(SpecialThemeDetailActivity.this, SpecialThemeDetailActivity.class);
                        intent.putExtra("Schizodata", jsonObject.toString());
                        startActivity(intent);

                    }

                }
            });

            VolumnGridAdapter.addHeaderView(featureGridView);
            alltitleBar = new FeatureTitleBar(this);
            alltitleBar.setTvTitle("全部商品");
            alltitleBar.hideMore();
            VolumnGridAdapter.addHeaderView(alltitleBar);
        }
    }

    private void updateFeatureSection(final VolumnResult result) {
        if (result.featuredSection != null) {
            ArrayList<Item> list = new ArrayList<Item>();
            List<Item> listt = result.featuredSection.items;
            for (int i = 0; listt != null && i < listt.size(); i++) {
                Item it = listt.get(i);
                //IconFilter fi = new IconFilter(it.item.goodsImgs.get(0), "¥" + it.item.price, "");
                LogUtil.d("danielluan", it.item.goodsImgs.get(0) + "#" + it.item.price);
                list.add(it);
            }
            featureGridView.setData(list);
            if (list.size() <= 0) {
                featureGridView.setVisibility(View.GONE);
                alltitleBar.setVisibility(View.GONE);

            } else {
                featureGridView.setVisibility(View.VISIBLE);
                alltitleBar.setVisibility(View.VISIBLE);
            }
        } else {
            if (featureGridView != null) {
                featureGridView.setVisibility(View.GONE);
                alltitleBar.setVisibility(View.GONE);
            }
        }
    }

    private void updateBrand(VolumnResult result) {
        if (result.brandSection != null) {
            //View gridHeader = fillBrandHead();
            View gridHeader = LayoutInflater.from(this).inflate(R.layout.classification_brand_header, recyclerView, false);
            SimpleDraweeView header = (SimpleDraweeView) gridHeader.findViewById(R.id.brandHeader);
            header.setImageURI(result.brandSection.image);
            //ImageLoader.(this).loadImage(header, result.brandSection.image, R.drawable.default_pic, R.drawable.default_pic, ScalingUtils.ScaleType.FIT_CENTER);
            TextView brandTitle = (TextView) gridHeader.findViewById(R.id.brandTitle);
            TextView tvContent = (TextView) gridHeader.findViewById(R.id.tvContent);
            if (!TextUtils.isEmpty(result.brandSection.title)) {
                brandTitle.setText(result.brandSection.title);
            }
            if (!TextUtils.isEmpty(result.brandSection.content)) {
                tvContent.setText(result.brandSection.content);
            }
            VolumnGridAdapter.addHeaderView(gridHeader);
        }
    }

    private void updateFilterBarAlpha(VolumnResult result) {
        if (result.filterBarAlpha != null) {
            //FilterIconView filterIconView = new FilterIconView(this);

            iconViewFilter.setOnItemClickListener(new FilterIconView.OnItemClickListener() {
                @Override
                public void onItemClick(IconFilter fi) {
                    paramsVolumn.put("cat2", String.valueOf(fi.getValue()));
                    refresh();
                    LogUtil.d("danielluan", fi.getNameDisplay());
                }
            });


            //VolumnGridAdapter.addHeaderView(filterIconView);
            ArrayList<IconFilter> list = new ArrayList<IconFilter>();
            List<VolumnResult.filterBarAlphaData.filterItem> list1 = result.filterBarAlpha.items;
            for (int i = 0; list1 != null && i < list1.size(); i++) {
                VolumnResult.filterBarAlphaData.filterItem item = list1.get(i);
                IconFilter fi = new IconFilter(item.image, item.title, item.value);
                list.add(fi);
            }
            iconViewFilter.setData(list);
            iconViewFilter.setVisibility(View.VISIBLE);
            showIconView = true;
        }
    }

    private void updateFilterBar(VolumnResult result) {
        ArrayList<VolumnResult.filterBarData> fb = result.filterBar;
        if (fb != null) {
            boolean findcat = false;
            boolean findregion = false;
            boolean findsorter = false;
            for (int i = 0; i < fb.size(); i++) {
                VolumnResult.filterBarData tfb = fb.get(i);
                if (tfb.type.equals("RegionFilter")) {
                    filterSelectView.setTitleByIndex(tfb.title, 0);
                    findregion = true;
                }
                if (tfb.type.equals("CategoryFilter")) {
                    filterSelectView.setTitleByIndex(tfb.title, 1);
                    findcat = true;
                    classFilterView.Config(tfb.attrs.level, tfb.attrs.items, tfb.attrs.expand);
                }
                if (tfb.type.equals("Sorter")) {
                    findsorter = true;
                }
            }
            if (!findregion) {
                filterSelectView.setSelections(0);
            }
            if (!findcat) {
                filterSelectView.setSelections(1);
            }
            if (!findsorter) {
                filterSelectView.setSelections(2);
            }
        } else {
            filterSelectView.setVisibility(View.GONE);
        }
    }

    private void updateFilterMenu() {

        filterMenuView.setMenuDataList(menudata);
    }


    private void initGridAdapter() {

        gridLoadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_filter);
        VolumnGridAdapter.setEmptyView(viewEmpty);
        VolumnGridAdapter.setOnLoadMoreListener(this);
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
            //recyclerView.setAdapter(classificationListAdapter.getWrapperAdapter());
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
            //recyclerView.setAdapter(classificationGridAdapter.getWrapperAdapter());
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
                paramsVolumn.put("province", result[0] + "");
                paramsVolumn.put("city", result[1] + "");
                paramsVolumn.put("district", result[2] + "");
                filterSelectView.setTitleByIndex(text, 0);
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
        classFilterView.setOnItemClickListener(new ClassificationSecondViewNew.OnItemClickListener() {

            @Override
            public void onItemClick(Cat cat) {
                if (cat != null) {
                    paramsVolumn.put("cat2", String.valueOf(cat.getCid()));
                    filterSelectView.setTitleByIndex(cat.getName(), 1);
                }
                hideAnimation(true);
            }

            @Override
            public void onItemClick(Cat cat1, Cat cat2) {
                if (cat1 != null) {
                    paramsVolumn.put("cat1", String.valueOf(cat1.getCid()));
                }
                if (cat2 != null) {
                    if (cat2.getCid() != 0) {
                        paramsVolumn.put("cat2", String.valueOf(cat2.getCid()));
                    } else {
                        paramsVolumn.remove("cat2");
                    }
                    filterSelectView.setTitleByIndex(cat2.getName(), 1);
                } else {
                    filterSelectView.setTitleByIndex(cat1.getName(), 1);
                }
                hideAnimation(true);
            }
        });

        //List<Cat> cats = CommonUtils.getSecondCats(firstId, secondId);

        //classFilterView.setData(cats);

        //排序

        filterSortView.setOnItemDataSelect(new FilterSortView.OnItemDataSelect() {

            @Override
            public void onDataSelected(SortEntity data) {
                //paramsVolumn.put("orderBy", data.id + "");
                //filterSelectView.setTitleByIndex(data.name, 2);
                //hideAnimation(true);
            }
        });

        //筛选
        filterMenuView.setOnConfirmButtonClick(new FilterMenuView.OnConfirmButtonClick() {

            @Override
            public void onClick(Map<String, String> paramsMap) {
                LogUtil.e("OnFilterConfirm", paramsMap);
                paramsVolumn.putAll(paramsMap);
                hideAnimation(true);
            }
        });

        blankSpace.setOnClickListener(this);
        filterMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        iconViewFilter.setOnItemClickListener(new FilterIconView.OnItemClickListener() {
            @Override
            public void onItemClick(IconFilter fi) {

//                paramsVolumn.put("cat2", String.valueOf(fi.getValue()));
//                filterSelectView.setTitleByIndex(fi.getNameDisplay(), 1);
//                hideAnimation(true);
                LogUtil.d("danielluan", fi.getNameDisplay());

            }
        });

    }


    /**
     * 显示筛选项动画
     */
    private void showAnimation(boolean judge) {
        if (llFilterLayout.getVisibility() != View.VISIBLE) {
            //iconViewFilter.setVisibility(View.GONE);
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
            if (!filterMenuView.isEmpty) {
                titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
            } else {
                titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_black));
            }
        }
    }

    private void hideFilterLayout() {
        llFilterLayout.setVisibility(View.GONE);
        if (showIconView) {
            //iconViewFilter.setVisibility(View.VISIBLE);
        }
        filterSelected = false;
        titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
    }

    private void hideAnimation(final boolean doRequest) {
        filterSelected = false;
        titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
        if (showIconView) {
            //iconViewFilter.setVisibility(View.VISIBLE);
        }
        llFilterLayout.setVisibility(View.VISIBLE);
       /* 隐藏软键盘 */
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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

                if (!filterMenuView.isEmpty) {
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
                    filterMenuView.setVisibility(View.GONE);
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
                    filterMenuView.setVisibility(View.GONE);
                    showAnimation(true);
                } else {
                    hideAnimation(false);
                }
                break;
            case 2:

                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                paramsVolumn.put("orderBy", "smart");
                refresh();
                break;
            case 3:

                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                paramsVolumn.put("orderBy", "-time");
                refresh();
                break;
            case 4:
                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                paramsVolumn.put("orderBy", "-hot");
                refresh();
                break;
            case 5:
// event
                break;
            default:
                break;
        }
    }

    private void startLoading() {
        page = 0;
        requestVolumn(LOADING);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);

    }

    private void refresh() {
        recyclerView.scrollToPosition(0);
        page = 0;
        requestVolumn(REFRESH);
    }

    private void loadMore() {
        requestVolumn(LOAD_MORE);
    }

    private void initAndAddData(VolumnResult result, String extra) {
        if (result.page == 0) {
            VolumnGridAdapter.setLoadMoreView(null);
        } else {
            VolumnGridAdapter.setLoadMoreView(gridLoadMoreLayout);
        }
        page++;

        ArrayList<Item> list = result.items;
        ArrayList<Item> var1 = null;

        if (list != null) {
            var1 = (ArrayList<Item>) list.clone();
        }

        if (extra.equals(LOADING)) {
            VolumnGridAdapter.setData(var1);
        } else if (extra.equals(REFRESH)) {
            VolumnGridAdapter.setData(var1);
        } else if (LOAD_MORE.equals(extra)) {
            if (var1 == null) {
                var1 = new ArrayList<>();
            }
            VolumnGridAdapter.addData(var1);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    private void setTitleName(String name) {
        if (titleBar != null) {
            titleBar.setTitle(name);
        }

    }

    private void initTitle() {
        View searchTitle = LayoutInflater.from(this).inflate(R.layout.search_rect, null);
        LinearLayout llTextSearch = (LinearLayout) searchTitle.findViewById(R.id.llTextSearch);
        llTextSearch.setOnClickListener(this);

        //titleBar.setTitle(searchTitle);
        titleBar.setTitle("");
        titleBar.setRightText("筛选", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterMenu();
            }
        });
    }

    private void openFilterMenu() {
        filterSelected = !filterSelected;
        if (filterSelected) {
            if (isfirstopenmenu) {
                updateFilterMenu();
                isfirstopenmenu = false;
            }
            filterSelectView.clearTwoChecked(-1);
            titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
            filterRegionView.setVisibility(View.GONE);
            classFilterView.setVisibility(View.GONE);
            filterSortView.setVisibility(View.GONE);
            filterMenuView.setVisibility(View.VISIBLE);
            showAnimation(false);
        } else {
            titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
            hideAnimation(false);
        }
    }

    private void actionToMineFragment(int selectFragment) {
        Intent intent = new Intent(SpecialThemeDetailActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
    }


    public void updateUnread() {
//        int unreadCount = MyConstants.UNREAD_ORDER_COUNT + MyConstants.UNREAD_PRIVATE_COUNT + MyConstants.UNREAD_SYSTEM_COUNT + MyConstants.UNREAD_COMMENT_MSG_COUNT;
//        titleBar.setUnRead(unreadCount);
//        updateUnreadCount(unreadCount);
    }

    public void onEventMainThread(final Object obj) {
        LogUtil.e(TAG, "onEventMainThread" + obj);
        if (obj == null) {
            return;
        }
        if (obj instanceof MessageUnReadCountEvent) {
            MessageUnReadCountEvent event = (MessageUnReadCountEvent) obj;
            if (event.isUpdate) {
                updateUnread();
            }
        } else if (obj instanceof MessagePrivateCountEvent) {
            updateUnread();

        } else if (obj instanceof PriceEvent) {
            PriceEvent priceEvent = (PriceEvent) obj;
            if (priceEvent.getState() == 1) {
                hideFilterLayout();
                paramsVolumn.put("orderBy", "+price");
                refresh();
            } else if (priceEvent.getState() == 0) {
                hideFilterLayout();
                paramsVolumn.put("orderBy", "-price");
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
