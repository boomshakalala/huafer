package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.UserSearchResultAdapter;
import com.huapu.huafen.adapter.VUserAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.RecommendUsers;
import com.huapu.huafen.beans.SuggestUsersBean;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;

/**
 * 用户搜索结果
 *
 * @author liang_xs
 */
public class SearchPersonalListActivity extends BaseActivity
        implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R2.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R2.id.ptrRecyclerSearchResult)
    PullToRefreshRecyclerView ptrRecyclerSearchResult;
    @BindView(R2.id.loadingStateView)
    HLoadingStateView loadingStateView;

    @BindView(R2.id.noDataRecycleView)
    RecyclerView noDataRecycleView;
    private VUserAdapter adapter;
    private String keyword;
    private int page = 0;
    private View loadMoreLayout;
    private TextView tvMsgUnRead;

    private UserSearchResultAdapter userSearchResultAdapter;
    private PopupWindow morePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_personal_list_new);
        EventBus.getDefault().register(this);
        if (getIntent().hasExtra(MyConstants.EXTRA_SEARCH_KEYWORD)) {
            keyword = getIntent().getStringExtra(MyConstants.EXTRA_SEARCH_KEYWORD);
        }
        initView();
        startLoading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取单品类别列表
     *
     * @param
     */

    private void initView() {
        initTitle();
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        ptrRecyclerSearchResult = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecyclerSearchResult);
        CommonUtils.buildPtr(ptrRecyclerSearchResult);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecyclerSearchResult.getRefreshableView().setLayoutManager(linearLayoutManager);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout,
                ptrRecyclerSearchResult.getRefreshableView(), false);
        adapter = new VUserAdapter(this);
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.view_empty_image,
                ptrRecyclerSearchResult.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.search_empty);
        adapter.setEmptyView(viewEmpty);
        ptrRecyclerSearchResult.setAdapter(adapter.getWrapperAdapter());
        ptrRecyclerSearchResult.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(noDataRecycleView.getContext(), LinearLayoutManager.VERTICAL, false);
        noDataRecycleView.setLayoutManager(layoutManager);
        userSearchResultAdapter = new UserSearchResultAdapter(this);
        noDataRecycleView.setAdapter(userSearchResultAdapter.getWrapperAdapter());

    }

    private void initTitle() {
        View searchTitle = LayoutInflater.from(this).inflate(R.layout.search_rect_left, null);
        TextView textView = (TextView) searchTitle.findViewById(R.id.tv);
        textView.setText(keyword);
        searchTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("keyword", keyword);
                setResult(RESULT_OK, intent);
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
        Intent intent = new Intent(SearchPersonalListActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
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
        startRequestForSearchResult(LOADING);
    }

    private void refresh() {
        page = 0;
        startRequestForSearchResult(REFRESH);
    }

    private void loadMore() {
        startRequestForSearchResult(LOAD_MORE);
    }

    /**
     * 获取查询用户结果列表
     *
     * @param
     */
    private void startRequestForSearchResult(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("keyword", keyword);
        params.put("page", page + "");
        params.put("pageNum", "12");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SEARCH_USERS_LIST, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecyclerSearchResult.onRefreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecyclerSearchResult.onRefreshComplete();
                }
                try {
                    LogUtil.i("liang", "查询用户结果列表:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        UserListResult result = JSON.parseObject(baseResult.obj, UserListResult.class);
                        if (null != result && null != result.getUsers() && result.getUsers().size() > 0) {
                            initData(result, extra);
                            ptrRecyclerSearchResult.setVisibility(View.VISIBLE);
                            noDataRecycleView.setVisibility(View.GONE);
                        } else {
                            RecommendUsers recommendUsers = JSON.parseObject(baseResult.obj, RecommendUsers.class);
                            ptrRecyclerSearchResult.setVisibility(View.GONE);
                            noDataRecycleView.setVisibility(View.VISIBLE);
                            SuggestUsersBean usersBean = JSON.parseObject(response, SuggestUsersBean.class);
                            View goodsHeader = LayoutInflater.from(SearchPersonalListActivity.this).inflate(R.layout.layout_goods_search_nodata, noDataRecycleView, false);
                            TextView textView = (TextView) goodsHeader.findViewById(R.id.titleInfo);
                            textView.setText("-没有找到相关用户-");
                            TextView personRecommand = (TextView) goodsHeader.findViewById(R.id.personRecommand);
                            personRecommand.setVisibility(View.VISIBLE);
                            userSearchResultAdapter.removeHeaders();
                            userSearchResultAdapter.addHeaderView(goodsHeader);
                            userSearchResultAdapter.setKeyWord(keyword);
//                            userSearchResultAdapter.setData(usersBean.obj.recUsers);
                            userSearchResultAdapter.setData(recommendUsers);
                        }

                    } else {
                        // TODO: 17/12/22
                        CommonUtils.error(baseResult, SearchPersonalListActivity.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }

        });
    }

    private void initData(UserListResult result, String extra) {
        if (result.page == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }
        page++;

        List<User> users = result.getUsers();
        if (extra.equals(LOADING)) {
            adapter.setData(users);
        } else if (extra.equals(REFRESH)) {
            adapter.setData(users);
        } else if (LOAD_MORE.equals(extra)) {
            if (users == null) {
                users = new ArrayList<>();
            }
            adapter.addAll(users);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL) {
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    int type = data.getIntExtra("type", 0);
                    if (position != -1 && type != 0) {
                        adapter.updateFollowState(position, type);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
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
