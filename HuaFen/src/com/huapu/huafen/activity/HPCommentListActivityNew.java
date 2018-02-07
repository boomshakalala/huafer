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
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.CommentAdapter;
import com.huapu.huafen.beans.ArticleAndGoods;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CommentListResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.HPCommentData;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
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
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
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

/**
 * 留言列表页
 */
public class HPCommentListActivityNew extends BaseActivity implements LoadMoreWrapper.OnLoadMoreListener {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.ptrFrameLayout) PtrDefaultFrameLayout ptrFrameLayout;
    @BindView(R.id.loadingStateView) HLoadingStateView loadingStateView;
    @BindView(R.id.tvEditReply) TextView tvEditReply;
    private CommentAdapter adapter;
    private View loadMoreLayout;
    private long targetId;
    private int targetType;
    private int page;
    private int sortType = 2;
    private String hint;
    private String recTranceId;
    private int recIndex;
    private String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hp_comment_list_new);

        if (getIntent().hasExtra(MyConstants.EXTRA_COMMENT_TARGET_ID)) {
            targetId = getIntent().getLongExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, 0);
        }
        if (getIntent().hasExtra(MyConstants.EXTRA_COMMENT_TARGET_TYPE)) {
            targetType = getIntent().getIntExtra(MyConstants.EXTRA_COMMENT_TARGET_TYPE, 0);
        }

        initView();
        startLoading();
    }


    private void initView() {
        setTitleString("留言");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadMoreLayout = LayoutInflater.from(this).inflate(R.layout.load_layout, recyclerView, false);
        adapter = new CommentAdapter(this);
        adapter.setTargetType(targetType);
        adapter.setOnLoadMoreListener(this);
        adapter.setOnSortChangedListener(new CommentAdapter.OnSortChangedListener() {

            @Override
            public void onSortChanged(int sort) {
                sortType = sort;
                refresh();
            }
        });
        recyclerView.setAdapter(adapter.getWrapperAdapter());

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


        if (targetType == MyConstants.Comment.TARGET_GOODS) {
            hint = "对这件商品感兴趣";
        } else if (targetType == MyConstants.Comment.TARGET_ARTICLE) {
            hint = "对这篇花语留言";
        } else if (targetType == MyConstants.Comment.TARGET_ARTICLE_ESAY) {
            hint = "对这篇花语留言";
        }else{
            hint="我来说一句...";
        }
        tvEditReply.setHint(hint);
    }


    @Override
    public void onLoadMoreRequested() {//加载更多
        loadMore();
    }

    private void startLoading() {
        loadingStateView.setStateShown(HLoadingStateView.State.LOADING);
        page = 0;
        startRequestForCommentList(LOADING);
    }

    private void refresh() {
        page = 0;
        startRequestForCommentList(REFRESH);
    }

    private void loadMore() {
        startRequestForCommentList(LOAD_MORE);
    }

    /**
     * 获取留言列表
     *
     * @param
     */
    private void startRequestForCommentList(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("targetId", String.valueOf(targetId));
        params.put("targetType", String.valueOf(targetType));
        params.put("sortType", String.valueOf(sortType)); // 1时间，2热度
        params.put("page", page + "");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.COMMENTLIST, params, new StringCallback() {

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
                Logger.e("get response:" + response);
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrFrameLayout.refreshComplete();
                }

                try {
                    CommentListResult result = JSON.parseObject(response, CommentListResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        initData(result, extra);
                    } else {
                        CommonUtils.error(result, HPCommentListActivityNew.this, "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private void initData(final CommentListResult result, String extra) {
        if (result == null || result.obj == null) {
            return;
        }
        List<HPCommentData> commentList = result.obj.comments;
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
                        ActionUtil.loginAndToast(HPCommentListActivityNew.this);
                        return;
                    }

                    if(ConfigUtil.isToVerify()){
                        final TextDialog dialog = new TextDialog(HPCommentListActivityNew.this, false);
                        dialog.setContentText("亲，您还未开通实名认证，1分钟完成认证后即可留言");
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
                                Intent intent = new Intent(HPCommentListActivityNew.this, VerifiedActivity.class);
                                startActivity(intent);
                            }
                        });
                        dialog.show();
                    }else {
                        replyPerformClick(result);
                    }
                }
            });
        } else if (LOAD_MORE.equals(extra)) {
            if (commentList == null) {
                commentList = new ArrayList<>();
            }
            adapter.addAll(commentList);
        }
    }

    private void replyPerformClick(CommentListResult result) {
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }


    private void actionToEditComment(CommentListResult result) {
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
            Intent intent = new Intent(this, CommentCommitActivity.class);
            intent.putExtra(MyConstants.Comment.TARGET_ID, targetId);
            intent.putExtra(MyConstants.Comment.TARGET_TYPE, targetType);
            if (!TextUtils.isEmpty(hint)) {
                intent.putExtra(MyConstants.EXTRA_MOMENT_HINT, hint);
            }
            intent.putExtra(MyConstants.REC_TRAC_ID,recTranceId);
            intent.putExtra(MyConstants.POSITION,recIndex);
            intent.putExtra(MyConstants.SEARCH_QUERY,searchQuery);
            startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
        } else {
            DialogManager.toVerify(HPCommentListActivityNew.this);
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
                        intent.setClass(HPCommentListActivityNew.this, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);
                    } else {
                        CommonUtils.error(baseResult, HPCommentListActivityNew.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
