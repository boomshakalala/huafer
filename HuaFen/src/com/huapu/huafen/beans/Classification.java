package com.huapu.huafen.beans;

import com.huapu.huafen.MyApplication;
import com.huapu.huafen.utils.CommonUtils;

import java.util.ArrayList;

public class Classification extends BaseResult{

	private int classificationId;
	private int classificationSort;
	private String classificationName;
	private String classificationIcon;
	private String slogan;
	private ArrayList<SecondaryClassification> secondaryClassifications;
	public int getClassificationId() {
		return classificationId;
	}
	public void setClassificationId(int classificationId) {
		this.classificationId = classificationId;
	}
	public Integer getClassificationSort() {
		return classificationSort;
	}
	public void setClassificationSort(int classificationSort) {
		this.classificationSort = classificationSort;
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
	public String getSlogan() {
		return slogan;
	}
	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}
	public ArrayList<SecondaryClassification> getSecondaryClassifications() {
		return secondaryClassifications;
	}
	public void setSecondaryClassifications(
			ArrayList<SecondaryClassification> secondaryClassifications) {
		this.secondaryClassifications = secondaryClassifications;
	}
	
}
