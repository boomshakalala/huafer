package com.huapu.huafen.model.impl;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.model.UserListModel;

import java.util.Map;

/**
 * 用户列表Model
 * Created by dengbin on 17/12/15.
 */
public class UserListModelImpl implements UserListModel {
    
    @Override
    public void getUserList(int type, Map<String, String> params, OkHttpClientManager.StringCallback stringCallback) {
        OkHttpClientManager.postAsyn(MyConstants.FOLLOWING_LIST, params, stringCallback);
    }
}
