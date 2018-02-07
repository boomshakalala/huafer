package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by qwe on 2017/7/20.
 */

public class SuggestUsersBean extends BaseResultNew {
    public Data obj;

    public static class Data extends BaseResultNew.BaseData {
        public List<UserData> result;
        public List<UserData> recUsers;
    }
}
