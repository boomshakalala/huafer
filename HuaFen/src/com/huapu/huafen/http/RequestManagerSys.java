package com.huapu.huafen.http;

import android.text.TextUtils;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;

import java.util.HashMap;

/**
 * Created by dengbin on 17/12/26.
 */

public class RequestManagerSys {
    public static void startRequestForKvsd(String authToken, OkHttpClientManager.StringCallback callback) {
        HashMap<String, String> params = new HashMap<>();
        //TODO 为了升级暂时沿用authToken 2.1.0
        if (!TextUtils.isEmpty(authToken))
            params.put("authToken", authToken);
        params.put("kvsd", CommonPreference.getKVSDSHA1());
        params.put("imei", CommonUtils.getImei(MyApplication.getApplication()));
        params.put("imsi", CommonUtils.getImsi(MyApplication.getApplication()));
        params.put("wifiMac", CommonUtils.getMac());
        params.put("regionsd", CommonPreference.getRegionsd());
        params.put("catsd", CommonPreference.getCatsd());
        OkHttpClientManager.postAsyn(MyConstants.INIT_KVSD, params, callback);
    }
}
