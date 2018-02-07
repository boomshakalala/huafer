package com.huapu.huafen.beans;

import java.util.ArrayList;

public class UserInfo extends BaseResult {
    public String avatarUrl;
    public int fellowship;//1 无关系 2 已关注 3 被关注 4 互相关注
    /**
     * 退款数量
     */
    public int refundCount;
    /**
     * 买到商品数量
     */
    public int buyedCount;
    /**
     * 生育状态，1备孕 2怀孕 3有孩子
     */
    private int pregnantStat;
    /**
     * 是否关注
     */
    private Boolean isFocus;
    /**
     * 是否屏蔽
     */
    private boolean isShield;
    private Area area = new Area();
    private ArrayList<Baby> babys = new ArrayList<Baby>();
    private String token;
    private long userId;
    private String wechatOpenId;
    private String aliPayId;
    private String userIcon;
    private String userName;
    // 用户状态 2:店铺被封3:用户被封
    private int status;
    /**
     * 用户等级：1.平民 2.VIP 3.明星
     */
    private int userLevel;
    private int userSex;
    private String phone;
    /**
     * 公告
     */
    private String notice;
    /**
     * 信用金
     */
    private boolean hasCredit;
    /**
     * 粉丝数量
     */
    private int fansCount;
    /**
     * 关注数量
     */
    private int focusCount;
    /**
     * 发布数量
     */
    private int releaseCount;
    /**
     * 屏蔽数量
     */
    private int shieldCount;
    /**
     * 在售商品数量
     */
    private int sellGoodsCount;
    /**
     * 卖出数量
     */
    private int selledCount;
    /**
     * 购买数量
     */
    private int buyGoodsCount;
    /**
     * 喜欢数量
     */
    private int wantCount;
    /**
     * 店铺宝贝数量
     */
    private int sellingCount;
    /**
     * 花粉认证文案
     */
    private String starUserTitle;
    /**
     * 是否绑定支付宝
     */
    private boolean isBindALi;
    /**
     * 是否绑定微信
     */
    private boolean isBindWechat;
    /**
     * 是否绑定微博
     */
    private boolean isBindWeibo;
    /**
     *
     */
    private String weiboUserId;
    /**
     * 芝麻信用额度
     **/
    private int zmCreditPoint;
    /**
     * 花语
     */
    private Notices notices;
    /**
     * 0 - false, 1 - true | 留言通知 |
     */
    private String notifyComment;
    /**
     * 0 - false, 1 - true | 订单消息 |
     */
    private String notifyOrder;
    /**
     * all - 全部，friend_only - 仅相互关注的，none - 关闭留言 | 商品留言配置 |
     */
    private String goodsComment;
    private long registerTime;

    private int authorityTag = 0;

    private String profileBackground;

    public boolean hasVerified;

    public String getProfileBackground() {
        return profileBackground;
    }

    public void setProfileBackground(String profileBackground) {
        this.profileBackground = profileBackground;
    }

    public int getAuthorityTag() {
        return authorityTag;
    }

    public void setAuthorityTag(int authorityTag) {
        this.authorityTag = authorityTag;
    }

    public String getWeiboUserId() {
        return weiboUserId;
    }

    public void setWeiboUserId(String weiboUserId) {
        this.weiboUserId = weiboUserId;
    }

    public boolean isBindWeibo() {
        return isBindWeibo;
    }

    public void setBindWeibo(boolean bindWeibo) {
        isBindWeibo = bindWeibo;
    }

    public long getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(long registerTime) {
        this.registerTime = registerTime;
    }

    public String getGoodsComment() {
        return goodsComment;
    }

    public void setGoodsComment(String goodsComment) {
        this.goodsComment = goodsComment;
    }

    public String getNotifyOrder() {
        return notifyOrder;
    }

    public void setNotifyOrder(String notifyOrder) {
        this.notifyOrder = notifyOrder;
    }

    public String getNotifyComment() {
        return notifyComment;
    }

    public void setNotifyComment(String notifyComment) {
        this.notifyComment = notifyComment;
    }

    public Notices getNotices() {
        return notices;
    }

    public void setNotices(Notices notices) {
        this.notices = notices;
    }

    public int getZmCreditPoint() {
        return this.zmCreditPoint;
    }

    public void setZmCreditPoint(int zmCreditPoint) {
        this.zmCreditPoint = zmCreditPoint;
    }

    public boolean getIsBindALi() {
        return isBindALi;
    }

    public void setIsBindALi(boolean isBindALi) {
        this.isBindALi = isBindALi;
    }

    public boolean getIsBindWechat() {
        return isBindWechat;
    }

    public void setIsBindWechat(boolean isBindWechat) {
        this.isBindWechat = isBindWechat;
    }

    public String getStarUserTitle() {
        return starUserTitle;
    }

    public void setStarUserTitle(String starUserTitle) {
        this.starUserTitle = starUserTitle;
    }

    public int getSellingCount() {
        return sellingCount;
    }

    public void setSellingCount(int sellingCount) {
        this.sellingCount = sellingCount;
    }

    public boolean getHasCredit() {
        return hasCredit;
    }

    public void setHasCredit(boolean hasCredit) {
        this.hasCredit = hasCredit;
    }

    public boolean getIsShield() {
        return isShield;
    }

    public void setIsShield(boolean isShield) {
        this.isShield = isShield;
    }

    public int getWantCount() {
        return wantCount;
    }

    public void setWantCount(int wantCount) {
        this.wantCount = wantCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFocusCount() {
        return focusCount;
    }

    public void setFocusCount(int focusCount) {
        this.focusCount = focusCount;
    }

    public int getReleaseCount() {
        return releaseCount;
    }

    public void setReleaseCount(int releaseCount) {
        this.releaseCount = releaseCount;
    }

    public int getShieldCount() {
        return shieldCount;
    }

    public void setShieldCount(int shieldCount) {
        this.shieldCount = shieldCount;
    }

    public int getSellGoodsCount() {
        return sellGoodsCount;
    }

    public void setSellGoodsCount(int sellGoodsCount) {
        this.sellGoodsCount = sellGoodsCount;
    }

    public int getSelledCount() {
        return selledCount;
    }

    public void setSelledCount(int selledCount) {
        this.selledCount = selledCount;
    }

    public int getBuyGoodsCount() {
        return buyGoodsCount;
    }

    public void setBuyGoodsCount(int buyGoodsCount) {
        this.buyGoodsCount = buyGoodsCount;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Baby> getBabys() {
        return babys;
    }

    public void setBabys(ArrayList<Baby> babys) {
        this.babys = babys;
    }

    public int getPregnantStat() {
        return pregnantStat;
    }

    public void setPregnantStat(int pregnantStat) {
        this.pregnantStat = pregnantStat;
    }

    public Boolean getIsFocus() {
        return isFocus;
    }

    public void setIsFocus(Boolean isFocus) {
        this.isFocus = isFocus;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getWechatOpenId() {
        return wechatOpenId;
    }

    public void setWechatOpenId(String wechatOpenId) {
        this.wechatOpenId = wechatOpenId;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
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

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getAliPayId() {
        return aliPayId;
    }

    public void setAliPayId(String aliPayId) {
        this.aliPayId = aliPayId;
    }

    public boolean isIdentityAllowed() {
        return getZmCreditPoint() > 0 || hasVerified;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "pregnantStat=" + pregnantStat +
                ", isFocus=" + isFocus +
                ", isShield=" + isShield +
                ", area=" + area +
                ", babys=" + babys +
                ", token='" + token + '\'' +
                ", userId=" + userId +
                ", wechatOpenId='" + wechatOpenId + '\'' +
                ", aliPayId='" + aliPayId + '\'' +
                ", userIcon='" + userIcon + '\'' +
                ", userName='" + userName + '\'' +
                ", userLevel=" + userLevel +
                ", userSex=" + userSex +
                ", phone='" + phone + '\'' +
                ", notice='" + notice + '\'' +
                ", hasCredit=" + hasCredit +
                ", fansCount=" + fansCount +
                ", focusCount=" + focusCount +
                ", releaseCount=" + releaseCount +
                ", shieldCount=" + shieldCount +
                ", sellGoodsCount=" + sellGoodsCount +
                ", selledCount=" + selledCount +
                ", buyGoodsCount=" + buyGoodsCount +
                ", wantCount=" + wantCount +
                ", sellingCount=" + sellingCount +
                ", starUserTitle='" + starUserTitle + '\'' +
                ", isBindALi=" + isBindALi +
                ", isBindWechat=" + isBindWechat +
                ", zmCreditPoint=" + zmCreditPoint +
                ", notices=" + notices +
                ", notifyComment='" + notifyComment + '\'' +
                ", notifyOrder='" + notifyOrder + '\'' +
                ", goodsComment='" + goodsComment + '\'' +
                ", fellowship=" + fellowship +
                ", registerTime=" + registerTime +
                ", status=" + status +
                '}';
    }
}
