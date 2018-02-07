package com.huapu.huafen.views;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommonWrapper;
import com.huapu.huafen.beans.HomeResult;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/10/13.
 */

public class TitleRecycleView extends LinearLayout {


    @BindView(R.id.tagrecycyleview)
    RecyclerView tagrecycyleview;
    @BindView(R.id.recycyleview)
    RecyclerView recycyleview;
    @BindView(R.id.title)
    HomeTitleBar titleBar;


    public TitleClickListenner getListenner() {
        return listenner;
    }

    public void setListenner(TitleClickListenner listenner) {
        this.listenner = listenner;
    }


    public interface TitleClickListenner {

        public void OnTitleClick(Object o);
    }

    private TitleClickListenner listenner;


    public TitleRecycleView(@NonNull Context context) {
        this(context, null);
    }

    public TitleRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void hideMore() {
        titleBar.hideMore();
    }

    public void hideTag() {
        tagrecycyleview.setVisibility(GONE);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.main_page_vip, this, true);
        ButterKnife.bind(this);
        LinearLayoutManager taglinearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        tagrecycyleview.setLayoutManager(taglinearLayoutManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recycyleview.setLayoutManager(linearLayoutManager);

        titleBar.setListenner(new HomeTitleBar.TitleClickListenner() {
            @Override
            public void OnTitleClick(Object o) {
                if (listenner != null) {
                    listenner.OnTitleClick(new Object());
                }
            }
        });
        recycyleview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://空闲状态
                        //ImageLoader.(getContext()).resumeLoadImage();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://自动滚
                        // ImageLoader.(getContext()).pauseLoadImage();
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

    public void setTitleimage(int resid) {
        titleBar.setTitleimage(resid);
    }

    public void setIndicator(int resid) {
        titleBar.setIndicator(resid);
    }


    public void setTitleAdapter(TitleAdapter titleAdapter) {

        tagrecycyleview.setAdapter(titleAdapter);
    }

    public void setContentAdapter(RecyclerView.Adapter vipAdapter) {
        recycyleview.setAdapter(vipAdapter);
    }

    public interface ItemOnclickListener {
        public void OnItemClick(HomeResult.CatData data);

    }

    public interface ItemClickListener {
        public void OnItemClick(Item item);

        public void OnItemClick(Object item);

    }


    public class VipAdapter extends CommonWrapper<VipAdapter.VipViewHolder> {

        private ArrayList<Item> data;
        private Context context;
        private ItemClickListener listener;
        View footview;

        public VipAdapter(Context context) {
            this.context = context;
            data = new ArrayList<Item>();
            footview = LayoutInflater.from(context).inflate(R.layout.main_page_adapter_footerview, null);
            setLoadMoreView(footview);
            footview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TitleRecycleView.this.getListenner().OnTitleClick(null);
                }
            });
            ImageView more = (ImageView) footview.findViewById(R.id.moreimg);
            more.getLayoutParams().width = (int) (180.0f / 750.0f * CommonUtils.getScreenWidth());
            more.getLayoutParams().height = (int) (180.0f / 750.0f * CommonUtils.getScreenWidth());

        }

        public void setData(ArrayList<Item> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public VipViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VipViewHolder(LayoutInflater.from(context).inflate(R.layout.main_page_vip_item, parent, false));
        }

        @Override
        public void onBindViewHolder(VipViewHolder holder, final int position) {
            final Item item = data.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.OnItemClick(item);
                    }
                }
            });
            String avatar = item.item.goodsImgs.get(0);
            ImageLoader.resizeSmall(holder.itemimage, avatar, 1);
            //holder.itemimage.setImageURI(avatar);
            holder.itemprice.setText(String.valueOf(item.item.sellPric));
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public ItemClickListener getListener() {
            return listener;
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        public class VipViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.itemimage)
            SimpleDraweeView itemimage;
            @BindView(R.id.itemprice)
            TextView itemprice;


            public VipViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemimage.getLayoutParams().width = (int) (180.0f / 750.0f * CommonUtils.getScreenWidth());
                itemimage.getLayoutParams().height = (int) (180.0f / 750.0f * CommonUtils.getScreenWidth());


            }
        }
    }

    public class TitleAdapter extends CommonWrapper<TitleAdapter.TitleViewHolder> {


        private ArrayList<HomeResult.CatData> data;
        private Context context;
        ItemOnclickListener listener;


        public TitleAdapter(Context context) {
            this.context = context;
            data = new ArrayList<HomeResult.CatData>();
            notifyDataSetChanged();
        }

        public void setOnclickListener(ItemOnclickListener listener) {
            this.listener = listener;
        }


        public void setData(ArrayList<HomeResult.CatData> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public TitleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TitleViewHolder(LayoutInflater.from(context).inflate(R.layout.main_page_vip_title, parent, false));
        }

        @Override
        public void onBindViewHolder(TitleViewHolder holder, final int position) {
            final HomeResult.CatData item = data.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        if (!item.select) {
                            for (int i = 0; i < data.size(); i++) {
                                HomeResult.CatData item = data.get(i);
                                item.select = false;
                            }
                            item.select = true;
                            notifyDataSetChanged();
                            listener.OnItemClick(item);
                        }
                    }
                }
            });

            if (item.select) {
                holder.title.setTextColor(Color.parseColor("#FF6677"));
            } else {
                holder.title.setTextColor(Color.parseColor("#8A000000"));
            }
            holder.title.setText(item.name);
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public class TitleViewHolder extends RecyclerView.ViewHolder {


            @BindView(R.id.title)
            TextView title;


            public TitleViewHolder(View itemView) {
                super(itemView);
//                if (data != null && data.size() >= 4) {
//                    itemView.getLayoutParams().width = CommonUtils.dip2px(context, 90.00f);
//                    itemView.getLayoutParams().height = CommonUtils.dip2px(context, 90.00f);
//                }
                ButterKnife.bind(this, itemView);
            }
        }
    }


    public class SubtleAdapter extends CommonWrapper<SubtleAdapter.SubtleViewHolder> {


        private ArrayList<HomeResult.ActionData> data;
        private Context context;
        private ItemClickListener listener;


        public SubtleAdapter(Context context) {
            this.context = context;
            data = new ArrayList<HomeResult.ActionData>();
            notifyDataSetChanged();
        }

        public void setData(ArrayList<HomeResult.ActionData> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        @Override
        public SubtleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SubtleViewHolder(LayoutInflater.from(context).inflate(R.layout.main_page_subtle, parent, false));
        }

        @Override
        public void onBindViewHolder(SubtleViewHolder holder, final int position) {
            final HomeResult.ActionData item = data.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getListener() != null) {
                        getListener().OnItemClick(item);
                    }
                }
            });

            String avatar = item.image;
            holder.itemimage.setImageURI(avatar);
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public ItemClickListener getListener() {
            return listener;
        }

        public void setListener(ItemClickListener listener) {
            this.listener = listener;
        }

        public class SubtleViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.itemimage)
            SimpleDraweeView itemimage;


            public SubtleViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemimage.getLayoutParams().width = (int) (408.0f / 750.0f * CommonUtils.getScreenWidth());
                //itemimage.getLayoutParams().height = (int) (itemimage.getLayoutParams().width / 2.18f);
            }
        }
    }


}
