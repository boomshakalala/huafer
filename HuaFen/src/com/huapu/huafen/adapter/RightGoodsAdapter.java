package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.CommonUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/18.
 */

public class RightGoodsAdapter extends RecyclerView.Adapter<RightGoodsAdapter.OneClassificationSecondViewHolder>{

    private Context context;
    private List<Cat> data;

    private int firstId;
    public RightGoodsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Cat> data,int firstId) {
        this.data = data;
        this.firstId = firstId;
        notifyDataSetChanged();
    }

    @Override
    public OneClassificationSecondViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OneClassificationSecondViewHolder viewHolder = new OneClassificationSecondViewHolder(LayoutInflater.from(context).inflate(R.layout.one_yuan_classification_item,parent,false));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
        params.height = (CommonUtils.getScreenWidth()*2/3-3*CommonUtils.dp2px(10f))/2;
        int size = params.height - 2 * CommonUtils.dp2px(10f) - 20;
        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams) viewHolder.goodsPic.getLayoutParams();
        params1.width = params1.height = size;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OneClassificationSecondViewHolder holder, int position) {

        final Cat item=data.get(position);
        if(item!=null){
            String name = item.getName();
            if(!TextUtils.isEmpty(name)){
                holder.tvName.setText(name);
            }

            String tag = (String) holder.goodsPic.getTag();
            String icon = item.getIcon();
            if(tag == null || !tag.equals(icon)){
                holder.goodsPic.setImageURI(icon);
                holder.goodsPic.setTag(icon);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(onRightItemClickListener!=null){
                        onRightItemClickListener.onItemClick(item,firstId);
                    }
                }
            });
        }

    }

    public interface OnRightItemClickListener {
        void onItemClick(Cat cat,int firstId);
    }


    private OnRightItemClickListener onRightItemClickListener;

    public void setOnRightItemClickListener(OnRightItemClickListener onRightItemClickListener) {
        this.onRightItemClickListener = onRightItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data == null ?0:data.size();
    }

    public class OneClassificationSecondViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.tvName)
        TextView tvName;
        @BindView(R2.id.goodsPic)
        SimpleDraweeView goodsPic;

        public OneClassificationSecondViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
