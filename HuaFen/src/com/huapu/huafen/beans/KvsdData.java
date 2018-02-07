package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/10/11.
 */
public class KvsdData implements Serializable ,Cloneable{

    private String kvsd;//当前 kvs 的 SHA1 摘要
    private Kvs kvs;//当前的 kvs 配置，其内容为若干键值对
    private String grantedCampaigns;//有权利参与的活动，若干活动 id ，逗号分隔eg:"id_1,xid_2"
    private int notifications; // 未读系统消息数量，0表示没有
    private ArrayList<Region> regions;
    private String phone;
    private String regionsd;
    private String catsd;
    private ArrayList<Cat> cats;
    private UnreadMessageCounts unreadMessageCounts;
    private String activationToken;
    private String activationSecret;
    private String accessSecret;
    private String accessToken;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public String getActivationSecret() {
        return activationSecret;
    }

    public void setActivationSecret(String activationSecret) {
        this.activationSecret = activationSecret;
    }

    public UnreadMessageCounts getUnreadMessageCounts() {
        return unreadMessageCounts;
    }

    public void setUnreadMessageCounts(UnreadMessageCounts unreadMessageCounts) {
        this.unreadMessageCounts = unreadMessageCounts;
    }

    public String getCatsd() {
        return catsd;
    }

    public void setCatsd(String catsd) {
        this.catsd = catsd;
    }

    public ArrayList<Cat> getCats() {
        return cats;
    }

    public void setCats(ArrayList<Cat> cats) {
        this.cats = cats;
    }

    public String getRegionsd() {
        return regionsd;
    }

    public void setRegionsd(String regionsd) {
        this.regionsd = regionsd;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Region> regions) {
        this.regions = regions;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public String getKvsd() {
        return kvsd;
    }

    public void setKvsd(String kvsd) {
        this.kvsd = kvsd;
    }

    public Kvs getKvs() {
        return kvs;
    }

    public void setKvs(Kvs kvs) {
        this.kvs = kvs;
    }

    public String getGrantedCampaigns() {
        return grantedCampaigns;
    }

    public void setGrantedCampaigns(String grantedCampaigns) {
        this.grantedCampaigns = grantedCampaigns;
    }

//    public static KvsdData mockData(KvsdData data){
//
//        KvsdData k  = null;
//        try {
//            k = (KvsdData) data.clone();
//            Kvs kvsCopy = k.getKvs();
//            kvsCopy.setCateBanners1(kvsCopy.getCampaignBanners());
//            kvsCopy.setCateBanners2(kvsCopy.getCampaignBanners());
//            kvsCopy.setHomeBanners1(kvsCopy.getCampaignBanners());
//            kvsCopy.setHomeBanners2(kvsCopy.getCampaignBanners());
//            ArrayList<CampaignBanner> list = kvsCopy.getCampaignBanners();
//            CampaignBanner campaignBanner = list.get(0);
//
//            list.add(campaignBanner);
//            list.add(campaignBanner);
//            list.add(campaignBanner);
//            list.add(campaignBanner);
//            kvsCopy.setShowcases(list);
//        } catch (CloneNotSupportedException e) {
//            e.printStackTrace();
//        }
//        return k;
//    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
