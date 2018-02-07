package com.huapu.huafen.beans.pages;

import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.Count;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.Item;
import com.huapu.huafen.beans.UserData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac on 2018/1/9.
 */

public class HomeRefreshBean implements Serializable {
    private BannerData homeBanner1;
    private ArrayList<CampaignBanner> showcases;
    private ArrayList<Item> stars;
    private BannerData homeBanner2;
    private ArrayList<VipData> vip;
    private BannerData homeBanner3;
    private ArrayList<ActionData> poems;

    public class ActionData {

        public String title;
        public String image;
        public String action;
        public String target;
        public String categorieName;
        public String tag;
        public String summery;
        public String userName;
        public String note;
    }

    public class VipData{
        public String itemType;
        public GoodsData item;
        public Count counts;
        public UserData user;
        public int depositStatus;
    }

    public BannerData getHomeBanner1() {
        return homeBanner1;
    }

    public void setHomeBanner1(BannerData homeBanner1) {
        this.homeBanner1 = homeBanner1;
    }

    public ArrayList<CampaignBanner> getShowcases() {
        return showcases;
    }

    public void setShowcases(ArrayList<CampaignBanner> showcases) {
        this.showcases = showcases;
    }

    public ArrayList<Item> getStars() {
        return stars;
    }

    public void setStars(ArrayList<Item> stars) {
        this.stars = stars;
    }

    public BannerData getHomeBanner2() {
        return homeBanner2;
    }

    public void setHomeBanner2(BannerData homeBanner2) {
        this.homeBanner2 = homeBanner2;
    }

    public ArrayList<VipData> getVip() {
        return vip;
    }

    public void setVip(ArrayList<VipData> vip) {
        this.vip = vip;
    }

    public BannerData getHomeBanner3() {
        return homeBanner3;
    }

    public void setHomeBanner3(BannerData homeBanner3) {
        this.homeBanner3 = homeBanner3;
    }

    public ArrayList<ActionData> getPoems() {
        return poems;
    }

    public void setPoems(ArrayList<ActionData> poems) {
        this.poems = poems;
    }
}
