package com.huapu.huafen.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
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
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonPriceView;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FCommodityGridAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    private static final long day = 3600 * 24 * 1000L;
    private static final long hours = 3600 * 1000L;
    private static final long minutes = 60 * 1000L;
    private List<Commodity> data;
    private Fragment fragment;
    private ArrayList<CommodityGridViewHolder> viewHolderArrayList = new ArrayList<>();


    public FCommodityGridAdapter(Fragment fragment, List<Commodity> data) {
        this.fragment = fragment;
        this.data = data;
    }

    public FCommodityGridAdapter(Fragment fragment) {
        this(fragment, null);
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(data);
    }

    public List<Commodity> getData() {
        return this.data;
    }

    public void setData(List<Commodity> data) {
        this.data = data;
        viewHolderArrayList.clear();
        notifyWrapperDataSetChanged();
    }

    public void addData(List<Commodity> data) {
        if (this.data == null) {
            this.data = new ArrayList<Commodity>();
        }
        this.data.addAll(data);

        notifyWrapperDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Commodity item = data.get(position);
        return item.getType() == 1 ? ITEM_TYPE.ITEM_TYPE_DATA.ordinal() : ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal();
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {

        final Commodity item = data.get(position);

        if (viewHolder instanceof FCommodityGridAdapter.CommodityGridViewHolder) {


            if (item == null || item.getUserData() == null) {
                return;
            }
            final CommodityGridViewHolder vh = (CommodityGridViewHolder) viewHolder;
            if (!viewHolderArrayList.contains(vh)) {
                viewHolderArrayList.add(vh);
            }
            vh.setPosition(position);
            vh.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(fragment.getActivity(), GoodsDetailsActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, item.getGoodsData().getGoodsId() + "");
                    fragment.startActivity(intent);
                }
            });

            GoodsData goodsData = item.getGoodsData();

            if (goodsData.isAuction == 1) {
                vh.preSellLayout1.setVisibility(View.VISIBLE);
                vh.preSellLayout.setVisibility(View.GONE);
                vh.tvSellPrice.setVisibility(View.VISIBLE);
                vh.cpvPrice.setVisibility(View.GONE);
                if (goodsData.getGoodsState() == 1) {//开售
                    vh.preSellLayout1.setVisibility(View.VISIBLE);
                    vh.preSellLayout1.setBackgroundColor(Color.parseColor("#80AEA9D7"));
                    vh.preSellText1.setText("结束时间：" + DateTimeUtils.getStringDate(goodsData.bidEndTime, DateTimeUtils.MM_DD_HH_MM));
                    vh.preSellText1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//                    String des = String.format(fragment.getContext().getString(R.string.auction_price),"起拍价",goodsData.hammerPrice);
                    String des;
                    if (goodsData.bidder > 0) {
                        des = String.format(fragment.getContext().getString(R.string.auction_price), "当前价", goodsData.hammerPrice);
                    } else {
                        des = String.format(fragment.getContext().getString(R.string.auction_price), "起拍价", goodsData.hammerPrice);
                    }
                    vh.tvSellPrice.setText(Html.fromHtml(des));
                } else if (goodsData.getGoodsState() == 2) {//预售
                    vh.preSellLayout1.setVisibility(View.VISIBLE);
                    vh.preSellLayout1.setBackgroundColor(Color.parseColor("#80AEA9D7"));
                    vh.preSellText1.setText("开拍时间：" + DateTimeUtils.getStringDate(goodsData.bidStartTime, DateTimeUtils.MM_DD_HH_MM));
                    vh.preSellText1.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    String des;
                    if (goodsData.bidder > 0) {
                        des = String.format(fragment.getContext().getString(R.string.auction_price), "当前价", goodsData.hammerPrice);
                    } else {
                        des = String.format(fragment.getContext().getString(R.string.auction_price), "起拍价", goodsData.hammerPrice);
                    }
                    vh.tvSellPrice.setText(Html.fromHtml(des));
                } else if (goodsData.getGoodsState() == 4) {//已售出
                    vh.preSellLayout1.setVisibility(View.VISIBLE);
                    vh.preSellLayout1.setBackgroundColor(Color.parseColor("#4D333333"));
                    vh.preSellText1.setText("拍卖结束");
                    vh.preSellText1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.auction_icon_small, 0, 0, 0);
                    String des = String.format(fragment.getContext().getString(R.string.auction_price), "成交价", goodsData.hammerPrice);
                    vh.tvSellPrice.setText(Html.fromHtml(des));
                } else {
                    vh.preSellLayout1.setVisibility(View.GONE);
                    vh.tvSellPrice.setText("");
                }
            } else {
                vh.preSellLayout1.setVisibility(View.GONE);
                vh.preSellLayout.setVisibility(View.VISIBLE);
                vh.tvSellPrice.setVisibility(View.GONE);
                vh.cpvPrice.setVisibility(View.VISIBLE);
                long preSellTime = goodsData.getPresell();
                long systemTime = System.currentTimeMillis();
                long residue = preSellTime - systemTime;
                if (residue > 0) {
                    vh.preSellLayout.setVisibility(View.VISIBLE);
                    vh.preSellText.setText("距离开售" + DateTimeUtils.getResidue(residue));
                    vh.preSellText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.shop_list_ys_icon, 0, 0, 0);
                } else {
                    vh.preSellLayout.setVisibility(View.GONE);
                }

                vh.cpvPrice.setData(item.getGoodsData());

            }

            String cover = item.getGoodsData().getVideoCover();


            if (!TextUtils.isEmpty(cover)) {
                vh.ivPic.setImageURI(cover);
                vh.ivPlay.setVisibility(View.VISIBLE);
            } else {
                vh.ivPlay.setVisibility(View.GONE);
                if (item.getGoodsData() != null && !ArrayUtil.isEmpty(item.getGoodsData().getGoodsImgs())) {
                    String url = item.getGoodsData().getGoodsImgs().get(0);
                    String tag = (String) vh.ivPic.getTag();
                    if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                        vh.ivPic.setTag(url);
                        vh.ivPic.setImageURI(url);
                    }
                }
            }

            vh.dlvGoodsName.setData(item.getGoodsData().getBrand(), item.getGoodsData().getName().trim());
            vh.layoutLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startForRequestWantBuy(position, vh.layoutLike);
                }
            });
            int isNew = item.getGoodsData().getIsNew();
            if (isNew == 0) {
                vh.ivNewSuperscriptHome.setVisibility(View.GONE);
            } else {
                vh.ivNewSuperscriptHome.setVisibility(View.VISIBLE);
            }
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

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) vh.itemView.getLayoutParams();

            int paddingOut = CommonUtils.dp2px(10f);
            int paddingInner = CommonUtils.dp2px(10f) / 2;
            params.bottomMargin = paddingOut;
            if ((position % 2) == 0) {
                params.leftMargin = paddingOut;
                params.rightMargin = paddingInner;
            } else {
                params.leftMargin = paddingInner;
                params.rightMargin = paddingOut;
            }
            params.height = (CommonUtils.getScreenWidth() - 3 * CommonUtils.dp2px(10f)) / 2 * 4 / 3;
        } else if (viewHolder instanceof ImageViewHolder) {
            ImageViewHolder vh = (ImageViewHolder) viewHolder;

            String url = item.getImage();
            String tag = (String) vh.imageView.getTag();
            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                vh.imageView.setImageURI(url);
                vh.imageView.setTag(url);
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionUtil.dispatchAction(fragment.getActivity(), item.getAction(), item.getTarget());
                }
            });

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) vh.itemView.getLayoutParams();
            int paddingOut = CommonUtils.dp2px(10f);
            int paddingInner = CommonUtils.dp2px(10f) / 2;
            params.bottomMargin = paddingOut;
            if ((position % 2) == 0) {
                params.leftMargin = paddingOut;
                params.rightMargin = paddingInner;
            } else {
                params.leftMargin = paddingInner;
                params.rightMargin = paddingOut;
            }

            params.height = (CommonUtils.getScreenWidth() - 3 * CommonUtils.dp2px(10f)) / 2 * 4 / 3;
        }
    }

    private String parseTime(long leftTime) {
        if (leftTime < 0) {
            return "";
        } else {
            String formatTime;
            if (leftTime >= day) {
                formatTime = String.valueOf(leftTime / day) + "天";
            } else if (leftTime < day && leftTime >= hours) {
                formatTime = String.valueOf(leftTime / hours) + "小时";
            } else if (leftTime < hours && leftTime >= minutes) {
                formatTime = String.valueOf(leftTime / minutes) + "分钟";
            } else {
                formatTime = String.valueOf(leftTime / 1000) + "秒";
            }
            return formatTime;
        }

    }

    private void startForRequestWantBuy(final int position, final View view) {
        if (!CommonUtils.isNetAvaliable(fragment.getActivity())) {
            ToastUtil.toast(fragment.getActivity(), "请检查网络连接");
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
        OkHttpClientManager.postAsyn(MyConstants.WANTBUY, params, new OkHttpClientManager.StringCallback() {

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
                        CommonUtils.error(baseResult, fragment.getActivity(), "");
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        if (itemType == ITEM_TYPE.ITEM_TYPE_DATA.ordinal()) {
            CommodityGridViewHolder vh = new CommodityGridViewHolder(LayoutInflater.from(fragment.getActivity()).inflate(
                    R.layout.item_gird_commodity, parent, false));
            return vh;
        } else {
            ImageViewHolder vh = new ImageViewHolder(LayoutInflater.from(fragment.getActivity()).inflate(R.layout.recommend_image_layout, parent, false));
            return vh;
        }
    }

    public void notifyCountDownDataSetChanged() {

        for (CommodityGridViewHolder viewHolder : viewHolderArrayList) {
            if (!ArrayUtil.isEmpty(data)) {
                if (viewHolder.position > data.size() - 1 || viewHolder.position < 0) {
                    return;
                }
                Commodity tmp = data.get(viewHolder.position);
                GoodsData goodsData = tmp.getGoodsData();
                long preSellTime = goodsData.getPresell();
                long systemTime = System.currentTimeMillis();
                long residue = preSellTime - systemTime;
                if (residue > 0) {
                    viewHolder.preSellLayout.setVisibility(View.VISIBLE);
                    viewHolder.preSellText.setText("距离开售" + DateTimeUtils.getResidue(residue));
                } else {
                    viewHolder.preSellLayout.setVisibility(View.GONE);
                }
            }
        }
    }

    public enum ITEM_TYPE {
        ITEM_TYPE_IMAGE,
        ITEM_TYPE_DATA
    }

    public class CommodityGridViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout llContainer;
        public SimpleDraweeView ivPic;
        public DashLineView dlvGoodsName;
        public LinearLayout layoutLike;
        public ImageView ivLike;
        public TextView tvLikeCount;
        public FrameLayout flImage;
        public ImageView ivPlay;
        public CommonPriceView cpvPrice;
        public SimpleDraweeView ivNewSuperscriptHome;
        public FrameLayout preSellLayout;
        public TextView preSellText;
        public FrameLayout preSellLayout1;
        public TextView preSellText1;
        public TextView tvSellPrice;
        private int position;

        public CommodityGridViewHolder(View itemView) {
            super(itemView);
            llContainer = (LinearLayout) itemView.findViewById(R.id.llContainer);
            ivPic = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
            ImageLoader.loadImage(ivPic, null, R.drawable.default_pic, R.drawable.default_pic);
            dlvGoodsName = (DashLineView) itemView.findViewById(R.id.dlvGoodsName);
            layoutLike = (LinearLayout) itemView.findViewById(R.id.layoutLike);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            tvLikeCount = (TextView) itemView.findViewById(R.id.tvLikeCount);
            flImage = (FrameLayout) itemView.findViewById(R.id.flImage);
            ivPlay = (ImageView) itemView.findViewById(R.id.ivPlay);
            ivNewSuperscriptHome = (SimpleDraweeView) itemView.findViewById(R.id.ivNewSuperscriptHome);
            cpvPrice = (CommonPriceView) itemView.findViewById(R.id.cpvPrice);
            preSellLayout = (FrameLayout) itemView.findViewById(R.id.preSellLayout);
            preSellText = (TextView) itemView.findViewById(R.id.preSellText);
            preSellLayout1 = (FrameLayout) itemView.findViewById(R.id.preSellLayout1);
            preSellText1 = (TextView) itemView.findViewById(R.id.preSellText1);
            tvSellPrice = (TextView) itemView.findViewById(R.id.tvSellPrice);
        }

        public void setPosition(int position) {
            this.position = position;
        }

    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public SimpleDraweeView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
            ImageLoader.loadImage(imageView, null, R.drawable.default_pic, R.drawable.default_pic);
        }
    }

}
