package com.huapu.huafen.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.HPCommentListActivityNew;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsValue;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.RecommendListFragment;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.CommonPriceTagView;
import com.huapu.huafen.views.DashLineView;
import com.huapu.huafen.views.HRecyclerView;
import com.huapu.huafen.views.UserView;
import com.squareup.okhttp.Request;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/11/5.
 */
public class FCommodityAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    public enum ITEM_TYPE {
        ITEM_TYPE_IMAGE,
        ITEM_TYPE_DATA
    }

    private List<Commodity> data;
    private Fragment fragment;

    public FCommodityAdapter(Fragment fragment, ArrayList<Commodity> data) {

        if (data == null) {
            this.data = new ArrayList<>();
        } else {
            this.data = data;
        }

        this.fragment = fragment;
    }

    public FCommodityAdapter(Fragment fragment) {
        this(fragment, null);
    }


    public void setData(List<Commodity> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }


    public void addAll(List<Commodity> data) {
        if (data == null) {
            data = new ArrayList<>();
        }

        this.data.addAll(data);
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
        return item.getType() == 1 ? ITEM_TYPE.ITEM_TYPE_DATA.ordinal() : ITEM_TYPE.ITEM_TYPE_IMAGE.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        if (itemType == ITEM_TYPE.ITEM_TYPE_DATA.ordinal()) {
            return new CommodityViewHolder(inflater.inflate(R.layout.item_commodity, parent, false));
        } else {
            return new ImageViewHolder(inflater.inflate(R.layout.recommend_image_layout, parent, false));
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
                    Intent intent = new Intent(fragment.getActivity(), GoodsDetailsActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, item.getGoodsData().getGoodsId() + "");
                    intent.putExtra("position", position);
                    fragment.startActivityForResult(intent, RecommendListFragment.REQUEST_CODE_FOR_GOODS_DETAIL);
                }
            });
            if (item != null && item.getUserData() != null && item.getGoodsData() != null) {
                final UserData userData = item.getUserData();
                GoodsData goodsData = item.getGoodsData();
                GoodsValue goodsValue = item.getGoodsValue();
                vh.userView.setData(userData);

                ViewUtil.setAvatar(vh.ivHeader, userData.getAvatarUrl());

                vh.cptv.setData(goodsData);
                if (TextUtils.isEmpty(goodsData.getContent())) {
                    vh.tvDes.setVisibility(View.GONE);
                } else {
                    vh.tvDes.setVisibility(View.VISIBLE);
                    vh.tvDes.setText(goodsData.getContent());
                }

                int distance = goodsData.getDistance();
                String distanceDesc = null;
                if (distance > 0 && distance <= 999) {
                    distanceDesc = distance + "m";
                } else if (distance > 999) {
                    float dis = ((float) distance) / 1000;
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    String desc = decimalFormat.format(dis);
                    distanceDesc = desc + "km";
                }
                String city = null;
                String district = null;
                Area area = goodsData.getArea();
                if (area != null) {
                    city = area.getCity();
                    district = area.getArea();
                }
                vh.dlvCity.setData(distanceDesc, city, district);

                vh.layoutVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //跳转到留言界面
                        Intent intent = new Intent(fragment.getActivity(), HPCommentListActivityNew.class);
                        intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, Long.valueOf(item.getGoodsData().getGoodsId()));
                        intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_TYPE, 1);
                        fragment.startActivity(intent);
                    }
                });
                vh.tvVoice.setText(item.getGoodsValue().getComments() + "");
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
                        Intent intent = new Intent(fragment.getActivity(), PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, userData.getUserId());
                        fragment.startActivity(intent);
                    }
                });

                List<VBanner> banners = covertBanners(goodsData);
                vh.adapter.setData(banners);
                vh.adapter.setGoodsId(item.getGoodsData().getGoodsId());
                boolean isLike = goodsData.getLiked();
                if (isLike) {
                    vh.ivLike.setImageResource(R.drawable.btn_item_like_select);
                    vh.tvLike.setTextColor(Color.parseColor("#ffff6677"));
                } else {
                    vh.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                    vh.tvLike.setTextColor(Color.parseColor("#888888"));
                }
                if (goodsValue != null) {
                    vh.tvLike.setText(String.valueOf(goodsValue.getLikeCount()));
                } else {
                    vh.tvLike.setText(String.valueOf(goodsData.getLikeCount()));
                }
                vh.dlvGoodsName.setData(goodsData.getBrand(), goodsData.getName());
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
                ImageLoader.loadImage(vh.imageView, url);
                vh.imageView.setTag(url);
            }

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionUtil.dispatchAction(fragment.getActivity(), item.getAction(), item.getTarget());
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

        if (!TextUtils.isEmpty(goodsData.getVideoPath()) && !TextUtils.isEmpty(goodsData.getVideoCover())) {
            VBanner videoBanner = new VBanner(1, goodsData.getVideoCover());
            videoBanner.videoPath = goodsData.getVideoPath();
            banners.add(videoBanner);
        }

        for (String banner : images) {
            VBanner vBanner = new VBanner(2, banner);
            banners.add(vBanner);
        }

        return banners;

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
                                GoodsValue goodsValue = item.getGoodsValue();
                                if (goodsValue != null) {
                                    if (isLike) {
                                        item.getGoodsData().setLiked(false);
                                        if (goodsValue.getLikeCount() > 0) {
                                            goodsValue.setLikeCount(goodsValue.getLikeCount() - 1);
                                        }
                                        notifyWrapperDataSetChanged();
                                    } else {
                                        item.getGoodsData().setLiked(true);
                                        goodsValue.setLikeCount(goodsValue.getLikeCount() + 1);
                                        notifyWrapperDataSetChanged();
                                    }
                                } else {
                                    if (isLike) {
                                        item.getGoodsData().setLiked(false);
                                        if (item.getGoodsData().getLikeCount() > 0) {
                                            item.getGoodsData().setLikeCount(item.getGoodsData().getLikeCount() - 1);
                                        }
                                        notifyWrapperDataSetChanged();
                                    } else {
                                        item.getGoodsData().setLiked(true);
                                        item.getGoodsData().setLikeCount(item.getGoodsData().getLikeCount() + 1);
                                        notifyWrapperDataSetChanged();
                                    }
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
            if (item.getGoodsValue() != null) {
                if (item.getGoodsValue().getLikeCount() > 0) {
                    item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() - 1);
                }
            } else {
                if (item.getGoodsData().getLikeCount() > 0) {
                    item.getGoodsData().setLikeCount(item.getGoodsData().getLikeCount() - 1);
                }
            }

        } else {
            item.getGoodsData().setLiked(true);
            if (item.getGoodsValue() != null) {
                item.getGoodsValue().setLikeCount(item.getGoodsValue().getLikeCount() + 1);
            } else {
                item.getGoodsData().setLikeCount(item.getGoodsData().getLikeCount() + 1);
            }

        }

        notifyWrapperDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class CommodityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R.id.userView)
        UserView userView;
        @BindView(R.id.rvHorizontalPic)
        HRecyclerView rvHorizontalPic;
        @BindView(R.id.cptv)
        CommonPriceTagView cptv;
        @BindView(R.id.dlvGoodsName)
        DashLineView dlvGoodsName;
        @BindView(R.id.tvDes)
        TextView tvDes;
        @BindView(R.id.dlvCity)
        DashLineView dlvCity;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tvLike)
        TextView tvLike;
        @BindView(R.id.layoutLike)
        LinearLayout layoutLike;
        @BindView(R.id.ivVoice)
        ImageView ivVoice;
        @BindView(R.id.tvVoice)
        TextView tvVoice;
        @BindView(R.id.layoutVoice)
        LinearLayout layoutVoice;
        @BindView(R.id.layoutToyTitle)
        RelativeLayout layoutToyTitle;
        @BindView(R.id.ivSelled)
        ImageView ivSelled;
        @BindView(R.id.contentListView)
        RelativeLayout contentListView;
        private HorizontalRecyclerImageAdapter adapter;

        public CommodityViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutManager layoutManagerPic = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            rvHorizontalPic.setLayoutManager(layoutManagerPic);
            ViewUtil.setOffItemAnimator(rvHorizontalPic);

            adapter = new HorizontalRecyclerImageAdapter(fragment);
            rvHorizontalPic.setAdapter(adapter);
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivPic)
        SimpleDraweeView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(data);
    }

}
