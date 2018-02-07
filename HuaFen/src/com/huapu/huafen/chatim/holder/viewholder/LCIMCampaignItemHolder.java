package com.huapu.huafen.chatim.holder.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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
public class LCIMCampaignItemHolder extends LCIMCommonViewHolder {
    private TextView tvSentAt;
    private TextView tvTitle;
    private TextView tvMsg;
    private SimpleDraweeView ivImg;
    private View layoutImageItem;

    public LCIMCampaignItemHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        initView();
    }

    private void setUnknownText(String time) {
        tvTitle.setText("无法识别的消息");
        tvMsg.setText("");
        tvSentAt.setText(time);
    }

    @Override
    public void bindData(Object avimMessage) {
        if (!(avimMessage instanceof AVIMTextMessage)) {
            setUnknownText("...");
            return;
        }

        AVIMTextMessage msg = (AVIMTextMessage) avimMessage;
        String time = DateTimeUtils.getYearMonthDayHourMinute2(msg.getTimestamp());
        JSONObject noticeJsonObj = (JSONObject) msg.getAttrs().get("notice");
        if (noticeJsonObj == null) {
            setUnknownText(time);
            return;
        }
        final NoticeData notice = JSON.toJavaObject(noticeJsonObj, NoticeData.class);
        if (notice.image != null && !notice.image.isEmpty()) {
//            ImageLoaderRoundManager.getImageLoader().displayImage(notice.image, ivImg, ImageLoaderRoundManager.getImageOptions());
//            ivImg.setImageResource(R.drawable.ad);
            ivImg.setImageURI(notice.image);
            if (ivImg.getVisibility() != View.VISIBLE) {
                ivImg.setVisibility(View.VISIBLE);
            }
        } else {
            if (ivImg.getVisibility() != View.GONE) {
                ivImg.setVisibility(View.GONE);
            }
        }
        tvTitle.setText(notice.title);
        tvSentAt.setText(time);
        if (!TextUtils.isEmpty(msg.getText())) {
            tvMsg.setText(msg.getText());
        } else {
            tvMsg.setVisibility(View.GONE);
        }
        layoutImageItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionUtil.dispatchAction(mContext, notice.action, notice.target);
            }
        });

    }

    public void initView() {
        tvSentAt = (TextView) itemView.findViewById(R.id.tvSentAt);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
        ivImg = (SimpleDraweeView) itemView.findViewById(R.id.ivImg);
        layoutImageItem = itemView.findViewById(R.id.layoutImageItem);
    }
}
