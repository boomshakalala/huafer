package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;

/**
 * 展示横向图片相当于Gallery
 */
public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter<RecyclerViewGalleryAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private GoodsInfo mDatas = new GoodsInfo();
    private Context context;

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public RecyclerViewGalleryAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setData(GoodsInfo mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        SimpleDraweeView mImg;
    }

    @Override
    public int getItemCount() {
        return mDatas.getGoodsImgs().size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_hlistview_goods, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mImg = (SimpleDraweeView) view.findViewById(R.id.ivPic);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        String image = CommonUtils.getOSSStyle(mDatas.getGoodsImgs().get(i), MyConstants.OSS_SMALL_STYLE);
        ImageLoader.resizeMiddle(viewHolder.mImg, image, 1);
        //如果设置了回调，则设置点击事件
//        if (mOnItemClickLitener != null)  
//        {  
//            viewHolder.itemView.setOnClickListener(new OnClickListener()  
//            {  
//                @Override  
//                public void onClick(View v)  
//                {  
//                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);  
//                }  
//            });  
//  
//        }
        viewHolder.mImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GoodsDetailsActivity.class);
                intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, mDatas.getGoodsId() + "");
                context.startActivity(intent);
            }
        });
    }
}
