package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/11/18.
 */
public class FilterPrice implements Serializable {

    public String price;

    public String value;

    public boolean isCheck;


    public FilterPrice(String value,String price) {
        this.price = price;
        this.value = value;
    }
}
