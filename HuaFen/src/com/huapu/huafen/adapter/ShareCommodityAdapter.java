package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/11/5.
 */
public class ShareCommodityAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    public enum ITEM_TYPE {
        ITEM_TYPE_IMAGE,
        ITEM_TYPE_DATA
    }

    private List<Commodity> data;
    private Activity context;

    public ShareCommodityAdapter(Activity context, ArrayList<Commodity> data) {

        if (data == null) {
            this.data = new ArrayList<Commodity>();
        } else {
            this.data = data;
        }

        this.context = context;
    }

    public ShareCommodityAdapter(Activity context) {
        this(context, null);
    }


    public void setData(List<Commodity> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addData(List<Commodity> data) {
        if (!ArrayUtil.isEmpty(data)) {
            this.data.addAll(data);
        }
        notifyWrapperDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        Commodity item = data.get(position);
        return item.getType() == 2 ? ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal() : ITEM_TYPE.ITEM_TYPE_DATA.ordinal();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        if (itemType == ITEM_TYPE.ITEM_TYPE_DATA.ordinal()) {
            return new CommodityViewHolder(LayoutInflater.from(context).inflate(R.layout.item_commodity, parent, false));
        } else {
            return new ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.recommend_image_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Commodity item = data.get(position);
        if (holder instanceof CommodityViewHolder) {
            final CommodityViewHolder vh = (CommodityViewHolder) holder;
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String jsonString = JSON.toJSONString(item);
                    Intent intent = new Intent();
                    intent.putExtra("json", jsonString);
                    intent.putExtra("extra", String.valueOf(item.getGoodsData().getGoodsId()));
                    context.setResult(Activity.RESULT_OK, intent);
                    context.finish();
                }
            });
            if (item != null && item.getUserData() != null && item.getGoodsData() != null) {
                final UserData userData = item.getUserData();
                GoodsData goodsData = item.getGoodsData();
                vh.ctvName.setData(userData);
                String tag = (String) vh.ivHeader.getTag();
                if (!TextUtils.isEmpty(tag)) {
                    if (!tag.equals(userData.getAvatarUrl())) {
                        vh.ivHeader.setTag(userData.getAvatarUrl());

                        ImageLoader.resizeSmall(vh.ivHeader, userData.getAvatarUrl(), 1);
                    }
                } else {
                    vh.ivHeader.setTag(userData.getAvatarUrl());
                    ImageLoader.resizeSmall(vh.ivHeader, userData.getAvatarUrl(), 1);
                }

                vh.cptv.setData(goodsData);
                if (TextUtils.isEmpty(goodsData.getContent())) {
                    vh.tvDes.setVisibility(View.GONE);
                } else {
                    vh.tvDes.setVisibility(View.VISIBLE);
                    vh.tvDes.setText(goodsData.getContent());
                }
                vh.dlvCity.setData(goodsData.getArea().getCity(),
                        goodsData.getArea().getArea());
                //喜欢按钮
                vh.layoutLike.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startForRequestWantBuy(position, vh.layoutLike);
                    }
                });
                vh.ivHeader.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, userData.getUserId());
                        context.startActivity(intent);
                    }
                });

                List<VBanner> banners = covertBanners(goodsData);
                vh.reAdapter.setData(banners);
                vh.reAdapter.setGoodsId(item.getGoodsData().getGoodsId());
                boolean isLike = goodsData.getLiked();
                if (isLike) {
                    vh.ivLike.setImageResource(R.drawable.btn_item_like_select);
                    vh.tvLike.setTextColor(Color.parseColor("#ffff6677"));
                } else {
                    vh.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                    vh.tvLike.setTextColor(Color.parseColor("#888888"));
                }

                if (item.getGoodsValue() != null) {
                    vh.tvLike.setText(String.valueOf(item.getGoodsValue().getLikeCount()));
                }

                vh.dlvGoodsName.setData(goodsData.getBrand(), goodsData.getName());
                if (item.getGoodsData().getGoodsState() == 4) {
                    vh.ivSelled.setVisibility(View.VISIBLE);
                } else {
                    vh.ivSelled.setVisibility(View.GONE);
                }
            }
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder vh = (ImageViewHolder) holder;

            int height = CommonUtils.getMyHeight();
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) vh.imageView.getLayoutParams();
            params.height = height;
            vh.imageView.setLayoutParams(params);

            String url = item.getImage();
            String tag = (String) vh.imageView.getTag();
            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                vh.imageView.setTag(url);
                ImageLoader.loadImage(vh.imageView, url);
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionUtil.dispatchAction(context, item.getAction(), item.getTarget());
                }
            });
        }
    }

    private ArrayList<VBanner> covertBanners(GoodsData goodsData) {
        if (goodsData == null) {
            return null;
        }
        ArrayList<VBanner> banners = new ArrayList<VBanner>();
        List<String> imgs = goodsData.getGoodsImgs();
        ArrayList<String> images = CommonUtils.getOSSStyle(imgs, MyConstants.OSS_SMALL_STYLE);

        if (!TextUtils.isEmpty(goodsData.getVideoCover())) {
            VBanner videoBanner = new VBanner(1, goodsData.getVideoCover());
            banners.add(videoBanner);
        }

        for (String banner : images) {
            VBanner vBanner = new VBanner(2, banner);
            banners.add(vBanner);
        }

        return banners;

    }

    private void startForRequestWantBuy(final int position, final View view) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
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
        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        view.setEnabled(true);
                    }

                    @Override
                    public void onResponse(String response) {
                        view.setEnabled(true);
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
                                CommonUtils.error(baseResult, context, "");
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
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class CommodityViewHolder extends RecyclerView.ViewHolder {

        private View itemView;
        public CommonTitleView ctvName;
        public TextView tvLike;
        public TextView tvDes;
        public DashLineView dlvCity;
        public RecyclerView rvHorizontalPic;
        public HorizontalRecyclerImageAdapter reAdapter;
        public SimpleDraweeView ivHeader;
        public ImageView ivLike;
        public View layoutLike;
        private DashLineView dlvGoodsName;
        private CommonPriceTagView cptv;
        private ImageView ivSelled;

        public CommodityViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivSelled = (ImageView) itemView.findViewById(R.id.ivSelled);
            ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
            tvDes = (TextView) itemView.findViewById(R.id.tvDes);
            dlvCity = (DashLineView) itemView.findViewById(R.id.dlvCity);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            rvHorizontalPic = (RecyclerView) itemView
                    .findViewById(R.id.rvHorizontalPic);
            reAdapter = new HorizontalRecyclerImageAdapter(context);
            rvHorizontalPic.setAdapter(reAdapter);
            dlvGoodsName = (DashLineView) itemView.findViewById(R.id.dlvGoodsName);
            // 创建一个线性布局管理器
            LinearLayoutManager layoutManagerPic = new LinearLayoutManager(
                    context);
            layoutManagerPic.setOrientation(LinearLayoutManager.HORIZONTAL);
            // 设置布局管理器
            rvHorizontalPic.setLayoutManager(layoutManagerPic);
            cptv = (CommonPriceTagView) itemView.findViewById(R.id.cptv);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public SimpleDraweeView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
        }
    }

}
