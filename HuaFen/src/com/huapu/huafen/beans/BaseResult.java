package com.huapu.huafen.beans;

import java.io.Serializable;

public class BaseResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int code;
	public String msg;
	public String obj;
	public int nextpage;
	public static final int ERROR_TYPE_FOR_TOAST = 1;
	public static final int ERROR_TYPE_FOR_LOGIN = 2;
	public static final int ERROR_TYPE_FOR_LOGOUT = 3;
	public static final int ERROR_TYPE_FOR_DATA_ERROR = 4;//新版加密请求规则加入 v2.1.0
		/**	 * @param status
	 * @return 1只做提示 ， 2验证失败，重新登录；3账号被登出，提示并重新登录
	 * 通过status 获取错误消息
	 */
	public static int getErrorType(int status){
		int type = 1;
		if(status == 202){
			type = 3;
		} else if(status == 500) {

		} else if(status == 412) {
			type = ERROR_TYPE_FOR_DATA_ERROR;
		} else {
			type = 1;
		}
		return type;
	}

}
