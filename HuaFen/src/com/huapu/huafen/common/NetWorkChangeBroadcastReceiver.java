package com.huapu.huafen.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.ToastUtil;

import cn.leancloud.chatkit.LCChatKit;

/**
 * 网络环境监听
 * Created by qwe on 2017/9/12.
 */
public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            for (int i = 0; i < networkInfos.length; i++) {
                NetworkInfo.State state = networkInfos[i].getState();
                if (NetworkInfo.State.CONNECTED == state) {
                    if (null == LCChatKit.getInstance().getClient()) {
                        // showToast("please login first!");
                        ConfigUtil.loginLeanCloud(MyApplication.getApplication());
                    }
                    return;
                }
            }

            ToastUtil.toast(context, "您当前的网络环境不好");
        }
    }
}
