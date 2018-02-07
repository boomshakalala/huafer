package com.huapu.huafen.beans.pages;

import java.io.Serializable;
import java.util.List;

/**
 * 每日上新
 * Created by dengbin on 18/1/9.
 */
public class EverydayFreshBean implements Serializable {

    private String pageId;
    private String title;
    private List<ComponentBean> components;

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ComponentBean> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentBean> components) {
        this.components = components;
    }
}
