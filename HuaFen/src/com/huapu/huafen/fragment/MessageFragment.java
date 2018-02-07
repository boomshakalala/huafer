package com.huapu.huafen.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.OrderListActivity;
import com.huapu.huafen.chatim.activity.DefaultConversationActivity;
import com.huapu.huafen.chatim.activity.PrivateConversationListActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.event.LCIMConnectionChangeEvent;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;

/**
 * 会话列表
 */
public class MessageFragment extends BaseFragment implements OnClickListener {
    private String TAG = "MessageFragment";
    @BindView(R.id.tvOrderUnRead)
    TextView tvOrderUnRead;
    @BindView(R.id.tvOrderMsg)
    TextView tvOrderMsg;
    @BindView(R.id.layoutOrderList)
    RelativeLayout layoutOrderList;
    @BindView(R.id.tvCustomerUnRead)
    TextView tvCustomerUnRead;
    @BindView(R.id.tvCustomerMsg)
    TextView tvCustomerMsg;
    @BindView(R.id.layoutHuafenList)
    RelativeLayout layoutHuafenList;
    @BindView(R.id.tvNoticeUnRead)
    TextView tvNoticeUnRead;
    @BindView(R.id.tvNoticeMsg)
    TextView tvNoticeMsg;
    @BindView(R.id.layoutNoticeList)
    RelativeLayout layoutNoticeList;
    @BindView(R.id.tvChatUnRead)
    TextView tvChatUnRead;
    @BindView(R.id.tvChatMsg)
    TextView tvChatMsg;
    @BindView(R.id.layoutChatList)
    RelativeLayout layoutChatList;
    @BindView(R.id.tvCommentUnRead)
    TextView tvCommentUnRead;
    @BindView(R.id.tvCommentMsg)
    TextView tvCommentMsg;
    @BindView(R.id.layoutCommentList)
    RelativeLayout layoutCommentList;
    @BindView(R.id.tvTitleBarText)
    TextView tvTitleBarText;

    Unbinder unbinder;

    //    private boolean hasGetData = false;
    private boolean isHidden = false;
    private View mContentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mContentView = inflater.inflate(R.layout.fragment_message, container, false);
        unbinder = ButterKnife.bind(this, mContentView);

        initView();

//        getData();
//        updateUnreadCount();
        return mContentView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setPoint(TextView textView, int count) {
        if (count <= 0) {
            textView.setVisibility(View.GONE);
        } else if (count > 0 && count <= 999) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(count));
            if (count < 10) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                params.height = CommonUtils.dp2px(14f);
                params.width = params.height;
                textView.setBackgroundResource(R.drawable.red_point);
            } else {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                params.height = CommonUtils.dp2px(14f);
                params.width = CommonUtils.dp2px(RelativeLayout.LayoutParams.WRAP_CONTENT);
                textView.setPadding(CommonUtils.dp2px(5f), 0, CommonUtils.dp2px(5f), 0);
                textView.setBackgroundResource(R.drawable.red_rectangle_point);
            }
        } else {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            params.height = CommonUtils.dp2px(14f);
            params.width = CommonUtils.dp2px(RelativeLayout.LayoutParams.WRAP_CONTENT);
            textView.setPadding(CommonUtils.dp2px(5f), 0, CommonUtils.dp2px(5f), 0);
            textView.setBackgroundResource(R.drawable.red_rectangle_point);
            textView.setVisibility(View.VISIBLE);
            textView.setText("···");
        }
    }

    private void initView() {
        layoutOrderList.setOnClickListener(this);
        layoutHuafenList.setOnClickListener(this);
        layoutNoticeList.setOnClickListener(this);
        layoutChatList.setOnClickListener(this);
        layoutCommentList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(getContext());
            return;
        }
        switch (v.getId()) {
            case R.id.layoutOrderList: // 订单消息
                intent = new Intent(getActivity(), OrderListActivity.class);
                startActivity(intent);
                break;
            case R.id.layoutHuafenList: // 活动消息
                intent = new Intent(getActivity(), DefaultConversationActivity.class);
                intent.putExtra(MyConstants.IM_CONV_TYPE_KEY, MyConstants.IM_CONV_TYPE_CAMPAIGN);
                startActivity(intent);
                break;
            case R.id.layoutNoticeList: // 通知
                intent = new Intent(getActivity(), DefaultConversationActivity.class);
                intent.putExtra(MyConstants.IM_CONV_TYPE_KEY, MyConstants.IM_CONV_TYPE_NOTIFICATION);
                startActivity(intent);
                break;
            case R.id.layoutChatList: // 私信
                intent = new Intent(getActivity(), PrivateConversationListActivity.class);
                startActivity(intent);
                break;
            case R.id.layoutCommentList: // 留言
                intent = new Intent(getActivity(), DefaultConversationActivity.class);
                intent.putExtra(MyConstants.IM_CONV_TYPE_KEY, MyConstants.IM_CONV_TYPE_COMMENT);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (isHidden) {
            this.updateConversationList();
            isHidden = false;
        } else {
            isHidden = true;
        }
        LogUtil.i(TAG, "onHiddenChanged");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!CommonUtils.isNetAvaliable(getContext())) {
            setTitle(2);
            return;
        }

        if (null == LCChatKit.getInstance().getClient()) {
            ConfigUtil.loginLeanCloud(getContext());
        } else {
            updateConversationList();
        }
    }

    private void setTitle(int type) {
        switch (type) {
            case 0:
                tvTitleBarText.setText("消息(连接中...)");
                break;
            case 1:
                tvTitleBarText.setText("消息");
                break;
            case 2:
                tvTitleBarText.setText("消息(未连接)");
                break;
        }
    }

    public void updateConversationList() {

        Activity activity = getActivity();
        if (activity == null)
            return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> convIdList = LCIMConversationItemCache.getInstance().getSortedConversationList();

                    LogUtil.i(TAG, String.format("updateConversationList, %s, %s", convIdList.size(), convIdList));
                    LogUtil.d("danielluan", String.format("updateConversationList, %s, %s", convIdList.size(), convIdList));

                    int privateMsgUnreadCount = 0;

                    for (String convId : convIdList) {
                        AVIMConversation conv = LCChatKit.getInstance().getClient().getConversation(convId);
                        AVIMMessage msg = conv.getLastMessage();
                        // TODO: 初次安装时，此处返回 AVIMMessage，再次则返回 AVIMTextMessage
                        // 原因未明，需要提工单，此处暂时容错处理
                        AVIMTextMessage txtMsg = msg instanceof AVIMTextMessage ? (AVIMTextMessage) msg : null;

                        if (MyConstants.CONV_ORDER_ID.equals(conv.getConversationId())) {
                            // 尽量使用 msg.attrs.unreadCount，若其不可用，则使用 conv.getUnreadMessagecount
                            int n = 0;
                            if (conv.getUnreadMessagesCount() > 0) {
                                if (txtMsg != null && txtMsg.getAttrs() != null) {
                                    Object obj = txtMsg.getAttrs().get("unreadCount");
                                    if (obj != null && obj instanceof Integer) {
                                        n = (int) obj;
                                    }
                                }
                                tvOrderMsg.setText("您有新订单，请查看");
                            } else {
                                setPoint(tvOrderUnRead, 0);
                                tvOrderMsg.setText("暂无新订单");
                            }
                            setPoint(tvOrderUnRead, n > 0 ? n : conv.getUnreadMessagesCount());
                        } else if (MyConstants.ACTIVITY_ID.equals(conv.getConversationId())) {
                            setPoint(tvCustomerUnRead, conv.getUnreadMessagesCount());
                            if (conv.getUnreadMessagesCount() > 0) {
                                tvCustomerMsg.setText(txtMsg == null || txtMsg.getText() == null
                                        ? "您有活动通知，请查看" : txtMsg.getText());
                            } else {
                                tvCustomerMsg.setText("暂无新消息");
                            }

                        } else if (MyConstants.NOTICE_ID.equals(conv.getConversationId())) {
                            setPoint(tvNoticeUnRead, conv.getUnreadMessagesCount());
                            if (conv.getUnreadMessagesCount() > 0) {
                                tvNoticeMsg.setText(txtMsg == null || txtMsg.getText() == null
                                        ? "您有通知，请查看" : txtMsg.getText());
                            } else {
                                tvNoticeMsg.setText("暂无新消息");
                            }

                        } else if (MyConstants.COMMENT_ID.equals(conv.getConversationId())) {
                            setPoint(tvCommentUnRead, conv.getUnreadMessagesCount());
                            if (conv.getUnreadMessagesCount() > 0) {
                                tvCommentMsg.setText("您有新留言，请查看");
                            } else {
                                tvCommentMsg.setText("暂无新消息");
                            }
                        } else {
                            // 某些异常情况
                            if (conv.getMembers().size() == 0) {
                                //LCIMConversationItemCache.getInstance().deleteConversation(convId);
                            } else {
                                privateMsgUnreadCount += conv.getUnreadMessagesCount();
                            }
                        }
                    }

                    setPoint(tvChatUnRead, privateMsgUnreadCount);
                    if (privateMsgUnreadCount > 0) {
                        tvChatMsg.setText("您有新私信，请查收");
                    } else {
                        tvChatMsg.setText("暂无新消息");
                    }
                } catch (Exception e) {
                    LogUtil.d("danielluan", e.getMessage());
                }
            }
        });
    }
    
    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateConversationList();
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateConversationList();
    }

    // 网络状态
    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }

        if (obj instanceof LCIMConnectionChangeEvent) {
            LCIMConnectionChangeEvent event = (LCIMConnectionChangeEvent) obj;
            setTitle(event.isConnect ? 1 : 2);
        }
    }
}
