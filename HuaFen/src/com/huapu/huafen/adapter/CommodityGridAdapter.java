package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommodityGridAdapter extends CommonWrapper<CommodityGridAdapter.CommodityGridViewHolder> {

    private List<Commodity> data;
    private Context mContext;

    public CommodityGridAdapter(Context context, ArrayList<Commodity> data) {
        this.mContext = context;
        this.data = data;
    }

    public void setData(List<Commodity> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<Commodity> data) {
        if (this.data == null) {
            this.data = new ArrayList<Commodity>();
        }
        this.data.addAll(data);

        notifyWrapperDataSetChanged();
    }

    public class CommodityGridViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView ivPic;
        public CommonTitleView ctvName;
        public TextView tvPrice;
        public TextView tvPastPrice;
        public DashLineView dlvGoodsName;
        public LinearLayout layoutLike;
        public ImageView ivLike;
        public TextView tvLikeCount;

        public CommodityGridViewHolder(View itemView) {
            super(itemView);
            ivPic = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
            ImageLoader.loadImage(ivPic, null, R.drawable.default_pic, R.drawable.default_pic);
            ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvPastPrice = (TextView) itemView.findViewById(R.id.tvPastPrice);
            tvPastPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            dlvGoodsName = (DashLineView) itemView.findViewById(R.id.dlvGoodsName);
            layoutLike = (LinearLayout) itemView.findViewById(R.id.layoutLike);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
        }

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onBindViewHolder(CommodityGridViewHolder vh, final int position) {

        final Commodity item = data.get(position);
        if (item == null || item.getUserData() == null) {
            return;
        }
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, item.getGoodsData().getGoodsId() + "");
                mContext.startActivity(intent);
            }
        });


        List<String> imgs = item.getGoodsData().getGoodsImgs();

        WindowManager wm = ((Activity) mContext).getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) vh.ivPic.getLayoutParams();
        lp.height = width / 2;
        lp.width = lp.height;
        vh.ivPic.setLayoutParams(lp);

        if (item.getGoodsData() != null && !ArrayUtil.isEmpty(item.getGoodsData().getGoodsImgs())) {
            String url = item.getGoodsData().getGoodsImgs().get(0);
            String tag = (String) vh.ivPic.getTag();
            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                vh.ivPic.setTag(url);
                vh.ivPic.setImageURI(url);
            }
        }


        vh.ctvName.setData(item.getUserData());
        vh.dlvGoodsName.setData(item.getGoodsData().getBrand(), item.getGoodsData().getName().trim());
        vh.tvPrice.setText(String.valueOf(item.getGoodsData().getPrice()));
        vh.tvPastPrice.setText("¥" + String.valueOf(item.getGoodsData().getPastPrice()));

        vh.layoutLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startForRequestWantBuy(position);
            }
        });

        boolean isLike = item.getGoodsData().getLiked();
        if (isLike) {
            vh.ivLike.setImageResource(R.drawable.btn_item_like_select);
            vh.tvLikeCount.setTextColor(Color.parseColor("#ffff6677"));
        } else {
            vh.ivLike.setImageResource(R.drawable.btn_item_like_normal);
            vh.tvLikeCount.setTextColor(Color.parseColor("#888888"));
        }

        if (item.getGoodsValue() != null) {
            vh.tvLikeCount.setText(String.valueOf(item.getGoodsValue().getLikeCount()));
        }
    }


    private void startForRequestWantBuy(final int position) {
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
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params,
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

    @Override
    public CommodityGridViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        CommodityGridViewHolder vh = new CommodityGridViewHolder(LayoutInflater.from(mContext).inflate(
                R.layout.item_gird_commodity, parent, false));
        return vh;
    }

}
