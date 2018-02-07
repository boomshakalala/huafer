package com.huapu.huafen.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
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
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.pages.HomeRefreshBean;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.component.ViewGoods;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/10/13.
 */

public class IconRecycleView extends LinearLayout {


    private SimpleDraweeView image;
    private RecyclerView recyclerView;
    private float imageRatioOfWidth,imageRatioOfHeight,ratioOfWidth,ratioOfHeight;

    public LeftImageOnClickListenner getListenner() {
        return listenner;
    }

    public void setListenner(LeftImageOnClickListenner listenner) {
        this.listenner = listenner;
    }

    public interface LeftImageOnClickListenner {

        public void OnImageClick(SimpleDraweeView sdv);

    }

    public interface ItemOnClickListenner {

        public void OnItemClick(Item item);

    }

    private LeftImageOnClickListenner listenner;

    public IconRecycleView(Context context) {
        this(context, null);
    }

    public IconRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.image_left_recycle_right, this, true);
        if (attrs != null) {
            TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.common_attrs);
            // 0：一排3个；1：小
            ratioOfHeight = mTypedArray.getFloat(R.styleable.common_attrs_ratioOfHeight, 0f);
            ratioOfWidth = mTypedArray.getFloat(R.styleable.common_attrs_ratioOfWidth,0f);
            imageRatioOfHeight = mTypedArray.getFloat(R.styleable.common_attrs_imageRatioOfHeight,0f);
            imageRatioOfWidth = mTypedArray.getFloat(R.styleable.common_attrs_imageRatioOfWidth,0f);
        }
        resetSize(imageRatioOfWidth,imageRatioOfHeight,ratioOfWidth,ratioOfHeight);
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getListenner() != null) {
                    getListenner().OnImageClick(image);
                }
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://空闲状态
                        // ImageLoader.(getContext()).resumeLoadImage();
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING://自动滚
                        //ImageLoader.(getContext()).pauseLoadImage();
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

    private void resetSize(float imageRatioOfWidth,float imageRatioOfHeight,float ratioOfWidth,float ratioOfHeight){

        LinearLayout iconbg = (LinearLayout) findViewById(R.id.iconBg);
        LinearLayout.LayoutParams ilp = (LinearLayout.LayoutParams) iconbg.getLayoutParams();
        if (ratioOfHeight != 0)
            ilp.height = (int) (ratioOfHeight * CommonUtils.getScreenWidth());

        image = (SimpleDraweeView) findViewById(R.id.image);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) image.getLayoutParams();
        if (imageRatioOfWidth!=0){
            lp.width = (int) (imageRatioOfWidth * CommonUtils.getScreenWidth());

        }
        if (imageRatioOfHeight != 0)
            lp.height = (int) (imageRatioOfHeight * CommonUtils.getScreenWidth());

    }

    public void setLefeImage(String url) {
        image.setImageURI(url);
    }


    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }


    public class StarAdapter extends CommonWrapper<StarAdapter.StarViewHolder> {

        private ArrayList<Item> data;
        private Context context;
        private ItemOnClickListenner listenner;
        View footview;


        public StarAdapter(Context context) {
            this.context = context;
            data = new ArrayList<>();
            footview = LayoutInflater.from(context).inflate(R.layout.main_page_adapter_footer_middle, null);
            footview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IconRecycleView.this.getListenner().OnImageClick(null);
                }
            });
            setLoadMoreView(footview);
            ImageView more = (ImageView) footview.findViewById(R.id.moreimg);
        }

        public void setData(ArrayList<Item> data) {
            this.data = data;
            notifyWrapperDataSetChanged();
        }

        @Override
        public StarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new StarViewHolder(LayoutInflater.from(context).inflate(R.layout.item_star_icon, parent, false));
        }

        @Override
        public void onBindViewHolder(StarViewHolder holder, final int position) {
            final Item item = data.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getListenner() != null) {

                        getListenner().OnItemClick(item);
                    }
                }
            });

            String avatar = item.user.getAvatarUrl();
            ImageLoader.resizeSmall(holder.staravatar, avatar, 1);
            holder.name.setText(item.user.getUserName());
            if ("当前在线".equals(item.user.getLastVisitText())) {
                holder.status.setTextColor(Color.parseColor("#2db511"));
            } else {
                holder.status.setTextColor(Color.parseColor("#cccccc"));
            }
            holder.status.setText(item.user.getLastVisitText());

        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public ItemOnClickListenner getListenner() {
            return listenner;
        }

        public void setListenner(ItemOnClickListenner listenner) {
            this.listenner = listenner;
        }

        public class StarViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.staravatar)
            SimpleDraweeView staravatar;
            @BindView(R.id.status)
            TextView status;
            @BindView(R.id.name)
            TextView name;


            public StarViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                staravatar.getLayoutParams().width = (int) (75.0f / 750.0f * CommonUtils.getScreenWidth());
                staravatar.getLayoutParams().height = (int) (75.0f / 750.0f * CommonUtils.getScreenWidth());

            }
        }
    }

    public class OneYuanAdapter extends CommonWrapper<OneYuanAdapter.OneYuanViewHolder> {

        private ArrayList<Item> data;
        private Context context;
        private ItemOnClickListenner listenner;
        View footview;

        public OneYuanAdapter(Context context) {
            this.context = context;
            data = new ArrayList<Item>();
            //notifyDataSetChanged();
            footview = LayoutInflater.from(context).inflate(R.layout.main_page_adapter_footer_small, null);
            setLoadMoreView(footview);
            footview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IconRecycleView.this.getListenner().OnImageClick(null);
                }
            });
            ImageView more = (ImageView) footview.findViewById(R.id.moreimg);
            more.getLayoutParams().width = (int) (120.0f / 750.0f * CommonUtils.getScreenWidth());
            more.getLayoutParams().height = (int) (120.0f / 750.0f * CommonUtils.getScreenWidth());


        }

        public void setData(ArrayList<Item> data) {
            this.data = data;
            notifyWrapperDataSetChanged();
        }

        @Override
        public OneYuanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new OneYuanViewHolder(LayoutInflater.from(context).inflate(R.layout.item_total_one, parent, false));
        }

        @Override
        public void onBindViewHolder(OneYuanViewHolder holder, final int position) {
            final Item item = data.get(position);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getListenner() != null) {
                        getListenner().OnItemClick(item);
                    }
                }
            });
            if (position == 0) {
                holder.hat.setImageResource(R.drawable.top1);
                holder.hat.setVisibility(VISIBLE);

            } else if (position == 1) {
                holder.hat.setImageResource(R.drawable.top2);
                holder.hat.setVisibility(VISIBLE);

            } else if (position == 2) {
                holder.hat.setImageResource(R.drawable.top3);
                holder.hat.setVisibility(VISIBLE);
            } else {
                holder.hat.setVisibility(INVISIBLE);
            }
            String avatar = item.item.goodsImgs.get(0);
            //holder.itemimage.setImageURI(avatar);
            ImageLoader.resizeSmall(holder.itemimage, avatar, 1);
            holder.itemprice.setText(String.valueOf(item.item.sellPric));
            holder.itemprice.setVisibility(VISIBLE);
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        public ItemOnClickListenner getListenner() {
            return listenner;
        }

        public void setListenner(ItemOnClickListenner listenner) {
            this.listenner = listenner;
        }

        public class OneYuanViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.itemimage)
            SimpleDraweeView itemimage;
            @BindView(R.id.itemprice)
            TextView itemprice;

            @BindView(R.id.hat)
            ImageView hat;

            public OneYuanViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                itemimage.getLayoutParams().width = (int) (120.0f / 750.0f * CommonUtils.getScreenWidth());
                itemimage.getLayoutParams().height = (int) (120.0f / 750.0f * CommonUtils.getScreenWidth());

            }
        }
    }


    public  class VipAdapter extends CommonWrapper<VipAdapter.Holder>{
        private List<HomeRefreshBean.VipData>  data;
        private Context context;
        View footview;

        public VipAdapter(Context context) {
            this.data = new ArrayList<>();
            this.context = context;
            footview = LayoutInflater.from(context).inflate(R.layout.main_page_adapter_footer_middle, null);
            footview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    IconRecycleView.this.getListenner().OnImageClick(null);
                }
            });
            setLoadMoreView(footview);
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.item_home_vip,null));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            HomeRefreshBean.VipData vipData = data.get(position);
            if (vipData != null) {
                GoodsData goodsData = vipData.item;
                if (goodsData != null) {
                    holder.setData(goodsData);
                }

            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(ArrayList<HomeRefreshBean.VipData> data) {
            this.data = data;
            notifyWrapperDataSetChanged();
        }


        public class Holder extends RecyclerView.ViewHolder {
            public Holder(View itemView) {
                super(itemView);
                goodsView = (ViewGoods) itemView.findViewById(R.id.goodsView);
            }

            public ViewGoods goodsView;

            public void setData(GoodsData goodsData){
                goodsView.setData(goodsData);
            }
        }
    }

    public class PoemAdapter extends CommonWrapper<PoemAdapter.ViewHolder>{

        private Context context;
        private List<HomeRefreshBean.ActionData> data;
        View footview;

        public PoemAdapter(Context context) {
            this.context = context;
            data = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_page_poems,parent,false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            HomeRefreshBean.ActionData actionData = data.get(position);
            holder.setData(actionData);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setData(ArrayList<HomeRefreshBean.ActionData> data) {
            this.data = data;
            notifyWrapperDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            SimpleDraweeView simpleDraweeView;

            TextView title;

            TextView note;

            public ViewHolder(View itemView) {
                super(itemView);
                simpleDraweeView = (SimpleDraweeView) itemView.findViewById(R.id.itemimage);
                title = (TextView) itemView.findViewById(R.id.title);
                note = (TextView) itemView.findViewById(R.id.note);
            }

            public void setData(final HomeRefreshBean.ActionData actionData){
                float width = 450.0f / 750.0f * CommonUtils.getScreenWidth();
                float height = 252.0f / 750.0f * CommonUtils.getScreenWidth();

                simpleDraweeView.getLayoutParams().width = (int) width;
                simpleDraweeView.getLayoutParams().height = (int) height;

                float aspectRatio = width / height;

                if (actionData != null) {
                    simpleDraweeView.setImageURI(actionData.image);
                    ImageLoader.resizeMiddle(simpleDraweeView, actionData.image, aspectRatio);
                    title.setText(actionData.title.trim());
                    note.setText(actionData.note);
                    simpleDraweeView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActionUtil.dispatchAction(getContext(), actionData.action, actionData.target);
                        }
                    });
                }

            }
        }
    }


}
