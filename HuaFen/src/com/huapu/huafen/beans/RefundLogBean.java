package com.huapu.huafen.beans;


public class RefundLogBean extends BaseResult{
	
	/**
	 * 当前退款状态
		1 申请退款
		2 申请退货退款
		3 买家取消退款
		4 卖家拒绝退款
		5拒绝退货退款
		6 同意退款
		7同意退货退款 
		8 买家退货
		9 收货后退款
		10 系统同意退款
		11 系统取消退款
		12 系统收货并退款
	 */
	private RefundLog refundLog;
	private Express expressInfo;
	private Consignee address;
	private long time;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public RefundLog getRefundLog() {
		return refundLog;
	}
	public void setRefundLog(RefundLog refundLog) {
		this.refundLog = refundLog;
	}
	public Express getExpressInfo() {
		return expressInfo;
	}
	public void setExpressInfo(Express expressInfo) {
		this.expressInfo = expressInfo;
	}
	public Consignee getAddress() {
		return address;
	}
	public void setAddress(Consignee address) {
		this.address = address;
	}
	
	
}
