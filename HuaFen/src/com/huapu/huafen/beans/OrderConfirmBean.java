package com.huapu.huafen.beans;

import java.util.ArrayList;

public class OrderConfirmBean extends BaseResult{

	private UserInfo userInfo;
	private GoodsInfo goodsInfo;
	private ArrayList<Consignee> consignees;
	
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
	public ArrayList<Consignee> getConsignees() {
		return consignees;
	}
	public void setConsignees(ArrayList<Consignee> consignees) {
		this.consignees = consignees;
	}
	
	
}
