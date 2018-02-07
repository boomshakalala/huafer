package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.R;
import com.huapu.huafen.adapter.HPReplyAdapter;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.HPComment;
import com.huapu.huafen.beans.HPCommentData;
import com.huapu.huafen.beans.HPReplyData;
import com.huapu.huafen.beans.HPReplyResult;
import com.huapu.huafen.beans.Sale;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.MultiItemDialog;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.wrapper.LoadMoreWrapper;
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
import com.huapu.huafen.views.DashLineView;
import com.huapu.huafen.views.HLoadingStateView;
import com.huapu.huafen.views.PullToRefreshRecyclerView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 商品留言回复列表
 */
@Deprecated
public class HPReplyListActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener<RecyclerView>,
        LoadMoreWrapper.OnLoadMoreListener {

    private PullToRefreshRecyclerView ptrRecyclerReply;
    private HLoadingStateView loadingStateView;
    private HPReplyAdapter adapter;
    private TextView tvEditReply;
    private int page = 0;
    private long targetId;
    private int sortType = 1;
    private View loadMoreLayout;
    private View headerCommentView;
    private View headerGoodsView;
    private View headerSortView;
    private SimpleDraweeView ivGoodsPic;
    private DashLineView dlvGoodsName;
    private TextView tvPrice;
    private TextView tvBtnGoods;
    private TextView tvHot;
    private TextView tvTime;
    private TextView tvCommentTag;
    private SimpleDraweeView ivHeader;
    private CommonTitleView ctvName;
    private ImageView ivReply;
    private TextView tvLike;
    private TextView tvCommentTime;
    private TextView tvContent;
    private HPComment comment;
    private GoodsData goods;
    private UserData user;
    private HPReplyResult result;
    private int commentable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hp_reply_list);
        if (getIntent().hasExtra(MyConstants.EXTRA_COMMENT_TARGET_ID)) {
            targetId = getIntent().getLongExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, 0);
        }

        initView();
        startLoading();
    }

    private void initView() {
        setTitleString("回复");
        loadingStateView = (HLoadingStateView) findViewById(R.id.loadingStateView);
        ptrRecyclerReply = (PullToRefreshRecyclerView) findViewById(R.id.ptrRecyclerReply);
        CommonUtils.buildPtr(ptrRecyclerReply);
        tvEditReply = (TextView) findViewById(R.id.tvEditReply);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ptrRecyclerReply.getRefreshableView().setLayoutManager(linearLayoutManager);

        LayoutInflater inflater = LayoutInflater.from(this);
        loadMoreLayout = inflater.inflate(R.layout.load_layout,
                ptrRecyclerReply.getRefreshableView(), false);

        View viewEmpty = inflater.inflate(R.layout.view_empty_image, ptrRecyclerReply, false);

        ImageView ivEmpty = (ImageView) viewEmpty.findViewById(R.id.ivEmpty);
        ivEmpty.setImageResource(R.drawable.empty_goods_reply);
        adapter = new HPReplyAdapter(this);
        adapter.setEmptyView(viewEmpty);
        ptrRecyclerReply.setAdapter(adapter.getWrapperAdapter());
        ptrRecyclerReply.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);
        tvEditReply.setOnClickListener(this);
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
     */
    private void startRequestForReplyList(final String extra) {
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            return;
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("targetId", String.valueOf(targetId));
        params.put("sortType", String.valueOf(sortType)); // 1时间，2热度
        params.put("page", page + "");
        LogUtil.i("liang", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.REPLYLIST, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecyclerReply.onRefreshComplete();
                }
            }

            @Override
            public void onResponse(String response) {
                if (extra.equals(LOADING)) {
                    loadingStateView.setStateShown(HLoadingStateView.State.COMPLETE);
                } else if (extra.equals(REFRESH)) {
                    ptrRecyclerReply.onRefreshComplete();
                }
                try {
                    LogUtil.i("liang", "回复:" + response);
                    JsonValidator validator = new JsonValidator();
                    boolean isJson = validator.validate(response);
                    if (!isJson) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        result = JSON.parseObject(baseResult.obj, HPReplyResult.class);
                        initData(result, extra);
                    } else {
                        CommonUtils.error(baseResult, HPReplyListActivity.this, "");
                    }
                } catch (Exception e) {
                    ProgressDialog.closeProgress();
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData(final HPReplyResult result, String extra) {
        if (result == null) {
            return;
        }
        List<HPReplyData> replies = result.getReplies();
        if (result.getPage() == 0) {
            adapter.setLoadMoreView(null);
        } else {
            adapter.setLoadMoreView(loadMoreLayout);
        }
        page++;

        if (extra.equals(LOADING) || extra.equals(REFRESH)) {
            dealHPReplyResult(result);
            adapter.setData(replies);
        } else if (LOAD_MORE.equals(extra)) {
            if (replies == null) {
                replies = new ArrayList<>();
            }
            adapter.addAll(replies);
        }

    }

    private void dealHPReplyResult(final HPReplyResult result) {
        comment = result.getComment();
        user = result.getUser();
        goods = result.getGoods();
        UserData goodsOwner = result.getGoodsOwner();
        if (goodsOwner != null) {
            adapter.setGoodsOwner(goodsOwner);
        }
        if (user != null) {
            String userName = user.getUserName();
            if (!TextUtils.isEmpty(userName)) {
                tvEditReply.setHint("回复 " + userName);
            }
        }
        if (goods != null) {
            if (headerGoodsView == null) {
                headerGoodsView = LayoutInflater.from(this).inflate(R.layout.view_header_comment_goods, ptrRecyclerReply.getRefreshableView(), false);
                adapter.addHeaderView(headerGoodsView);
                ivGoodsPic = (SimpleDraweeView) headerGoodsView.findViewById(R.id.ivGoodsPic);
                dlvGoodsName = (DashLineView) headerGoodsView.findViewById(R.id.dlvGoodsName);
                tvPrice = (TextView) headerGoodsView.findViewById(R.id.tvPrice);
                tvBtnGoods = (TextView) headerGoodsView.findViewById(R.id.tvBtnGoods);
                tvBtnGoods.setOnClickListener(HPReplyListActivity.this);
            }

            if (!ArrayUtil.isEmpty(goods.getGoodsImgs())) {
                ImageLoader.resizeSmall(ivGoodsPic, goods.getGoodsImgs().get(0), 1);
            }

            dlvGoodsName.setData(goods.getBrand(), goods.getName());
            CommonUtils.setPriceSizeData(tvPrice, "", goods.getPrice());
            adapter.setGoodsData(goods);
        }
        if (comment != null) {
            if (headerCommentView == null) {
                headerCommentView = LayoutInflater.from(this).inflate(R.layout.view_header_reply_comment, ptrRecyclerReply.getRefreshableView(), false);
                adapter.addHeaderView(headerCommentView);
                ivHeader = (SimpleDraweeView) headerCommentView.findViewById(R.id.ivHeader);
                ctvName = (CommonTitleView) headerCommentView.findViewById(R.id.ctvName);
                ivReply = (ImageView) headerCommentView.findViewById(R.id.ivReply);
                tvLike = (TextView) headerCommentView.findViewById(R.id.tvLike);
                tvCommentTag = (TextView) headerCommentView.findViewById(R.id.tvCommentTag);
                tvCommentTime = (TextView) headerCommentView.findViewById(R.id.tvCommentTime);
                tvContent = (TextView) headerCommentView.findViewById(R.id.tvContent);
            }

            ImageLoader.resizeSmall(ivHeader, user.getAvatarUrl(), 1);

            ctvName.setData(user);
            final boolean isLiked = comment.getLiked();
            if (isLiked) {
                tvLike.setSelected(true);
            } else {
                tvLike.setSelected(false);
            }
            if (comment.getLikeCount() == 0) {
                tvLike.setText("");
            } else {
                tvLike.setText(CommonUtils.getIntegerCount(comment.getLikeCount(), MyConstants.COUNT_COMMENT));
            }
            if (goodsOwner != null) {
                if (goodsOwner.getUserId() == user.getUserId()) {
                    tvCommentTag.setVisibility(View.VISIBLE);
                } else {
                    tvCommentTag.setVisibility(View.GONE);
                }
            }
            tvCommentTime.setText(DateTimeUtils.getMonthDayHourMinute(comment.getCreatedAt()));
            tvContent.setText(comment.getContent());
            commentable = result.getCommentable();
            ivReply.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (goods != null) {
                        boolean isComment = CommonUtils.checkCommentCommit(goods.getGoodsState(), goods.getAuditStatus());
                        if (isComment) {
                            actionToEditComment();
                        } else {
                            CommonUtils.commentClickFailed(HPReplyListActivity.this);
                        }
                    }
                }
            });
            ivHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(HPReplyListActivity.this, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, user.getUserId());
                    startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                }
            });
            tvContent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    long userId = result.getUser().getUserId();
                    String[] items;
                    if (userId == CommonPreference.getUserId()
//								||(goodsOwner!=null&&goodsOwner.getUserId() ==CommonPreference.getUserId())
                            ) {
                        items = new String[]{"回复", "复制", "举报", "删除"};
                    } else {
                        items = new String[]{"回复", "复制", "举报"};
                    }
                    final String title = result.getUser().getUserName() + ":" + result.getComment().getContent();

                    MultiItemDialog dialog = new MultiItemDialog(HPReplyListActivity.this, title, items, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent;
                            switch (which) {
                                case 0://回复
                                    if (goods != null) {
                                        boolean isComment = CommonUtils.checkCommentCommit(goods.getGoodsState(), goods.getAuditStatus());
                                        if (isComment) {
                                            actionToEditComment();
                                        } else {
                                            CommonUtils.commentClickFailed(HPReplyListActivity.this);
                                        }
                                    } else {
                                        actionToEditComment();
                                    }
                                    break;
                                case 1://复制
                                    CommonUtils.copy(HPReplyListActivity.this, title);
                                    break;
                                case 2://举报
                                    intent = new Intent(HPReplyListActivity.this, ReportActivity.class);
                                    intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, "4");
                                    if (result != null) {
                                        HPCommentData hpCommentData = new HPCommentData();
                                        hpCommentData.setComment(result.getComment());
                                        hpCommentData.setUser(result.getUser());
                                        intent.putExtra(MyConstants.EXTRA_HPCOMMENT_DATA, hpCommentData);
                                    }
                                    HPReplyListActivity.this.startActivity(intent);
                                    break;
                                case 3://删除
                                    final TextDialog textDialog = new TextDialog(HPReplyListActivity.this, true);
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
            });
            tvLike.setOnClickListener(HPReplyListActivity.this);
        }
        ArrayList<HPReplyData> replies = result.getReplies();
        if (!ArrayUtil.isEmpty(replies)) {
            if (headerSortView == null) {
                headerSortView = LayoutInflater.from(this).inflate(R.layout.view_header_comment_sort, ptrRecyclerReply.getRefreshableView(), false);
                adapter.addHeaderView(headerSortView);
                tvHot = (TextView) headerSortView.findViewById(R.id.tvHot);
                tvTime = (TextView) headerSortView.findViewById(R.id.tvTime);
                tvHot.setOnClickListener(HPReplyListActivity.this);
                tvTime.setOnClickListener(HPReplyListActivity.this);
                tvHot.performClick();
            }
            initSortText();
        }
        adapter.setCommentable(commentable);
    }


    private void startRequestForDelComment(final HPReplyResult item) {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
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
                        onBackPressed();
                    } else {
                        CommonUtils.error(baseResult, HPReplyListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initSortText() {
        if (sortType == 1) {
            tvTime.setTextColor(getResources().getColor(R.color.base_pink));
            tvHot.setTextColor(getResources().getColor(R.color.text_color_gray));
        } else {
            tvHot.setTextColor(getResources().getColor(R.color.base_pink));
            tvTime.setTextColor(getResources().getColor(R.color.text_color_gray));
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


    private void startRequestForLike(final HPComment data) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(this);
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
                                CommonUtils.error(baseResult, HPReplyListActivity.this, "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBtnGoods:
                if (goods != null) {
                    Intent intent = new Intent(this, GoodsDetailsActivity.class);
                    intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goods.getGoodsId()));
                    startActivity(intent);
                }
                break;
            case R.id.tvHot:
                sortType = 2;
                refresh();
                break;

            case R.id.tvTime:
                sortType = 1;
                refresh();
                break;
            case R.id.tvEditReply:
                if (ConfigUtil.isToVerify()) {
                    Intent intent = new Intent(this, VerifiedActivity.class);
                    startActivity(intent);
                } else {
                    if (goods != null) {
                        boolean isComment = CommonUtils.checkCommentCommit(goods.getGoodsState(), goods.getAuditStatus());
                        if (isComment) {
                            actionToEditComment();
                        } else {
                            CommonUtils.commentClickFailed(this);
                        }
                    } else {
                        actionToEditComment();
                    }
                }
                break;
            case R.id.tvLike:
                startRequestForLike(comment);
                break;
        }
    }

    private void actionToEditComment() {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(this);
            return;
        }

        if (commentable == 0) {//不能留言
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
            if (comment != null && user != null) {
                Intent intent = new Intent(this, ReplyCommitActivity.class);
                intent.putExtra(MyConstants.Comment.TARGET_ID, comment.getCommentId());
                intent.putExtra(MyConstants.USER_NAME, user.getUserName());
                intent.putExtra(MyConstants.Comment.TARGET_TYPE, 3);
                startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
            }
        } else {
            final TextDialog dialogText = new TextDialog(HPReplyListActivity.this, true);
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
                        intent.setClass(HPReplyListActivity.this, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);

                    } else {
                        CommonUtils.error(baseResult, HPReplyListActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }
}
