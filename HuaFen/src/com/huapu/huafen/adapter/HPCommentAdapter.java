
package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.HPReplyListActivityNew;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReplyCommitActivity;
import com.huapu.huafen.activity.ReportActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.HPComment;
import com.huapu.huafen.beans.HPCommentData;
import com.huapu.huafen.beans.HPReplyData;
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
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.other.OnClickableSpanListener;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;

/**
 * Created by admin on 2016/12/3.
 */
public class HPCommentAdapter extends CommonWrapper<HPCommentAdapter.CommentHolder> implements OnClickableSpanListener {


    private Context context;
    private List<HPCommentData> data;
    private Fragment fragment;
    private long goodsOwnerId;
    private GoodsData goods;
    private int commentable;
    private int targetType;
    private OnHandleHPCommentAdapterListener onHandleHPCommentAdapterListener;

    public HPCommentAdapter(Fragment fragment) {
        this(fragment, null);
    }

    public HPCommentAdapter(Fragment fragment, List<HPCommentData> data) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.data = data;
    }

    public HPCommentAdapter(Context context) {
        this(context, null);
    }

    public HPCommentAdapter(Context context, List<HPCommentData> data) {
        this.context = context;
        this.data = data;

    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    public void setGoodsOwnerId(long goodsOwnerId) {
        this.goodsOwnerId = goodsOwnerId;
    }

    public void setGoodsData(GoodsData goods) {
        this.goods = goods;
    }

    public void setData(List<HPCommentData> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<HPCommentData> data) {
        if (data == null) {
            data = new ArrayList<HPCommentData>();
        }

        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).
                inflate(R.layout.item_hp_comment_list, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, final int position) {
        final HPCommentData item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        String url = item.getUser().getAvatarUrl();
        String tag = (String) holder.ivHeader.getTag();
        if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
            holder.ivHeader.setTag(url);
            ImageLoader.resizeSmall(holder.ivHeader, url, 1);
        }
        holder.ivHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, item.getUser().getUserId());
                intent.putExtra("position", position);
                if (fragment == null) {
                    ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                } else {
                    fragment.startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                }
            }
        });
        if (goodsOwnerId != 0) {
            if (goodsOwnerId == item.getUser().getUserId()) {
                holder.tvCommentTag.setVisibility(View.VISIBLE);
            } else {
                holder.tvCommentTag.setVisibility(View.GONE);
            }
        }
        //姓名及其图标
        holder.ctvName.setData(item.getUser());
        final boolean isLiked = item.getComment().getLiked();
        if (isLiked) {
            holder.tvLike.setSelected(true);
            holder.tvLike.setTextColor(context.getResources().getColor(R.color.base_pink));
        } else {
            holder.tvLike.setSelected(false);
            holder.tvLike.setTextColor(context.getResources().getColor(R.color.text_black_enable));
        }
        if (item.getComment().getLikeCount() == 0) {
            holder.tvLike.setText("");
        } else {
            holder.tvLike.setText(CommonUtils.getIntegerCount(item.getComment().getLikeCount(), MyConstants.COUNT_COMMENT));
        }
        holder.tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(item.getComment().getCreatedAt()));
        holder.tvContent.setText(item.getComment().getContent());
        holder.tvContent.setOnClickListener(new View.OnClickListener() {

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
                    long userId = item.getUser().getUserId();
                    String[] items;
                    if (userId == CommonPreference.getUserId() || (goodsOwnerId == CommonPreference.getUserId())) {
                        items = new String[]{"回复", "复制", "举报", "删除"};
                    } else {
                        items = new String[]{"回复", "复制", "举报"};
                    }
                    final String title = item.getUser().getUserName() + ":" + item.getComment().getContent();

                    MultiItemDialog dialog = new MultiItemDialog(context, title, items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent;
                            switch (which) {
                                case 0://回复
                                    if (goods != null) {
                                        boolean isComment = CommonUtils.checkCommentCommit(goods.getGoodsState(), goods.getAuditStatus());
                                        if (isComment) {
                                            actionToEditComment(item);
                                        } else {
                                            CommonUtils.commentClickFailed(context);
                                        }
                                    } else {
                                        actionToEditComment(item);
                                    }
                                    break;
                                case 1://复制
                                    CommonUtils.copy(context, title);
                                    break;
                                case 2://举报
                                    intent = new Intent(context, ReportActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "4");
                                    intent.putExtra(MyConstants.EXTRA_HPCOMMENT_DATA, item);
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
                                            startRequestForDelComment(item);
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
        if (ArrayUtil.isEmpty(item.getReplies())) {
            holder.replyLayout.setVisibility(View.GONE);
        } else {
            holder.replyLayout.removeAllViews();
            holder.replyLayout.setVisibility(View.VISIBLE);
            String userName = item.getReplies().get(item.getReplies().size() - 1).getUser().getUserName();
            int count = item.getComment().getReplyCount();
            String countDes = CommonUtils.getIntegerCount(count, MyConstants.COUNT_COMMENT);
            String var2;
            if (count == 1) {
                var2 = "  ";
            } else {
                var2 = "等人  ";
            }

            String format = String.format(context.getString(R.string.msg_reply_des), userName, var2, countDes);
            holder.tvReplyMore.setText(Html.fromHtml(format));
            ArrayList<HPReplyData> hpReplyDataArrayList = item.getReplies();
            int lastIndex = hpReplyDataArrayList.size() - 1;

            Logger.e("get size:" + hpReplyDataArrayList.size());
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
                                    intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, Long.valueOf(item.getComment().getCommentId()));
                                    ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                                }
                            }).setSpecialTextColor(Color.parseColor("#151515")));
                    textView.setText(simplifySpanBuild.build());
                    holder.replyLayout.addView(textView);
                }
            }

            TextView textViewBottom = new TextView(context);
            textViewBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, HPReplyListActivityNew.class);
                    intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, Long.valueOf(item.getComment().getCommentId()));
                    ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                }
            });
            textViewBottom.setText(Html.fromHtml(format));
            textViewBottom.setPadding(fiveDp, fiveDp, fiveDp, fiveDp);
            textViewBottom.setBackgroundResource(R.color.base_bg);
            textViewBottom.setTextAppearance(context, R.style.leave_message);
            holder.replyLayout.addView(textViewBottom);

        }

        holder.tvLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRequestForLike(item);
            }
        });
        holder.ivReply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (goods != null) {
                    boolean isComment = CommonUtils.checkCommentCommit(goods.getGoodsState(), goods.getAuditStatus());
                    if (isComment) {
                        actionToEditComment(item);
                    } else {
                        CommonUtils.commentClickFailed(context);
                    }
                } else {
                    actionToEditComment(item);
                }
            }
        });

    }

    private void actionToEditComment(HPCommentData item) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(context);
            return;
        }

        if (commentable == 0) {//不能留言
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
                if (fragment == null) {
                    ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                } else {
                    fragment.startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                }
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

    private void startRequestForDelComment(final HPCommentData item) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();

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
                            if (onHandleHPCommentAdapterListener != null) {
                                onHandleHPCommentAdapterListener.onDelete();
                            }
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
    public void onClick(TextView tv, String clickText) {
        Logger.e("this is run:" + clickText);
    }

    public void setOnHandleHPCommentAdapterListener(OnHandleHPCommentAdapterListener onHandleHPCommentAdapterListener) {
        this.onHandleHPCommentAdapterListener = onHandleHPCommentAdapterListener;
    }

    private void startRequestForLike(final HPCommentData data) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast((Activity) context);
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("targetId", String.valueOf(data.getComment().getCommentId()));
        params.put("targetType", "3");
        if (data.getComment().getLiked()) {
            params.put("type", "2");
        } else {
            params.put("type", "1");
        }
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.LIKE, params,
                new OkHttpClientManager.StringCallback() {

                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        LogUtil.i("liang", "点赞:" + response);
                        JsonValidator validator = new JsonValidator();
                        boolean isJson = validator.validate(response);
                        if (!isJson) {
                            return;
                        }
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

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public void setCommentable(int commentable) {
        this.commentable = commentable;
    }

    public void updateLikeState(final int position, final int type) {
        if (!ArrayUtil.isEmpty(data) && data.size() > position) {
            final HPCommentData item = data.get(position);
            if (type == 2) {
                item.getComment().setLiked(false);
            } else {
                item.getComment().setLiked(true);
            }
            notifyWrapperDataSetChanged();
        }
    }

    public interface OnHandleHPCommentAdapterListener {
        void onDelete();
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public SimpleDraweeView ivHeader;
        public CommonTitleView ctvName;
        public ImageView ivReply;
        public TextView tvLike;
        public TextView tvReplyMore;
        public TextView tvCommentTime;
        public TextView tvContent;
        public TextView tvCommentTag;
        public LinearLayout replyLayout;


        public CommentHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            this.tvCommentTag = (TextView) itemView.findViewById(R.id.tvCommentTag);
            this.ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            this.tvCommentTime = (TextView) itemView.findViewById(R.id.tvCommentTime);
            this.tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            this.tvLike = (TextView) itemView.findViewById(R.id.tvLike);
            this.tvReplyMore = (TextView) itemView.findViewById(R.id.tvReplyMore);
            this.ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            this.replyLayout = (LinearLayout) itemView.findViewById(R.id.replyLayout);

        }
    }

}
