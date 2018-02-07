package com.huapu.huafen.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ShopkeepersAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.beans.ShopkeepersData;
import com.huapu.huafen.common.MyConstants;
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

/**
 * 花语广场
 * Created by xk on 2017/5/9.
 */
public class SquareFragment extends BaseFragment implements LoadMoreWrapper.OnLoadMoreListener, PullToRefreshBase.OnRefreshListener<RecyclerView>, View.OnClickListener {

    private View view;
    private PullToRefreshRecyclerView recyclerView;
    private HLoadingStateView loadingStateView;
    private boolean isUnloaded;
    private ShopkeepersAdapter shopkeepersAdapter;
    private View loadMoreView;
    private boolean isCanLoadMore;
    private int cat;
    private int page;
    private int order = -3;
    private boolean isFirstRequest = true;
    private TextView hot;
    private TextView time;
    private View viewEmpty;
    private Bundle arguments;


    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        view = inflater.inflate(R.layout.item_square_viewpager, null);

        //获取当前页数
        arguments = getArguments();
        cat = arguments.getInt("cat");
        initView();
        initData();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        startRequestForGetRecommendData();
        boolean isFirstLoad = arguments.getBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD_ARTICLE, false);
        if (isFirstLoad) {
            initRequest();
        }
    }

    /**
     * 从服务端获取推荐列表
     */
    public void startRequestForGetRecommendData() {
        startRequestForFollowing(REFRESH);
    }

    /**
     * 初始化控件
     */
    private void initView() {

        recyclerView = (PullToRefreshRecyclerView) view.findViewById(R.id.item_square_viewpager_recycler);
        loadingStateView = (HLoadingStateView) view.findViewById(R.id.loadingStateView);
        //加载更多布局
        loadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.load_layout, recyclerView, false);
    }


    //调用多个fragment时的单个请求网络
    @Override
    protected void loadResponse() {
        super.loadResponse();
        initRequest();

    }

    /**
     * 初始化数据
     */
    private void initData() {

        //设置布局管理者为瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.getRefreshableView().setLayoutManager(layoutManager);
        CommonUtils.buildPtr(recyclerView);

        //初始化 加载更多 布局
        loadMoreView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadMoreView.setPadding(CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(25));

        shopkeepersAdapter = new ShopkeepersAdapter(getActivity());
        //加载商品为空布局
        viewEmpty = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_follow);

        recyclerView.setAdapter(shopkeepersAdapter.getWrapperAdapter());

        //设置下拉刷新，加载更多监听器
        recyclerView.setOnRefreshListener(this);
        shopkeepersAdapter.setOnLoadMoreListener(this);

        //排序
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_order, recyclerView, false);
        hot = (TextView) header.findViewById(R.id.hot);
        time = (TextView) header.findViewById(R.id.time);

        if (cat != 0) {
            //将排序布局加入头部
            shopkeepersAdapter.addHeaderView(header);
            hot.setSelected(true);
            time.setSelected(false);

            hot.setOnClickListener(this);
            time.setOnClickListener(this);
        }


    }


    private void initRequest() {
        if (shopkeepersAdapter != null && shopkeepersAdapter.isEmpty() && isFirstRequest) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
            startRequestForGetRecommendData();
        }
    }

    /**
     * 刷新
     */
    private void refresh() {
        //初始化页数，进行网络请求
        page = 0;
        startRequestForFollowing(REFRESH);
    }


    /**
     * 网络请求
     *
     * @param extra
     */
    public void startRequestForFollowing(final String extra) {

        //判断网络是否可用
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            ToastUtil.toast(getActivity(), "请检查网络连接");
            //加载更多完成
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            //进行下拉刷新
            if (recyclerView != null) {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setRefreshing(true);
                    }
                }, 1000);
            }
            isUnloaded = true;
            return;
        }
        //如果下拉刷新，页数置为0
        if (extra.equals(REFRESH)) {
            page = 0;
        }

        //网络参数
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("page", page + "");//页数

        if (cat != 0) {
            params.put("cat", cat + "");//分类
            params.put("orderBy", order + "");//页数
        }
        OkHttpClientManager.postAsyn(MyConstants.ARTICLESQUARE, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("XKKKKK", "花语广场error:" + e.toString());
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    isUnloaded = true;
                } else if (extra.equals(REFRESH)) {
                    recyclerView.onRefreshComplete();
                } else if (extra.equals(LOAD_MORE)) {

                }

            }


            @Override
            public void onResponse(String response) {
                try {

                    if (extra.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        isUnloaded = false;
                    } else if (extra.equals(REFRESH)) {
                        recyclerView.onRefreshComplete();
                    } else if (extra.equals(LOAD_MORE)) {

                    }
                    LogUtil.e("liang", "花语广场:~~" + response.toString());
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    isFirstRequest = false;
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


    /**
     * 加载数据
     *
     * @param data
     * @param extra
     */
    private void initData(ShopkeepersData data, String extra) {
        if (data == null) {
            shopkeepersAdapter.setEmptyView(viewEmpty);
        } else {
            //显示加载更多布局
            shopkeepersAdapter.setLoadMoreView(loadMoreView);

            if (data.getPage() == 0) {//load完毕
                loadMoreView.setVisibility(View.INVISIBLE);
            } else {
                loadMoreView.setVisibility(View.VISIBLE);
            }

            isCanLoadMore = data.getPage() == 0 ? false : true;
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
        }


    }


    /**
     * 下拉刷新
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        refresh();
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        if (isCanLoadMore) {
            startRequestForFollowing(LOAD_MORE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hot:
                order = -3;
                time.setSelected(false);
                hot.setSelected(true);
                break;
            case R.id.time:
                order = -2;
                time.setSelected(true);
                hot.setSelected(false);
                break;
        }
        recyclerView.setRefreshing();
    }


}
