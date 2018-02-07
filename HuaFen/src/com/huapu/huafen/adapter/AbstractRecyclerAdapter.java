package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.utils.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lvgy on 16/8/1.
 */
public abstract class AbstractRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    /**
     * item 类型
     */
    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_HEADER = 1;//头部--支持头部增加一个headerView
    public final static int TYPE_FOOTER = 2;//底部--往往是loading_more
    public final static int TYPE_LIST = 3;//代表item展示的模式是list模式
    public final static int TYPE_STAGGER = 4;//代码item展示模式是网格模式
    public final static int TYPE_GRID = 5; //girdview
    private static final int TYPE_HEADER_TWO = 500;
    protected Context context;
    protected List<T> listData;
    protected LayoutInflater mLayoutInflater;
    protected OnRecyclerViewItemClickListener mOnItemClickListener = null;
    protected boolean mIsFooterEnable = false;//是否允许加载更多
    protected boolean mIsHeaderEnable = false;
    protected View headerView = null;
    protected int headerNumber = 1;
    /**
     * 标记是否正在加载更多，防止再次调用加载更多接口
     */
    private boolean mIsLoadingMore;
    /**
     * 标记加载更多的position
     */
    private int mLoadMorePosition;
    /**
     * 加载更多的监听-业务需要实现加载数据
     */
    private LoadMoreListener mListener;
    private ProgressBar progressBar;
    private TextView showState;
    private LinearLayout loadingMoreLayout;
    private RecyclerView recyclerView;
    private boolean mIsFinishLoad = false;


    public AbstractRecyclerAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        listData = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
        this.recyclerView = recyclerView;
        init();
    }

    private void init() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mIsFinishLoad) {
                    if (null != mListener && mIsFooterEnable && !mIsLoadingMore && RecyclerView.SCROLL_STATE_IDLE == newState) {
                        int lastVisiblePosition = getLastVisiblePosition();
                        if (lastVisiblePosition + 1 == AbstractRecyclerAdapter.this.getItemCount()) {
                            setLoadingMore(true);
                            mLoadMorePosition = lastVisiblePosition;
                            mListener.onLoadMore();
                        }
                    }
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
    }

    @Override
    public int getItemCount() {
        int count = listData == null?0:listData.size();

        if (mIsFooterEnable) count++;
        if (mIsHeaderEnable) count++;
        return count;
    }

    public void removeByPosition(int position) {
        listData.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 设置加载更多的监听
     *
     * @param listener
     */
    public void setLoadMoreListener(LoadMoreListener listener) {
        mListener = listener;
    }

    public void appendData(List<T> list) {
        Logger.e("get size:" + listData.size());
        listData.addAll(list);
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        if (position < listData.size())
            return listData.get(position);
        else
            return null;
    }

    public void setRes(List<T> list) {
//        listData.clear();
//        listData.addAll(list);
        listData = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(headerView);
        }
        if (viewType == TYPE_FOOTER) {
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (spanSizeLookup != null) {
                            return spanSizeLookup.getSpanSize(position);
                        }
                        int type = getItemViewType(position);
                        if (type == TYPE_FOOTER || type == TYPE_HEADER || type == TYPE_HEADER_TWO || type == TYPE_GRID) {
                            return gridLayoutManager.getSpanCount();
                        }

                        return 1;
                    }
                });
            }
            return new FooterViewHolder(initFootLayout());
        } else { // type normal
            return this.onCreateViewHolder(parent, viewType, true);
        }
    }

    private View initFootLayout() {
        View view = LayoutInflater.from(context).inflate(
                R.layout.list_foot_loading, null, false);
        progressBar = (ProgressBar) view.findViewById(R.id.loadMoreProgressBar);
        showState = (TextView) view.findViewById(R.id.showStateText);
        loadingMoreLayout = (LinearLayout) view.findViewById(R.id.loadMoreLayout);
        return view;
    }

    public void clearAllData() {
        listData.clear();
        notifyDataSetChanged();
    }


    protected abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem);

    protected abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position, boolean isItem);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type != TYPE_FOOTER && type != TYPE_HEADER) {
            this.onBindViewHolder(holder, position, true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int headerPosition = 0;
        int footerPosition = getItemCount() - 1;

        if (headerPosition == position && mIsHeaderEnable && null != headerView) {
            return TYPE_HEADER;
        }
        if (footerPosition == position && mIsFooterEnable) {
            return TYPE_FOOTER;
        }
        /**
         * 这么做保证layoutManager切换之后能及时的刷新上对的布局
         */
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            return TYPE_LIST;
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            return TYPE_STAGGER;
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            return TYPE_GRID;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_FOOTER || type == TYPE_HEADER) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        return 1;
                    }
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isStaggeredGridLayout(holder)) {
            handleLayoutIfStaggeredGridLayout(holder, holder.getLayoutPosition());
        }
    }


    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return true;
        }
        return false;
    }

    protected void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER || getItemViewType(position) == TYPE_FOOTER) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(true);
        }
    }

    /**
     * 设置是否支持自动加载更多
     *
     * @param autoLoadMore
     */
    public void setAutoLoadMoreEnable(boolean autoLoadMore) {
        mIsFooterEnable = autoLoadMore;
        if (null == progressBar || null == showState) {
            initFootLayout();
        }
        if (autoLoadMore) {
            showState.setText("加载中…");
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    public void loadFinish(boolean mIsFinishLoad) {
        this.mIsFinishLoad = mIsFinishLoad;
        if (mIsFinishLoad) {
            mIsLoadingMore = false;
            if (null == progressBar || null == showState) {
                initFootLayout();
            }
            showState.setText("");
            progressBar.setVisibility(View.GONE);
        }
    }

    public void initLoading(boolean mIsFinishLoad) {
        this.mIsFinishLoad = mIsFinishLoad;
        if (null == progressBar || null == showState) {
            initFootLayout();
        }

        progressBar.setVisibility(View.VISIBLE);
        showState.setText(context.getResources().getString(R.string.loading));

    }


    /**
     * 显示加载更多失败
     *
     * @param showLoadMoreFailed
     */
    public void showLoadMoreFailed(boolean showLoadMoreFailed) {
        if (null == progressBar || null == showState) {
            initFootLayout();
        }
        if (showLoadMoreFailed) {
            showState.setText(context.getResources().getString(R.string.network_connect_error));
            progressBar.setVisibility(View.GONE);
            loadingMoreLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener && !mIsFinishLoad) {
                        progressBar.setVisibility(View.VISIBLE);
                        showState.setText(context.getResources().getString(R.string.loading));
                        mListener.onLoadMore();
                    }
                }
            });

        } else {
            progressBar.setVisibility(View.VISIBLE);
            showState.setText(context.getResources().getString(R.string.loading));
        }
    }

    public void setHeaderEnable(boolean enable) {
        mIsHeaderEnable = enable;
    }

    /**
     * 通知更多的数据已经加载
     * <p>
     * 每次加载完成之后添加了Data数据，用notifyItemRemoved来刷新列表展示，
     * 而不是用notifyDataSetChanged来刷新列表
     */
    public void notifyMoreFinish() {
        if (!mIsFinishLoad) {
            this.notifyItemRemoved(mLoadMorePosition);
        }

        mIsLoadingMore = false;
    }

    public void addHeaderView(View headerView) {
        this.headerView = headerView;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    private int getLastVisiblePosition() {
        int position;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            position = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof GridLayoutManager) {
            position = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(new int[staggeredGridLayoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = layoutManager.getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    private int getMaxPosition(int[] positions) {
        int maxPosition = Integer.MIN_VALUE;
        for (int position : positions) {
            maxPosition = Math.max(maxPosition, position);
        }
        return maxPosition;
    }

    /**
     * 设置正在加载更多
     *
     * @param loadingMore
     */
    public void setLoadingMore(boolean loadingMore) {
        this.mIsLoadingMore = loadingMore;
        if (null == progressBar || null == showState) {
            initFootLayout();
        }
        if (loadingMore) {
            progressBar.setVisibility(View.VISIBLE);
            showState.setText(context.getResources().getString(R.string.loading));
        }

    }

    /**
     * 加载更多监听
     */
    public interface LoadMoreListener {
        /**
         * 加载更多
         */
        void onLoadMore();
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
}
