package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
import com.huapu.huafen.views.PtrDefaultFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by qwe on 2017/5/19.
 */

public class VIPNewGoodsActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;

    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_newgoods);
        ButterKnife.bind(this);
        initView();
        refresh();
    }

    private void initView() {
        getTitleBar().setTitle("VIP上新榜");
        initPtr();
    }

    public void initPtr() {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int index = manager.findFirstVisibleItemPosition();
                return index == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });
        PtrAnimationBackgroundHeader header = new PtrAnimationBackgroundHeader(this);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        // the following are default settings
        ptrFrameLayout.setResistance(1.7f);
        ptrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrameLayout.setDurationToClose(200);
        ptrFrameLayout.setDurationToCloseHeader(300);
        // default is false
        ptrFrameLayout.setPullToRefresh(false);
        // default is true
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);

    }

    private void refresh() {
        page = 0;
        doRequest(REFRESH);
    }

    private void doRequest(final String state) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            return;
        }

        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("page", String.valueOf(page));

    }
}
