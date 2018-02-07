package com.huapu.huafen.beans.pages;

import com.huapu.huafen.beans.Banner;

import java.io.Serializable;
import java.util.List;

/**
 * 组件
 * Created by dengbin on 18/1/9.
 */
public class ShowcaseBean implements Serializable {

    private String title;
    private String subtitle;
    private MoreBean more;
    private List<Banner> banners;

    private List<ItemDataBean> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public MoreBean getMore() {
        return more;
    }

    public void setMore(MoreBean more) {
        this.more = more;
    }

    public List<ItemDataBean> getItems() {
        return items;
    }

    public void setItems(List<ItemDataBean> items) {
        this.items = items;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}
