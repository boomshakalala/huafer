package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.PoiAddress;
import com.huapu.huafen.utils.ArrayUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PoiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> data = new ArrayList<>();
    private final Context context;
    private int HEADER = 0;
    private int ADDRESS = 1;

    public PoiListAdapter(Context context) {
        this.context = context;
    }


    public void setData(ArrayList<Object> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = data.get(position);
        if (obj instanceof PoiAddress) {
            return ADDRESS;
        } else {
            return HEADER;
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.poi_header, viewGroup, false));
        } else {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.side_list, viewGroup, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Object obj = data.get(position);
        if (holder instanceof ViewHolder) {
            final PoiAddress bean = (PoiAddress) obj;
            ViewHolder vh = (ViewHolder) holder;
            vh.itemTitle.setText(bean.title);
            vh.itemText.setText(bean.text);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //需要回调传值
                    Intent intent = new Intent();
                    intent.putExtra(MyConstants.POI_RESULT, bean);
                    ((Activity) context).setResult(Activity.RESULT_OK, intent);
                    ((Activity) context).finish();
                }
            });
        } else {
            final String location = (String) obj;
            HeaderViewHolder vh = (HeaderViewHolder) holder;
            if(!TextUtils.isEmpty(location)){
                vh.tvLocation.setText(location);
            }

            vh.tvAdd.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    PoiAddress bean = new PoiAddress(0,0,location,"","");
                    Intent intent = new Intent();
                    intent.putExtra(MyConstants.POI_RESULT, bean);
                    ((Activity) context).setResult(Activity.RESULT_OK, intent);
                    ((Activity) context).finish();
                }
            });

            if(data.size()>1){
                vh.tvMatchLocation.setVisibility(View.VISIBLE);
            }else {
                vh.tvMatchLocation.setVisibility(View.GONE);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_text)
        TextView itemText;
        @BindView(R.id.item_linear)
        LinearLayout item_linear;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.tvLocation)
        TextView tvLocation;
        @BindView(R2.id.tvAdd)
        TextView tvAdd;
        @BindView(R2.id.tvMatchLocation)
        TextView tvMatchLocation;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(data);
    }
}
