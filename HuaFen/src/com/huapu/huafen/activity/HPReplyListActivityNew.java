package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ReplyAdapter;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.HPReplyData;
import com.huapu.huafen.beans.ReplyListResult;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.utils.ViewUtil;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PtrDefaultFrameLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

import static com.alibaba.mtl.log.a.getContext;

/**
 * 留言回复列表
 */
public class HPReplyListActivityNew extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    public static final String TAG = "HPReplyListActivityNew";

    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.ptrFrameLayout)
    PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R2.id.tvEditReply)
    TextView tvEditReply;
    @BindView(R2.id.loadingStateView)
    HLoadingStateView loadingStateView;
    private ReplyAdapter adapter;
    private long targetId;
    private int page;
    private int sortType = 2;
    private View loadMoreLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hp_reply_list_new);
        Intent intent = getIntent();
        // 从 LCIMMessageHandler 经过 PendingIntent，target 的类型会变成 String，此处做兼容
        if (intent.hasExtra(MyConstants.EXTRA_COMMENT_TARGET_ID)) {
            Object obj = intent.getExtras().get(MyConstants.EXTRA_COMMENT_TARGET_ID);
            if (obj instanceof String) {
                targetId = Long.valueOf((String) obj);
            } else if (obj instanceof Long) {
                targetId = intent.getLongExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, 0);
            } else {
                LogUtil.e(TAG, "onCreate: target 类型错误");
                targetId = 0;
            }
        }

        initView();
        startLoading();
    }


    private void initView() {
        setTitleString("回复");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        adapter = new ReplyAdapter(this);
        recyclerView.setAdapter(adapter.getWrapperAdapter());
        adapter.setOnLoadMoreListener(this);
        adapter.setOnSortChangedListener(new ReplyAdapter.OnSortChangedListener() {

            @Override
            public void onSortChanged(int sort) {
                sortType = sort;
                refresh();
            }
        });

        ptrFrameLayout.buildPtr(new PtrHandler() {

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                boolean flag = PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int index = manager.findFirstCompletelyVisibleItemPosition();
                return index == 0 && flag;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refresh();
            }
        });
        tvEditReply.setOnClickListener(this);

        ViewUtil.setOffItemAnimator(recyclerView);
    }

    @Override
    public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {//下拉刷新
        refresh();
    }

    @Override
    public void onLoadMoreRequested() {//加载更多
        loadMore();
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        page = 0;
        startRequestForReplyList(LOADING);
    }

    private void refresh() {
        page = 0;
        startRequestForReplyList(REFRESH);
    }

    private void loadMore() {
        startRequestForReplyList(LOAD_MORE);
    }

    /**
     * 获取回复
     *
     * @param
     */
    private void startRequestForReplyList(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("commentId", String.valueOf(targetId));
        params.put("sortType", String.valueOf(sortType)); // 1时间，2热度
        params.put("page", page + "");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.REPLYLIST, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                }
                try {
                    ReplyListResult result = JSON.parseObject(response, ReplyListResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initData(result, extra);
                    } else {
                        CommonUtils.error(result, HPReplyListActivityNew.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }

        });
    }

    private void actionToEditComment(ReplyListResult result) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(this);
            return;
        }

        if (result == null || result.obj == null) {
            return;
        }

        if (result.obj.commentable == 0) {//不能留言
            toast(getString(R.string.commentable_0));
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
            if (result.obj.comment != null && result.obj.user != null) {
                Intent intent = new Intent(this, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, result.obj.comment.getCommentId());
                intent.putExtra(MyConstants.USER_NAME, result.obj.user.getUserName());
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, 3);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
            }
        } else {
            final TextDialog dialogText = new TextDialog(this, true);
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
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<String, String>();

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new StringCallback() {

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
                        intent.setClass(HPReplyListActivityNew.this, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);
                    } else {
                        CommonUtils.error(baseResult, HPReplyListActivityNew.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(final ReplyListResult result, String extra) {
        if (result == null || result.obj == null) {
            return;
        }
        List<HPReplyData> replies = result.obj.replies;
        if (result.obj.page == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }
        page++;

        if (extra.equals(LOADING) || extra.equals(REFRESH)) {
            adapter.setData(result);
            tvEditReply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (!CommonPreference.isLogin()) {
                        ActionUtil.loginAndToast(getContext());
                        return;
                    }

                    if (ConfigUtil.isToVerify()) {
                        final TextDialog dialog = new TextDialog(HPReplyListActivityNew.this, false);
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
                                Intent intent = new Intent(HPReplyListActivityNew.this, VerifiedActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    } else {
                        replyPerformClick(result);
                    }
                }
            });
            if (result.obj.user != null) {
                String userName = result.obj.user.getUserName();
                if (!TextUtils.isEmpty(userName)) {
                    tvEditReply.setHint("回复 " + userName);
                }
            }
        } else if (LOAD_MORE.equals(extra)) {
            if (replies == null) {
                replies = new ArrayList<>();
            }
            adapter.addAll(replies);
        }

    }

    private void replyPerformClick(ReplyListResult result) {
        if (result == null || result.obj == null) {
            return;
        }
        if ("goods".equals(result.obj.item.itemType)) {
            ArticleAndGoods goods = result.obj.item.item;

            boolean isComment = CommonUtils.checkCommentCommit(goods.goodsState, goods.auditStatus);
            if (isComment) {
                actionToEditComment(result);
            } else {
                CommonUtils.commentClickFailed(this);
            }
        } else {
            actionToEditComment(result);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MyConstants.REQUEST_CODE_COMMENT) {
                refresh();
            }
        }
    }

}
