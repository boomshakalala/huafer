package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by lalo on 2016/11/14.
 */
public class FilterAreaData implements Serializable ,Cloneable{

    private int did;
    private String name;
    private int dc;
    private ArrayList<FilterAreaData> districts;
    private ArrayList<FilterAreaData> cities;
    public boolean isCheck;
    public boolean isLocationCity;
    public int provinceDid;
    public boolean isShowLocationIcon;

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

    public int getDc() {
        return dc;
    }

    public void setDc(int dc) {
        this.dc = dc;
    }

    public ArrayList<FilterAreaData> getDistricts() {
        return districts;
    }

    public void setDistricts(ArrayList<FilterAreaData> districts) {
        this.districts = districts;
    }

    public ArrayList<FilterAreaData> getCities() {
        return cities;
    }

    public void setCities(ArrayList<FilterAreaData> cities) {
        this.cities = cities;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
