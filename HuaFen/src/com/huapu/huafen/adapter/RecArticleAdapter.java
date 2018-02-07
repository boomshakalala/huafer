package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.RecArticle;
import com.huapu.huafen.utils.ActionUtil;

import java.util.List;

/**
 * Created by qwe on 2017/10/17.
 */

public class RecArticleAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<RecArticle> recArticleList;
    public void setData(List<RecArticle> recArticleList){
        this.recArticleList=recArticleList;
        notifyDataSetChanged();
    }
    public RecArticleAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecArticleHolder(LayoutInflater.from(context).inflate(R.layout.rec_article_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final RecArticle recArticle = recArticleList.get(position);
        final RecArticleHolder viewHolder = (RecArticleHolder) holder;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(context, ArticleDetailActivity.class);
//                intent.putExtra(MyConstants.ARTICLE_ID, recArticle.target);
//                context.startActivity(intent);
                ActionUtil.dispatchAction(context, recArticle.action, recArticle.target);

            }
        });
        viewHolder.sdRecArticle.setImageURI(recArticle.image);
        viewHolder.tvRecTitle.setText(recArticle.title);
        viewHolder.tvRecContent.setText(recArticle.summery);
    }

    @Override
    public int getItemCount() {
        return recArticleList ==null?0:recArticleList.size();
    }

    public class RecArticleHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdRecArticle;
        TextView tvRecTitle;
        TextView tvRecContent;
        public RecArticleHolder(View itemView) {
            super(itemView);
            sdRecArticle= (SimpleDraweeView) itemView.findViewById(R.id.sd_rec_article);
            tvRecTitle= (TextView) itemView.findViewById(R.id.tv_rec_title);
            tvRecContent= (TextView) itemView.findViewById(R.id.tv_rec_content);
        }
    }
}
