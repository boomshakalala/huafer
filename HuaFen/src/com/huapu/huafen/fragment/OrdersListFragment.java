package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrderBatchShipListActivity;
import com.huapu.huafen.adapter.OrdersListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Orders;
import com.huapu.huafen.beans.OrdersResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liang on 2016/10/26.
 */
public class OrdersListFragment extends BaseFragment implements
                                                        PullToRefreshBase.OnRefreshListener<RecyclerView>,
                                                        LoadMoreWrapper.OnLoadMoreListener {

    private final static String TAG = OrdersListFragment.class.getSimpleName();
    private final static int REQUEST_CODE_ORDERS_LIST = 0x1111;

    private int status;
    private int state;
    private int role;
    public PullToRefreshRecyclerView ptrRecycler;
    private int page;
    private View footerView;
    public OrdersListAdapter adapter;
    private HLoadingStateView loadingStateView;
    private boolean isFirstRequest = true;
    private ImageView ivEmpty;
    private View viewEmpty;
    private View header;
    private View layoutBatchShip;

    public static OrdersListFragment newInstance(Bundle bundle) {
        OrdersListFragment commentListFragment = new OrdersListFragment();
        commentListFragment.setArguments(bundle);
        return commentListFragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_orders_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        initRecyclerView(root);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        status = bundle.getInt(MyConstants.EXTRA_ORDERS_STATUS);
        state = bundle.getInt(MyConstants.EXTRA_ORDERS_STATE);
        role = bundle.getInt(MyConstants.EXTRA_ORDERS_ROLE);
        boolean isFirstLoad = bundle.getBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD, false);
        if (isFirstLoad) {
            initRequest();
        }
        switch (role) {
            case 0:
                ivEmpty.setImageResource(R.drawable.empty_refund);
                break;
            case 1:
                ivEmpty.setImageResource(R.drawable.empty_buy);
                break;
            case 2:
                ivEmpty.setImageResource(R.drawable.empty_sell);
                break;
        }
        adapter.setEmptyView(viewEmpty);
    }

    private void initRequest() {
        if (adapter != null && adapter.isEmpty() && isFirstRequest) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
            startRequestForGetOrders(role, status, state, REFRESH);
        }
    }


    @Override
    protected void loadResponse() {
        super.loadResponse();
        initRequest();
    }

    private void initRecyclerView(View rootView) {
        ptrRecycler = (PullToRefreshRecyclerView) rootView.findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(layoutManager);
        ptrRecycler.setOnRefreshListener(this);
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.load_layout, ptrRecycler.getRefreshableView(), false);
        adapter = new OrdersListAdapter(this, new ArrayList<Orders>());
        viewEmpty = LayoutInflater.from(getContext()).inflate(R.layout.view_empty_image, ptrRecycler.getRefreshableView(), false);
        ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        adapter.setOnLoadMoreListener(this);
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());
        adapter.setOnItemClickListener(new OrdersListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {
                ptrRecycler.setRefreshing();
            }
        });
        loadingStateView = (HLoadingStateView) rootView.findViewById(R.id.loadingStateView);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        header = LayoutInflater.from(getActivity()).inflate(R.layout.view_headview_batch_ship, ptrRecycler.getRefreshableView(), false);
        layoutBatchShip = header.findViewById(R.id.layoutBatchShip);
    }


    private void startRequestForGetOrders(final int role, final int status, final int state, final String extra) {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
            ptrRecycler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrRecycler.onRefreshComplete();
                }
            }, 1000);
            ToastUtil.toast(getActivity(), "请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(page));
        params.put("role", String.valueOf(role));
        params.put("status", String.valueOf(status));
        params.put("state", String.valueOf(state));
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.ORDERS, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                        LogUtil.i(TAG, "error orders：" + e.toString());
                        ptrRecycler.onRefreshComplete();
                        loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            LogUtil.i(TAG, "orders：" + response);
                            // 测试数据
//                            response = "{\"code\": 200,\"msg\": \"返回成功\",\"obj\": {\"page\": 0,\"orders\":[{\"goodsData\": {\"goodsImgs\": [\"huafer.image.cc\"],\"name\": \"白兰地\",\"brand\": \"白兰地\",\"goodsId\": 898819},\"orderData\": {\"orderId\": 898819,\"status\":1,\"statusTitle\":\"待付款\",\"price\":123,\"state\":0},\"refund\":{\"rid\":1,\"title\":\"买家申请仅退款\",\"userId\":82},\"arbitration\":{\"aid\":1,\"title\":\"仲裁举证期\",\"userId\":82},\"userData\":{\"userName\":\"孙俪\",\"userId\":82,\"avatarUrl\":\"huafer.image.cc\",\"userLevel\":3,\"hasCredit\":1},\"userValue\":{\"zmCreditPoint\":700}},{\"goodsData\": {\"goodsImgs\": [\"huafer.image.cc\"],\"name\": \"白兰地\",\"brand\": \"白兰地\",\"goodsId\": 898819},\"orderData\": {\"orderId\": 898819,\"status\":1,\"statusTitle\":\"\",\"price\":123,\"state\":1},\"refund\":{\"rid\":1,\"title\":\"买家申请仅退款\",\"userId\":82},\"arbitration\":{\"aid\":1,\"title\":\"仲裁举证期\",\"userId\":82},\"userData\":{\"userName\":\"孙俪\",\"userId\":82,\"avatarUrl\":\"huafer.image.cc\",\"userLevel\":3,\"hasCredit\":1},\"userValue\":{\"zmCreditPoint\":700}},{\"goodsData\": {\"goodsImgs\": [\"huafer.image.cc\"],\"name\": \"白兰地\",\"brand\": \"白兰地\",\"goodsId\": 898819},\"orderData\": {\"orderId\": 898819,\"status\":1,\"statusTitle\":\"待付款\",\"price\":123,\"state\":2},\"refund\":{\"rid\":1,\"title\":\"买家申请仅退款\",\"userId\":82},\"arbitration\":{\"aid\":1,\"title\":\"仲裁举证期\",\"userId\":82},\"userData\":{\"userName\":\"孙俪\",\"userId\":82,\"avatarUrl\":\"huafer.image.cc\",\"userLevel\":3,\"hasCredit\":1},\"userValue\":{\"zmCreditPoint\":700}},{\"goodsData\": {\"goodsImgs\": [\"huafer.image.cc\"],\"name\": \"白兰地\",\"brand\": \"白兰地\",\"goodsId\": 898819},\"orderData\": {\"orderId\": 898819,\"status\":1,\"statusTitle\":\"待付款\",\"price\":123,\"state\":3},\"refund\":{\"rid\":1,\"title\":\"买家申请仅退款\",\"userId\":82},\"arbitration\":{\"aid\":1,\"title\":\"仲裁举证期\",\"userId\":82},\"userData\":{\"userName\":\"孙俪\",\"userId\":82,\"avatarUrl\":\"huafer.image.cc\",\"userLevel\":3,\"hasCredit\":1},\"userValue\":{\"zmCreditPoint\":700}}]}}";
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if (!isJson) {
                                ptrRecycler.onRefreshComplete();
                                loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                                return;
                            }
                            isFirstRequest = false;
                            ptrRecycler.onRefreshComplete();
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    OrdersResult data = JSON.parseObject(baseResult.obj, OrdersResult.class);
                                    initData(data, extra);
                                }
                            } else {
                                CommonUtils.error(baseResult, getContext(), "");
                            }
                        } catch (Exception e) {
                            LogUtil.e(TAG, "buy crash..." + e.getMessage());
                            ptrRecycler.onRefreshComplete();
                            loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                        }
                    }

                });
    }


    private void initData(OrdersResult datas, String extra) throws Exception {
        if(datas == null) {
            return;
        }
        if (REFRESH.equals(extra)) {//刷新
            if(datas.getHasBatchShipment() == 1) {
                if(adapter.getHeadersCount() == 0) {
                    adapter.addHeaderView(header);
                }
                layoutBatchShip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), OrderBatchShipListActivity.class);
                        startActivityForResult(intent, REQUEST_CODE_ORDERS_LIST);
                    }
                });
            } else {
                adapter.removeHeaders();
            }
            adapter.setData(datas.getOrders(), role, status);
            if(onRefreshOverListener!=null){
                onRefreshOverListener.onComplete(datas.getOrderStatusCounts());
            }
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            List<Orders> data = datas.getOrders();
            if (data == null) {
                data = new ArrayList<>();
            }
            adapter.addAll(data, role, status);
        }
        if (datas.getPage() == 0) {//load完毕
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(footerView);
        }
        adapter.getWrapperAdapter().notifyDataSetChanged();
        page++;
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        page = 0;
        startRequestForGetOrders(role, status, state, REFRESH);
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        startRequestForGetOrders(role, status, state, LOAD_MORE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "onActivityResult(" + requestCode + "," + resultCode + ")");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_ORDERS_LIST) {
                LogUtil.e("lalo", "requestCode:" + requestCode);
                ptrRecycler.setRefreshing();
            }
        }
    }

    public interface OnRefreshOverListener{
        void onComplete(OrdersResult.OrderStatusCounts orderStatusCounts);
    }

    private OnRefreshOverListener onRefreshOverListener;

    public void setOnRefreshOverListener(OnRefreshOverListener onRefreshOverListener) {
        this.onRefreshOverListener = onRefreshOverListener;
    }
}
