package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class Cat implements Serializable{
	private int cid;
	private String name;
	private String icon;
	private String title;
	private ArrayList<Cat> cats;
	public boolean isCheck;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ArrayList<Cat> getCats() {
		return cats;
	}

	public void setCats(ArrayList<Cat> cats) {
		this.cats = cats;
	}
}
