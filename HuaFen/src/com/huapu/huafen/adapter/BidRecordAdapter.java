package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BidRecord;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.views.UserView;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/8/2.
 */

public class BidRecordAdapter extends RecyclerView.Adapter<BidRecordAdapter.BidRecordViewHolder> {

    private List<BidRecord> data;

    private Context context;

    private int goodsState;

    public BidRecordAdapter(Context context) {
        this.context = context;
    }

    public void setGoodsState(int goodsState) {
        this.goodsState = goodsState;
    }

    public void setData(List<BidRecord> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public BidRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BidRecordViewHolder(LayoutInflater.from(context).inflate(R.layout.bid_record_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BidRecordViewHolder holder, int position) {
        final BidRecord item = data.get(position);
        holder.avatar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.getUserId());
                context.startActivity(intent);
            }
        });
        String avatar = item.user.getAvatarUrl();
        holder.avatar.setImageURI(avatar);

        holder.userView.setData(item.user);

        holder.tvPrice.setText(String.valueOf("¥"+item.price));

        if(position==0){
            holder.tvState.setVisibility(View.VISIBLE);
            if(goodsState == 4 || goodsState ==3 || goodsState == 5){//已成交
                holder.tvState.setBackgroundResource(R.drawable.text_new_bg);
                holder.tvState.setPadding(CommonUtils.dp2px(3f),CommonUtils.dp2px(1f),CommonUtils.dp2px(3f),CommonUtils.dp2px(1f));
                holder.tvState.setTextColor(Color.parseColor("#FFFFFF"));
                holder.tvState.setTextSize(12);
                holder.tvState.setText("成交");
            }else{
                holder.tvState.setBackgroundColor(0);
                holder.tvState.setPadding(0,0,0,0);
                holder.tvState.setTextColor(Color.parseColor("#DE000000"));
                holder.tvState.setTextSize(14);
                holder.tvState.setText("最新出价");
            }
            holder.tvPrice.setTextColor(Color.parseColor("#ff6677"));
        }else{
            holder.tvState.setVisibility(View.GONE);
            holder.tvPrice.setTextColor(Color.parseColor("#DE000000"));
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class BidRecordViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar) SimpleDraweeView avatar;
        @BindView(R.id.tvPrice) TextView tvPrice;
        @BindView(R.id.tvState) TextView tvState;
        @BindView(R.id.userView) UserView userView;

        public BidRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
