package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class City implements Serializable {

	private int did;
    private String name;
    private ArrayList<District> districts;


    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public ArrayList<District> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<District> districts) {
        this.districts = districts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
