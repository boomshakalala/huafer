package com.huapu.huafen.beans;

import java.io.Serializable;

public class District implements Serializable {

	private int did;
    private String name;

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
