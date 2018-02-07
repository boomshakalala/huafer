package com.huapu.huafen.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HomeAttentionNoDataAdapter;
import com.huapu.huafen.adapter.ShopkeepersAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.FlowerData;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.beans.ShopkeepersData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.scrollablelayoutlib.ScrollableHelper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 首页关注的商品列表
 * Created by admin on 2016/12/10.
 */
public class FollowGoodsFragment extends ScrollAbleFragment implements
        ScrollableHelper.ScrollableContainer,
        LoadMoreWrapper.OnLoadMoreListener {

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;

    //带下拉刷新、上拉加载更多的listview
    private RecyclerView ptrRecycler;
    private RecyclerView ptrRecyclerNoData;
    private HLoadingStateView loadingStateView;

    public ShopkeepersAdapter shopkeepersAdapter;
    private HomeAttentionNoDataAdapter noDataAdapter;
    public boolean isUnloaded = true;
    private View view;
    private int page = 0;
    private String snapshotId;
    private View loadMoreView;
    private boolean isCanLoadMore;
    private FlowerData flowerData;
    private OnFollowGoodsPullListener onFollowGoodsPullListener = null;

    public static FollowGoodsFragment newInstance() {
        FollowGoodsFragment listFragment = new FollowGoodsFragment();
        return listFragment;
    }

    public static FollowGoodsFragment newInstance(Bundle data) {
        FollowGoodsFragment listFragment = new FollowGoodsFragment();
        listFragment.setArguments(data);
        return listFragment;
    }

    public void listViewToTop() {
        if (ptrRecycler != null) {
            ptrRecycler.scrollToPosition(0);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_follow_goods, container, false);
        //初始化控件
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            boolean isFirst = data.getBoolean("isFirst");
            if (isFirst) {
                startLoading();
            }
        }
    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        if (shopkeepersAdapter != null && shopkeepersAdapter.isEmpty() && isUnloaded) {
            startLoading();
        }
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        startRequestForFollowing(LOADING);
    }

    private void initViews() {
        ptrRecycler = (RecyclerView) view.findViewById(R.id.ptrRecycler);
        ptrRecyclerNoData = (RecyclerView) view.findViewById(R.id.ptrRecyclerNoData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ptrRecyclerNoData.getContext(), LinearLayoutManager.VERTICAL, false);
        ptrRecyclerNoData.setLayoutManager(linearLayoutManager);
        noDataAdapter = new HomeAttentionNoDataAdapter(getContext(), ptrRecyclerNoData);
        ptrRecyclerNoData.setAdapter(noDataAdapter);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.layout_goods_search_nodata, ptrRecyclerNoData, false);
        TextView textView = (TextView) header.findViewById(R.id.titleInfo);
        textView.setText("-您可能感兴趣的店主-");
        View diverView = header.findViewById(R.id.divider_line);
        diverView.setVisibility(View.GONE);
        noDataAdapter.setAutoLoadMoreEnable(false);
        noDataAdapter.setHeaderEnable(true);
        noDataAdapter.addHeaderView(header);

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        ptrRecycler.setLayoutManager(layoutManager);
        // ptrRecycler.addItemDecoration(new StaggeredGridDecoration(ViewUtil.getTenDp()));
        ptrRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                layoutManager.invalidateSpanAssignments();
            }
        });
        ptrRecycler.setItemViewCacheSize(12);

        ViewUtil.setOffItemAnimator(ptrRecycler);
        ViewUtil.setOffItemAnimator(ptrRecyclerNoData);

        loadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.load_layout, ptrRecycler, false);
        loadMoreView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadMoreView.setPadding(CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(25));
        shopkeepersAdapter = new ShopkeepersAdapter(getActivity());

        View viewEmpty = ViewUtil.initImgEmptyView(ptrRecycler, R.drawable.follow_empty);

        shopkeepersAdapter.setEmptyView(viewEmpty);
        shopkeepersAdapter.setOnLoadMoreListener(this);
        ptrRecycler.setAdapter(shopkeepersAdapter.getWrapperAdapter());
        loadingStateView = (HLoadingStateView) view.findViewById(R.id.loadingStateView);
    }

    @Override
    public void onLoadMoreRequested() {
        if (isCanLoadMore) {
            startRequestForFollowing(LOAD_MORE);
        }
    }

    public void startRequestForFollowing(final String extra) {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            ToastUtil.toast(getActivity(), "请检查网络连接");
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            if (ptrRecycler != null) {
                ptrRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onFollowGoodsPullListener != null) {
                            onFollowGoodsPullListener.onRecommendPull();
                        }
                    }
                }, 1000);
            }
            isUnloaded = true;
            return;
        }
        if (extra.equals(REFRESH)) {
            page = 0;
        }

        HashMap<String, String> params = new HashMap<>();

        params.put("page", String.valueOf(page));
        if (snapshotId != null) {
            params.put("snapshotId", snapshotId);
        }
        OkHttpClientManager.postAsyn(MyConstants.FOLLOWINGLIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    isUnloaded = true;
                } else if (extra.equals(REFRESH)) {
                    if (onFollowGoodsPullListener != null) {
                        onFollowGoodsPullListener.onRecommendPull();
                    }
                } else if (extra.equals(LOAD_MORE)) {

                }

            }

            @Override
            public void onResponse(String response) {
                try {
                    Logger.e("get response:" + response);
                    if (extra.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        isUnloaded = false;
                    } else if (extra.equals(REFRESH)) {
                        if (onFollowGoodsPullListener != null) {
                            onFollowGoodsPullListener.onRecommendPull();
                        }
                    } else if (extra.equals(LOAD_MORE)) {

                    }
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            ShopkeepersData data = JSON.parseObject(baseResult.obj, ShopkeepersData.class);
                            initData(data, extra);

                        }
                    } else {
                        CommonUtils.error(baseResult, getActivity(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(ShopkeepersData data, String extra) {
        if (data.getSnapshotId() != null) {
            snapshotId = data.getSnapshotId();
        }
        if (null != data.getList() || data.getPage() != 0) {
            if (null != data.getList() && data.getList().size() > 0) {
                ptrRecyclerNoData.setVisibility(View.GONE);
                ptrRecycler.setVisibility(View.VISIBLE);
                shopkeepersAdapter.setLoadMoreView(loadMoreView);
                if (data.getPage() == 0) {//load完毕
                    loadMoreView.setVisibility(View.INVISIBLE);
                } else {
                    loadMoreView.setVisibility(View.VISIBLE);
                }

                isCanLoadMore = data.getPage() != 0;
                page++;

                ArrayList<ShopArticleData> list = data.getList();

                if (REFRESH.equals(extra)) {//刷新
                    shopkeepersAdapter.setData(list);
                } else if (LOAD_MORE.equals(extra)) {//加载更多
                    shopkeepersAdapter.addDatas(list);
                } else if (LOADING.equals(extra)) {
                    shopkeepersAdapter.addDatas(list);
                }

                shopkeepersAdapter.notifyWrapperDataSetChanged();
            } else {
                ptrRecyclerNoData.setVisibility(View.VISIBLE);
                ptrRecycler.setVisibility(View.GONE);
                noDataAdapter.setData(data.getVipList(), data.getStarList());
            }
        }

    }

    @Override
    public View getScrollableView() {
        if (ptrRecycler.getVisibility() == View.VISIBLE) {
            return ptrRecycler;
        } else {
            return ptrRecyclerNoData;
        }

    }

    /**
     * 从服务端获取推荐列表
     */
    public void startRequestForGetRecommendData() {
        page = 0;
        startRequestForFollowing(REFRESH);
    }

    public void setOnFollowGoodsPullListener(OnFollowGoodsPullListener onFollowGoodsPullListener) {
        this.onFollowGoodsPullListener = onFollowGoodsPullListener;
    }

    public interface OnFollowGoodsPullListener {

        void onRecommendPull();

    }
}
