package com.huapu.huafen.beans;

import com.huapu.huafen.beans.pages.MoreBean;

/**
 * 轮播图
 *
 * @author liang_xs
 */
public class Banner extends BaseResult {
    private String bannerImage;
    private int bannerTypeId;
    private String bannerTitle;
    private String bannerLink;
    private String bannerContent;
    private int bannerType;

    // 新版
    private String image;
    private String action;
    private MoreBean.Target target;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public int getBannerTypeId() {
        return bannerTypeId;
    }

    public void setBannerTypeId(int bannerTypeId) {
        this.bannerTypeId = bannerTypeId;
    }

    public String getBannerTitle() {
        return bannerTitle;
    }

    public void setBannerTitle(String bannerTitle) {
        this.bannerTitle = bannerTitle;
    }

    public String getBannerLink() {
        return bannerLink;
    }

    public void setBannerLink(String bannerLink) {
        this.bannerLink = bannerLink;
    }

    public String getBannerContent() {
        return bannerContent;
    }

    public void setBannerContent(String bannerContent) {
        this.bannerContent = bannerContent;
    }

    public int getBannerType() {
        return bannerType;
    }

    public void setBannerType(int bannerType) {
        this.bannerType = bannerType;
    }


}
