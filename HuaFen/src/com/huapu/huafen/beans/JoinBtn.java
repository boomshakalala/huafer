package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/11.
 * title 文字
 * icon 图片地址
 */
public class JoinBtn implements Serializable {

    private String title;
    private String icon;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "JoinBtn{" +
                "title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
