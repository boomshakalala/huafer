package com.huapu.huafen.beans;

import java.util.ArrayList;
import java.util.List;

public class WantListMap extends BaseResult{

	private List<WantList> wantList = new ArrayList<WantList>();
	private int page;
	private int count;
	
	
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public List<WantList> getWantList() {
		return wantList;
	}
	public void setWantList(List<WantList> wantList) {
		this.wantList = wantList;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
}
