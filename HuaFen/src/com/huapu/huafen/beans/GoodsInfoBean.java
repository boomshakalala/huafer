package com.huapu.huafen.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class GoodsInfoBean extends BaseResult{

	private UserInfo userInfo;
	private GoodsInfo goodsInfo;
	private Comment comments;
	private OrderInfo orderInfo;
	private BannerData banner1;
	private HPCommentsResult comment;
	private int commentable;
	public TreeMap<String,Integer> rateCount;
	public String lastVisitText;
	public ArrayList<Item> articles;
	public List<Item> recItems;
	public String recTraceId;
	public int unshelvable = -1;
	public int editable = -1;
	public Tips1 tips1;

	public BidInfo bidInfo;

	public int getCommentable() {
		return commentable;
	}

	public void setCommentable(int commentable) {
		this.commentable = commentable;
	}

	public HPCommentsResult getComment() {
		return comment;
	}

	public void setComment(HPCommentsResult comment) {
		this.comment = comment;
	}

	public BannerData getBanner1() {
		return banner1;
	}

	public void setBanner1(BannerData banner1) {
		this.banner1 = banner1;
	}



	public OrderInfo getOrderInfo() {
		return orderInfo;
	}
	public void setOrderInfo(OrderInfo orderInfo) {
		this.orderInfo = orderInfo;
	}
	public Comment getComments() {
		return comments;
	}
	public void setComments(Comment comments) {
		this.comments = comments;
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
	
}
