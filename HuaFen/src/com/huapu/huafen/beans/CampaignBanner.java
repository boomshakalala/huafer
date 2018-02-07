package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/11.
 */
public class CampaignBanner implements Serializable{
    /**
     * 可用的 action 及其可接受的 target：
     * openWebview url
     * openGoodsDetail goodsId
     * openUserShopDetail userId
     * openCompaignList null
     */
    private String title;//标题
    private String image;//图片
    private String note;//备注
    private String action;//动作
    private String target;//动作的参数（目标），注意：类型为字符串，即使目标是整形

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "CampaignBanner{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", note='" + note + '\'' +
                ", action='" + action + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
}
