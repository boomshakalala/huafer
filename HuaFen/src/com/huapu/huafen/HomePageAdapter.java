package com.huapu.huafen;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.adapter.CommonWrapper;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommonPriceView;
import com.huapu.huafen.views.DashLineView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/11/29.
 */
public class HomePageAdapter extends CommonWrapper<HomePageAdapter.HomePageViewHolder> {

    private List<GoodsInfo> data;
    private BaseFragment fragment;

    public HomePageAdapter(BaseFragment fragment, List<GoodsInfo> data) {
        this.fragment = fragment;
        this.data = data;
    }

    public HomePageAdapter(BaseFragment fragment) {
        this(fragment, null);
    }

    public void setData(List<GoodsInfo> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<GoodsInfo> data) {
        if (data == null) {
            data = new ArrayList<GoodsInfo>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    @Override
    public HomePageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomePageViewHolder viewHolder = new HomePageViewHolder(LayoutInflater.from(fragment.getActivity()).
                inflate(R.layout.item_home_page, parent, false));
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) viewHolder.ivPhoto.getLayoutParams();
        lp.height = CommonUtils.getScreenWidth() / 2;
        viewHolder.ivPhoto.setLayoutParams(lp);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HomePageViewHolder holder, int position) {
        final GoodsInfo item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        if (item != null) {

            if (!ArrayUtil.isEmpty(item.getGoodsImgs())) {
                String url = item.getGoodsImgs().get(0);
                String tag = (String) holder.ivPhoto.getTag();
                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    holder.ivPhoto.setTag(url);

                    ImageLoader.loadImage(holder.ivPhoto, url);
                }

            } else {
                holder.ivPhoto.setImageResource(R.drawable.default_pic);
            }

            holder.dlvGoodsName.setData(item.getGoodsBrand(), item.getGoodsName());
            holder.cpvPrice.setData(item);
            holder.tvLikeCount.setText(item.getWantCount() + "");
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class HomePageViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public SimpleDraweeView ivPhoto;
        public DashLineView dlvGoodsName;
        public CommonPriceView cpvPrice;
        public TextView tvLikeCount;

        public HomePageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.ivPhoto);
            this.dlvGoodsName = (DashLineView) itemView.findViewById(R.id.dlvGoodsName);
            this.cpvPrice = (CommonPriceView) itemView.findViewById(R.id.cpvPrice);
            this.tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
        }
    }
}
