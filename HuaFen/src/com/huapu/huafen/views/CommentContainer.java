package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.CommentCommitActivity;
import com.huapu.huafen.activity.HPCommentListActivityNew;
import com.huapu.huafen.activity.WebViewActivity;
import com.huapu.huafen.adapter.HPCommentAdapter;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.EasyArticleDetail;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.HPCommentsResult;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.layoutmanager.LinearLayoutManagerPlus;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 留言
 * Created by admin on 2017/5/6.
 */
public class CommentContainer extends LinearLayout {

    @BindView(R2.id.tvLeaveMsg)
    TextView tvLeaveMsg;
    @BindView(R2.id.tvToView)
    TextView tvToView;
    @BindView(R2.id.llCommentList)
    LinearLayout llCommentList;
    @BindView(R2.id.ivBtnComment)
    ImageView ivBtnComment;
    @BindView(R2.id.commentList)
    RecyclerView commentList;
    private TextView tvMessageCount;
    private HPCommentAdapter adapter;
    private int targetType;
    private long targetId;
    private int commentAble;
    private HPCommentAdapter.OnHandleHPCommentAdapterListener onHandleHPCommentAdapterListener;
    private String recTraceId;
    private int recIndex;
    private String searchQuery;
    private Context context;


    public void setRecTraceId(String recTraceId) {
        this.recTraceId = recTraceId;
    }


    public void setRecIndex(int recIndex) {
        this.recIndex = recIndex;
    }


    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public CommentContainer(Context context) {
        this(context, null);
    }

    public CommentContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setCommentAble(int commentAble) {
        this.commentAble = commentAble;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public void setTargetType(int targetType) {
        this.targetType = targetType;
    }

    private void initView() {
        context = getContext();
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.comment_container, this, true);
        ButterKnife.bind(this);

        LinearLayoutManagerPlus linearLayoutManager = new LinearLayoutManagerPlus(context, LinearLayoutManager.VERTICAL, false);
        commentList.setLayoutManager(linearLayoutManager);
        adapter = new HPCommentAdapter(context);
        commentList.setAdapter(adapter.getWrapperAdapter());

        View footerView = LayoutInflater.from(context).inflate(R.layout.goods_detail_footer, commentList, false);
        tvMessageCount = (TextView) footerView.findViewById(R.id.tvMessageCount);
        tvMessageCount.setText("查看全部留言");
        tvMessageCount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HPCommentListActivityNew.class);
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, targetId);
                intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_TYPE, targetType);
                intent.putExtra(MyConstants.EXTRA_FROM_GOODS_DETAILS, false);
                intent.putExtra(MyConstants.REC_TRAC_ID, recTraceId);
                intent.putExtra(MyConstants.POSITION, recIndex);
                intent.putExtra(MyConstants.SEARCH_QUERY, searchQuery);
                ((Activity) context).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
            }
        });
        adapter.addFootView(footerView);
        adapter.setOnHandleHPCommentAdapterListener(new HPCommentAdapter.OnHandleHPCommentAdapterListener() {

            @Override
            public void onDelete() {
                if (onHandleHPCommentAdapterListener != null) {
                    onHandleHPCommentAdapterListener.onDelete();
                }
            }
        });
    }

    public void setOnHandleHPCommentAdapterListener(HPCommentAdapter.OnHandleHPCommentAdapterListener onHandleHPCommentAdapterListener) {
        this.onHandleHPCommentAdapterListener = onHandleHPCommentAdapterListener;
    }

    public void setData(final HPCommentsResult result, ArticleDetailResult.Count count) {
        if (result == null || result.getCount() == 0) {//没有留言
            llCommentList.setVisibility(GONE);
            ivBtnComment.setVisibility(VISIBLE);
            tvMessageCount.setVisibility(INVISIBLE);

            String countDesc = CommonUtils.getIntegerCount(0, MyConstants.COUNT_COMMENT);
            String desc = String.format(getContext().getString(R.string.msg_count), countDesc);
            tvLeaveMsg.setText(desc);

            ivBtnComment.setVisibility(View.VISIBLE);
            ivBtnComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionToEditComment();
                }
            });
            adapter.setData(null);
        } else {
            llCommentList.setVisibility(VISIBLE);
            ivBtnComment.setVisibility(GONE);

            int replyCount = 0;
            try {
                replyCount = Integer.parseInt(count.reply);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            int msgCount = result.getCount() + replyCount;

            if (result.getCount() > 3) {
                tvMessageCount.setVisibility(VISIBLE);
            } else {
                tvMessageCount.setVisibility(INVISIBLE);
            }

            String countDesc = CommonUtils.getIntegerCount(msgCount, MyConstants.COUNT_COMMENT);
            String desc = String.format(getContext().getString(R.string.msg_count), countDesc);
            tvLeaveMsg.setText(desc);
            ivBtnComment.setVisibility(View.GONE);
            adapter.setData(result.getComments());
        }

        tvToView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getContext());
                    return;
                }

                actionToEditComment();
            }
        });
        adapter.setCommentable(commentAble);
        adapter.setTargetType(targetType);
    }

    public void setData(final HPCommentsResult result, EasyArticleDetail.ObjBean.CountBean count) {
        if (result == null || result.getCount() == 0) {//没有留言
            llCommentList.setVisibility(GONE);
            ivBtnComment.setVisibility(VISIBLE);
            tvMessageCount.setVisibility(INVISIBLE);

            String countDesc = CommonUtils.getIntegerCount(0, MyConstants.COUNT_COMMENT);
            String desc = String.format(getContext().getString(R.string.msg_count), countDesc);
            tvLeaveMsg.setText(desc);

            ivBtnComment.setVisibility(View.VISIBLE);
            ivBtnComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionToEditComment();
                }
            });
            adapter.setData(null);
        } else {
            llCommentList.setVisibility(VISIBLE);
            ivBtnComment.setVisibility(GONE);

            int replyCount = 0;
            try {
                replyCount = Integer.parseInt(count.reply);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            int msgCount = result.getCount() + replyCount;

            if (result.getCount() > 3) {
                tvMessageCount.setVisibility(VISIBLE);
            } else {
                tvMessageCount.setVisibility(INVISIBLE);
            }

            String countDesc = CommonUtils.getIntegerCount(msgCount, MyConstants.COUNT_COMMENT);
            String desc = String.format(getContext().getString(R.string.msg_count), countDesc);
            tvLeaveMsg.setText(desc);
            ivBtnComment.setVisibility(View.GONE);
            adapter.setData(result.getComments());
        }

        tvToView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionToEditComment();
            }
        });
        adapter.setCommentable(commentAble);
        adapter.setTargetType(targetType);
    }

    public void setData(final HPCommentsResult result, final GoodsInfo goodsInfo) {
        if (result == null || result.getCount() == 0) {//没有留言
            llCommentList.setVisibility(GONE);
            ivBtnComment.setVisibility(VISIBLE);
            tvMessageCount.setVisibility(INVISIBLE);

            String countDesc = CommonUtils.getIntegerCount(0, MyConstants.COUNT_COMMENT);
            String desc = String.format(getContext().getString(R.string.msg_count), countDesc);
            tvLeaveMsg.setText(desc);

            ivBtnComment.setVisibility(View.VISIBLE);
            ivBtnComment.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 留言
                    if (!canComment() || goodsInfo == null)
                        return;

                    boolean isComment = CommonUtils.checkCommentCommit(goodsInfo.getGoodsState(), goodsInfo.getAuditStatus());
                    if (isComment) {
                        toComment();
                    } else {
                        CommonUtils.commentClickFailed(getContext());
                    }
                }
            });
            adapter.setData(null);
        } else {
            llCommentList.setVisibility(VISIBLE);
            ivBtnComment.setVisibility(GONE);

            int msgCount = result.getCount() + result.replyCount;

            if (result.getCount() > 3) {
                tvMessageCount.setVisibility(VISIBLE);
            } else {
                tvMessageCount.setVisibility(INVISIBLE);
            }

            String countDesc = CommonUtils.getIntegerCount(msgCount, MyConstants.COUNT_COMMENT);
            String desc = String.format(getContext().getString(R.string.msg_count), countDesc);
            tvLeaveMsg.setText(desc);
            ivBtnComment.setVisibility(View.GONE);
            adapter.setData(result.getComments());
        }

        tvToView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CommonPreference.isLogin()) {
                    ActionUtil.loginAndToast(getContext());
                    return;
                }

                actionToEditComment();
            }
        });
        adapter.setCommentable(commentAble);
        adapter.setTargetType(targetType);
    }

    // 留言
    private void actionToEditComment() {
        if (canComment()) {
            toComment();
        }
    }

    private void toComment() {
        Intent intent = new Intent(getContext(), CommentCommitActivity.class);
        intent.putExtra(MyConstants.Comment.TARGET_ID, targetId);
        intent.putExtra(MyConstants.Comment.TARGET_TYPE, targetType);
        intent.putExtra(MyConstants.EXTRA_MOMENT_HINT, "对这篇花语留言");
        intent.putExtra(MyConstants.REC_TRAC_ID, recTraceId);
        intent.putExtra(MyConstants.POSITION, recIndex);
        intent.putExtra(MyConstants.SEARCH_QUERY, searchQuery);
        ((Activity) getContext()).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
    }

    private boolean canComment() {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(getContext());
            return false;
        }
        if (commentAble == 0) {
            ToastUtil.toast(getContext(), getContext().getResources().getString(R.string.commentable_0));
            return false;
        }
        int zmPoint = CommonPreference.getIntValue(CommonPreference.USER_ZM_CREDIT_POINT, 0);
        int level = CommonPreference.getIntValue(CommonPreference.USER_LEVEL, 0);
        Sale sale = CommonPreference.getSale();
        String idAuthRequired;
        String idAuthRequiredArr[] = new String[0];

        if (sale != null) {
            idAuthRequired = sale.getIdAuthRequiredFor();
            idAuthRequiredArr = idAuthRequired.split(",");
        }

        if (ConfigUtil.isToVerify()) {
            DialogManager.toVerify(getContext());
            return false;
        }

        return true;
    }

    private void doRequestForCredit() {
        if (!CommonUtils.isNetAvaliable(getContext())) {
            ToastUtil.toast(getContext(), "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(getContext());
        HashMap<String, String> params = new HashMap<>();

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditInfo info = JSON.parseObject(baseResult.obj, CreditInfo.class);
                        String url = info.getCredential();
                        Intent intent = new Intent();
                        intent.setClass(getContext(), WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        ((Activity) getContext()).startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);
                    } else {
                        CommonUtils.error(baseResult, (Activity) getContext(), "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setGoodsOwnerId(long goodsOwnerId) {
        adapter.setGoodsOwnerId(goodsOwnerId);
    }
}
