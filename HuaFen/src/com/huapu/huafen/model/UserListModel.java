package com.huapu.huafen.model;

import com.huapu.huafen.http.OkHttpClientManager;

import java.util.Map;

/**
 * 用户列表
 * Created by dengbin on 17/12/15.
 */
public interface UserListModel {
    void getUserList(int type, Map<String, String> params, OkHttpClientManager.StringCallback stringCallback);
}
