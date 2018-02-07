package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/10/11.
 */
public class Campaign implements Serializable {

    private int cid;//活动 id
    private JoinBtn joinBtn;//加入活动的按钮
    private String joinPeriod;//可加入活动的时间段，两个时间戳，逗号分隔，A,B，则时间为 A<=x<B
    private String caption;//注意事项
    private int[] prices;//价格，0 表示可修改，其他表示只能使用此值
    private String peroid;//活动时间段，参考 joinPeriod
    private String name; // 活动名称
    private String title;
    private ArrayList<CampaignBanner> banners;
    private String ages;

    public String getAges() {
        return ages;
    }

    public void setAges(String ages) {
        this.ages = ages;
    }

    public ArrayList<CampaignBanner> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<CampaignBanner> banners) {
        this.banners = banners;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public JoinBtn getJoinBtn() {
        return joinBtn;
    }

    public void setJoinBtn(JoinBtn joinBtn) {
        this.joinBtn = joinBtn;
    }

    public String getJoinPeriod() {
        return joinPeriod;
    }

    public void setJoinPeriod(String joinPeriod) {
        this.joinPeriod = joinPeriod;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int []getPrices() {
        return prices;
    }

    public void setPrices(int []prices) {
        this.prices = prices;
    }

    public String getPeroid() {
        return peroid;
    }

    public void setPeroid(String peroid) {
        this.peroid = peroid;
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "cid=" + cid +
                ", joinBtn=" + joinBtn +
                ", joinPeriod='" + joinPeriod + '\'' +
                ", caption='" + caption + '\'' +
                ", price=" + prices +
                ", peroid='" + peroid + '\'' +
                '}';
    }
}
