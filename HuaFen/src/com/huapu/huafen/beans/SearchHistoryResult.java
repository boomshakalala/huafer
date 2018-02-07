package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.LinkedList;

public class SearchHistoryResult extends BaseResult{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String clearVar;
	
	public Data data;
	
	public SearchHistoryResult(){
		
	}
	
	
	public static class Data implements Serializable{
		public LinkedList<CodeValuePair> searchHistory;
	}
	
}
