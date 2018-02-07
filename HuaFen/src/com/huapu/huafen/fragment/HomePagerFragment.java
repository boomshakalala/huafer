package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.FCommodityGridAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.CommodityListResult;
import com.huapu.huafen.beans.HomePagerFilter;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.GoodsEditEvent;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.scrollablelayoutlib.ScrollableHelper;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2016/12/15.
 */
public class HomePagerFragment extends ScrollAbleFragment implements ScrollableHelper.ScrollableContainer, LoadMoreWrapper.OnLoadMoreListener, RadioGroup.OnCheckedChangeListener {

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;
    private String url;
    private RecyclerView recyclerView;
    private FCommodityGridAdapter adapter;
    private long userId;
    private int page;
    private View loadMoreView;
    private HLoadingStateView loadingStateView;
    private HashMap<String, String> params = new HashMap<String, String>();
    private LinearLayout llFilterLayout;
    private RadioGroup rgFilters;
    private ScheduledExecutorService executorService = Executors
            .newSingleThreadScheduledExecutor();
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean isUnloaded = false;

    public static HomePagerFragment newInstance(Bundle bundle) {
        HomePagerFragment tabFragment = new HomePagerFragment();
        tabFragment.setArguments(bundle);
        return tabFragment;
    }


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        EventBus.getDefault().register(this);
        Bundle data = getArguments();
        userId = data.getLong("userId");
        url = data.getString("url");
        return inflater.inflate(R.layout.home_fragment_layout, container, false);

    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        loadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.load_layout, recyclerView, false);
        adapter = new FCommodityGridAdapter(this);
        View viewEmpty = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty_image, recyclerView, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_personal_home);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ivEmpty.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = CommonUtils.dp2px(70f);
        adapter.setEmptyView(viewEmpty);
        adapter.setOnLoadMoreListener(this);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://空闲状态
                        ImageLoader.resumeLoadImage();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://自动滚
                        ImageLoader.pauseLoadImage();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        loadingStateView = (HLoadingStateView) view.findViewById(R.id.loadingStateView);
        llFilterLayout = (LinearLayout) view.findViewById(R.id.llFilterLayout);
        rgFilters = (RadioGroup) view.findViewById(R.id.rgFilters);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        params.put("filterId", "0");
        if (url.equals(MyConstants.USER_SELLING)) {
            startLoading();
            executorService.scheduleAtFixedRate(
                    new Task(),
                    0,
                    1000,
                    TimeUnit.MILLISECONDS);
        }

    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        if (adapter != null && adapter.isEmpty() && !isUnloaded) {
            startLoading();
        }
    }

    @Override
    public void onLoadMoreRequested() {
        loadMore();
    }

    public void refresh() {
        page = 0;
        startRequestForList(REFRESH);
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        startRequestForList(LOADING);
    }

    private void loadMore() {
        startRequestForList(LOAD_MORE);
    }

    /**
     * 用户闲置的宝贝接口(没卖出的)
     *
     * @param
     */
    private void startRequestForList(final String extra) {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            ToastUtil.toast(getActivity(), "请检查网络连接");
            return;
        }

        params.put("userId", String.valueOf(userId));
        params.put("page", String.valueOf(page));
        OkHttpClientManager.postAsyn(url, params,

                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        if (extra.equals(LOADING)) {
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                            isUnloaded = true;
                        } else if (extra.equals(REFRESH)) {

                        } else if (extra.equals(LOAD_MORE)) {

                        }
                    }

                    @Override
                    public void onResponse(String response) {
                        if (extra.equals(LOADING)) {
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                            isUnloaded = true;
                        } else if (extra.equals(REFRESH)) {

                        } else if (extra.equals(LOAD_MORE)) {

                        }
                        try {
                            LogUtil.i("liang", "用户闲置的宝贝接口(没卖出的):" + response);
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                if (!TextUtils.isEmpty(baseResult.obj)) {
                                    CommodityListResult result = JSON.parseObject(baseResult.obj, CommodityListResult.class);
                                    initData(result, extra);
                                }
                            } else {
                                CommonUtils.error(baseResult, getActivity(), "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
        );
    }


    private void initData(CommodityListResult result, String extra) {

        if (result.getPage() == 0) {//分页完毕
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreView);
        }
        page++;

        List<Commodity> list = result.getGoodsList();
        List<HomePagerFilter> filters = result.filters;

        if (LOADING.equals(extra) || REFRESH.equals(extra)) {
            initFilters(filters);
            adapter.setData(list);
        } else if (LOAD_MORE.equals(extra)) {
            if (list == null) {
                list = new ArrayList<>();
            }
            adapter.addData(list);
        }
    }

    private void initFilters(List<HomePagerFilter> filters) {
        rgFilters.removeAllViews();
        rgFilters.setOnCheckedChangeListener(null);
        if (!ArrayUtil.isEmpty(filters)) {
            llFilterLayout.setVisibility(View.VISIBLE);
            for (HomePagerFilter p : filters) {
                if (!TextUtils.isEmpty(p.name)) {
                    RadioButton rb = new RadioButton(getContext());
                    rb.setTag(p);
                    rb.setButtonDrawable(null);
                    rb.setTextColor(getResources().getColorStateList(R.color.radio_group_grey_pink_selector));
                    rb.setPadding(CommonUtils.dp2px(0f), CommonUtils.dp2px(5f), CommonUtils.dp2px(0f), CommonUtils.dp2px(5f));
                    rb.setText(p.name);
                    rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, CommonUtils.dp2px(30f), 1.0f);
                    rb.setGravity(Gravity.CENTER);
                    rb.setLayoutParams(params);
                    rgFilters.addView(rb, params);
                    if (this.params.containsKey("filterId")) {
                        String key = this.params.get("filterId");
                        if (String.valueOf(p.identity).equals(key)) {
                            rgFilters.check(rb.getId());
                        }
                    }
                    LogUtil.e("initFilters", p.identity + "--" + p.name);
                }
            }
            rgFilters.setOnCheckedChangeListener(this);
        } else {
            llFilterLayout.setVisibility(View.GONE);
            rgFilters.setOnCheckedChangeListener(null);
        }
    }

    @Override
    public View getScrollableView() {
        return recyclerView;
    }

    public void listViewToTop() {
        if (recyclerView != null) {
            recyclerView.scrollToPosition(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_GOODS_DETAIL) {
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    int type = data.getIntExtra("type", 0);
                    if (position != -1 && type != 0) {
                        adapter.updateLikeState(position, type);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        Object obj = group.findViewById(checkedId).getTag();
        if (obj instanceof HomePagerFilter) {
            HomePagerFilter tag = (HomePagerFilter) obj;
            params.put("filterId", String.valueOf(tag.identity));
            LogUtil.e("type", params.get("filterId"));
            refresh();
        }
    }

    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof GoodsEditEvent) {
            GoodsEditEvent event = (GoodsEditEvent) obj;
            if (event.isSave) {
                refresh();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (null != executorService) {
            executorService.shutdown();
        }

    }

    private class Task implements Runnable {

        @Override
        public void run() {
            if (null != adapter) {
                if (null != adapter.getData()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyCountDownDataSetChanged();
                        }
                    });
                }

            }
        }
    }
}
