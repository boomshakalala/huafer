package com.huapu.huafen.beans;

import java.util.List;

public class Express extends BaseResult{
	
	private int expressId;
	private String expressName;
	private String expressNum;
	private String expressTel;
	private String expressIcon;
	private List<Trace> traces;
	private int expressState;
	
	
	public int getExpressState() {
		return expressState;
	}
	public void setExpressState(int expressState) {
		this.expressState = expressState;
	}
	public List<Trace> getTraces() {
		return traces;
	}
	public void setTraces(List<Trace> traces) {
		this.traces = traces;
	}
	public String getExpressNum() {
		return expressNum;
	}
	public void setExpressNum(String expressNum) {
		this.expressNum = expressNum;
	}
	public String getExpressTel() {
		return expressTel;
	}
	public void setExpressTel(String expressTel) {
		this.expressTel = expressTel;
	}
	public int getExpressId() {
		return expressId;
	}
	public void setExpressId(int expressId) {
		this.expressId = expressId;
	}
	public String getExpressName() {
		return expressName;
	}
	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}
	public String getExpressIcon() {
		return expressIcon;
	}
	public void setExpressIcon(String expressIcon) {
		this.expressIcon = expressIcon;
	}
	
}
