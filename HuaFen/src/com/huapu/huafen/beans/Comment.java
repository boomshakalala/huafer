package com.huapu.huafen.beans;

import java.util.ArrayList;

public class Comment extends BaseResult{
	private int commentCount;
	private ArrayList<UserInfo> users;
	private ArrayList<CommentLabel> commentLabels;
	private UserInfo userInfo;
	private GoodsInfo goodsInfo;
	private ArrayList<String> commentImgs = new ArrayList<String>();
	private int point;
	private int isSatisfied;
	private long time;
	private int commentId;
	private String commentContent;
	private String sellerCommentContent;
	
	
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
	public UserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}
	public GoodsInfo getGoodsInfo() {
		return goodsInfo;
	}
	public void setGoodsInfo(GoodsInfo goodsInfo) {
		this.goodsInfo = goodsInfo;
	}
	public ArrayList<String> getCommentImgs() {
		return commentImgs;
	}
	public void setCommentImgs(ArrayList<String> commentImgs) {
		this.commentImgs = commentImgs;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public int getIsSatisfied() {
		return isSatisfied;
	}
	public void setIsSatisfied(int isSatisfied) {
		this.isSatisfied = isSatisfied;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getCommentId() {
		return commentId;
	}
	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public ArrayList<CommentLabel> getCommentLabels() {
		return commentLabels;
	}
	public void setCommentLabels(ArrayList<CommentLabel> commentLabels) {
		this.commentLabels = commentLabels;
	}
	public ArrayList<UserInfo> getUsers() {
		return users;
	}
	public void setUsers(ArrayList<UserInfo> users) {
		this.users = users;
	}
	
	public void setSellerCommentContent(String sellerCommentContent) {
		this.sellerCommentContent = sellerCommentContent;
	}
	
	public String getSellerCommentContent() {
		return sellerCommentContent;
	}
	
}
