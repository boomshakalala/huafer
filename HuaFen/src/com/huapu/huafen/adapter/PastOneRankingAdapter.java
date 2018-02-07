package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.WebViewActivity;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/20.
 */

public class PastOneRankingAdapter extends RecyclerView.Adapter<PastOneRankingAdapter.PastOneRankingViewHolder> {

    private Context context;
    private List<Map<String,String>> data;
    private String id;

    public PastOneRankingAdapter(Context context , String id) {
        this.context = context;
        this.id = id;
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public PastOneRankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PastOneRankingViewHolder(LayoutInflater.from(context).inflate(R.layout.past_ranking_item,parent,false));
    }

    @Override
    public void onBindViewHolder(PastOneRankingViewHolder holder, int position) {
        final Map<String, String> item = data.get(position);
        Set<Map.Entry<String, String>> entrySet = item.entrySet();
        Iterator<Map.Entry<String, String>> itt = entrySet.iterator();
        String key =null;
        String value = null;

        while (itt.hasNext()){
            Map.Entry<String, String> tmp = itt.next();
            key = tmp.getKey();
            value = tmp.getValue();
        }
        if(!TextUtils.isEmpty(id)){
            if(id.equals(key)){
                holder.ivArrow.setImageResource(R.drawable.past_ranking_select);
            }else{
                holder.ivArrow.setImageResource(R.drawable.past_ranking_normal);
            }

            if(!TextUtils.isEmpty(value)){
                holder.tvRanking.setText(value);
            }
            final String finalKey = key;
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String url = MyConstants.PAST_RANKING + finalKey;
                    Intent intent = new Intent();
                    intent.setClass(context,WebViewActivity.class);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL,url);
                    intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE,"一元专区");
                    context.startActivity(intent);
                    ((Activity)context).finish();
                    ((Activity)context).overridePendingTransition(0,0);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return data == null ?0:data.size();
    }



    public class PastOneRankingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.tvRanking) TextView tvRanking;
        @BindView(R2.id.ivArrow) ImageView ivArrow;

        public PastOneRankingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
