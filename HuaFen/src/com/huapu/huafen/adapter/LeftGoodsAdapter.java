package com.huapu.huafen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.utils.ArrayUtil;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/20.
 */

public class LeftGoodsAdapter extends RecyclerView.Adapter<LeftGoodsAdapter.LeftGoodViewHolder> {


    private List<Cat> data;

    private Context context;

    public LeftGoodsAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Cat> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public LeftGoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LeftGoodViewHolder(LayoutInflater.from(context).inflate(R.layout.left_goods_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LeftGoodViewHolder holder, int position) {
        final Cat item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setCheck(item);
                if(onLeftItemClickListener!=null){
                    onLeftItemClickListener.onItemClick(item);
                }
            }
        });

        if(item.isCheck){
            holder.viewCheck.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else{
            holder.viewCheck.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.parseColor("#F7F9FB"));
        }

        holder.tvClass.setText(item.getName());
    }

    private void clearAllCheck(){
        if(!ArrayUtil.isEmpty(data)){
            for (Cat cat : data) {
                if(cat!=null){
                    cat.isCheck = false;
                }
            }
        }

    }

    private void setCheck(Cat cat){
        clearAllCheck();
        cat.isCheck  = true;
        notifyDataSetChanged();
    }

    public void setCheck(int firstId){
        if(data!=null){
            for (Cat cat:data) {
                if(cat.getCid() == firstId){
                    cat.isCheck = true;
                }else{
                    cat.isCheck = false;
                }
            }
            notifyDataSetChanged();
        }
    }


    public interface OnLeftItemClickListener {
        void onItemClick(Cat cat);
    }

    private OnLeftItemClickListener onLeftItemClickListener;

    public void setOnLeftItemClickListener(OnLeftItemClickListener onLeftItemClickListener) {
        this.onLeftItemClickListener = onLeftItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class LeftGoodViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.viewCheck) View viewCheck;
        @BindView(R.id.tvClass) TextView tvClass;

        public LeftGoodViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
