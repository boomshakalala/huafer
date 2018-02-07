package com.huapu.huafen.beans;

public class Consignee extends BaseResult{
	private String consigneeName = "";
	private String consigneePhone = "";
	private String consigneeAddress = "";
	private Long consigneesId;
	private boolean isDefault;
	private Area area;
	public String getConsigneeName() {
		return consigneeName;
	}
	public void setConsigneeName(String consigneeName) {
		this.consigneeName = consigneeName;
	}
	public String getConsigneePhone() {
		return consigneePhone;
	}
	public void setConsigneePhone(String consigneePhone) {
		this.consigneePhone = consigneePhone;
	}
	public String getConsigneeAddress() {
		return consigneeAddress;
	}
	public void setConsigneeAddress(String consigneeAddress) {
		this.consigneeAddress = consigneeAddress;
	}
	public Long getConsigneesId() {
		return consigneesId;
	}
	public void setConsigneesId(Long consigneesId) {
		this.consigneesId = consigneesId;
	}
	
	public boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	
	

}
