package com.huapu.huafen.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.IconFilter;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by danielluan on 2017/9/25.
 */

public class ThemeIconFilterAdapter extends RecyclerView.Adapter<ThemeIconFilterAdapter.ThemeIconViewHolder> {

    private List<IconFilter> data;
    private Context context;
    private OnItemClickListener listener;

    private boolean smallrefresh;

    public ThemeIconFilterAdapter(Context context) {
        this.context = context;

        data = new ArrayList<IconFilter>();
        IconFilter fi = new IconFilter("www.abd.ccc", "童装");
        IconFilter fi2 = new IconFilter("www.abd.ccc", "男人装");
        IconFilter fi3 = new IconFilter("www.abd.ccc", "女人装");
        IconFilter fi4 = new IconFilter("www.abd.ccc", "帽子");
        IconFilter fi5 = new IconFilter("www.abd.ccc", "裙子");
        data.add(fi);
        data.add(fi2);
        data.add(fi3);
        data.add(fi4);
        data.add(fi5);
        notifyDataSetChanged();

    }

    public void setData(List<IconFilter> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ThemeIconViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ThemeIconViewHolder(LayoutInflater.from(context).inflate(R.layout.item_theme_iconfilter, parent, false));
    }

    @Override
    public void onBindViewHolder(ThemeIconViewHolder holder, final int position) {
        final IconFilter item = data.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (!item.ischeck()) {
                        for (int i = 0; i < data.size(); i++) {
                            IconFilter item = data.get(i);
                            item.setIscheck(false);
                        }
                        item.setIscheck(true);
                        smallrefresh = true;
                        notifyDataSetChanged();
                        listener.onItemClick(item);
                    }
                }
            }
        });

        if (!smallrefresh) {
            String avatar = item.getImageUrl();
            holder.filteravatar.setImageURI(avatar);
        }

        if (item.ischeck()) {
            holder.filtername.setTextColor(Color.parseColor("#FF6677"));
        } else {
            holder.filtername.setTextColor(Color.parseColor("#8A000000"));
        }
        holder.filtername.setText(item.getNameDisplay());
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(IconFilter fi);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class ThemeIconViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.filteravatar)
        SimpleDraweeView filteravatar;
        @BindView(R.id.filtername)
        TextView filtername;

        public ThemeIconViewHolder(View itemView) {
            super(itemView);
            if (data != null && data.size() >= 4) {

                int width = CommonUtils.getScreenWidth() / 4;
                itemView.getLayoutParams().width = width;
                itemView.getLayoutParams().height = width;
            }
            ButterKnife.bind(this, itemView);
        }
    }
}
