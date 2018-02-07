package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.WalletListAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.WalletResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.AlwaysMarqueeTextView;
import com.huapu.huafen.views.GuideView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author liang_xs
 */
public class WalletActivity extends BaseActivity
        implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    private static final String TAG = WalletActivity.class.getSimpleName();
    private PullToRefreshRecyclerView ptrRecycler;
    private HLoadingStateView loadingStateView;
    private WalletListAdapter adapter;
    private int page = 0;
    private View loadMoreLayout;
    private TextView tvEarn;
    private TextView tvSave;
    private AlwaysMarqueeTextView tvTip;
    private View headerView;
    private TextView tvBtnLeft;
    private TextView tvBtnRight;
    private boolean isNeedsLoadMore;
    private int savedMoney = -1;

    private GuideView guideView;

    private int shareEarned;

    private RelativeLayout layoutBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initView();
        startLoading();
    }

    private void initView() {
        getTitleBar().
                setTitle("花粉儿小金库");


        /**初始化recyclerView*/
        ptrRecycler = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecycler);
        CommonUtils.buildPtr(ptrRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecycler.getRefreshableView().setLayoutManager(layoutManager);
        ptrRecycler.getRefreshableView().setBackgroundResource(R.drawable.wallet_recycler_view_fucking_bg);
        ptrRecycler.setOnRefreshListener(this);

        /**初始化adapter*/
        adapter = new WalletListAdapter(this);
        //header
        headerView = LayoutInflater.from(this).inflate(R.layout.wallet_header, ptrRecycler.getRefreshableView(), false);
        tvEarn = (TextView) headerView.findViewById(R.id.tvEarn);
        tvSave = (TextView) headerView.findViewById(R.id.tvSave);
        tvTip = (AlwaysMarqueeTextView) headerView.findViewById(R.id.tvTip);
        //loadMoreLayout
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, ptrRecycler.getRefreshableView(), false);
        loadMoreLayout.setBackgroundColor(getResources().getColor(R.color.white));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) loadMoreLayout.getLayoutParams();
        params.leftMargin = params.rightMargin = CommonUtils.dp2px(10);
        TextView textMore = (TextView) loadMoreLayout.findViewById(R.id.loadMoreText);
        textMore.setText("");
        //空数据视图
        View viewEmpty = LayoutInflater.from(this).inflate(R.layout.wallet_empty, ptrRecycler.getRefreshableView(), false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.burse_empty);
        //addHeader setEmptyView setOnLoadMoreListener setLoadMoreView
        adapter.addHeaderView(headerView);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        adapter.setLoadMoreView(loadMoreLayout);

        /**初始化setAdapter*/
        ptrRecycler.setAdapter(adapter.getWrapperAdapter());

        /**初始化loadingStateView*/
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);

        tvBtnLeft = (TextView) findViewById(R.id.tvBtnLeft);
        tvBtnRight = (TextView) findViewById(R.id.tvBtnRight);

        tvBtnLeft.setOnClickListener(this);
        tvBtnRight.setOnClickListener(this);
        layoutBase = (RelativeLayout) findViewById(R.id.layoutBase);

    }

    private void addGuideTips() {
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.show_tips);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params1);

        guideView = new GuideView.Builder(this)
                .setTargetView(layoutBase)
                .setCustomGuideView(iv)
                .setDirction(GuideView.Direction.BOTTOM)
                .setDrawRec()
                .setBgColor(ContextCompat.getColor(this, R.color.color_montage_bg))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
                    }
                })
                .build();

        guideView.show();
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        refresh();
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        loadMore();
    }

    private void loadMore() {
        if (isNeedsLoadMore) {
            startRequest(LOAD_MORE);
        }
    }

    private void refresh() {
        page = 0;
        startRequest(REFRESH);
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        page = 0;
        startRequest(LOADING);
    }

    private void startRequest(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            if (LOADING.equals(extra)) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            } else if (REFRESH.equals(extra)) {
                ptrRecycler.onRefreshComplete();
            } else if (LOAD_MORE.equals(extra)) {

            }
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("page", String.valueOf(page));
        LogUtil.i(TAG, "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.WALLET_HOME, params, new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        LogUtil.e(TAG, "onError:" + e.getMessage());
                        ProgressDialog.closeProgress();
                        LogUtil.i(TAG, e.toString());
                        if (LOADING.equals(extra)) {
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        } else if (REFRESH.equals(extra)) {
                            ptrRecycler.onRefreshComplete();
                        } else if (LOAD_MORE.equals(extra)) {

                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        if (LOADING.equals(extra)) {
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        } else if (REFRESH.equals(extra)) {
                            ptrRecycler.onRefreshComplete();
                        } else if (LOAD_MORE.equals(extra)) {

                        }
                        try {
                            LogUtil.e(TAG, "onResponse:" + response);
                            // 测试数据
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    WalletResult data = JSON.parseObject(baseResult.obj, WalletResult.class);
                                    initData(data, extra);
                                }
                            } else {
                                CommonUtils.error(baseResult, WalletActivity.this);
                            }
                        } catch (Exception e) {
                            LogUtil.e(TAG, "onResponse crush..." + e.getMessage());
                        }
                    }
                }
        );
    }

    private void initData(WalletResult data, String extra) {

        if (data.page == 0) {
            isNeedsLoadMore = false;
        } else {
            isNeedsLoadMore = true;
        }
        page++;

        List<WalletResult.Transaction> list = data.transactions;

        if (LOADING.equals(extra) || REFRESH.equals(extra)) {//进入界面大loading或下拉刷新
            String earnDes = String.format(getString(R.string.earn_count), data.earned);
            shareEarned = data.earned;

            if (shareEarned > 0) {
                getTitleBar().setRightText("炫耀一下", new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(WalletActivity.this, WalletShareActivity.class);
                        intent.putExtra("shareEarned", shareEarned);
                        startActivity(intent);
                    }
                });
                if (CommonPreference.getBooleanValue("showOthers", true)) {
                    addGuideTips();
                    CommonPreference.setBooleanValue("showOthers", false);
                }
            }

            tvEarn.setText(Html.fromHtml(earnDes));
            String saveDes = String.format(getString(R.string.save_count), data.saved);
            tvSave.setText(Html.fromHtml(saveDes));
            savedMoney = data.saved;
            tvTip.setText(data.tip);
            adapter.setData(list);
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            if (list == null) {
                list = new ArrayList<WalletResult.Transaction>();
            }
            adapter.addData(list);
        }
    }


    @Override
    public void onBackPressed() {
        if (savedMoney >= 0) {
            Intent data = new Intent();
            data.putExtra("saved", savedMoney);
            setResult(RESULT_OK, data);
        }
        super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvBtnLeft) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.EARN_AND_SAVE);
            startActivity(intent);
        } else if (v.getId() == R.id.tvBtnRight) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.WHERE_CASH);
            startActivity(intent);
        }
    }
}
