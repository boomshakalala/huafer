package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
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
import com.huapu.huafen.adapter.MyAuctionAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.MyAuctionBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 拍卖
 * Created by qwe on 2017/8/3.
 */
public class MyAuctionFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>, LoadMoreWrapper.OnLoadMoreListener {

    public PullToRefreshRecyclerView ptrRecycler;
    private ScheduledExecutorService executorService
            = Executors.newSingleThreadScheduledExecutor();
    private int page;
    private View footerView;
    private MyAuctionAdapter adapter;
    private HLoadingStateView loadingStateView;
    private boolean isFirstRequest = true;

    private int pageType = 1;

    public static MyAuctionFragment newInstance(Bundle bundle) {
        MyAuctionFragment commentListFragment = new MyAuctionFragment();
        commentListFragment.setArguments(bundle);
        return commentListFragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_myauction, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        pageType = getArguments().getInt("type");
        initRecyclerView(root);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestList(REFRESH);

        executorService.scheduleAtFixedRate(
                new Task(),
                0,
                1000,
                TimeUnit.MILLISECONDS);
    }

    private void initRecyclerView(View rootView) {
        ptrRecycler = (PullToRefreshRecyclerView) rootView.findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(layoutManager);
        ptrRecycler.setOnRefreshListener(this);
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.load_layout, ptrRecycler.getRefreshableView(), false);
        adapter = new MyAuctionAdapter(getContext(), pageType);
        View viewEmpty = LayoutInflater.from(getContext()).inflate(R.layout.view_empty_image, ptrRecycler.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.no_deposit_data);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());

        ViewUtil.setOffItemAnimator(ptrRecycler.getRefreshableView());

        loadingStateView = (HLoadingStateView) rootView.findViewById(R.id.loadingStateView);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
    }

    private void requestList(final String extra) {
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
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("type", String.valueOf(pageType));
        arrayMap.put("page", String.valueOf(page));
        OkHttpClientManager.postAsyn(MyConstants.MY_AUCTION, arrayMap,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        Logger.e("get response:" + e.getMessage());
                        ProgressDialog.closeProgress();
                        ptrRecycler.onRefreshComplete();
                        loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            Logger.e("get response:" + response);
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
                                    MyAuctionBean myAuctionBean = JSON.parseObject(baseResult.obj, MyAuctionBean.class);
                                    initData(myAuctionBean, extra);
                                }
                            } else {
                                CommonUtils.error(baseResult, getContext(), "");
                            }
                        } catch (Exception e) {
                            ptrRecycler.onRefreshComplete();
                            loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                        }
                    }
                });
    }

    private void initData(MyAuctionBean data, String extra) throws Exception {
        if (REFRESH.equals(extra)) {//刷新
            adapter.setData(data.list);
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            List<Item> goods = data.list;
            if (goods == null) {
                goods = new ArrayList<>();
            }
            adapter.addAll(goods);
        }
        if (data.page == 0) {//load完毕
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(footerView);
        }
        adapter.getWrapperAdapter().notifyDataSetChanged();
        page++;
    }

    @Override
    public void onLoadMoreRequested() {
        requestList(LOAD_MORE);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        page = 0;
        requestList(REFRESH);
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            if (null != adapter) {
                if (adapter.getItemCount() > 0) {
                    ptrRecycler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyWrapperDataSetChanged();
                        }
                    });
                }

            }
        }
    }
}
