package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.huapu.huafen.R;

/**
 * Created by xk on 17/4/25.
 */

public class ArticleFilssAdapter extends CommonWrapper {

    private final Context mContext;

    public ArticleFilssAdapter(Context mContext){
        super();
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*return new ArticleFilssAdapter(LayoutInflater.from(mContext).inflate(R.layout.article_filss,
                parent, false));*/
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
