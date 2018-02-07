package com.huapu.huafen.beans;

/**
 * Created by admin on 2016/9/24.
 */
public class CampaignData extends UserResult {

    private GoodsData goodsData;
    private GoodsValue goodsValue;


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


}
