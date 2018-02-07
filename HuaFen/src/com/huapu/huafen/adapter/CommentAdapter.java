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
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.ArticleDetailActivity;
import com.huapu.huafen.activity.BaseActivity;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.HPReplyListActivityNew;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReplyCommitActivity;
import com.huapu.huafen.activity.ReportActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CommentListResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.HPComment;
import com.huapu.huafen.beans.HPCommentData;
import com.huapu.huafen.beans.HPReplyData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.beans.UserData;
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
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.OnClickableSpanListener;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;

/**
 * Created by admin on 2017/5/13.
 */

public class CommentAdapter extends CommonWrapper<RecyclerView.ViewHolder> {

    private final static Integer res = R.drawable.empty_goods_comment;
    private Context context;
    private List<Object> data;
    private CommentListResult result;
    private int targetType;
    private boolean isEmpty;
    private OnSortChangedListener onSortChangedListener;
    private long pageUserId;

    public CommentAdapter(Context context) {
        this.context = context;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public void setData(CommentListResult result) {
        if (result != null && result.obj != null) {
            this.result = result;
            this.data = new ArrayList<>();
            Item item = result.obj.item;
            Logger.e("get item:" + item.itemType);
            if (item != null) {
                this.data.add(item);
            }
            List<HPCommentData> comments = result.obj.comments;
            if (!ArrayUtil.isEmpty(comments)) {
                this.data.addAll(comments);
            }

            notifyWrapperDataSetChanged();
        }
    }

    public void addAll(List<HPCommentData> data) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        Object item = data.get(position);
        if (item instanceof Item) {
            Item tmp = (Item) item;
            if ("goods".equals(tmp.itemType)) {
                return ItemType.GOODS.ordinal();
            } else if ("article".equals(tmp.itemType) || "moment".equals(tmp.itemType)) {
                return ItemType.ARTICLE.ordinal();
            } else {
                return ItemType.H5Title.ordinal();
            }
        } else if (item instanceof Integer) {
            return ItemType.EMPTY.ordinal();
        } else {
            return ItemType.COMMENT.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.GOODS.ordinal()) {
            return new CommentGoodsHeader(LayoutInflater.from(context).inflate(R.layout.goods_header, parent, false));
        } else if (viewType == ItemType.ARTICLE.ordinal()) {
            return new CommentArticleHeader(LayoutInflater.from(context).inflate(R.layout.article_header, parent, false));
        } else if (viewType == ItemType.EMPTY.ordinal()) {
            return new EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.view_empty_image, parent, false));
        } else if (viewType == ItemType.H5Title.ordinal()) {
            return new H5TitleHolder(LayoutInflater.from(context).inflate(R.layout.header_h5_sort, parent, false));
        } else {
            return new CommentHolder(LayoutInflater.from(context).inflate(R.layout.item_hp_comment, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Object item = data.get(position);
        if (holder instanceof CommentHolder) {
            final HPCommentData commentData = (HPCommentData) item;
            CommentHolder viewHolder = (CommentHolder) holder;
            if (commentData != null) {
                final UserData user = commentData.getUser();
                if (user != null) {
                    String url = commentData.getUser().getAvatarUrl();
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
                            intent.putExtra(MyConstants.EXTRA_USER_ID, user.getUserId());
                            intent.putExtra("position", position);
                            ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                        }
                    });

                    //姓名及其图标
                    viewHolder.ctvName.setData(user);
                }

                if (result.obj.item != null && result.obj.item.user != null) {
                    if (result.obj.item.user.getUserId() == commentData.getUser().getUserId()) {
                        viewHolder.tvCommentTag.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.tvCommentTag.setVisibility(View.GONE);
                    }
                }

                HPComment comment = commentData.getComment();
                if (comment != null) {
                    final boolean isLiked = comment.getLiked();
                    if (isLiked) {
                        viewHolder.tvLike.setSelected(true);
                        viewHolder.tvLike.setTextColor(context.getResources().getColor(R.color.base_pink));
                    } else {
                        viewHolder.tvLike.setSelected(false);
                        viewHolder.tvLike.setTextColor(context.getResources().getColor(R.color.text_black_enable));
                    }

                    int likeCount = comment.getLikeCount();
                    if (likeCount == 0) {
                        viewHolder.tvLike.setText("");
                    } else {
                        viewHolder.tvLike.setText(CommonUtils.getIntegerCount(likeCount, MyConstants.COUNT_COMMENT));
                    }
                    viewHolder.tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(comment.getCreatedAt()));
                    String content = comment.getContent();
                    if (!TextUtils.isEmpty(content)) {
                        viewHolder.tvContent.setVisibility(View.VISIBLE);
                        viewHolder.tvContent.setText(comment.getContent());
                        viewHolder.tvContent.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if (!CommonPreference.isLogin()) {
                                    ActionUtil.loginAndToast(context);
                                    return;
                                }

                                if(ConfigUtil.isToVerify()){
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
                                }else {
                                    initMultiChoiceDialog(commentData);
                                }
                            }
                        });
                    } else {
                        viewHolder.tvContent.setVisibility(View.GONE);
                    }

                }

                if (ArrayUtil.isEmpty(commentData.getReplies())) {
                    viewHolder.replyLayout.setVisibility(View.GONE);
                } else {
                    viewHolder.replyLayout.removeAllViews();
                    viewHolder.replyLayout.setVisibility(View.VISIBLE);
                    String userName = commentData.getReplies().get(commentData.getReplies().size() - 1).getUser().getUserName();
                    int count = commentData.getComment().getReplyCount();
                    String countDes = CommonUtils.getIntegerCount(count, MyConstants.COUNT_COMMENT);
                    String var2;
                    if (count == 1) {
                        var2 = "  ";
                    } else {
                        var2 = "等人  ";
                    }
                    String format = String.format(context.getString(R.string.msg_reply_des), userName, var2, countDes);
                    viewHolder.tvReplyMore.setText(Html.fromHtml(format));

                    ArrayList<HPReplyData> hpReplyDataArrayList = commentData.getReplies();
                    int lastIndex = hpReplyDataArrayList.size() - 1;

                    if (null != hpReplyDataArrayList.get(0) && !hpReplyDataArrayList.get(0).removeData) {
                        if (hpReplyDataArrayList.size() > 1) {
                            hpReplyDataArrayList.remove(lastIndex);
                        }
                        if (hpReplyDataArrayList.size() > 0 && null != hpReplyDataArrayList.get(0)) {
                            hpReplyDataArrayList.get(0).removeData = true;
                        }

                    }

                    int fiveDp = context.getResources().getDimensionPixelSize(R.dimen.text_to_text);
                    Logger.e("get size:" + hpReplyDataArrayList.size());
                    if (hpReplyDataArrayList.size() > 1) {
                        for (final HPReplyData hpReplyData : hpReplyDataArrayList) {
                            TextView textView = new TextView(context);
                            textView.setPadding(fiveDp, 0, fiveDp, 0);
                            textView.setBackgroundResource(R.color.base_bg);
                            textView.setTextAppearance(context, R.style.leave_message);
                            SimplifySpanBuild simplifySpanBuild = new SimplifySpanBuild(context, textView);
                            simplifySpanBuild.appendSpecialUnit(new SpecialTextUnit(hpReplyData.getUser().getUserName() + "：").setOnClickListener(false, 0xFFF7F9FB, new OnClickableSpanListener() {
                                @Override
                                public void onClick(TextView tv, String clickText) {
                                    Intent intent = new Intent(context, PersonalPagerHomeActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_USER_ID, hpReplyData.getUser().getUserId());
                                    context.startActivity(intent);

                                }
                            }).setSpecialTextColor(Color.parseColor("#53709c")))
                                    .appendSpecialUnit(new SpecialTextUnit(hpReplyData.getReply().getContent()).setOnClickListener(false, 0xFFF7F9FB, new OnClickableSpanListener() {
                                        @Override
                                        public void onClick(TextView tv, String clickText) {
                                            Intent intent = new Intent(context, HPReplyListActivityNew.class);
                                            intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, Long.valueOf(commentData.getComment().getCommentId()));
                                            ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                                        }
                                    }).setSpecialTextColor(Color.parseColor("#151515")));
                            textView.setText(simplifySpanBuild.build());
                            viewHolder.replyLayout.addView(textView);
                        }
                    }

                    TextView textViewBottom = new TextView(context);
                    textViewBottom.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, HPReplyListActivityNew.class);
                            intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, Long.valueOf(commentData.getComment().getCommentId()));
                            ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                        }
                    });
                    textViewBottom.setText(Html.fromHtml(format));
                    textViewBottom.setPadding(fiveDp, fiveDp, fiveDp, fiveDp);
                    textViewBottom.setBackgroundResource(R.color.base_bg);
                    textViewBottom.setTextAppearance(context, R.style.leave_message);
                    viewHolder.replyLayout.addView(textViewBottom);
                }
                viewHolder.tvLike.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startRequestForLike(commentData);
                    }
                });
                viewHolder.ivReply.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        replyPerformClick(commentData);
                    }
                });
            }


        } else if (holder instanceof CommentGoodsHeader) {
            CommentGoodsHeader viewHolder = (CommentGoodsHeader) holder;
            final Item var = (Item) item;
            if (var != null) {
                UserData user = var.user;
                if (user != null) {
                    viewHolder.avatar.setImageURI(user.getAvatarUrl());
                    pageUserId = user.getUserId();

                }

                if (var.item != null) {

                    List<String> list = var.item.goodsImgs;
                    if (!ArrayUtil.isEmpty(list)) {
                        viewHolder.avatar.setImageURI(list.get(0));
                    }

                    String name = var.item.brand + " | " + var.item.name;
                    viewHolder.tvGoodsNameAndBrand.setText(name);
                    int price = var.item.price;
                    if(var.item.isAuction==1){
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

                    if (isEmpty) {
                        viewHolder.rlSort.setVisibility(View.GONE);
                    } else {
                        viewHolder.rlSort.setVisibility(View.VISIBLE);
                        viewHolder.rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

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

        } else if (holder instanceof H5TitleHolder) {
            H5TitleHolder viewHolder = (H5TitleHolder) holder;
            if (isEmpty) {
                viewHolder.rlSort.setVisibility(View.GONE);
            } else {
                viewHolder.rlSort.setVisibility(View.VISIBLE);
                viewHolder.rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        if (onSortChangedListener != null) {
                            onSortChangedListener.onSortChanged(checkedId == R.id.rbTime ? 1 : 2);
                        }
                    }
                });
            }
        } else if (holder instanceof CommentArticleHeader) {
            CommentArticleHeader viewHolder = (CommentArticleHeader) holder;
            final Item var = (Item) item;
            if (var != null) {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

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
                UserData user = var.user;
                if (user != null) {
                    pageUserId=user.getUserId();
                    viewHolder.avatar.setImageURI(user.getAvatarUrl());
                }

                if (var.item != null) {

                    viewHolder.avatar.setImageURI(var.item.titleMediaUrl);


                    if (!TextUtils.isEmpty(var.item.title)) {
                        viewHolder.articleTitle.setText(var.item.title);
                    }

                    if (isEmpty) {
                        viewHolder.rlSort.setVisibility(View.GONE);
                    } else {
                        viewHolder.rlSort.setVisibility(View.VISIBLE);
                        viewHolder.rgSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

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
        } else {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            Integer res = (Integer) item;
            emptyViewHolder.ivEmpty.setImageResource(res.intValue());
        }
    }

    private void replyPerformClick(HPCommentData commentData) {
        if ("goods".equals(result.obj.item.itemType)) {

            ArticleAndGoods goods = result.obj.item.item;

            boolean isComment = CommonUtils.checkCommentCommit(goods.goodsState, goods.auditStatus);
            if (isComment) {
                actionToEditComment(commentData);
            } else {
                CommonUtils.commentClickFailed(context);
            }
        } else {
            actionToEditComment(commentData);
        }
    }

    private void initMultiChoiceDialog(final HPCommentData commentData) {
        long userId = commentData.getUser().getUserId();
        String[] items;
        long localUserId=CommonPreference.getUserId();
        LogUtil.e("CommentbyGuan","userId     "+userId);
        LogUtil.e("CommentbyGuan","pageUserId     "+pageUserId);
        LogUtil.e("CommentbyGuan","localUserId     "+localUserId);
        if (userId ==localUserId||pageUserId==localUserId ) {
            items = new String[]{"回复", "复制", "举报", "删除"};
        } else {
            items = new String[]{"回复", "复制", "举报"};
        }
        final String title = commentData.getUser().getUserName() + ":" + commentData.getComment().getContent();

        MultiItemDialog dialog = new MultiItemDialog(context, title, items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent;
                switch (which) {
                    case 0://回复
                        replyPerformClick(commentData);
                        break;
                    case 1://复制
                        CommonUtils.copy(context, title);
                        break;
                    case 2://举报
                        intent = new Intent(context, ReportActivity.class);
                        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "4");
                        intent.putExtra(MyConstants.EXTRA_HPCOMMENT_DATA, commentData);
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
                                startRequestForDelComment(commentData);
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

    public void setOnSortChangedListener(OnSortChangedListener onSortChangedListener) {
        this.onSortChangedListener = onSortChangedListener;
    }

    private void startRequestForLike(final HPCommentData data) {
        HashMap<String, String> params = new HashMap<>();
        params.put("targetId", String.valueOf(data.getComment().getCommentId()));
        params.put("targetType", "3");
        if (data.getComment().getLiked()) {
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
                        int likeCount = data.getComment().getLikeCount();
                        if (data.getComment().getLiked()) {
                            data.getComment().setLiked(false);
                            if (likeCount > 0) {
                                data.getComment().setLikeCount(likeCount - 1);
                            }
                        } else {
                            data.getComment().setLiked(true);
                            data.getComment().setLikeCount(likeCount + 1);
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

    private void actionToEditComment(HPCommentData item) {
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
            if (item != null && item.getComment() != null) {
                HPComment comment = item.getComment();
                Intent intent = new Intent(context, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, comment.getCommentId());
                if (item.getUser() != null) {
                    UserData user = item.getUser();
                    String userName = user.getUserName();
                    intent.putExtra(MyConstants.USER_NAME, userName);
                }
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

    private void startRequestForDelComment(final HPCommentData item) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<>();

        params.put(MyConstants.Comment.TARGET_ID, String.valueOf(item.getComment().getCommentId()));

        OkHttpClientManager.postAsyn(MyConstants.DEL_COMMENT, params, new OkHttpClientManager.StringCallback() {

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
                if (obj instanceof HPCommentData) {
                    return false;
                }
            }
        }
        return true;
    }

    private void doRequestForCredit() {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(context);
        HashMap<String, String> params = new HashMap<>();


        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("lalo", "芝麻信用：" + response);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
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
        COMMENT,
        GOODS,
        ARTICLE,
        EMPTY,
        H5Title
    }

    public interface OnSortChangedListener {
        void onSortChanged(int sort);
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.ivHeader)
        SimpleDraweeView ivHeader;
        @BindView(R2.id.ctvName)
        CommonTitleView ctvName;
        @BindView(R2.id.ivReply)
        ImageView ivReply;
        @BindView(R2.id.tvLike)
        TextView tvLike;
        @BindView(R2.id.tvReplyMore)
        TextView tvReplyMore;
        @BindView(R2.id.tvCommentTime)
        TextView tvCommentTime;
        @BindView(R2.id.tvContent)
        TextView tvContent;
        @BindView(R2.id.tvCommentTag)
        TextView tvCommentTag;
        @BindView(R2.id.replyLayout)
        LinearLayout replyLayout;


        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class CommentGoodsHeader extends RecyclerView.ViewHolder {

        @BindView(R2.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R2.id.tvGoodsNameAndBrand)
        TextView tvGoodsNameAndBrand;
        @BindView(R2.id.tvPrice)
        TextView tvPrice;
        @BindView(R2.id.tvGoods)
        TextView tvGoods;
        @BindView(R2.id.rlSort)
        RelativeLayout rlSort;
        @BindView(R2.id.rgSort)
        RadioGroup rgSort;

        public CommentGoodsHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public class CommentArticleHeader extends RecyclerView.ViewHolder {

        @BindView(R2.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R2.id.articleTitle)
        TextView articleTitle;
        @BindView(R2.id.rlSort)
        RelativeLayout rlSort;
        @BindView(R2.id.rgSort)
        RadioGroup rgSort;

        public CommentArticleHeader(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R2.id.ivEmpty)
        ImageView ivEmpty;

        public EmptyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class H5TitleHolder extends RecyclerView.ViewHolder {


        @BindView(R2.id.rlSort)
        RelativeLayout rlSort;
        @BindView(R2.id.rgSort)
        RadioGroup rgSort;

        public H5TitleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
