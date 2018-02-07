package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.HotGoodsBean;
import com.huapu.huafen.beans.VIPRegionBean;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.CommonTitleViewSmall;
import com.huapu.huafen.views.UserView;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by qwe on 2017/5/17.
 */

public class FullyNewRegionAdapter extends AbstractRecyclerAdapter {

    private Context context;
    private int itemType = 1;

    private RecyclerView recyclerView;

    public FullyNewRegionAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void setItemType(int type) {
        this.itemType = type;
    }

    @Override
    public int getItemViewType(int position) {
        int headerPosition = 0;
        int footerPosition = getItemCount() - 1;

        if (headerPosition == position && mIsHeaderEnable && null != headerView) {
            return TYPE_HEADER;
        }
        if (footerPosition == position && mIsFooterEnable) {
            return TYPE_FOOTER;
        }

        if (itemType == 1) {
            return TYPE_LIST;
        } else {
            return TYPE_GRID;
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType, boolean isItem) {
        if (viewType == TYPE_LIST) {
            return new GoodsItemListHolder(LayoutInflater.from(context).inflate(R.layout.goods_list_item, parent, false));
        } else {
            return new GoodsItemGridHolder(LayoutInflater.from(context).inflate(R.layout.goods_item_grid, parent, false));
        }

    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, boolean isItem) {
        if (position == 0) {
            return;
        }
        final VIPRegionBean.ListBean hotGoodsBean = (VIPRegionBean.ListBean) listData.get(position - 1);

        try {
            if (holder instanceof GoodsItemGridHolder) {
                GoodsItemGridHolder viewHolder = (GoodsItemGridHolder) holder;
                final HotGoodsBean.ItemBean itemBean = hotGoodsBean.item;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(itemBean.goodsId));
                        intent.putExtra("position", position);
                        ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                    }
                });

                if (!ArrayUtil.isEmpty(itemBean.goodsImgs)) {
                    String url = itemBean.goodsImgs.get(0);
                    String tag = (String) viewHolder.goodsPic.getTag();
                    if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                        if (url.contains("@!logo")) {
                            url = StringUtils.substringBeforeLast(url, "@!logo");
                        }
                        viewHolder.goodsPic.setImageURI(url + MyConstants.OSS_MEDIUM_STYLE);
                        viewHolder.goodsPic.setTag(url);
                    }
                }

                if (!TextUtils.isEmpty(itemBean.brand) || !TextUtils.isEmpty(itemBean.name)) {
                    viewHolder.tvBrandAndName.setText(itemBean.brand + " | " + itemBean.name);
                }

                viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startForRequestWantBuy(hotGoodsBean);
                    }
                });

                boolean isLike = itemBean.collected;
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

                if (TextUtils.isEmpty(hotGoodsBean.counts.collection)) {
                    viewHolder.tvLikeCount.setText("0");
                } else {
                    viewHolder.tvLikeCount.setText(String.valueOf(hotGoodsBean.counts.collection));
                }
                viewHolder.cptv.setData(hotGoodsBean.item);

                viewHolder.userView.setData(hotGoodsBean.user);

                viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, hotGoodsBean.user.getUserId());
                        context.startActivity(intent);
                    }
                });


                String tag = (String) viewHolder.avatar.getTag();
                String url = hotGoodsBean.user.getAvatarUrl();

                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    viewHolder.avatar.setImageURI(url);
                    viewHolder.avatar.setTag(url);
                }

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

                int paddingOut = CommonUtils.dp2px(10f);
                int paddingInner = CommonUtils.dp2px(10f) / 2;

                params.topMargin = paddingOut;
                if (((position - 1) % 2) == 0) {
                    params.leftMargin = paddingOut;
                    params.rightMargin = paddingInner;
                } else {
                    params.leftMargin = paddingInner;
                    params.rightMargin = paddingOut;
                }

                int count = listData.size();
                int lastRow = count / 2 + count % 2;
                int currentRow = ((position - 1) + 1) / 2 + ((position - 1) + 1) % 2;
                if (lastRow == currentRow) {
                    params.bottomMargin = paddingOut;
                } else {
                    params.bottomMargin = 0;
                }
            } else if (holder instanceof GoodsItemListHolder) {
                GoodsItemListHolder viewHolder = (GoodsItemListHolder) holder;
                final HotGoodsBean.ItemBean itemBean = hotGoodsBean.item;
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(itemBean.goodsId));
                        intent.putExtra("position", position);
                        ((Activity) context).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                    }
                });

                if (!ArrayUtil.isEmpty(itemBean.goodsImgs)) {
                    String url = itemBean.goodsImgs.get(0);
                    String tag = (String) viewHolder.goodsPic.getTag();
                    if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                        if (url.contains("@!logo")) {
                            url = StringUtils.substringBeforeLast(url, "@!logo");
                        }
                        viewHolder.goodsPic.setImageURI(url + MyConstants.OSS_MEDIUM_STYLE);
                        viewHolder.goodsPic.setTag(url);
                    }
                }

                if (!TextUtils.isEmpty(itemBean.brand) || !TextUtils.isEmpty(itemBean.name)) {
                    viewHolder.tvBrandAndName.setText(itemBean.brand + " | " + itemBean.name);
                }

                viewHolder.tvLikeCount.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startForRequestWantBuy(hotGoodsBean);
                    }
                });

                boolean isLike = hotGoodsBean.item.collected;
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

                if (TextUtils.isEmpty(hotGoodsBean.counts.collection)) {
                    viewHolder.tvLikeCount.setText("0");
                } else {
                    viewHolder.tvLikeCount.setText(String.valueOf(hotGoodsBean.counts.collection));
                }
                viewHolder.cptv.setData(hotGoodsBean.item);

                viewHolder.ctvName.setData(hotGoodsBean.user);

                viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, hotGoodsBean.user.getUserId());
                        context.startActivity(intent);
                    }
                });

                String tag = (String) viewHolder.avatar.getTag();
                String url = hotGoodsBean.user.getAvatarUrl();

                if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                    viewHolder.avatar.setImageURI(url);
                    viewHolder.avatar.setTag(url);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void startForRequestWantBuy(final VIPRegionBean.ListBean goods) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("targetType", "1");
        params.put("targetId", String.valueOf(goods.item.goodsId));
        if (goods.item.collected) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
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
                        if (goods.item.collected) {
                            goods.item.collected = false;
                        } else {
                            goods.item.collected = true;
                        }
                        goods.counts.collection = String.valueOf(data.getCollections());
                        notifyDataSetChanged();
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    class GoodsItemGridHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.goodsPic)
        SimpleDraweeView goodsPic;
        @BindView(R2.id.ivPlay)
        ImageView ivPlay;
        @BindView(R2.id.tvBrandAndName)
        TextView tvBrandAndName;
        @BindView(R2.id.cptv)
        CommonPriceTagView cptv;
        @BindView(R2.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R2.id.userView)
        UserView userView;
        @BindView(R2.id.tvLikeCount)
        TextView tvLikeCount;

        GoodsItemGridHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class GoodsItemListHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.goodsPic)
        SimpleDraweeView goodsPic;
        @BindView(R2.id.ivPlay)
        ImageView ivPlay;
        @BindView(R2.id.tvBrandAndName)
        TextView tvBrandAndName;
        @BindView(R2.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R2.id.ctvName)
        CommonTitleViewSmall ctvName;
        @BindView(R2.id.tvLikeCount)
        TextView tvLikeCount;
        @BindView(R2.id.cptv)
        CommonPriceTagView cptv;

        GoodsItemListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
