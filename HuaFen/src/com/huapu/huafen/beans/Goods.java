
package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by liang_xs by 2016/11/02
 */
public class Goods implements Serializable {

    private GoodsData goodsData;
    private GoodsValue goodsValue;
    private AuditResult auditResult;
    private DisplayTime displayTime;
    private int hasCandidateCampaigns;


    public int getHasCandidateCampaigns() {
        return hasCandidateCampaigns;
    }

    public void setHasCandidateCampaigns(int hasCandidateCampaigns) {
        this.hasCandidateCampaigns = hasCandidateCampaigns;
    }

    public DisplayTime getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(DisplayTime displayTime) {
        this.displayTime = displayTime;
    }

    private int page;

    public GoodsValue getGoodsValue() {
        return goodsValue;
    }

    public void setGoodsValue(GoodsValue goodsValue) {
        this.goodsValue = goodsValue;
    }

    public AuditResult getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(AuditResult auditResult) {
        this.auditResult = auditResult;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public GoodsData getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
    }
}
