package com.huapu.huafen.beans;


import java.util.List;

public class RefundLogData extends BaseResult{

	private int page;
	private List<RefundLogBean> refundLogList;
	private GoodsData goodsData;
	private OrderData orderData;
	private Refund refund;

	public Refund getRefund() {
		return refund;
	}

	public void setRefund(Refund refund) {
		this.refund = refund;
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

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public List<RefundLogBean> getRefundLogList() {
		return refundLogList;
	}

	public void setRefundLogList(List<RefundLogBean> refundLogList) {
		this.refundLogList = refundLogList;
	}
}
