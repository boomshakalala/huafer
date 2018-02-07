package com.huapu.huafen.chatim.handler;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.activity.HPReplyListActivityNew;
import com.huapu.huafen.activity.OrderDetailActivity;
import com.huapu.huafen.beans.NoticeData;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.chatim.activity.PrivateConversationActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.LogUtil;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.cache.LCIMProfileCache;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.utils.LCIMLogUtils;
import cn.leancloud.chatkit.utils.LCIMNotificationUtils;
import de.greenrobot.event.EventBus;

import static com.avos.avoscloud.im.v2.AVIMMessageType.IMAGE_MESSAGE_TYPE;
import static com.avos.avoscloud.im.v2.AVIMMessageType.TEXT_MESSAGE_TYPE;

/**
 * Created by zhangxiaobo on 15/4/20.
 * AVIMTypedMessage 的 handler，socket 过来的 AVIMTypedMessage 都会通过此 handler 与应用交互
 * 需要应用主动调用 AVIMMessageManager.registerMessageHandler 来注册
 * 当然，自定义的消息也可以通过这种方式来处理
 */
public class LCIMMessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {

    private static final String TAG = "LCIMMessageHandler";
    private Context context;

    public LCIMMessageHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        LogUtil.i(TAG, "onMessage ", conversation.getConversationId());
        if (message == null || message.getMessageId() == null) {
            LCIMLogUtils.d("may be SDK Bug, message or message id is null");
            return;
        }

        if (LCChatKit.getInstance().getCurrentUserId() == null) {
            LCIMLogUtils.d("selfId is null, please call LCChatKit.open!");
            client.close(null);
        } else {
            if (!client.getClientId().equals(LCChatKit.getInstance().getCurrentUserId())) {
                client.close(null);
            } else {
                if (LCIMNotificationUtils.isShowNotification(conversation.getConversationId())) {
                    sendNotification(message, conversation);
                }
                LCIMConversationItemCache.getInstance().insertConversation(message.getConversationId());
                if (!message.getFrom().equals(client.getClientId())) {
                    sendEvent(message, conversation);
                }
            }
        }
    }

    @Override
    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessageReceipt(message, conversation, client);
    }

    /**
     * 发送消息到来的通知事件
     */
    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        LCIMIMTypeMessageEvent event = new LCIMIMTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

    private void sendNotification(final AVIMTypedMessage message, final AVIMConversation conversation) {
        if (MyConstants.ONSTAGE || conversation == null || message == null) {
            return;
        }
        AVIMTextMessage txtMsg = null;
        final Intent intent;
        String title = "您有新的消息";
        final String content;
        switch (message.getMessageType()) {
            case TEXT_MESSAGE_TYPE:
                txtMsg = (AVIMTextMessage) message;
                content = txtMsg.getText();
                break;
            case IMAGE_MESSAGE_TYPE:
                content = "[图片]";
                break;
            default:
                content = "";
        }
        final String convId = conversation.getConversationId();
        if (convId.equals(MyConstants.CONV_ORDER_ID)) { // 订单
            LogUtil.i(TAG, "order");
            if (txtMsg != null) {
                intent = new Intent(context, OrderDetailActivity.class);
                long orderId = (Integer) txtMsg.getAttrs().get("orderId");
                intent.putExtra(MyConstants.EXTRA_ORDER_DETAIL_ID, orderId);
                intent.putExtra(MyConstants.IM_MARK_AS_READ, conversation.getConversationId());
            } else {
                intent = null;
            }
        } else if (convId.equals(MyConstants.ACTIVITY_ID)
                || convId.equals(MyConstants.NOTICE_ID)) { // 活动通知 & 通知
            LogUtil.i(TAG, "campaign or notification ");
            if (txtMsg != null) {
                JSONObject noticeJsonObj = (JSONObject) txtMsg.getAttrs().get("notice");
                NoticeData notice = JSON.toJavaObject(noticeJsonObj, NoticeData.class);
                LogUtil.i(TAG, String.format("action %s %s", notice.action, notice.target));
                intent = ActionUtil.dispatchPushAction(context,notice.action, notice.target);
                if (intent != null) {
                    intent.putExtra(MyConstants.IM_MARK_AS_READ, conversation.getConversationId());
                }
            } else {
                intent = null;
            }
        } else if (convId.equals(MyConstants.COMMENT_ID)) { // 留言
            LogUtil.i(TAG, "comment");
            if (txtMsg != null) {
                intent = new Intent(MyApplication.getApplication(), HPReplyListActivityNew.class);
                long target = (Integer) txtMsg.getAttrs().get("target");
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, target);
                intent.putExtra(MyConstants.IM_MARK_AS_READ, conversation.getConversationId());
            } else {
                intent = null;
            }
        } else {
            LogUtil.i(TAG, "private message");
            intent = new Intent(MyApplication.getApplication(), PrivateConversationActivity.class);
            intent.putExtra(MyConstants.IM_CONV_ID, conversation.getConversationId());
        }

        if (intent == null) {
            // 某个环节出问题，未正常构造 Intent
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (IMUtils.isSystem(conversation)) {
            LCIMNotificationUtils.showNotification(context, title, content, null, intent);
        } else {
            long peerId = IMUtils.parsePeerId(conversation.getLastMessage().getFrom(), -1);
            if (peerId > 0) {
                LCIMProfileCache.getInstance().getCachedUser(message.getFrom(), new AVCallback<LCChatKitUser>() {
                    @Override
                    protected void internalDone0(LCChatKitUser userProfile, AVException e) {
                        if (null != e) {
                            LCIMLogUtils.logException(e);
                        } else if (null != userProfile) {
                            LCIMNotificationUtils.showNotification(context, userProfile.getUserName(), content, null, intent);
                        }
                    }
                });
            }
        }
    }
}
