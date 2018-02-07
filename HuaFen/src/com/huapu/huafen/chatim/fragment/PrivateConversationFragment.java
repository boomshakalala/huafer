package com.huapu.huafen.chatim.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageOption;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMAudioMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.huapu.huafen.R;
import com.huapu.huafen.chatim.adapter.PrivateConversationAdapter;
import com.huapu.huafen.common.LeanCloudError;
import com.huapu.huafen.common.RequestCode;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.utils.BitmapUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.leancloud.chatkit.event.LCIMConversationReadStatusEvent;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMInputBottomBarEvent;
import cn.leancloud.chatkit.event.LCIMInputBottomBarRecordEvent;
import cn.leancloud.chatkit.event.LCIMInputBottomBarTextEvent;
import cn.leancloud.chatkit.event.LCIMMessageResendEvent;
import cn.leancloud.chatkit.utils.LCIMAudioHelper;
import cn.leancloud.chatkit.utils.LCIMLogUtils;
import cn.leancloud.chatkit.utils.LCIMNotificationUtils;
import cn.leancloud.chatkit.utils.LCIMPathUtils;
import cn.leancloud.chatkit.view.LCIMInputBottomBar;
import de.greenrobot.event.EventBus;
import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by wli on 15/8/27.
 * 将聊天相关的封装到此 Fragment 里边，只需要通过 setConversation 传入 Conversation 即可
 */
public class PrivateConversationFragment extends Fragment {
    private String TAG = "PrivateConversationFragment";
    protected AVIMConversation imConversation;
    /**
     * recyclerView 对应的 Adapter
     */
    protected PrivateConversationAdapter itemAdapter;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;
    /**
     * 负责下拉刷新的 SwipeRefreshLayout
     */
    protected SwipeRefreshLayout refreshLayout;
    /**
     * 底部的输入栏
     */
    protected LCIMInputBottomBar inputBottomBar;
    // 记录拍照等的文件路径
    protected String localCameraPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.lcim_conversation_fragment, container, false);
//        final View rlChat = view.findViewById(R.id.rl_chatfragment);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LogUtil.i(TAG,"view被点击");
//                LCIMSoftInputUtils.hideSoftInput(getContext(),rlChat);
//            }
//        });
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_rv_chat);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_chat_srl_pullrefresh);
        refreshLayout.setEnabled(false);

        inputBottomBar = (LCIMInputBottomBar) view.findViewById(R.id.fragment_chat_inputbottombar);
        layoutManager = new LinearLayoutManager(getActivity());
        //layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        ViewUtil.setOffItemAnimator(recyclerView);

        itemAdapter = getAdpter();
        itemAdapter.resetRecycledViewPoolSize(recyclerView);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    LogUtil.i(TAG, TAG + "  MotionEvent.ACTION_DOWN");
                    inputBottomBar.touchOthers();
                }
                return false;
            }
        });
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AVIMMessage message = itemAdapter.getFirstMessage();
                if (null == message) {
                    refreshLayout.setRefreshing(false);
                } else {
                    imConversation.queryMessages(message.getMessageId(), message.getTimestamp(), 20, new AVIMMessagesQueryCallback() {
                        @Override
                        public void done(List<AVIMMessage> list, AVIMException e) {
                            refreshLayout.setRefreshing(false);
                            if (filterException(e)) {
                                if (null != list && list.size() > 0) {
                                    itemAdapter.addMessageList(list);
                                    itemAdapter.setDeliveredAndReadMark(imConversation.getLastDeliveredAt(),
                                            imConversation.getLastReadAt());
                                    // itemAdapter.notifyDataSetChanged();
                                    layoutManager.scrollToPositionWithOffset(list.size() - 1, 0);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    protected PrivateConversationAdapter getAdpter() {
        return new PrivateConversationAdapter();
    }

    public void setConversation(final AVIMConversation conversation) {
        imConversation = conversation;
        refreshLayout.setEnabled(true);
        inputBottomBar.setTag(imConversation.getConversationId());
        fetchMessages();
        LCIMNotificationUtils.addTag(conversation.getConversationId());
        if (!conversation.isTransient()) {
            if (conversation.getMembers().size() == 0) {
                conversation.fetchInfoInBackground(new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        if (null != e) LCIMLogUtils.logException(e);
                        itemAdapter.showUserName(conversation.getMembers().size() > 2);
                    }
                });
            } else {
                itemAdapter.showUserName(conversation.getMembers().size() > 2);
            }
        } else {
            itemAdapter.showUserName(true);
        }
    }

    /**
     * 拉取消息，必须加入 conversation 后才能拉取消息
     */
    private void fetchMessages() {
        imConversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(final List<AVIMMessage> messageList, AVIMException e) {
                if (filterException(e)) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                itemAdapter.setMessageList(messageList);
                                // recyclerView.setAdapter(itemAdapter);
                                // itemAdapter.setDeliveredAndReadMark(imConversation.getLastDeliveredAt(),
                                // imConversation.getLastReadAt());
                                int unreadMessagesCount = imConversation.getUnreadMessagesCount();
                                LogUtil.e("PrivateConversationFragment", "未读消息数   " + unreadMessagesCount);
                                // imConversation.read();
                                // itemAdapter.notifyDataSetChanged();
                                scrollToBottom();
                                clearUnreadConut();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 输入事件处理，接收后构造成 AVIMTextMessage 然后发送
     * 因为不排除某些特殊情况会受到其他页面过来的无效消息，所以此处加了 tag 判断
     */
    public void onEvent(LCIMInputBottomBarTextEvent textEvent) {
        LogUtil.i(TAG, String.format("event - TextEvent, " +
                        "content=%s, conversationIsNull=%s, conversationId=%s, event.tag=%s",
                textEvent.sendContent, imConversation == null,
                imConversation == null ? null : imConversation.getConversationId(), textEvent.tag));
        if (null != imConversation && null != textEvent) {
            if (!TextUtils.isEmpty(textEvent.sendContent)
                    && imConversation.getConversationId().equals(textEvent.tag)) {
                sendText(textEvent.sendContent);
            }
        }
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
            // itemAdapter.notifyDataSetChanged();
            scrollToBottom();
            clearUnreadConut();
        }
    }

    /**
     * 重新发送已经发送失败的消息
     */
    public void onEvent(LCIMMessageResendEvent resendEvent) {
        if (null != imConversation && null != resendEvent && null != resendEvent.message
                && imConversation.getConversationId().equals(resendEvent.message.getConversationId())) {

            if (AVIMMessage.AVIMMessageStatus.AVIMMessageStatusFailed == resendEvent.message.getMessageStatus()
                    && imConversation.getConversationId().equals(resendEvent.message.getConversationId())) {
                sendMessage(resendEvent.message, resendEvent.pos, false);
            }
        }
    }

    /**
     * 处理输入栏发送过来的事件
     */
    public void onEvent(LCIMInputBottomBarEvent event) {
        if (null != imConversation && null != event && imConversation.getConversationId().equals(event.tag)) {
//    if (null != imConversation && null != event ) {
            switch (event.eventAction) {
                case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_IMAGE_ACTION:
                    // dispatchPickPictureIntent();
                    dispatchPickPicture();
                    break;
                case LCIMInputBottomBarEvent.INPUTBOTTOMBAR_CAMERA_ACTION:
                    dispatchTakePictureIntent();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 处理录音事件
     *
     * @param recordEvent
     */
    public void onEvent(LCIMInputBottomBarRecordEvent recordEvent) {
        if (null != imConversation && null != recordEvent &&
                !TextUtils.isEmpty(recordEvent.audioPath) &&
                imConversation.getConversationId().equals(recordEvent.tag)) {
            sendAudio(recordEvent.audioPath);
        }
    }

    /**
     * 更新对方已读的位置事件
     */
    public void onEvent(LCIMConversationReadStatusEvent readEvent) {
        if (null != imConversation && null != readEvent &&
                imConversation.getConversationId().equals(readEvent.conversationId)) {
            itemAdapter.setDeliveredAndReadMark(imConversation.getLastDeliveredAt(),
                    imConversation.getLastReadAt());
            itemAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 发送 Intent 跳转到系统拍照页面
     */
    private void dispatchTakePictureIntent() {
        localCameraPath = LCIMPathUtils.getPicturePathByCurrentTime(getContext());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = Uri.fromFile(new File(localCameraPath));
        takePictureIntent.putExtra("return-data", false);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RequestCode.REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * 选择照片
     */
    private void dispatchPickPicture() {
        DialogManager.pickImage(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case RequestCode.REQUEST_IMAGE_CAPTURE:
                    sendImage(localCameraPath);
                    break;
                case RequestCode.REQUEST_IMAGE_PICK:
                    ArrayList<String> selectPaths = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                    sendImages(selectPaths);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 滚动 recyclerView 到底部
     */
    public void scrollToBottom() {
        layoutManager.scrollToPositionWithOffset(itemAdapter.getItemCount() - 1, 0);
    }

    /**
     * 根据 Uri 获取文件所在的位置
     *
     * @param context
     * @param contentUri
     * @return
     */
    private String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri.getScheme().equals("file")) {
            return contentUri.getEncodedPath();
        } else {
            Cursor cursor = null;
            try {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
                if (null != cursor) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    return cursor.getString(column_index);
                } else {
                    return "";
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * 发送文本消息
     */
    protected void sendText(String content) {
        AVIMTextMessage message = new AVIMTextMessage();
        message.setText(content);
        HashMap<String, Object> arr = new HashMap<String, Object>();
        if (CommonPreference.getUserInfo() != null) {
            arr.put("username", CommonPreference.getUserInfo().getUserName());
            message.setAttrs(arr);
            LogUtil.d("danielluan", "username=" + CommonPreference.getUserInfo().getUserName());
        }
        sendMessage(message);
    }

    private void sendImages(ArrayList<String> selectBitmap) {
        for (String item : selectBitmap) {
            sendImage(item);
        }
    }

    /**
     * 发送图片消息
     * TODO 上传的图片最好要压缩一下
     */
    @SuppressLint("StaticFieldLeak")
    protected void sendImage(final String imagePath) {
        new AsyncTask<String, String, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                File file = new File(params[0]);
                Bitmap bitmap = BitmapUtil.createImageThumbnail(file, 256);
                return CommonUtils.isQRCode(bitmap);
            }

            @Override
            protected void onPostExecute(Boolean isQRCode) {
                if (isQRCode) {
                    ToastUtil.toast(getContext(),
                            "警告！平台不支持站外二维码交易！请遵守和维护平台交易规则。");
                } else {
                    try {
                        sendMessage(new AVIMImageMessage(imagePath));
                    } catch (IOException e) {
                        LogUtil.e(e, TAG, "发送图片消息失败");
                        ToastUtil.toast(getContext(), "发送失败");
                    }
                }
            }
        }.execute(imagePath);
    }

    /**
     * 发送语音消息
     *
     * @param audioPath
     */
    protected void sendAudio(String audioPath) {
        try {
            AVIMAudioMessage audioMessage = new AVIMAudioMessage(audioPath);
            sendMessage(audioMessage);
        } catch (IOException e) {
            LCIMLogUtils.logException(e);
        }
    }

    public void sendMessage(AVIMMessage message) {
        sendMessage(message, itemAdapter.getItemCount(), true);
    }

    /**
     * 发送消息
     */
    public void sendMessage(final AVIMMessage message, final int pos, boolean addToList) {
        LogUtil.i(TAG, "sending message");
        if (addToList) {
            message.setFrom(CommonPreference.getUserId() + "");
            itemAdapter.addMessage(message);
        }

        scrollToBottom();

        AVIMMessageOption option = new AVIMMessageOption();
        option.setReceipt(true);
        imConversation.sendMessage(message, option, new AVIMConversationCallback() {
            @Override
            public void done(AVIMException e) {
                if (null != e) {
                    LeanCloudError.toast(getContext(), e.getCode());
                    LogUtil.e(TAG, e);
                }
                itemAdapter.notifyItemChanged(pos);
            }
        });
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
    }

}
