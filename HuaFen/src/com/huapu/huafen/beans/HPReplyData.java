package com.huapu.huafen.beans;

public class HPReplyData extends BaseResult{
	public boolean removeData = false;
	private UserData user;
	private HPReply reply;
	private UserData relatedUser;
	private int commentable;

	public int getCommentable() {
		return commentable;
	}

	public void setCommentable(int commentable) {
		this.commentable = commentable;
	}

	public UserData getRelatedUser() {
		return relatedUser;
	}

	public void setRelatedUser(UserData relatedUser) {
		this.relatedUser = relatedUser;
	}

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public HPReply getReply() {
		return reply;
	}

	public void setReply(HPReply reply) {
		this.reply = reply;
	}
}
