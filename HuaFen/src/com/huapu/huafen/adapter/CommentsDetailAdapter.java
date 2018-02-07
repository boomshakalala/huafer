package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.GalleryUrlActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.CommentDetailData;
import com.huapu.huafen.beans.CommentListBean;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.views.CommonTitleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lalo on 2016/10/20.
 */
public class CommentsDetailAdapter extends CommonWrapper<CommentsDetailAdapter.CommentsDetailHolder> {

    private Context mContext;
    private List<CommentDetailData.Comments> mComments = new ArrayList<CommentDetailData.Comments>();
    private RecyclerViewGalleryAdapter adapter;
    private OnItemClickListener onItemClickListener;

    public CommentsDetailAdapter(Context context, List<CommentDetailData.Comments> comments) {
        super();
        this.mContext = context;
        this.mComments = comments;
    }

    public CommentsDetailAdapter(Context context) {
        this(context, null);
    }

    public void setData(List<CommentDetailData.Comments> comments) {
        this.mComments = comments;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<CommentDetailData.Comments> comments) {
        if (mComments == null) {
            mComments = new ArrayList<CommentDetailData.Comments>();
        }
        this.mComments.addAll(comments);
        notifyWrapperDataSetChanged();
    }

    public boolean isEmpty() {
        return ArrayUtil.isEmpty(mComments);
    }

    @Override
    public CommentsDetailHolder onCreateViewHolder(ViewGroup parent, int i) {
        CommentsDetailHolder vh = new CommentsDetailHolder(LayoutInflater.from(mContext).inflate(R.layout.item_comment_detail, parent, false));
        return vh;
    }

    @Override
    public void onBindViewHolder(CommentsDetailHolder commentListHolder, int position) {
        final CommentDetailData.Comments comment = mComments.get(position);

        if (comment != null && comment.getUserData() != null) {

            ImageLoader.resizeSmall(commentListHolder.ivHeader, comment.getUserData().getAvatarUrl(), 1);

            int userType = comment.getUserData().getUserType();
            if (userType == 1) {
                commentListHolder.tvBuy.setVisibility(View.VISIBLE);
                commentListHolder.tvSell.setVisibility(View.GONE);
            } else if (userType == 2) {
                commentListHolder.tvBuy.setVisibility(View.GONE);
                commentListHolder.tvSell.setVisibility(View.VISIBLE);
            } else {
                commentListHolder.tvBuy.setVisibility(View.GONE);
                commentListHolder.tvSell.setVisibility(View.GONE);
            }
            commentListHolder.ctvName.setData(comment.getUserData());
            long timeStamp = comment.getCommentData().getRatedAt();
            if (timeStamp > 0) {
                commentListHolder.tvTime.setVisibility(View.VISIBLE);
                commentListHolder.tvTime.setText(DateTimeUtils.getYearMonthDayHourMinuteSecond(timeStamp));
            } else {
                commentListHolder.tvTime.setVisibility(View.GONE);
            }

            String content = comment.getCommentData().getContent();
            if (!TextUtils.isEmpty(content)) {
                commentListHolder.tvContent.setVisibility(View.VISIBLE);
                commentListHolder.tvContent.setText(content);
            } else {
                commentListHolder.tvContent.setVisibility(View.GONE);
            }

            commentListHolder.ivHeader.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (null == comment.getUserData() || comment.getUserData().getUserId() <= 0) {
                        return;
                    }
                    Intent intent = new Intent(mContext, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, comment.getUserData().getUserId());
                    mContext.startActivity(intent);

                }
            });

            int isSatisfied = comment.getCommentData().getSatisfaction();//10不满意 20一般 30满意

            if (isSatisfied == 10) { // 不满意
                commentListHolder.ivReplied.setImageResource(R.drawable.disreplied_dissatisfacion);
            } else if (isSatisfied == 20) { //一般
                commentListHolder.ivReplied.setImageResource(R.drawable.disreplied_general);
            } else if (isSatisfied == 30) {//满意
                commentListHolder.ivReplied.setImageResource(R.drawable.disreplied_satisfacion);
            }

            ArrayList<String> imgs = comment.getCommentData().getImgs();
            if (ArrayUtil.isEmpty(imgs)) {
                commentListHolder.rvHorizontalPic.setVisibility(View.GONE);
            } else {
                commentListHolder.rvHorizontalPic.setVisibility(View.VISIBLE);
                adapter = new RecyclerViewGalleryAdapter();
                commentListHolder.rvHorizontalPic.setAdapter(adapter);
                adapter.setData(imgs);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mComments == null ? 0 : mComments.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(CommentListBean comment);
    }

    public class CommentsDetailHolder extends RecyclerView.ViewHolder {

        public View item;
        public SimpleDraweeView ivHeader;
        public CommonTitleView ctvName;
        public TextView tvTime;
        public TextView tvContent;
        public RecyclerView rvHorizontalPic;
        public ImageView ivReplied;
        public TextView tvSell;
        public TextView tvBuy;

        public CommentsDetailHolder(View itemView) {
            super(itemView);
            this.item = itemView;
            ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            rvHorizontalPic = (RecyclerView) itemView.findViewById(R.id.rvHorizontalPic);
            ivReplied = (ImageView) itemView.findViewById(R.id.ivReplied);
            // 创建一个线性布局管理器
            LinearLayoutManager layoutManagerPic = new LinearLayoutManager(mContext);
            layoutManagerPic.setOrientation(LinearLayoutManager.HORIZONTAL);
            // 设置布局管理器
            rvHorizontalPic.setLayoutManager(layoutManagerPic);
            tvSell = (TextView) itemView.findViewById(R.id.tvSell);
            tvBuy = (TextView) itemView.findViewById(R.id.tvBuy);
        }
    }

    class RecyclerViewGalleryAdapter extends RecyclerView.Adapter<RecyclerViewGalleryAdapter.ViewHolder> {

        // 数据集
        private ArrayList<String> list = new ArrayList<String>();

        public RecyclerViewGalleryAdapter() {
            super();
        }

        public void setData(ArrayList<String> list) {
            if (list == null) {
                list = new ArrayList<String>();
            }
            this.list = list;
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
                    ArrayList<String> images = CommonUtils.getOSSStyle(list, MyConstants.OSS_ORIGINAL_STYLE);
                    Intent intent = new Intent(mContext, GalleryUrlActivity.class);
                    intent.putExtra(MyConstants.EXTRA_SHOW_PIC, images);
                    intent.putExtra(MyConstants.EXTRA_IMAGE_INDEX, position);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
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
            return list.size();
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
