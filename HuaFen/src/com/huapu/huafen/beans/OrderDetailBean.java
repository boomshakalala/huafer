package com.huapu.huafen.beans;


import java.io.Serializable;
import java.util.List;

public class OrderDetailBean implements Serializable{

	private UserInfo userInfo;
	private GoodsInfo goodsInfo;
	private Consignee consignee;
	private OrderInfo orderInfo;
	private RefundInfo refundInfo;
	private ArbitrationInfo arbitrationInfo;
	private List<OrderHistoryResult.OrderOperate> orderOperates;
	private List<Item> recItems;
	public String recTraceId;

	public List<Item> getRecItems() {
		return recItems;
	}

	public void setRecItems(List<Item> recItems) {
		this.recItems = recItems;
	}

	public List<OrderHistoryResult.OrderOperate> getOrderOperates() {
		return orderOperates;
	}

	public void setOrderOperates(List<OrderHistoryResult.OrderOperate> orderOperates) {
		this.orderOperates = orderOperates;
	}

	public ArbitrationInfo getArbitrationInfo() {
		return arbitrationInfo;
	}

	public void setArbitrationInfo(ArbitrationInfo arbitrationInfo) {
		this.arbitrationInfo = arbitrationInfo;
	}

	public RefundInfo getRefundInfo() {
		return refundInfo;
	}
	public void setRefundInfo(RefundInfo refundInfo) {
		this.refundInfo = refundInfo;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public GoodsInfo getGoodsInfo() {
		return goodsInfo;
	}
	public void setGoodsInfo(GoodsInfo goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
	public Consignee getConsignee() {
		return consignee;
	}
	public void setConsignee(Consignee consignee) {
		this.consignee = consignee;
	}
	public OrderInfo getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(OrderInfo orderInfo) {
		this.orderInfo = orderInfo;
	}
	
	
	
}
