package com.huapu.huafen.beans;

public class UserResult extends BaseResult{
	private UserData userData;
	private UserValue userValue;

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}

	public UserValue getUserValue() {
		return userValue;
	}

	public void setUserValue(UserValue userValue) {
		this.userValue = userValue;
	}
}
