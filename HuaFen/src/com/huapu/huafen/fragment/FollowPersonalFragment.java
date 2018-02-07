package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.adapter.VUserAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.scrollablelayoutlib.ScrollableHelper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/12/10.
 */
public class FollowPersonalFragment extends ScrollAbleFragment
        implements
        ScrollableHelper.ScrollableContainer,
        LoadMoreWrapper.OnLoadMoreListener {

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;

    private View view;
    //带下拉刷新、上拉加载更多的listview
    private RecyclerView ptrRecycler;
    private int page = 0;
    private VUserAdapter adapter;
    private View loadMoreView;
    private HLoadingStateView loadingStateView;
    private boolean isCanLoadMore;

    public static FollowPersonalFragment newInstance() {
        FollowPersonalFragment listFragment = new FollowPersonalFragment();
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
        startLoading();
        return view;
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        startRequestForFansList(LOADING);
    }

    private void initViews() {
        ptrRecycler = (RecyclerView) view.findViewById(R.id.ptrRecycler);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.setLayoutManager(layoutManager);
        ViewUtil.setOffItemAnimator(ptrRecycler);

        loadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.load_layout, ptrRecycler, false);
        loadMoreView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadMoreView.setPadding(CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(12), CommonUtils.dp2px(25));
        adapter = new VUserAdapter(this);

        View viewEmpty = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty_image, ptrRecycler, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.follow_empty);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());
        loadingStateView = (HLoadingStateView) view.findViewById(R.id.loadingStateView);
    }

    @Override
    public void onLoadMoreRequested() {
        if (isCanLoadMore) {
            startRequestForFansList(LOAD_MORE);
        }
    }

    /**
     * 获取粉丝列表
     *
     * @param
     */
    public void startRequestForFansList(final String extra) {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            toast("请检查网络连接");
            if (ptrRecycler != null) {
                ptrRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onFollowPersonalPullListener != null) {
                            onFollowPersonalPullListener.onRecommendPull();
                        }
                    }
                }, 1000);
            }
            return;
        }
        if (extra.equals(REFRESH)) {
            page = 0;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", String.valueOf(CommonPreference.getUserId()));
        params.put("page", page + "");
        params.put("pageNum", "12");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.FOLLOWING_LIST, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    if (onFollowPersonalPullListener != null) {
                        onFollowPersonalPullListener.onRecommendPull();
                    }
                }
            }

            @Override
            public void onResponse(String response) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    if (onFollowPersonalPullListener != null) {
                        onFollowPersonalPullListener.onRecommendPull();
                    }
                }
                try {
                    LogUtil.i("liang", "followPersonal:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        UserListResult result = JSON.parseObject(baseResult.obj, UserListResult.class);
                        initData(result, extra);
                    } else {
                        CommonUtils.error(baseResult, getActivity(), "");
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
            adapter.setLoadMoreView(loadMoreView);
        }
        isCanLoadMore = result.page == 0 ? false : true;
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
    public View getScrollableView() {
        return ptrRecycler;
    }


    public interface OnFollowPersonalPullListener {

        void onRecommendPull();

    }

    private OnFollowPersonalPullListener onFollowPersonalPullListener;

    public void setOnFollowPersonalPullListener(OnFollowPersonalPullListener onFollowPersonalPullListener) {
        this.onFollowPersonalPullListener = onFollowPersonalPullListener;
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
}
