package com.huapu.huafen.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

public class BrandSuggestListAdapterNew extends CommonWrapper<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> data ;

    private enum ItemType{
        BRAND,
        STRING_BRAND,
        ;

    }
    public BrandSuggestListAdapterNew(Context context, List<Object> data) {
        this.context = context;
        this.data = data;
    }

    public BrandSuggestListAdapterNew(Context context) {
        this(context,null);
    }


    public void setData(List<Object> data){
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = data.get(position);
        if(obj instanceof Brand){
            return ItemType.BRAND.ordinal();
        }else{
            return ItemType.STRING_BRAND.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ItemType.BRAND.ordinal()){
            return new BrandSuggestListViewHolder(LayoutInflater.from(context).inflate(R.layout.suggest_result_item,parent,false));
        }else{
            return new StringHolder(LayoutInflater.from(context).inflate(R.layout.brand_suggest,parent,false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Object item = data.get(position);
        int type = getItemViewType(position);
        if(type == ItemType.BRAND.ordinal()){
            final Brand brand = (Brand) item;
            BrandSuggestListViewHolder viewHolder = (BrandSuggestListViewHolder)holder;
            if(!TextUtils.isEmpty(brand.brandName)){
                viewHolder.tvBrand.setText(brand.brandName);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(brand);
                    }
                }
            });
        }else{
            final String brand = (String) item;
            StringHolder viewHolder = (StringHolder)holder;
            viewHolder.tvInputBrand.setText(brand);
            viewHolder.tvAddBrand.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(mOnEmptyBrandClickListener!=null){
                        mOnEmptyBrandClickListener.onClick(brand);
                    }
                }
            });
        }
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

    public static class StringHolder extends RecyclerView.ViewHolder{

        @BindView(R2.id.tvInputBrand) TextView tvInputBrand;//空搜索提示内容keyword
        @BindView(R2.id.tvAddBrand) TextView tvAddBrand;//选择品牌按钮

        public StringHolder(View itemView) {
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

    public interface OnEmptyBrandClickListener{
        void onClick(String brand);
    }

    private OnEmptyBrandClickListener mOnEmptyBrandClickListener;


    public void setOnEmptyBrandClickListener(OnEmptyBrandClickListener OnEmptyBrandClickListener) {
        this.mOnEmptyBrandClickListener = OnEmptyBrandClickListener;
    }
}
