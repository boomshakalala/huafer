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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.UserView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/9/27.
 */

public class VolumnGridAdapter extends CommonWrapper<VolumnGridAdapter.SearchResultListHolder> {

    private final static String TAG = ClassificationGridAdapter.class.getSimpleName();
    //    private List<Commodity> data;
    private List<Item> data;

    private Context mContext;
    private String searchQuery;

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    public VolumnGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setData(List<Item> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<Item> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    public void addItemData(List<Item> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }


    @Override
    public SearchResultListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SearchResultListHolder(LayoutInflater.from(mContext).inflate(R.layout.classification_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(final SearchResultListHolder holder, final int position) {

        final Item item = data.get(position);


        final ArticleAndGoods goodsData = item.item;
        final UserData userData = item.user;
        //GoodsValue goodsValue = item.getGoodsValue();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, goodsData.goodsId + "");
                intent.putExtra(MyConstants.POSITION, position);
                intent.putExtra(MyConstants.SEARCH_QUERY, searchQuery);
                ((Activity) mContext).startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
            }
        });

        if (userData != null && !TextUtils.isEmpty(userData.getAvatarUrl())) {
            ImageLoader.resizeSmall(holder.ivHeader, userData.getAvatarUrl(), 1);
            //设置名称
            holder.userView.setData(userData);
        }

        if (goodsData != null) {
            String cover = goodsData.videoCover;
            if (!TextUtils.isEmpty(cover)) {
                holder.ivPlay.setVisibility(View.VISIBLE);
                ImageLoader.loadImage(holder.ivProPic, cover);
            } else {
                if (!ArrayUtil.isEmpty(goodsData.goodsImgs)) {
                    ImageLoader.loadImage(holder.ivProPic, goodsData.goodsImgs.get(0));
                }
                holder.ivPlay.setVisibility(View.GONE);
            }

            holder.cptv.setData(goodsData);

            String brand = goodsData.brand;
            String goodsName = goodsData.name;
            String goodsNameDesc;
            if (!TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                String format = mContext.getString(R.string.goods_name_desc);
                goodsNameDesc = String.format(format, brand, goodsName);
            } else if (!TextUtils.isEmpty(brand) && TextUtils.isEmpty(goodsName)) {
                goodsNameDesc = brand;
            } else if (TextUtils.isEmpty(brand) && !TextUtils.isEmpty(goodsName)) {
                goodsNameDesc = goodsName;
            } else {
                goodsNameDesc = "";
            }
            holder.tvTitle.setText(goodsNameDesc);

            holder.layoutLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startForRequestWantBuy(position, holder.layoutLike);
                }
            });
            boolean isLike = goodsData.liked;
            if (isLike) {
                holder.ivLike.setImageResource(R.drawable.btn_item_like_select);
                holder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
            } else {
                holder.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                holder.tvLike.setTextColor(Color.parseColor("#888888"));
            }
            if (goodsData != null) {

                int likeCount = goodsData.likeCount;
                String count = CommonUtils.getDoubleCount(likeCount, MyConstants.COUNT_FANS);
                holder.tvLike.setText(count);
            }


        }


        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();

        int paddingOut = CommonUtils.dp2px(10f);
        int paddingInner = CommonUtils.dp2px(10f) / 2;

        params.topMargin = paddingOut;
        if ((position % 2) == 0) {
            params.leftMargin = paddingOut;
            params.rightMargin = paddingInner;
        } else {
            params.leftMargin = paddingInner;
            params.rightMargin = paddingOut;
        }

        int count = data.size();
        int lastRow = count / 2 + count % 2;
        int currentRow = (position + 1) / 2 + (position + 1) % 2;
        if (lastRow == currentRow) {
            params.bottomMargin = paddingOut;
        } else {
            params.bottomMargin = 0;
        }


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    private void startForRequestWantBuy(final int position, final View view) {
        if (!CommonUtils.isNetAvaliable(mContext)) {
            ToastUtil.toast(mContext, "请检查网络连接");
            return;
        }
        if (data == null) {
            return;
        }
        final Item item = data.get(position);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goodsId", String.valueOf(item.item.goodsId));
        final boolean isLike = item.item.liked;
        if (isLike) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i(TAG, "params:" + params.toString());
        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                view.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                LogUtil.i("liang", "喜欢:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (isLike) {
                            //item.getGoodsData().setLiked(false);
                            item.item.liked = false;
                            if (item.item.likeCount > 0) {
                                item.item.likeCount = item.item.likeCount - 1;
                                //item.getGoodsValue().setLikeCount(item.item.likeCount - 1);
                            }
                            notifyWrapperDataSetChanged();
                        } else {
                            item.item.liked = true;
                            //item.getGoodsData().setLiked(true);
                            item.item.likeCount = item.item.likeCount + 1;
                            //item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() + 1);
                            notifyWrapperDataSetChanged();
                        }

                    } else {
                        CommonUtils.error(baseResult, (Activity) mContext, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void updateLikeState(final int position, final int type) {
        final Item item = data.get(position);
        if (type == 2) {
            //item.getGoodsData().setLiked(false);
            item.item.liked = false;
            if (item.item.likeCount > 0) {
                //item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() - 1);
                item.item.likeCount = item.item.likeCount - 1;
            }
        } else {
            item.item.liked = true;
            item.item.likeCount = item.item.likeCount + 1;
//            item.getGoodsData().setLiked(true);
//            item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() + 1);
        }

        notifyWrapperDataSetChanged();
    }

    public class SearchResultListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProPic)
        SimpleDraweeView ivProPic;
        @BindView(R.id.ivPlay)
        ImageView ivPlay;
        @BindView(R.id.flImage)
        FrameLayout flImage;
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.cptv)
        CommonPriceTagView cptv;
        @BindView(R.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R.id.userView)
        UserView userView;
        @BindView(R.id.llBottom)
        LinearLayout llBottom;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tvLike)
        TextView tvLike;
        @BindView(R.id.layoutLike)
        LinearLayout layoutLike;

        public SearchResultListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
