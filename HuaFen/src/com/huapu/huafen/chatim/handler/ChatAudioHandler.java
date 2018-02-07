package com.huapu.huafen.chatim.handler;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;

import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by qwe on 2017/10/17.
 */

public class ChatAudioHandler extends AVIMTypedMessageHandler<AVIMAudioMessage>{

    @Override
    public void onMessage(AVIMAudioMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessage(message, conversation, client);
        LCIMIMTypeMessageEvent lcimimTypeMessageEvent = new LCIMIMTypeMessageEvent();
        lcimimTypeMessageEvent.message = message;
        lcimimTypeMessageEvent.conversation = conversation;
        EventBus.getDefault().post(lcimimTypeMessageEvent);
    }
}
