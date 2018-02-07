package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/11/14.
 */
public class LocationData implements Serializable {

    public double gLat; // 纬度

    public double gLng; // 经度

    public String city;

    public String district;

    public String province;

    public String country;

    public String cityCode;

    public LocationData(double gLat, double gLng, String city) {
        this.gLat = gLat;
        this.gLng = gLng;
        this.city = city;
    }

    public LocationData() {
    }
}
