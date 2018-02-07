package com.huapu.huafen.beans;

import java.util.ArrayList;

public class HPCommentData extends BaseResult{

	private UserData user;
	private HPComment comment;
	private GoodsData goods;
	private ArrayList<HPReplyData> replies;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	private Item item;


	private int commentable;

	public int getCommentable() {
		return commentable;
	}

	public void setCommentable(int commentable) {
		this.commentable = commentable;
	}

	public GoodsData getGoods() {
		return goods;
	}

	public void setGoods(GoodsData goods) {
		this.goods = goods;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public HPComment getComment() {
		return comment;
	}

	public void setComment(HPComment comment) {
		this.comment = comment;
	}

	public ArrayList<HPReplyData> getReplies() {
		return replies;
	}

	public void setReplies(ArrayList<HPReplyData> replies) {
		this.replies = replies;
	}
}
