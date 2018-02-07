package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.OrderHistoryResult;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.OrderHistoryListLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/6/29.
 */

public class OrderHistoryAdapter extends CommonWrapper<OrderHistoryAdapter.OrderHistoryViewHolder> {

    private Context context;
    private List<OrderHistoryResult.OrderOperate> data;

    public OrderHistoryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<OrderHistoryResult.OrderOperate> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    @Override
    public OrderHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.order_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(OrderHistoryViewHolder holder, int position) {
        final OrderHistoryResult.OrderOperate item = data.get(position);
        if(position == 0){
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.base_pink));
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.base_pink));
            holder.llRedBallLayout.setVisibility(View.VISIBLE);
            holder.llGrayBallLayout.setVisibility(View.GONE);
            holder.orderHistoryListLayout.setTextColor(R.color.text_black);
        }else{
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.text_black_light));
            holder.tvTime.setTextColor(context.getResources().getColor(R.color.text_black_light));
            holder.llRedBallLayout.setVisibility(View.GONE);
            holder.llGrayBallLayout.setVisibility(View.VISIBLE);
            holder.orderHistoryListLayout.setTextColor(R.color.text_black_light);
        }
        if(!TextUtils.isEmpty(item.title)){
            holder.tvTitle.setText(item.title);
        }
        if(!TextUtils.isEmpty(item.createdAt)){
            holder.tvTime.setText(item.createdAt);
        }

        if(!ArrayUtil.isEmpty(item.properties)){
            holder.orderHistoryListLayout.setVisibility(View.VISIBLE);
            holder.orderHistoryListLayout.setData(item.properties);
        }else{
            holder.orderHistoryListLayout.setVisibility(View.GONE);
        }

        if(position ==data.size()-1){
            holder.llContainer.setPadding(0,0,0,CommonUtils.dp2px(10f));
        }else{
            holder.llContainer.setPadding(0,0,0,0);
        }
    }

    @Override
    public int getItemCount() {
        return data==null?0:data.size();
    }

    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.llRedBallLayout) LinearLayout llRedBallLayout;
        @BindView(R.id.llGrayBallLayout) RelativeLayout llGrayBallLayout;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvTime) TextView tvTime;
        @BindView(R.id.orderHistoryListLayout) OrderHistoryListLayout orderHistoryListLayout;
        @BindView(R.id.llContainer) LinearLayout llContainer;

        public OrderHistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
