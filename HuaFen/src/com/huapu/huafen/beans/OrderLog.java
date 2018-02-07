package com.huapu.huafen.beans;


public class OrderLog extends BaseResult{
	private long orderId;
	private long sellerId;
	private long buyerId;
	private int operationType;
	private String operationTypeLabel;
	private String operationRemark;
	private long operationTime;
	private long operatorId;
	private int status;
	private int orderMsgId;
	
	
	public int getOrderMsgId() {
		return orderMsgId;
	}
	public void setOrderMsgId(int orderMsgId) {
		this.orderMsgId = orderMsgId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public long getSellerId() {
		return sellerId;
	}
	public void setSellerId(long sellerId) {
		this.sellerId = sellerId;
	}
	public long getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(long buyerId) {
		this.buyerId = buyerId;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public String getOperationTypeLabel() {
		return operationTypeLabel;
	}
	public void setOperationTypeLabel(String operationTypeLabel) {
		this.operationTypeLabel = operationTypeLabel;
	}
	public String getOperationRemark() {
		return operationRemark;
	}
	public void setOperationRemark(String operationRemark) {
		this.operationRemark = operationRemark;
	}
	public long getOperationTime() {
		return operationTime;
	}
	public void setOperationTime(long operationTime) {
		this.operationTime = operationTime;
	}
	public long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	
	
	
}
