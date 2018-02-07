
package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by liang_xs by 2016/11/02
 */
public class Orders implements Serializable {

    private GoodsData goodsData;
    private OrderData orderData;
    private Refund refund;
    private Arbitration arbitration;
    private UserData userData;
    private int page;
    public boolean isCheck;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public GoodsData getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
    }

    public OrderData getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderData orderData) {
        this.orderData = orderData;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }

    public Arbitration getArbitration() {
        return arbitration;
    }

    public void setArbitration(Arbitration arbitration) {
        this.arbitration = arbitration;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

}
