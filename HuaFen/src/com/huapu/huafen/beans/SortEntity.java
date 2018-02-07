package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/11/17.
 */
public class SortEntity implements Serializable{

    public String name;
    public int id;
    public boolean isCheck;

    public SortEntity(int id ,String name) {
        this.name = name;
        this.id = id;
    }

    public SortEntity() {
    }

    public SortEntity(int id,String name, boolean isCheck) {
        this.name = name;
        this.id = id;
        this.isCheck = isCheck;
    }
}
