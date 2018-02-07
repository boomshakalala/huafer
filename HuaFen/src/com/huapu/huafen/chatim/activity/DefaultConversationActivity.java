package com.huapu.huafen.chatim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.chatim.fragment.DefaultConversationFragment;
import com.huapu.huafen.utils.LogUtil;

import cn.leancloud.chatkit.LCChatKit;

/**
 * 消息-通知
 */
public class DefaultConversationActivity extends BaseActivity {
    private String TAG = "DefaultConversationActivity";
    protected DefaultConversationFragment conversationFragment;
    private String targetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_conversation);
        initByIntent(getIntent());

    }

    private void initByIntent(Intent intent) {
        // 若当前为未登录状态
        if (null == LCChatKit.getInstance().getClient()) {
            // showToast("please login first!");
            finish();
            return;
        }
        // 若Intent 未提供正确的 type
        if (intent.getExtras() == null || !intent.getExtras().containsKey("type")) {
            // showToast("memberId or conversationId is needed");
            finish();
        }
        // 创建 Fragement 并用 Intent.extras 初始化
        conversationFragment = new DefaultConversationFragment();
        conversationFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_campaign_conversation, conversationFragment)
                .commit();

        int type = getIntent().getExtras().getInt("type");
        String convId = null;
        String title = null;
        switch (type) {
            case MyConstants.IM_CONV_TYPE_CAMPAIGN:
                convId = MyConstants.ACTIVITY_ID;
                title = "活动消息";
                break;
            case MyConstants.IM_CONV_TYPE_NOTIFICATION:
                convId = MyConstants.NOTICE_ID;
                title = "通知";
                break;
            case MyConstants.IM_CONV_TYPE_COMMENT:
                convId = MyConstants.COMMENT_ID;
                title = "留言";
                break;
            default:
                // fixme: throw exception
                LogUtil.e(TAG, "无法识别的类型");
        }
        updateConversation(LCChatKit.getInstance().getClient().getConversation(convId));
        getTitleBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 主动刷新 UI
     */
    protected void updateConversation(AVIMConversation conversation) {
        LogUtil.i(TAG, "updateConversation ", conversation);
        if (null != conversation) {
            conversationFragment.setConversation(conversation);
        }
    }
}