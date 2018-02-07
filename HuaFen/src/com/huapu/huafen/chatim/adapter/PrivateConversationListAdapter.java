package com.huapu.huafen.chatim.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserIcon;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.squareup.okhttp.Request;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.leancloud.chatkit.event.LCIMConversationItemLongClickEvent;
import de.greenrobot.event.EventBus;


/**
 * 聊天 私信列表
 */
public class PrivateConversationListAdapter extends RecyclerView.Adapter {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    private List<AVIMConversation> conversationList;
    private Context context;
    private String localUserId;

    public PrivateConversationListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<AVIMConversation> conversationList) {
        this.conversationList = conversationList;
        localUserId = CommonPreference.getUserId() + "";
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChatListHolder(LayoutInflater.from(context)
                .inflate(R.layout.lcim_conversation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final ChatListHolder chatListHolder = (ChatListHolder) holder;
            final AVIMConversation conversation = conversationList.get(position);
            if (conversation == null) {
                return;
            }
            if (conversation.getCreatedAt() == null) {
                conversation.fetchInfoInBackground(new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        if (e != null) {
                            LogUtil.e(e);
                        } else {
                            chatListHolder.updateIconName(conversation);
                        }
                    }
                });
            } else {
                chatListHolder.updateIconName(conversation);
            }

            final AVIMMessage lastMessage = conversation.getLastMessage();
            if (lastMessage != null) {
                // 设置最后一条消息内容
                chatListHolder.messageView.setText(getMessageeShorthand(lastMessage));
                // 设置最后一条消息时间，大于今日凌晨 0 点，显示时间，否则显示日期
                chatListHolder.timeView.setText(
                        lastMessage.getTimestamp() >
                                DateTimeUtils.getMidnightInMillis(System.currentTimeMillis())
                                ? DateTimeUtils.getHourMinute(lastMessage.getTimestamp())
                                : DATE_FORMAT.format(lastMessage.getTimestamp()));
            } else {
                chatListHolder.messageView.setText("");
                chatListHolder.timeView.setText("");

            }

            // 设置未读数量
            chatListHolder.unreadView.setText(String.valueOf(conversation.getUnreadMessagesCount()));
            chatListHolder.unreadView.setVisibility(
                    conversation.getUnreadMessagesCount() > 0 ? View.VISIBLE : View.INVISIBLE);
            chatListHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 开启私信会话
                    Intent intent = new Intent(context, PrivateConversationActivity.class);
                    intent.putExtra(MyConstants.IM_CONV_ID, conversation.getConversationId());
                    context.startActivity(intent);
                }
            });
            chatListHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final TextDialog dialog = new TextDialog(context, false);
                    dialog.setContentText("您确定要删除会话吗？");
                    dialog.setLeftText("取消");
                    dialog.setColor(Color.parseColor("#2d8bff"));
                    dialog.setLeftCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();
                        }
                    });
                    dialog.setRightText("确定");
                    dialog.setRightCall(new DialogCallback() {

                        @Override
                        public void Click() {
                            dialog.dismiss();
                            EventBus.getDefault().post(new LCIMConversationItemLongClickEvent(conversation));
                        }
                    });
                    dialog.show();
                    return false;
                }
            });
        } catch (Exception e) {
            LogUtil.e(e);
        }
    }

    @Override
    public int getItemCount() {
        return conversationList == null ? 0 : conversationList.size();
    }

    public class ChatListHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView avatarView;
        TextView unreadView;
        TextView messageView;
        TextView timeView;
        TextView nameView;
        RelativeLayout avatarLayout;
        LinearLayout contentLayout;

        public ChatListHolder(View itemView) {
            super(itemView);
            avatarView = (SimpleDraweeView) itemView.findViewById(R.id.conversation_item_iv_avatar);
            nameView = (TextView) itemView.findViewById(R.id.conversation_item_tv_name);
            timeView = (TextView) itemView.findViewById(R.id.conversation_item_tv_time);
            unreadView = (TextView) itemView.findViewById(R.id.conversation_item_tv_unread);
            messageView = (TextView) itemView.findViewById(R.id.conversation_item_tv_message);
            avatarLayout = (RelativeLayout) itemView.findViewById(R.id.conversation_item_layout_avatar);
            contentLayout = (LinearLayout) itemView.findViewById(R.id.conversation_item_layout_content);
        }

        public void updateIconName(AVIMConversation conversation) {
            if (null == conversation) {
                return;
            }

            HashMap<String, String> params = new HashMap<>();
            String peerId = conversation.getMembers().get(1);
            if (peerId.equals(localUserId)) {
                peerId = conversation.getMembers().get(0);
            }

            nameView.setText("...");
            ViewUtil.setAvatar(avatarView, null);

            params.put("userId", peerId);
            OkHttpClientManager.postAsyn(MyConstants.GETUSERICON, params, new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    LogUtil.e(e);
                }

                @Override
                public void onResponse(String response) {
                    LogUtil.e("用户", response);
                    try {
                        BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                        if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                            UserIcon info = JSON.parseObject(baseResult.obj, UserIcon.class);
                            // userName = info.userInfo.getUserName();

                            nameView.setText(info.userInfo.getUserName());
                            ViewUtil.setAvatar(avatarView, info.userInfo.getUserIcon());
                        } else {
                            // avatarView.setImageURI("");
                        }
                    } catch (Exception e) {
                        LogUtil.e(e);
                    }
                }
            });
        }
    }

    private CharSequence getMessageeShorthand(AVIMMessage message) {
        if (message instanceof AVIMTypedMessage) {
            AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(
                    ((AVIMTypedMessage) message).getMessageType());
            switch (type) {
                case TextMessageType:
                    return ((AVIMTextMessage) message).getText();
                case ImageMessageType:
                    return "[图片]";
                case LocationMessageType:
                    return "[位置]";
                case AudioMessageType:
                    return "[语音]";
                default:
                    return "[未知]";
            }
        } else {
//            return message.getContent();
            return "";
        }

    }
}