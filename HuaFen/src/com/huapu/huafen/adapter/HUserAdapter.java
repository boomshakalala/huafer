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
import com.huapu.huafen.activity.PersonalPagerHomeActivity;
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
import com.huapu.huafen.views.CommonTitleView;
import com.huapu.huafen.views.FollowImageView;
import com.squareup.okhttp.Request;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Lalo on 2016/12/3.
 * 横向的用户列表
 */
public class HUserAdapter extends RecyclerView.Adapter<HUserAdapter.UserHolder>{


    private List<User> data;

    private Context context;

    public HUserAdapter(Context context,List<User> data){
        this.context = context;
        this.data = data;
    }

    public HUserAdapter(Context context){
        this(context,null);
    }

    public void  setData(List<User> data){
        this.data = data;
        notifyDataSetChanged();
    }


    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        UserHolder viewHolder = new UserHolder(LayoutInflater.from(context).inflate(R.layout.item_user_horizontal, parent, false));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) viewHolder.itemView.getLayoutParams();
        params.width = (int) (CommonUtils.getScreenWidth()/3.5);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UserHolder holder, int position) {
        final User item = data.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, PersonalPagerHomeActivity.class);
                intent.putExtra(MyConstants.EXTRA_USER_ID,item.getUserData().getUserId());
                context.startActivity(intent);
            }
        });

        String url =item.getUserData().getAvatarUrl();
        String tag = (String) holder.ivHeader.getTag();
        if(TextUtils.isEmpty(tag)||!tag.equals(url)){
            holder.ivHeader.setTag(url);
            holder.ivHeader.setImageURI(url);
        }

        holder.ctvName.setDataVertical(item.getUserData());

        int fansCount = item.getUserValue().getFansCount();
        String fansDes = CommonUtils.getDoubleCount(fansCount, MyConstants.COUNT_FANS);
        String format = context.getString(R.string.fans_count);
        String des = String.format(format,fansDes);
        holder.tvContent.setText(des);

        final int fellowship = item.getUserData().getFellowship();
        final boolean isFollowed = item.getUserData().getFollowed();
        if(fellowship == 0) {
            holder.ivFollow.setPinkData(isFollowed);
        } else {
            holder.ivFollow.setPinkData(fellowship);
        }
        holder.ivFollow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isFollowed){
                    Intent intent = new Intent();
                    intent.setClass(context, PersonalPagerHomeActivity.class);
                    intent.putExtra(MyConstants.EXTRA_USER_ID,item.getUserData().getUserId());
                    context.startActivity(intent);
                } else {
                    startRequestForFollowing(item,holder.ivFollow);
                }
            }
        });


        if(position == 0){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.leftMargin = CommonUtils.dp2px(10.0f);
            params.rightMargin = CommonUtils.dp2px(2.0f);
        } else if(position == data.size()-1){
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.leftMargin = CommonUtils.dp2px(2.0f);
            params.rightMargin = CommonUtils.dp2px(10.0f);
        } else{
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.leftMargin = CommonUtils.dp2px(2.0f);
            params.rightMargin = CommonUtils.dp2px(2.0f);
        }
    }


    private void startRequestForFollowing(final User user,final View view){
        if (!CommonPreference.isLogin()) {
            ActionUtil.loginAndToast((Activity) context);
            return;
        }
        long userId = user.getUserData().getUserId();
        if(userId != CommonPreference.getUserId()) {
            if(!CommonUtils.isNetAvaliable(context)) {
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
                            LogUtil.i("liang", "关注:"+response);
                            JsonValidator validator = new JsonValidator();
                            boolean isJson = validator.validate(response);
                            if(!isJson) {
                                return;
                            }
                            try {
                                BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                                if(baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                                    int fellowship = user.getUserData().getFellowship();
                                    switch (fellowship) {
                                        case 1: // 无关系
                                            user.getUserData().setFellowship(2);
                                            user.getUserData().setFollowed(true);
                                            break;
//                                        case 2: // 已关注
//                                            user.getUserData().setFellowship(1);
//                                        user.getUserData().setFollowed(true);
//                                            break;
                                        case 3: // 被关注
                                            user.getUserData().setFellowship(4);
                                            user.getUserData().setFollowed(true);
                                            break;
//                                        case 4: // 相互关注
//                                            user.getUserData().setFellowship(3);
//                                        user.getUserData().setFollowed(true);
//                                            break;
                                        default:
                                            user.getUserData().setFollowed(true);
                                            break;
                                    }
                                    notifyDataSetChanged();
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
        return data==null?0:data.size();
    }

    public class UserHolder extends RecyclerView.ViewHolder{

        public SimpleDraweeView ivHeader;//头像
        public CommonTitleView ctvName;
        public TextView tvContent;
        public FollowImageView ivFollow;//加关注

        public UserHolder(View itemView) {
            super(itemView);
            this.ivHeader = (SimpleDraweeView) itemView.findViewById(R.id.ivHeader);
            ImageLoader.loadImage(ivHeader,null,R.drawable.default_pic,R.drawable.default_pic);
            this.ctvName = (CommonTitleView) itemView.findViewById(R.id.ctvName);
            this.tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            this.ivFollow = (FollowImageView) itemView.findViewById(R.id.ivFollow);


        }
    }


}
