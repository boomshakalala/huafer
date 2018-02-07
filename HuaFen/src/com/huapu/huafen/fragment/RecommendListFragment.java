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

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.FCommodityAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.RecommendData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.base.ScrollAbleFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.scrollablelayoutlib.ScrollableHelper;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页-推荐列表
 *
 * @author lalo
 */
public class RecommendListFragment extends ScrollAbleFragment implements
        ScrollableHelper.ScrollableContainer, LoadMoreWrapper.OnLoadMoreListener {

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;

    private View view;
    private RecyclerView ptrRecycler;
    private int page = 0;
    public FCommodityAdapter recommendAdapter;
    private View loadMoreView;
    private HLoadingStateView loadingStateView;
    private boolean isCanLoadMore;

    public static RecommendListFragment newInstance() {
        RecommendListFragment listFragment = new RecommendListFragment();
        return listFragment;
    }

    public void listViewToTop() {
        if (ptrRecycler != null) {
            ptrRecycler.scrollToPosition(0);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recommend_new, container, false);
        //初始化控件
        initViews();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startLoading();
    }


    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        startRequestForGetRecommend(LOADING);
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
        recommendAdapter = new FCommodityAdapter(this);

        View viewEmpty = LayoutInflater.from(getActivity()).inflate(R.layout.view_empty_image, ptrRecycler, false);
        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.recommend_empty);
        recommendAdapter.setEmptyView(viewEmpty);
        recommendAdapter.setOnLoadMoreListener(this);
        ptrRecycler.setAdapter(recommendAdapter.getWrapperAdapter());
        loadingStateView = (HLoadingStateView) view.findViewById(R.id.loadingStateView);

        ptrRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

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
                        recommendAdapter.updateLikeState(position, type);
                    }

                }
            }
        }

    }

    @Override
    public void onLoadMoreRequested() {
        if (isCanLoadMore) {
            startRequestForGetRecommend(LOAD_MORE);
        }
    }

    /**
     * 从服务端获取推荐列表
     */
    public void startRequestForGetRecommendData() {
        page = 0;
        startRequestForGetRecommend(REFRESH);
    }


    private void startRequestForGetRecommend(final String extra) {
        if (!CommonUtils.isNetAvaliable(getActivity())) {
            ToastUtil.toast(getActivity(), "请检查网络连接");
            if (loadingStateView != null) {
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
            }
            if (ptrRecycler != null) {
                ptrRecycler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onRecommendPullListener != null) {
                            onRecommendPullListener.onRecommendPull();
                        }
                    }
                }, 1000);
            }
            return;
        }
        if (page == 0) {
            cacheMap = new HashMap<Integer, Commodity>();
        }
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("page", page + "");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.RECOMMEND_LIST, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("liang", "推荐列表error:" + e.toString());


                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    if (onRecommendPullListener != null) {
                        onRecommendPullListener.onRecommendPull();
                    }
                } else if (extra.equals(LOAD_MORE)) {

                }

            }


            @Override
            public void onResponse(String response) {
                try {

                    if (extra.equals(LOADING)) {
                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                    } else if (extra.equals(REFRESH)) {
                        if (onRecommendPullListener != null) {
                            onRecommendPullListener.onRecommendPull();
                        }
                    } else if (extra.equals(LOAD_MORE)) {

                    }
                    LogUtil.e("liang", "推荐列表:~~" + response.toString());
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            RecommendData data = JSON.parseObject(baseResult.obj, RecommendData.class);
                            initData(data, extra);
                        }
                    } else {
                        CommonUtils.error(baseResult, getActivity(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initData(RecommendData data, String extra) {
        recommendAdapter.setLoadMoreView(loadMoreView);
        if (data.getPage() == 0) {//load完毕
            loadMoreView.setVisibility(View.INVISIBLE);
        } else {
            loadMoreView.setVisibility(View.VISIBLE);
        }

        isCanLoadMore = data.getPage() == 0 ? false : true;

        page++;

        ArrayList<Commodity> list = data.getItems();
        ArrayList<Commodity> items = null;
        if (!ArrayUtil.isEmpty(list)) {
            items = delRepetition(list);
        }

        if (REFRESH.equals(extra)) {//刷新
            recommendAdapter.setData(items);
        } else if (LOAD_MORE.equals(extra)) {//加载更多
            if (items == null) {
                items = new ArrayList<Commodity>();
            }
            recommendAdapter.addData(items);
        } else if (LOADING.equals(extra)) {
            recommendAdapter.setData(items);
        }

        recommendAdapter.notifyWrapperDataSetChanged();
    }

    private Map<Integer, Commodity> cacheMap = new HashMap<Integer, Commodity>();

    /**
     * @param list
     * @return List<GoodsInfoBean>
     * @Description: 去重
     * @author liang_xs
     * @date 2016-8-26 下午5:51:37
     */
    private ArrayList<Commodity> delRepetition(List<Commodity> list) {
        ArrayList<Commodity> newList = new ArrayList<Commodity>();
        if (cacheMap != null && cacheMap.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Commodity javaBean = cacheMap.get(list.get(i).getGoodsData().getGoodsId());
                if (javaBean == null) {
                    cacheMap.put(list.get(i).getGoodsData().getGoodsId(), list.get(i));
                    LogUtil.d("liangxs", "add");
                } else {
                    LogUtil.d("liangxs", "remove");
                    list.remove(i);
                    i--;
                }
            }
            newList.addAll(list);
        } else {
            for (Commodity bean : list) {
                cacheMap.put(bean.getGoodsData().getGoodsId(), bean);
            }
            newList.addAll(list);
        }
        return newList;
    }

    @Override
    public View getScrollableView() {
        return ptrRecycler;
    }


    public interface OnRecommendPullListener {

        void onRecommendPull();

    }

    private OnRecommendPullListener onRecommendPullListener;

    public void setOnRecommendPullListener(OnRecommendPullListener onRecommendPullListener) {
        this.onRecommendPullListener = onRecommendPullListener;
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
}
