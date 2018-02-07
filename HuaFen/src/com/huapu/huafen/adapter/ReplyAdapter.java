package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReplyCommitActivity;
import com.huapu.huafen.activity.ReportActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.HPComment;
import com.huapu.huafen.beans.HPCommentData;
import com.huapu.huafen.beans.HPReplyData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.ReplyListResult;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.ActionConstants;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.MultiItemDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.HLinkTextView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 回复列表
 * Created by admin on 2017/5/14.
 */
public class ReplyAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    private final static Integer res = R.drawable.empty_goods_reply;
    private Context context;
    private List<Object> data;
    private ReplyListResult result;
    private boolean isEmpty;
    private OnSortChangedListener onSortChangedListener;
    private long pageUserId = 0;
    private long localUserId = 0;

    public ReplyAdapter(Context context) {
        this.context = context;
    }

    public void setData(ReplyListResult result) {
        if (result != null && result.obj != null) {
            this.result = result;
            this.data = new ArrayList<>();
            Item item = result.obj.item;
            if (item != null && item.item != null) {
                this.data.add(item);
            }

            HPComment comment = result.obj.comment;
            if (comment != null) {
                this.data.add(comment);
            }

            ArrayList<HPReplyData> replies = result.obj.replies;
            if (!ArrayUtil.isEmpty(replies)) {
                this.data.addAll(replies);
            }
            notifyWrapperDataSetChanged();
        }
    }

    public void addAll(List<HPReplyData> data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    @Override
    public void notifyWrapperDataSetChanged() {
        isEmpty = isEmpty();
        if (isEmpty) {
            if (data == null) {
                data = new ArrayList<>();
            }
            data.add(res);
        } else {
            if (data.contains(res)) {
                data.remove(res);
            }
        }
        super.notifyWrapperDataSetChanged();
    }

    private boolean isEmpty() {
        if (!ArrayUtil.isEmpty(this.data)) {
            for (Object obj : data) {
                if (obj instanceof HPReplyData) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = data.get(position);
        if (object instanceof Item) {
            Item item = (Item) object;
            if ("goods".equals(item.itemType)) {
                return ItemType.GOODS.ordinal();
            } else if (TextUtils.equals("web", item.itemType)) {
                // 活动
                return ItemType.ACTIVITY.ordinal();
            } else {
                // 花语
                return ItemType.ARTICLE.ordinal();
            }
        } else if (object instanceof HPComment) {
            return ItemType.COMMENT.ordinal();
        } else if (object instanceof Integer) {
            return ItemType.EMPTY.ordinal();
        } else {
            return ItemType.REPLY.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.GOODS.ordinal()) {
            return new ReplyGoodsHeader(LayoutInflater.from(context).inflate(R.layout.goods_header, parent, false));
        } else if (viewType == ItemType.ARTICLE.ordinal()) {
            // 花语
            return new ReplyArticleHeader(LayoutInflater.from(context).inflate(R.layout.article_header, parent, false));
        } else if (viewType == ItemType.ACTIVITY.ordinal()) {
            return new ReplyActivityHeader(LayoutInflater.from(context).inflate(R.layout.article_header, parent, false));
        } else if (viewType == ItemType.EMPTY.ordinal()) {
            return new EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.view_empty_image, parent, false));
        } else if (viewType == ItemType.COMMENT.ordinal()) {
            return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.header_reply_comment, parent, false));
        } else {
            return new ReplyHolder(LayoutInflater.from(context).inflate(R.layout.item_hp_reply, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Object obj = data.get(position);
        localUserId = CommonPreference.getUserId();
        if (holder instanceof ReplyHolder) {
            ReplyHolder viewHolder = (ReplyHolder) holder;
            final HPReplyData replyData = (HPReplyData) obj;

            String url = replyData.getUser().getAvatarUrl();
            String tag = (String) viewHolder.ivHeader.getTag();
            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                viewHolder.ivHeader.setImageURI(url);
                viewHolder.ivHeader.setTag(url);
            }
            viewHolder.ivHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, replyData.getUser().getUserId());
                    intent.putExtra("position", position);
                    ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                }
            });

            if (result.obj.item != null && result.obj.item.user != null) {
                if (result.obj.item.user.getUserId() == replyData.getUser().getUserId()) {
                    viewHolder.tvCommentTag.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.tvCommentTag.setVisibility(View.GONE);
                }
            }
            //姓名及其图标
            viewHolder.ctvName.setData(replyData.getUser());
            final boolean isLiked = replyData.getReply().getLiked();
            if (isLiked) {
                viewHolder.tvLike.setSelected(true);
            } else {
                viewHolder.tvLike.setSelected(false);
            }
            if (replyData.getReply().getLikeCount() == 0) {
                viewHolder.tvLike.setText("");
            } else {
                viewHolder.tvLike.setText(CommonUtils.getIntegerCount(replyData.getReply().getLikeCount(), MyConstants.COUNT_COMMENT));
            }
            viewHolder.tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(replyData.getReply().getCreatedAt()));

            viewHolder.hltvContent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!CommonPreference.isLogin()) {
                        ActionUtil.loginAndToast(context);
                        return;
                    }


                    if (ConfigUtil.isToVerify()) {
                        final TextDialog dialog = new TextDialog(context, false);
                        dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可回复");
                        dialog.setLeftText("取消");
                        dialog.setRightColor(Color.parseColor("#2d8bff"));
                        dialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                dialog.dismiss();
                            }
                        });
                        dialog.setRightText("去开通");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                Intent intent = new Intent(context, VerifiedActivity.class);
                                context.startActivity(intent);

                            }
                        });
                        dialog.show();
                    } else {
                        initMultiChoiceDialog(replyData);
                    }
                }
            });

            if (replyData.getRelatedUser() == null) {
                viewHolder.hltvContent.setText(replyData.getReply().getContent());
            } else {
                if (!TextUtils.isEmpty(replyData.getRelatedUser().getUserName()) && !TextUtils.isEmpty(replyData.getReply().getContent())) {
                    viewHolder.hltvContent.setLinkText(replyData.getRelatedUser().getUserName(), replyData.getReply().getContent());
                    viewHolder.hltvContent.setOnLinkTextClick(new HLinkTextView.OnLinkTextClick() {
                        @Override
                        public void onClick(String name) {
                            Intent intent = new Intent();
                            intent.setClass(context, PersonalPagerHomeActivity.class);
                            intent.putExtra(MyConstants.EXTRA_USER_ID, replyData.getRelatedUser().getUserId());
                            intent.putExtra("position", position);
                            ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                        }
                    });
                }
            }

            viewHolder.tvLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startRequestForLike(replyData);
                }
            });


        } else if (holder instanceof ReplyGoodsHeader) {
            ReplyGoodsHeader viewHolder = (ReplyGoodsHeader) holder;
            final Item var = (Item) obj;
            if (var != null) {
                UserData user = var.user;
                if (user != null) {
                    ViewUtil.setAvatar(viewHolder.avatar, user.getAvatarUrl());
                    pageUserId = user.getUserId();
                }

                if (var.item != null) {

                    List<String> list = var.item.goodsImgs;
                    if (!ArrayUtil.isEmpty(list)) {
                        ViewUtil.setAvatar(viewHolder.avatar, list.get(0));
                    }

                    String name = var.item.brand + " | " + var.item.name;
                    viewHolder.tvGoodsNameAndBrand.setText(name);
                    int price = var.item.price;
                    if (var.item.isAuction == 1) {
                        price = var.item.hammerPrice;
                    }
                    String format = String.format(context.getString(R.string.price_tag), price);
                    viewHolder.tvPrice.setText(Html.fromHtml(format));

                    viewHolder.tvGoods.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (context instanceof BaseActivity) {
                                String fromActivity = ((BaseActivity) context).fromActivity;
                                if (GoodsDetailsActivity.class.getSimpleName().equals(fromActivity)) {
                                    ((Activity) context).finish();
                                } else {
                                    Intent intent = new Intent(context, GoodsDetailsActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(var.item.goodsId));
                                    context.startActivity(intent);
                                }
                            }
                        }
                    });
                }
            }
        } else if (holder instanceof ReplyArticleHeader) {
            // 花语
            ReplyArticleHeader viewHolder = (ReplyArticleHeader) holder;
            final Item var = (Item) obj;
            viewHolder.tvGoods.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (var.item != null) {
                        if ("moment".equals(var.itemType)) {
                            Intent intent = new Intent(context, MomentDetailActivity.class);
                            intent.putExtra(MomentDetailActivity.MOMENT_ID, String.valueOf(var.item.articleId));
                            context.startActivity(intent);
                        } else if ("article".equals(var.itemType)) {
                            Intent intent = new Intent(context, ArticleDetailActivity.class);
                            intent.putExtra(MyConstants.ARTICLE_ID, String.valueOf(var.item.articleId));
                            context.startActivity(intent);
                        }
                    }
                }
            });

            if (var != null) {
                UserData user = var.user;
                if (user != null) {
                    ViewUtil.setAvatar(viewHolder.avatar, user.getAvatarUrl());
                    pageUserId = user.getUserId();
                }

                if (var.item != null) {
                    ViewUtil.setAvatar(viewHolder.avatar, var.item.titleMediaUrl);
                    if (!TextUtils.isEmpty(var.item.title)) {
                        viewHolder.articleTitle.setText(var.item.title);
                    }
                }
            }
        } else if (holder instanceof ReplyActivityHeader) {
            // 活动
            ReplyActivityHeader viewHolder = (ReplyActivityHeader) holder;
            final Item var = (Item) obj;
            viewHolder.tvGoods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (var.item != null) {
                        ActionUtil.dispatchAction(context, ActionConstants.OPEN_WEB_VIEW, var.item.target);
                    }
                }
            });

            if (var != null) {
                UserData user = var.user;
                if (user != null) {
                    ViewUtil.setAvatar(viewHolder.avatar, user.getAvatarUrl());
                    pageUserId = user.getUserId();
                }

                if (var.item != null) {
                    ViewUtil.setAvatar(viewHolder.avatar, var.item.image);
                    if (!TextUtils.isEmpty(var.item.title)) {
                        viewHolder.articleTitle.setText(var.item.title);
                    }
                }
            }
        } else if (holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            Integer res = (Integer) obj;
            emptyViewHolder.ivEmpty.setImageResource(res.intValue());
        } else {
            final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            final HPComment comment = (HPComment) obj;


            commentViewHolder.ivHeader.setImageURI(result.obj.user.getAvatarUrl());
            commentViewHolder.ctvName.setData(result.obj.user);
            final boolean isLiked = comment.getLiked();
            if (isLiked) {
                commentViewHolder.tvLike.setSelected(true);
            } else {
                commentViewHolder.tvLike.setSelected(false);
            }
            if (comment.getLikeCount() == 0) {
                commentViewHolder.tvLike.setText("");
            } else {
                commentViewHolder.tvLike.setText(CommonUtils.getIntegerCount(comment.getLikeCount(), MyConstants.COUNT_COMMENT));
            }

            if (result.obj.item != null && result.obj.item.user != null) {
                if (result.obj.item.user.getUserId() == result.obj.user.getUserId()) {
                    commentViewHolder.tvCommentTag.setVisibility(View.VISIBLE);
                } else {
                    commentViewHolder.tvCommentTag.setVisibility(View.GONE);
                }
            }
            commentViewHolder.tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(comment.getCreatedAt()));
            commentViewHolder.tvContent.setText(comment.getContent());

            commentViewHolder.ivReply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    replyPerformClick(comment);
                }
            });
            commentViewHolder.ivHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, result.obj.user.getUserId());
                    ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                }
            });
            commentViewHolder.tvContent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    if (!CommonPreference.isLogin()) {
                        ActionUtil.loginAndToast(context);
                        return;
                    }

                    if (ConfigUtil.isToVerify()) {
                        final TextDialog dialog = new TextDialog(context, false);
                        dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可回复");
                        dialog.setLeftText("取消");
                        dialog.setRightColor(Color.parseColor("#2d8bff"));
                        dialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                dialog.dismiss();
                            }
                        });
                        dialog.setRightText("去开通");
                        dialog.setRightCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                Intent intent = new Intent(context, VerifiedActivity.class);
                                context.startActivity(intent);

                            }
                        });
                        dialog.show();
                    } else {
                        long userId = result.obj.user.getUserId();
                        String[] items;
                        LogUtil.e("ReplybyGuan", "userId     " + userId);
                        LogUtil.e("ReplybyGuan", "replyUserId      " + pageUserId);
                        LogUtil.e("ReplybyGuan", "localUserId      " + localUserId);
                        if (userId == localUserId || pageUserId == localUserId) {
                            items = new String[]{"回复", "复制", "举报", "删除"};
                        } else {
                            items = new String[]{"回复", "复制", "举报"};
                        }
                        final String title = result.obj.user.getUserName() + ":" + result.obj.comment.getContent();

                        MultiItemDialog dialog = new MultiItemDialog(context, title, items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent;
                                switch (which) {
                                    case 0://回复
                                        replyPerformClick(comment);
                                        break;
                                    case 1://复制
                                        CommonUtils.copy(context, title);
                                        break;
                                    case 2://举报
                                        intent = new Intent(context, ReportActivity.class);
                                        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "4");
                                        if (result != null) {
                                            HPCommentData hpCommentData = new HPCommentData();
                                            hpCommentData.setComment(result.obj.comment);
                                            hpCommentData.setUser(result.obj.user);
                                            intent.putExtra(MyConstants.EXTRA_HPCOMMENT_DATA, hpCommentData);
                                        }
                                        context.startActivity(intent);
                                        break;
                                    case 3://删除
                                        final TextDialog textDialog = new TextDialog(context, true);
                                        textDialog.setContentText("要删除这条回复吗？");
                                        textDialog.setLeftText("取消");
                                        textDialog.setLeftCall(new DialogCallback() {

                                            @Override
                                            public void Click() {
                                                textDialog.dismiss();

                                            }
                                        });
                                        textDialog.setRightText("确定");
                                        textDialog.setRightCall(new DialogCallback() {
                                            @Override
                                            public void Click() {
                                                textDialog.dismiss();
                                                startRequestForDelComment(result);
                                            }
                                        });
                                        textDialog.show();

                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                        dialog.show();
                    }
                }
            });
            commentViewHolder.tvLike.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startRequestForLike(comment, commentViewHolder.tvLike);
                }
            });

            if (isEmpty) {
                commentViewHolder.rlSort.setVisibility(View.GONE);
            } else {
                commentViewHolder.rlSort.setVisibility(View.VISIBLE);
                commentViewHolder.rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        if (onSortChangedListener != null) {
                            onSortChangedListener.onSortChanged(checkedId == R.id.rbTime ? 1 : 2);
                        }
                    }
                });
            }
        }
    }

    public void setOnSortChangedListener(OnSortChangedListener onSortChangedListener) {
        this.onSortChangedListener = onSortChangedListener;
    }

    private void startRequestForLike(final HPComment data, final TextView tvLike) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(context);
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("targetId", String.valueOf(data.getCommentId()));
        params.put("targetType", "3");
        if (data.getLiked()) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.LIKE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "点赞:" + response);

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        int likeCount = data.getLikeCount();
                        if (data.getLiked()) {
                            data.setLiked(false);
                            if (likeCount > 0) {
                                data.setLikeCount(likeCount - 1);
                            }
                        } else {
                            data.setLiked(true);
                            data.setLikeCount(likeCount + 1);
                        }
                        if (data.getLiked()) {
                            tvLike.setSelected(true);
                        } else {
                            tvLike.setSelected(false);
                        }
                        if (data.getLikeCount() == 0) {
                            tvLike.setText("");
                        } else {
                            tvLike.setText(CommonUtils.getIntegerCount(data.getLikeCount(), MyConstants.COUNT_COMMENT));
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startRequestForDelComment(final ReplyListResult result) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put(MyConstants.Comment.TARGET_ID, String.valueOf(result.obj.comment.getCommentId()));

        OkHttpClientManager.postAsyn(MyConstants.DEL_COMMENT, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        ((Activity) context).onBackPressed();
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void replyPerformClick(HPComment comment) {
        if ("goods".equals(result.obj.item.itemType)) {

            ArticleAndGoods goods = result.obj.item.item;
            boolean isComment = CommonUtils.checkCommentCommit(goods.goodsState, goods.auditStatus);
            if (isComment) {
                actionToEditComment(comment);
            } else {
                CommonUtils.commentClickFailed(context);
            }
        } else {
            actionToEditComment(comment);
        }
    }

    private void actionToEditComment(HPComment comment) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(context);
            return;
        }

        if (result.obj.commentable == 0) {//不能留言
            ToastUtil.toast(context, context.getResources().getString(R.string.commentable_0));
            return;
        }
        int zmPoint = CommonPreference.getIntValue(CommonPreference.USER_ZM_CREDIT_POINT, 0);
        int level = CommonPreference.getIntValue(CommonPreference.USER_LEVEL, 0);
        Sale sale = CommonPreference.getSale();
        String idAuthRequiredFor = "";
        String idAuthRequiredFors[] = new String[0];
        if (sale != null) {
            idAuthRequiredFor = sale.getIdAuthRequiredFor();
            idAuthRequiredFors = idAuthRequiredFor.split(",");
        }
        if (!ConfigUtil.isToVerify()) {
            if (comment != null && result.obj.user != null) {
                Intent intent = new Intent(context, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, comment.getCommentId());
                intent.putExtra(MyConstants.USER_NAME, result.obj.user.getUserName());
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, 3);
                ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
            }
        } else {
            final TextDialog dialogText = new TextDialog(context, true);
            dialogText.setContentText("实名认证后才可以回复，赶快去开通芝麻信用吧~");
            dialogText.setLeftText("取消");
            dialogText.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialogText.dismiss();

                }
            });
            dialogText.setRightText("确定");
            dialogText.setRightCall(new DialogCallback() {
                @Override
                public void Click() {
                    dialogText.dismiss();
                    doRequestForCredit();
                }
            });
            dialogText.show();
        }
    }

    private void initMultiChoiceDialog(final HPReplyData replyData) {
        long userId = replyData.getUser().getUserId();
        String[] items;
        LogUtil.e("ReplybyGuan", "userId     " + userId);
        LogUtil.e("ReplybyGuan", "replyUserId      " + pageUserId);
        LogUtil.e("ReplybyGuan", "localUserId      " + localUserId);
        if (userId == localUserId || pageUserId == localUserId) {
            items = new String[]{"回复", "复制", "举报", "删除"};
        } else {
            items = new String[]{"回复", "复制", "举报"};
        }
        final String title = replyData.getUser().getUserName() + ":" + replyData.getReply().getContent();

        MultiItemDialog dialog = new MultiItemDialog(context, title, items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent;
                switch (which) {
                    case 0://回复
                        replyPerformClick(replyData);
                        break;
                    case 1://复制
                        CommonUtils.copy(context, title);
                        break;
                    case 2://举报
                        intent = new Intent(context, ReportActivity.class);
                        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "5");
                        intent.putExtra(MyConstants.EXTRA_HPREPLY_DATA, replyData);
                        context.startActivity(intent);
                        break;
                    case 3://删除
                        final TextDialog textDialog = new TextDialog(context, true);
                        textDialog.setContentText("要删除这条留言吗？");
                        textDialog.setLeftText("取消");
                        textDialog.setLeftCall(new DialogCallback() {

                            @Override
                            public void Click() {
                                textDialog.dismiss();

                            }
                        });
                        textDialog.setRightText("确定");
                        textDialog.setRightCall(new DialogCallback() {
                            @Override
                            public void Click() {
                                textDialog.dismiss();
                                startRequestForDelReply(replyData);
                            }
                        });
                        textDialog.show();

                        break;
                    default:
                        break;
                }
            }
        });
        dialog.show();
    }

    private void replyPerformClick(HPReplyData replyData) {
        if ("goods".equals(result.obj.item.itemType)) {
            ArticleAndGoods goods = result.obj.item.item;

            boolean isComment = CommonUtils.checkCommentCommit(goods.goodsState, goods.auditStatus);
            if (isComment) {
                actionToEditComment(replyData);
            } else {
                CommonUtils.commentClickFailed(context);
            }
        } else {
            actionToEditComment(replyData);
        }
    }

    private void actionToEditComment(HPReplyData item) {
        Intent intent;
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(context);
            return;
        }

        if (result.obj.commentable == 0) {//不能留言
            ToastUtil.toast(context, context.getResources().getString(R.string.commentable_0));
            return;
        }

        int zmPoint = CommonPreference.getIntValue(CommonPreference.USER_ZM_CREDIT_POINT, 0);
        int level = CommonPreference.getIntValue(CommonPreference.USER_LEVEL, 0);
        Sale sale = CommonPreference.getSale();
        String idAuthRequiredFor = "";
        String idAuthRequiredFors[] = new String[0];
        if (sale != null) {
            idAuthRequiredFor = sale.getIdAuthRequiredFor();
            idAuthRequiredFors = idAuthRequiredFor.split(",");
        }
        if (!ConfigUtil.isToVerify()) {
            if (item != null && item.getReply() != null) {
                intent = new Intent(context, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, item.getReply().getReplyId());
                if (item.getUser() != null && item.getUser().getUserName() != null) {
                    intent.putExtra(MyConstants.USER_NAME, item.getUser().getUserName());
                }
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, 4);
                ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
            }
        } else {
            final TextDialog dialogText = new TextDialog(context, true);
            dialogText.setContentText("实名认证后才可以回复，赶快去开通芝麻信用吧~");
            dialogText.setLeftText("取消");
            dialogText.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialogText.dismiss();

                }
            });
            dialogText.setRightText("确定");
            dialogText.setRightCall(new DialogCallback() {
                @Override
                public void Click() {
                    dialogText.dismiss();
                    doRequestForCredit();
                }
            });
            dialogText.show();
        }
    }

    private void startRequestForDelReply(final HPReplyData item) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put(MyConstants.Comment.TARGET_ID, String.valueOf(item.getReply().getReplyId()));

        OkHttpClientManager.postAsyn(MyConstants.DEL_REPLY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!ArrayUtil.isEmpty(data)) {
                            data.remove(item);
                            notifyWrapperDataSetChanged();
                        }
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void doRequestForCredit() {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(context);
        HashMap<String, String> params = new HashMap<String, String>();

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "芝麻信用：" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditInfo info = JSON.parseObject(baseResult.obj, CreditInfo.class);
                        String url = info.getCredential();
                        Intent intent = new Intent();
                        intent.setClass(context, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);

                    } else {
                        CommonUtils.error(baseResult, ((Activity) context), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void startRequestForLike(final HPReplyData data) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(context);
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("targetId", String.valueOf(data.getReply().getReplyId()));
        params.put("targetType", "4");
        if (data.getReply().getLiked()) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.LIKE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                LogUtil.i("liang", "点赞:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        int likeCount = data.getReply().getLikeCount();
                        if (data.getReply().getLiked()) {
                            data.getReply().setLiked(false);
                            if (likeCount > 0) {
                                data.getReply().setLikeCount(likeCount - 1);
                            }
                        } else {
                            data.getReply().setLiked(true);
                            data.getReply().setLikeCount(likeCount + 1);
                        }
                        notifyWrapperDataSetChanged();
                    } else {
                        CommonUtils.error(baseResult, (Activity) context, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public enum ItemType {
        GOODS,
        ARTICLE,
        ACTIVITY,
        COMMENT,
        REPLY,
        EMPTY,;
    }

    public interface OnSortChangedListener {
        void onSortChanged(int sort);
    }

    public class ReplyHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R.id.ctvName)
        CommonTitleView ctvName;
        @BindView(R.id.tvLike)
        TextView tvLike;
        @BindView(R.id.tvCommentTime)
        TextView tvCommentTime;
        @BindView(R.id.hltvContent)
        HLinkTextView hltvContent;
        @BindView(R.id.tvCommentTag)
        TextView tvCommentTag;


        public ReplyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

    public class ReplyGoodsHeader extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.tvGoodsNameAndBrand)
        TextView tvGoodsNameAndBrand;
        @BindView(R.id.tvPrice)
        TextView tvPrice;
        @BindView(R.id.tvGoods)
        TextView tvGoods;

        public ReplyGoodsHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // 花语
    public class ReplyArticleHeader extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.articleTitle)
        TextView articleTitle;
        @BindView(R.id.tvGoods)
        TextView tvGoods;

        public ReplyArticleHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // 活动
    public class ReplyActivityHeader extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R.id.articleTitle)
        TextView articleTitle;
        @BindView(R.id.tvGoods)
        TextView tvGoods;

        public ReplyActivityHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvGoods.setText("点击查看");
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivEmpty)
        ImageView ivEmpty;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R.id.ctvName)
        CommonTitleView ctvName;
        @BindView(R.id.ivReply)
        ImageView ivReply;
        @BindView(R.id.tvLike)
        TextView tvLike;
        @BindView(R.id.tvCommentTag)
        TextView tvCommentTag;
        @BindView(R.id.tvCommentTime)
        TextView tvCommentTime;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.rlSort)
        RelativeLayout rlSort;
        @BindView(R.id.rgSort)
        RadioGroup rgSort;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
