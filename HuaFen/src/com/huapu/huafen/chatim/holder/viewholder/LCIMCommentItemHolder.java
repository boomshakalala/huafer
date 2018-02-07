package com.huapu.huafen.chatim.holder.viewholder;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.util.TypeUtils;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.HPReplyListActivityNew;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.CommentMsg;
import com.huapu.huafen.beans.Msg;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.StringUtils;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.HLinkTextView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 聊天页面中的文本 commentMsg 对应的 holder
 */
public class LCIMCommentItemHolder extends LCIMCommonViewHolder {

    @BindView(R2.id.ivHeader)
    SimpleDraweeView ivHeader;
    @BindView(R2.id.ctvName)
    CommonTitleView ctvName;
    @BindView(R2.id.tvCommentTime)
    TextView tvCommentTime;
    @BindView(R2.id.hltvContent)
    HLinkTextView hltvContent;
    @BindView(R2.id.ivGoodsPic)
    SimpleDraweeView ivGoodsPic;

    public LCIMCommentItemHolder(Context context, ViewGroup root, int layoutRes) {
        super(context, root, layoutRes);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void bindData(Object avimMessage) {
        if (!(avimMessage instanceof AVIMTextMessage)) {
            return;
        }

        AVIMTextMessage msg = (AVIMTextMessage) avimMessage;
        final CommentMsg commentMsg = new CommentMsg();
        commentMsg.image = (String) msg.getAttrs().get("image");
        commentMsg.action = (String) msg.getAttrs().get("action");
        commentMsg.target = (Integer) msg.getAttrs().get("target");
        
        final UserInfo userInfo = TypeUtils.castToJavaBean(msg.getAttrs().get("user"), UserInfo.class);
        commentMsg.message = TypeUtils.castToJavaBean(msg.getAttrs().get("message"), Msg.class);
        ctvName.setData(userInfo);
        ctvName.setNameSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tvCommentTime.setText(DateTimeUtils.getYearMonthDay(msg.getTimestamp()));
        ivHeader.setImageURI(userInfo.avatarUrl);
        ivGoodsPic.setImageURI(commentMsg.image);
        if (commentMsg.message.user != null) {
            if (!StringUtils.isEmpty(commentMsg.message.user.userName)) {
                hltvContent.setLinkText(commentMsg.message.user.userName, commentMsg.message.content);
            }
        } else {
            hltvContent.setText(commentMsg.message.content);
        }
        ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, userInfo.getUserId());
                getContext().startActivity(intent);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HPReplyListActivityNew.class);
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, commentMsg.target);
                getContext().startActivity(intent);
            }

        });

    }
}
