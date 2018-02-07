package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/12/12.
 */
public class VBanner implements Serializable{

    public int type;
    public String imgUrl;
    public String videoPath;
    public int position;

    public VBanner(int type, String imgUrl) {
        this.type = type;
        this.imgUrl = imgUrl;
    }

    public VBanner() {

    }
}
