package com.huapu.huafen.chatim.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.Conversation;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.chatim.adapter.PrivateConversationListAdapter;
import com.huapu.huafen.itemdecoration.RecycleViewDivider;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.event.LCIMConversationItemLongClickEvent;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import de.greenrobot.event.EventBus;

/**
 * 私信 聊天
 */
public class PrivateConversationListActivity extends BaseActivity {
    private String TAG = "PrivateConversationListActivity";
    protected RecyclerView recyclerView;
    private PrivateConversationListAdapter privateConversationListAdapter;
    private HLoadingStateView loadingStateView;
    private View placeholder;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        getTitleBar().setTitle("私信");

        recyclerView = (RecyclerView) findViewById(R.id.recy_contact_list);
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        placeholder = findViewById(R.id.placeholder);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL));

        ViewUtil.setOffItemAnimator(recyclerView);

        privateConversationListAdapter = new PrivateConversationListAdapter(this);
        recyclerView.setAdapter(privateConversationListAdapter);

        EventBus.getDefault().register(this);

        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
    }

    public void updateConversationList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();

                LogUtil.i(TAG, String.format("updateConversationList, %s, %s", convIdList.size(), convIdList));
                LogUtil.d("danielluant", String.format("updateConversationList, %s, %s", convIdList.size(), convIdList));

                final List<AVIMConversation> convs = new ArrayList<>();

                for (String convId : convIdList) {
                    LogUtil.d("PrivateConversationListActivity", convId);
                    final AVIMConversation conv = LCChatKit.getInstance().getClient().getConversation(convId);
                    if (!IMUtils.isSystem(conv)) {
                        convs.add(conv);
                    }
                }

                for (int i = 0, k = convs.size(); i < k; i++) {

                    AVIMConversation conI = convs.get(i);
                    if (conI.getLastMessage() == null)
                        continue;

                    for (int j = i + 1; j < convs.size(); j++) {

                        AVIMConversation conJ = convs.get(j);

                        if (conJ.getLastMessage() == null)
                            continue;

                        if (conI.getLastMessage().getTimestamp() < conJ.getLastMessage().getTimestamp()) {
                            conI = convs.get(i);
                            conJ = convs.get(j);

                            convs.remove(i);
                            convs.add(i, conJ);
                            convs.remove(j);
                            convs.add(j, conI);
                        }
                    }
                }

                privateConversationListAdapter.setData(convs);
                loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                placeholder.setVisibility(convs.size() == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateConversationList();
    }

    /**
     * 删除会话列表中的某个 item
     *
     * @param event
     */
    public void onEvent(LCIMConversationItemLongClickEvent event) {
        if (null != event.conversation) {
            String conversationId = event.conversation.getConversationId();
            LCIMConversationItemCache.getInstance().deleteConversation(conversationId);
            updateConversationList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonPreference.isLogin())
            updateConversationList();
        else
            finish();
    }
}