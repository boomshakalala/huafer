package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.SortEntity;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.List;

/**
 * Created by admin on 2016/11/17.
 */
public class SortAdapter extends RecyclerView.Adapter<SortAdapter.SortViewHolder>{

    private Context mContext;
    private SortEntity[] data;

    public SortAdapter(Context context, SortEntity[] data){
        this.mContext = context;
        this.data = data;
    }

    public SortAdapter(Context context){
        this(context,null);
    }

    @Override
    public SortViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new SortViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.filter_item_layout,viewGroup,false));
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

        sortViewHolder.underLine.setVisibility(View.VISIBLE);
        if(filterData.isCheck){//选中
            sortViewHolder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_pink));
            sortViewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.base_pink));
        }else{
            sortViewHolder.tvTitle.setTextColor(mContext.getResources().getColor(R.color.text_color));
            sortViewHolder.underLine.setBackgroundColor(mContext.getResources().getColor(R.color.base_tab_bar_divider));
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

        public View itemView;
        public TextView tvTitle;
        public View underLine;

        public SortViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            this.underLine = itemView.findViewById(R.id.underLine);
        }
    }


    public interface OnItemClickListener{

        void onItemClick(SortEntity filterData,int position);
    }

    private OnItemClickListener listener;


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}
