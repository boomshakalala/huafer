package com.huapu.huafen.adapter;

import android.app.Activity;
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
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.UserView;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/8/14.
 */

public class StaggeredGridAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    private Context context;
    private List<Item> data;

    private enum ItemType {
        GOODS,
        ARTICLE,;
    }

    public StaggeredGridAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Item> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<Item> more) {
        if (data == null) {
            data = new ArrayList<>();
        }

        data.addAll(more);
        notifyWrapperDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Item item = data.get(position);
        if ("goods".equals(item.itemType)) {
            return ItemType.GOODS.ordinal();
        } else {
            return ItemType.ARTICLE.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.GOODS.ordinal()) {
            StaggeredGridGoodsViewHolder viewHolder = new StaggeredGridGoodsViewHolder(LayoutInflater.from(context).inflate(R.layout.goods_item_staggered, parent, false));
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            params.leftMargin = params.topMargin = params.rightMargin = CommonUtils.dp2px(5f);
            params.bottomMargin = CommonUtils.dp2px(10f);
            return viewHolder;
        } else {
            StaggeredGridArticleViewHolder viewHolder = new StaggeredGridArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.article_item_staggered, parent, false));
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            params.leftMargin = params.topMargin = params.rightMargin = CommonUtils.dp2px(5f);
            params.bottomMargin = CommonUtils.dp2px(10f);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        int itemType = getItemViewType(position);
        final Item item = data.get(position);
        if (ItemType.GOODS.ordinal() == itemType) {
            StaggeredGridGoodsViewHolder viewHolder = (StaggeredGridGoodsViewHolder) holder;
            if (item != null) {
                final ArticleAndGoods goods = item.item;
                if (goods != null) {
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goods.goodsId + "");
                            ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                        }
                    });


                    if (!ArrayUtil.isEmpty(goods.goodsImgs)) {
                        String url = goods.goodsImgs.get(0);
                        String tag = (String) viewHolder.goodsPic.getTag();
                        if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                            viewHolder.goodsPic.setImageURI(url);
                            viewHolder.goodsPic.setTag(url);
                        }
                    }

                    String cover = goods.videoCover;
                    if (!TextUtils.isEmpty(cover)) {
                        String tag = (String) viewHolder.goodsPic.getTag();
                        if (TextUtils.isEmpty(tag) || !tag.equals(cover)) {
                            viewHolder.goodsPic.setImageURI(cover);
                            viewHolder.goodsPic.setTag(cover);
                        }
                        viewHolder.ivPlay.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.ivPlay.setVisibility(View.GONE);
                        if (!ArrayUtil.isEmpty(goods.goodsImgs)) {
                            String url = goods.goodsImgs.get(0);
                            String tag = (String) viewHolder.goodsPic.getTag();
                            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                                viewHolder.goodsPic.setImageURI(url);
                                viewHolder.goodsPic.setTag(url);
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(goods.brand) || !TextUtils.isEmpty(goods.name)) {
                        viewHolder.tvBrandAndName.setText(goods.brand + " | " + goods.name);
                    }


                    viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startForRequestWantBuy(position);
                        }
                    });

                    boolean isLike = goods.collected;
                    if (isLike) {
                        viewHolder.tvLikeCount.setTextColor(Color.parseColor("#ffff6677"));
                        viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                                context.getResources().getDrawable(R.drawable.btn_item_like_select),
                                null, null, null);
                    } else {
                        viewHolder.tvLikeCount.setTextColor(Color.parseColor("#8A000000"));
                        viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                                context.getResources().getDrawable(R.drawable.btn_item_like_normal),
                                null, null, null);

                    }

                    if (item.counts != null && !TextUtils.isEmpty(item.counts.collection)) {
                        viewHolder.tvLikeCount.setText(item.counts.collection);
                    } else {
                        viewHolder.tvLikeCount.setText("0");
                    }


                    if (item.item.isAuction == 1) {
                        viewHolder.cptv.setVisibility(View.GONE);
                        viewHolder.tvSellPrice.setVisibility(View.VISIBLE);
                        if (item.item.goodsState == 1) {//在售
                            viewHolder.flAuctionIcon.setVisibility(View.VISIBLE);
                            viewHolder.flAuctionIcon.setBackgroundColor(Color.parseColor("#80AEA9D7"));
                            viewHolder.tvAuction.setText("结束时间:" + DateTimeUtils.getStringDate(item.item.bidEndTime, DateTimeUtils.MM_DD_HH_MM));
                            String des;
                            if (item.item.bidder > 0) {
                                des = String.format(context.getString(R.string.auction_price), "当前价", item.item.hammerPrice);
                            } else {
                                des = String.format(context.getString(R.string.auction_price), "起拍价", item.item.hammerPrice);
                            }
                            viewHolder.tvSellPrice.setText(Html.fromHtml(des));
                        } else if (item.item.goodsState == 2) {//预售
                            viewHolder.flAuctionIcon.setVisibility(View.VISIBLE);
                            viewHolder.flAuctionIcon.setBackgroundColor(Color.parseColor("#80AEA9D7"));
                            viewHolder.cptv.setVisibility(View.GONE);
                            viewHolder.tvAuction.setText("开拍时间:" + DateTimeUtils.getStringDate(item.item.bidStartTime, DateTimeUtils.MM_DD_HH_MM));
                            String des = String.format(context.getString(R.string.auction_price), "起拍价", item.item.hammerPrice);
                            viewHolder.tvSellPrice.setText(Html.fromHtml(des));
                        } else if (item.item.goodsState == 4 || item.item.goodsState == 3) {//已售出
                            viewHolder.flAuctionIcon.setVisibility(View.VISIBLE);
                            viewHolder.flAuctionIcon.setBackgroundColor(Color.parseColor("#4D333333"));
                            viewHolder.tvAuction.setText("拍卖结束");
                            String des = String.format(context.getString(R.string.auction_price), "成交价", item.item.hammerPrice);
                            viewHolder.tvSellPrice.setText(Html.fromHtml(des));
                        } else if (item.item.goodsState == 5) {//下架
                            viewHolder.flAuctionIcon.setVisibility(View.VISIBLE);
                            viewHolder.flAuctionIcon.setBackgroundColor(Color.parseColor("#4D333333"));
                            viewHolder.tvAuction.setText("拍卖结束");
                            if (item.item.bidder > 0) {
                                String des = String.format(context.getString(R.string.auction_price), "成交价", item.item.hammerPrice);
                                viewHolder.tvSellPrice.setText(Html.fromHtml(des));
                            } else {
                                String des = String.format(context.getString(R.string.auction_price), "起拍价", item.item.hammerPrice);
                                viewHolder.tvSellPrice.setText(Html.fromHtml(des));
                            }

                        } else {
                            viewHolder.flAuctionIcon.setVisibility(View.GONE);
                        }

                    } else {

                        if (goods.goodsState == 4 || goods.goodsState == 3) {
                            viewHolder.sellout.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.sellout.setVisibility(View.INVISIBLE);
                        }

                        viewHolder.cptv.setVisibility(View.VISIBLE);
                        viewHolder.tvSellPrice.setVisibility(View.GONE);
                        viewHolder.flAuctionIcon.setVisibility(View.GONE);
                        viewHolder.cptv.setData(goods);
                    }
                }
            }

            if (item.user != null) {
                viewHolder.userView.setData(item.user);

                viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.getUserId());
                        context.startActivity(intent);
                    }
                });


                String tag = (String) viewHolder.avatar.getTag();
                String url = item.user.getAvatarUrl();

                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    viewHolder.avatar.setImageURI(url);
                    viewHolder.avatar.setTag(url);
                }
            }


            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            if(position == 0 || position ==1){
                params.topMargin = CommonUtils.dp2px(10f);
            }else{
                params.topMargin = 0;
            }

        } else {
            StaggeredGridArticleViewHolder viewHolder = (StaggeredGridArticleViewHolder) holder;

            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if ("moment".equals(item.itemType)) {
                        Intent intent = new Intent(context, MomentDetailActivity.class);
                        intent.putExtra(MomentDetailActivity.MOMENT_ID, String.valueOf(item.item.articleId));
                        context.startActivity(intent);
                    } else if ("article".equals(item.itemType)) {
                        Intent intent = new Intent(context, ArticleDetailActivity.class);
                        intent.putExtra(MyConstants.ARTICLE_ID, String.valueOf(item.item.articleId));
                        context.startActivity(intent);
                    }
                }
            });

            if ("article".equals(item.itemType)) {
                viewHolder.articleIcon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.articleIcon.setVisibility(View.GONE);
            }

            UserData user = item.user;
            if(user!=null){
                viewHolder.userView.setData(user);
                viewHolder.avatar.setImageURI(user.getAvatarUrl());
                viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.getUserId());
                        context.startActivity(intent);
                    }
                });

            }

            if (item != null && item.item!=null) {
                String title = item.item.title;
                if (!TextUtils.isEmpty(title)) {
                    viewHolder.tvArticleTitle.setText(title);
                }

                String url = item.item.titleMediaUrl;
                int height = item.item.height;
                int width = item.item.width;

                float aspectRatio = 1.41f;
                if (width > 0 && height > 0) {
                    aspectRatio = (float) width / height;
                    if (aspectRatio < 0.75f) {
                        aspectRatio = 0.75f;
                    }
                }
                viewHolder.articleImage.setAspectRatio(aspectRatio);
                viewHolder.articleImage.setImageURI(url);


                if (!TextUtils.isEmpty(item.item.summary)) {
                    viewHolder.tvSummary.setVisibility(View.VISIBLE);
                    viewHolder.tvSummary.setText(item.item.summary);
                } else {
                    viewHolder.tvSummary.setVisibility(View.GONE);
                }

                viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startForRequestWantBuy(position);
                    }
                });

                boolean isLike = item.item.collected;
                if (isLike) {
                    viewHolder.tvLikeCount.setTextColor(Color.parseColor("#ffff6677"));
                    viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                            context.getResources().getDrawable(R.drawable.btn_item_like_select),
                            null, null, null);
                } else {
                    viewHolder.tvLikeCount.setTextColor(Color.parseColor("#8A000000"));
                    viewHolder.tvLikeCount.setCompoundDrawablesWithIntrinsicBounds(
                            context.getResources().getDrawable(R.drawable.btn_item_like_normal),
                            null, null, null);

                }

                if (item.counts != null && !TextUtils.isEmpty(item.counts.collection)) {
                    viewHolder.tvLikeCount.setText(item.counts.collection);
                } else {
                    viewHolder.tvLikeCount.setText("0");
                }
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
            if(position == 0 || position ==1){
                params.topMargin = CommonUtils.dp2px(10f);
            }else{
                params.topMargin = 0;
            }

        }
    }


    private void startForRequestWantBuy(final int position) {
        final Item item = data.get(position);
        HashMap<String, String> params = new HashMap<String, String>();
        if (item.item.collected) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        if ("goods".equals(item.itemType)) {
            params.put("targetId", item.item.goodsId + "");
            params.put("targetType", "1");
        } else if ("moment".equals(item.itemType)) {
            params.put("targetId", item.item.articleId + "");
            params.put("targetType", "8");
        } else if ("article".equals(item.itemType)) {
            params.put("targetId", item.item.articleId + "");
            params.put("targetType", "6");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.COLLECT, params, new OkHttpClientManager.StringCallback() {

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

                        CollectionData data = JSON.parseObject(baseResult.obj, CollectionData.class);

                        if (item.item.collected) {
                            item.item.collected = false;
                        } else {
                            item.item.collected = true;
                        }
                        item.counts.collection = String.valueOf(data.getCollections());
                        notifyWrapperDataSetChanged();
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class StaggeredGridGoodsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goodsPic)
        SimpleDraweeView goodsPic;
        @BindView(R.id.ivPlay)
        ImageView ivPlay;
        @BindView(R.id.tvBrandAndName)
        TextView tvBrandAndName;
        @BindView(R.id.cptv)
        CommonPriceTagView cptv;
        @BindView(R.id.tvSellPrice)
        TextView tvSellPrice;
        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.userView)
        UserView userView;
        @BindView(R.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R.id.flAuctionIcon)
        FrameLayout flAuctionIcon;
        @BindView(R.id.tvAuction)
        TextView tvAuction;
        @BindView(R.id.sellout)
        ImageView sellout;

        public StaggeredGridGoodsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class StaggeredGridArticleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.articleImage) SimpleDraweeView articleImage;
        @BindView(R.id.tvArticleTitle) TextView tvArticleTitle;
        @BindView(R.id.tvSummary) TextView tvSummary;
        @BindView(R.id.avatar) SimpleDraweeView avatar;
        @BindView(R.id.userView) UserView userView;
        @BindView(R.id.tvLikeCount) TextView tvLikeCount;
        @BindView(R.id.articleIcon) ImageView articleIcon;

        public StaggeredGridArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
