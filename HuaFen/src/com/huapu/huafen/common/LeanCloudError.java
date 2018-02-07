package com.huapu.huafen.common;

import android.content.Context;

import com.huapu.huafen.utils.ToastUtil;

/**
 * leancloud错误处理
 * Created by dengbin on 17/12/27.
 */
public class LeanCloudError {
    public static void toast(Context context, int code) {
        switch (code) {
            case 4315:
                ToastUtil.toast(context, "当前用户被加入此对话的黑名单，无法发送消息");
                break;
            default:
                // ToastUtil.toast(context, "消息发送失败");
                break;
        }
    }
}
