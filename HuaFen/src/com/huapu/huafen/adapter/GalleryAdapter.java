package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.utils.ActionUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 
 * 展示横向图片相当于Gallery
 *
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private ArrayList<CampaignBanner> mData = new ArrayList<>();
    private Context context;


    public GalleryAdapter(Context context) {
    	this.context = context;
    }

    public void setData(ArrayList<CampaignBanner> data){
    	this.mData = data;
    	notifyDataSetChanged();
    }
  
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.ivPic) SimpleDraweeView mImg;
        @BindView(R2.id.rightDivider) View rightDivider;
        @BindView(R2.id.bottomDivider) View bottomDivider;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }  
  
    @Override  
    public int getItemCount() {
        return mData==null?0:mData.size();
    }  
  
    /** 
     * 创建ViewHolder 
     */  
    @Override  
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_gallery_layout, viewGroup, false));
    }  
  
    /** 
     * 设置值 
     */  
    @Override  
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final CampaignBanner item = mData.get(position);
        String url =item.getImage();
        String tag = (String) viewHolder.mImg.getTag();
        if(TextUtils.isEmpty(tag)||!tag.equals(url)){
            viewHolder.mImg.setImageURI(url);
            viewHolder.mImg.setTag(url);
        }

        viewHolder.mImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                ActionUtil.dispatchAction(context,item.getAction(),item.getTarget());
			}
		});

        if(position%2 == 0){
            viewHolder.rightDivider.setVisibility(View.VISIBLE);
        }else{
            viewHolder.rightDivider.setVisibility(View.GONE);
        }

        int count = mData.size();
        int lastRow = count / 2 + count % 2;
        int currentRow = (position + 1) / 2 + (position + 1) % 2;
        if (lastRow == currentRow) {
            viewHolder.bottomDivider.setVisibility(View.GONE);
        } else {
            viewHolder.bottomDivider.setVisibility(View.VISIBLE);
        }
    }

}
