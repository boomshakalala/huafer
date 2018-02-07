package com.huapu.huafen.utils;

import com.huapu.huafen.beans.CodeValuePair;
import com.huapu.huafen.beans.SearchHistoryResult;

import java.util.LinkedList;

public class SearchArticleHistoryHelper {


	public static void put(CodeValuePair p){
		SearchHistoryResult result = CommonPreference.getObject(
				CommonPreference.SEARCH_ARTICLE_HISTORY_LIST_KEY,SearchHistoryResult.class);
		if(result==null || result.data == null || result.data.searchHistory == null){
			result=new SearchHistoryResult();
			result.clearVar="清除记录";
			result.data=new SearchHistoryResult.Data();
			result.data.searchHistory=new LinkedList<>();
		}
		
		if(!result.data.searchHistory.contains(p)){
			result.data.searchHistory.push(p);
		}
		CommonPreference.setObject(CommonPreference.SEARCH_ARTICLE_HISTORY_LIST_KEY,result);
	}
	
	public static void clearSearchHistory(){
		SearchHistoryResult result = CommonPreference.getObject(
				CommonPreference.SEARCH_ARTICLE_HISTORY_LIST_KEY,SearchHistoryResult.class);
		result.data.searchHistory=new LinkedList<>();
		CommonPreference.setObject(CommonPreference.SEARCH_ARTICLE_HISTORY_LIST_KEY, result);
	}
	
	public static boolean isEmpty(){
		SearchHistoryResult result = CommonPreference.getObject(
				CommonPreference.SEARCH_ARTICLE_HISTORY_LIST_KEY,SearchHistoryResult.class);
		return result==null||ArrayUtil.isEmpty(result.data.searchHistory);
	}
	
	public static LinkedList<CodeValuePair> getSearchHistory(){
		SearchHistoryResult result = CommonPreference.getObject(
				CommonPreference.SEARCH_ARTICLE_HISTORY_LIST_KEY,SearchHistoryResult.class);
		if(result==null){
			return new LinkedList<>();
		}
		if(result.data == null || result.data.searchHistory == null) {
			return new LinkedList<>();
		}
		return result.data.searchHistory;
	}
	
}
