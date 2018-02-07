package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.huapu.huafen.views.TagsContainer;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/2.
 */

public class ArticlePreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final static String TAG = ArticlePreviewAdapter.class.getSimpleName();
    private Context context ;
    private List<Object> data ;


    public ArticlePreviewAdapter(Context context) {
        this.context  = context ;
    }

    public void setData(ArticleDetailResult result){
        if(result!=null && result.obj!=null || result.obj.article!=null){
            data = new ArrayList<>();
            data.add(result);
            if(!ArrayUtil.isEmpty(result.obj.article.sections)){
                data.addAll(result.obj.article.sections);
            }
            notifyDataSetChanged();
        }
    }


    private enum ItemType{
        HEADER,
        ITEM,
        ;
    }


    @Override
    public int getItemViewType(int position) {

        Object item = data.get(position);

        if(item instanceof ArticleDetailResult){
            return ItemType.HEADER.ordinal();
        }else{
            return ItemType.ITEM.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ItemType.HEADER.ordinal()){
            return new ArticleDetailHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.article_detail_header,parent,false));
        }else{
            return new ArticleDetailItemViewHolder(LayoutInflater.from(context).inflate(R.layout.article_detail_item,parent,false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Object item = data.get(position);
        int itemType = getItemViewType(position);

        if(itemType == ItemType.HEADER.ordinal()){
            ArticleDetailHeaderViewHolder viewHolder = (ArticleDetailHeaderViewHolder) holder;
            final ArticleDetailResult result = (ArticleDetailResult) item;

            if(result!=null&&result.obj!=null){

                if(result.obj.article!=null){
                    viewHolder.tagsContainer.setData(result.obj.article.titleMedia);
                    if(!TextUtils.isEmpty(result.obj.article.title)){
                        viewHolder.tvTitle.setText(result.obj.article.title);
                    }
                }

                final UserInfo user = CommonPreference.getUserInfo();
                if(user!=null){
                    viewHolder.avatar.setImageURI(user.getUserIcon());
                    viewHolder.ctvName.setData(user);
                    viewHolder.followImage.setVisibility(View.GONE);
                }

                FloridData article = result.obj.article;

                if(article != null){
                    long time = System.currentTimeMillis();
                    String date = DateTimeUtils.getDateStr(time, "yyyy-MM-dd");
                    viewHolder.tvTime.setText(date);
                }

                viewHolder.tvWatch.setText("0");
                viewHolder.tvMessageCount.setText("0");
            }
        }else{
            ArticleDetailItemViewHolder viewHolder = (ArticleDetailItemViewHolder) holder;
            FloridData.Section section = (FloridData.Section) item;

            viewHolder.tagsContainer.setData(section.media);

            if(!TextUtils.isEmpty(section.content)){
                viewHolder.tvContent.setText(section.content);
            }
        }
    }


    @Override
    public int getItemCount() {
        return data ==null?0:data.size();
    }


    public class ArticleDetailItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.tagsContainer)
        TagsContainer tagsContainer;
        @BindView(R2.id.tvContent)
        TextView tvContent;


        public ArticleDetailItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tagsContainer.setContainerType(TagsContainer.ContainerType.DETAIL);
        }
    }

    public class ArticleDetailHeaderViewHolder extends RecyclerView.ViewHolder {


        @BindView(R2.id.tagsContainer) TagsContainer tagsContainer;
        @BindView(R2.id.avatar) SimpleDraweeView avatar;
        @BindView(R2.id.ctvName) CommonTitleView ctvName;
        @BindView(R2.id.tvTime) TextView tvTime;
        @BindView(R2.id.tvWatch) TextView tvWatch;
        @BindView(R2.id.tvMessageCount) TextView tvMessageCount;
        @BindView(R2.id.followImage) FollowImageView followImage;
        @BindView(R2.id.tvTitle) TextView tvTitle;

        public ArticleDetailHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tagsContainer.setContainerType(TagsContainer.ContainerType.DETAIL);
        }
    }


}
