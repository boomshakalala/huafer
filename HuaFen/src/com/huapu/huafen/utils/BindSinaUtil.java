package com.huapu.huafen.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.mob.tools.utils.UIHandler;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

/**
 * Created by qwe on 2017/6/24.
 */

public class BindSinaUtil implements Handler.Callback {

    private Activity activity;

    private RefreshSinaUI refreshSinaUI;

    public BindSinaUtil(Activity activity) {
        this.activity = activity;
        this.refreshSinaUI = (RefreshSinaUI) activity;
    }

    public void bindSina() {
        ShareSDK.initSDK(activity);
        Platform platform = ShareSDK.getPlatform(SinaWeibo.NAME);
        if (null != platform) {
            if (platform.isValid()) {
                platform.removeAccount(true);
                ShareSDK.removeCookieOnAuthorize(true);
            }
            platform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    msg.arg2 = action;
                    msg.obj = platform;
                    UIHandler.sendMessage(msg, BindSinaUtil.this);
                }

                @Override
                public void onError(Platform platform, int action, Throwable throwable) {
                    Message msg = new Message();
                    msg.arg1 = 2;
                    msg.arg2 = action;
                    msg.obj = platform;
                    UIHandler.sendMessage(msg, BindSinaUtil.this);
                }

                @Override
                public void onCancel(Platform platform, int action) {
                    Message msg = new Message();
                    msg.arg1 = 3;
                    msg.arg2 = action;
                    msg.obj = platform;
                    UIHandler.sendMessage(msg, BindSinaUtil.this);
                }
            });

            platform.SSOSetting(false); // 设置false表示使用SSO授权方式
            platform.showUser(null);// 执行登录，登录后在回调里面获取用户资料
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        Platform plat = (Platform) message.obj;
        String text;
        switch (message.arg1) {
            case 1:
                // 成功
                if (plat != null) {
                    PlatformDb platDB = plat.getDb();
                    if (platDB != null) {
                        startRequestForBind(platDB.getUserId(), platDB.getToken(), String.valueOf(platDB.getExpiresIn()));
                    }

                }
                break;
            case 2:
                if (plat != null) {
                    if (plat.isValid()) {
                        plat.removeAccount(true);
                    }
                }
                text = plat.getName() + "发生错误";
                ToastUtil.toast(activity, text);
                //失败
                break;
            case 3:
                // 取消
                if (plat != null) {
                    if (plat.isValid()) {
                        plat.removeAccount(true);
                    }
                }
                if (plat.getName().equals(SinaWeibo.NAME)) {
                    text = "绑定微博未成功，请重新尝试一下吧！";
                } else {
                    text = plat.getName() + "授权取消";
                }
                ToastUtil.toast(activity, text);
                break;

        }
        return false;
    }

    private void startRequestForBind(final String unionId, final String accessToken, String expiresIn) {
        if (!CommonUtils.isNetAvaliable(activity)) {
            ToastUtil.toast(activity, "请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(activity);
        ArrayMap<String, String> arrayMap = new ArrayMap<>();
        arrayMap.put("providerId", "3");
        arrayMap.put("unionId", unionId);
        arrayMap.put("accessToken", accessToken);
        arrayMap.put("expiresIn", expiresIn);
        arrayMap.put("refreshToken", "");
        OkHttpClientManager.postAsyn(MyConstants.BIND_SINA_WEIBO, arrayMap, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();

            }

            @Override
            public void onResponse(String response) {
                Log.e("get response:", response);
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        refreshSinaUI.refreshSinaUI(unionId);
                    } else {
                        CommonUtils.error(baseResult, activity, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public interface RefreshSinaUI {
        void refreshSinaUI(String unionId);
    }
}
