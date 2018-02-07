package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/15.
 */
public class CampaignInfo implements Serializable {
    private String title;
    private int layout;
    private int cid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
