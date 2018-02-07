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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.PickArticleModeActivity;
import com.huapu.huafen.beans.ArticleData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.FlowerData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.StringUtils;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

/**
 * Created by lvgy on 2017/5/13.
 */

public class MyFlowerAdapter extends AbstractRecyclerAdapter {

    private static final int TYPE_HEADER_TWO = 200;
    private static final int TYPE_HEADER_TWO_FULL = 205;
    private static final int REQUEST_FOR_ARTICLE_DETAIL = 202;
    private static final int TYPE_MARGIN = 210;
    private final Context mContext;
    private int userLevel;
    private FlowerData flowerData;
    private int height;
    private int width;

    private boolean isMe = false;

    public MyFlowerAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mContext = context;
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }

    public void setData(List list, FlowerData data) {
        this.flowerData = data;
        listData.clear();
        if (null != list) {
            listData.addAll(list);
        } else {
            if (null == listData || listData.size() == 0) {
                setAutoLoadMoreEnable(false);
            }

        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int count = listData.size();
        if (mIsFooterEnable) count++;
        if (mIsHeaderEnable) count++;
        if (isMe) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (isMe) {
            if (position == 1) {
                if (getItemCount() == 2) {
                    return TYPE_HEADER_TWO_FULL;
                }
                return TYPE_HEADER_TWO;
            }

            if (position - 2 == 0) {
                return TYPE_MARGIN;
            }
        } else {
            if (position == 1 || position == 2) {
                return TYPE_MARGIN;
            }
        }

        return super.getItemViewType(position);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {

        if (viewType == TYPE_HEADER_TWO) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.head_follow, parent, false);
            v.setOnClickListener(this);
            return new HeaderTwoViewHolder(v);
        } else if (viewType == TYPE_HEADER_TWO_FULL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.header_follow_full, parent, false);
            v.setOnClickListener(this);
            return new HeaderTwoFullViewHolder(v);
        } else if (viewType == TYPE_MARGIN) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.article_file_margin, parent, false);
            v.setOnClickListener(this);
            return new ViewHolderMargin(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.article_filss, parent, false);
            v.setOnClickListener(this);
            return new ViewHolder(v);
        }

    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, final int position, boolean isItem) {
        try {
            if (holder instanceof ViewHolder || holder instanceof ViewHolderMargin) {
                final ArticleData articleData;
                if (isMe) {
                    articleData = (ArticleData) listData.get(position - 2);
                } else {
                    articleData = (ArticleData) listData.get(position - 1);
                }

                final ArticleData.items item = articleData.getItem();

                if (isMe ? position - 2 == 0 : (position == 2 || position == 1)) {
                    ViewHolderMargin mHolder = (ViewHolderMargin) holder;
                    mHolder.itemView.setTag(position);
                    mHolder.text.setText(item.getTitle());
                    String url = item.getTitleMediaUrl();
                    height = item.getHeight();
                    width = item.getWidth();
                    if (flowerData.getUser() == null) {
                        return;
                    }
                    //获取用户的等级
                    userLevel = flowerData.getUser().getUserLevel();
                    //获取认证点
                    int creditPoint = flowerData.getUser().getZmCreditPoint();
                    if (userLevel > 1 || creditPoint > 0 || flowerData.getUser().hasVerified) {
                        if (userLevel > 1 && userLevel <= 2) {
                            mHolder.ivInfo.setVisibility(View.VISIBLE);
                            mHolder.ivInfo.setBackgroundResource(R.drawable.icon_vip);
                        } else if (userLevel > 2 && userLevel <= 3) {
                            mHolder.ivInfo.setVisibility(View.VISIBLE);
                            mHolder.ivInfo.setBackgroundResource(R.drawable.icon_xing);
                        } else {
                            mHolder.ivInfo.setBackgroundResource(R.drawable.iv_zm);
                            mHolder.ivInfo.setVisibility(View.VISIBLE);
                        }
                    } else {      //不显示控件，
                        mHolder.ivInfo.setVisibility(View.GONE);
                    }

                    float aspectRatio = 1.41f;
                    if (width > 0 && height > 0) {
                        aspectRatio = (float) width / height;
                        if (aspectRatio < 0.75f) {
                            aspectRatio = 0.75f;
                        }
                    }
                    mHolder.ivProPic.setVisibility(View.VISIBLE);
                    mHolder.ivProPic.setAspectRatio(aspectRatio);
                    //@300w_300h_90q.jpg
                    if (url.contains("@!logo")) {
                        url = StringUtils.substringBeforeLast(url, "@!logo");
                    }
                    mHolder.ivProPic.setImageURI(url + MyConstants.OSS_MEDIUM_STYLE);


                    if (articleData.getItemType().equals("article")) {
                        mHolder.articleImage.setBackgroundResource(R.drawable.aritcle);
                        mHolder.articleImage.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.articleImage.setVisibility(View.GONE);
                    }
                    mHolder.ivHeader.setImageURI(flowerData.getUser().getAvatarUrl());
                    mHolder.ctvName.setText(flowerData.getUser().getUserName());
                    if (!TextUtils.isEmpty(item.getSummary())) {
                        mHolder.tvBody.setVisibility(View.VISIBLE);
                        mHolder.tvBody.setText(item.getSummary());
                    } else {
                        mHolder.tvBody.setVisibility(View.GONE);
                        mHolder.tvBody.setText(item.getSummary());
                    }

                    mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (articleData.getItemType().equals("moment")) {
                                LogUtil.e("flowersbyGuan", "MomentDetailActivity");
                                Intent intent = new Intent(mContext, MomentDetailActivity.class);
                                intent.putExtra(MomentDetailActivity.MOMENT_ID, item.getArticleId());
                                ((Activity) mContext).startActivityForResult(intent, REQUEST_FOR_ARTICLE_DETAIL);
                            } else if (articleData.getItemType().equals("article")) {
                                Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                                intent.putExtra(MyConstants.ARTICLE_ID, item.getArticleId());
                                ((Activity) mContext).startActivityForResult(intent, REQUEST_FOR_ARTICLE_DETAIL);
                            }

                        }
                    });

                    mHolder.llBottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity) mContext).finish();
                        }
                    });

                    mHolder.layoutLike.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startForRequestWantBuy(position, articleData);
                        }
                    });

                    if (item.isCollected()) {
                        mHolder.ivLike.setImageResource(R.drawable.btn_item_like_select);
                        mHolder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
                    } else {
                        mHolder.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                        mHolder.tvLike.setTextColor(Color.parseColor("#888888"));
                    }
                    if (articleData.getCounts().getCollection() == null) {
                        mHolder.tvLike.setText("0");
                    } else {
                        mHolder.tvLike.setText(articleData.getCounts().getCollection());
                    }

                } else {
                    final ViewHolder mHolder = (ViewHolder) holder;
                    mHolder.itemView.setTag(position);
                    mHolder.text.setText(item.getTitle());
                    String url = item.getTitleMediaUrl();
                    height = item.getHeight();
                    width = item.getWidth();
                    if (flowerData.getUser() == null) {
                        return;
                    }
                    //获取用户的等级
                    userLevel = flowerData.getUser().getUserLevel();
                    //获取认证点
                    int creditPoint = flowerData.getUser().getZmCreditPoint();
                    if (userLevel > 1 || creditPoint > 0 || flowerData.getUser().hasVerified) {
                        if (userLevel > 1 && userLevel <= 2) {
                            mHolder.ivInfo.setVisibility(View.VISIBLE);
                            mHolder.ivInfo.setBackgroundResource(R.drawable.icon_vip);
                        } else if (userLevel > 2 && userLevel <= 3) {
                            mHolder.ivInfo.setVisibility(View.VISIBLE);
                            mHolder.ivInfo.setBackgroundResource(R.drawable.icon_xing);
                        } else {
                            mHolder.ivInfo.setBackgroundResource(R.drawable.iv_zm);
                            mHolder.ivInfo.setVisibility(View.VISIBLE);
                        }
                    } else {      //不显示控件，
                        mHolder.ivInfo.setVisibility(View.GONE);
                    }

                    float aspectRatio = 1.41f;
                    if (width > 0 && height > 0) {
                        aspectRatio = (float) width / height;
                        if (aspectRatio < 0.75f) {
                            aspectRatio = 0.75f;
                        }
                    }
                    mHolder.ivProPic.setVisibility(View.VISIBLE);
                    mHolder.ivProPic.setAspectRatio(aspectRatio);
                    //@300w_300h_90q.jpg
                    if (url.contains("@!logo")) {
                        url = StringUtils.substringBeforeLast(url, "@!logo");
                    }
                    mHolder.ivProPic.setImageURI(url + MyConstants.OSS_MEDIUM_STYLE);


                    if (articleData.getItemType().equals("article")) {
                        mHolder.articleImage.setBackgroundResource(R.drawable.aritcle);
                        mHolder.articleImage.setVisibility(View.VISIBLE);
                    } else {
                        mHolder.articleImage.setVisibility(View.GONE);
                    }
                    mHolder.ivHeader.setImageURI(flowerData.getUser().getAvatarUrl());
                    mHolder.ctvName.setText(flowerData.getUser().getUserName());
                    if (!TextUtils.isEmpty(item.getSummary())) {
                        mHolder.tvBody.setVisibility(View.VISIBLE);
                        mHolder.tvBody.setText(item.getSummary());
                    } else {
                        mHolder.tvBody.setVisibility(View.GONE);
                        mHolder.tvBody.setText(item.getSummary());
                    }

                    mHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (articleData.getItemType().equals("moment")) {
                                Intent intent = new Intent(mContext, MomentDetailActivity.class);
                                intent.putExtra(MomentDetailActivity.MOMENT_ID, item.getArticleId());
                                ((Activity) mContext).startActivityForResult(intent, REQUEST_FOR_ARTICLE_DETAIL);
                            } else if (articleData.getItemType().equals("article")) {
                                LogUtil.e("flowersbyGuan", "ArticleDetailActivity");
                                Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                                intent.putExtra(MyConstants.ARTICLE_ID, item.getArticleId());
                                ((Activity) mContext).startActivityForResult(intent, REQUEST_FOR_ARTICLE_DETAIL);
                            }

                        }
                    });

                    mHolder.llBottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((Activity) mContext).finish();
                        }
                    });

                    mHolder.layoutLike.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startForRequestWantBuy(position, articleData);
                        }
                    });

                    if (item.isCollected()) {
                        mHolder.ivLike.setImageResource(R.drawable.btn_item_like_select);
                        mHolder.tvLike.setTextColor(Color.parseColor("#ffff6677"));
                    } else {
                        mHolder.ivLike.setImageResource(R.drawable.btn_item_like_normal);
                        mHolder.tvLike.setTextColor(Color.parseColor("#888888"));
                    }
                    if (articleData.getCounts().getCollection() == null) {
                        mHolder.tvLike.setText("0");
                    } else {
                        mHolder.tvLike.setText(articleData.getCounts().getCollection());
                    }
                }

            } else if (holder instanceof HeaderTwoViewHolder) {
                HeaderTwoViewHolder twoViewHolder = (HeaderTwoViewHolder) holder;
                twoViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toPickArticleMode();
                    }
                });
            } else if (holder instanceof HeaderTwoFullViewHolder) {
                HeaderTwoFullViewHolder twoFullViewHolder = (HeaderTwoFullViewHolder) holder;
                twoFullViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toPickArticleMode();
                    }
                });
            }

        } catch (Exception e) {
            Logger.e("error found:" + e.getMessage());
        }
    }

    private void toPickArticleMode() {
        if (ConfigUtil.isToVerify()) {
            DialogManager.toVerify(context);
            return;
        }
        Intent intent = new Intent(context, PickArticleModeActivity.class);
        context.startActivity(intent);
    }

    private void startForRequestWantBuy(final int position, final ArticleData articleData) {
        HashMap<String, String> params = new HashMap<>();
        params.put("targetId", articleData.getItem().getArticleId());
        if (articleData.getItem().isCollected()) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        if (articleData.getItemType().equals("goods")) {
            params.put("targetType", "1");
        } else if (articleData.getItemType().equals("moment")) {
            params.put("targetType", "8");
        } else if (articleData.getItemType().equals("article")) {
            params.put("targetType", "6");
        }
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
                                CollectionData data = JSON.parseObject(baseResult.obj, CollectionData.class);
                                if (articleData.getItem().isCollected()) {
                                    articleData.getItem().setCollected(false);
                                    articleData.getCounts().setCollection(String.valueOf(data.getCollections()));
                                    notifyDataSetChanged();
                                } else {
                                    articleData.getItem().setCollected(true);
                                    articleData.getCounts().setCollection(String.valueOf(data.getCollections()));
                                    notifyDataSetChanged();
                                }
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text, tvBody;
        TextView ctvName;
        SimpleDraweeView ivProPic;
        SimpleDraweeView ivHeader;
        ImageView ivLike;
        TextView tvLike;
        View layoutLike;
        ImageView articleImage;
        ImageView ivInfo;
        View llBottom;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProPic = (SimpleDraweeView) itemView.findViewById(R.id.ivProPic);
            text = (TextView) itemView.findViewById(R.id.tvTitle);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ctvName = (TextView) itemView.findViewById(R.id.ctvName);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            articleImage = (ImageView) itemView.findViewById(R.id.article_image);
            ivInfo = (ImageView) itemView.findViewById(R.id.ivInfo);
            llBottom = itemView.findViewById(R.id.llBottom);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
        }
    }

    static class ViewHolderMargin extends RecyclerView.ViewHolder {
        TextView text, tvBody;
        TextView ctvName;
        SimpleDraweeView ivProPic;
        SimpleDraweeView ivHeader;
        ImageView ivLike;
        TextView tvLike;
        View layoutLike;
        ImageView articleImage;
        ImageView ivInfo;
        View llBottom;
        LinearLayout parentLayout;

        public ViewHolderMargin(View itemView) {
            super(itemView);
            ivProPic = (SimpleDraweeView) itemView.findViewById(R.id.ivProPic);
            text = (TextView) itemView.findViewById(R.id.tvTitle);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            ctvName = (TextView) itemView.findViewById(R.id.ctvName);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            tvLike = (TextView) itemView.findViewById(R.id.tvLike);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            articleImage = (ImageView) itemView.findViewById(R.id.article_image);
            ivInfo = (ImageView) itemView.findViewById(R.id.ivInfo);
            llBottom = itemView.findViewById(R.id.llBottom);
            parentLayout = (LinearLayout) itemView.findViewById(R.id.parentLayout);
        }
    }

    private static class HeaderTwoViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sendFlowerLayout;

        HeaderTwoViewHolder(View itemView) {
            super(itemView);
            sendFlowerLayout = (LinearLayout) itemView.findViewById(R.id.sendFlowerLayout);
            ViewGroup.LayoutParams layoutParams = sendFlowerLayout.getLayoutParams();
            int thirtyDp = 3 * itemView.getContext().getResources().getDimensionPixelSize(R.dimen.flower_space_ten);
            layoutParams.height = (CommonUtils.getScreenWidth() - thirtyDp) / 2 * 360 / 346;
            sendFlowerLayout.setLayoutParams(layoutParams);

        }
    }

    private static class HeaderTwoFullViewHolder extends RecyclerView.ViewHolder {
        LinearLayout sendFlowerLayout;

        HeaderTwoFullViewHolder(View itemView) {
            super(itemView);
            sendFlowerLayout = (LinearLayout) itemView.findViewById(R.id.sendFlowerLayout);
            ViewGroup.LayoutParams layoutParams = sendFlowerLayout.getLayoutParams();
            int twoHundredDp = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.flower_two_hundred);
            layoutParams.height = CommonUtils.getScreenHeight() - twoHundredDp;
            layoutParams.width = CommonUtils.getScreenWidth();
            sendFlowerLayout.setLayoutParams(layoutParams);

        }
    }


}
