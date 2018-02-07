package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/6/8.
 */

public class HorizontalArticleAdapter extends RecyclerView.Adapter<HorizontalArticleAdapter.HorizontalArticleViewHolder> {


    private final long userId;
    private Context context;
    private List<Item> data;

    public HorizontalArticleAdapter(Context context,long userId) {
        this.context = context;
        this.userId = userId;
    }

    public void setData(List<Item> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public HorizontalArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HorizontalArticleViewHolder viewHolder = new HorizontalArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.article_horizontal_item, parent, false));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
        if (data.size() > 1) {
            params.width = (int) ((CommonUtils.getScreenWidth() - 2 * CommonUtils.dp2px(16f)) / 1.5);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(HorizontalArticleViewHolder holder, int position) {
        final Item item = data.get(position);
        if(item!=null && item.item!=null){
            holder.article.setImageURI(item.item.titleMediaUrl);
            if(!TextUtils.isEmpty(item.item.title)){
                holder.tvArticleTitle.setText(item.item.title);
            }

            if(item.counts!=null){
                long myUserId = CommonPreference.getUserId() ;

                if(myUserId>0 && userId>0 && myUserId == userId){
                    holder.tvWatch.setVisibility(View.VISIBLE);
                    if(!TextUtils.isEmpty(item.counts.pv)){
                        holder.tvWatch.setText(item.counts.pv);
                    }else{
                        holder.tvWatch.setText("0");
                    }
                }else{
                    holder.tvWatch.setVisibility(View.GONE);
                }

                if(!TextUtils.isEmpty(item.counts.like)){
                    holder.tvLikeCount.setText(item.counts.like);
                }else{
                    holder.tvLikeCount.setText("0");
                }
            }

            final String type = item.itemType;
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if("moment".equals(type)){
                        Intent intent = new Intent(context, MomentDetailActivity.class);
                        intent.putExtra(MomentDetailActivity.MOMENT_ID,String.valueOf(item.item.articleId));
                        context.startActivity(intent);
                    }else if("article".equals(type)){
                        Intent intent = new Intent(context, ArticleDetailActivity.class);
                        intent.putExtra(MyConstants.ARTICLE_ID,String.valueOf(item.item.articleId));
                        context.startActivity(intent);
                    }
                }
            });

            if("moment".equals(type)){
                holder.articleFlag.setVisibility(View.GONE);
            }else if("article".equals(type)){
                holder.articleFlag.setVisibility(View.VISIBLE);
            }
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
        if(data.size()>1){
            if(position == 0){
                params.leftMargin = CommonUtils.dp2px(16f);
                params.rightMargin = CommonUtils.dp2px(5f);
            }else if(position ==data.size()-1){
                params.leftMargin = 0;
                params.rightMargin = CommonUtils.dp2px(16f);
            }else{
                params.leftMargin = 0;
                params.rightMargin = CommonUtils.dp2px(5f);
            }
        }else{
            params.leftMargin = CommonUtils.dp2px(16f);
            params.rightMargin = CommonUtils.dp2px(16f);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class HorizontalArticleViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.article) SimpleDraweeView article;
        @BindView(R2.id.tvArticleTitle) TextView tvArticleTitle;
        @BindView(R2.id.tvWatch) TextView tvWatch;
        @BindView(R2.id.tvLikeCount) TextView tvLikeCount;
        @BindView(R2.id.articleFlag) ImageView articleFlag;

        public HorizontalArticleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
