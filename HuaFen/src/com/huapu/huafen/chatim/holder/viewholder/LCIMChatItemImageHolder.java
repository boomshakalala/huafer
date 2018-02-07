package com.huapu.huafen.chatim.holder.viewholder;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.messages.AVIMImageMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ViewUtil;

import cn.leancloud.chatkit.R;
import cn.leancloud.chatkit.activity.LCIMImageActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;

/**
 * Created by wli on 15/9/17.
 * 聊天页面中的图片 item 对应的 holder
 */
public class LCIMChatItemImageHolder extends LCIMChatItemHolder {
    private String TAG = "LCIMChatItemImageHolder";
    protected SimpleDraweeView contentView;
    private static final int MAX_DEFAULT_HEIGHT = 400;
    private static final int MAX_DEFAULT_WIDTH = 400;

    public LCIMChatItemImageHolder(Context context, ViewGroup root, boolean isLeft) {
        super(context, root, isLeft);
    }

    @Override
    public void initView() {
        super.initView();
        conventLayout.addView(View.inflate(getContext(), R.layout.lcim_chat_item_image_layout, null));
        contentView = (SimpleDraweeView) itemView.findViewById(R.id.item_image);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getContext(), LCIMImageActivity.class);
                    intent.setPackage(getContext().getPackageName());
                    intent.putExtra(LCIMConstants.IMAGE_LOCAL_PATH, ((AVIMImageMessage) message).getLocalFilePath());
                    intent.putExtra(LCIMConstants.IMAGE_URL, ((AVIMImageMessage) message).getFileUrl());
                    getContext().startActivity(intent);
                } catch (ActivityNotFoundException exception) {
                    Log.i(LCIMConstants.LCIM_LOG_TAG, exception.toString());
                }
            }
        });
    }

    @Override
    public void bindData(Object o) {
        super.bindData(o);
        AVIMMessage message = (AVIMMessage) o;
        if (message instanceof AVIMImageMessage) {
            AVIMImageMessage imageMsg = (AVIMImageMessage) message;
            // 图片的真实高度与宽度
            float actualHeight = imageMsg.getHeight();
            float actualWidth = imageMsg.getWidth();

            float viewHeight = MAX_DEFAULT_HEIGHT;
            float viewWidth = MAX_DEFAULT_WIDTH;

            float ratio = 1;

            if (0 != actualHeight && 0 != actualWidth) {
                // 要保证图片的长宽比不变
                ratio = actualWidth / actualHeight;
                if (ratio > viewWidth / viewHeight) {
                    viewWidth = actualWidth > viewWidth ? viewHeight : actualHeight;
                    viewHeight = viewWidth / ratio;
                } else {
                    viewHeight = actualHeight > viewHeight ? viewHeight : actualHeight;
                    viewWidth = viewHeight * ratio;
                }
            }

            ViewGroup.LayoutParams lp = contentView.getLayoutParams();
            lp.height = (int) viewHeight;
            lp.width = (int) viewWidth;
            contentView.setLayoutParams(lp);

            String localFilePath = imageMsg.getLocalFilePath();

            LogUtil.i(TAG, "localFilePath:" + localFilePath + "  getFileUrl：" + imageMsg.getFileUrl());

            if (!TextUtils.isEmpty(localFilePath)) {
                ViewUtil.setImgMiddle(contentView, MyConstants.FILE + localFilePath, ratio);
            } else if (!TextUtils.isEmpty(imageMsg.getFileUrl())) {
                ViewUtil.setImgMiddle(contentView, imageMsg.getFileUrl(), ratio);
            }
        }
    }
}