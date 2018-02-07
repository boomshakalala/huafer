package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.MoreInfoDialog;
import com.huapu.huafen.dialog.OfferPriceDialog;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;
import static com.alibaba.mtl.log.a.getContext;


/**
 * Created by qwe on 2017/8/4.
 */

public class MyAuctionAdapter extends CommonWrapper<MyAuctionAdapter.MyAuctionListHolder> {

    /**
     * goodsState 1 :未开始 2:进行中 3:已结束   bidder 出价最高人id
     */
    private Context mContext;
    private List<Item> data = new ArrayList<>();
    private int pageType = 1;

    public MyAuctionAdapter(Context context, int pageType) {
        super();
        this.mContext = context;
        this.pageType = pageType;
    }

    public void setData(List<Item> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data = data;
    }

    public void addAll(List<Item> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data.addAll(data);
    }

    @Override
    public MyAuctionListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyAuctionListHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_adapter_myauction, parent, false));
    }

    @Override
    public void onBindViewHolder(MyAuctionListHolder holder, int position) {
        try {
            final Item auctionItem = data.get(position);
            holder.commonTitleView.setData(auctionItem.user);

            ImageLoader.resizeSmall(holder.personPhoto, auctionItem.user.getAvatarUrl(), 1);
            ViewUtil.setImgMiddle(holder.goodsPhoto, auctionItem.item.goodsImgs.get(0), 1);

            holder.personLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, auctionItem.user.getUserId());
                    mContext.startActivity(intent);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(auctionItem.item.goodsId));
                    mContext.startActivity(intent);
                }
            });
            holder.dlvGoodsName.setData(auctionItem.item.brand, auctionItem.item.name);

            holder.beginPrice.setText(String.valueOf(auctionItem.item.sellPric));

            Logger.e("get type:" + pageType);
            /**
             * 拍卖中
             */
            int state = auctionItem.item.goodsState;

            int depositStatus = auctionItem.depositStatus;
            if (depositStatus == 0) {
                holder.depositMoney.setText("保证金：¥" + auctionItem.item.bidDeposit);
            } else if (depositStatus == 1) {
                holder.depositMoney.setText("保证金已释放");
            } else if (depositStatus == 2) {
                holder.depositMoney.setText("保证金已罚没");
            }

            if (state == 1) {
                if (auctionItem.item.bidder > 0) {
                    holder.beginPriceText.setText("当前价¥");
//                    holder.beginPrice.setText(String.valueOf(auctionItem.item.hammerPrice));
                } else {
                    holder.beginPriceText.setText("起拍价¥");
                }

            } else if (state == 2) {
                holder.beginPriceText.setText("起拍价¥");
            } else {
                if (auctionItem.item.bidder > 0) {//有人出价
                    holder.beginPriceText.setText("成交价");
                } else {
                    holder.beginPriceText.setText("起拍价¥");
                }
            }
            holder.beginPrice.setText(String.valueOf(auctionItem.item.hammerPrice));


            if (auctionItem.item.goodsState == 2) {
                holder.offerPriceLayout.setVisibility(VISIBLE);
                holder.otherOperateLayout.setVisibility(View.GONE);
                holder.auctionState.setBackgroundResource(R.drawable.shape_item_title);
                holder.auctionState.setTextColor(Color.parseColor("#ffffff"));
                long preSellTime = auctionItem.item.bidStartTime;
                long systemTime = System.currentTimeMillis();
                long residue = preSellTime - systemTime;
                if (residue > 0) {
                    holder.auctionState.setText("距离开拍时间：" + DateTimeUtils.getResidueTime(residue));
                    holder.auctionStateImage.setImageResource(R.drawable.wait_auction);
                    holder.offerPriceLayout.setBackgroundResource(R.drawable.shape_corner_auction);
                    holder.offerPriceText.setTextColor(Color.parseColor("#cccccc"));
                    holder.offerPriceLayout.setEnabled(false);
                } else {

                    if (auctionItem.item.bidder == CommonPreference.getUserId()) {
                        holder.auctionStateImage.setImageResource(R.drawable.first_auction);
                    } else {
                        holder.auctionStateImage.setImageResource(R.drawable.out_auction);
                    }
                    long endTime = auctionItem.item.bidEndTime;
                    long endDuration = endTime - systemTime;
                    holder.auctionState.setText("距离结束时间：" + DateTimeUtils.getResidueTime(endDuration));
                    holder.offerPriceLayout.setBackgroundResource(R.drawable.shape_corner_auction_going);
                    holder.offerPriceText.setTextColor(Color.parseColor("#ffffff"));
                    holder.offerPriceLayout.setEnabled(true);
                }


            } else if (auctionItem.item.goodsState == 1) {
                holder.offerPriceLayout.setVisibility(VISIBLE);
                holder.otherOperateLayout.setVisibility(View.GONE);
                holder.auctionState.setBackgroundResource(R.drawable.shape_item_title);
                holder.auctionState.setTextColor(Color.parseColor("#ffffff"));

                if (auctionItem.item.bidder == CommonPreference.getUserId()) {
                    holder.auctionStateImage.setVisibility(View.VISIBLE);
                    holder.auctionStateImage.setImageResource(R.drawable.first_auction);
                } else {
                    if (pageType == 2) {
                        holder.auctionStateImage.setVisibility(View.VISIBLE);
                        holder.auctionStateImage.setImageResource(R.drawable.wait_auction);
                    } else {
                        if (auctionItem.item.bidder <= 0) {
                            holder.auctionStateImage.setVisibility(View.GONE);
                        } else {
                            holder.auctionStateImage.setVisibility(View.VISIBLE);
                            holder.auctionStateImage.setImageResource(R.drawable.out_auction);
                        }

                    }

                }

                long endTime = auctionItem.item.bidEndTime;
                long systemTime = System.currentTimeMillis();
                long residue = endTime - systemTime;
                if (residue > 0) {
                    holder.auctionState.setText("距离结束时间：" + DateTimeUtils.getResidueTime(residue));

                    holder.offerPriceLayout.setBackgroundResource(R.drawable.shape_corner_auction_going);
                    holder.offerPriceText.setTextColor(Color.parseColor("#ffffff"));
                } else {
                    holder.auctionState.setText("已结束");
                    holder.auctionState.setBackgroundResource(R.drawable.shape_item_title_end);
                    holder.auctionState.setTextColor(Color.parseColor("#ffffff"));
                    holder.offerPriceLayout.setVisibility(View.GONE);
                    holder.otherOperateLayout.setVisibility(VISIBLE);
                }

            } else {

                holder.offerPriceLayout.setVisibility(View.GONE);
                holder.otherOperateLayout.setVisibility(VISIBLE);

                holder.auctionState.setText("已结束");
                holder.auctionState.setBackgroundResource(R.drawable.shape_item_title_end);
                holder.auctionState.setTextColor(Color.parseColor("#ffffff"));

                if (auctionItem.item.bidder == CommonPreference.getUserId()) {
                    holder.auctionStateImage.setImageResource(R.drawable.win_auction);
                    holder.orderDetail.setText("订单详情");
                    holder.orderDetail.setTextColor(Color.parseColor("#aea9d7"));
                    holder.moreInfo.setVisibility(VISIBLE);
                    holder.orderDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (auctionItem.orderId > 0) {
                                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, auctionItem.orderId);
                                mContext.startActivity(intent);
                            }
                        }
                    });
                } else {
                    holder.auctionStateImage.setImageResource(R.drawable.out_auction);
                    holder.orderDetail.setText("保证金去向");
                    holder.orderDetail.setTextColor(Color.parseColor("#aea9d7"));
                    holder.orderDetail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, WebViewActivity.class);
                            intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.BID_RULE_WITHOUT_TITLE);
                            mContext.startActivity(intent);
                        }
                    });
                    holder.moreInfo.setVisibility(View.GONE);
                }


            }


            holder.moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MoreInfoDialog moreInfoDialog = new MoreInfoDialog(mContext);
                    moreInfoDialog.show();
                }
            });
            holder.offerPriceLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArticleAndGoods goodsInfo = auctionItem.item;
                    int goodsState = goodsInfo.goodsState;
                    if (goodsState == 1) {//进行中
                        OfferPriceDialog offerPriceDialog = new OfferPriceDialog(mContext, goodsInfo.hammerPrice, goodsInfo.bidIncrement, goodsInfo.goodsId);
                        offerPriceDialog.show();
                    } else if (goodsState == 2) {//未开始
                        ToastUtil.toast(mContext, "拍卖商品还没开拍");
                    }
                }
            });
            holder.tvContactTa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonPreference.isLogin()) {
                        if (null == auctionItem.item) {
                            return;
                        }
                        // 启动会话界面
//                        if (RongIM.getInstance().getCurrentConnectionStatus()
//                                .equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
//                            Intent activityIntent = new Intent();
//                            activityIntent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(auctionItem.item.goodsId));
//                            Uri uri = Uri.parse("rong://" + mContext.getApplicationInfo().packageName).buildUpon()
//                                    .appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase())
//                                    .appendQueryParameter("targetId", String.valueOf(auctionItem.user.getUserId()))
//                                    .appendQueryParameter("title", auctionItem.user.getUserName())
//                                    .build();
//                            activityIntent.setData(uri);
//                            mContext.startActivity(activityIntent);
//                        } else {
//                            if (mContext instanceof Activity) {
//                                RongCloudEvent.rongCount = 3;
//                                RongCloudEvent.getInstance().login2Rong(RongCloudEvent.RONG_START_CONVERSATION, String.valueOf(auctionItem.user.getUserId()), auctionItem.user.getUserName(), (Activity) mContext, true);
//                            }
//                        }

                        if (CommonPreference.isLogin()) {
                            Intent intent = new Intent(getContext(), PrivateConversationActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(auctionItem.orderId));
                            intent.putExtra(MyConstants.IM_PEER_ID, auctionItem.user.getUserId() + "");
                            getContext().startActivity(intent);
                        } else {
                            ActionUtil.loginAndToast(getContext());
                        }
                    } else {
                        if (mContext instanceof Activity) {
                            ActionUtil.loginAndToast(mContext);
                        }
                    }
                }
            });


            holder.tvFreeDelivery.setText("包邮");
            holder.tvFreeDelivery.setVisibility(VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return null == data ? 0 : data.size();
    }

    static class MyAuctionListHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.commonTitleView)
        CommonTitleView commonTitleView;
        @BindView(R.id.dlvGoodsName)
        DashLineView dlvGoodsName;
        @BindView(R.id.depositMoney)
        TextView depositMoney;
        @BindView(R.id.auctionState)
        TextView auctionState;
        @BindView(R.id.personPhoto)
        SimpleDraweeView personPhoto;
        @BindView(R.id.goodsPhoto)
        SimpleDraweeView goodsPhoto;
        @BindView(R.id.beginPrice)
        TextView beginPrice;
        @BindView(R.id.tvFreeDelivery)
        TextView tvFreeDelivery;
        @BindView(R.id.tvContactTa)
        TextView tvContactTa;
        @BindView(R.id.offerPriceLayout)
        FrameLayout offerPriceLayout;
        @BindView(R.id.auctionStateImage)
        ImageView auctionStateImage;
        @BindView(R.id.moreInfo)
        TextView moreInfo;
        @BindView(R.id.beginPriceText)
        TextView beginPriceText;
        @BindView(R.id.orderDetail)
        TextView orderDetail;
        @BindView(R.id.offerPriceText)
        TextView offerPriceText;
        @BindView(R.id.otherOperateLayout)
        LinearLayout otherOperateLayout;
        @BindView(R.id.personLayout)
        LinearLayout personLayout;

        MyAuctionListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
