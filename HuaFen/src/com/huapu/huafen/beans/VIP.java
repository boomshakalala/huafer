package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/10/11.
 */
public class VIP implements Serializable{
    private ArrayList<CampaignBanner> banners;

    public ArrayList<CampaignBanner> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<CampaignBanner> banners) {
        this.banners = banners;
    }
}
