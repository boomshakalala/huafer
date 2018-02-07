package com.huapu.huafen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.NoticeData;
import com.huapu.huafen.beans.NoticeInfo;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

/**
 * Created by qwe on 2017/9/21.
 */

public class NoticeListAdapter extends CommonWrapper<NoticeListAdapter.NoticeListHolder> {


    private ArrayList<NoticeInfo> data;
    private Context context;

    public NoticeListAdapter(Context context) {
        this(context, null);
    }

    public NoticeListAdapter(Context context, ArrayList<NoticeInfo> data) {
        this.data = data;
        this.context = context;
    }


    public void setData(ArrayList<NoticeInfo> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }
    public void addAll(List<NoticeInfo> data){
        if(this.data==null){
            this.data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    @Override
    public NoticeListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NoticeListHolder(LayoutInflater.from(context).inflate(R.layout.item_notice_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(NoticeListHolder holder, int position) {
        try {
            final NoticeData notice = data.get(position).notice;
            String time = DateTimeUtils.getYearMonthDayHourMinute2(notice.timestamp);
            holder.tvNoticeTime.setText(time);
            holder.tvNoticeTitle.setText(notice.title);
            holder.tvNoticeContent.setText(notice.msg );
            holder.llNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionUtil.dispatchAction(mContext, notice.action, notice.target);
                }
            });
            if (notice.image==null||notice.image.isEmpty()) {
                if (holder.ivNotice.getVisibility() != View.GONE) {
                    holder.ivNotice.setVisibility(View.GONE);
                }
            } else {
                holder.ivNotice.setImageURI(notice.image);
                if (holder.ivNotice.getVisibility() != View.VISIBLE) {
                    holder.ivNotice.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            LogUtil.e(e);
//            ToastUtil.toast(context, "通知消息展示失败");
        }
    }


    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class NoticeListHolder extends RecyclerView.ViewHolder {

        RelativeLayout llNotice;
        TextView tvNoticeTime;
        TextView tvNoticeTitle;
        SimpleDraweeView ivNotice;
        TextView tvNoticeContent;

        public NoticeListHolder(View itemView) {
            super(itemView);
            tvNoticeTime = (TextView) itemView.findViewById(R.id.tv_notice_time);
            tvNoticeContent = (TextView) itemView.findViewById(R.id.tv_notice_content);
            tvNoticeTitle = (TextView) itemView.findViewById(R.id.tv_notice_title);
            llNotice = (RelativeLayout) itemView.findViewById(R.id.ll_notice);
            ivNotice = (SimpleDraweeView) itemView.findViewById(R.id.iv_notice);
        }
    }

}
