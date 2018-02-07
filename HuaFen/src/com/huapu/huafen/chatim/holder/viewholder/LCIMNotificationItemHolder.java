package com.huapu.huafen.chatim.holder.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.NoticeData;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.DateTimeUtils;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;


/**
 * Created by wli on 15/9/17.
 * 聊天页面中的文本 item 对应的 holder
 */
public class LCIMNotificationItemHolder extends LCIMCommonViewHolder {
    private RelativeLayout llNotice;
    private TextView tvNoticeTime;
    private TextView tvNoticeTitle;
    private SimpleDraweeView ivNotice;
    private TextView tvNoticeContent;

    public LCIMNotificationItemHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        initView();
    }

    @Override
    public void bindData(Object avimMessage) {

        if (!(avimMessage instanceof AVIMTextMessage)) {
            setUnknowMsg("");
            return;
        }

        AVIMTextMessage msg = (AVIMTextMessage) avimMessage;

        String time = DateTimeUtils.getYearMonthDayHourMinute2(msg.getTimestamp());

        JSONObject noticeJsonObj = (JSONObject) msg.getAttrs().get("notice");

        if (noticeJsonObj == null) {
            setUnknowMsg(time);
            return;
        }

        final NoticeData notice = JSON.toJavaObject(noticeJsonObj, NoticeData.class);

        if (notice.image != null && !notice.image.isEmpty()) {
            ivNotice.setImageURI(notice.image);
            if (ivNotice.getVisibility() != View.VISIBLE) {
                ivNotice.setVisibility(View.VISIBLE);
            }
        } else {
            if (ivNotice.getVisibility() != View.GONE) {
                ivNotice.setVisibility(View.GONE);
            }
        }
        tvNoticeTitle.setText(notice.title);
        tvNoticeTime.setText(time);


        if (!TextUtils.isEmpty(msg.getText())) {
            tvNoticeContent.setText(msg.getText());
        } else {
            tvNoticeContent.setVisibility(View.GONE);
        }

        llNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionUtil.dispatchAction(mContext, notice.action, notice.target);
            }
        });
    }

    private void setUnknowMsg(String time) {
        tvNoticeTitle.setText("无法识别的消息");
        tvNoticeContent.setText("");
        tvNoticeTime.setText(time);
    }

    public void initView() {
        tvNoticeTime = (TextView) itemView.findViewById(R.id.tv_notice_time);
        tvNoticeContent = (TextView) itemView.findViewById(R.id.tv_notice_content);
        tvNoticeTitle = (TextView) itemView.findViewById(R.id.tv_notice_title);
        llNotice = (RelativeLayout) itemView.findViewById(R.id.ll_notice);
        ivNotice = (SimpleDraweeView) itemView.findViewById(R.id.iv_notice);
    }
}
