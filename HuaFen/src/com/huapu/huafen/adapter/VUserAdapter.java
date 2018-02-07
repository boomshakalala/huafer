package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;
import com.huapu.huafen.views.FollowImageView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/12/3.
 */
public class VUserAdapter extends CommonWrapper<VUserAdapter.UserHolder> {


    private Context context;
    private List<User> data;
    private Fragment fragment;

    public VUserAdapter(Fragment fragment) {
        this(fragment, null);
    }

    public VUserAdapter(Fragment fragment, List<User> data) {
        this.context = fragment.getActivity();
        this.fragment = fragment;
        this.data = data;
    }

    public VUserAdapter(Context context) {
        this(context, null);
    }

    public VUserAdapter(Context context, List<User> data) {
        this.context = context;
        this.data = data;
    }

    public void setData(List<User> data) {
        this.data = data;
        notifyWrapperDataSetChanged();
    }

    public void addAll(List<User> data) {
        if (data == null) {
            data = new ArrayList<User>();
        }

        this.data.addAll(data);
        notifyWrapperDataSetChanged();
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserHolder(LayoutInflater.from(context).
                inflate(R.layout.item_user_vertical, parent, false));
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, final int position) {
        final User item = data.get(position);
        if (item == null || item.getUserData() == null || item.getUserValue() == null) {
            return;
        }

        if (position == 0) {
            holder.spaceView.setVisibility(View.VISIBLE);
        } else {
            holder.spaceView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID, item.getUserData().getUserId());
                intent.putExtra("position", position);
                if (fragment == null) {
                    ((Activity) context).startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                } else {
                    fragment.startActivityForResult(intent, PersonalPagerHomeActivity.REQUEST_CODE_FOR_PERSONAL_DETAIL);
                }

            }
        });

        String url = item.getUserData().getAvatarUrl();
        String tag = (String) holder.ivHeader.getTag();
        if (TextUtils.isEmpty(tag) || !tag.equals(url)) {
            holder.ivHeader.setTag(url);
            ImageLoader.resizeSmall(holder.ivHeader, url, 1);
        }

        //姓名及其图标
        holder.ctvName.setData(item.getUserData());

        //展示位置或花粉儿认证
        if (item.getUserData().getUserLevel() >= 3) {//明星
            holder.dlvLocation.setVisibility(View.GONE);
            String title = item.getUserData().getTitle();
            if (!TextUtils.isEmpty(title)) {
                holder.tvAuth.setVisibility(View.VISIBLE);
                holder.tvAuth.setText(item.getUserData().getTitle());
            } else {
                holder.tvAuth.setVisibility(View.GONE);
            }
        } else {
            holder.tvAuth.setVisibility(View.GONE);
            Area area = item.getUserData().getArea();
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

        int fansCount = item.getUserValue().getFansCount();
        String fansDes = CommonUtils.getDoubleCount(fansCount, MyConstants.COUNT_FANS);

        int sellingCount = item.getUserValue().getGoodsCount();

        String des = String.format(context.getString(R.string.fans_selling_count), fansDes, sellingCount);

        holder.tvFansCountAndSellingCountDes.setText(des);
        final int fellowship = item.getUserData().getFellowship();
        final boolean isFollowed = item.getUserData().getFollowed();
        if (fellowship == 0) {
            holder.ivFollow.setPinkData(isFollowed);
        } else {
            holder.ivFollow.setPinkData(fellowship);
        }
        holder.llFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int fellowship = item.getUserData().getFellowship();
                if (1 == DialogManager.concernedUserDialog(context, fellowship, new DialogCallback() {
                    @Override
                    public void Click() {
                        // 取消关注
                        startRequestForFollowing(item, "2", holder.llFollow);
                    }
                })) {
                    // 关注
                    startRequestForFollowing(item, "1", holder.llFollow);
                }
            }
        });

    }

    private void startRequestForFollowing(final User user, String type, final View view) {
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast(context);
            return;
        }
        long userId = user.getUserData().getUserId();
        if (userId != CommonPreference.getUserId()) {
            if (!CommonUtils.isNetAvaliable(context)) {
                ToastUtil.toast(context, "请检查网络连接");
                return;
            }
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userId", String.valueOf(userId));
            params.put("type", type);

            view.setEnabled(false);
            OkHttpClientManager.postAsyn(MyConstants.CONCERNEDUSER, params,
                    new OkHttpClientManager.StringCallback() {

                        @Override
                        public void onError(Request request, Exception e) {
                            view.setEnabled(true);

                        }

                        @Override
                        public void onResponse(String response) {
                            view.setEnabled(true);
                            LogUtil.i("liang", "关注:" + response);
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if (!isJson) {
                                return;
                            }
                            try {
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    int fellowship = user.getUserData().getFellowship();
                                    switch (fellowship) {
                                        case 1: // 无关系
                                            user.getUserData().setFellowship(2);
                                            user.getUserData().setFollowed(true);
                                            break;
                                        case 2: // 已关注
                                            user.getUserData().setFellowship(1);
                                            user.getUserData().setFollowed(true);
                                            break;
                                        case 3: // 被关注
                                            user.getUserData().setFellowship(4);
                                            user.getUserData().setFollowed(true);
                                            break;
                                        case 4: // 相互关注
                                            user.getUserData().setFellowship(3);
                                            user.getUserData().setFollowed(true);
                                            break;
                                        default:
                                            user.getUserData().setFollowed(true);
                                            break;
                                    }
                                    notifyWrapperDataSetChanged();
                                } else {
                                    CommonUtils.error(baseResult, (Activity) context, "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });
        } else {
            ToastUtil.toast(context, "无法关注自己哦");
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
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
        public View spaceView;

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
            this.spaceView = itemView.findViewById(R.id.space);
        }
    }


    public void updateFollowState(final int position, final int type) {
        final User item = data.get(position);
        if (type == 2) {
            int fellowship = item.getUserData().getFellowship();
            switch (fellowship) {
                case 1: // 无关系 取消关注不会出现此状态
                    item.getUserData().setFellowship(1);
                    item.getUserData().setFollowed(false);
                    break;
                case 2: // 已关注
                    item.getUserData().setFellowship(1);
                    item.getUserData().setFollowed(false);
                    break;
                case 3: // 被关注
                    item.getUserData().setFellowship(4);
                    item.getUserData().setFollowed(false);
                    break;
                case 4: // 相互关注
                    item.getUserData().setFellowship(3);
                    item.getUserData().setFollowed(false);
                    break;
                default:
                    item.getUserData().setFollowed(false);
                    break;
            }
        } else {
            int fellowship = item.getUserData().getFellowship();
            switch (fellowship) {
                case 1: // 无关系
                    item.getUserData().setFellowship(2);
                    item.getUserData().setFollowed(true);
                    break;
                case 2: // 已关注
                    item.getUserData().setFellowship(1);
                    item.getUserData().setFollowed(true);
                    break;
                case 3: // 被关注
                    item.getUserData().setFellowship(4);
                    item.getUserData().setFollowed(true);
                    break;
                case 4: // 相互关注
                    item.getUserData().setFellowship(3);
                    item.getUserData().setFollowed(true);
                    break;
                default:
                    item.getUserData().setFollowed(true);
                    break;
            }
        }
        notifyWrapperDataSetChanged();
    }

}
