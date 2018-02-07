package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liang_xs by 2016/11/02
 */
public class OrderData implements Serializable {

    private long orderId;
    /**
     * 订单主状态
     1 待支付
     2 支付中
     3 已支付
     4 待收货
     5 已收货
     6:已关闭(异常关闭orderState)
     7 已完成
     */
    private int status;
    private int price;
    private int postage;
    private String statusTitle;

    public long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    private long receiveTime;
    /**
     * 订单异常状态
     0 正常
     1;取消
     2 退款
     3 仲裁
     */
    private int state;
    private int orderStateCode;
    private boolean isArbitration; // 是否显示仲裁按钮
    private long reportLockTime; // 取消仲裁倒计时
    /**
     * 订单编号
     */
    private long orderNum;
    /**
     * 订单创建时间
     */
    private long orderCreateTime;
    /**
     * 支付时间
     */
    private long orderPayTime;
    /**
     * 1. 微信支付
     * 2. 支付宝支付
     */
    private int orderPayType;
    private long buyerId;
    private String orderMemo;

    public String getOrderMemo() {
        return orderMemo;
    }

    public void setOrderMemo(String orderMemo) {
        this.orderMemo = orderMemo;
    }

    public long getSellerId() {
        return sellerId;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    private long sellerId;

    private int shipType; // 3为当面交易

    public int getShipType() {
        return shipType;
    }

    public void setShipType(int shipType) {
        this.shipType = shipType;
    }
    public int getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(int shippingCost) {
        this.shippingCost = shippingCost;
    }

    private int shippingCost;

    public long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(long buyerId) {
        this.buyerId = buyerId;
    }

    public long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(long orderNum) {
        this.orderNum = orderNum;
    }

    public long getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(long orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public long getOrderPayTime() {
        return orderPayTime;
    }

    public void setOrderPayTime(long orderPayTime) {
        this.orderPayTime = orderPayTime;
    }

    public int getOrderPayType() {
        return orderPayType;
    }

    public void setOrderPayType(int orderPayType) {
        this.orderPayType = orderPayType;
    }

    public boolean getIsArbitration() {
        return isArbitration;
    }

    public void setIsArbitration(boolean arbitration) {
        isArbitration = arbitration;
    }
    public long getReportLockTime() {
        return reportLockTime;
    }

    public void setReportLockTime(long reportLockTime) {
        this.reportLockTime = reportLockTime;
    }    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOrderStateCode() {
        return orderStateCode;
    }

    public void setOrderStateCode(int orderStateCode) {
        this.orderStateCode = orderStateCode;
    }

    public String getStatusTitle() {
        return statusTitle;
    }

    public void setStatusTitle(String statusTitle) {
        this.statusTitle = statusTitle;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }
}
