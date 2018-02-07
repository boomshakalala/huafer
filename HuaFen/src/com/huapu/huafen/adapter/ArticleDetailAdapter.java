package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.ArticleDetailResult;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.FloridData;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.huapu.huafen.views.TagsContainer;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by admin on 2017/5/2.
 */

public class ArticleDetailAdapter extends CommonWrapper<RecyclerView.ViewHolder> {


    private final static String TAG = ArticleDetailAdapter.class.getSimpleName();
    private Context context;
    private List<Object> data;


    public ArticleDetailAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArticleDetailResult result) {
        if (result != null && result.obj != null || result.obj.article != null) {
            data = new ArrayList<>();
            data.add(result);
            if (!ArrayUtil.isEmpty(result.obj.article.sections)) {
                data.addAll(result.obj.article.sections);
            }
            notifyWrapperDataSetChanged();
        }
    }


    private enum ItemType {
        HEADER,
        ITEM,;
    }


    @Override
    public int getItemViewType(int position) {

        Object item = data.get(position);

        if (item instanceof ArticleDetailResult) {
            return ItemType.HEADER.ordinal();
        } else {
            return ItemType.ITEM.ordinal();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEADER.ordinal()) {
            return new ArticleDetailHeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.article_detail_header, parent, false));
        } else {
            return new ArticleDetailItemViewHolder(LayoutInflater.from(context).inflate(R.layout.article_detail_item, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Object item = data.get(position);
        int itemType = getItemViewType(position);

        if (itemType == ItemType.HEADER.ordinal()) {
            final ArticleDetailHeaderViewHolder viewHolder = (ArticleDetailHeaderViewHolder) holder;
            final ArticleDetailResult result = (ArticleDetailResult) item;

            if (result != null && result.obj != null) {

                if (result.obj.article != null) {
                    viewHolder.tagsContainer.setData(result.obj.article.titleMedia);
                    if (!TextUtils.isEmpty(result.obj.article.title)) {
                        viewHolder.tvTitle.setText(result.obj.article.title);
                    }
                }

                final UserData user = result.obj.user;
                if (user != null) {
                    viewHolder.avatar.setImageURI(user.getAvatarUrl());

                    viewHolder.avatar.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(context, PersonalPagerHomeActivity.class);
                            intent.putExtra(MyConstants.EXTRA_USER_ID, user.getUserId());
                            intent.putExtra("position", position);
                            ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                        }
                    });

                    viewHolder.ctvName.setData(user);
                    if (CommonPreference.getUserId() == user.getUserId()) {
                        viewHolder.followImage.setVisibility(View.GONE);
                    } else {
                        viewHolder.followImage.setVisibility(View.VISIBLE);
                        int fellowship = user.getFellowship();
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        viewHolder.followImage.setPinkData(fellowship);

                        viewHolder.followImage.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (user == null) {
                                    return;
                                }

                                int type = DialogManager.concernedUserDialog(v.getContext(), user.getFellowship(), new DialogCallback() {
                                    @Override
                                    public void Click() {
                                        startRequestForWatch(user, "2", viewHolder.followImage);
                                    }
                                });
                                if (type == 1)
                                    startRequestForWatch(user, "1", viewHolder.followImage);
                            }
                        });
                    }

                }

                FloridData article = result.obj.article;

                if (article != null) {
                    long time = article.updatedAt != 0 ? article.updatedAt : article.createdAt;
                    String date = DateTimeUtils.getDateStr(time, "yyyy-MM-dd");
                    viewHolder.tvTime.setText(date);
                }

                if (result.obj.count != null) {
                    if (user != null && user.getUserId() > 0 && CommonPreference.getUserId() > 0 && CommonPreference.getUserId() == user.getUserId()) {
                        viewHolder.tvWatch.setVisibility(View.VISIBLE);
                        if (!TextUtils.isEmpty(result.obj.count.pv)) {
                            viewHolder.tvWatch.setText(result.obj.count.pv);
                        } else {
                            viewHolder.tvWatch.setText("0");
                        }
                    } else {
                        viewHolder.tvWatch.setVisibility(View.GONE);
                    }
                    ArticleDetailResult.Count count = result.obj.count;
                    int replyCount = 0;
                    int comment = 0;
                    try {
                        comment = Integer.parseInt(count.comment);
                        replyCount = Integer.parseInt(count.reply);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    int msgCount = comment + replyCount;
                    String countDesc = CommonUtils.getIntegerCount(msgCount, MyConstants.COUNT_COMMENT);
                    if (!TextUtils.isEmpty(countDesc)) {
                        viewHolder.tvMessageCount.setText(countDesc);
                    }
                }
            }
        } else {
            ArticleDetailItemViewHolder viewHolder = (ArticleDetailItemViewHolder) holder;
            FloridData.Section section = (FloridData.Section) item;

            viewHolder.tagsContainer.setData(section.media);

            if (!TextUtils.isEmpty(section.content)) {
                viewHolder.tvContent.setText(section.content);
            }
        }
    }

    private void startRequestForWatch(final UserData userData, String type, final View view) {
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

                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        //1 无关系 2 已关注 3 被关注 4 互相关注
                        if (fellowship == 1) {
                            userData.setFellowship(2);
                        } else if (fellowship == 2) {
                            userData.setFellowship(1);
                        } else if (fellowship == 3) {
                            userData.setFellowship(4);
                        } else if (fellowship == 4) {
                            userData.setFellowship(3);
                        }
                        notifyWrapperDataSetChanged();
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess((Activity) context);
                        }
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


    public class ArticleDetailItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.tagsContainer)
        TagsContainer tagsContainer;
        @BindView(R2.id.tvContent)
        TextView tvContent;


        public ArticleDetailItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tagsContainer.setContainerType(TagsContainer.ContainerType.DETAIL);
        }
    }

    public class ArticleDetailHeaderViewHolder extends RecyclerView.ViewHolder {


        @BindView(R2.id.tagsContainer)
        TagsContainer tagsContainer;
        @BindView(R2.id.avatar)
        SimpleDraweeView avatar;
        @BindView(R2.id.ctvName)
        CommonTitleView ctvName;
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

        public ArticleDetailHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tagsContainer.setContainerType(TagsContainer.ContainerType.DETAIL);
        }
    }


}
