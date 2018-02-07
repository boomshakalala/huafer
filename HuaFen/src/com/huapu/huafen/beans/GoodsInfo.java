package com.huapu.huafen.beans;

import java.util.ArrayList;

public class GoodsInfo extends BaseResult{


	public int goodsBrandId;
	public String goodsImg;
	public int crowd;//适合人群 1 成人 2 女性 3 男性 4 宝宝 5 女宝宝 6 男宝宝
	public int isAuction;
	public int hammerPrice;
	public int sellPric;
	public int bidDeposit;
	public int bidIncrement;
	public long bidStartTime;
	public long bidEndTime;
	public long bidder;
	public int hasBidDepositPayed;
    public String brandName;
    public String name;
	public long orderId;
	private ArrayList<GoodsTypes> goodsTypes = new ArrayList<GoodsTypes>();
    /**
	 * 商品所在地区
	 */
	private Area area;
	/**
	 * 商品是否喜欢
	 */
	private boolean isLike;
	/**
	 * 商品可操作类型
	 * 	1. 微信支付
	 * 	2. 支付宝支付
	 * 	3. 退换货
	 * 	4. 包邮
	 */
	private ArrayList<Integer> goodsOperations = new ArrayList<Integer>();
	private long goodsId;
	/**
	 * 商品详情图片列表
	 */
	private ArrayList<String> goodsImgs = new ArrayList<String>();
	/**
	 * 商品价格
	 */
	private int price;
	/**
	 * 商品原价
	 */
	private int pastPrice;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品品牌名称
	 */
	private String goodsBrand;
	/**
	 * 商品语音描述地址
	 */
	private String sound;
	/**
	 * 商品语音描述时长，单位秒s
	 */
	private int soundTime;
	/**
	 * 商品预售时间戳
	 */
	private long presell = 0;
	/**
	 * 商品预览数量
	 */
	private int goodsPV;
	/**
	 * 商品被喜欢数量
	 */
	private int wantCount;
	/**
	 * 1：在售
		2：预售中
		3：交易中
		4：已售出
		5：下架
		6:被举报
		7:已删除
	 */
	private int goodsState;
	private int shippingCost;
	/**
	 * 商品描述
	 */
	private String goodsContent;
	//是否包邮
	private boolean isFreeDelivery;
	//等于4 到货付款
	private int postType;
	private int cfId;
	private int scfId;
	private String cfName;
	private String scfName;
	private Age age;
	/**
	 * 1.全新
	 * 0.闲置
	 */
	private int isNew;
	/**
	 * 宝宝年龄段数组(将来需要发布宝贝信息时进行选)
	 */
	private ArrayList<Age> ageList = new ArrayList<Age>();
	private int campaignId;
	private int privileges;
	private int auditStatus;
	private int likeCount;
	private String videoCover;
	private String videoPath;
	private int discount;
	private double lng;
	private double lat;
	private int postage;
	private int hasReceipt;
	private int supportFaceToFace;
	private long draftId;
    public long currentTimeMillis;

    /**
	 * 商品邮费
	 */
	public int getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(int shippingCost) {
		this.shippingCost = shippingCost;
	}

	public int getPostType() {
		return postType;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

	public long getDraftId() {
		return draftId;
	}

	public void setDraftId(long draftId) {
		this.draftId = draftId;
	}

	public int getHasReceipt() {
		return hasReceipt;
	}

	public void setHasReceipt(int hasReceipt) {
		this.hasReceipt = hasReceipt;
	}

	public int getSupportFaceToFace() {
		return supportFaceToFace;
	}

	public void setSupportFaceToFace(int supportFaceToFace) {
		this.supportFaceToFace = supportFaceToFace;
	}

	public int getPostage() {
		return postage;
	}
	public void setPostage(int postage) {
		this.postage = postage;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}

	public int getPrivileges() {
		return privileges;
	}

	public void setPrivileges(int privileges) {
		this.privileges = privileges;
	}

	public int getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}

	public ArrayList<Age> getAgeList() {
		return ageList;
	}
	public void setAgeList(ArrayList<Age> ageList) {
		this.ageList = ageList;
	}
	public int getIsNew() {
		return isNew;
	}
	public void setIsNew(int isNew) {
		this.isNew = isNew;
	}
	public int getCfId() {
		return cfId;
	}
	public void setCfId(int cfId) {
		this.cfId = cfId;
	}
	public int getScfId() {
		return scfId;
	}
	public void setScfId(int scfId) {
		this.scfId = scfId;
	}
	public String getCfName() {
		return cfName;
	}
	public void setCfName(String cfName) {
		this.cfName = cfName;
	}
	public String getScfName() {
		return scfName;
	}
	public void setScfName(String scfName) {
		this.scfName = scfName;
	}
	public Age getAge() {
		return age;
	}
	public void setAge(Age age) {
		this.age = age;
	}
	public void setLike(boolean isLike) {
		this.isLike = isLike;
	}
	public void setFreeDelivery(boolean isFreeDelivery) {
		this.isFreeDelivery = isFreeDelivery;
	}
	public boolean getIsFreeDelivery() {
		return isFreeDelivery;
	}
	public void setIsFreeDelivery(boolean isFreeDelivery) {
		this.isFreeDelivery = isFreeDelivery;
	}
	public String getGoodsContent() {
		return goodsContent;
	}
	public void setGoodsContent(String goodsContent) {
		this.goodsContent = goodsContent;
	}
	public Area getArea() {
		return area;
	}
	public void setArea(Area area) {
		this.area = area;
	}
	public ArrayList<GoodsTypes> getGoodsTypes() {
		return goodsTypes;
	}
	public void setGoodsTypes(ArrayList<GoodsTypes> goodsTypes) {
		this.goodsTypes = goodsTypes;
	}
	
	public boolean getIsLike() {
		return isLike;
	}
	public void setIsLike(boolean isLike) {
		this.isLike = isLike;
	}
	
	public String getSound() {
		return sound;
	}
	public void setSound(String sound) {
		this.sound = sound;
	}
	public int getSoundTime() {
		return soundTime;
	}
	public void setSoundTime(int soundTime) {
		this.soundTime = soundTime;
	}
	public long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(long goodsId) {
		this.goodsId = goodsId;
	}
	
	public ArrayList<Integer> getGoodsOperations() {
		return goodsOperations;
	}
	public void setGoodsOperations(ArrayList<Integer> goodsOperations) {
		this.goodsOperations = goodsOperations;
	}
	
	public ArrayList<String> getGoodsImgs() {
		return goodsImgs;
	}
	public void setGoodsImgs(ArrayList<String> goodsImgs) {
		this.goodsImgs = goodsImgs;
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
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public String getGoodsBrand() {
		return goodsBrand;
	}
	public void setGoodsBrand(String goodsBrand) {
		this.goodsBrand = goodsBrand;
	}
	public long getPresell() {
		return presell;
	}
	public void setPresell(long presell) {
		this.presell = presell;
	}
	public int getGoodsPV() {
		return goodsPV;
	}
	public void setGoodsPV(int goodsPV) {
		this.goodsPV = goodsPV;
	}
	public int getWantCount() {
		return wantCount;
	}
	public void setWantCount(int wantCount) {
		this.wantCount = wantCount;
	}
	public int getGoodsState() {
		return goodsState;
	}
	public void setGoodsState(int goodsState) {
		this.goodsState = goodsState;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getVideoCover() {
		return videoCover;
	}

	public void setVideoCover(String videoCover) {
		this.videoCover = videoCover;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}
}
