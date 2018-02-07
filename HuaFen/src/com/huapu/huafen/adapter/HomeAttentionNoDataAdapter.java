package com.huapu.huafen.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.NewStarListActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.StarUserInfo;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.VIPUserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.squareup.okhttp.Request;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.alibaba.sdk.android.feedback.impl.FeedbackAPI.activity;

/**
 * Created by qwe on 2017/7/27.
 */

public class HomeAttentionNoDataAdapter extends AbstractRecyclerAdapter {
    private List<VIPUserInfo> vipUserInfoList;
    private List<StarUserInfo> starUserInfoList;

    private int HEADER_TYPE_TWO = 1000;

    private int ITEM_TYPE_STAR = HEADER_TYPE_TWO + 1;

    private int ITEM_TYPE_VIP = ITEM_TYPE_STAR + 1;

    private Context mContext;

    public HomeAttentionNoDataAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mContext = context;
    }

    public void setData(List<VIPUserInfo> vipUserInfoList, List<StarUserInfo> starUserInfoList) {
        Logger.e("get response:" + vipUserInfoList.size() + "size:" + starUserInfoList.size());
        this.vipUserInfoList = vipUserInfoList;
        this.starUserInfoList = starUserInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (null != vipUserInfoList && vipUserInfoList.size() > 0) {
            count += vipUserInfoList.size();
        }

        if (null != starUserInfoList && starUserInfoList.size() > 0) {
            count += starUserInfoList.size();
        }
        count += 2;
        if (mIsFooterEnable) count++;
        if (mIsHeaderEnable) count++;
        Logger.e("get response:" + count);
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int headerPosition = 0;

        if (headerPosition == position && mIsHeaderEnable && null != headerView) {
            return TYPE_HEADER;
        }

        if (position == 1) {
            return HEADER_TYPE_TWO;
        }

        if (null != starUserInfoList) {
            if (position == 2 + starUserInfoList.size()) {
                return HEADER_TYPE_TWO;
            }

            if (position > 1 && position < 2 + starUserInfoList.size()) {
                return ITEM_TYPE_STAR;
            }
        }
        return ITEM_TYPE_VIP;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        if (viewType == ITEM_TYPE_STAR) {
            return new StarListHolder(LayoutInflater.from(context).inflate(R.layout.layout_item_star, parent, false));
        } else if (viewType == HEADER_TYPE_TWO) {
            return new HeaderTwoHolder(LayoutInflater.from(context).inflate(R.layout.header_two_star_region_home, parent, false));
        } else {
            return new VIPItemListHolder(LayoutInflater.from(context).inflate(R.layout.layout_item_vip, parent, false));
        }
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position, boolean isItem) {
        try {
            if (holder instanceof VIPItemListHolder) {
                final VIPItemListHolder vipItemListHolder = (VIPItemListHolder) holder;
                final VIPUserInfo vipUserInfo = vipUserInfoList.get(position - starUserInfoList.size() - 3);
                String selling = "0";
                if (!TextUtils.isEmpty(vipUserInfo.counts.selling)) {
                    selling = vipUserInfo.counts.selling;
                }
                if (vipUserInfo.rate != null && !vipUserInfo.rate.isEmpty()) {
                    int count = 0;
                    int countGoods = 0;
                    if (vipUserInfo.rate.containsKey("30")) {
                        Integer var = vipUserInfo.rate.get("30");
                        count += var;
                        countGoods = var;
                    }

                    if (vipUserInfo.rate.containsKey("10")) {
                        Integer var = vipUserInfo.rate.get("10");
                        count += var;
                    }

                    if (count > 0) {
                        int progress = countGoods * 100 / count;
                        vipItemListHolder.userInfoVIP.setText("好评率：" + progress + "%" + "   在售：" + selling);
                    } else {
                        vipItemListHolder.userInfoVIP.setText("好评率：0%" + "   在售：" + selling);
                    }

                } else {
                    vipItemListHolder.userInfoVIP.setText("好评率：0%" + "   在售：" + selling);
                }

                vipItemListHolder.vipUserPhoto.setImageURI(vipUserInfo.user.getAvatarUrl());
                vipItemListHolder.vipUserPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, vipUserInfo.user.getUserId());
                        mContext.startActivity(intent);
                    }
                });
                vipItemListHolder.commonTitleView.setData(vipUserInfo.user);
                final int fellowship = vipUserInfo.user.getFellowship();
                vipItemListHolder.followImage.setPinkData(vipUserInfo.user.getFellowship());
                vipItemListHolder.followImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dealAttention(fellowship, vipUserInfo.user, vipItemListHolder.followImage);
                    }
                });
            } else if (holder instanceof HeaderTwoHolder) {
                HeaderTwoHolder headerTwoHolder = (HeaderTwoHolder) holder;
                if (position == 1) {
                    headerTwoHolder.headerTwoText.setText("明星店铺");
                    headerTwoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, NewStarListActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                } else {
                    headerTwoHolder.headerTwoText.setText("推荐店主");
                    headerTwoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, NewStarListActivity.class);
                            intent.putExtra("VIP", "VIP");
                            mContext.startActivity(intent);
                        }
                    });
                }
            } else if (holder instanceof StarListHolder) {
                final StarListHolder starListHolder = (StarListHolder) holder;
                final StarUserInfo starUserInfo = starUserInfoList.get(position - 2);
                starListHolder.starLayout.setVisibility(View.VISIBLE);
                starListHolder.userInfo.setText("花粉儿认证：" + starUserInfo.user.getTitle());
                starListHolder.userPhoto.setImageURI(starUserInfo.user.getAvatarUrl());
                starListHolder.commonTitleView.setData(starUserInfo.user);
                starListHolder.followImage.setPinkData(starUserInfo.user.getFellowship());
                final int fellowship = starUserInfo.user.getFellowship();
                starListHolder.userPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, starUserInfo.user.getUserId());
                        mContext.startActivity(intent);
                    }
                });
                starListHolder.followImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dealAttention(fellowship, starUserInfo.user, starListHolder.followImage);
                    }
                });
            }
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    private void doDealAttention(final int followShip, ArrayMap attentionParams, final UserData userData,
                                 final FollowImageView followImageView) {

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
                        if (followShip == 1) {
                            followImageView.setPinkData(2);
                            userData.setFellowship(2);
                        } else if (followShip == 2) {
                            followImageView.setPinkData(1);
                            userData.setFellowship(1);
                        } else if (followShip == 3) {
                            followImageView.setPinkData(4);
                            userData.setFellowship(4);
                        } else if (followShip == 4) {
                            followImageView.setPinkData(3);
                            userData.setFellowship(3);
                        }
                        notifyDataSetChanged();
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess(activity);
                        } else {
                            ToastUtil.toast(mContext, baseResult.msg);
                        }
                    }
                } catch (Exception e) {
                    Log.e("catch", e.getMessage(), e);
                }
            }
        });
    }

    private void dealAttention(final int followShip, final UserData userData, final FollowImageView followImageView) {
        final ArrayMap<String, String> attentionParams = new ArrayMap<>();
        attentionParams.put("userId", String.valueOf(userData.getUserId()));

        Logger.e("get fellowship:" + followShip);

        int type = DialogManager.concernedUserDialog(mContext, followShip, new DialogCallback() {
            @Override
            public void Click() {
                attentionParams.put("type", "2");
                doDealAttention(followShip, attentionParams, userData, followImageView);
            }
        });

        if (type == 1) {
            attentionParams.put("type", "1");
            doDealAttention(followShip, attentionParams, userData, followImageView);
        }
    }

    static final class VIPItemListHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.userInfoVIP)
        TextView userInfoVIP;
        @BindView(R2.id.vipUserPhoto)
        SimpleDraweeView vipUserPhoto;
        @BindView(R2.id.commonTitleView)
        CommonTitleView commonTitleView;
        @BindView(R2.id.followImage)
        FollowImageView followImage;

        VIPItemListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static final class StarListHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.userInfo)
        TextView userInfo;
        @BindView(R2.id.userPhoto)
        SimpleDraweeView userPhoto;
        @BindView(R2.id.starLayout)
        FrameLayout starLayout;
        @BindView(R2.id.commonTitleView)
        CommonTitleView commonTitleView;
        @BindView(R2.id.followImage)
        FollowImageView followImage;

        StarListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static final class HeaderTwoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.headerTwoText)
        TextView headerTwoText;
        @BindView(R.id.right_icon)
        ImageView rightIcon;
        @BindView(R.id.seeAll)
        TextView seeAll;
        @BindView(R.id.space)
        View space;

        public HeaderTwoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
