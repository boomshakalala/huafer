package com.huapu.huafen.beans;

/**
 * Created by qwe on 2017/9/13.
 */

public class CountsData extends BaseResult {
    private long userId;
    private int fans;
    private int selling;
    private int collection;
    private int sold;
    private long fpoem;
    private int focus;

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public int getSelling() {
        return selling;
    }

    public void setSelling(int selling) {
        this.selling = selling;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    public long getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public long getFpoem() {
        return fpoem;
    }

    public void setFpoem(long fpoem) {
        this.fpoem = fpoem;
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
