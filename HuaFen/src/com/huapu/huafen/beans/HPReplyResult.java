package com.huapu.huafen.beans;

import java.util.ArrayList;

public class HPReplyResult extends BaseResult{
	private HPComment comment;
	private UserData user;
	private GoodsData goods;
	private int page;
	private ArrayList<HPReplyData> replies;
	private UserData goodsOwner;
	private int commentable;

	public int getCommentable() {
		return commentable;
	}

	public void setCommentable(int commentable) {
		this.commentable = commentable;
	}

	public UserData getGoodsOwner() {
		return goodsOwner;
	}

	public void setGoodsOwner(UserData goodsOwner) {
		this.goodsOwner = goodsOwner;
	}


	public GoodsData getGoods() {
		return goods;
	}

	public void setGoods(GoodsData goods) {
		this.goods = goods;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public HPComment getComment() {
		return comment;
	}

	public void setComment(HPComment comment) {
		this.comment = comment;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public ArrayList<HPReplyData> getReplies() {
		return replies;
	}

	public void setReplies(ArrayList<HPReplyData> replies) {
		this.replies = replies;
	}
}
