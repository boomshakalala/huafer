package com.huapu.huafen.beans;


public class RefundLog extends BaseResult{
	/**
	 * 退款状态
	 * 1 申请退款 refundlog 买（申请仲裁，取消申请） 卖（拒绝退款，同意退款） 
		2 申请退货退款 refundlog  买（申请仲裁，取消申请） 卖（拒绝退款，同意退货退款） 同意退货退款后跳转至收货地址选择
		3 买家取消退款 refundlog
		4 卖家拒绝退款 refundlog
		5拒绝退货退款 refundlog
		6 同意退款 refundlog 
		7同意退货退款(返回收货人) address 买（退货） 卖（无）
		8 买家退货(返回快递信息) express 买（无）卖（申请仲裁，收货后同意退款）
		9 收货后退款 
		10 系统同意退款
		11 系统取消退款
		12 系统收货并退款
	 */
	private int operationType;
	private String refundLabel = "";
	private long operatorTime;
	private long operatorId;
	private int refundMoney;
	private String refundContent = "";
	
	
	public String getRefundContent() {
		return refundContent;
	}
	public void setRefundContent(String refundContent) {
		this.refundContent = refundContent;
	}
	public int getRefundMoney() {
		return refundMoney;
	}
	public void setRefundMoney(int refundMoney) {
		this.refundMoney = refundMoney;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public String getRefundLabel() {
		return refundLabel;
	}
	public void setRefundLabel(String refundLabel) {
		this.refundLabel = refundLabel;
	}
	public long getOperatorTime() {
		return operatorTime;
	}
	public void setOperatorTime(long operatorTime) {
		this.operatorTime = operatorTime;
	}
	public long getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	
}
