package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.beans.ArticleData;
import java.util.ArrayList;

/**
 */
public class DongTaiAdapter extends RecyclerView.Adapter<DongTaiAdapter.ViewHolder> {


    private ArrayList<ArticleData> mDatas = new ArrayList<>();

    private Context context;


    public DongTaiAdapter(Context context) {
        this.context = context;
    }


    public void addDatas(ArrayList<ArticleData> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.dongtai_view_item,
                viewGroup, false));
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final ArticleData data = mDatas.get(position);
        if (mDatas == null && mDatas.size() == 0) {
        } else {
            if (mDatas.size() == 1) {
                viewHolder.content1.setVisibility(View.VISIBLE);
                viewHolder.content2.setVisibility(View.GONE);
                if (data.getItemType().equals("article")){
                    viewHolder.article1_image.setVisibility(View.VISIBLE);
                }
                if (data.getCounts().getPv() == null) {
                    viewHolder.content1_eye.setText("0");
                } else {
                    viewHolder.content1_eye.setText(data.getCounts().getPv());
                }
                if (data.getCounts().getCollection() == null) {
                    viewHolder.content1_collect.setText("0");
                } else {
                    viewHolder.content1_collect.setText(data.getCounts().getCollection());
                }
                viewHolder.content1_title.setText(data.getItem().getTitle());
                viewHolder.content1_image.setImageURI(data.getItem().getTitleMediaUrl());
            } else {
                viewHolder.content2.setVisibility(View.VISIBLE);
                viewHolder.content1.setVisibility(View.GONE);
                if (data.getItemType().equals("article")){
                    viewHolder.article2_image.setVisibility(View.VISIBLE);
                }
                if (data.getCounts().getPv() == null) {
                    viewHolder.content2_eye.setText("0");
                } else {
                    viewHolder.content2_eye.setText(data.getCounts().getPv());
                }
                if (data.getCounts().getCollection() == null) {
                    viewHolder.content2_collect.setText("0");
                } else {
                    viewHolder.content2_collect.setText(data.getCounts().getCollection());
                }
                viewHolder.content2_title.setText(data.getItem().getTitle());
                viewHolder.content2_image.setImageURI(data.getItem().getTitleMediaUrl());
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (data.getItemType().equals("moment")) {
                        Intent intent = new Intent(context, MomentDetailActivity.class);
                        intent.putExtra(MomentDetailActivity.MOMENT_ID,data.getItem().getArticleId());
                        context.startActivity(intent);
                    }else if(data.getItemType().equals("article")){
                        Intent intent = new Intent(context, ArticleDetailActivity.class);
                        intent.putExtra(MyConstants.ARTICLE_ID,data.getItem().getArticleId());
                        context.startActivity(intent);
                    }
                }
            });
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout content1, content2;
        SimpleDraweeView content1_image;
        SimpleDraweeView content2_image;
        TextView content1_eye;
        TextView content1_collect;
        TextView content2_eye;
        TextView content2_collect;
        TextView content1_title;
        TextView content2_title;
        ImageView article1_image;
        ImageView article2_image;


        public ViewHolder(View itemView) {
            super(itemView);
            content1 = (RelativeLayout) itemView.findViewById(R.id.content1);
            content2 = (RelativeLayout) itemView.findViewById(R.id.content2);

            content1_image = (SimpleDraweeView) itemView.findViewById(R.id.content1_image);
            content2_image = (SimpleDraweeView) itemView.findViewById(R.id.content2_image);

            article1_image = (ImageView) itemView.findViewById(R.id.article_1);
            article2_image = (ImageView) itemView.findViewById(R.id.article_2);

            content1_eye = (TextView) itemView.findViewById(R.id.shop_eye);
            content1_collect = (TextView) itemView.findViewById(R.id.shop_collect);
            content1_title = (TextView) itemView.findViewById(R.id.title);

            content2_eye = (TextView) itemView.findViewById(R.id.shop_eye_2);
            content2_collect = (TextView) itemView.findViewById(R.id.shop_collect_2);
            content2_title = (TextView) itemView.findViewById(R.id.title_2);

        }
    }
}
