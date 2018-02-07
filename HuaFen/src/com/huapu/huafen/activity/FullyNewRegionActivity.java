package com.huapu.huafen.activity;

import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.AbstractRecyclerAdapter;
import com.huapu.huafen.adapter.FullyNewRegionAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.VIPRegionBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.FilterView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.RegionCommonHeader;
import com.huapu.huafen.views.RegionCommonHeaderVIP;
import com.squareup.okhttp.Request;

import java.util.HashMap;

/**
 * Created by qwe on 2017/5/17.
 */

public class FullyNewRegionActivity extends BaseFilterListActivity implements AbstractRecyclerAdapter.LoadMoreListener {

    private final static String FILTER_SORT = "filter_sort";
    /**
     * type=99 全新专区
     * type=110 vip
     */
    private int type = 0;
    private FullyNewRegionAdapter fullyNewRegionAdapter;
    private RegionCommonHeader headerView;
    private RegionCommonHeaderVIP headerViewVIP;
    private int page = 0;

    @Override
    protected void initView() {
        if (getIntent().hasExtra("type")) {
            type = getIntent().getIntExtra("type", 0);
        }
        super.initView();
        recyclerView.setBackgroundResource(R.color.base_bg);
        headerView = new RegionCommonHeader(this, type);
        headerViewVIP = new RegionCommonHeaderVIP(this, type);
        fullyNewRegionAdapter = new FullyNewRegionAdapter(this, recyclerView);
        fullyNewRegionAdapter.setLoadMoreListener(this);
        fullyNewRegionAdapter.setHeaderEnable(true);
        fullyNewRegionAdapter.setAutoLoadMoreEnable(false);

        if (type == 110) {
            fullyNewRegionAdapter.addHeaderView(headerViewVIP);
        } else {
            fullyNewRegionAdapter.addHeaderView(headerView);
        }

        boolean isCheck = CommonPreference.getBooleanValue(MyConstants.REGION_LAYOUT, false);
        if (isCheck) {
            fullyNewRegionAdapter.setItemType(1);
        } else {
            fullyNewRegionAdapter.setItemType(2);
        }
        recyclerView.setAdapter(fullyNewRegionAdapter);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        if (type == 110) {
            filterView.setState(FilterView.STATE.VIP);
        } else {
            filterView.setState(FilterView.STATE.NEW);
        }
        filterView.initStateNew();
        refreshData();
    }

    @Override
    public void onLayoutManagerChanged(int layoutManagerID) {
        if (null != fullyNewRegionAdapter) {
            fullyNewRegionAdapter.setItemType(layoutManagerID);
            fullyNewRegionAdapter.notifyDataSetChanged();
            if (layoutManagerID == GRID) {
                recyclerView.setAdapter(fullyNewRegionAdapter);
            }
        }

//        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//        layoutManager.scrollToPositionWithOffset(1, 0);
//        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        int index = manager.findFirstVisibleItemPosition();
//        recyclerView.scrollToPosition(index + 1);


    }

    private void refreshData() {
        if (page > 0) {
            fullyNewRegionAdapter.initLoading(false);
        }
        page = 0;
        doRequest(REFRESH);
    }

    @Override
    public void refresh(String state) {
        page = 0;
        doRequest(state);
    }

    private void loadMore() {
        doRequest(LOAD_MORE);
    }

    private void doRequest(final String state) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("page", String.valueOf(page));

        if (page == 0) {
            fullyNewRegionAdapter.initLoading(false);
        }
        String requestUrl;
        if (type == 110) {
            requestUrl = MyConstants.NEW_VIPREGION;
            if (filterView.getCheckBox().isChecked()) {
                params.remove("vipGoods");
            } else {
                params.put("vipGoods", "1");
                params.put("starGoods", "1");
            }
        } else {
            requestUrl = MyConstants.FULLY_BRANDNEW;
            params.put("brandnew", "1");
        }
        LocationData locationData = CommonPreference.getLocalData();
        if (locationData != null) {
            params.put("lng", String.valueOf(locationData.gLng));
            params.put("lat", String.valueOf(locationData.gLat));
        }

        if (null != params && params.size() > 0) {
            hashMap.putAll(params);
        }
        OkHttpClientManager.postAsyn(requestUrl, hashMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(FullyNewRegionActivity.this, "请检查网络连接");
                if (state.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (state.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                } else if (state.equals(LOAD_MORE)) {

                }
            }


            @Override
            public void onResponse(String response) {
                try {
                    Logger.e("get response:" + response);
                    if (state.equals(REFRESH)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        ptrFrameLayout.refreshComplete();
                    } else if (state.equals(LOAD_MORE)) {
                        fullyNewRegionAdapter.notifyMoreFinish();
                    } else if (state.equals(FILTER_SORT)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        ptrFrameLayout.refreshComplete();
                    }
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }

                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            VIPRegionBean regionBean = JSON.parseObject(baseResult.obj, VIPRegionBean.class);
                            initData(regionBean, state);
                        }
                    } else {
                        CommonUtils.error(baseResult, FullyNewRegionActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(VIPRegionBean regionBean, String state) {
        if (regionBean.page == 0) {
            fullyNewRegionAdapter.setAutoLoadMoreEnable(false);
            fullyNewRegionAdapter.loadFinish(true);
        } else {
            fullyNewRegionAdapter.setAutoLoadMoreEnable(true);
        }

        page++;


        if (state.equals(REFRESH)) {
            headerView.setData(regionBean.hotCats, regionBean.hotGoods, regionBean.activeUsers);
            headerViewVIP.setData(regionBean.hotCats, regionBean.hotGoods, regionBean.activeUsers);
            fullyNewRegionAdapter.setRes(regionBean.list);
        } else if (LOAD_MORE.equals(state)) {
            fullyNewRegionAdapter.appendData(regionBean.list);
        } else if (FILTER_SORT.equals(state)) {
            headerView.setData(regionBean.hotCats, regionBean.hotGoods, regionBean.activeUsers);
            headerViewVIP.setData(regionBean.hotCats, regionBean.hotGoods, regionBean.activeUsers);
            fullyNewRegionAdapter.setRes(regionBean.list);
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(1, 0);
        }
    }


    @Override
    public void onLoadMore() {
        loadMore();
    }
}
