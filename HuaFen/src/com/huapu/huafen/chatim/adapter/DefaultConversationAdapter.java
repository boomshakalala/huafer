package com.huapu.huafen.chatim.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.chatim.holder.viewholder.LCIMChatHolderOption;
import com.huapu.huafen.chatim.holder.viewholder.LCIMCampaignItemHolder;
import com.huapu.huafen.chatim.holder.viewholder.LCIMChatItemHolder;
import com.huapu.huafen.chatim.holder.viewholder.LCIMCommentItemHolder;
import com.huapu.huafen.chatim.holder.viewholder.LCIMCommonViewHolder;
import com.huapu.huafen.chatim.holder.viewholder.LCIMNotificationItemHolder;
import com.huapu.huafen.utils.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by wli on 15/8/13.
 * 聊天的 Adapter，此处还有可优化的地方，稍后考虑一下提取出公共的 adapter
 */
public class DefaultConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "DefaultConversationAdapter";

    private final int type;
    protected List<AVIMMessage> messageList = new ArrayList<AVIMMessage>();

    public DefaultConversationAdapter(int type) {
        super();
        this.type = type;
    }

    public void setMessageList(List<AVIMMessage> messages) {
        messageList.clear();
        if (null != messages) {
            messageList.addAll(messages);
        }
    }

    /**
     * 添加多条消息记录
     *
     * @param messages
     */
    public void addMessageList(List<AVIMMessage> messages) {
        messageList.addAll(messages);
    }

    /**
     * 添加消息记录
     *
     * @param message
     */
    public void addMessage(AVIMMessage message) {
        messageList.addAll(Arrays.asList(message));
    }

    /**
     * 获取第一条消息记录，方便下拉时刷新数据
     *
     * @return
     */
    public AVIMMessage getFirstMessage() {
        if (null != messageList && messageList.size() > 0) {
            return messageList.get(0);
        } else {
            return null;
        }
    }

    public AVIMMessage getLastMessage() {
        if (null != messageList && messageList.size() > 0) {
            return messageList.get(messageList.size() - 1);
        } else {
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder itemHolder = null;
        switch (type) {
            case MyConstants.IM_CONV_TYPE_CAMPAIGN:
                itemHolder = new LCIMCampaignItemHolder(parent.getContext(), parent, R.layout.item_listview_sys_img);
                break;
            case MyConstants.IM_CONV_TYPE_NOTIFICATION:
                itemHolder = new LCIMNotificationItemHolder(parent.getContext(), parent, R.layout.item_notice_msg);
                break;
            case MyConstants.IM_CONV_TYPE_COMMENT:
                itemHolder = new LCIMCommentItemHolder(parent.getContext(), parent, R.layout.item_comment_msg);
                break;
            default:
                // fixme: throw exception
                LogUtil.e(TAG, "无法识别的类型");
        }
        return itemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((LCIMCommonViewHolder) holder).bindData(messageList.get(position));
        if (holder instanceof LCIMChatItemHolder) {
            LCIMChatHolderOption option = new LCIMChatHolderOption();
            ((LCIMChatItemHolder) holder).setHolderOption(option);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}