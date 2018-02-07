package com.huapu.huafen.beans;

public class UserData extends BaseResult{

	private long userId;
	private String userName;
	private String avatarUrl;
	private int gender;
	private boolean followed;
	private Area area;
	private int userLevel;
	private boolean hasCredit;
	private int zmCreditPoint;
	private int userType;
	private String title;
	private int fellowship;
	private String phone;
	public boolean hasVerified;

    /**
     * new add
     *
     * @return
     */
    private String articleBackground;
	private String lastVisitText;

	public String getLastVisitText() {
		return lastVisitText;
	}

	public void setLastVisitText(String lastVisitText) {
		this.lastVisitText = lastVisitText;
	}

	public String getArticleBackground() {
		return articleBackground;
    }

    public void setArticleBackground(String articleBackground) {
        this.articleBackground = articleBackground;
    }

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getFellowship() {
		return fellowship;
	}

	public void setFellowship(int fellowship) {
		this.fellowship = fellowship;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getZmCreditPoint() {
		return zmCreditPoint;
	}

	public void setZmCreditPoint(int zmCreditPoint) {
		this.zmCreditPoint = zmCreditPoint;
	}


	public boolean getHasCredit() {
		return hasCredit;
	}
	public void setHasCredit(boolean hasCredit) {
		this.hasCredit = hasCredit;
	}
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public boolean getFollowed() {
		return followed;
	}

	public void setFollowed(boolean followed) {
		this.followed = followed;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public int getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}


}
