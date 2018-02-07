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
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.UserView;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/7/19.
 */

public class GridAdapter extends CommonWrapper<GridAdapter.GridViewHolder>{

    private Context context;
    private List<Item> data;
    private String recTraceId;
    private String searchQuery;

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public GridAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Item> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void setRecTraceId(String recTraceId) {
        this.recTraceId = recTraceId;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(context).inflate(R.layout.goods_item_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(GridViewHolder viewHolder, final int position) {
        final Item item = data.get(position);
        if (item != null) {
            final ArticleAndGoods goods = item.item;
            if (goods != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, GoodsDetailsActivity.class);
                        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goods.goodsId + "");
                        intent.putExtra(MyConstants.POSITION, position);
                        intent.putExtra(MyConstants.REC_TRAC_ID,recTraceId);
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
                        startForRequestWantBuy(item,position);
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

                if(item.counts!=null && !TextUtils.isEmpty(item.counts.collection)){
                    viewHolder.tvLikeCount.setText(item.counts.collection);
                }else{
                    viewHolder.tvLikeCount.setText("0");
                }

                viewHolder.cptv.setData(goods);
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

        int paddingOut = CommonUtils.dp2px(10f);
        int paddingInner = CommonUtils.dp2px(10f) / 2;

        params.bottomMargin = paddingOut;
        if ((position  % 2) == 0) {
            params.leftMargin = paddingOut;
            params.rightMargin = paddingInner;
        } else {
            params.leftMargin = paddingInner;
            params.rightMargin = paddingOut;
        }



    }

    @Override
    public int getItemCount() {
        return data == null?0:data.size();
    }

    private void startForRequestWantBuy(final Item item, int position) {
        HashMap<String, String> params = new HashMap<>();
        params.put("targetType", "1");
        params.put("targetId", String.valueOf(item.item.goodsId));
        if (item.item.collected) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }

        if(!TextUtils.isEmpty(recTraceId)){
            params.put("recTraceId",recTraceId);
        }

        if(position!=-1){
            params.put("recIndex",String.valueOf(position));
        }

        if(!TextUtils.isEmpty(searchQuery)){
            params.put("searchQuery",searchQuery);
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.COLLECT, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "喜欢:" + response);

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
                        CommonUtils.error(baseResult, (Activity) context);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.goodsPic) SimpleDraweeView goodsPic;
        @BindView(R.id.ivPlay) ImageView ivPlay;
        @BindView(R.id.tvBrandAndName) TextView tvBrandAndName;
        @BindView(R.id.cptv) CommonPriceTagView cptv;
        @BindView(R.id.avatar) SimpleDraweeView avatar;
        @BindView(R.id.userView) UserView userView;
        @BindView(R.id.tvLikeCount) TextView tvLikeCount;

        public GridViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
