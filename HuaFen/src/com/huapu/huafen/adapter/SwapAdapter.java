package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.utils.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/4/28.
 */

public class SwapAdapter extends RecyclerView.Adapter<SwapAdapter.SwapViewHolder> {

    private Context context;

    private List<FloridData.Section> data;

    public SwapAdapter(Context context, List<FloridData.Section> data) {
        this.context = context;
        this.data = data;
    }


    public List<FloridData.Section> getData() {
        return data;
    }

    @Override
    public SwapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SwapViewHolder(LayoutInflater.from(context).inflate(R.layout.item_swap, parent, false));
    }

    @Override
    public void onBindViewHolder(SwapViewHolder holder, int position) {
        FloridData.Section item = data.get(position);

        if (item != null) {
            if (item.media != null) {
                if (!TextUtils.isEmpty(item.media.url) && (item.media.url.startsWith(MyConstants.HTTP) || item.media.url.startsWith(MyConstants.HTTPS))) {
                    holder.articleCover.setImageURI(item.media.url);
                } else {
                    holder.articleCover.setImageURI(MyConstants.FILE + item.media.url);
                }
            }

            if (!TextUtils.isEmpty(item.content)) {
                holder.tvContent.setText(item.content);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class SwapViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.articleCover)
        SimpleDraweeView articleCover;
        @BindView(R2.id.tvContent)
        TextView tvContent;


        public SwapViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ImageLoader.loadImage(articleCover, null, R.drawable.default_pic, R.drawable.default_pic);
        }
    }

}
