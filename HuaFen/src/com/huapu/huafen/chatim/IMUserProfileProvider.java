package com.huapu.huafen.chatim;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.UserIcon;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

/**
 * Created by qwe on 2017/10/29.
 */

public class IMUserProfileProvider implements LCChatProfileProvider {
    public static final String TAG = "IMUserProfileProvider";

    @Override
    public void fetchProfiles(List<String> userIdList, final LCChatProfilesCallBack profilesCallBack) {
        HashMap<String, String> params = new HashMap<>();
        String userId = userIdList.get(0);
        params.put("userId", userId);
        OkHttpClientManager.postAsyn(MyConstants.GETUSERICON, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                profilesCallBack.done(new ArrayList<LCChatKitUser>(0), e);
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e("用户", response);
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        UserIcon info = JSON.parseObject(baseResult.obj, UserIcon.class);
                        String url = info.userInfo.getUserIcon();
                        profilesCallBack.done(Collections.singletonList(
                                new LCChatKitUser(info.userInfo.getUserId() + "",
                                        info.userInfo.getUserName(),
                                        info.userInfo.getUserIcon())),
                                null);
                    } else {
                        profilesCallBack.done(new ArrayList<LCChatKitUser>(0), new Exception(TAG + ", 服务器返回码非 200"));
                    }
                } catch (Exception e) {
                    profilesCallBack.done(new ArrayList<LCChatKitUser>(0), e);
                }

            }
        });
    }
}
