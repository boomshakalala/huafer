package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/12/3.
 */
public class User implements Serializable {


    private UserData userData;

    private UserValue userValue;

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserValue getUserValue() {
        return userValue;
    }

    public void setUserValue(UserValue userValue) {
        this.userValue = userValue;
    }
}
