package com.huapu.huafen.beans;

public class SecondaryClassification extends BaseResult{

	private int goodsFirstCatesId;
	private int classificationId;
	private String classificationName;
	private String classificationIcon;
	private int classificationSort;
	public int getGoodsFirstCatesId() {
		return goodsFirstCatesId;
	}
	public void setGoodsFirstCatesId(int goodsFirstCatesId) {
		this.goodsFirstCatesId = goodsFirstCatesId;
	}
	public int getClassificationId() {
		return classificationId;
	}
	public void setClassificationId(int classificationId) {
		this.classificationId = classificationId;
	}
	public String getClassificationName() {
		return classificationName;
	}
	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}
	public String getClassificationIcon() {
		return classificationIcon;
	}
	public void setClassificationIcon(String classificationIcon) {
		this.classificationIcon = classificationIcon;
	}
	public int getClassificationSort() {
		return classificationSort;
	}
	public void setClassificationSort(int classificationSort) {
		this.classificationSort = classificationSort;
	}
	
	
}
