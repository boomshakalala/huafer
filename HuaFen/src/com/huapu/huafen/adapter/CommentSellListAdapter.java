package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.beans.Comments;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.OrderData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommentUserView;
import com.huapu.huafen.views.DashLineView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lalo on 2016/10/20.
 */
public class CommentSellListAdapter extends CommonWrapper<CommentSellListAdapter.CommentListHolder> {

    private Context mContext;
    private BaseFragment fragment;
    private List<Comments> mComments = new ArrayList<Comments>();

    public CommentSellListAdapter(BaseFragment fragment, List<Comments> comments) {
        super();
        this.fragment = fragment;
        this.mContext = this.fragment.getContext();
        this.mComments = comments;
    }

    public void setData(List<Comments> comments) {
        this.mComments = comments;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<Comments> comments) {
        if (mComments == null) {
            mComments = new ArrayList<Comments>();
        }
        this.mComments.addAll(comments);
        notifyWrapperDataSetChanged();
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(mComments);
    }

    @Override
    public CommentListHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new CommentListHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comment_list_sell, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentListHolder commentListHolder, int position) {
        final Comments comment = mComments.get(position);
        final GoodsData goodsData = comment.getGoodsData();
        if (goodsData != null) {
            commentListHolder.line1.setVisibility(View.VISIBLE);
            commentListHolder.layoutGoods.setVisibility(View.VISIBLE);
            commentListHolder.dlvGoodsName.setData(goodsData.getBrand(), goodsData.getName());
            List<String> list = goodsData.getGoodsImgs();
            if (!ArrayUtil.isEmpty(list)) {
                String url = list.get(0);
                ImageLoader.resizeSmall(commentListHolder.ivGoodPic, url, 1);
            }

        } else {
            commentListHolder.line1.setVisibility(View.GONE);
            commentListHolder.layoutGoods.setVisibility(View.GONE);
        }


        final OrderData orderData = comment.getOrderData();
        if (orderData != null) {
            int price = orderData.getPrice();
            String format = String.format(mContext.getString(R.string.price_tag), price);
            commentListHolder.tvPrice.setText(Html.fromHtml(format));

            commentListHolder.layoutGoods.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (orderData.getOrderId() <= 0) {
                        return;
                    }
                    Intent intent = new Intent(mContext, OrderDetailActivity.class);
                    intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderData.getOrderId());
                    mContext.startActivity(intent);
                }
            });
        }

        if (comment.getComment() != null) {
            commentListHolder.cuvComment.setVisibility(View.VISIBLE);
            commentListHolder.cuvComment.setData(comment.getComment());
        } else {
            commentListHolder.cuvComment.setVisibility(View.GONE);
        }

        if (comment.getReply() != null) {
            commentListHolder.line2.setVisibility(View.VISIBLE);
            commentListHolder.cuvReply.setVisibility(View.VISIBLE);
            commentListHolder.cuvReply.setData(comment.getReply());
        } else {
            commentListHolder.cuvReply.setVisibility(View.GONE);
            commentListHolder.line2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public class CommentListHolder extends RecyclerView.ViewHolder {

        public RelativeLayout layoutGoods;
        public SimpleDraweeView ivGoodPic;
        public DashLineView dlvGoodsName;
        public TextView tvPrice;
        public CommentUserView cuvComment;
        public CommentUserView cuvReply;
        public View line1;
        public View line2;

        public CommentListHolder(View itemView) {
            super(itemView);
            this.layoutGoods = (RelativeLayout) itemView.findViewById(R.id.layoutGoods);
            this.ivGoodPic = (SimpleDraweeView) itemView.findViewById(R.id.ivGoodPic);
            this.dlvGoodsName = (DashLineView) itemView.findViewById(R.id.dlvGoodsName);
            this.tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            this.cuvComment = (CommentUserView) itemView.findViewById(R.id.cuvComment);
            this.cuvReply = (CommentUserView) itemView.findViewById(R.id.cuvReply);
            this.line1 = itemView.findViewById(R.id.line1);
            this.line2 = itemView.findViewById(R.id.line2);
        }
    }


}
