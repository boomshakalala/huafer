package com.huapu.huafen.beans;

import java.io.Serializable;

public class CodeValuePair implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int code;
	public String value;
	public CodeValuePair(){
		
	}
	public CodeValuePair(int code,String value){
		this.code=code;
		this.value=value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + code;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeValuePair other = (CodeValuePair) obj;
		if (code != other.code)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	
}
