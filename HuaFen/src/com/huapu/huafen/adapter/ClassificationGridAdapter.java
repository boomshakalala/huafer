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
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsValue;
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
 * Created by admin on 2017/4/13.
 */

public class ClassificationGridAdapter extends CommonWrapper<ClassificationGridAdapter.SearchResultListHolder> {

    private final static String TAG = ClassificationGridAdapter.class.getSimpleName();
    private List<Commodity> data;
    private Context mContext;
    private String searchQuery;

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }


    public ClassificationGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setData(List<Commodity> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<Commodity> data) {
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

        final Commodity item = data.get(position);
        GoodsData goodsData = item.getGoodsData();
        final UserData userData = item.getUserData();
        GoodsValue goodsValue = item.getGoodsValue();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, item.getGoodsData().getGoodsId() + "");
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
            String cover = goodsData.getVideoCover();
            if (!TextUtils.isEmpty(cover)) {
                ImageLoader.loadImage(holder.ivProPic, cover);
                holder.ivPlay.setVisibility(View.VISIBLE);
            } else {
                if (!ArrayUtil.isEmpty(goodsData.getGoodsImgs())) {
                    ImageLoader.loadImage(holder.ivProPic, goodsData.getGoodsImgs().get(0));
                }
                holder.ivPlay.setVisibility(View.GONE);
            }

            holder.cptv.setData(goodsData);

            String brand = goodsData.getBrand();
            String goodsName = goodsData.getName();
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
            boolean isLike = goodsData.getLiked();
            if (isLike) {
                holder.ivLike.setImageResource(R.drawable.btn_item_like_select);
                holder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
            } else {
                holder.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                holder.tvLike.setTextColor(Color.parseColor("#888888"));
            }
            if (goodsValue != null) {
                int likeCount = goodsValue.getLikeCount();
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
        final Commodity item = data.get(position);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("goodsId", String.valueOf(item.getGoodsData().getGoodsId()));
        final boolean isLike = item.getGoodsData().getLiked();
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
                            item.getGoodsData().setLiked(false);
                            if (item.getGoodsValue().getLikeCount() > 0) {
                                item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() - 1);
                            }
                            notifyWrapperDataSetChanged();
                        } else {
                            item.getGoodsData().setLiked(true);
                            item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() + 1);
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
        final Commodity item = data.get(position);
        if (type == 2) {
            item.getGoodsData().setLiked(false);
            if (item.getGoodsValue().getLikeCount() > 0) {
                item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() - 1);
            }
        } else {
            item.getGoodsData().setLiked(true);
            item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() + 1);
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
