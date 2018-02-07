package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.adapter.ArticleDetailAdapter;
import com.huapu.huafen.adapter.HPCommentAdapter;
import com.huapu.huafen.adapter.RecArticleAdapter;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.chatim.IMUtils;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.events.RefreshEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ShareHelper;
import com.huapu.huafen.views.ArticleBottomOperateLayout;
import com.huapu.huafen.views.CommentContainer;
import com.huapu.huafen.views.TitleBarNew;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.event.LCIMOfflineMessageCountChangeEvent;
import de.greenrobot.event.EventBus;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.mContext;

/**
 * 图文花语
 * Created by admin on 2017/4/28.
 */
public class ArticleDetailActivity extends BaseActivity {

    public static final int COMMENT = 100;
    private final static String TAG = ArticleDetailActivity.class.getSimpleName();
    private static final int ARTICLE_TYPE = 6;
    private static final int REQUEST_CODE_FOR_EDIT_ARTICLE = 55;
    @BindView(R2.id.titleBar)
    TitleBarNew titleBar;
    @BindView(R2.id.articles)
    RecyclerView articles;
    @BindView(R2.id.bottomOperateLayout)
    ArticleBottomOperateLayout bottomOperateLayout;
    private ArticleDetailAdapter adapter;
    private String articleId;
    private SimpleDraweeView avatar;
    private TextView tvUserName;
    private CommentContainer commentContainer;
    private TextView tvMsgUnRead, tvMore;
    private PopupWindow morePopupWindow;
    private ArticleDetailResult articleDetailResult;
    private long myUserId;
    private RecyclerView rlArticle;
    private RecArticleAdapter recArticleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_article_detail);
        articleId = mIntent.getStringExtra(MyConstants.ARTICLE_ID);
        initView();
        doRequestForArticleDetail();
    }

    private void initView() {
        myUserId = CommonPreference.getUserId();
        initTitle();
        initContainer();
    }

    private void initContainer() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        articles.setLayoutManager(linearLayoutManager);
        adapter = new ArticleDetailAdapter(this);
        articles.setAdapter(adapter.getWrapperAdapter());

        articles.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lHeight = CommonUtils.getScreenWidth();
                titleBar.setAlphaInRecyclerView(dy, lHeight);
                int findFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition != 0) {
                    titleBar.getCenterLayout().setVisibility(View.VISIBLE);
                } else {
                    titleBar.getCenterLayout().setVisibility(View.GONE);
                }
            }
        });

        View footerView = LayoutInflater.from(this).inflate(R.layout.article_detail_footer, articles, false);
        commentContainer = ButterKnife.findById(footerView, R.id.commentContainer);
        commentContainer.setTargetType(ARTICLE_TYPE);
//        commentContainer.setGoodsOwnerId(myUserId);
        commentContainer.setOnHandleHPCommentAdapterListener(new HPCommentAdapter.OnHandleHPCommentAdapterListener() {

            @Override
            public void onDelete() {
                doRequestForArticleDetail();
            }
        });
        tvMore = ButterKnife.findById(footerView, R.id.tv_more);
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ArticleSquareActivity.class);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
        rlArticle = ButterKnife.findById(footerView, R.id.rl_rec_article);
        LinearLayoutManager llManager = new LinearLayoutManager(this);
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlArticle.setLayoutManager(llManager);
        recArticleAdapter = new RecArticleAdapter(this);
        rlArticle.setAdapter(recArticleAdapter);
        adapter.addFootView(footerView);
    }

    private void initTitle() {
        titleBar.setBackgroundAlpha(0);
        View titleCenterView = LayoutInflater.from(this).inflate(R.layout.article_detail_user, null, false);
        avatar = (SimpleDraweeView) titleCenterView.findViewById(R.id.avatar);
        tvUserName = (TextView) titleCenterView.findViewById(R.id.tvUserName);
        titleBar.setTitle(titleCenterView);
        titleBar.getBtnTitleRight().setImageResource(R.drawable.personal_title_more);
        titleBar.getBtnTitleRight().setVisibility(View.VISIBLE);
        titleBar.getBtnTitleBarRight2().setImageResource(R.drawable.personal_title_share);
        titleBar.getBtnTitleBarRight2().setVisibility(View.VISIBLE);
        titleBar.getBtnTitleLeft().setImageResource(R.drawable.personal_title_back);
    }

    private void initPopMore(View v, boolean isMe) {
        if (morePopupWindow == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            // 引入窗口配置文件
            View view = inflater.inflate(R.layout.pop_more, null);
            View layoutSwitchHome = view.findViewById(R.id.layoutSwitchHome);
            View layoutSwitchMsg = view.findViewById(R.id.layoutSwitchMsg);
            View layoutSwitchEdit = view.findViewById(R.id.layoutSwitchEdit);
            View layoutSwitchDelete = view.findViewById(R.id.layoutSwitchDelete);
            View layoutSwitchMine = view.findViewById(R.id.layoutSwitchMine);
            View layoutSwitchReport = view.findViewById(R.id.layoutSwitchReport);

            if (isMe) {
                layoutSwitchHome.setVisibility(View.GONE);
                layoutSwitchMine.setVisibility(View.GONE);
                layoutSwitchReport.setVisibility(View.GONE);

                layoutSwitchEdit.setVisibility(View.VISIBLE);
                layoutSwitchDelete.setVisibility(View.VISIBLE);
            } else {
                layoutSwitchHome.setVisibility(View.VISIBLE);
                layoutSwitchMine.setVisibility(View.VISIBLE);
                layoutSwitchReport.setVisibility(View.VISIBLE);

                layoutSwitchEdit.setVisibility(View.GONE);
                layoutSwitchDelete.setVisibility(View.GONE);

            }

            tvMsgUnRead = (TextView) view.findViewById(R.id.tvMsgUnRead);
            layoutSwitchHome.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(1);
                    morePopupWindow.dismiss();
                }
            });

            layoutSwitchMsg.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(3);
                    morePopupWindow.dismiss();
                }
            });

            layoutSwitchMine.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    actionToMineFragment(4);
                    morePopupWindow.dismiss();
                }
            });

            layoutSwitchReport.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActionToComplain();
                    morePopupWindow.dismiss();
                }
            });

            layoutSwitchEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (articleDetailResult != null && articleDetailResult.obj != null && articleDetailResult.obj.article != null) {
                        Intent intent = new Intent(ArticleDetailActivity.this, ArticleCoverActivity.class);
                        intent.putExtra(MyConstants.ARTICLE_DATA, articleDetailResult.obj.article);
                        startActivityForResult(intent, REQUEST_CODE_FOR_EDIT_ARTICLE);
                    }
                    morePopupWindow.dismiss();
                }
            });

            layoutSwitchDelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    doRequestForDeleteArticle();
                    morePopupWindow.dismiss();
                }
            });

            morePopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            morePopupWindow.setFocusable(true);
            morePopupWindow.setOutsideTouchable(true);
            morePopupWindow.setBackgroundDrawable(new BitmapDrawable());
            morePopupWindow.setAnimationStyle(R.style.pop_search_switch);
        }
        tvMsgUnRead.setVisibility(titleBar.getMoreBtnBadgeVisibility() ? View.VISIBLE : View.GONE);
        morePopupWindow.showAsDropDown(v);
    }

    private void startActionToComplain() {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(ArticleDetailActivity.this);
            return;
        }
        Intent intent = new Intent(ArticleDetailActivity.this, ReportActivity.class);
        intent.putExtra(MyConstants.EXTRA_REPORT_TYPE, String.valueOf(ARTICLE_TYPE));
        intent.putExtra(MyConstants.EXTRA_USER_ID, articleId);
        startActivity(intent);
    }

    private void actionToMineFragment(int selectFragment) {
        Intent intent = new Intent(ArticleDetailActivity.this, MainActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_WHICH, selectFragment);
        startActivity(intent);
    }

    private void doRequestForArticleDetail() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("articleId", articleId);
        OkHttpClientManager.postAsyn(MyConstants.ARTICLE_DETAIL, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                try {
                    ArticleDetailResult result = JSON.parseObject(response, ArticleDetailResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        LogUtil.e(TAG, response.toString());
                        articleDetailResult = result;
                        initData(articleDetailResult);
                    } else {
                        CommonUtils.error(result, ArticleDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void doRequestForDeleteArticle() {
        HashMap<String, String> params = new HashMap<>();
        params.put("articleId", articleId);
        OkHttpClientManager.postAsyn(MyConstants.ARTICLE_DELETE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                try {
                    ArticleDetailResult result = JSON.parseObject(response, ArticleDetailResult.class);
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {

                        RefreshEvent event = new RefreshEvent();
                        event.refresh = true;
                        EventBus.getDefault().post(event);

                        Intent pickIntent = new Intent(ArticleDetailActivity.this, FlowerNewActivity.class);
                        pickIntent.putExtra(MyConstants.EXTRA_USER_ID, CommonPreference.getUserId());
                        pickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(pickIntent);

                        setResult(RESULT_OK);
                        finish();
                    } else {
                        CommonUtils.error(result, ArticleDetailActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initData(final ArticleDetailResult result) {
        if (result != null && result.obj != null) {
            addTitleAvatarAndShareWithMoreListeners(result);
            initContainerData(result);
            initBottom(result);
        }
    }

    private void initContainerData(ArticleDetailResult result) {
        //花语列表
        adapter.setData(result);
        //留言
        commentContainer.setGoodsOwnerId(result.obj.user.getUserId());
        commentContainer.setCommentAble(result.obj.commentable);
        commentContainer.setTargetId(result.obj.article.articleId);
        commentContainer.setData(result.obj.comment, result.obj.count);
        //推荐花语
        recArticleAdapter.setData(result.obj.recPoems);
    }

    private void addTitleAvatarAndShareWithMoreListeners(final ArticleDetailResult result) {
        if (result.obj.user != null) {
            avatar.setImageURI(result.obj.user.getAvatarUrl());
            if (result.obj.user.getUserName() != null) {
                tvUserName.setText(result.obj.user.getUserName());
            }
        }

        titleBar.setOnRightButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                long userId = 0L;
                if (result.obj.user != null) {
                    userId = result.obj.user.getUserId();
                }
                boolean flag = myUserId > 0 && userId > 0 && myUserId == userId;
                initPopMore(v, flag);
            }
        });
        titleBar.setOnRightButton2ClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (result == null || result.obj == null || result.obj.article == null) {
                    return;
                }
                ShareHelper.shareArticle(ArticleDetailActivity.this, result);
            }
        });
    }

    private void initBottom(ArticleDetailResult result) {
        if (result.obj.article != null && result.obj.count != null) {
            ArrayMap<String, Object> arrayMapBottom = new ArrayMap<>();
            arrayMapBottom.put("momentId", result.obj.article.articleId);
            arrayMapBottom.put("moment_type", ARTICLE_TYPE);
            arrayMapBottom.put("isLiked", result.obj.article.liked);
            arrayMapBottom.put("isCollected", result.obj.article.collected);

            arrayMapBottom.put("save_count", !TextUtils.isEmpty(result.obj.count.collection) ? result.obj.count.collection : "0");
            arrayMapBottom.put("prize_count", !TextUtils.isEmpty(result.obj.count.like) ? result.obj.count.like : "0");
            String commentCount = "0";
            if (!TextUtils.isEmpty(result.obj.count.comment) || !TextUtils.isEmpty(result.obj.count.reply)) {
                int var1 = 0, var2 = 0;
                try {
                    var1 = Integer.parseInt(result.obj.count.comment);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                try {
                    var2 = Integer.parseInt(result.obj.count.reply);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                commentCount = String.valueOf(var1 + var2);
            }
            arrayMapBottom.put("comment_count", commentCount);
            arrayMapBottom.put("isComment", result.obj.commentable);

            bottomOperateLayout.setData(ArticleDetailActivity.this, arrayMapBottom);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == COMMENT) {
                doRequestForArticleDetail();
            } else if (requestCode == MyConstants.REQUEST_CODE_COMMENT) {
                doRequestForArticleDetail();
            } else if (requestCode == PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL) {
                doRequestForArticleDetail();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(LCIMOfflineMessageCountChangeEvent updateEvent) {
        updateUnreadBadge();
    }

    public void onEvent(LCIMIMTypeMessageEvent event) {
        updateUnreadBadge();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUnreadBadge();
    }

    private void updateUnreadBadge() {
        titleBar.showMoreBtnBadge(IMUtils.hasUnread());
    }
}
