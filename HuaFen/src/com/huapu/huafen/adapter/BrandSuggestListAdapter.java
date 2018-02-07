package com.huapu.huafen.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Brand;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/3/24.
 */

public class BrandSuggestListAdapter extends CommonWrapper<BrandSuggestListAdapter.BrandSuggestListViewHolder> {

    private Context context;
    private List<Brand> data ;

    public BrandSuggestListAdapter(Context context, List<Brand> data) {
        this.context = context;
        this.data = data;
    }

    public BrandSuggestListAdapter(Context context) {
        this(context,null);
    }


    public void setData(List<Brand> data){
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    @Override
    public BrandSuggestListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BrandSuggestListViewHolder(LayoutInflater.from(context).inflate(R.layout.suggest_result_item,parent,false));
    }

    @Override
    public void onBindViewHolder(BrandSuggestListViewHolder holder, int position) {
        final Brand item = data.get(position);
        if(!TextUtils.isEmpty(item.brandName)){
            holder.tvBrand.setText(item.brandName);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    public static class BrandSuggestListViewHolder extends RecyclerView.ViewHolder{

        @BindView(R2.id.tvBrand) TextView tvBrand ;

        public BrandSuggestListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public interface OnItemClickListener{
        void onItemClick(Brand brand);
    }

    private OnItemClickListener onItemClickListener ;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
