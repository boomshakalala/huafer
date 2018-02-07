package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ReleaseListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Goods;
import com.huapu.huafen.beans.GoodsResult;
import com.huapu.huafen.beans.MyGoods;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.GoodsStateEvent;
import com.huapu.huafen.events.RefreshEvent;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
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

import de.greenrobot.event.EventBus;


/**
 * 发布的商品
 * Created by liang on 2016/10/26.
 */
public class ReleaseListFragment extends BaseFragment implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>, LoadMoreWrapper.OnLoadMoreListener {

    private final static String TAG = ReleaseListFragment.class.getSimpleName();

    private final static int REQUEST_CODE_SELLER_COMMENT = 0x1111;

    private MyGoods type;
    private long userId;
    public PullToRefreshRecyclerView ptrRecycler;
    private int page;
    private View footerView;
    private ReleaseListAdapter adapter;
    private HLoadingStateView loadingStateView;
    private boolean isFirstRequest = true;
    private int waitingToWater = 0;
    private View waterAll;
    private TextView tvWaterAll;
    private TextView tvShow;
    private GoodsResult data;
    private boolean hasWaterHead = false;
    private boolean hasShelveHead = false;
    private boolean isFirstLoad;

    public static ReleaseListFragment newInstance(Bundle bundle) {
        ReleaseListFragment commentListFragment = new ReleaseListFragment();
        commentListFragment.setArguments(bundle);
        return commentListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        type = (MyGoods) bundle.getSerializable(MyConstants.EXTRA_TYPE);
        isFirstLoad = bundle.getBoolean(MyConstants.EXTRA_ORDERS_IS_FIRST_LOAD, false);
        userId = CommonPreference.getUserId();
        EventBus.getDefault().register(this);
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_orders_list_layout, container, false);
    }

    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        initRecyclerView(root);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        waterAll = LayoutInflater.from(getActivity()).inflate(R.layout.view_water_all, ptrRecycler.getRefreshableView(), false);
        tvWaterAll = (TextView) waterAll.findViewById(R.id.tvWaterAll);
        tvShow = (TextView) waterAll.findViewById(R.id.tvShow);
        String status = type.getStatus();
        String AuditStatus = type.getAuditStatus();
        if (status.equals("1")) {
            tvWaterAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OkHttpClientManager.postAsyn(MyConstants.WATER_ALL,
                            new OkHttpClientManager.StringCallback() {

                                @Override
                                public void onError(Request request, Exception e) {
                                }

                                @Override
                                public void onResponse(String response) {
                                    try {
                                        ToastUtil.toast(getContext(), "一键浇水成功");
                                        tvShow.setCompoundDrawables(null, null, null, null);
                                        tvShow.setText("浇水成功，将增加更多曝光机会");
                                        tvShow.setTextColor(Color.parseColor("#888888"));
                                        tvWaterAll.setBackgroundResource(R.drawable.water_grey_bg);
                                        tvWaterAll.setClickable(false);
                                        setRefreshing();
                                        LogUtil.i(TAG, "一键浇水刷新");
                                    } catch (Exception e) {
                                        LogUtil.e(TAG, "" + e.getMessage());
                                    }
                                }
                            });
                }
            });
        } else if (status.equals("5")) {
            tvWaterAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextDialog dialog = new TextDialog(getContext(), false);
                    dialog.setContentText("您确定要一键上架所有商品吗？");
                    dialog.setLeftText("取消");
                    dialog.setColor(Color.parseColor("#2d8bff"));
                    dialog.setLeftCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();
                        }
                    });
                    dialog.setRightText("确定");
                    dialog.setRightCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            //一键上架
                            OkHttpClientManager.postAsyn(MyConstants.SHELVE_ALL,
                                    new OkHttpClientManager.StringCallback() {

                                        @Override
                                        public void onError(Request request, Exception e) {
                                        }

                                        @Override
                                        public void onResponse(String response) {


                                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                                try {
                                                    if (hasShelveHead) {
                                                        adapter.removeHeaders();
                                                        hasShelveHead = false;
                                                    }
                                                    ToastUtil.toast(getContext(), "上架成功");
                                                    GoodsStateEvent event = new GoodsStateEvent();
                                                    event.isSave = true;
                                                    EventBus.getDefault().post(event);
//                                                setRefreshing();
                                                    LogUtil.i(TAG, "一键上架刷新");
//                                                    startRequestForGoods(type,REFRESH);
                                                } catch (Exception e) {
                                                    LogUtil.e(TAG, "" + e.getMessage());
                                                }
                                            } else {
                                                ToastUtil.toast(getContext(), baseResult.msg);
                                            }
                                        }
                                    });
                        }
                    });
                    dialog.show();
                }
            });
        }

//        if (isFirstLoad) {
//            initRequest();
//        }
    }

    private void initRequest() {
        if (adapter != null && adapter.isEmpty() && isFirstRequest) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
            startRequestForGoods(type, REFRESH);
        }
    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        initRequest();
    }

    private void initRecyclerView(View rootView) {
        ptrRecycler = (PullToRefreshRecyclerView) rootView.findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(layoutManager);
        ptrRecycler.setOnRefreshListener(this);
        footerView = LayoutInflater.from(getContext()).inflate(R.layout.load_layout, ptrRecycler.getRefreshableView(), false);
        adapter = new ReleaseListAdapter(this, new ArrayList<Goods>());
        View viewEmpty = LayoutInflater.from(getContext()).inflate(R.layout.view_empty_image, ptrRecycler.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_release);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        adapter.setOnItemClickListener(new ReleaseListAdapter.OnItemClickListener() {
            @Override
            public void onWaterItemClick() {
                ToastUtil.toast(getContext(), "浇水成功");
                setRefreshing();
                LogUtil.i(TAG, "浇水刷新");

//                waitingToWater -= 1;
//                tvShow.setText(waitingToWater + "件商品在等待浇水");
//                if (waitingToWater == 0) {
//                    tvShow.setText("浇水成功，将增加更多曝光机会");
//                    tvShow.setTextColor(Color.parseColor("#888888"));
//                    tvWaterAll.setBackgroundResource(R.drawable.water_grey_bg);
//                    tvWaterAll.setClickable(false);
//                    startRequestForGoods(type, REFRESH);
//                }
            }

            @Override
            public void onDownAndDeleteItemClick() {
                GoodsStateEvent event = new GoodsStateEvent();
                event.isSave = true;
                EventBus.getDefault().post(event);
//                setRefreshing();
                LogUtil.i(TAG, "onDownAndDeleteItemClick刷新");
            }
        });
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());
        loadingStateView = (HLoadingStateView) rootView.findViewById(R.id.loadingStateView);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
    }

    private void startRequestForGoods(final MyGoods publish, final String extra) {
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
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("page", String.valueOf(page));
        params.put("status", publish.getStatus());
        params.put("auditStatus", publish.getAuditStatus());
        params.put("preselling", String.valueOf(0));
        params.put("complainted", String.valueOf(0));
        LogUtil.i("liang", "params:" + params.toString());
        LogUtil.i("byGuan", "status:" + publish.getStatus());
        OkHttpClientManager.postAsyn(MyConstants.GOODS, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ProgressDialog.closeProgress();
                        ptrRecycler.onRefreshComplete();
                        loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
//                            LogUtil.i(TAG, "我发布的：" + response);
                            // 测试数据
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if (!isJson) {
                                ptrRecycler.onRefreshComplete();
                                loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                                return;
                            }
                            ptrRecycler.onRefreshComplete();
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    data = JSON.parseObject(baseResult.obj, GoodsResult.class);
                                    initData(data, extra, publish.getStatus(), publish.getAuditStatus());
                                }
                            } else {
                                CommonUtils.error(baseResult, getContext(), "");
                            }
                            isFirstRequest = false;
                        } catch (Exception e) {
                            ptrRecycler.onRefreshComplete();
                            loadingStateView.setStateShown(HLoadingStateView.State.LOADING_FAILED);
                            LogUtil.e(TAG, "buy crash..." + e.getMessage());
                        }
                    }
                });
    }

    private void initData(final GoodsResult data, String extra, String status, String AuditStatus) throws Exception {
        if (data != null) {
            if (status.equals("1")) {

                if (AuditStatus.equals("3,4,5")) {

                } else {
                    if (data.getGoodsStatusCounts().waitingToWater > 1) {
                        int waitingToWater = data.getGoodsStatusCounts().waitingToWater;
                        if (waitingToWater == 0) {
                            tvShow.setCompoundDrawables(null, null, null, null);
                            tvShow.setText("浇水成功，将增加更多曝光机会");
                            tvShow.setTextColor(Color.parseColor("#888888"));
                            tvWaterAll.setBackgroundResource(R.drawable.water_grey_bg);
                            tvWaterAll.setClickable(false);
                        } else {
                            tvShow.setText(waitingToWater + "件商品在等待浇水");
                        }
                        if (!hasWaterHead) {
                            adapter.addHeaderView(waterAll);
                            hasWaterHead = true;
                        }
                    } else {
                        if (hasWaterHead) {
                            if (!LOAD_MORE.equals(extra)) {
                                adapter.removeHeaders();
                                hasShelveHead = false;
                            }
                        }
                    }
                }
            } else if (status.equals("5")) {
                if (data.getGoodsStatusCounts().waitingToShelve > 1) {
                    tvShow.setText(data.getGoods().size() + "件商品等待上架");
                    tvWaterAll.setText("一键上架");
                    if (!hasShelveHead) {
                        adapter.addHeaderView(waterAll);
                        hasShelveHead = true;
                    }
                } else {
                    if (hasShelveHead) {
                        if (!LOAD_MORE.equals(extra)) {
                            adapter.removeHeaders();
                            hasShelveHead = false;
                        }

                    }
                }
            } else {
                if (adapter != null) {
                    adapter.removeHeaders();
                }
            }
        }
        if (REFRESH.equals(extra)) {//刷新
            adapter.setData(data.getGoods());
            if (onRefreshOverListener != null) {
                onRefreshOverListener.onComplete(data.getGoodsStatusCounts());
                LogUtil.i(TAG, "上传接口" + type.getStatus());
            }
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            List<Goods> goods = data.getGoods();
            if (goods == null) {
                goods = new ArrayList<>();
            }
            adapter.addAll(goods);
        }
        if (data.getPage() == 0) {//load完毕
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(footerView);
        }
        adapter.getWrapperAdapter().notifyDataSetChanged();
        page++;
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        page = 0;
        startRequestForGoods(type, REFRESH);
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        startRequestForGoods(type, LOAD_MORE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e(TAG, "onActivityResult(" + requestCode + "," + resultCode + ")");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELLER_COMMENT) {
                ptrRecycler.setRefreshing();
            }
        }
    }

    public interface OnRefreshOverListener {
        void onComplete(GoodsResult.GoodsStatusCounts orderStatusCounts);
    }

    private OnRefreshOverListener onRefreshOverListener;

    public void setOnRefreshOverListener(OnRefreshOverListener onRefreshOverListener) {
        this.onRefreshOverListener = onRefreshOverListener;
    }

    public void setRefreshing() {
        LogUtil.i(TAG, "刷新" + type.getStatus());
        ptrRecycler.getRefreshableView().scrollToPosition(0);
        page = 0;
        startRequestForGoods(type, REFRESH);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(RefreshEvent refreshEvent){
        if (refreshEvent.refresh)
            setRefreshing();
    }
}
