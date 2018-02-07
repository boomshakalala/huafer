package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.CommentsDetailActivity;
import com.huapu.huafen.activity.GalleryActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.CommentData;
import com.huapu.huafen.beans.Comments;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/1/11.
 */

public class CommentUserView extends LinearLayout {

    @BindView(R2.id.ivHeader)
    SimpleDraweeView ivHeader;
    @BindView(R2.id.tvCommentTag)
    TextView tvCommentTag;
    @BindView(R2.id.ctvName)
    CommonTitleView ctvName;
    @BindView(R2.id.tvTime)
    TextView tvTime;
    @BindView(R2.id.ivReplied)
    ImageView ivReplied;
    @BindView(R2.id.tvContent)
    TextView tvContent;
    @BindView(R2.id.rvHorizontalPic)
    RecyclerView rvHorizontalPic;
    @BindView(R2.id.tvMutualAssessment)
    TextView tvMutualAssessment;
    private RecyclerViewGalleryAdapter adapter;

    private long mId;


    public CommentUserView(Context context) {
        this(context, null);
    }

    public CommentUserView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.comment_user_new, this, true);
        ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvHorizontalPic.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerViewGalleryAdapter(context);
        rvHorizontalPic.setAdapter(adapter);
    }

    public void setDataWithId(Comments.Comment comment, long mId) {
        this.mId = mId;
        setData(comment);
    }


    public void setData(final Comments.Comment comment) {
        if (comment == null) {
            return;
        }

        final UserData userData = comment.getUserData();
        if (userData != null) {
            String url = userData.getAvatarUrl();
            ImageLoader.loadImage(ivHeader, url, R.drawable.default_pic, R.drawable.default_pic);
            ctvName.setData(comment.getUserData());
            int userType = userData.getUserType();
            if (userType == 1) {//买家
                tvCommentTag.setVisibility(VISIBLE);
                tvCommentTag.setText("买家");
                tvCommentTag.setBackgroundResource(R.drawable.pink_bg_shape);
            } else if (userType == 2) {
                tvCommentTag.setVisibility(VISIBLE);
                tvCommentTag.setText("卖家");
                tvCommentTag.setBackgroundResource(R.drawable.light_blue_bg_shape);
            } else {
                tvCommentTag.setVisibility(GONE);
            }

            ivHeader.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (userData.getUserId() <= 0) {
                        return;
                    }
                    Intent intent = new Intent(getContext(), PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, userData.getUserId());
                    getContext().startActivity(intent);

                }
            });
        }

        final CommentData commentData = comment.getCommentData();
        if (commentData != null) {
            long time = comment.getCommentData().getRatedAt();
            if (time > 0) {
                tvTime.setVisibility(View.VISIBLE);
                tvTime.setText(DateTimeUtils.getYearMonthDayHourMinuteSecond(time));
            } else {
                tvTime.setVisibility(View.GONE);
            }

            String content = commentData.getContent();
            if (!TextUtils.isEmpty(content)) {
                tvContent.setVisibility(VISIBLE);
                tvContent.setText(content);
                tvContent.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getContext(), CommentsDetailActivity.class);
                        intent.putExtra(MyConstants.EXTRA_COMMENT_ID, commentData.getOrderId());
                        intent.putExtra(MyConstants.EXTRA_FROM_USER_ID, mId);
                        getContext().startActivity(intent);
                    }
                });
            } else {
                tvContent.setVisibility(GONE);
            }

            boolean replied = comment.getCommentData().getReplied();
            int isSatisfied = comment.getCommentData().getSatisfaction();//10不满意 20一般 30满意
            if (replied) {//已互评,图片无右padding
                ivReplied.setPadding(0, 0, CommonUtils.dp2px(10), 0);
                if (isSatisfied == 10) { // 不满意
                    ivReplied.setImageResource(R.drawable.disreplied_dissatisfacion);
                } else if (isSatisfied == 20) { //一般
                    ivReplied.setImageResource(R.drawable.disreplied_general);
                } else if (isSatisfied == 30) {//满意
                    ivReplied.setImageResource(R.drawable.disreplied_satisfacion);
                }
            } else {//没互评,图片有右padding
                ivReplied.setPadding(0, 0, CommonUtils.dp2px(10), 0);
                if (isSatisfied == 10) { // 不满意
                    ivReplied.setImageResource(R.drawable.disreplied_dissatisfacion);
                } else if (isSatisfied == 20) { //一般
                    ivReplied.setImageResource(R.drawable.disreplied_general);
                } else if (isSatisfied == 30) {//满意
                    ivReplied.setImageResource(R.drawable.disreplied_satisfacion);
                }
            }

            tvMutualAssessment.setVisibility(replied ? VISIBLE : GONE);

            ArrayList<String> images = comment.getCommentData().getImgs();
            if (ArrayUtil.isEmpty(images)) {
                rvHorizontalPic.setVisibility(View.GONE);
            } else {
                rvHorizontalPic.setVisibility(View.VISIBLE);
                adapter.setData(images);
            }

        } else {
            rvHorizontalPic.setVisibility(GONE);
        }
    }


    public class RecyclerViewGalleryAdapter extends RecyclerView.Adapter<RecyclerViewGalleryAdapter.ViewHolder> {

        private Context context;
        // 数据集
        private ArrayList<String> list = new ArrayList<String>();

        public RecyclerViewGalleryAdapter(Context context) {
            this.context = context;
        }

        public void setData(ArrayList<String> list) {
            if (list == null) {
                list = new ArrayList<String>();
            }
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // 创建一个View，简单起见直接使用系统提供的布局，就是一个TextView
            View view = View.inflate(viewGroup.getContext(), R.layout.item_hlistview_goods, null);
            // 创建一个ViewHolder
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // 绑定数据到ViewHolder上
            ImageLoader.resizeMiddle(viewHolder.ivPic, list.get(position), 1);
            
            viewHolder.ivPic.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (list == null || list.size() == 0) {
                        return;
                    }

                    Intent intent = new Intent(context, GalleryActivity.class);
                    intent.putExtra("banners", CommonUtils.covertOriginalBanners(list));
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(0, 0);
                }
            });

            if (position == 0) {
                viewHolder.itemView.setPadding(CommonUtils.dp2px(10), 0, 0, 0);
            } else {
                viewHolder.itemView.setPadding(0, 0, 0, 0);
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public SimpleDraweeView ivPic;

            public ViewHolder(View itemView) {
                super(itemView);
                ivPic = (SimpleDraweeView) itemView.findViewById(R.id.ivPic);
            }
        }
    }
}
