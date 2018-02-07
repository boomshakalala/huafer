package com.huapu.huafen.beans;

public class RongWord extends BaseResult{

	private String word;
	private String notice;

	public RongWord() {
	}

	public RongWord(String word, String notice) {
		this.word = word;
		this.notice = notice;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getNotice() {
		return notice;
	}

	public void setNotice(String notice) {
		this.notice = notice;
	}
}
