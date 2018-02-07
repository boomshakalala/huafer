package com.huapu.huafen.beans;

public class OrderInfo extends BaseResult{
	private long orderNum;
	private long sellerid;
	private long buyerId;
	private long goodsId;
	public boolean modifiedCourierNumber;
	/**
	 * 订单异常状态
		0 正常
		1;取消
		2 退款
		3 仲裁
	 */
	private int orderState;
	/**
	 * 订单状态描述码 
		orderState非0或者orderStatus为6时
		1  买家取消
		2  卖家取消
		3  支付超时取消
		4  发货超时取消
		5  系统取消
		6  申请退款
		7  取消退款
		8  同意退款
		9  拒绝退款
		10 买家已退货
		11 退款完成
		12 仲裁中
		13 同意退货退款
		14 收货并退款
		15 系统同意退款
		16 系统取消退款
		17 系统自动确认收货
		18 延迟收货
		19 已收获
		20 支付处理中
	    21 仲裁举证期(3天倒计时)
	    22 平台介入期(平台5-7内完成,不可取消仲裁)
	 */
	private int orderStateCode;
	private String orderStateTitle;
	private long orderResidualTime;
	private long orderDeliverTime;
	private long orderPayTime;
	private long orderReceiveTime;
	private long orderStateTime;
	private long orderId;
	private long orderCreateTime;
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
	private int orderStatus;
	private int orderPrice;
	private int orderPostage;
	private int orderPayType;
	private String orderStateDescription;
	private boolean isArbitration; // 是否显示仲裁按钮
	private boolean isEvidence; // 是否举证过
	private String orderStateUnderTitle = ""; // 异常状态倒计时文案
	private long reportLockTime; // 取消仲裁倒计时
	private int shipType; // 3为当面交易
	private String orderMemo;


	public String getOrderMemo() {
		return orderMemo;
	}

	public void setOrderMemo(String orderMemo) {
		this.orderMemo = orderMemo;
	}

	public int getShipType() {
		return shipType;
	}

	public void setShipType(int shipType) {
		this.shipType = shipType;
	}

	public boolean getIsArbitration() {
		return isArbitration;
	}

	public void setIsArbitration(boolean arbitration) {
		isArbitration = arbitration;
	}

	public boolean getIsEvidence() {
		return isEvidence;
	}

	public void setIsEvidence(boolean evidence) {
		isEvidence = evidence;
	}

	public String getOrderStateUnderTitle() {
		return orderStateUnderTitle;
	}

	public void setOrderStateUnderTitle(String orderStateUnderTitle) {
		this.orderStateUnderTitle = orderStateUnderTitle;
	}

	public long getReportLockTime() {
		return reportLockTime;
	}

	public void setReportLockTime(long reportLockTime) {
		this.reportLockTime = reportLockTime;
	}

	public long getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(long orderNum) {
		this.orderNum = orderNum;
	}
	public long getSellerid() {
		return sellerid;
	}
	public void setSellerid(long sellerid) {
		this.sellerid = sellerid;
	}
	public long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(long buyerId) {
		this.buyerId = buyerId;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	public int getOrderState() {
		return orderState;
	}
	public void setOrderState(int orderState) {
		this.orderState = orderState;
	}
	public int getOrderStateCode() {
		return orderStateCode;
	}
	public void setOrderStateCode(int orderStateCode) {
		this.orderStateCode = orderStateCode;
	}
	public String getOrderStateTitle() {
		return orderStateTitle;
	}
	public void setOrderStateTitle(String orderStateTitle) {
		this.orderStateTitle = orderStateTitle;
	}
	public long getOrderResidualTime() {
		return orderResidualTime;
	}
	public void setOrderResidualTime(long orderResidualTime) {
		this.orderResidualTime = orderResidualTime;
	}
	public long getOrderDeliverTime() {
		return orderDeliverTime;
	}
	public void setOrderDeliverTime(long orderDeliverTime) {
		this.orderDeliverTime = orderDeliverTime;
	}
	public long getOrderPayTime() {
		return orderPayTime;
	}
	public void setOrderPayTime(long orderPayTime) {
		this.orderPayTime = orderPayTime;
	}
	public long getOrderReceiveTime() {
		return orderReceiveTime;
	}
	public void setOrderReceiveTime(long orderReceiveTime) {
		this.orderReceiveTime = orderReceiveTime;
	}
	public long getOrderStateTime() {
		return orderStateTime;
	}
	public void setOrderStateTime(long orderStateTime) {
		this.orderStateTime = orderStateTime;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public long getOrderCreateTime() {
		return orderCreateTime;
	}
	public void setOrderCreateTime(long orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}
	public int getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}
	public int getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(int orderPrice) {
		this.orderPrice = orderPrice;
	}
	public int getOrderPostage() {
		return orderPostage;
	}
	public void setOrderPostage(int orderPostage) {
		this.orderPostage = orderPostage;
	}
	public int getOrderPayType() {
		return orderPayType;
	}
	public void setOrderPayType(int orderPayType) {
		this.orderPayType = orderPayType;
	}
	public String getOrderStateDescription() {
		return orderStateDescription;
	}
	public void setOrderStateDescription(String orderStateDescription) {
		this.orderStateDescription = orderStateDescription;
	}
	
	
	
}
