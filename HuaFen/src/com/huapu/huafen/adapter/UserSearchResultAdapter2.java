package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.recycler.base.ViewHolder;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.SearchHistoryHelper;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qwe on 2017/7/21.
 */
public class UserSearchResultAdapter2 extends CommonWrapper<UserSearchResultAdapter2.UserSearchViewHolder> {

    private List<UserData> mData = new ArrayList<>();
    private Activity activity;
    private String keyWord;

    public UserSearchResultAdapter2(Activity activity) {
        this.activity = activity;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setData(List<UserData> data) {
        this.mData = data;
        notifyWrapperDataSetChanged();
    }

    @Override
    public UserSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserSearchViewHolder(parent.getContext(),
                LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.search_user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserSearchViewHolder holder, final int position) {
        final UserData userData = mData.get(position);
        final long userId = userData.getUserId();
        holder.userPhoto.setImageURI(userData.getAvatarUrl());
        SpannableString s = new SpannableString(userData.getUserName());
        Pattern p = Pattern.compile(keyWord);
        Matcher m = p.matcher(s);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#419BF9")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.userView.setData(userData);
        holder.userView.getNameTextView().setText(s);

        if (userData.getUserLevel() == 3) {
            if (!TextUtils.isEmpty(userData.getTitle())) {
                holder.userInfo.setVisibility(View.VISIBLE);
                holder.userInfo.setText(userData.getTitle());
            } else {
                holder.userInfo.setVisibility(View.GONE);
            }
        } else {
            if (null != userData.getArea()) {
                Area area = userData.getArea();
                if (!TextUtils.isEmpty(area.getCity()) && !TextUtils.isEmpty(area.getArea())) {
                    holder.userInfo.setText(area.getCity() + " | " + area.getArea());
                    holder.userInfo.setVisibility(View.VISIBLE);
                } else {
                    holder.userInfo.setVisibility(View.GONE);
                }
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistoryHelper.put(new CodeValuePair(2, userData.getUserName()));
                Intent intent = new Intent(activity, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, userId);
                activity.startActivity(intent);
            }
        });

        final int fellowship = mData.get(position).getFellowship();
        holder.followImage.setPinkData(userData.getFellowship());
        holder.followImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int type = DialogManager.concernedUserDialog(v.getContext(), fellowship, new DialogCallback() {
                    @Override
                    public void Click() {
                        dealAttention(fellowship, "2", mData.get(position), holder.followImage);
                    }
                });
                if (type == 1) {
                    dealAttention(fellowship, "1", mData.get(position), holder.followImage);
                }
            }
        });
    }

    private void dealAttention(final int fellowship, String type, final UserData userData, final FollowImageView followImageView) {
        ArrayMap<String, String> attentionParams = new ArrayMap<>();
        attentionParams.put("userId", String.valueOf(userData.getUserId()));
        attentionParams.put("type", type);

        followImageView.setEnabled(false);

        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, attentionParams, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                followImageView.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                followImageView.setEnabled(true);
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (fellowship == 1) {
                            followImageView.setPinkData(2);
                            userData.setFellowship(2);
                        } else if (fellowship == 2) {
                            followImageView.setPinkData(1);
                            userData.setFellowship(1);
                        } else if (fellowship == 3) {
                            followImageView.setPinkData(4);
                            userData.setFellowship(4);
                        } else if (fellowship == 4) {
                            followImageView.setPinkData(3);
                            userData.setFellowship(3);
                        }
                        notifyWrapperDataSetChanged();
                    } else {
                        ToastUtil.toast(activity, baseResult.msg);
                    }
                } catch (Exception e) {
                    Log.e("catch", e.getMessage(), e);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }


    class UserSearchViewHolder extends ViewHolder {
        @BindView(R.id.userPhoto)
        SimpleDraweeView userPhoto;
        @BindView(R.id.userInfo)
        TextView userInfo;
        @BindView(R.id.followImage)
        FollowImageView followImage;
        @BindView(R.id.userView)
        CommonTitleView userView;

        UserSearchViewHolder(Context context, View itemView) {
            super(context, itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}