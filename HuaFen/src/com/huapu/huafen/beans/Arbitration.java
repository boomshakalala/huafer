
package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by liang_xs by 2016/11/02
 */
public class Arbitration implements Serializable {

    private int aid;
    private String title;
    private long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
