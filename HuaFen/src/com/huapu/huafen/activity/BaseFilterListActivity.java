package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.callbacks.AppBarStateChangeListener;
import com.huapu.huafen.events.PriceEvent;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.ClassFilterView;
import com.huapu.huafen.views.FilterRegionView;
import com.huapu.huafen.views.FilterSelectView;
import com.huapu.huafen.views.FilterSortView;
import com.huapu.huafen.views.FilterView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.huapu.huafen.views.SelectButton;
import com.huapu.huafen.views.TitleBarNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/5/17.
 */

public abstract class BaseFilterListActivity extends BaseActivity implements
        FilterSelectView.OnCheckedChangedListener,
        CompoundButton.OnCheckedChangeListener {

    public final static int LINEAR = 1;
    public final static int GRID = LINEAR + 1;
    private static final long ANIMATION_DURATION = 10L;
    private final static int COVERT_REGIONS = 0x4321;
    private final static String FILTER_SORT = "filter_sort";
    protected HashMap<String, String> params = new HashMap<>();
    @BindView(R.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R.id.filterSelectView)
    FilterSelectView filterSelectView;
    @BindView(R.id.chbOrderBy)
    CheckBox chbOrderBy;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.filterRegionView)
    FilterRegionView filterRegionView;
    @BindView(R.id.classFilterView)
    ClassFilterView classFilterView;
    @BindView(R.id.filterSortView)
    FilterSortView filterSortView;
    @BindView(R.id.filterView)
    FilterView filterView;
    @BindView(R.id.flContainer)
    FrameLayout flContainer;
    @BindView(R.id.blankSpace)
    View blankSpace;
    @BindView(R.id.llFilterLayout)
    LinearLayout llFilterLayout;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
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
    private PopupWindow morePopupWindow;

    private boolean filterSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_list_layout);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initParams();
        initView();
    }

    public void initParams() {

    }

    protected void initView() {
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
                int index = manager.findFirstVisibleItemPosition();
                View childAt = recyclerView.getChildAt(0);
                boolean indexTop = false;
                if (childAt == null || (index == 0 && childAt.getTop() == 0)) {
                    indexTop = true;
                }

                return isExpanded && indexTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh(REFRESH);
            }
        });
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        linearLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);


        boolean isCheck = CommonPreference.getBooleanValue(MyConstants.REGION_LAYOUT, false);
        if (isCheck) {
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        onLayoutManagerChanged(isCheck ? LINEAR : GRID);

        chbOrderBy.setChecked(isCheck);
    }

    public abstract void onLayoutManagerChanged(int layoutManagerID);

    public abstract void refresh(String state);

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        if (llFilterLayout.getVisibility() == View.VISIBLE) {
            hideAnimation(false);
        }
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int index = manager.findFirstVisibleItemPosition();
        if (isCheck) {
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            recyclerView.setLayoutManager(gridLayoutManager);
        }
        onLayoutManagerChanged(isCheck ? LINEAR : GRID);

//        recyclerView.scrollToPosition(index);

        CommonPreference.setBooleanValue(MyConstants.REGION_LAYOUT, isCheck);
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
                    refresh(FILTER_SORT);
                }

//                filterSelectView.clearChecked();
                int childCount = filterSelectView.getChildCount();

                if (childCount == 6) {
                    filterSelectView.clearTwoChecked(-1);
                } else if (childCount == 5) {
                    filterSelectView.clearOneChecked(-1);
                }

//                if(filterView.isEmpty){
//                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
//                }else{
//                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
//                }
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
            //hideFilterLayout();
            LogUtil.d("danielluan", "inhere.......");
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
                refresh(FILTER_SORT);
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
                refresh(FILTER_SORT);
                break;
            case 4:
                hideFilterLayout();
                button.setTextColor(getResources().getColor(R.color.base_pink));
                params.put("orderBy", "-3");
                refresh(FILTER_SORT);
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
                    titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
                    filterRegionView.setVisibility(View.GONE);
                    classFilterView.setVisibility(View.GONE);
                    filterSortView.setVisibility(View.GONE);
                    filterView.setVisibility(View.VISIBLE);
                    showAnimation();
                } else {
                    if (filterView.isEmpty) {
                        titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.text_color));
                    } else {
                        titleBar.getTitleTextRight().setTextColor(getResources().getColor(R.color.base_pink));
                    }

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
                refresh(FILTER_SORT);
            } else if (priceEvent.getState() == 0) {
                hideFilterLayout();
                params.put("orderBy", "-1");
                refresh(FILTER_SORT);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
