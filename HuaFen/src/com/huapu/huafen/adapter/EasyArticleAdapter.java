package com.huapu.huafen.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.beans.EasyArticleContentBean;

import java.util.List;

/**
 * Created by qwe on 2017/4/28.
 */

public class EasyArticleAdapter extends CommonWrapper<EasyArticleAdapter.EasyArticleViewHolder> {
    private List<EasyArticleContentBean> data;

    public EasyArticleAdapter() {

    }

    @Override
    public EasyArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EasyArticleViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.adapter_item_easy_article, parent, false));
    }


    @Override
    public void onBindViewHolder(EasyArticleViewHolder holder, int position) {
        EasyArticleContentBean item = data.get(position);
        holder.title.setText(item.title);
        holder.content.setText(item.content);

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setData(List<EasyArticleContentBean> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public class EasyArticleViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView content;


        public EasyArticleViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.content = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
