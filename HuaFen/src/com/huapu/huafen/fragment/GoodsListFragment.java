package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ShopkeepersAdapter;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.beans.ShopkeepersData;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.presenter.ListPresenter;
import com.huapu.huafen.presenter.impl.GoodListPresenterImpl;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * 可刷新商品列表-GridLayout
 * Created by dengbin
 */
public class GoodsListFragment extends BaseFragment implements LoadMoreWrapper.OnLoadMoreListener {

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;

    @BindView(R.id.ptr_view)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.recycler_view)
    RecyclerView ptrRecycler;
    @BindView(R.id.loading_state_view)
    HLoadingStateView loadingStateView;

    private ListPresenter mPresenter;
    private View view;
    private int page = 0;
    private View loadMoreView;
    private boolean isCanLoadMore;
    private int type;
    private GridLayoutManager gridLayoutManager;
    private ShopkeepersAdapter gridAdapter;

    public static GoodsListFragment newInstance() {
        GoodsListFragment listFragment = new GoodsListFragment();
        return listFragment;
    }

    public static GoodsListFragment newInstance(Bundle data) {
        GoodsListFragment listFragment = new GoodsListFragment();
        listFragment.setArguments(data);
        return listFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new GoodListPresenterImpl(this);
        type = getArguments().getInt("type");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_refreshable_list, container, false);
        //初始化控件
        initViews(view);
        return view;
    }

    public void listViewToTop() {
        if (ptrRecycler != null) {
            ptrRecycler.scrollToPosition(0);
        }
    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        if (gridAdapter.getItemCount() == 0) {
            page = 0;
            mPresenter.getList(LOADING);
        }
    }

    private void initGridAdapter() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        loadMoreView = inflater.inflate(R.layout.load_layout, ptrRecycler, false);
        loadMoreView.setVisibility(View.INVISIBLE);
        View viewEmpty = ViewUtil.initImgEmptyView(ptrRecycler, R.mipmap.img_empty_follow_good);

        gridAdapter.setEmptyView(viewEmpty);
        gridAdapter.setLoadMoreView(loadMoreView);
        gridAdapter.setOnLoadMoreListener(this);
    }

    @Override
    public void initViews(View view) {
        super.initViews(view);

        // PtrFrameLayout属性设置
        ViewUtil.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) ptrRecycler.getLayoutManager();
                int index = manager.findFirstCompletelyVisibleItemPosition();
                return index == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 0;
                mPresenter.getList(REFRESH);
            }
        });

        ViewUtil.setOffItemAnimator(ptrRecycler);
        gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        ptrRecycler.setLayoutManager(gridLayoutManager);
        gridAdapter = new ShopkeepersAdapter(getContext());
        ptrRecycler.setAdapter(gridAdapter.getWrapperAdapter());

        initGridAdapter();
    }

    @Override
    public void onLoadMoreRequested() {
        if (isCanLoadMore) {
            mPresenter.getList(LOAD_MORE);
        }
    }

    public void setData(ShopkeepersData data, String extra) {
        if (null == data.getList() || data.getList().size() <= 0)
            return;

        if (data.getPage() == 0) { // load完毕
            loadMoreView.setVisibility(View.INVISIBLE);
        } else {
            loadMoreView.setVisibility(View.VISIBLE);
        }

        isCanLoadMore = data.getPage() != 0;
        page++;

        ArrayList<ShopArticleData> list = data.getList();

        if (REFRESH.equals(extra)) { // 刷新
            gridAdapter.setData(list);
        } else if (LOAD_MORE.equals(extra)) { // 加载更多
            gridAdapter.addDatas(list);
        } else if (LOADING.equals(extra)) {
            gridAdapter.setData(list);
        }

        gridAdapter.notifyWrapperDataSetChanged();
    }

    public int getType() {
        return type;
    }

    public void setLoadingState(HLoadingStateView.State state) {
        loadingStateView.setStateShown(state);
    }

    public Map<String, String> getParams() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("pageSize", "12");
        return params;
    }

    public void onRefreshComplete() {
        mPtrFrame.refreshComplete();
    }

    public interface OnFollowGoodsPullListener {
        void onRecommendPull();
    }
}
