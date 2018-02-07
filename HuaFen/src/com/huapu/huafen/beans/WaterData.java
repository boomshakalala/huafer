package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017/1/18.
 */

public class WaterData implements Serializable {

    private DisplayTime displayTime;

    public DisplayTime getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(DisplayTime displayTime) {
        this.displayTime = displayTime;
    }
}
