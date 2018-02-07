package com.huapu.huafen.beans.pages;

import java.io.Serializable;

/**
 * 组件
 * Created by dengbin on 18/1/9.
 */
public class ButtonBean implements Serializable {
    private String title;
    private String action;
    private MoreBean.Target target;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public MoreBean.Target getTarget() {
        return target;
    }

    public void setTarget(MoreBean.Target target) {
        this.target = target;
    }
}
