package com.huapu.huafen.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.User;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.DashLineView;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2016/12/3.
 */
public class ShareVUserAdapter extends CommonWrapper<ShareVUserAdapter.UserHolder> {


    private Context context;
    private List<User> data;

    public ShareVUserAdapter(Context context) {
        this(context, null);
    }

    public ShareVUserAdapter(Context context, List<User> data) {
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
    public void onBindViewHolder(final UserHolder holder, int position) {
        final User item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String jsonString = JSON.toJSONString(item);
                Intent intent = new Intent();
                intent.putExtra("json", jsonString);
                intent.putExtra("extra", String.valueOf(item.getUserData().getUserId()));
                ((Activity) context).setResult(Activity.RESULT_OK, intent);
                ((Activity) context).finish();
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

        final boolean isFollowed = item.getUserData().getFollowed();

        if (isFollowed) {
            holder.ivFollow.setImageResource(R.drawable.followed);
            holder.tvFollow.setBackgroundResource(R.drawable.text_white_round_gray_stroke_bg);
            holder.tvFollow.setTextColor(context.getResources().getColor(R.color.text_color_gray));
            holder.tvFollow.setText("已关注");
        } else {
            holder.ivFollow.setImageResource(R.drawable.follow_add);
            holder.tvFollow.setBackgroundResource(R.drawable.text_white_round_pink_stroke_bg);
            holder.tvFollow.setTextColor(context.getResources().getColor(R.color.base_pink));
            holder.tvFollow.setText("加关注");
        }
        holder.llFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFollowed) {
                    Intent intent = new Intent();
                    intent.setClass(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID, item.getUserData().getUserId());
                    context.startActivity(intent);
                } else {
                    startRequestForFollowing(item, holder.llFollow);
                }
            }
        });
//        long userId = item.getUserData().getUserId();
//
//        if(userId != CommonPreference.getUserId()){
//            holder.llFollow.setVisibility(View.VISIBLE);
//        }else{
//            holder.llFollow.setVisibility(View.GONE);
//        }

    }

    private void startRequestForFollowing(final User user, final View view) {
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
            params.put("type", "1");
            LogUtil.i("liang", "params:" + params.toString());
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
                                    user.getUserData().setFollowed(true);
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
        public ImageView ivFollow;
        public TextView tvFollow;

        public UserHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            this.ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            this.dlvLocation = (DashLineView) itemView.findViewById(R.id.dlvLocation);
            this.tvAuth = (TextView) itemView.findViewById(R.id.tvAuth);
            this.tvFansCountAndSellingCountDes = (TextView) itemView.findViewById(R.id.tvFansCountAndSellingCountDes);
            this.llFollow = (LinearLayout) itemView.findViewById(R.id.llFollow);
            this.ivFollow = (ImageView) itemView.findViewById(R.id.ivFollow);
            this.tvFollow = (TextView) itemView.findViewById(R.id.tvFollow);

        }
    }
}
