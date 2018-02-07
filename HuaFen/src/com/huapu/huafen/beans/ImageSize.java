package com.huapu.huafen.beans;

import java.io.Serializable;

public class ImageSize implements Serializable {
    private int w;
    private int h;

    public ImageSize(int i, int j) {
        this.w = i;
        this.h = j;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
