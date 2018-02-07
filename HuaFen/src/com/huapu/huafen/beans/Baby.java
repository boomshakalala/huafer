package com.huapu.huafen.beans;

import java.io.Serializable;

public class Baby implements Serializable{
	
	private long dateOfBirth;
	private int sex;
	public long getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}

	public boolean isEditAble;
	
}
