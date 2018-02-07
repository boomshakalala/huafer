package com.huapu.huafen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.MomentCategoryBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qwe on 2017/4/26.
 */

public class MomentCategoryAdapter extends RecyclerView.Adapter<MomentCategoryAdapter.MomentCategoryViewHolder> {


    private List<MomentCategoryBean.ObjBean.CatsBean> beanList;

    private OnRecyclerViewListener onRecyclerViewListener;

    public MomentCategoryAdapter() {
        this.beanList = new ArrayList<>();
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public MomentCategoryBean.ObjBean.CatsBean getItem(int position) {
        return beanList.get(position);
    }

    public void setData(List<MomentCategoryBean.ObjBean.CatsBean> list) {
        this.beanList = list;
        notifyDataSetChanged();
    }

    @Override
    public MomentCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MomentCategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_moment_category, null));
    }

    @Override
    public void onBindViewHolder(final MomentCategoryViewHolder holder, final int position) {
        final MomentCategoryBean.ObjBean.CatsBean categoryBean = beanList.get(position);
        holder.position = position;
        holder.cateGoryName.setText(categoryBean.getName());
        holder.catImage.setImageURI(categoryBean.getUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null != beanList && beanList.size() > 0) {
            return beanList.size();
        }
        return 0;
    }

    public interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    public static class MomentCategoryViewHolder extends RecyclerView.ViewHolder {
        public int position;
        @BindView(R.id.catImage)
        SimpleDraweeView catImage;
        @BindView(R.id.cateGoryName)
        TextView cateGoryName;

        public MomentCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
