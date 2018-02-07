package com.huapu.huafen.views;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.UserData;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.FrescoUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mac on 17/7/3.
 */

public class UserView extends LinearLayout {


    @BindView(R.id.tvUserName)
    TextView tvUserName;

    public UserView(Context context) {
        this(context, null);
    }

    public UserView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.user_view, this, true);
        ButterKnife.bind(this);
    }

    public void setData(UserInfo userInfo){
        if(userInfo!=null){

            String userName = userInfo.getUserName();
            if(!TextUtils.isEmpty(userName)){
                tvUserName.setText(userName);
            }

            int userLevel = userInfo.getUserLevel();
            if(userLevel <= 0){
                tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }else{
                if(userLevel == 1){//实名
                    if(userInfo.getZmCreditPoint() > 0||userInfo.hasVerified){
                        tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.iv_zm,0);
                    }
                }else if(userLevel == 2){//vip
                    tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_vip,0);
                }else if(userLevel >= 3){//明星
                    tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_xing,0);
                }
            }
        }
    }

    public void setData(UserData userData){
        if(userData!=null){

            String userName = userData.getUserName();
            if(!TextUtils.isEmpty(userName)){
                tvUserName.setText(userName);
            }

            int userLevel = userData.getUserLevel();
            if(userLevel <= 0){
                tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }else{
                Uri uri = null;
                if(userLevel == 1){//实名
                    if(userData.getZmCreditPoint() > 0||userData.hasVerified){
                        tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.iv_zm,0);
                    }
                }else if(userLevel == 2){//vip
                    tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_vip,0);
                }else if(userLevel >= 3){//明星
                    tvUserName.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.icon_xing,0);
                }
            }
        }
    }

}
