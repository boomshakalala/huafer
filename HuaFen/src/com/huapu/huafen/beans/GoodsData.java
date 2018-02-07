package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/9/24.
 */
public class GoodsData implements Serializable {

    private List<String> goodsImgs;//
    private String name;//
    private String brand;//
    private int brandId;//
    private int goodsId;//
    private List<Type> types;//
    private String content;//
    private boolean isFreeDelivery;
    private Area area;//
    private boolean liked;//
    private int isNew;
    public int bidCount;
    public int postType;
    /**
     * 1：在售
     * 2：预售中
     * 3：交易中
     * 4：已售出
     * 5：下架
     * 6:被举报
     * 7:已删除
     */
    private int goodsState;
    private int privileges;
    private int price;
    private int pastPrice;
    private int campaignId;
    private int likeCount;
    /**
     * 1 image 2 video
     */
    private int imageType;

    private String videoPath;

    private String videoCover;
    private int distance;

    private int discount;
    private int postage;
    private long presell;
    private long showTime;
    private int firstCateId;
    public int isAuction;
    public int hammerPrice;
    public int sellPric;
    public int bidDeposit;
    public int bidIncrement;
    public long bidStartTime;
    public long bidEndTime;
    public long bidder;
    public int hasBidDepositPayed;
    public int crowd;
    public boolean collected;
    public long currentTimeMillis;

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getCrowd() {
        return crowd;
    }

    public void setCrowd(int crowd) {
        this.crowd = crowd;
    }

    public int getFirstCateId() {
        return firstCateId;
    }

    public void setFirstCateId(int firstCateId) {
        this.firstCateId = firstCateId;
    }

    public long getShowTime() {
        return showTime;
    }

    public void setShowTime(long showTime) {
        this.showTime = showTime;
    }

    public long getPresell() {
        return presell;
    }

    public void setPresell(long presell) {
        this.presell = presell;
    }

    public int getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(int shippingCost) {
        this.shippingCost = shippingCost;
    }

    private int shippingCost;

    public int getPostage() {
        return postage;
    }

    public void setPostage(int postage) {
        this.postage = postage;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }


    public int getDiscount() {
        return discount;
    }

    private int auditStatus;

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }


    public void setDiscount(int discount) {
        this.discount = discount;
    }


    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public int getPrivileges() {
        return privileges;
    }

    public void setPrivileges(int privileges) {
        this.privileges = privileges;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPastPrice() {
        return pastPrice;
    }

    public void setPastPrice(int pastPrice) {
        this.pastPrice = pastPrice;
    }

    public int getGoodsState() {
        return goodsState;
    }

    public void setGoodsState(int goodsState) {
        this.goodsState = goodsState;
    }

    public boolean getIsFreeDelivery() {
        return isFreeDelivery;
    }

    public void setIsFreeDelivery(boolean freeDelivery) {
        isFreeDelivery = freeDelivery;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public List<String> getGoodsImgs() {
        return goodsImgs;
    }

    public void setGoodsImgs(List<String> goodsImgs) {
        this.goodsImgs = goodsImgs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoCover() {
        return videoCover;
    }

    public void setVideoCover(String videoCover) {
        this.videoCover = videoCover;
    }
}
