//package com.huapu.huafen.dao;
//
//import java.util.ArrayList;
//
//import com.j256.ormlite.field.DatabaseField;
//import com.j256.ormlite.table.DatabaseTable;
///**
// * orm测试数据库
// * @author liang_xs
// *
// */
//@DatabaseTable
//public class LoginUser {
//	@DatabaseField(id = true)
//	private int id;
//	@DatabaseField
//	private String Email;
//	@DatabaseField
//	private String HideEmail;
//	@DatabaseField
//	private String NickName = "";
//	@DatabaseField
//	private String Mobile;
//	@DatabaseField
//	private String HideMobile;
//	@DatabaseField
//	private String UserId;
//	@DatabaseField
//	private String LoginToken;
//	@DatabaseField
//	private String Sign;
//	@DatabaseField
//	private String AccessToken;
//	@DatabaseField
//	private String AccessTokenExpireTime;
////	"Type": 1,--用户类型
////	"Code": "GZ99396",--用户编号
////	"schoolId": 3,--学校编号
////	"BindDate": "2015-06-02 14:15:27"--绑定时间 StudentName 
//	@DatabaseField
//	private String Type;
//	@DatabaseField
//	private String Code;
//	@DatabaseField
//	private String schoolId ;
//	@DatabaseField
//	private String BindDate;
//	@DatabaseField
//	private String Name;
//	@DatabaseField
//	private String faceUrl;
//	@DatabaseField
//	private String rongYunId;
//	
//	public LoginUser() {
//		super();
//	}
//
//	public LoginUser(String nickName, String name) {
//		super();
//		NickName = nickName;
//		Name = name;
//	}
//
//	public String getEmail() {
//		return Email;
//	}
//
//	public void setEmail(String email) {
//		Email = email;
//	}
//
//	public String getHideEmail() {
//		return HideEmail;
//	}
//
//	public void setHideEmail(String hideEmail) {
//		HideEmail = hideEmail;
//	}
//
//	public String getNickName() {
//		return NickName;
//	}
//
//	public void setNickName(String nickName) {
//		NickName = nickName;
//	}
//
//	public String getMobile() {
//		return Mobile;
//	}
//
//	public void setMobile(String mobile) {
//		Mobile = mobile;
//	}
//
//	public String getHideMobile() {
//		return HideMobile;
//	}
//
//	public void setHideMobile(String hideMobile) {
//		HideMobile = hideMobile;
//	}
//
//	public String getUserId() {
//		return UserId;
//	}
//
//	public void setUserId(String userId) {
//		UserId = userId;
//	}
//
//	public int getId() {
//		return id;
//	}
//
//	public void setId(int id) {
//		this.id = id;
//	}
//
//	public String getLoginToken() {
//		return LoginToken;
//	}
//
//	public void setLoginToken(String loginToken) {
//		LoginToken = loginToken;
//	}
//
//	public String getSign() {
//		return Sign;
//	}
//
//	public void setSign(String sign) {
//		Sign = sign;
//	}
//
//	public String getAccessToken() {
//		return AccessToken;
//	}
//
//	public void setAccessToken(String accessToken) {
//		AccessToken = accessToken;
//	}
//
//	public String getAccessTokenExpireTime() {
//		return AccessTokenExpireTime;
//	}
//
//	public void setAccessTokenExpireTime(String accessTokenExpireTime) {
//		AccessTokenExpireTime = accessTokenExpireTime;
//	}
//
//	public String getType() {
//		return Type;
//	}
//
//	public void setType(String type) {
//		Type = type;
//	}
//
//	public String getCode() {
//		return Code;
//	}
//
//	public void setCode(String code) {
//		Code = code;
//	}
//
//	public String getSchoolId() {
//		return schoolId;
//	}
//
//	public void setSchoolId(String schoolId) {
//		this.schoolId = schoolId;
//	}
//
//	public String getBindDate() {
//		return BindDate;
//	}
//
//	public void setBindDate(String bindDate) {
//		BindDate = bindDate;
//	}
//
//	public String getName() {
//			return Name;
//	}
//
//	public void setName(String studentName) {
//		Name = studentName;
//	}
//
//	public String getFaceUrl() {
//		return faceUrl;
//	}
//
//	public void setFaceUrl(String faceUrl) {
//		this.faceUrl = faceUrl;
//	}
//
//	public String getRongYunId() {
//		return rongYunId;
//	}
//
//	public void setRongYunId(String rongYunId) {
//		this.rongYunId = rongYunId;
//	}
//	
//}
