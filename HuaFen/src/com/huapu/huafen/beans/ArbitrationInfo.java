package com.huapu.huafen.beans;

public class ArbitrationInfo extends BaseResult{
	private long arbitrationTime;
	private long arbitrationUserId;
	private long arbitrationId;

	public long getArbitrationTime() {
		return arbitrationTime;
	}

	public void setArbitrationTime(long arbitrationTime) {
		this.arbitrationTime = arbitrationTime;
	}

	public long getArbitrationUserId() {
		return arbitrationUserId;
	}

	public void setArbitrationUserId(long arbitrationUserId) {
		this.arbitrationUserId = arbitrationUserId;
	}

	public long getArbitrationId() {
		return arbitrationId;
	}

	public void setArbitrationId(long arbitrationId) {
		this.arbitrationId = arbitrationId;
	}

}
