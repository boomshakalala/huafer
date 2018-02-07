package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.SearchGoodsListActivity;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.recycler.base.ViewHolder;
import com.huapu.huafen.utils.SearchHistoryHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qwe on 2017/7/20.
 */

public class GoodsSearchResultAdapter extends CommonWrapper<GoodsSearchResultAdapter.GoodsSearchViewHolder> {

    private List<String> mData = new ArrayList<>();
    private Context activity;

    public GoodsSearchResultAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setData(List<String> data) {
        this.mData = data;
        notifyWrapperDataSetChanged();
    }


    @Override
    public GoodsSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodsSearchViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.search_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GoodsSearchViewHolder holder, int position) {
        final String item = mData.get(position);
        holder.tvSearch.setText(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistoryHelper.put(new CodeValuePair(1, item));
                Intent intent = new Intent(activity, SearchGoodsListActivity.class);
                intent.putExtra(MyConstants.EXTRA_SEARCH_KEYWORD, item);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class GoodsSearchViewHolder extends ViewHolder {
        TextView tvSearch;

        GoodsSearchViewHolder(Context context, View itemView) {
            super(context, itemView);
            tvSearch = (TextView) itemView.findViewById(R.id.tvSearch);
        }

    }
}
