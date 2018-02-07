package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.CommentCommitActivity;
import com.huapu.huafen.activity.HPCommentListActivityNew;
import com.huapu.huafen.activity.MomentDetailActivity;
import com.huapu.huafen.activity.VerifiedActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.squareup.okhttp.Request;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by qwe on 2017/4/28.
 */

public class ArticleBottomOperateLayout extends FrameLayout {

    @BindView(R.id.zan)
    TextView zan;
    @BindView(R.id.leaveMessageBottom)
    TextView leaveMessageBottom;
    @BindView(R.id.saveArticle)
    TextView saveArticle;
    private long momentId;

    private int isComment;

    private int momentType;

    private boolean liked;

    private boolean collected;

    private Activity activity;

    private String zanCount;

    private String leaveMessageCount;

    private String saveCount;

    private Activity mActivity;

    public ArticleBottomOperateLayout(@NonNull Context context) {
        super(context);
        this.mActivity = (Activity) context;
        initView();
    }

    public ArticleBottomOperateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mActivity = (Activity) context;
        initView();
    }

    public ArticleBottomOperateLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mActivity = (Activity) context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_bottom_article_detail, this, true);
        ButterKnife.bind(this, view);
    }

    public void setData(Activity activity, ArrayMap<String, Object> arrayMap) {
        this.momentId = (long) arrayMap.get("momentId");
        this.momentType = (int) arrayMap.get("moment_type");
        this.activity = activity;
        this.isComment = (int) arrayMap.get("isComment");
        this.liked = (boolean) arrayMap.get("isLiked");
        this.collected = (boolean) arrayMap.get("isCollected");
        if (null == arrayMap.get("save_count") || TextUtils.isEmpty(String.valueOf(arrayMap.get("save_count"))) || String.valueOf(arrayMap.get("save_count")).contains("null")) {
            this.saveCount = "0";
        } else {
            this.saveCount = (String) arrayMap.get("save_count");
        }

        if (null == arrayMap.get("prize_count") || TextUtils.isEmpty(String.valueOf(arrayMap.get("prize_count"))) || String.valueOf(arrayMap.get("save_count")).contains("null")) {
            this.zanCount = "0";
        } else {
            this.zanCount = (String) arrayMap.get("prize_count");
        }
        this.leaveMessageCount = (String) arrayMap.get("comment_count");
        setView();
    }

    private void setView() {
        if (!TextUtils.isEmpty(leaveMessageCount)) {
            leaveMessageBottom.setText("留言 · " + leaveMessageCount);
        } else {
            leaveMessageBottom.setText("留言 · 0");
        }

        setZanView();
        setSaveView();
    }

    private void setSaveView() {
        if (!TextUtils.isEmpty(saveCount)) {
            saveArticle.setText("收藏 · " + saveCount);
        } else {
            saveArticle.setText("收藏 · ");
        }

        if (collected) {
            hasCollection();
        } else {
            noneCollection();
        }
    }

    private void setZanView() {
        if (!TextUtils.isEmpty(zanCount)) {
            zan.setText("点赞 · " + zanCount);
        } else {
            zan.setText("点赞 · ");
        }

        if (liked) {
            prizeHasLiked();
        } else {
            prizeNoLiked();
        }
    }

    private void hasCollection() {
        Drawable drawable = getResources().getDrawable(R.drawable.btn_item_like_select);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        saveArticle.setCompoundDrawables(drawable, null, null, null);
    }

    private void noneCollection() {
        Drawable drawable = getResources().getDrawable(R.drawable.btn_item_like_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        saveArticle.setCompoundDrawables(drawable, null, null, null);
    }

    private void prizeHasLiked() {
        Drawable drawable = getResources().getDrawable(R.drawable.prize_red);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        zan.setCompoundDrawables(drawable, null, null, null);
    }

    private void prizeNoLiked() {
        Drawable drawable = getResources().getDrawable(R.drawable.prize_grey);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        zan.setCompoundDrawables(drawable, null, null, null);
    }

    @OnClick({R.id.zan, R.id.leaveMessageBottom, R.id.saveArticle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.leaveMessageBottom:
                if (TextUtils.isEmpty(CommonPreference.getAccessToken())) {
                    ActionUtil.loginAndToast(mActivity);
                    return;
                }

                // 检查是否已经认证
                if (ConfigUtil.isToVerify()) {
                    final TextDialog dialog = new TextDialog(getContext(), false);
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
                            Intent intent = new Intent(getContext(), VerifiedActivity.class);
                            getContext().startActivity(intent);

                        }
                    });
                    dialog.show();
                    break;
                }

                if (!TextUtils.isEmpty(leaveMessageCount)) {
                    int value = Integer.valueOf(leaveMessageCount);
                    if (value == 0) {
                        if (isComment == 0) {
                            ToastUtil.toast(activity, activity.getResources().getString(R.string.commentable_0));
                            return;
                        } else {
                            Intent intent = new Intent(activity, CommentCommitActivity.class);
                            intent.putExtra(MyConstants.Comment.TARGET_ID, momentId);
                            intent.putExtra(MyConstants.Comment.TARGET_TYPE, momentType);
                            intent.putExtra(MyConstants.EXTRA_MOMENT_HINT, "对这篇花语留言");
                            activity.startActivityForResult(intent, MomentDetailActivity.COMMENT);
                        }
                    } else {
                        Intent intent = new Intent(getContext(), HPCommentListActivityNew.class);
                        intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_ID, momentId);
                        intent.putExtra(MyConstants.EXTRA_COMMENT_TARGET_TYPE, momentType);
                        intent.putExtra(MyConstants.EXTRA_MOMENT_HINT, "对这篇花语感兴趣");
                        ((Activity) getContext()).startActivityForResult(intent, MyConstants.REQUEST_CODE_COMMENT);
                    }

                } else {
                    Intent intent = new Intent(activity, CommentCommitActivity.class);
                    intent.putExtra(MyConstants.Comment.TARGET_ID, momentId);
                    intent.putExtra(MyConstants.Comment.TARGET_TYPE, momentType);
                    intent.putExtra(MyConstants.EXTRA_MOMENT_HINT, "对这篇花语留言");
                    activity.startActivityForResult(intent, MomentDetailActivity.COMMENT);
                }

                break;
            case R.id.zan:
                ArrayMap<String, String> params = new ArrayMap<>();
                params.put("targetId", String.valueOf(momentId));
                params.put("targetType", String.valueOf(momentType));
                if (liked) {
                    params.put("type", "2");
                } else {
                    params.put("type", "1");
                }
                OkHttpClientManager.postAsyn(MyConstants.LIKE, params,
                        new OkHttpClientManager.StringCallback() {

                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                Logger.e("get data:" + response);
                                JsonValidator validator = new JsonValidator();
                                boolean isJson = validator.validate(response);
                                if (!isJson) {
                                    return;
                                }
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    if (liked) {
                                        liked = false;
                                        zanCount = String.valueOf(Long.parseLong(zanCount) - 1);
                                    } else {
                                        liked = true;
                                        zanCount = String.valueOf(Long.parseLong(zanCount) + 1);
                                    }
                                    setZanView();
                                } else {
                                    CommonUtils.error(baseResult, mActivity, "");
                                }
                            }

                        });
                break;
            case R.id.saveArticle:
                final ArrayMap<String, String> saveParams = new ArrayMap<>();
                saveParams.put("targetId", String.valueOf(momentId));
                saveParams.put("targetType", String.valueOf(momentType));
                if (collected) {
                    saveParams.put("type", "1");
                } else {
                    saveParams.put("type", "0");
                }
                OkHttpClientManager.postAsyn(MyConstants.MOMENT_SAVE, saveParams,
                        new OkHttpClientManager.StringCallback() {

                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                Logger.e("get data:" + response);
                                JsonValidator validator = new JsonValidator();
                                boolean isJson = validator.validate(response);
                                if (!isJson) {
                                    return;
                                }
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    if (collected) {
                                        collected = false;
                                        saveCount = String.valueOf(Long.parseLong(saveCount) - 1);
                                    } else {
                                        collected = true;
                                        saveCount = String.valueOf(Long.parseLong(saveCount) + 1);
                                    }
                                    setSaveView();
                                } else {
                                    CommonUtils.error(baseResult, mActivity, "");
                                }
                            }

                        });
                break;
        }
    }
}
