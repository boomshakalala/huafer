package com.huapu.huafen.chatim;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.huapu.huafen.utils.LogUtil;

import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;

/**
 * IMUtils
 * Created by wangchen on 30/10/2017.
 */

public class IMUtils {
    public static final String TAG = "IMUtils";

    /**
     * 检查是否有未读消息
     * <p>
     * 这是标准逻辑，发现未读就可以 break 出去，但因为订单消息的存在只能使用 hasUnread 中的逻辑，
     * 每次必须完整遍历所有 conversations，以便将订单未读消息数保存到本地，并将其标记为已读。
     */
    public static boolean hasUnread() {
        boolean yes = false;
        try {
            List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();
            LogUtil.i(TAG, String.format("hasUnread, %s, %s", convIdList.size(), convIdList));
            for (String convId : convIdList) {
                AVIMConversation conv = LCChatKit.getInstance().getClient().getConversation(convId);
                if (conv.getUnreadMessagesCount() > 0) {
                    yes = true;
                    break;
                }
                if (conv.getUnreadMessagesCount() == 0) {
                    break;
                }
            }
        } catch (Exception e) {
            LogUtil.d("danielluan", e.getMessage());
        }
        return yes;
    }

    public static boolean isSystem(AVIMConversation conv) {
        Object sys = conv.get("sys");
        return sys != null && (Boolean) sys;

    }

    public static long parsePeerId(String str, long def) {
        long ret = def;
        try {
            ret = Long.parseLong(str);
        } catch (NumberFormatException e) {
            // ignored
        }
        return ret;
    }
}
