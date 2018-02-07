
package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.activity.ReplyCommitActivity;
import com.huapu.huafen.activity.ReportActivity;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.GoodsData;
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
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.HLinkTextView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/12/3.
 */
public class HPReplyAdapter extends CommonWrapper<HPReplyAdapter.CommentHolder> {


    private Context context;
    private List<HPReplyData> data;
    private Fragment fragment;
    private UserData goodsOwner;
    private GoodsData goods;
    private int commentable;

    public HPReplyAdapter(Fragment fragment) {
        this(fragment, null);
    }

    public HPReplyAdapter(Fragment fragment, List<HPReplyData> data) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.data = data;
    }

    public HPReplyAdapter(Context context) {
        this(context, null);
    }

    public HPReplyAdapter(Context context, List<HPReplyData> data) {
        this.context = context;
        this.data = data;

    }

    public void setGoodsOwner(UserData goodsOwner) {
        this.goodsOwner = goodsOwner;
    }

    public void setGoodsData(GoodsData goods) {
        this.goods = goods;
    }

    public void setData(List<HPReplyData> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<HPReplyData> data) {
        if (data == null) {
            data = new ArrayList<HPReplyData>();
        }

        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }


    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentHolder(LayoutInflater.from(context).
                inflate(R.layout.item_hp_reply_list, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, final int position) {
        final HPReplyData item = data.get(position);
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
        if (goodsOwner != null) {
            if (goodsOwner.getUserId() == item.getUser().getUserId()) {
                holder.tvCommentTag.setVisibility(View.VISIBLE);
            } else {
                holder.tvCommentTag.setVisibility(View.GONE);
            }
        }
        //姓名及其图标
        holder.ctvName.setData(item.getUser());
        final boolean isLiked = item.getReply().getLiked();
        if (isLiked) {
            holder.tvLike.setSelected(true);
        } else {
            holder.tvLike.setSelected(false);
        }
        if (item.getReply().getLikeCount() == 0) {
            holder.tvLike.setText("");
        } else {
            holder.tvLike.setText(CommonUtils.getIntegerCount(item.getReply().getLikeCount(), MyConstants.COUNT_COMMENT));
        }
        holder.tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(item.getReply().getCreatedAt()));

        holder.hltvContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long userId = item.getUser().getUserId();
                String[] items;
                if (userId == CommonPreference.getUserId() || (goodsOwner != null && goodsOwner.getUserId() == CommonPreference.getUserId())) {
                    items = new String[]{"回复", "复制", "举报", "删除"};
                } else {
                    items = new String[]{"回复", "复制", "举报"};
                }
                final String title = item.getUser().getUserName() + ":" + item.getReply().getContent();

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
                                intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "5");
                                intent.putExtra(MyConstants.EXTRA_HPREPLY_DATA, item);
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
                                        startRequestForDelReply(item);
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
        });

        if (item.getRelatedUser() == null) {
            holder.hltvContent.setText(item.getReply().getContent());
        } else {
            if (!TextUtils.isEmpty(item.getRelatedUser().getUserName()) && !TextUtils.isEmpty(item.getReply().getContent())) {
                holder.hltvContent.setLinkText(item.getRelatedUser().getUserName(), item.getReply().getContent());
                holder.hltvContent.setOnLinkTextClick(new HLinkTextView.OnLinkTextClick() {
                    @Override
                    public void onClick(String name) {
                        Intent intent = new Intent();
                        intent.setClass(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, item.getRelatedUser().getUserId());
                        intent.putExtra("position", position);
                        if (fragment == null) {
                            ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                        } else {
                            fragment.startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                        }
                    }
                });
            }
        }

        holder.tvLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startRequestForLike(item);
            }
        });
    }

    private void actionToEditComment(HPReplyData item) {
        Intent intent;
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
            if (item != null && item.getReply() != null) {
                intent = new Intent(context, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, item.getReply().getReplyId());
                if (item.getUser() != null && item.getUser().getUserName() != null) {
                    intent.putExtra(MyConstants.USER_NAME, item.getUser().getUserName());
                }
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, 4);
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

    private void startRequestForDelReply(final HPReplyData item) {
        if (!CommonUtils.isNetAvaliable(context)) {
            ToastUtil.toast(context, "请检查网络连接");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
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
            ActionUtil.loginAndToast((Activity) context);
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

    public void setCommentable(int commentable) {
        this.commentable = commentable;
    }

    public class CommentHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public SimpleDraweeView ivHeader;
        public CommonTitleView ctvName;
        public TextView tvLike;
        public TextView tvCommentTime;
        public HLinkTextView hltvContent;
        public TextView tvCommentTag;


        public CommentHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            this.tvCommentTag = (TextView) itemView.findViewById(R.id.tvCommentTag);
            this.ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            this.tvCommentTime = (TextView) itemView.findViewById(R.id.tvCommentTime);
            this.hltvContent = (HLinkTextView) itemView.findViewById(R.id.hltvContent);
            this.tvLike = (TextView) itemView.findViewById(R.id.tvLike);
        }
    }

}
