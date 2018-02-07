package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.huapu.huafen.R;
import com.huapu.huafen.activity.SelectedCoverActivity;
import com.huapu.huafen.adapter.VUserAdapter;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.beans.UserListResult;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.presenter.ListPresenter;
import com.huapu.huafen.presenter.impl.UserListPresenterImpl;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 用户列表
 *
 * @author dengbin
 */
public class UserListFragment extends BaseFragment implements OnClickListener {

    @BindView(R.id.ptrView)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    View loadMoreLayout;
    
    private int type;
    private long userId;
    private OnCreateViewListener listener;
    private ListPresenter presenter;
    private VUserAdapter adapter;
    private int page;

    public static UserListFragment getInstance(int type, long userId) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putLong("user_id", userId);
        UserListFragment instance = new UserListFragment();
        instance.setArguments(bundle);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getInt("type");
        userId = getArguments().getLong("user_id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_user_list, container, false);

        presenter = new UserListPresenterImpl(this);

        if (this.listener != null) {
            listener.onCreateView();
        }

        initViews(layout);

        return layout;
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.layoutMyself: // 个人主页
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getActivity());
                    return;
                }
                intent = new Intent(getActivity(), SelectedCoverActivity.class);
                intent.putExtra("type", "1");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK)
            return;
        if (data == null)
            return;
        int position = data.getIntExtra("position", -1);
        int type = data.getIntExtra("type", 0);
        if (position != -1 && type != 0) {
            adapter.updateFollowState(position, type);
        }
    }

    public void getData(String extra) {
        presenter.getList(extra);
    }

    // 请求参数
    public Map<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(userId));
        params.put("page", String.valueOf(page));
        params.put("pageNum", "12");
        return params;
    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        page = 0;
        getData(LOADING);
    }

    public int getType() {
        return type;
    }

    // 设置loading状态
    public void setLoadingState(HLoadingStateView.State complete) {
        loadingStateView.setStateShown(complete);
    }

    public void onRefreshComplete() {
        mPtrFrame.refreshComplete();
    }

    public void setData(UserListResult result, String extra) {
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
    protected void initViews(View layout) {
        super.initViews(layout);

        // PtrFrameLayout属性设置
        ViewUtil.setPtrFrameLayout(mPtrFrame);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                View view = mRecyclerView.getChildAt(0);
                if (view == null || (view.getTop() == 0 && CommonPreference.isLogin())) {
                    return true;
                }
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 0;
                getData(REFRESH);
            }
        });

        ViewUtil.setOffItemAnimator(mRecyclerView);
        adapter = new VUserAdapter(getActivity());
        mRecyclerView.setAdapter(adapter.getWrapperAdapter());

        loadMoreLayout = LayoutInflater.from(getContext()).inflate(R.layout.load_layout, mRecyclerView, false);
        View emptyView = ViewUtil.initImgEmptyView(mRecyclerView, R.mipmap.img_empty_follow_user);

        adapter.setEmptyView(emptyView);
        adapter.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getData(LOAD_MORE);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter = null;
    }
}
