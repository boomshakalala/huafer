package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/11/7.
 */
public class RecommendData implements Serializable{
    private ArrayList<Commodity> items;
    private int page;
    private String heavyTime;

    public ArrayList<Commodity> getItems() {
        return items;
    }

    public void setItems(ArrayList<Commodity> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getHeavyTime() {
        return heavyTime;
    }

    public void setHeavyTime(String heavyTime) {
        this.heavyTime = heavyTime;
    }
}
