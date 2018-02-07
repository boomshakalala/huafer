package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by admin on 2016/11/21.
 */
public class CommodityListResult implements Serializable{


    private ArrayList<Commodity> goodsList;
    private BannerData banner1;
    public List<HomePagerFilter> filters;
    private int page;

    public ArrayList<Commodity> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(ArrayList<Commodity> goodsList) {
        this.goodsList = goodsList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public BannerData getBanner1() {
        return banner1;
    }

    public void setBanner1(BannerData banner1) {
        this.banner1 = banner1;
    }
}
