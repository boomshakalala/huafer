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
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.beans.ArticleData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CollectionData;
import com.huapu.huafen.beans.FlowerData;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xk on 2017/4/25.
 */
public class MyadapterSpa extends CommonWrapper<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private final Context mContext;
    int userLevel;
    private ArrayList<ArticleData> mDatas = new ArrayList<>();
    FlowerData fadatas;
    private View mHeaderView;
    private OnItemClickListener mListener;
    private ArrayList<ArticleData> datas;
    private int height;
    private int width;


    public MyadapterSpa(Context context) {
        this.mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setData(ArrayList<ArticleData> datas, FlowerData data) {
        this.mDatas = datas;
        fadatas = data;
        notifyWrapperDataSetChanged();
    }

    public void addDatas(ArrayList<ArticleData> datas) {
        mDatas.addAll(datas);
        notifyWrapperDataSetChanged();

    }


    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null)
            return TYPE_NORMAL;
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER)
            return new Holder(mHeaderView);
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_filss, parent, false);
        return new Holder(layout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_HEADER)
            return;
        final int pos = getRealPosition(viewHolder);
        final ArticleData data = mDatas.get(pos);
        if (viewHolder instanceof Holder) {
            ((Holder) viewHolder).text.setText(data.getItem().getTitle());
            String url = data.getItem().getTitleMediaUrl();
            height = data.getItem().getHeight();
            width = data.getItem().getWidth();
            if (fadatas.getUser() == null){
                return;
            }
            //获取用户的等级
                userLevel = fadatas.getUser().getUserLevel();
            //获取认证点
            int creditPoint = fadatas.getUser().getZmCreditPoint();
            if (userLevel > 1 || creditPoint > 0) {
                if (userLevel > 1 && userLevel <= 2) {
                    ((Holder) viewHolder).ivInfo.setVisibility(View.VISIBLE);
                    ((Holder) viewHolder).ivInfo.setBackgroundResource(R.drawable.icon_vip);
                } else if (userLevel > 2 && userLevel <= 3) {
                    ((Holder) viewHolder).ivInfo.setVisibility(View.VISIBLE);
                    ((Holder) viewHolder).ivInfo.setBackgroundResource(R.drawable.icon_xing);
                } else {
                    ((Holder) viewHolder).ivInfo.setBackgroundResource(R.drawable.iv_zm);
                    ((Holder) viewHolder).ivInfo.setVisibility(View.VISIBLE);
                }
            } else {      //不显示控件，
                ((Holder) viewHolder).ivInfo.setVisibility(View.GONE);
            }

            float aspectRatio = 1.41f;
            if(width>0 && height > 0){
                aspectRatio = (float) width / height;
                if(aspectRatio < 0.75f){
                    aspectRatio = 0.75f;
                }
            }
            ((Holder) viewHolder).ivProPic.setVisibility(View.VISIBLE);
            ((Holder) viewHolder).ivProPic.setAspectRatio(aspectRatio);
            ((Holder) viewHolder).ivProPic.setImageURI(url);

//            if (height == 0 || width == 0) {
//                ((Holder) viewHolder).ivProPic.setVisibility(View.GONE);
//                return;
//            } else {
//                float min = (float) width / height;
//                if (min > 1.0f) {
//                    ((Holder) viewHolder).ivProPic.setVisibility(View.VISIBLE);
//                    Glide
//                            .with(mContext)
//                            .load(url)
//                            .override(400, 400)
//                            .into(((Holder) viewHolder).ivProPic);
//                } else if (min > 0.75f) {
//                    ((Holder) viewHolder).ivProPic.setVisibility(View.VISIBLE);
//                    Glide
//                            .with(mContext)
//                            .load(url)
//                            .override(width, height)
//                            .into(((Holder) viewHolder).ivProPic);
//                } else if (min == 0.75f) {
//                    ((Holder) viewHolder).ivProPic.setVisibility(View.VISIBLE);
//                    Glide
//                            .with(mContext)
//                            .load(url)
//                            .override(400, 600)
//                            .into(((Holder) viewHolder).ivProPic);
//                } else {
//                    ((Holder) viewHolder).ivProPic.setVisibility(View.VISIBLE);
//                    Glide
//                            .with(mContext)
//                            .load(url)
//                            .override(600, 800)
//                            .into(((Holder) viewHolder).ivProPic);
//                }
//
//            }
            if (data.getItemType().equals("article")) {
                ((Holder) viewHolder).articleImage.setBackgroundResource(R.drawable.aritcle);
                ((Holder) viewHolder).articleImage.setVisibility(View.VISIBLE);
            } else {
                ((Holder) viewHolder).articleImage.setVisibility(View.GONE);
            }
            ((Holder) viewHolder).ivHeader.setImageURI(fadatas.getUser().getAvatarUrl());
            ((Holder) viewHolder).ctvName.setText(fadatas.getUser().getUserName());
            if(!TextUtils.isEmpty(data.getItem().getSummary())){
                ((Holder) viewHolder).tvBody.setVisibility(View.VISIBLE);
                ((Holder) viewHolder).tvBody.setText(data.getItem().getSummary());
            }else{
                ((Holder) viewHolder).tvBody.setVisibility(View.GONE);
                ((Holder) viewHolder).tvBody.setText(data.getItem().getSummary());
            }



            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.getItemType().equals("moment")) {
                        Intent intent = new Intent(mContext, MomentDetailActivity.class);
                        intent.putExtra(MomentDetailActivity.MOMENT_ID, data.getItem().getArticleId());
                        mContext.startActivity(intent);
                    } else if (data.getItemType().equals("article")) {
                        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                        intent.putExtra(MyConstants.ARTICLE_ID, data.getItem().getArticleId());
                        mContext.startActivity(intent);
                    }

                }
            });


            ((Holder) viewHolder).llBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) mContext).finish();
                }
            });


            //喜欢按钮
            ((Holder) viewHolder).layoutLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startForRequestWantBuy(pos);
                }
            });

        }
        if (data.getItem().isCollected()) {
            ((Holder) viewHolder).ivLike.setImageResource(R.drawable.btn_item_like_select);
            ((Holder) viewHolder).tvLike.setTextColor(Color.parseColor("#ffff6677"));
        } else {
            ((Holder) viewHolder).ivLike.setImageResource(R.drawable.btn_item_like_normal);
            ((Holder) viewHolder).tvLike.setTextColor(Color.parseColor("#888888"));
        }
        if (data.getCounts().getCollection() == null) {
            ((Holder) viewHolder).tvLike.setText("0");
        } else {
            ((Holder) viewHolder).tvLike.setText(data.getCounts().getCollection());
        }

    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? mDatas.size() : mDatas.size() + 1;
    }

    class Holder extends RecyclerView.ViewHolder {

        TextView text, tvBody;
        TextView ctvName;
        public SimpleDraweeView ivProPic;
        public SimpleDraweeView ivHeader;
        private ImageView ivLike;
        private TextView tvLike;
        private View layoutLike;
        private ImageView articleImage;
        private ImageView ivInfo;
        View llBottom;

        public Holder(View itemView) {
            super(itemView);
            if (itemView == mHeaderView)
                return;
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

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ArticleData data);
    }

    private void startForRequestWantBuy(final int position) {
        final ArticleData data = mDatas.get(position);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("targetId", data.getItem().getArticleId());
        if (data.getItem().isCollected()) {
            params.put("type", "1");
        } else {
            params.put("type", "0");
        }
        if (data.getItemType().equals("goods")) {
            params.put("targetType", "1");
        } else if (data.getItemType().equals("moment")) {
            params.put("targetType", "8");
        } else if (data.getItemType().equals("article")) {
            params.put("targetType", "6");
        }
        LogUtil.i("liang", "params:" + params.toString());
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
                                final ArticleData Adata = mDatas.get(position);
                                CollectionData data = JSON.parseObject(baseResult.obj, CollectionData.class);
                                    if (Adata.getItem().isCollected()) {
                                        Adata.getItem().setCollected(false);
                                        Adata.getCounts().setCollection(String.valueOf(data.getCollections()));
                                        notifyWrapperDataSetChanged();
                                    } else {
                                        Adata.getItem().setCollected(true);
                                        Adata.getCounts().setCollection(String.valueOf(data.getCollections()));
                                        notifyWrapperDataSetChanged();
                                    }
                            } else {
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });

    }


}