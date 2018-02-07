package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.NoticeInfo;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


public class CampaignMessageAdapter extends CommonWrapper<RecyclerView.ViewHolder> {
    private static final int IMAGE_ITEM = 0;
    private static final int TEXT_ITEM = 1;

    private Context mContext;
    private List<NoticeInfo> mDataList;
    private LayoutInflater mLayoutInflater;

    public CampaignMessageAdapter(Context mContext, List<NoticeInfo> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    public CampaignMessageAdapter(Context mContext) {
        this(mContext, null);
    }

    public void setData(List<NoticeInfo> mDataList) {
        this.mDataList = mDataList;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<NoticeInfo> data) {
        if (this.mDataList == null) {
            this.mDataList = new ArrayList<>();
        }
        this.mDataList.addAll(data);
        notifyWrapperDataSetChanged();
    }

    /**
     * 渲染具体的ViewHolder
     *
     * @param viewGroup ViewHolder的容器
     * @param i         一个标志，我们根据该标志可以实现渲染不同类型的ViewHolder
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        if (i == IMAGE_ITEM) {
            return new ImageItemHolder(mLayoutInflater.inflate(R.layout.item_listview_sys_img, viewGroup, false));
        } else {
            return new TextItemHolder(mLayoutInflater.inflate(R.layout.item_listview_sys_text, viewGroup, false));
        }
    }

    /**
     * 绑定ViewHolder的数据。
     *
     * @param viewHolder
     * @param i          数据源list的下标
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        try {
            final NoticeInfo entity = mDataList.get(i);

            if (null == entity)
                return;

            if (viewHolder instanceof TextItemHolder) {
                bindTextItem(entity, (TextItemHolder) viewHolder);
                ((TextItemHolder) viewHolder).layoutTextItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActionUtil.dispatchAction(mContext, entity.notice.action, entity.notice.target);
                    }
                });
            } else {
                bindImageItem(entity, (ImageItemHolder) viewHolder);
                ((ImageItemHolder) viewHolder).layoutImageItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActionUtil.dispatchAction(mContext, entity.notice.action, entity.notice.target);
                    }
                });
            }
        } catch (Exception e) {
            ToastUtil.toast(mContext, "活动消息展示失败");
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    /**
     * 决定元素的布局使用哪种类型
     *
     * @param position 数据源List的下标
     * @return 一个int型标志，传递给onCreateViewHolder的第二个参数
     */
    @Override
    public int getItemViewType(int position) {
        boolean isImage = TextUtils.isEmpty(mDataList.get(position).notice.image);
        return isImage ? TEXT_ITEM : IMAGE_ITEM;
    }

    void bindImageItem(NoticeInfo entity, ImageItemHolder holder) {
        if (entity.notice.image.isEmpty()) {
            if (holder.ivImg.getVisibility() != View.GONE) {
                holder.ivImg.setVisibility(View.GONE);
            }
        } else {
            ImageLoader.loadImage(holder.ivImg, entity.notice.image);

            if (holder.ivImg.getVisibility() != View.VISIBLE) {
                holder.ivImg.setVisibility(View.VISIBLE);
            }
        }
        String time = DateTimeUtils.getYearMonthDayHourMinute2(entity.notice.timestamp);
//        String date = DateTimeUtils.systemDate(entity.notice.timestamp);
        holder.tvTitle.setText(Html.fromHtml(entity.notice.title));
//        holder.tvSentAt.setText(Html.fromHtml(date));
        holder.tvSentAt.setText(time);
        if (!TextUtils.isEmpty(entity.notice.msg)) {
            holder.tvMsg.setText(Html.fromHtml(entity.notice.msg));
        } else {
            holder.tvMsg.setVisibility(View.GONE);
        }
    }

    void bindTextItem(NoticeInfo entity, TextItemHolder holder) {
        String date = DateTimeUtils.systemDate(entity.notice.timestamp);
        holder.tvTitle.setText(Html.fromHtml(entity.notice.title));
        holder.tvSentAt.setText(Html.fromHtml(date));
        if (!TextUtils.isEmpty(entity.notice.msg)) {
            holder.tvMsg.setText(Html.fromHtml(entity.notice.msg));
        } else {
            holder.tvMsg.setVisibility(View.GONE);
        }
    }


    /**
     * 带图片的item
     */
    public class ImageItemHolder extends RecyclerView.ViewHolder {
        TextView tvSentAt;
        TextView tvTitle;
        TextView tvMsg;
        SimpleDraweeView ivImg;
        View layoutImageItem;

        public ImageItemHolder(View itemView) {
            super(itemView);
            tvSentAt = (TextView) itemView.findViewById(R.id.tvSentAt);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
            ivImg = (SimpleDraweeView) itemView.findViewById(R.id.ivImg);
            layoutImageItem = itemView.findViewById(R.id.layoutImageItem);
            WindowManager wm = ((Activity) mContext).getWindowManager();
            int w = CommonUtils.dp2px(52);
            int width = wm.getDefaultDisplay().getWidth() - w;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ivImg.getLayoutParams();
            lp.height = width / 2;
            ivImg.setLayoutParams(lp);
        }
    }

    /**
     * 只有文字的item
     */
    public class TextItemHolder extends RecyclerView.ViewHolder {
        TextView tvSentAt;
        TextView tvTitle;
        TextView tvMsg;
        View layoutTextItem;

        public TextItemHolder(View itemView) {
            super(itemView);
            tvSentAt = (TextView) itemView.findViewById(R.id.tvSentAt);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
            layoutTextItem = itemView.findViewById(R.id.layoutTextItem);
        }
    }
}