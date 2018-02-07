package com.huapu.huafen.activity;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.AbstractRecyclerAdapter;
import com.huapu.huafen.adapter.StarRegionAdapter;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.StarRegionBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.ClassBannerView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrAnimationBackgroundHeader;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by qwe on 2017/5/19.
 */

public class NewStarRegionActivity extends BaseActivity implements AbstractRecyclerAdapter.LoadMoreListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    @BindView(R.id.titleBar)
    TitleBarNew titleBar;
    private int page = 0;

    private StarRegionAdapter starRegionAdapter;
    private BannerData starUserBanner = CommonPreference.getStarGoodsBanner();
    private ArrayList<CampaignBanner> publicBanners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newstar_region);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initPtr();
        initTitle();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        starRegionAdapter = new StarRegionAdapter(this, recyclerView);
        starRegionAdapter.setLoadMoreListener(this);
        recyclerView.setAdapter(starRegionAdapter);
        ViewUtil.setOffItemAnimator(recyclerView);

        initBanner();
        refresh();
    }

    private void initTitle() {
        titleBar.setBackgroundAlpha(0);
        titleBar.getBtnTitleLeft().setImageResource(R.drawable.personal_title_back);
        titleBar.setTitle("明星专区");
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lHeight = CommonUtils.getScreenWidth();
                titleBar.setAlphaInRecyclerView(dy, lHeight);
            }
        });
    }

    private void initBanner() {
        if (starUserBanner != null) {
            publicBanners = starUserBanner.getBanners();
            if (!ArrayUtil.isEmpty(publicBanners)) {
                View header = LayoutInflater.from(this).inflate(R.layout.view_headview_star_banner, null, false);
                ClassBannerView bannerView = (ClassBannerView) header.findViewById(R.id.bannerView);
                View layoutBanner = header.findViewById(R.id.layoutBanner);
                int width = CommonUtils.getScreenWidth();
                int height = (int) (width * 34f / 75f);
                LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
                        width, height);
                // 设置banner高度
                layoutBanner.setLayoutParams(localLayoutParams);
                bannerView.setBanners(publicBanners);
                starRegionAdapter.addHeaderView(header);
                starRegionAdapter.setHeaderEnable(true);
                CommonUtils.setAutoLoop(starUserBanner, bannerView);
            }
        }
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
        if (page > 0) {
            starRegionAdapter.initLoading(false);
        }
        page = 0;
        doRequest(REFRESH);
    }

    private void doRequest(final String state) {
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("page", String.valueOf(page));
        OkHttpClientManager.postAsyn(MyConstants.NEW_STAR, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ToastUtil.toast(NewStarRegionActivity.this, "请检查网络连接");
                if (state.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (state.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                } else if (state.equals(LOAD_MORE)) {

                }
            }


            @Override
            public void onResponse(String response) {
                try {
                    Logger.e("get response:" + response);
                    if (state.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    } else if (state.equals(REFRESH)) {
                        ptrFrameLayout.refreshComplete();
                    } else if (state.equals(LOAD_MORE)) {
                        starRegionAdapter.notifyMoreFinish();
                    }
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }

                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            StarRegionBean regionBean = JSON.parseObject(baseResult.obj, StarRegionBean.class);
                            initData(regionBean, state);
                        }
                    } else {
                        CommonUtils.error(baseResult, NewStarRegionActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(StarRegionBean starRegionBean, String state) {


        if (starRegionBean.page == 0) {
            starRegionAdapter.setAutoLoadMoreEnable(false);
            starRegionAdapter.loadFinish(false);
        } else {
            starRegionAdapter.setAutoLoadMoreEnable(true);
        }

        page++;


        if (state.equals(REFRESH)) {
            starRegionAdapter.setHeaderNumber(starRegionBean.activeUsers.size() + 3);
            starRegionAdapter.setActiveUserData(starRegionBean.activeUsers);
            starRegionAdapter.setRes(starRegionBean.recUserWithGoods);
        } else if (LOAD_MORE.equals(state)) {
            if (starRegionBean.page == 0) {
                starRegionAdapter.notifyDataSetChanged();
            } else {
                starRegionAdapter.appendData(starRegionBean.recUserWithGoods);
            }

        }

    }

    @Override
    public void onLoadMore() {
        doRequest(LOAD_MORE);
    }
}
