package com.huapu.huafen.events;

/**
 * Created by qwe on 2017/7/26.
 */

public class PriceEvent {
    /**
     * 1 high
     * 0 low
     */
    private int state = 1;

    public PriceEvent(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
