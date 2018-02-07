package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.OneYuanRegionAdapter;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.OneYuanRegionResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by admin on 2017/5/18.
 */

public class OneYuanRegionActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = OneYuanRegionActivity.class.getSimpleName();
    private final static String FILTER_SORT = "filter_sort";

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout) PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.ivRelease) ImageView ivRelease;
    @BindView(R.id.llSort) LinearLayout llSort;
    @BindView(R.id.rgSort) RadioGroup rgSort;
    @BindView(R.id.rgClassification) RadioGroup rgClassification;

    private HashMap<String,String> params = new HashMap<>();
    private OneYuanRegionAdapter adapter;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private View loadMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_yuan_region_layout);
        rgClassification.setOnCheckedChangeListener(this);
        rgSort.setOnCheckedChangeListener(this);
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

                return indexTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });


        executorService.scheduleAtFixedRate(
                new Task(),
                0,
                1000,
                TimeUnit.MILLISECONDS);


        adapter = new OneYuanRegionAdapter(this);

        GridLayoutManager manager = new GridLayoutManager(recyclerView.getContext(),OneYuanRegionAdapter.FULL_SPAN,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter.getWrapperAdapter());

        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        adapter.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {

            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        });
        adapter.setOnFilterChangedListener(new OneYuanRegionAdapter.OnFilterChangedListener() {

            @Override
            public void onFilterChanged(int res) {
                ((RadioButton)rgClassification.findViewById(res)).setChecked(true);
            }

            @Override
            public void onSortChanged(int res) {
                LogUtil.e("OneYuanKKKKK","res");
//                rgSort.check(res);
                ((RadioButton)rgSort.findViewById(res)).setChecked(true);
            }
        });

        adapter.setOnRefreshListener(new OneYuanRegionAdapter.OnRefreshListener() {

            @Override
            public void refresh() {
                recyclerView.scrollToPosition(0);
                OneYuanRegionActivity.this.refresh();
            }
        });
        
        int grantedOneYun = CommonPreference.getGrantedOneYun();
        if(grantedOneYun == 1){
            ivRelease.setVisibility(View.VISIBLE);
        }else{
            ivRelease.setVisibility(View.GONE);
        }

        ivRelease.setOnClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager manager = (GridLayoutManager)recyclerView.getLayoutManager();
                int firstPosition = manager.findFirstCompletelyVisibleItemPosition();
                if(firstPosition>=adapter.headerCount){
                    llSort.setVisibility(View.VISIBLE);
                }else{
                    llSort.setVisibility(View.GONE);
                }
            }
        });



        params.put("cat2","0");
        params.put("orderBy","-3");
        startLoading();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(v.getId() == R.id.ivRelease){
            if (!CommonPreference.isLogin()) {
                ActionUtil.loginAndToast(this);
                return;
            }
            Intent intent = new Intent(this, ReleaseActivity.class);
            intent.putExtra("visit_one_yuan",true);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != executorService) {
            executorService.shutdown();
        }
    }

    private void doRequest(final String extra) {
        params.put("page", page + "");
        params.put("cat1", "20");
        LocationData locationData = CommonPreference.getLocalData();
        if(locationData!=null){
            params.put("lng", locationData.gLng+"");
            params.put("lat", locationData.gLat+""+"");
        }
        OkHttpClientManager.postAsyn(MyConstants.ONE_REGION, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(OneYuanRegionActivity.this, "请检查网络连接");
                LogUtil.e(TAG, "OneYuanRegion error:" + e.toString());
                if(extra.equals(LOADING)){
//                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }else if(extra.equals(REFRESH)){
                    ptrFrameLayout.refreshComplete();
                }else if(extra.equals(LOAD_MORE)){

                }
            }


            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "OneYuanRegion response:" + response);
                // 调用刷新完成
                if(extra.equals(LOADING)){
//                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                }else if(extra.equals(REFRESH)){
                    ptrFrameLayout.refreshComplete();
                }else if(extra.equals(LOAD_MORE)){

                }
                try {
                    OneYuanRegionResult result = JSON.parseObject(response, OneYuanRegionResult.class);

                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initData(result,extra,params.get("cat2"),params.get("orderBy"));
                    } else {
                        CommonUtils.error(result, OneYuanRegionActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startLoading(){
        page = 0;
        doRequest(LOADING);
    }


    private void refresh(){
        page = 0;
        doRequest(REFRESH);
    }

    private void filterSort(){
        page = 0;
        doRequest(FILTER_SORT);
    }

    private void loadMore(){
        doRequest(LOAD_MORE);
    }

    private int page;

    private void initData(OneYuanRegionResult result, String extra,String cat2,String orderBy) {
        if(result == null || result.obj ==null){
            return;
        }
        if(result.obj.page==0){
            adapter.setLoadMoreView(null);
        }else{
            adapter.setLoadMoreView(loadMoreLayout);
        }

        page++;

        if(extra.equals(LOADING)){
            adapter.setData(result,cat2,orderBy);
        }else if(extra.equals(REFRESH)){
            adapter.setData(result,cat2,orderBy);
        }else if(FILTER_SORT.equals(extra)){
            adapter.setData(result,cat2,orderBy);
            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(adapter.headerCount -1,0);
        }else if(LOAD_MORE.equals(extra)){

            List<Item> list = result.obj.list;
            if(list == null) {
                list = new ArrayList<>();
            }

            adapter.addAll(list);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if(group == rgClassification){
            if(checkedId == R.id.rbAll){
                params.put("cat2","0");
            }else if(checkedId == R.id.rbLady){
                params.put("cat2","2010");
            }else if(checkedId == R.id.rbBaby){
                params.put("cat2","2020");
            }else if(checkedId == R.id.rbHome){
                params.put("cat2","2030");
            }
        }else if(group == rgSort){
            if(checkedId == R.id.rbTime){
                LogUtil.e("OneYuanKKKKK","rbTime");
                params.put("orderBy", "-2"); // 1时间，2热度
            }else if(checkedId == R.id.rbHot){
                LogUtil.e("OneYuanKKKKK","rbHot");
                params.put("orderBy", "-3"); // 1时间，2热度
            }
        }

        filterSort();
    }


    private class Task implements Runnable {

        @Override
        public void run() {
            if (null != adapter) {
                Logger.i("this is run task");
                if (null != adapter.getData()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyCountDownDataSetChanged();
                        }
                    });
                }

            }
        }
    }

    @Override
    public void initTitleBar() {
        setTitleString("花粉儿1元店");
    }
}
