package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.OrderJointAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.OrderJointResult;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.alibaba.fastjson.JSON.parseObject;

/**
 * 批量发货订单
 */
public class OrderBatchShipListActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    private final static String TAG = OrderBatchShipListActivity.class.getSimpleName();
    private PullToRefreshRecyclerView ptrRecycler;
    private HLoadingStateView loadingStateView;
    private OrderJointAdapter adapter;
    private int page = 0;
    private View loadMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_batch_ship_list);
        initView();
        startLoading();
    }

    /**
     * 获取单品类别列表
     *
     * @param
     */

    private void initView() {
        setTitleString("批量发货");
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        ptrRecycler = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(linearLayoutManager);

        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
                ptrRecycler.getRefreshableView(), false);

        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecycler, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_batch_ship);
        adapter = new OrderJointAdapter(this);
        adapter.setEmptyView(viewEmpty);
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());

        ptrRecycler.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);

    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        refresh();
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        loadMore();
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        page = 0;
        startRequestForOrderJointList(LOADING);
    }

    private void refresh() {
        page = 0;
        startRequestForOrderJointList(REFRESH);
    }

    private void loadMore() {
        startRequestForOrderJointList(LOAD_MORE);
    }

    /**
     * 获取合并订单列表
     *
     * @param
     */
    private void startRequestForOrderJointList(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(page));
        LogUtil.e(TAG, "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.ORDER_JOINT_LIST, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e(TAG, "批量发货onError:" + e.getMessage());

                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecycler.onRefreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e(TAG, "批量发货onResponse:" + response);

                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecycler.onRefreshComplete();
                }
                try {
                    BaseResult baseResult = parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        OrderJointResult result = JSON.parseObject(baseResult.obj, OrderJointResult.class);
                        initData(result, extra);
                    } else {
                        CommonUtils.error(baseResult, OrderBatchShipListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(OrderJointResult result, String extra) {

        if (result.getPage() == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }

        page++;

        List<OrderJointResult.OrderJointData> batches = result.getBatches();
        if (extra.equals(LOADING)) {
            adapter.setData(batches);
        } else if (extra.equals(REFRESH)) {
            adapter.setData(batches);
        } else if (LOAD_MORE.equals(extra)) {
            if (batches == null) {
                batches = new ArrayList<>();
            }
            adapter.addAll(batches);
        }

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "onActivityResult(" + requestCode + "," + resultCode + ")");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == adapter.REQUEST_CODE_FOR_REFRESH) {
                LogUtil.e("lalo", "requestCode:" + requestCode);
                ptrRecycler.setRefreshing();
            }
        }
    }

}
