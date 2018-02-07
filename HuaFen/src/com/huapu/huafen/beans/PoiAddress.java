package com.huapu.huafen.beans;

import java.io.Serializable;

public class PoiAddress implements Serializable {
    public double longitude;//经度
    public double latitude;//纬度
    public String title;//信息标题
    public String text;//信息内容
    public String poiId;
    public PoiAddress(double lon, double lat, String title, String text ,String poiId){
        this.longitude = lon;
        this.latitude = lat;
        this.title = title;
        this.text = text;
        this.poiId = poiId;
    }

}
