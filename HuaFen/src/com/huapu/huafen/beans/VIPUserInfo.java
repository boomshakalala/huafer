package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by qwe on 2017/7/27.
 */

public class VIPUserInfo implements Serializable {
    public String itemType;
    public UserData user;
    public TreeMap<String, Integer> rate;
    public Count counts;

    public static class Count implements Serializable {
        public String selling;
    }
}
