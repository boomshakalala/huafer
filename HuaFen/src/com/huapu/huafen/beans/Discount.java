package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/14.
 */

public class Discount implements Serializable{

    public String key;
    public String value;
    public boolean isCheck;


    public Discount(){

    }

    public Discount(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
