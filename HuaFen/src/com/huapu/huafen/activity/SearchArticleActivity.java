package com.huapu.huafen.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ShopkeepersAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.beans.ShopkeepersData;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;

/**
 * Created by Think on 2017/5/10.
 */

public class SearchArticleActivity extends BaseActivity implements LoadMoreWrapper.OnLoadMoreListener, PullToRefreshBase.OnRefreshListener<RecyclerView> {

    @BindView(R.id.tvSwitch)
    TextView tvSwitch;
    @BindView(R.id.layoutSwitch)
    LinearLayout layoutSwitch;
    @BindView(R.id.ivSearch)
    ImageView ivSearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.btnSearchRight)
    FrameLayout btnSearchRight;
    @BindView(R.id.layoutTitle)
    RelativeLayout layoutTitle;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.searchHistory)
    PullToRefreshRecyclerView ptrRecycler;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    @BindView(R.id.image_finish)
    ImageView imageFinish;
    @BindView(R.id.tvUnRead)
    TextView tvUnRead;
    private ShopkeepersAdapter shopkeepersAdapter;
    private View loadMoreView;
    private int page;
    private int order = -3;
    private boolean isCanLoadMore;
    private TextView hot;
    private TextView time;
    private String keyword;
    private TitleBarNew titleBar;

    private PopupWindow morePopupWindow;
    private TextView tvMsgUnRead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowersearch);
        ButterKnife.bind(this);
        initView();
        startRequestForGetRecommendData();
    }

    private void initView() {
        layoutTitle.setVisibility(View.GONE);
        titleBar = (TitleBarNew) findViewById(R.id.titleBar);
        keyword = getIntent().getStringExtra(MyConstants.EXTRA_SEARCH_KEYWORD);
        initTitle();

        btnSearchRight.setOnClickListener(this);
        shopkeepersAdapter = new ShopkeepersAdapter(this);

        //加载更多布局
        loadMoreView = LayoutInflater.from(this).inflate(R.layout.load_layout, ptrRecycler, false);
        //设置布局管理者为瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(layoutManager);
        CommonUtils.buildPtr(ptrRecycler);

        //搜索为空时
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image,
                ptrRecycler.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.article_se);
        shopkeepersAdapter.setEmptyView(viewEmpty);

        //初始化 加载更多 布局
        loadMoreView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadMoreView.setPadding(CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(25));
        ptrRecycler.setAdapter(shopkeepersAdapter.getWrapperAdapter());

        //设置下拉刷新，加载更多监听器
        ptrRecycler.setOnRefreshListener(this);
        shopkeepersAdapter.setOnLoadMoreListener(this);
        //排序
        View header = LayoutInflater.from(this).inflate(R.layout.item_order, ptrRecycler, false);
        hot = (TextView) header.findViewById(R.id.hot);
        time = (TextView) header.findViewById(R.id.time);
        //将排序布局加入头部
        shopkeepersAdapter.addHeaderView(header);
        hot.setSelected(true);
        time.setSelected(false);
        hot.setOnClickListener(this);
        time.setOnClickListener(this);
        imageFinish.setOnClickListener(this);
    }

    private void initTitle() {
        View searchTitle = LayoutInflater.from(this).inflate(R.layout.search_flower_article, null);
        TextView tv = (TextView) searchTitle.findViewById(R.id.tv);
        LinearLayout llTextSearch = (LinearLayout) searchTitle.findViewById(R.id.llTextSearch);
        tv.setText(keyword);
        llTextSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleBar.setTitle(searchTitle);
        titleBar.setOnRightButtonClickListener(R.drawable.gray_point, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initPopMore(v);
            }
        });

    }


    /**
     * 从服务端获取推荐列表
     */
    public void startRequestForGetRecommendData() {
        startRequestForFollowing(REFRESH);
    }

    //下拉刷新
    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        refresh();
    }

    /**
     * 刷新
     */
    private void refresh() {
        //初始化页数，进行网络请求
        page = 0;
        startRequestForFollowing(REFRESH);
    }

    //加载更多
    @Override
    public void onLoadMoreRequested() {
        if (isCanLoadMore) {
            startRequestForFollowing(LOAD_MORE);
        }
    }

    /**
     * 网络请求
     *
     * @param extra
     */
    public void startRequestForFollowing(final String extra) {
        //判断网络是否可用
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            //加载更多完成
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            //进行下拉刷新
            if (ptrRecycler != null) {
                ptrRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrRecycler.setRefreshing(true);
                    }
                }, 1000);
            }
            return;
        }
        //如果下拉刷新，页数置为0
        if (extra.equals(REFRESH)) {
            page = 0;
        }

        //网络参数
        final HashMap<String, String> params = new HashMap<String, String>();
//        params.put("userId", 80 + "");
        params.put("keywords", keyword);
        params.put("page", page + "");
        params.put("orderBy", order + "");
        OkHttpClientManager.postAsyn(MyConstants.SEARCHARTICLESQUARE, params, new OkHttpClientManager.StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("XJJJJJJJ", "花语广场error:" + e.toString());
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecycler.onRefreshComplete();
                } else if (extra.equals(LOAD_MORE)) {

                }

            }

            @Override
            public void onResponse(String response) {
                try {

                    if (extra.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    } else if (extra.equals(REFRESH)) {
                        ptrRecycler.onRefreshComplete();
                    } else if (extra.equals(LOAD_MORE)) {

                    }
                    LogUtil.e("liang", "花语广场:~~" + response.toString());
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
                        // TODO: 17/12/22
                        CommonUtils.error(baseResult, SearchArticleActivity.this, "");
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.hot:
                order = -3;
                refresh();
                time.setSelected(false);
                hot.setSelected(true);
                break;
            case R.id.time:
                order = -2;
                refresh();
                time.setSelected(true);
                hot.setSelected(false);
                break;
//            case R.id.tv:
//                finish();
//                break;
            case R.id.btnSearchRight:
                initPopMore(v);
                break;
            case R.id.image_finish:
                finish();
                break;
        }

    }

    private void initPopMore(View v) {
        if (morePopupWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            View layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            View layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            View layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            View layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);
            layoutSwitchReport.setVisibility(View.GONE);
            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            layoutSwitchHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchMsg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    morePopupWindow.dismiss();
                }
            });
            layoutSwitchMine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    morePopupWindow.dismiss();
                }
            });

            morePopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            morePopupWindow.setFocusable(true);
            morePopupWindow.setOutsideTouchable(true);
            morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            morePopupWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvMsgUnRead.setVisibility(titleBar.getMoreBtnBadgeVisibility() ? View.VISIBLE : View.GONE);
        morePopupWindow.showAsDropDown(v);
    }

    private void actionToMineFragment(int selectFragment) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
    }

    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateUnreadBadge();
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateUnreadBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadBadge();
    }

    private void updateUnreadBadge() {
        titleBar.showMoreBtnBadge(IMUtils.hasUnread());
    }
}
