package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/11/30.
 */
public class GoodsListResult implements Serializable {

    public int page;

    private List<GoodsInfo> goodsList;


    public List<GoodsInfo> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<GoodsInfo> goodsList) {
        this.goodsList = goodsList;
    }
}
