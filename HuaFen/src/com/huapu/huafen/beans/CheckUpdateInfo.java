package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/11.
 */
public class CheckUpdateInfo implements Serializable {

    private int level;
    private String url;
    private String note;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
