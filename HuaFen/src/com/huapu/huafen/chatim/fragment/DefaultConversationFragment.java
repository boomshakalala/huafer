package com.huapu.huafen.chatim.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.chatim.adapter.DefaultConversationAdapter;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.utils.LCIMAudioHelper;
import cn.leancloud.chatkit.utils.LCIMLogUtils;
import cn.leancloud.chatkit.utils.LCIMNotificationUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
public class DefaultConversationFragment extends Fragment {
    public static final int PAGE_SIZE = 12;

    @BindView(R.id.recyclerSystemMessage)
    PullToRefreshRecyclerView mRecyclerView;
    @BindView(R.id.loadingStateView)
    HLoadingStateView loadingStateView;
    @BindView(R.id.placeholder)
    ImageView placeholder;
    Unbinder unbinder;
    private String TAG = "PrivateConversationFragment";

    protected AVIMConversation imConversation;

    /**
     * recyclerView 对应的 Adapter
     */
    protected DefaultConversationAdapter itemAdapter;

    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.lcim_campaign_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.getRefreshableView().setLayoutManager(layoutManager);

        // 内容为留言的时候不需要
        if (MyConstants.IM_CONV_TYPE_COMMENT == getType()) {
            mRecyclerView.getRefreshableView().setBackgroundResource(R.color.white);
        } else {
            mRecyclerView.getRefreshableView().setBackgroundResource(R.drawable.wallet_recycler_view_fucking_bg);
        }

        mRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        CommonUtils.buildPtr(mRecyclerView);
        ViewUtil.setOffItemAnimator(mRecyclerView.getRefreshableView());

        itemAdapter = getAdapter();
        mRecyclerView.setAdapter(itemAdapter);
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);

        EventBus.getDefault().register(this);
        return view;
    }

    protected DefaultConversationAdapter getAdapter() {
        return new DefaultConversationAdapter(getType());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                AVIMMessage message = itemAdapter.getLastMessage();
                if (null == message) {
                    mRecyclerView.onRefreshComplete();
                } else {
                    queryMessageMore(message);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != imConversation) {
            LCIMNotificationUtils.addTag(imConversation.getConversationId());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LCIMAudioHelper.getInstance().stopPlayer();
        if (null != imConversation) {
            LCIMNotificationUtils.removeTag(imConversation.getConversationId());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    public void setConversation(final AVIMConversation conversation) {
        imConversation = conversation;
        fetchMessages();
        LCIMNotificationUtils.addTag(conversation.getConversationId());
    }

    /**
     * 拉取消息，必须加入 conversation 后才能拉取消息
     */
    private void fetchMessages() {
        imConversation.queryMessages(PAGE_SIZE, new AVIMMessagesQueryCallback() {
            @Override
            public void done(final List<AVIMMessage> messageList, AVIMException e) {
                if (!filterException(e)) {
                    return;
                }

                Activity activity = getActivity();

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                            Collections.reverse(messageList);
                            itemAdapter.setMessageList(messageList);
                            mRecyclerView.setAdapter(itemAdapter);
                            itemAdapter.notifyDataSetChanged();
                            scrollToTop();
                            clearUnreadConut();
                            if (messageList.size() == 0) {
                                switch (getType()) {
                                    case MyConstants.IM_CONV_TYPE_CAMPAIGN:

                                        break;
                                    case MyConstants.IM_CONV_TYPE_NOTIFICATION:
                                        placeholder.setImageResource(R.drawable.empty_system_message);
                                        break;
                                    case MyConstants.IM_CONV_TYPE_COMMENT:
                                        placeholder.setImageResource(R.drawable.empty_msg_comment);
                                        break;
                                    default:
                                        // fixme: throw exception
                                        LogUtil.e(TAG, "无法识别的类型");
                                }
                                placeholder.setVisibility(View.VISIBLE);
                            } else {
                                placeholder.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void queryMessageMore(AVIMMessage message) {
        imConversation.queryMessages(message.getMessageId(),
                message.getTimestamp(), PAGE_SIZE, new AVIMMessagesQueryCallback() {
                    @Override
                    public void done(List<AVIMMessage> list, AVIMException e) {

                        loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                        mRecyclerView.onRefreshComplete();

                        if (filterException(e)) {
                            if (null != list && list.size() > 0) {
                                Collections.reverse(list);
                                itemAdapter.addMessageList(list);
                                itemAdapter.notifyDataSetChanged();
                                layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                            }
                        }
                    }
                });
    }


    /**
     * 处理推送过来的消息
     * 同理，避免无效消息，此处加了 conversation id 判断
     */
    public void onEvent(LCIMIMTypeMessageEvent messageEvent) {
        LogUtil.i(TAG, "event - MessageEvent");
        if (null != imConversation && null != messageEvent &&
                imConversation.getConversationId().equals(messageEvent.conversation.getConversationId())) {
            itemAdapter.addMessage(messageEvent.message);
            itemAdapter.notifyDataSetChanged();
            scrollToTop();
            clearUnreadConut();
        }
    }

    /**
     * 滚动 recyclerView 到底部
     */
    private void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
    }

    /**
     * 滚动 recyclerView 到顶部
     */
    private void scrollToTop() {
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    private boolean filterException(Exception e) {
        if (null != e) {
            LCIMLogUtils.logException(e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return (null == e);
    }

    private void clearUnreadConut() {
        if (imConversation.getUnreadMessagesCount() > 0) {
            imConversation.read();
        }
    }

    public int getType() {
        return getArguments().getInt(MyConstants.IM_CONV_TYPE_KEY);
    }
}
