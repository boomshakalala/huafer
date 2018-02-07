package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017/3/20.
 */

public class HomePagerFilter implements Serializable{

    public int identity;

    public String name;

    public HomePagerFilter(int identity, String name) {
        this.identity = identity;
        this.name = name;
    }

    public HomePagerFilter() {
    }
}
