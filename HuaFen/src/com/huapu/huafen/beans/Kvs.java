package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/10/11.
 */
public class Kvs implements Serializable {

    private ArrayList<Campaign> campaigns;
    private ArrayList<CampaignBanner> campaignBanners;
    private ArrayList<CampaignBanner> showcases;
    private BannerData homeBanner1;
    private BannerData homeBanner1_5;
    private BannerData homeBanner2;
    private BannerData cateBanner1;
    private BannerData cateBanner2;
    private BannerData vipUserBanner;
    private BannerData vipGoodsBanner;
    private BannerData starUserBanner;
    private BannerData starGoodsBanner;
    private BannerData followingBanner;
    private BannerData brandnewGoodsBanner;

    public BannerData getOneEvent() {
        return oneEvent;
    }

    public void setOneEvent(BannerData oneEvent) {
        this.oneEvent = oneEvent;
    }

    private BannerData oneEvent;
    private VIP vip;
    private ArrayList<MyGoods> myGoods;
    private Audit audit;
    private Sale sale;
    private Star star;
    private SplashScreen splashScreen;

    public BannerData getFollowingBanner() {
        return followingBanner;
    }

    public void setFollowingBanner(BannerData focusBanner) {
        this.followingBanner = focusBanner;
    }

    public BannerData getBrandnewGoodsBanner() {
        return brandnewGoodsBanner;
    }

    public void setBrandnewGoodsBanner(BannerData brandnewGoodsBanner) {
        this.brandnewGoodsBanner = brandnewGoodsBanner;
    }

    public SplashScreen getSplashScreen() {
        return splashScreen;
    }

    public void setSplashScreen(SplashScreen splashScreen) {
        this.splashScreen = splashScreen;
    }

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public Audit getAudit() {
        return audit;
    }

    public BannerData getHomeBanner1_5() {
        return homeBanner1_5;
    }

    public void setHomeBanner1_5(BannerData homeBanner1_5) {
        this.homeBanner1_5 = homeBanner1_5;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public ArrayList<CampaignBanner> getShowcases() {
        return showcases;
    }

    public void setShowcases(ArrayList<CampaignBanner> showcases) {
        this.showcases = showcases;
    }


    public BannerData getHomeBanner1() {
        return homeBanner1;
    }

    public void setHomeBanner1(BannerData homeBanner1) {
        this.homeBanner1 = homeBanner1;
    }

    public BannerData getHomeBanner2() {
        return homeBanner2;
    }

    public void setHomeBanner2(BannerData homeBanner2) {
        this.homeBanner2 = homeBanner2;
    }

    public BannerData getCateBanner1() {
        return cateBanner1;
    }

    public void setCateBanner1(BannerData cateBanner1) {
        this.cateBanner1 = cateBanner1;
    }

    public BannerData getCateBanner2() {
        return cateBanner2;
    }

    public void setCateBanner2(BannerData cateBanner2) {
        this.cateBanner2 = cateBanner2;
    }

    public ArrayList<MyGoods> getMyGoods() {
        return myGoods;
    }

    public void setMyGoods(ArrayList<MyGoods> myGoods) {
        this.myGoods = myGoods;
    }

    public VIP getVip() {
        return vip;
    }

    public void setVip(VIP vip) {
        this.vip = vip;
    }

    public ArrayList<Campaign> getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(ArrayList<Campaign> campaigns) {
        this.campaigns = campaigns;
    }

    public ArrayList<CampaignBanner> getCampaignBanners() {
        return campaignBanners;
    }

    public void setCampaignBanners(ArrayList<CampaignBanner> campaignBanners) {
        this.campaignBanners = campaignBanners;
    }

    public BannerData getVipUserBanner() {
        return vipUserBanner;
    }

    public void setVipUserBanner(BannerData vipUserBanner) {
        this.vipUserBanner = vipUserBanner;
    }

    public BannerData getVipGoodsBanner() {
        return vipGoodsBanner;
    }

    public void setVipGoodsBanner(BannerData vipGoodsBanner) {
        this.vipGoodsBanner = vipGoodsBanner;
    }

    public BannerData getStarUserBanner() {
        return starUserBanner;
    }

    public void setStarUserBanner(BannerData starUserBanner) {
        this.starUserBanner = starUserBanner;
    }

    public BannerData getStarGoodsBanner() {
        return starGoodsBanner;
    }

    public void setStarGoodsBanner(BannerData starGoodsBanner) {
        this.starGoodsBanner = starGoodsBanner;
    }
}
