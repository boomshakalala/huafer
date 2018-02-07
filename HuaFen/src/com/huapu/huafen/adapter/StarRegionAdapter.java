package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.activity.GoodsDetailsActivity;
import com.huapu.huafen.activity.NewStarListActivity;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.StarCount;
import com.huapu.huafen.beans.StarRegionBean;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.VIPRegionBean;
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
import com.huapu.huafen.views.FollowImageView;
import com.squareup.okhttp.Request;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qwe on 2017/5/19.
 */

public class StarRegionAdapter extends AbstractRecyclerAdapter {
    private static final int TYPE_HEADER_TWO = 500;
    private static final int TYPE_HEADER_ACTIVE_USER = 501;
    private Context mContext;
    private RecyclerView recyclerView;

    private List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList;

    public StarRegionAdapter(Context context, RecyclerView recyclerView) {
        super(context, recyclerView);
        this.mContext = context;
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {

        int headerPosition = 0;
        int footerPosition = getItemCount() - 1;

        if (headerPosition == position && mIsHeaderEnable && null != headerView) {
            return TYPE_HEADER;
        }
        if (footerPosition == position && mIsFooterEnable) {
            return TYPE_FOOTER;
        }
        if (position == 1) {
            return TYPE_HEADER_TWO;
        } else if (position >= 2 && position <= activeUsersBeanList.size() + 1) {
            return TYPE_HEADER_ACTIVE_USER;
        }

        if (position == activeUsersBeanList.size() + 2) {
            return TYPE_HEADER_TWO;
        }

        return TYPE_GRID;
    }

    @Override
    public int getItemCount() {
        int count = listData.size();
        if (mIsFooterEnable) count++;
        if (mIsHeaderEnable) count++;
        count += (null == activeUsersBeanList ? 0 : activeUsersBeanList.size() + 2);
        return count;
    }

    public void setActiveUserData(List<VIPRegionBean.ActiveUsersBean> activeUsersBeanList) {
        this.activeUsersBeanList = activeUsersBeanList;
    }

    public void setHeaderNumber(int number) {
        headerNumber = number;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    if (type == TYPE_FOOTER || type == TYPE_HEADER || type == TYPE_HEADER_TWO || type == TYPE_GRID) {
                        return gridLayoutManager.getSpanCount();
                    }

                    if (spanSizeLookup != null) {
                        return spanSizeLookup.getSpanSize(position);
                    }
                    return 1;
                }
            });
        }
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType, boolean isItem) {
        if (viewType == TYPE_HEADER_TWO) {
            return new HeaderTwoHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_two_star_region, parent, false));
        } else if (viewType == TYPE_HEADER_ACTIVE_USER) {
            return new HeaderActiveHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.header_active_user, parent, false));
        } else {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_star_region, parent, false));
        }
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, boolean isItem) {
        try {
            if (holder instanceof HeaderActiveHolder) {
                final HeaderActiveHolder activeHolder = (HeaderActiveHolder) holder;
                final VIPRegionBean.ActiveUsersBean activeUsersBean = activeUsersBeanList.get(position - 2);
                final UserData userData = activeUsersBean.user;
                StarCount starCount = activeUsersBean.counts;
                activeHolder.activeUserImage.setImageURI(userData.getAvatarUrl());
                activeHolder.activeUserImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, userData.getUserId());
                        context.startActivity(intent);
                    }
                });
                activeHolder.activeUserName.setText(userData.getUserName());

                if (!TextUtils.isEmpty(userData.getTitle())) {
                    activeHolder.fansNumber.setText(userData.getTitle());
                } else {
                    if (!TextUtils.isEmpty(starCount.fans)) {
                        activeHolder.fansNumber.setText("粉丝：" + starCount.fans);
                    } else {
                        activeHolder.fansNumber.setText("粉丝：0");
                    }

                }


                if ("当前在线".equals(userData.getLastVisitText())) {
                    activeHolder.onLineState.setTextColor(Color.parseColor("#78D067"));
                } else {
                    activeHolder.onLineState.setTextColor(Color.parseColor("#8A000000"));
                }
                activeHolder.onLineState.setText(userData.getLastVisitText());
                final int fellowship = userData.getFellowship();
                activeHolder.followImage.setPinkData(fellowship);
                activeHolder.followImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAttentionState(activeHolder.followImage, fellowship, userData, activeHolder.followImage);
                    }
                });
            } else if (holder instanceof ItemHolder) {
                final ItemHolder itemHolder = (ItemHolder) holder;
                final StarRegionBean.UserWithGoods userWithGoods = (StarRegionBean.UserWithGoods) listData.get(position - activeUsersBeanList.size() - 3);

                ImageLoader.resizeSmall(itemHolder.userImage, userWithGoods.user.getAvatarUrl(), 1);

                itemHolder.userImage.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(context, PersonalPagerHomeActivity.class);
                        intent.putExtra(MyConstants.EXTRA_USER_ID, userWithGoods.user.getUserId());
                        context.startActivity(intent);
                    }
                });
                itemHolder.commonTitle.setData(userWithGoods.user);

                if (null != userWithGoods.item && null != userWithGoods.item.get(0)) {
                    itemHolder.oneImage.setImageURI(userWithGoods.item.get(0).goodsImgs.get(0));
                    itemHolder.tvPrice1.setText("¥" + String.valueOf(userWithGoods.item.get(0).price));
                    itemHolder.oneImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(userWithGoods.item.get(0).goodsId));
                            context.startActivity(intent);
                        }
                    });
                }


                if (null != userWithGoods.item && null != userWithGoods.item.get(1)) {
                    itemHolder.twoImage.setImageURI(userWithGoods.item.get(1).goodsImgs.get(0));
                    itemHolder.tvPrice2.setText("¥" + String.valueOf(userWithGoods.item.get(1).price));
                    itemHolder.twoImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(userWithGoods.item.get(1).goodsId));
                            context.startActivity(intent);
                        }
                    });
                }

                if (null != userWithGoods.item && null != userWithGoods.item.get(2)) {
                    itemHolder.threeImage.setImageURI(userWithGoods.item.get(2).goodsImgs.get(0));
                    itemHolder.tvPrice3.setText("¥" + String.valueOf(userWithGoods.item.get(2).price));
                    itemHolder.threeImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(userWithGoods.item.get(2).goodsId));
                            context.startActivity(intent);
                        }
                    });
                }

                if (null != userWithGoods.item && null != userWithGoods.item.get(3)) {
                    itemHolder.fourImage.setImageURI(userWithGoods.item.get(3).goodsImgs.get(0));
                    itemHolder.tvPrice4.setText("¥" + String.valueOf(userWithGoods.item.get(3).price));
                    itemHolder.fourImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(userWithGoods.item.get(3).goodsId));
                            context.startActivity(intent);
                        }
                    });
                }


                if (null != userWithGoods.item && null != userWithGoods.item.get(4)) {
                    itemHolder.fiveImage.setImageURI(userWithGoods.item.get(4).goodsImgs.get(0));
                    itemHolder.tvPrice5.setText("¥" + String.valueOf(userWithGoods.item.get(4).price));
                    itemHolder.fiveImage.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, GoodsDetailsActivity.class);
                            intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(userWithGoods.item.get(4).goodsId));
                            context.startActivity(intent);
                        }
                    });
                }


                itemHolder.fansNumber.setText(Html.fromHtml("粉丝：" + userWithGoods.counts.fans + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;商品：" + userWithGoods.counts.selling));
                final int fellowship = userWithGoods.user.getFellowship();
                itemHolder.followImage.setPinkData(fellowship);
                itemHolder.followImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAttentionState(itemHolder.followImage, fellowship, userWithGoods.user, itemHolder.followImage);
                    }
                });


            } else if (holder instanceof HeaderTwoHolder) {
                HeaderTwoHolder headerTwoHolder = (HeaderTwoHolder) holder;
                if (position == 1) {
                    headerTwoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, NewStarListActivity.class);
                            mContext.startActivity(intent);
                        }
                    });
                    headerTwoHolder.rightIcon.setVisibility(View.VISIBLE);
                    headerTwoHolder.seeAll.setVisibility(View.VISIBLE);
                    headerTwoHolder.headerTwoText.setText("明星活跃榜");
                    headerTwoHolder.space.setVisibility(View.GONE);
                } else {
                    headerTwoHolder.rightIcon.setVisibility(View.GONE);
                    headerTwoHolder.seeAll.setVisibility(View.GONE);
                    headerTwoHolder.headerTwoText.setText("推荐明星商品");
                    headerTwoHolder.space.setVisibility(View.VISIBLE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPost(final FollowImageView followImageView, ArrayMap attentionParams,
                        final UserData userData, final View view, final int fellowship) {

        view.setEnabled(false);
        OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, attentionParams, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                view.setEnabled(true);
            }

            @Override
            public void onResponse(String response) {
                view.setEnabled(true);
                Logger.e("get response:" + response);
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
                        notifyDataSetChanged();
                    } else {
                        if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
                            CommonUtils.checkAccess((Activity) mContext);
                        }

                        ToastUtil.toast(context, baseResult.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAttentionState(final FollowImageView followImageView, final int fellowship,
                                   final UserData userData, final View view) {

        final ArrayMap<String, String> attentionParams = new ArrayMap<>();
        attentionParams.put("userId", String.valueOf(userData.getUserId()));

        final int type = DialogManager.concernedUserDialog(mContext, fellowship, new DialogCallback() {
            @Override
            public void Click() {
                attentionParams.put("type", "2");
                doPost(followImageView, attentionParams, userData, view, fellowship);
            }
        });

        if (type != 1) {
            return;
        }

        attentionParams.put("type", "1");
        doPost(followImageView, attentionParams, userData, view, fellowship);
    }

    public static final class HeaderTwoHolder extends RecyclerView.ViewHolder {
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

    public static final class HeaderActiveHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.activeUserImage)
        SimpleDraweeView activeUserImage;
        @BindView(R.id.activeUserName)
        TextView activeUserName;
        @BindView(R.id.fansNumber)
        TextView fansNumber;
        @BindView(R.id.onLineState)
        TextView onLineState;
        @BindView(R.id.followImage)
        FollowImageView followImage;

        public HeaderActiveHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.userImage)
        SimpleDraweeView userImage;
        @BindView(R.id.commonTitle)
        CommonTitleView commonTitle;
        @BindView(R.id.fansNumber)
        TextView fansNumber;
        @BindView(R.id.followImage)
        FollowImageView followImage;
        @BindView(R.id.oneImage)
        SimpleDraweeView oneImage;
        @BindView(R.id.twoImage)
        SimpleDraweeView twoImage;
        @BindView(R.id.threeImage)
        SimpleDraweeView threeImage;
        @BindView(R.id.fourImage)
        SimpleDraweeView fourImage;
        @BindView(R.id.fiveImage)
        SimpleDraweeView fiveImage;
        @BindView(R2.id.tvPrice1)
        TextView tvPrice1;
        @BindView(R2.id.tvPrice2)
        TextView tvPrice2;
        @BindView(R2.id.tvPrice3)
        TextView tvPrice3;
        @BindView(R2.id.tvPrice4)
        TextView tvPrice4;
        @BindView(R2.id.tvPrice5)
        TextView tvPrice5;

        ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
