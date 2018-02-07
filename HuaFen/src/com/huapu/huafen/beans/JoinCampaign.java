package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by mac on 2018/1/3.
 */

public class JoinCampaign implements Serializable{
    private int status;
    private long campaignsBeginTime;
    private long campaignsEndTime;
    private long joinBeginTime;
    private long joinEndTime;
    private String banner;
    private String cats;
    private String prices;
    private int campaignId;
    private String campaignName;
    private String campaignNote;
    private String campaignImg;

    private boolean Checked;

    public boolean isChecked() {
        return Checked;
    }

    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setCampaignsBeginTime(long campaignsBeginTime) {
        this.campaignsBeginTime = campaignsBeginTime;
    }
    public long getCampaignsBeginTime() {
        return campaignsBeginTime;
    }

    public void setCampaignsEndTime(long campaignsEndTime) {
        this.campaignsEndTime = campaignsEndTime;
    }
    public long getCampaignsEndTime() {
        return campaignsEndTime;
    }

    public void setJoinBeginTime(long joinBeginTime) {
        this.joinBeginTime = joinBeginTime;
    }
    public long getJoinBeginTime() {
        return joinBeginTime;
    }

    public void setJoinEndTime(long joinEndTime) {
        this.joinEndTime = joinEndTime;
    }
    public long getJoinEndTime() {
        return joinEndTime;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }
    public String getBanner() {
        return banner;
    }

    public void setCats(String cats) {
        this.cats = cats;
    }
    public String getCats() {
        return cats;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }
    public String getPrices() {
        return prices;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }
    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }
    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignNote(String campaignNote) {
        this.campaignNote = campaignNote;
    }
    public String getCampaignNote() {
        return campaignNote;
    }

    public void setCampaignImg(String campaignImg) {
        this.campaignImg = campaignImg;
    }
    public String getCampaignImg() {
        return campaignImg;
    }
}
