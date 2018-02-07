package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * Created by admin on 2017/3/14.
 */

public class PersonalShopResult implements Serializable{

    public User user;
    public TreeMap<String,Integer> rateCount;
    public GoodsCount goodsCount;

    public static class GoodsCount implements Serializable{

        public int selling;
        public int sold;

    }

}
