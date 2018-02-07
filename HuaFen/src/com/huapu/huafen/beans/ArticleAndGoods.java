package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/5/14.
 */

public class ArticleAndGoods implements Serializable {
    public List<String> goodsImgs;//
    public String name;//
    public String brand;//
    public int brandId;//
    public int goodsId;//
    public List<Type> types;//
    public String content;//
    public boolean isFreeDelivery;
    public Area area;//
    public int isNew;
    public int auditStatus;
    public String summary;
    /**
     * 1：在售
     * 2：预售中
     * 3：交易中
     * 4：已售出
     * 5：下架
     * 6:被举报
     * 7:已删除
     */
    public int goodsState;
    public int depositStatus;
    public int privileges;
    public int price;
    public int pastPrice;
    public int campaignId;
    public int likeCount;
    public int hasReceipt;
    public int supportFaceToFace;
    /**
     * 1 image 2 video
     */
    public int imageType;

    public String videoPath;

    public String videoCover;
    public int distance;

    public int discount;
    public int postage;
    public long presell;
    public long showTime;


    public boolean liked;
    public int postType;

    public int isAuction;
    public int bidDeposit;
    public int bidIncrement;
    public long bidStartTime;
    public long bidEndTime;
    public int hasBidDepositPayed;
    public int hammerPrice;
    public int bidder;
    public int sellPric;


    public String title;
    public String titleMediaType;
    public String titleMediaUrl;
    public int width;
    public int height;
    public long categoryId;

    public boolean collected;
    public long articleId;
    public String location;
    public long currentTimeMillis;
    public long firstCateId;

    // 活动
    public String image;
    public String action;
    public String target;

}
