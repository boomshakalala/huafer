package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.NewStarListActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.NewStarUserBean;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;
import com.huapu.huafen.views.FollowImageView;
import com.squareup.okhttp.Request;

/**
 * Created by qwe on 2017/5/19.
 */

public class NewStarAdapter extends AbstractRecyclerAdapter {

    private Context context;

    public NewStarAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.context = context;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        return new UserHolder(LayoutInflater.from(context).
                inflate(R.layout.item_user_vertical, parent, false));
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position, boolean isItem) {
        final int realPosition = position - 1;
        final NewStarUserBean.StarUserBean item = (NewStarUserBean.StarUserBean) listData.get(realPosition);
        final UserHolder holder = (UserHolder) viewHolder;
        try {
            holder.itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, item.user.getUserId());
                    intent.putExtra("position", realPosition);
                    ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                }
            });

            String url = item.user.getAvatarUrl();
            String tag = (String) holder.ivHeader.getTag();
            if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
                holder.ivHeader.setTag(url);
                ImageLoader.resizeSmall(holder.ivHeader, url, 1);
            }

            //姓名及其图标
            holder.ctvName.setData(item.user);

            //展示位置或花粉儿认证
            if (item.user.getUserLevel() >= 3) {//明星
                holder.dlvLocation.setVisibility(View.GONE);
                String title = item.user.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    holder.tvAuth.setVisibility(View.VISIBLE);
                    holder.tvAuth.setText(title);
                } else {
                    holder.tvAuth.setVisibility(View.GONE);
                }
            } else {
                holder.tvAuth.setVisibility(View.GONE);
                Area area = item.user.getArea();
                if (area != null) {
                    if (!TextUtils.isEmpty(area.getCity()) && !TextUtils.isEmpty(area.getArea())) {
                        holder.dlvLocation.setVisibility(View.VISIBLE);
                        holder.dlvLocation.setData(area.getCity(), area.getArea());
                    } else {
                        holder.dlvLocation.setVisibility(View.GONE);
                    }
                } else {
                    holder.dlvLocation.setVisibility(View.GONE);
                }
            }

            if (context instanceof NewStarListActivity) {
                NewStarListActivity listActivity = (NewStarListActivity) context;
                int orderBy = listActivity.getOrderBy();

                if (orderBy == 1) {
                    String sellingCount = "0";
                    String fansDes = "0";
                    if (!TextUtils.isEmpty(item.counts.selling)) {
                        sellingCount = item.counts.selling;
                    }
                    if (!TextUtils.isEmpty(item.counts.fans)) {
                        int fansCount = Integer.valueOf(item.counts.fans);
                        fansDes = CommonUtils.getDoubleCount(fansCount, MyConstants.COUNT_FANS);
                    }
                    holder.tvFansCountAndSellingCountDes.setText(Html.fromHtml("粉丝：" + fansDes + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商品：" + sellingCount));
                } else if (orderBy == 2 || orderBy == 3) {
                    String sellingCount = "0";
                    if (!TextUtils.isEmpty(item.counts.selling)) {
                        sellingCount = item.counts.selling;
                    }
                    if (!TextUtils.isEmpty(item.user.getLastVisitText())) {
                        if ("当前在线".equals(item.user.getLastVisitText())) {
                            holder.tvFansCountAndSellingCountDes.setText(Html.fromHtml("<font color='#78D067'>" + item.user.getLastVisitText() + "</font>" + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商品：" + sellingCount));
                        } else {
                            holder.tvFansCountAndSellingCountDes.setTextColor(Color.parseColor("#8A000000"));
                            holder.tvFansCountAndSellingCountDes.setText(Html.fromHtml(item.user.getLastVisitText() + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商品：" + sellingCount));
                        }
                    } else {
                        holder.tvFansCountAndSellingCountDes.setText(Html.fromHtml("商品：" + sellingCount));
                    }

                }
            }

            final int fellowShip = item.user.getFellowship();
            holder.ivFollow.setPinkData(fellowShip);
            holder.ivFollow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int type = DialogManager.concernedUserDialog(context, fellowShip, new DialogCallback() {
                        @Override
                        public void Click() {
                            // 取消关注
                            dealAttention(fellowShip, "2", item.user, holder.ivFollow);
                        }
                    });

                    if (1 == type) {
                        // 关注
                        dealAttention(fellowShip, "1", item.user, holder.ivFollow);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealAttention(final int fellowShip, String type, final UserData userData, final FollowImageView followImageView) {
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
                Logger.e("get response:" + response);
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
                        if (fellowShip == 1) {
                            followImageView.setPinkData(2);
                            userData.setFellowship(2);
                        } else if (fellowShip == 2) {
                            followImageView.setPinkData(1);
                            userData.setFellowship(1);
                        } else if (fellowShip == 3) {
                            followImageView.setPinkData(4);
                            userData.setFellowship(4);
                        } else if (fellowShip == 4) {
                            followImageView.setPinkData(3);
                            userData.setFellowship(3);
                        }
                        notifyDataSetChanged();
                    } else {
                        ToastUtil.toast(context, baseResult.msg);
                    }
                } catch (Exception e) {
                    Log.e("catch", e.getMessage(), e);
                }
            }
        });
    }


    public class UserHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public SimpleDraweeView ivHeader;
        public CommonTitleView ctvName;
        public DashLineView dlvLocation;
        public TextView tvAuth;
        public TextView tvFansCountAndSellingCountDes;
        public LinearLayout llFollow;
        public FollowImageView ivFollow;

        public UserHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            this.ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            this.dlvLocation = (DashLineView) itemView.findViewById(R.id.dlvLocation);
            this.tvAuth = (TextView) itemView.findViewById(R.id.tvAuth);
            this.tvFansCountAndSellingCountDes = (TextView) itemView.findViewById(R.id.tvFansCountAndSellingCountDes);
            this.llFollow = (LinearLayout) itemView.findViewById(R.id.llFollow);
            this.ivFollow = (FollowImageView) itemView.findViewById(R.id.ivFollow);

        }
    }
}
