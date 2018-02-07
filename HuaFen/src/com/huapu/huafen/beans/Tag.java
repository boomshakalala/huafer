package com.huapu.huafen.beans;

public class Tag extends BaseResult{

	private boolean isSelect;
	private String name;

	public Tag(boolean isSelect, String name) {
		this.isSelect = isSelect;
		this.name = name;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
