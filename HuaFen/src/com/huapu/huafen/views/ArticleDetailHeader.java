package com.huapu.huafen.views;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/2.
 */

public class ArticleDetailHeader extends LinearLayout {

    private final static String TAG = ArticleDetailHeader.class.getSimpleName();
    @BindView(R2.id.tagsContainer)
    TagsContainer tagsContainer;
    @BindView(R2.id.avatar)
    SimpleDraweeView avatar;
    @BindView(R2.id.tvUserName)
    TextView tvUserName;
    @BindView(R2.id.tvTime)
    TextView tvTime;
    @BindView(R2.id.tvWatch)
    TextView tvWatch;
    @BindView(R2.id.tvMessageCount)
    TextView tvMessageCount;
    @BindView(R2.id.followImage)
    FollowImageView followImage;
    @BindView(R2.id.tvTitle)
    TextView tvTitle;

    public ArticleDetailHeader(Context context) {
        this(context, null);
    }

    public ArticleDetailHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.article_detail_header, this, true);
        ButterKnife.bind(this);
    }

    public void setData(ArticleDetailResult result) {
        if (result != null && result.obj != null) {

            tagsContainer.setData(result.obj.article.titleMedia);

            final UserData user = result.obj.user;
            if (user != null) {
                avatar.setImageURI(user.getAvatarUrl());

                if (!TextUtils.isEmpty(user.getUserName())) {
                    tvUserName.setText(user.getUserName());
                }

                if (CommonPreference.getUserId() == user.getUserId()) {
                    followImage.setVisibility(GONE);
                } else {
                    followImage.setVisibility(VISIBLE);
                    final int fellowship = user.getFellowship();
                    //1 无关系 2 已关注 3 被关注 4 互相关注
                    followImage.setPinkData(fellowship);

                    followImage.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (1 == DialogManager.concernedUserDialog(v.getContext(), fellowship, new DialogCallback() {
                                @Override
                                public void Click() {
                                    startRequestForWatch(user, followImage, "2");
                                }
                            })) {
                                startRequestForWatch(user, followImage, "1");
                            }
                        }
                    });
                }

            }

            FloridData article = result.obj.article;

            if (article != null) {
                long time = article.updatedAt != 0 ? article.updatedAt : article.createdAt;
                String date = DateTimeUtils.getDateStr(time, "yyyy年MM月dd日");
                tvTime.setText(date);
            }
        }
    }

    private void startRequestForWatch(final UserData userData, final View view, String type) {

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(userData.getUserId()));
        params.put("type", type);

        final int fellowship = userData.getFellowship();

        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                view.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                LogUtil.i(TAG, "关注:" + response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (fellowship == 1) {
                            followImage.setPinkData(2);
                            userData.setFellowship(2);
                        } else if (fellowship == 2) {
                            followImage.setPinkData(1);
                            userData.setFellowship(1);
                        } else if (fellowship == 3) {
                            followImage.setPinkData(4);
                            userData.setFellowship(4);
                        } else if (fellowship == 4) {
                            followImage.setPinkData(3);
                            userData.setFellowship(3);
                        }
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess((Activity) getContext());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
