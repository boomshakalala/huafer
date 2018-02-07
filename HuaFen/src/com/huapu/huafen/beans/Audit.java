package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/10/11.
 */
public class Audit implements Serializable {
    private int buyable;
    private int shareable;

    public int getBuyable() {
        return buyable;
    }

    public void setBuyable(int buyable) {
        this.buyable = buyable;
    }

    public int getShareable() {
        return shareable;
    }

    public void setShareable(int shareable) {
        this.shareable = shareable;
    }
}
