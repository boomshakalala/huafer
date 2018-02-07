package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.StaggeredGridAdapter;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.StaggeredResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 我喜欢的列表
 *
 * @author liang_xs
 */
public class LikeListActivity extends BaseActivity
                                                    implements
                                                    PullToRefreshBase.OnRefreshListener<RecyclerView>,
                                                    LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout) PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.loadingStateView) HLoadingStateView loadingStateView;
    private StaggeredGridAdapter adapter;
    private int page = 0;
    private View loadMoreLayout;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);
        ButterKnife.bind(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_ID)) {
            userId = getIntent().getLongExtra(MyConstants.EXTRA_USER_ID, 0);
        }
        if (userId == 0) {
            userId = CommonPreference.getUserId();
        }
        initView();
        startLoading();
    }

    @Override
    public void onTitleBarDoubleOnClick() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void initTitleBar() {
        setTitleString("我收藏的");
    }

    private void initView() {
        ptrFrameLayout.buildPtr(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                int[] firstVisibleItemPositions = manager.findFirstCompletelyVisibleItemPositions(null);
                boolean indexTop = firstVisibleItemPositions.length == manager.getSpanCount();
                for(int i =0;i<manager.getSpanCount();i++){
                    if(firstVisibleItemPositions[i] > manager.getSpanCount()){
                        return false;
                    }
                }
                return indexTop && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);

        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_follow);
        adapter = new StaggeredGridAdapter(this);
        adapter.setEmptyView(viewEmpty);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
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
        startRequestForLikes(LOADING);
    }

    private void refresh() {
        page = 0;
        startRequestForLikes(REFRESH);
    }

    private void loadMore() {
        startRequestForLikes(LOAD_MORE);
    }

    /**
     * 获取关注列表
     *
     * @param
     */
    private void startRequestForLikes(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", userId + "");
        params.put("page", page + "");
        LogUtil.i("lalo", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.I_LIKES, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("lalo", "campaignError:" + e.toString());
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                } else if (extra.equals(LOAD_MORE)) {

                }
            }


            @Override
            public void onResponse(String response) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                } else if (extra.equals(LOAD_MORE)) {

                }
                try {
                    LogUtil.e("LikeListActivity", response);
                    StaggeredResult result = JSON.parseObject(response, StaggeredResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initData(result, extra);
                    } else {
                        CommonUtils.error(result, LikeListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(StaggeredResult result, String extra) {

        if (result.obj.page == 0) {//load完毕
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }

        page++;

        List<Item> list = result.obj.list;

        if (REFRESH.equals(extra)) {//刷新
            adapter.setData(list);
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            if (list == null) {
                list = new ArrayList<>();
            }
            adapter.addData(list);
        } else if (LOADING.equals(extra)) {
            adapter.setData(list);
        }

    }
}
