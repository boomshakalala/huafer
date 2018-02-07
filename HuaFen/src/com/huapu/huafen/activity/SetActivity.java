package com.huapu.huafen.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.activity.mine.AboutActivity;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.SendSuccessEvent;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import cn.leancloud.chatkit.LCChatKit;
import de.greenrobot.event.EventBus;

/**
 * 设置
 *
 * @author liang_xs
 */
public class SetActivity extends BaseActivity {
    private View layoutPersonalData, layoutAbout, layoutBlack;
    private TextView tvBtnLogout;
    private TextDialog dialog;
    private RelativeLayout ivSetting;
    private RelativeLayout ivMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initView();
    }

    private void initView() {
        setTitleString("设置");
        layoutPersonalData = findViewById(R.id.layoutPersonalData);
        layoutAbout = findViewById(R.id.layoutAbout);
        layoutBlack = findViewById(R.id.layoutBlack);
        tvBtnLogout = (TextView) findViewById(R.id.tvBtnLogout);
        ivSetting = (RelativeLayout) findViewById(R.id.ivSetting);
        ivMessage = (RelativeLayout) findViewById(R.id.ivMessage);
        layoutPersonalData.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        ivMessage.setOnClickListener(this);
        layoutAbout.setOnClickListener(this);
        layoutBlack.setOnClickListener(this);
        tvBtnLogout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.layoutPersonalData:
                intent = new Intent(SetActivity.this, PersonalDataActivity.class);
                startActivity(intent);
                break;
            case R.id.layoutAbout:
                intent = new Intent(SetActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            case R.id.layoutBlack:
                intent = new Intent(SetActivity.this, BlackActivity.class);
                startActivity(intent);
                break;
            case R.id.tvBtnLogout:
                dialog = new TextDialog(this, false);
                dialog.setContentText("您确定退出用户吗？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确定");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        logout();
                    }
                });
                dialog.show();
                break;
            case R.id.ivSetting:
                intent = new Intent(SetActivity.this, MessageSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ivMessage:
                intent = new Intent(SetActivity.this, MessageFreeActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void logout() {
        asynchronousLogout();
        logoutLeanCloud();
        synchronizeLogout();
        // TODO: 17/12/26
    }

    private void logoutLeanCloud() {
        PushService.unsubscribe(MyApplication.getApplication(), String.valueOf(CommonPreference.getUserId()));
        LCChatKit.getInstance().close(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                LogUtil.i("退出登录成功");
            }
        });
    }

    /**
     * 退出登录
     */
    private void asynchronousLogout() {
        HashMap<String, String> params = new HashMap<String, String>();
        LogUtil.i("liang", "退出登录params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.USERLOGOFF, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "退出登录：" + e.toString());
            }


            @Override
            public void onResponse(String response) {

            }
        });
    }

    private void synchronizeLogout() {
        CommonPreference.setUserId(0);
        CommonPreference.cleanUserInfoAndAccess();
        CommonPreference.setIntValue(CommonPreference.USER_ZM_CREDIT_POINT, 0);
        CommonPreference.setIntValue("savedMoney", -1);//钱包金额
        CommonPreference.setBooleanValue(CommonPreference.IS_TIP_BING, true);
        toast("退出成功");
        MyConstants.UNREAD_ORDER_COUNT = 0;
        MyConstants.UNREAD_PRIVATE_COUNT = 0;
        MyConstants.UNREAD_SYSTEM_COUNT = 0;
        MyConstants.UNREAD_COMMENT_MSG_COUNT = 0;
        MessageUnReadCountEvent event = new MessageUnReadCountEvent();
        event.isUpdate = true;
        EventBus.getDefault().post(event);
        if (MainActivity.mActivity != null) {
            MainActivity.mActivity.selectIndexFragment();
            if (MainActivity.mActivity.indexFragment != null && MainActivity.mActivity.indexFragment.vpGoods != null) {
                MainActivity.mActivity.indexFragment.vpGoods.setCurrentItem(0);
            }
        }
        
        SendSuccessEvent eventSuccess = new SendSuccessEvent();
        eventSuccess.freshPage = true;
        org.greenrobot.eventbus.EventBus.getDefault().post(eventSuccess);

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
