package com.huapu.huafen.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.AbstractRecyclerAdapter;
import com.huapu.huafen.adapter.NewStarAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.NewStarUserBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 全部明星，全部VIP
 * Created by qwe on 2017/5/19.
 */
public class NewStarListActivity extends BaseActivity implements AbstractRecyclerAdapter.LoadMoreListener {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;

    private NewStarAdapter newStarAdapter;

    private int page = 0;

    private int orderBy = 3;

    private boolean isVIP = false;
    private String snapshotId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_star);
        ButterKnife.bind(this);

        if (getIntent().hasExtra("VIP")) {
            if (getIntent().getStringExtra("VIP").equals("VIP")) {
                isVIP = true;
            }
        }
        initView();
    }

    private void initView() {
        if (isVIP) {
            getTitleBar().setTitle("全部VIP");
        } else {
            getTitleBar().setTitle("全部明星");
        }

        initPtr();
        GridLayoutManager linearLayoutManager = new GridLayoutManager(this, 1);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        newStarAdapter = new NewStarAdapter(this, recyclerView);
        newStarAdapter.setLoadMoreListener(this);
        recyclerView.setAdapter(newStarAdapter);
        ViewUtil.setOffItemAnimator(recyclerView);

        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_star_header, null, false);
        final TextView fansText = ButterKnife.findById(headerView, R.id.fans);
        final TextView activeUserText = ButterKnife.findById(headerView, R.id.activeUser);
        final TextView newJoin = ButterKnife.findById(headerView, R.id.newJoin);
        fansText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 1;
                fansText.setTextColor(Color.parseColor("#FF6677"));
                activeUserText.setTextColor(Color.parseColor("#333333"));
                newJoin.setTextColor(Color.parseColor("#333333"));
                newStarAdapter.clearAllData();
                refresh();
            }
        });
        activeUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 2;
                fansText.setTextColor(Color.parseColor("#333333"));
                newJoin.setTextColor(Color.parseColor("#333333"));
                activeUserText.setTextColor(Color.parseColor("#FF6677"));
                newStarAdapter.clearAllData();
                refresh();
            }
        });

        newJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderBy = 3;
                fansText.setTextColor(Color.parseColor("#333333"));
                newJoin.setTextColor(Color.parseColor("#FF6677"));
                activeUserText.setTextColor(Color.parseColor("#333333"));
                newStarAdapter.clearAllData();
                refresh();
            }
        });


        newStarAdapter.setHeaderEnable(true);
        newStarAdapter.addHeaderView(headerView);
        newStarAdapter.setAutoLoadMoreEnable(false);

        refresh();
    }

    public int getOrderBy() {
        return orderBy;
    }

    public void initPtr() {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int index = manager.findFirstVisibleItemPosition();
                return index == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });
        PtrAnimationBackgroundHeader header = new PtrAnimationBackgroundHeader(this);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        // the following are default settings
        ptrFrameLayout.setResistance(1.7f);
        ptrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrameLayout.setDurationToClose(200);
        ptrFrameLayout.setDurationToCloseHeader(300);
        // default is false
        ptrFrameLayout.setPullToRefresh(false);
        // default is true
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);

    }

    private void refresh() {
        if (page > 0) {
            newStarAdapter.initLoading(false);
        }
        newStarAdapter.setAutoLoadMoreEnable(false);
        page = 0;
        doRequest(REFRESH);
    }

    private void doRequest(final String state) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            return;
        }

        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("orderBy", String.valueOf(orderBy));
        if (snapshotId != null) {
            arrayMap.put("snapshotId", snapshotId);
        }
        arrayMap.put("page", String.valueOf(page));
        String requestURL;
        if (isVIP) {
            requestURL = MyConstants.NEW_VIP_LIST;
        } else {
            requestURL = MyConstants.NEW_STRT_LIST;
        }
        OkHttpClientManager.postAsyn(requestURL, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
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

                    if (state.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    } else if (state.equals(REFRESH)) {
                        ptrFrameLayout.refreshComplete();
                    } else if (state.equals(LOAD_MORE)) {
                        newStarAdapter.notifyMoreFinish();
                    }
                    Logger.e("get response:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            NewStarUserBean newStarUserBean = JSON.parseObject(baseResult.obj, NewStarUserBean.class);
                            initData(newStarUserBean, state);
                        }
                    } else {
                        CommonUtils.error(baseResult, NewStarListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initData(NewStarUserBean regionBean, String state) {
        if (regionBean.snapshotId != null) {
            snapshotId = regionBean.snapshotId;
        }
        if (regionBean.page == 0) {
            newStarAdapter.setAutoLoadMoreEnable(false);
//            newStarAdapter.loadFinish(true);
        } else {
            newStarAdapter.setAutoLoadMoreEnable(true);
        }

        page++;


        if (state.equals(LOADING)) {
            newStarAdapter.setRes(regionBean.list);
        } else if (state.equals(REFRESH)) {
            newStarAdapter.setRes(regionBean.list);
        } else if (LOAD_MORE.equals(state)) {
            if (regionBean.page == 0) {
                newStarAdapter.notifyDataSetChanged();
            } else {
                newStarAdapter.appendData(regionBean.list);
            }

        }
    }

    @Override
    public void onLoadMore() {
        doRequest(LOAD_MORE);
    }
}
