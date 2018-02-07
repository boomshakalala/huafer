package com.huapu.huafen.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.adapter.pages.EverydayFreshAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.pages.ComponentBean;
import com.huapu.huafen.beans.pages.EverydayFreshBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 每日上新
 * Created by dengbin on 18/1/9.
 */
public class EverydayFreshActivity extends BaseActivity {

    @BindView(R.id.ptrView)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;

    private EverydayFreshAdapter mAdapter;

    private OkHttpClientManager.StringCallback dataCallback = new OkHttpClientManager.StringCallback() {

        @Override
        public void onError(Request request, Exception e) {
            refreshComplete();
        }

        @Override
        public void onResponse(String response) {
            refreshComplete();

            try {
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    LogUtil.i("EverydayFreshActivity", response);
                    return;
                }

                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {

                    EverydayFreshBean result = JSON.parseObject(baseResult.obj, EverydayFreshBean.class);

                    setData(result);
                } else {
                    CommonUtils.error(baseResult, context, "");
                }
            } catch (Exception e) {
                ProgressDialog.closeProgress();
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_everyday_fresh);

        initView();
        getData(LOADING);
    }

    private void setData(EverydayFreshBean result) {
        List<ComponentBean> componentBeans = result.getComponents();

        if (componentBeans == null)
            return;

        for (ComponentBean bean : componentBeans) {
            bean.parseData();
        }
        mAdapter.setData(componentBeans);
    }

    private void refreshComplete() {
        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
        mPtrFrame.refreshComplete();
    }

    private void getData(String extra) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        if (TextUtils.equals(BaseFragment.LOADING, extra)) {
            loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", "everydayFresh");
        OkHttpClientManager.postAsyn(MyConstants.PAGES, params, dataCallback);
    }

    private void initView() {
        setTitleString("每日上新");

        // PtrFrameLayout属性设置
        ViewUtil.setPtrFrameLayout(mPtrFrame);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                View view = mRecyclerView.getChildAt(0);
                int p = layoutManager.findFirstVisibleItemPosition();
                if (p == 0 && (view == null || view.getTop() == 0)) {
                    return true;
                }
                return false;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                getData(REFRESH);
            }
        });

        ViewUtil.setOffItemAnimator(mRecyclerView);
        mAdapter = new EverydayFreshAdapter(context);
        mRecyclerView.setAdapter(mAdapter.getWrapperAdapter());

        View emptyView = ViewUtil.initImgEmptyView(mRecyclerView, R.mipmap.img_empty_follow_user);

        mAdapter.setEmptyView(emptyView);
    }

    public static void startMe(Context context) {
        Intent intent = new Intent(context, EverydayFreshActivity.class);
        context.startActivity(intent);
    }
}
