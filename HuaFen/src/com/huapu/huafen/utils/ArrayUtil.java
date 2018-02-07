package com.huapu.huafen.utils;

import java.util.List;

public class ArrayUtil {

	public static <T> boolean isEmpty(List<T> list){
		if(list!=null&&!list.isEmpty()){
			return false;
		}
		return true;
	}
	
	public static <T> boolean isEmpty(T[] arr){
		if(arr!=null&&arr.length!=0){
			return false;
		}
		return true;
	}
	
}
