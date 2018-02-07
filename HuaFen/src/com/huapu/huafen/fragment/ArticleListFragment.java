package com.huapu.huafen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.huapu.huafen.R;
import com.huapu.huafen.adapter.ShopkeepersAdapter;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.beans.ShopkeepersData;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.presenter.ListPresenter;
import com.huapu.huafen.presenter.impl.ArticleListPresenterImpl;
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
 * 可刷新花语列表-瀑布流
 * Created by dengbin
 */
public class ArticleListFragment extends BaseFragment implements LoadMoreWrapper.OnLoadMoreListener {

    public final static int REQUEST_CODE_FOR_GOODS_DETAIL = 0x222;

    @BindView(R.id.ptr_view)
    PtrFrameLayout mPtrFrame;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.loading_state_view)
    HLoadingStateView loadingStateView;

    private ListPresenter mPresenter;
    private View view;
    private int page = 0;
    private View loadMoreView;
    private boolean isCanLoadMore;
    private int type;
    private ShopkeepersAdapter shopkeepersAdapter;

    public static ArticleListFragment newInstance(Bundle data) {
        ArticleListFragment listFragment = new ArticleListFragment();
        listFragment.setArguments(data);
        return listFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new ArticleListPresenterImpl(this);
        type = getArguments().getInt("type");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_refreshable_list, container, false);
        //初始化控件
        initViews(view);
        return view;
    }

    public void listViewToTop() {
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    @Override
    protected void loadResponse() {
        super.loadResponse();
        if (shopkeepersAdapter.getItemCount() == 0) {
            page = 0;
            mPresenter.getList(LOADING);
        }
    }

    private void initGridAdapter() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        loadMoreView = inflater.inflate(R.layout.load_layout, mRecyclerView, false);
        loadMoreView.setVisibility(View.INVISIBLE);

        View viewEmpty = ViewUtil.initImgEmptyView(mRecyclerView, R.mipmap.img_empty_follow_article);

        shopkeepersAdapter.setEmptyView(viewEmpty);
        shopkeepersAdapter.setLoadMoreView(loadMoreView);
        shopkeepersAdapter.setOnLoadMoreListener(this);
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
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
                int[] index = manager.findFirstCompletelyVisibleItemPositions(null);
                return index[0] == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = 0;
                mPresenter.getList(REFRESH);
            }
        });

        //设置布局管理者为瀑布流
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        // mRecyclerView.addItemDecoration(new StaggeredGridDecoration(ViewUtil.getTenDp()));
        shopkeepersAdapter = new ShopkeepersAdapter(getActivity());
        mRecyclerView.setAdapter(shopkeepersAdapter.getWrapperAdapter());

        shopkeepersAdapter.setOnLoadMoreListener(this);

        ViewUtil.setOffItemAnimator(mRecyclerView);

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
            shopkeepersAdapter.setData(list);
        } else if (LOAD_MORE.equals(extra)) { // 加载更多
            shopkeepersAdapter.addDatas(list);
        } else if (LOADING.equals(extra)) {
            shopkeepersAdapter.setData(list);
        }

        shopkeepersAdapter.notifyWrapperDataSetChanged();
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
