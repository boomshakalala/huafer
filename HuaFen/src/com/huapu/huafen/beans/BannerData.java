package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/11/8.
 */
public class BannerData implements Serializable {

    private boolean autoLoop;

    private ArrayList<CampaignBanner> banners;

    public boolean getAutoLoop() {
        return autoLoop;
    }

    public void setAutoLoop(boolean autoLoop) {
        this.autoLoop = autoLoop;
    }

    public ArrayList<CampaignBanner> getBanners() {
        return banners;
    }

    public void setBanners(ArrayList<CampaignBanner> banners) {
        this.banners = banners;
    }
}
