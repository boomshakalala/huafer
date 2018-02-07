package com.huapu.huafen.beans;


/**
 * Created by admin on 2017/5/26.
 */

public class CredentialsResult extends BaseResultNew {

    public Data obj;

    public static class Data extends BaseData {
        public Credentials credentials;
    }

}
