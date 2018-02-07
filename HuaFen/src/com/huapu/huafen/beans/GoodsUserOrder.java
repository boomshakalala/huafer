
package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by liang_xs by 2016/11/02
 */
public class GoodsUserOrder implements Serializable {

    private GoodsData goodsData;
    private OrderData orderData;
    private UserData sellerData;

    public UserData getSellerData() {
        return sellerData;
    }

    public void setSellerData(UserData sellerData) {
        this.sellerData = sellerData;
    }

    public OrderData getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderData orderData) {
        this.orderData = orderData;
    }


    public GoodsData getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
    }
}