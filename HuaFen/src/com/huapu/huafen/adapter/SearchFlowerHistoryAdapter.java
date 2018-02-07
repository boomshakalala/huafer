package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.SearchArticleActivity;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.recycler.base.ViewHolder;

import java.util.LinkedList;

public class SearchFlowerHistoryAdapter extends RecyclerView.Adapter<SearchFlowerHistoryAdapter.SearchViewHolder> {

    private LinkedList<CodeValuePair> mData = new LinkedList<CodeValuePair>();
    private Context mContext;

    public SearchFlowerHistoryAdapter(Context context, LinkedList<CodeValuePair> data) {
        this.mData = data;
        this.mContext = context;
    }

    public void setData(LinkedList<CodeValuePair> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public class SearchViewHolder extends ViewHolder {
        public TextView tvSearch;
//        public ImageView ivIcon;

        public SearchViewHolder(Context context, View itemView) {
            super(context, itemView);
            tvSearch = (TextView) itemView.findViewById(R.id.tvSearch);
//            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);

        }

    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(SearchViewHolder vh, int position) {
        final CodeValuePair item = mData.get(position);
//        vh.ivIcon.setVisibility(item.code == 1 ? View.GONE : View.VISIBLE);
        if (!TextUtils.isEmpty(item.value)) {
            vh.tvSearch.setText(item.value);
        }
        vh.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent;
                if (item.code == 1) {
                    intent = new Intent(mContext, SearchArticleActivity.class);
                    intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, item.value);
                    mContext.startActivity(intent);
                }
            }
        });
    }


    @Override
    public SearchViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        SearchViewHolder vh = new SearchViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.search_history_item, parent, false));

        return vh;
    }
}
