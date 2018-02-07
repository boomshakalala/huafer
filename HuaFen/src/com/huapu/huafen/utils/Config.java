package com.huapu.huafen.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.huapu.huafen.MyApplication;

/**
 * Created by admin on 2017/1/4.
 */

public class Config {

    public final static boolean DEBUG = isDebug(MyApplication.getApplication());
    public static final String API_HOST;
    public static final String API_WEB_HOST;
    public static final String OSS_VIDEO_FOLDER_BUCKET;

    static {
        if (DEBUG) {
            API_HOST = CommonPreference.getApiHost();
//            API_HOST ="http://192.168.0.80:8080/api/";
            API_WEB_HOST = "https://i-t.huafer.cc/";
            // OSS视频测试
            OSS_VIDEO_FOLDER_BUCKET = "hp-vi-test";
        } else {
            API_HOST = "https://api.huafer.cc/api/";
            API_WEB_HOST = "https://i.huafer.cc/";
            // OSS视频
            OSS_VIDEO_FOLDER_BUCKET = "hp-vi";
        }
//        API_HOST = "https://api.huafer.cc/api/";
//        API_WEB_HOST="https://i.huafer.cc/";
    }

    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
//        return true;
    }
}
