package com.huapu.huafen.utils;

import android.content.Context;

import com.avos.avoscloud.PushService;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.activity.MainActivity;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.event.LCIMConnectionChangeEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by admin on 2017/1/4.
 */

public class ConfigUtil {

    public static boolean isToVerify() {
        boolean hasVerified = CommonPreference.getUserInfo().hasVerified;
        // boolean hasCredit = CommonPreference.getUserInfo().getHasCredit();
        boolean hasZmCreditPoint = CommonPreference.getUserInfo().getZmCreditPoint() > 0 ? true : false;
        // 1普通用户,2vip,3明星
        int userLevel = CommonPreference.getUserInfo().getUserLevel();
        if (hasVerified || hasZmCreditPoint || userLevel == 3 || userLevel == 2) {
            return false;
        } else {
            return true;
        }
    }

    public static void loginLeanCloud(final Context context) {
        long uid = CommonPreference.getUserId();

        if (uid == 0)
            return;

        String uidStr = String.valueOf(uid);

        PushService.subscribe(MyApplication.getApplication(), uidStr, MainActivity.class);

        LCChatKit.getInstance().open(uidStr, uidStr, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (null != e) {
                    String errorMsg = "登录聊天服务失败";
                    ToastUtil.toast(context, errorMsg);
                    LogUtil.e(e, errorMsg);
                    EventBus.getDefault().post(new LCIMConnectionChangeEvent(false));
                } else {
                    EventBus.getDefault().post(new LCIMConnectionChangeEvent(true));
                }
            }
        });
    }

}
