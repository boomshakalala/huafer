package com.huapu.huafen.beans;

import java.util.List;

/**
 * 留言列表
 */
public class HPCommentsResult extends BaseResult{

	private int page;

	private GoodsData goods;
	private UserData goodsOwner;
	private List<HPCommentData> comments;
	private int count;
	private int commentable;
	public int replyCount;

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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}


	public List<HPCommentData> getComments() {
		return comments;
	}

	public void setComments(List<HPCommentData> comments) {
		this.comments = comments;
	}

	public GoodsData getGoods() {
		return goods;
	}

	public void setGoods(GoodsData goods) {
		this.goods = goods;
	}

}
