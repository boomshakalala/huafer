package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.beans.WalletResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/16.
 */
public class WalletListAdapter extends CommonWrapper<WalletListAdapter.ViewHolder> {

    private List<WalletResult.Transaction> data;

    private Context context;

    public WalletListAdapter(Context context, List<WalletResult.Transaction> data) {
        this.context = context;
        this.data = data;
    }

    public WalletListAdapter(Context context) {
        this(context, null);
    }

    public void setData(List<WalletResult.Transaction> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<WalletResult.Transaction> data) {
        if (this.data == null) {
            this.data = new ArrayList<WalletResult.Transaction>();
        }

        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_wallet_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WalletResult.Transaction item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, item.orderId);
                context.startActivity(intent);
            }
        });

        String url = item.goodsImageUrl;
        String tag = (String) holder.ivHeader.getTag();
        if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
            holder.ivHeader.setTag(url);

            ImageLoader.resizeSmall(holder.ivHeader, item.goodsImageUrl, 1);
        }

        String ss1 = "¥";
        String ss2 = String.valueOf(item.amount);
        SpannableString spannableString = new SpannableString(ss1 + ss2);
        spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(12)), 0, ss1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new AbsoluteSizeSpan(CommonUtils.dp2px(16)), ss1.length(), ss1.length() + ss2.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tvPrice.setText(spannableString);


        if (item.type == 1) {//买 省
            holder.tvSaveOrEarned.setText("(省)");
            holder.tvFlag.setText("买");
            holder.tvFlag.setBackgroundResource(R.drawable.buy_bg);
        } else if (item.type == 2) {//卖 赚
            holder.tvSaveOrEarned.setText("(赚)");
            holder.tvFlag.setText("卖");
            holder.tvFlag.setBackgroundResource(R.drawable.sell_bg);
        }

        if (!TextUtils.isEmpty(item.goodsName)) {
            holder.tvDes.setText(item.goodsName);
        }


        try {
            String time = DateTimeUtils.getYearMonthDay(item.createdAt);
            if (!TextUtils.isEmpty(time)) {
                holder.tvDate.setText(time);
            }
        } catch (Exception e) {

        }

        if (position == 0) {
            holder.itemView.setBackgroundResource(R.drawable.shape_grey_bg);
            holder.rlLayout.setBackgroundResource(R.drawable.left_right_top_round_shape_white);
            holder.itemView.setPadding(0, CommonUtils.dp2px(10), 0, 0);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.shape_grey_bg);
            holder.rlLayout.setBackgroundResource(R.drawable.shape_white);
            holder.itemView.setPadding(0, 0, 0, 0);
        }


    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlLayout;
        public SimpleDraweeView ivHeader;
        public TextView tvPrice;
        public TextView tvSaveOrEarned;
        public TextView tvDate;
        public TextView tvDes;
        public TextView tvFlag;//买 卖
        public View line;

        public ViewHolder(View itemView) {
            super(itemView);
            this.rlLayout = (RelativeLayout) itemView.findViewById(R.id.rlLayout);
            this.ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            this.tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            this.tvSaveOrEarned = (TextView) itemView.findViewById(R.id.tvSaveOrEarned);
            this.tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            this.tvDes = (TextView) itemView.findViewById(R.id.tvDes);
            this.tvFlag = (TextView) itemView.findViewById(R.id.tvFlag);
            this.line = itemView.findViewById(R.id.line);
        }
    }
}
