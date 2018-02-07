package com.huapu.huafen.beans;


public class OrderListBean extends BaseResult{
	private GoodsInfo goodsInfo;
	private UserInfo userInfo;
	private OrderLog orderLog;
	public GoodsInfo getGoodsInfo() {
		return goodsInfo;
	}
	public void setGoodsInfo(GoodsInfo goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public OrderLog getOrderLog() {
		return orderLog;
	}
	public void setOrderLog(OrderLog orderLog) {
		this.orderLog = orderLog;
	}
	
	
}
