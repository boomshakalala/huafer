
package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.HPReplyListActivityNew;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.CommentMsg;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.StringUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.HLinkTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2016/12/3.
 */
public class CommentMsgAdapter extends CommonWrapper<CommentMsgAdapter.CommentHolder> {

    private String TAG="CommentMsgAdapter";
    private Context context;
    private ArrayList<CommentMsg> data;
    private Fragment fragment;

    public CommentMsgAdapter(Fragment fragment) {
        this(fragment, null);
    }

    public CommentMsgAdapter(Fragment fragment, ArrayList<CommentMsg> data) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.data = data;
    }

    public CommentMsgAdapter(Context context) {
        this(context, null);
    }

    public CommentMsgAdapter(Context context, ArrayList<CommentMsg> data) {
        this.context = context;
        this.data = data;

    }

    public void setData(ArrayList<CommentMsg> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }
    public void addAll(List<CommentMsg> data){
        if(this.data==null){
            this.data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }
    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).
                inflate(R.layout.item_comment_msg, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, final int position) {
        try {

            final CommentMsg item = data.get(position);
            if (item == null || item.user == null || item.message == null) {
                return;
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserLevel(item.user.userLevel);
            userInfo.setUserName(item.user.userName);
            holder.ctvName.setData(userInfo);
            holder.tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(item.timestamp));
            holder.ivHeader.setImageURI(item.user.avatarUrl);
            holder.ivGoodsPic.setImageURI(item.image);
            if (item.message.user != null) {
                LogUtil.i(TAG,"item.message.user != null");
                if (!StringUtils.isEmpty(item.message.user.userName)) {
                    holder.hltvContent.setLinkText(item.message.user.userName, item.message.content);
                }
            }else {
                holder.hltvContent.setText(item.message.content);
            }
            holder.ivHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.i(TAG,"用户id:  "+ item.user.userId);
                    Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.userId);
                    context.startActivity(intent);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //留言跳转
                    Intent intent = new Intent(context, HPReplyListActivityNew.class);
                    intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, item.target);
                    ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                    //回复跳转
                }

            });

        } catch (Exception e) {
            ToastUtil.toast(context, "留言数据展示失败");
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }


    public class CommentHolder extends RecyclerView.ViewHolder {

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

        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
