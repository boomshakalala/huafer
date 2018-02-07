package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/11/8.
 */
public class AddData implements Serializable {


    public String title ;


    public int resId;


    public AddData(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }
}
