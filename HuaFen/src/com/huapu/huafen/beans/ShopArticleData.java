package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017
 */
public class ShopArticleData implements Serializable {
    private String itemType;
    private items item;
    private ucounts counts;
    private users user;


    public users getUser() {
        return user;
    }

    public void setUser(users user) {
        this.user = user;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public items getItem() {
        return item;
    }

    public void setItem(items item) {
        this.item = item;
    }

    public ucounts getCounts() {
        return counts;
    }

    public void setCounts(ucounts counts) {
        this.counts = counts;
    }


    public static class items implements Serializable {
        private String title;
        private String summary;
        private String titleMediaType;
        private String titleMediaUrl;
        private int width;
        private int height;
        private String categoryId;
        private boolean collected;
        private String articleId;
        //goods
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

        public int isAuction;
        public int hammerPrice;
        public int sellPric;
        public int bidDeposit;
        public int bidIncrement;
        public long bidStartTime;
        public long bidEndTime;
        public long bidder;
        public boolean hasBidDepositPayed;


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

        public boolean isFreeDelivery() {
            return isFreeDelivery;
        }

        public void setFreeDelivery(boolean freeDelivery) {
            isFreeDelivery = freeDelivery;
        }

        public Area getArea() {
            return area;
        }

        public void setArea(Area area) {
            this.area = area;
        }

        public int getIsNew() {
            return isNew;
        }

        public void setIsNew(int isNew) {
            this.isNew = isNew;
        }

        public int getGoodsState() {
            return goodsState;
        }

        public void setGoodsState(int goodsState) {
            this.goodsState = goodsState;
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

        public int getCampaignId() {
            return campaignId;
        }

        public void setCampaignId(int campaignId) {
            this.campaignId = campaignId;
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

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public int getPostage() {
            return postage;
        }

        public void setPostage(int postage) {
            this.postage = postage;
        }

        public long getPresell() {
            return presell;
        }

        public void setPresell(long presell) {
            this.presell = presell;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getTitleMediaType() {
            return titleMediaType;
        }

        public void setTitleMediaType(String titleMediaType) {
            this.titleMediaType = titleMediaType;
        }

        public String getTitleMediaUrl() {
            return titleMediaUrl;
        }

        public void setTitleMediaUrl(String titleMediaUrl) {
            this.titleMediaUrl = titleMediaUrl;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

    }

    public static class ucounts implements Serializable {
        private String pv;
        private String collection;

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public String getPv() {
            return pv;
        }

        public void setPv(String pv) {
            this.pv = pv;
        }
    }

    public static class users implements Serializable {
        private int gender;
        private int zmCreditPoint;
        private int fellowship;
        private String userId;
        private String avatarUrl;
        private String userName;
        private int userLevel;
        private boolean hasCredit;

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public int getZmCreditPoint() {
            return zmCreditPoint;
        }

        public void setZmCreditPoint(int zmCreditPoint) {
            this.zmCreditPoint = zmCreditPoint;
        }

        public int getFellowship() {
            return fellowship;
        }

        public void setFellowship(int fellowship) {
            this.fellowship = fellowship;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(int userLevel) {
            this.userLevel = userLevel;
        }

        public boolean isHasCredit() {
            return hasCredit;
        }

        public void setHasCredit(boolean hasCredit) {
            this.hasCredit = hasCredit;
        }
    }

}
