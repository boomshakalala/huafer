package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by lalo on 2016/11/5.
 */
public class Commodity implements Serializable {

    //商品数据
    private GoodsData goodsData;
    private GoodsValue goodsValue;
    private UserData userData;
    private UserValue userValue;

    //如果type = 2,则显示图片，否则显示商品数据
    private int type = 1;
    private String image;
    private String action;
    private String target;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GoodsData getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
    }

    public GoodsValue getGoodsValue() {
        return goodsValue;
    }

    public void setGoodsValue(GoodsValue goodsValue) {
        this.goodsValue = goodsValue;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public UserValue getUserValue() {
        return userValue;
    }

    public void setUserValue(UserValue userValue) {
        this.userValue = userValue;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
