package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.huapu.huafen.adapter.CommentListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CommentListData;
import com.huapu.huafen.beans.Comments;
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
import java.util.List;
import java.util.TreeMap;

/**
 * 评价
 * Created by lalo on 2016/10/25.
 */
public class CommentListFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    private final static String TAG = CommentListFragment.class.getSimpleName();
    private final static int REQUEST_CODE_FOR_COMMENT_DETAIL = 0x1111;

    private long mId;
    private String from;
    private String mUrl;
    private PullToRefreshRecyclerView ptrRecycler;
    private int page;
    private View footerView;
    private CommentListAdapter adapter;
    private boolean isFirstRequest = true;
    private HLoadingStateView loadingStateView;
    private int type;
    private int satisfaction;
    private OnCountChangedListener mOnCountChangedListener;

    public static CommentListFragment newInstance(Bundle bundle) {
        CommentListFragment commentListFragment = new CommentListFragment();
        commentListFragment.setArguments(bundle);
        return commentListFragment;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_comment_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        initRecyclerView(root);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (satisfaction == 0) {
            initRequest();
        }
    }

    private void initRequest() {
        if (adapter != null && adapter.isEmpty() && isFirstRequest) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
            startRequestForCommentList(REFRESH);
        }
    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        initRequest();
    }

    private void initRecyclerView(View rootView) {

        Bundle bundle = getArguments();
        mId = bundle.getLong(MyConstants.COMMENT_LIST_ID);
        from = bundle.getString(MyConstants.EXTRA_FROM_WHERE);
        mUrl = bundle.getString(MyConstants.COMMENT_LIST_URL);
        type = bundle.getInt(MyConstants.TYPE);
        satisfaction = bundle.getInt("satisfaction", -1);

        if (satisfaction < 0) {
            toast("satisfaction 不能小于0");
            getActivity().finish();
        }
        ptrRecycler = (PullToRefreshRecyclerView) rootView.findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(layoutManager);
        ptrRecycler.setOnRefreshListener(this);
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.load_layout, ptrRecycler.getRefreshableView(), false);
        adapter = new CommentListAdapter(this, new ArrayList<Comments>());

        View viewEmpty = LayoutInflater.from(getContext()).inflate(R.layout.view_empty_image, ptrRecycler.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_comment);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());

        loadingStateView = (HLoadingStateView) rootView.findViewById(R.id.loadingStateView);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
    }

    private void startRequestForCommentList(final String extra) {
        if (!CommonUtils.isNetAvaliable(getContext())) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
            ptrRecycler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ptrRecycler.onRefreshComplete();
                }
            }, 1000);
            ToastUtil.toast(getContext(), "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(from, String.valueOf(mId));
        params.put("page", String.valueOf(page));
        params.put("satisfaction", String.valueOf(satisfaction));
        params.put("pageSize", String.valueOf(20));

        OkHttpClientManager.postAsyn(mUrl, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ptrRecycler.onRefreshComplete();
                loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
            }

            @Override
            public void onResponse(String response) {
                try {
                    ptrRecycler.onRefreshComplete();

                    LogUtil.e(TAG, "订单评价：" + response);

                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            CommentListData data = JSON.parseObject(baseResult.obj, CommentListData.class);
                            initData(data, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, getContext(), "");
                    }
                } catch (Exception e) {
                    LogUtil.e(TAG, "publish crash..." + e.getMessage());
                }
                isFirstRequest = false;
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
        });
    }

    private void initData(CommentListData data, String extra) {
        if (data.getPage() == 0) {//load完毕
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(footerView);
        }
        page++;

        List<Comments> comments = data.getComments();
        if (REFRESH.equals(extra)) {//刷新
            if (mOnCountChangedListener != null) {
                mOnCountChangedListener.onCountChange(data.getCount());
            }
            adapter.setData(comments, mId);
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            if (comments == null) {
                comments = new ArrayList<>();
            }
            adapter.addAll(comments);
        }

    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
        //下拉刷新
        page = 0;
        startRequestForCommentList(REFRESH);
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        startRequestForCommentList(LOAD_MORE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_COMMENT_DETAIL) {
                ptrRecycler.setRefreshing();
            }
        }
    }

    public void setOnCountChangedListener(OnCountChangedListener onCountChangedListener) {
        this.mOnCountChangedListener = onCountChangedListener;
    }

    public interface OnCountChangedListener {
        void onCountChange(TreeMap<String, Integer> map);
    }


}
