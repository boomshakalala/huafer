package com.huapu.huafen.chatim.holder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserIcon;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import cn.leancloud.chatkit.event.LCIMMessageResendEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/9/17.
 * 聊天 item 的 holder
 */
public class LCIMChatItemHolder extends LCIMCommonViewHolder {

    protected boolean isLeft;

    protected AVIMMessage message;
    protected SimpleDraweeView avatarView;
    protected TextView timeView;
    protected TextView nameView;
    protected LinearLayout conventLayout;
    protected FrameLayout statusLayout;
    protected ProgressBar progressBar;
    protected TextView statusView;
    protected ImageView errorView;

    public LCIMChatItemHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft ? R.layout.lcim_chat_item_left_layout : R.layout.lcim_chat_item_right_layout);
        this.isLeft = isLeft;
        initView();
    }
    
    public void initView() {
        if (isLeft) {
            avatarView = (SimpleDraweeView) itemView.findViewById(R.id.chat_left_iv_avatar);
            timeView = (TextView) itemView.findViewById(R.id.chat_left_tv_time);
            nameView = (TextView) itemView.findViewById(R.id.chat_left_tv_name);
            conventLayout = (LinearLayout) itemView.findViewById(R.id.chat_left_layout_content);
            statusLayout = (FrameLayout) itemView.findViewById(R.id.chat_left_layout_status);
            statusView = (TextView) itemView.findViewById(R.id.chat_left_tv_status);
            progressBar = (ProgressBar) itemView.findViewById(R.id.chat_left_progressbar);
            errorView = (ImageView) itemView.findViewById(R.id.chat_left_tv_error);
        } else {
            avatarView = (SimpleDraweeView) itemView.findViewById(R.id.chat_right_iv_avatar);
            timeView = (TextView) itemView.findViewById(R.id.chat_right_tv_time);
            nameView = (TextView) itemView.findViewById(R.id.chat_right_tv_name);
            conventLayout = (LinearLayout) itemView.findViewById(R.id.chat_right_layout_content);
            statusLayout = (FrameLayout) itemView.findViewById(R.id.chat_right_layout_status);
            progressBar = (ProgressBar) itemView.findViewById(R.id.chat_right_progressbar);
            errorView = (ImageView) itemView.findViewById(R.id.chat_right_tv_error);
            statusView = (TextView) itemView.findViewById(R.id.chat_right_tv_status);
        }

        // setAvatarClickEvent();
        setResendClickEvent();
    }

    @Override
    public void bindData(Object o) {
        message = (AVIMMessage) o;
        timeView.setText(DateTimeUtils.getMonthDayHourMinute2(message.getTimestamp()));
        nameView.setText("");
        long userId = 0;
        try {
            if (!isLeft) {
                userId = CommonPreference.getUserId();
                String localUserIconUrl = CommonPreference.getUserInfo().getUserIcon();
                ViewUtil.setAvatar(avatarView, localUserIconUrl);
            } else {
                userId = Long.parseLong(message.getFrom());
                HashMap<String, String> params = new HashMap<>();
                params.put("userId", message.getFrom());
                OkHttpClientManager.postAsyn(MyConstants.GETUSERICON, params, new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {
                        ViewUtil.setAvatar(avatarView, MyConstants.RES + R.drawable.lcim_default_avatar_icon);
                    }

                    @Override
                    public void onResponse(String response) {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            UserIcon info = JSON.parseObject(baseResult.obj, UserIcon.class);
                            String url = info.userInfo.getUserIcon();
                            ViewUtil.setAvatar(avatarView, url);
                        } else {
                            ViewUtil.setAvatar(avatarView, MyConstants.RES + R.drawable.lcim_default_avatar_icon);
                        }
                    }
                });
            }
        } catch (Exception e) {
            ViewUtil.setAvatar(avatarView, MyConstants.RES + R.drawable.lcim_default_avatar_icon);
        }
        final long finalUserId = userId;
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, finalUserId);
                getContext().startActivity(intent);
            }
        });
//    LCIMProfileCache.getInstance().getCachedUser(message.getFrom(), new AVCallback<LCChatKitUser>() {
//      @Override
//      protected void internalDone0(LCChatKitUser userProfile, AVException e) {
//        if (null != e) {
//          LCIMLogUtils.logException(e);
//        } else if (null != userProfile) {
//          nameView.setText(userProfile.getUserName());
//        }
//      }
//    });

        switch (message.getMessageStatus()) {
            case AVIMMessageStatusFailed:
                statusLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                break;
            case AVIMMessageStatusSent:
                // 发送成功不显示发送状态
                statusLayout.setVisibility(View.GONE);

                progressBar.setVisibility(View.GONE);
                statusView.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case AVIMMessageStatusSending:
                statusLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                statusView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                break;
            case AVIMMessageStatusNone:
                break;
            case AVIMMessageStatusReceipt:
                statusLayout.setVisibility(View.GONE);
                break;
        }
    }

    public void setHolderOption(LCIMChatHolderOption option) {
//    if (null != option && !isLeft &&
//      (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusSent == message.getMessageStatus() ||
//      AVIMMessage.AVIMMessageStatus.AVIMMessageStatusReceipt == message.getMessageStatus())) {
        if (option != null) {
            timeView.setVisibility(option.isShowTime() ? View.VISIBLE : View.GONE);
            nameView.setVisibility(option.isShowName() ? View.VISIBLE : View.GONE);
            statusView.setVisibility(option.isShowDelivered() || option.isShowRead() ? View.VISIBLE : View.GONE);
            // statusLayout.setVisibility(option.isShowDelivered() || option.isShowRead() ? View.VISIBLE : View.GONE);
            // progressBar.setVisibility(View.GONE);
            // errorView.setVisibility(View.GONE);
//      if (option.isShowRead()) {
//        statusView.setText("已读");
//      } else if (option.isShowDelivered()) {
//        statusView.setText("已收到");
//      }
            LogUtil.e("LCIMChatItemHolderlllll", "jjjjjjj" + option.isShowRead() + "    " + option.isShowDelivered());
        }
    }

    /**
     * 设置发送失败的叹号按钮的事件
     */
    private void setResendClickEvent() {
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LCIMMessageResendEvent event = new LCIMMessageResendEvent();
                event.message = message;
                event.pos = getLayoutPosition();
                EventBus.getDefault().post(event);
            }
        });
    }
}

