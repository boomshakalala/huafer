package com.huapu.huafen.beans;

public class Age extends BaseResult{

	/**
	 * 宝宝年龄段说明
	 */
	private String ageTitle;
	private int ageId;
	private int sequence;
	private boolean isSelect;
	public boolean isCheck;
	private String value;

	public boolean getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(boolean select) {
		isSelect = select;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getAgeTitle() {
		return ageTitle;
	}
	public void setAgeTitle(String ageTitle) {
		this.ageTitle = ageTitle;
	}
	public int getAgeId() {
		return ageId;
	}
	public void setAgeId(int ageId) {
		this.ageId = ageId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Age age = (Age) o;

		if (ageId != age.ageId) return false;
		return ageTitle != null ? ageTitle.equals(age.ageTitle) : age.ageTitle == null;

	}

	@Override
	public int hashCode() {
		int result = ageTitle != null ? ageTitle.hashCode() : 0;
		result = 31 * result + ageId;
		return result;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
