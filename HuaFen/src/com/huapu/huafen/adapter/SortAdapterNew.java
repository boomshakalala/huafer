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
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.utils.ArrayUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/11/17.
 */
public class SortAdapterNew extends RecyclerView.Adapter<SortAdapterNew.SortViewHolder>{

    private Context mContext;
    private SortEntity[] data;

    public SortAdapterNew(Context context, SortEntity[] data){
        this.mContext = context;
        this.data = data;
    }

    public SortAdapterNew(Context context){
        this(context,null);
    }

    @Override
    public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new SortViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.sort_item_new,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(SortViewHolder sortViewHolder, final int position) {
        final SortEntity filterData = data[position];
        sortViewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(filterData,position);
                }

            }
        });
        String name = filterData.name;
        if(!TextUtils.isEmpty(name)){
            sortViewHolder.tvTitle.setText(name);
        }

        if(filterData.isCheck){//选中
            sortViewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            sortViewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_black));
            sortViewHolder.viewCheck.setVisibility(View.VISIBLE);
        }else{
            sortViewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.base_bg));
            sortViewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color_gray));
            sortViewHolder.viewCheck.setVisibility(View.INVISIBLE);
        }
    }


    public void setCheckItemByPosition(int position){
        if(!ArrayUtil.isEmpty(data)){
            for(int i = 0;i<data.length;i++){
                SortEntity item = data[i];
                if(i == position){
                    item.isCheck = true;
                }else{
                    item.isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.length;
    }

    public class SortViewHolder extends RecyclerView.ViewHolder{

        @BindView(R2.id.viewCheck) View viewCheck;
        @BindView(R2.id.tvTitle) TextView tvTitle;

        public SortViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public interface OnItemClickListener{

        void onItemClick(SortEntity filterData, int position);
    }

    private OnItemClickListener listener;


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
