package com.huapu.huafen.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.OrderListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.OrderListBean;
import com.huapu.huafen.beans.OrderListResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.leancloud.chatkit.LCChatKit;
import de.greenrobot.event.EventBus;

/**
 * 订单消息列表
 *
 * @author liang_xs
 */
public class OrderListActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R.id.ptrRecyclerView)
    PullToRefreshRecyclerView ptrRecyclerView;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    private int page;
    private OrderListAdapter adapter;
    private View loadMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_new);

        CommonUtils.buildPtr(ptrRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecyclerView.getRefreshableView().setLayoutManager(layoutManager);
        ptrRecyclerView.getRefreshableView().setBackgroundResource(R.drawable.wallet_recycler_view_fucking_bg);

        adapter = new OrderListAdapter(this);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
                ptrRecyclerView.getRefreshableView(), false);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, ptrRecyclerView.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_order_message);
        adapter.setEmptyView(viewEmpty);

        ptrRecyclerView.setAdapter(adapter.getWrapperAdapter());

        ptrRecyclerView.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);

        ptrRecyclerView.getRefreshableView().addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://空闲状态
                        ImageLoader.resumeLoadImage();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://自动滚
                        ImageLoader.pauseLoadImage();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        startLoading();
    }

    @Override
    public void initTitleBar() {
        getTitleBar().setTitle("订单消息");
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        page = 0;
        startRequestForGetOrderList(LOADING);
        try {
            LCChatKit.getInstance().getClient().getConversation(MyConstants.CONV_ORDER_ID).read();
        } catch (Exception e) {
            LogUtil.d("danielluan", e.getMessage());
        }
        this.startRequestForAllRead();
    }

    private void refresh() {
        page = 0;
        startRequestForGetOrderList(REFRESH);
    }

    private void loadMore() {
        startRequestForGetOrderList(LOAD_MORE);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        refresh();
    }

    @Override
    public void onLoadMoreRequested() {
        LogUtil.i("OrderListActivity", "加载更多");
        loadMore();
    }

    private void startRequestForGetOrderList(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("page", String.valueOf(page));
        LogUtil.i("liang", "parmas:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GETORDERLOG, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecyclerView.onRefreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", " ---订单消息列表:" + response);
                if (LOADING.equals(extra)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (REFRESH.equals(extra)) {
                    ptrRecyclerView.onRefreshComplete();
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
//                            ptrRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
                            OrderListResult result = JSON.parseObject(baseResult.obj, OrderListResult.class);
                            initData(result, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(OrderListResult result, String extra) {
        if (result.page == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }

        page++;

        List<OrderListBean> orderLogList = result.orderLogList;
        LogUtil.i("OrderListActivity", "列表size" + orderLogList.size());
        if (extra.equals(LOADING)) {
            adapter.setData(orderLogList);
        } else if (extra.equals(REFRESH)) {
            adapter.setData(orderLogList);
        } else if (LOAD_MORE.equals(extra)) {
            if (orderLogList == null) {
                orderLogList = new ArrayList<>();
            }
            adapter.addAll(orderLogList);
        }

        if (adapter.hasUnreadMessage()) {
            getTitleBar().getTitleTextRight().setEnabled(true);
            getTitleBar().getTitleTextRight().setTextColor(getResources().getColorStateList(R.color.text_pink_pink45_selector));
        } else {
            getTitleBar().getTitleTextRight().setEnabled(false);
            getTitleBar().getTitleTextRight().setTextColor(Color.parseColor("#cccccc"));
        }
    }

    private void startRequestForAllRead() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        OkHttpClientManager.postAsyn(MyConstants.BATCH_ORDER_READ, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", " ---订单消息列表:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (adapter != null) {
                            adapter.readAll();
                            if (adapter.hasUnreadMessage()) {
                                getTitleBar().getTitleTextRight().setEnabled(true);
                                getTitleBar().getTitleTextRight().setTextColor(getResources().getColorStateList(R.color.text_pink_pink45_selector));
                            } else {
                                getTitleBar().getTitleTextRight().setEnabled(false);
                                getTitleBar().getTitleTextRight().setTextColor(Color.parseColor("#cccccc"));
                                MyConstants.UNREAD_ORDER_COUNT = 0;
                                MessageUnReadCountEvent event = new MessageUnReadCountEvent();
                                event.isUpdate = true;
                                EventBus.getDefault().post(event);
                            }
                        }
                    } else {
                        CommonUtils.error(baseResult, OrderListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
