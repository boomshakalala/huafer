package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.ShopArticleData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.CommonPriceTagView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商品、花语 瀑布流
 */
public class ShopkeepersAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    private ArrayList<ShopArticleData> mDatas = new ArrayList<>();

    private Context context;

    private int type = 1;

    public ShopkeepersAdapter(Context context) {
        this.context = context;
    }

    public void addDatas(ArrayList<ShopArticleData> datas) {
        mDatas.addAll(datas);
        notifyWrapperDataSetChanged();
    }

    public void setData(ArrayList<ShopArticleData> datas) {
        this.mDatas = datas;
        // notifyWrapperDataSetChanged();
        notifyWrapperItemRangeInserted(datas.size());
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.shopkeeper_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ShopArticleData data = mDatas.get(position);
        if (holder instanceof ViewHolder) {
            ViewHolder vh = (ViewHolder) holder;
            if ("goods".equals(data.getItemType())) {

                vh.ivProPic.setAspectRatio(1f);
                List<String> imgs = data.getItem().getGoodsImgs();
                if (imgs != null && imgs.size() > 0)
                    ImageLoader.resizeMiddle(vh.ivProPic, imgs.get(0), 1);

                vh.text.setText(data.getItem().getBrand() + " | " + data.getItem().getName());
                vh.ctvName.setText(data.getUser().getUserName());
                vh.ivHeader.setImageURI(data.getUser().getAvatarUrl());
                vh.cptv.setVisibility(View.VISIBLE);
                vh.articleImage.setVisibility(View.GONE);
                vh.tvBody.setVisibility(View.GONE);
                vh.cptv.setData(data);

                if (data.getItem().isAuction == 1) {
                    if (data.getItem().getGoodsState() == 1) {//在售
                        vh.flAuctionIcon.setVisibility(View.VISIBLE);
                        vh.flAuctionIcon.setBackgroundColor(Color.parseColor("#80AEA9D7"));
                        vh.tvSellPrice.setVisibility(View.VISIBLE);
                        vh.cptv.setVisibility(View.GONE);
                        vh.tvAuction.setText("结束时间:" + DateTimeUtils.getStringDate(data.getItem().bidEndTime, DateTimeUtils.MM_DD_HH_MM));
                        String des;
                        if (data.getItem().bidder > 0) {
                            des = String.format(context.getString(R.string.auction_price), "当前价", data.getItem().hammerPrice);
                        } else {
                            des = String.format(context.getString(R.string.auction_price), "起拍价", data.getItem().hammerPrice);
                        }
                        vh.tvSellPrice.setText(Html.fromHtml(des));
                    } else if (data.getItem().getGoodsState() == 2) {//预售
                        vh.flAuctionIcon.setVisibility(View.VISIBLE);
                        vh.flAuctionIcon.setBackgroundColor(Color.parseColor("#80AEA9D7"));
                        vh.tvSellPrice.setVisibility(View.VISIBLE);
                        vh.cptv.setVisibility(View.GONE);
                        vh.tvAuction.setText("开拍时间:" + DateTimeUtils.getStringDate(data.getItem().bidStartTime, DateTimeUtils.MM_DD_HH_MM));
                        String des = String.format(context.getString(R.string.auction_price), "起拍价", data.getItem().hammerPrice);
                        vh.tvSellPrice.setText(Html.fromHtml(des));
                    } else if (data.getItem().getGoodsState() == 4 || data.getItem().getGoodsState() == 3) {//已售出
                        vh.flAuctionIcon.setVisibility(View.VISIBLE);
                        vh.flAuctionIcon.setBackgroundColor(Color.parseColor("#4D333333"));
                        vh.tvAuction.setText("拍卖结束");
                        String des = String.format(context.getString(R.string.auction_price), "成交价", data.getItem().hammerPrice);
                        vh.tvSellPrice.setText(Html.fromHtml(des));
                    } else if (data.getItem().getGoodsState() == 5) {//下架
                        vh.flAuctionIcon.setVisibility(View.VISIBLE);
                        vh.flAuctionIcon.setBackgroundColor(Color.parseColor("#4D333333"));
                        vh.tvAuction.setText("拍卖结束");
                        if (data.getItem().bidder > 0) {
                            String des = String.format(context.getString(R.string.auction_price), "成交价", data.getItem().hammerPrice);
                            vh.tvSellPrice.setText(Html.fromHtml(des));
                        } else {
                            String des = String.format(context.getString(R.string.auction_price), "起拍价", data.getItem().hammerPrice);
                            vh.tvSellPrice.setText(Html.fromHtml(des));
                        }

                    } else {
                        vh.flAuctionIcon.setVisibility(View.GONE);
                        vh.tvAuction.setText("");
                    }
                } else {
                    vh.flAuctionIcon.setVisibility(View.GONE);
                    vh.tvSellPrice.setVisibility(View.GONE);
                    vh.cptv.setVisibility(View.VISIBLE);
                }

                ShopArticleData.users user = data.getUser();
                if (user != null) {
                    //获取用户的等级
                    int userLevel = user.getUserLevel();

                    //获取认证点
                    int creditPoint = user.getZmCreditPoint();
                    if (userLevel > 1 || creditPoint > 0) {
                        if (userLevel > 1 && userLevel <= 2) {
                            vh.ivInfo.setVisibility(View.VISIBLE);
                            vh.ivInfo.setBackgroundResource(R.drawable.icon_vip);
                        } else if (userLevel > 2 && userLevel <= 3) {
                            vh.ivInfo.setVisibility(View.VISIBLE);
                            vh.ivInfo.setBackgroundResource(R.drawable.icon_xing);
                        } else {
                            vh.ivInfo.setBackgroundResource(R.drawable.iv_zm);
                            vh.ivInfo.setVisibility(View.VISIBLE);
                        }
                    } else {      //不显示控件，
                        vh.ivInfo.setVisibility(View.GONE);
                    }
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, data.getItem().getGoodsId() + "");
                        context.startActivity(intent);
                    }
                });
            } else {
                vh.flAuctionIcon.setVisibility(View.GONE);
                if ("article".equals(data.getItemType())) {
                    vh.articleImage.setBackgroundResource(R.drawable.aritcle);
                    vh.articleImage.setVisibility(View.VISIBLE);
                } else {
                    vh.articleImage.setVisibility(View.GONE);
                }
                vh.tvSellPrice.setVisibility(View.GONE);
                vh.cptv.setVisibility(View.GONE);
                ShopArticleData.items item = data.getItem();

                if (item != null) {
                    String title = item.getTitle();
                    if (!TextUtils.isEmpty(title)) {
                        vh.text.setText(title);
                    }

                    String url = item.getTitleMediaUrl();
                    int height = item.getHeight();
                    int width = item.getWidth();

                    float aspectRatio = 1.41f;
                    if (width > 0 && height > 0) {
                        aspectRatio = (float) width / height;
                        if (aspectRatio < 0.75f) {
                            aspectRatio = 0.75f;
                        }
                    }

                    vh.ivProPic.setAspectRatio(aspectRatio);
                    ImageLoader.resizeMiddle(vh.ivProPic, url, aspectRatio);

                    if (!TextUtils.isEmpty(item.getSummary())) {
                        vh.tvBody.setVisibility(View.VISIBLE);
                        vh.tvBody.setText(item.getSummary());
                    } else {
                        vh.tvBody.setVisibility(View.GONE);
                    }
                }

                ShopArticleData.users user = data.getUser();
                if (user != null) {
                    vh.ivHeader.setImageURI(user.getAvatarUrl());
                    String userName = user.getUserName();
                    if (!TextUtils.isEmpty(userName)) {
                        vh.ctvName.setText(userName);
                    }

                    //获取用户的等级
                    int userLevel = user.getUserLevel();
                    //获取认证点
                    int creditPoint = user.getZmCreditPoint();
                    if (userLevel > 1 || creditPoint > 0) {
                        if (userLevel > 1 && userLevel <= 2) {
                            vh.ivInfo.setVisibility(View.VISIBLE);
                            vh.ivInfo.setBackgroundResource(R.drawable.icon_vip);
                        } else if (userLevel > 2 && userLevel <= 3) {
                            vh.ivInfo.setVisibility(View.VISIBLE);
                            vh.ivInfo.setBackgroundResource(R.drawable.icon_xing);
                        } else {
                            vh.ivInfo.setBackgroundResource(R.drawable.iv_zm);
                            vh.ivInfo.setVisibility(View.VISIBLE);
                        }
                    } else {      //不显示控件，
                        vh.ivInfo.setVisibility(View.GONE);
                    }
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if ("moment".equals(data.getItemType())) {
                            Intent intent = new Intent(context, MomentDetailActivity.class);
                            intent.putExtra(MomentDetailActivity.MOMENT_ID, data.getItem().getArticleId());
                            context.startActivity(intent);
                        } else if ("article".equals(data.getItemType())) {
                            Intent intent = new Intent(context, ArticleDetailActivity.class);
                            intent.putExtra(MyConstants.ARTICLE_ID, data.getItem().getArticleId());
                            context.startActivity(intent);
                        }
                    }
                });
            }


            //喜欢按
            ((ViewHolder) holder).layoutLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startForRequestWantBuy(position);
                }
            });
        }

        if (data != null && data.getItem() != null && data.getItem().isCollected()) {
            ((ViewHolder) holder).ivLike.setImageResource(R.drawable.btn_item_like_select);
            ((ViewHolder) holder).tvLike.setTextColor(Color.parseColor("#ffff6677"));
        } else {
            ((ViewHolder) holder).ivLike.setImageResource(R.drawable.btn_item_like_normal);
            ((ViewHolder) holder).tvLike.setTextColor(Color.parseColor("#888888"));
        }

        if (data != null && data.getCounts() != null) {
            if (data.getCounts().getCollection() == null) {
                ((ViewHolder) holder).tvLike.setText("0");
            } else {
                ((ViewHolder) holder).tvLike.setText(data.getCounts().getCollection());

            }
        }

        ((ViewHolder) holder).llBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到用户店铺主页
                Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, Long.parseLong(data.getUser().getUserId()));
                context.startActivity(intent);
            }
        });

        if (position == 0 || position == 1) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.topMargin = CommonUtils.dp2px(10f);
        }

    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(mDatas);
    }

    private void startForRequestWantBuy(final int position) {
        final ShopArticleData data = mDatas.get(position);
        HashMap<String, String> params = new HashMap<String, String>();
        if (data.getItem().isCollected()) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
//        params.put("type", type + "");
        if (data.getItemType().equals("goods")) {
            params.put("targetId", data.getItem().getGoodsId() + "");
            params.put("targetType", "1");
        } else if (data.getItemType().equals("moment")) {
            params.put("targetId", data.getItem().getArticleId());
            params.put("targetType", "8");
        } else if (data.getItemType().equals("article")) {
            params.put("targetId", data.getItem().getArticleId());
            params.put("targetType", "6");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.COLLECT, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i("liang", "喜欢:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
                        try {
                            BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                            if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                final ShopArticleData Adata = mDatas.get(position);
                                CollectionData data = JSON.parseObject(baseResult.obj, CollectionData.class);

                                if (Adata.getItem().isCollected()) {
                                    Adata.getItem().setCollected(false);
                                    Adata.getCounts().setCollection(String.valueOf(data.getCollections()));
                                    notifyWrapperDataSetChanged();
                                } else {
                                    Adata.getItem().setCollected(true);
                                    Adata.getCounts().setCollection(String.valueOf(data.getCollections()));
                                    notifyWrapperDataSetChanged();
                                }

                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView ivProPic;
        public SimpleDraweeView ivHeader;
        public CommonPriceTagView cptv;
        TextView text, tvBody;
        TextView ctvName;
        ImageView ivInfo;
        private LinearLayout llBottom;
        private LinearLayout layoutLike;
        private ImageView ivLike;
        private TextView tvLike;
        private ImageView articleImage;
        private TextView tvAuction;
        private FrameLayout flAuctionIcon;
        private TextView tvSellPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProPic = (SimpleDraweeView) itemView.findViewById(R.id.ivProPic);
            text = (TextView) itemView.findViewById(R.id.tvTitle);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ctvName = (TextView) itemView.findViewById(R.id.ctvName);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            layoutLike = (LinearLayout) itemView.findViewById(R.id.layoutLike);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
            cptv = (CommonPriceTagView) itemView.findViewById(R.id.cptv);
            ivInfo = (ImageView) itemView.findViewById(R.id.ivInfo);
            articleImage = (ImageView) itemView.findViewById(R.id.article_image);
            llBottom = (LinearLayout) itemView.findViewById(R.id.llBottom);
            flAuctionIcon = (FrameLayout) itemView.findViewById(R.id.flAuctionIcon);
            tvAuction = (TextView) itemView.findViewById(R.id.tvAuction);
            tvSellPrice = (TextView) itemView.findViewById(R.id.tvSellPrice);
        }
    }
}
