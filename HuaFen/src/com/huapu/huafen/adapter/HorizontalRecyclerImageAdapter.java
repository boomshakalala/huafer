package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ViewUtil;

import java.util.List;

/**
 * 展示横向图片相当于Gallery
 */
public class HorizontalRecyclerImageAdapter extends RecyclerView.Adapter<HorizontalRecyclerImageAdapter.ViewHolder> {

    private List<VBanner> data;
    private Context context;
    private Fragment fragment;

    private long goodsId;

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public HorizontalRecyclerImageAdapter(Context context) {
        this.context = context;
    }

    public HorizontalRecyclerImageAdapter(Fragment fragment) {
        this.fragment = fragment;
        this.context = fragment.getActivity();
    }

    public void setData(List<VBanner> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_goods, viewGroup, false));
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        VBanner item = data.get(position);

        ViewUtil.setImgMiddle(viewHolder.mImg, item.imgUrl, 1);

        viewHolder.mImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goodsId + "");
                if (fragment == null) {
                    context.startActivity(intent);
                } else {
                    fragment.startActivity(intent);
                }
            }
        });

        if (position == 0) {
            viewHolder.itemView.setPadding(CommonUtils.dp2px(10), 0, 0, 0);
        } else {
            viewHolder.itemView.setPadding(0, 0, 0, 0);
        }

        if (item.type == 1) {
            viewHolder.ivPlay.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivPlay.setVisibility(View.GONE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView mImg;
        ImageView ivPlay;

        public ViewHolder(View itemView) {
            super(itemView);
            mImg = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
        }
    }
}
