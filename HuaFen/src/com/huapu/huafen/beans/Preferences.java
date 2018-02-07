package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by mac on 2017/12/20.
 */

public class Preferences implements Serializable{
    private String notificationComment;//留言消息
    private String notificationOrder;//订单消息
    private String notificationOther;//通知消息
    private String notificationCampaign;//活动消息
    private String notificationOtherFeatureAlerts;//其他消息
    private String comment;//允许留言配置

    public String getNotificationComment() {
        return notificationComment;
    }

    public void setNotificationComment(String notificationComment) {
        this.notificationComment = notificationComment;
    }

    public String getNotificationOrder() {
        return notificationOrder;
    }

    public void setNotificationOrder(String notificationOrder) {
        this.notificationOrder = notificationOrder;
    }

    public String getNotificationOther() {
        return notificationOther;
    }

    public void setNotificationOther(String notificationOther) {
        this.notificationOther = notificationOther;
    }

    public String getNotificationCampaign() {
        return notificationCampaign;
    }

    public void setNotificationCampaign(String notificationCampaign) {
        this.notificationCampaign = notificationCampaign;
    }

    public String getNotificationOtherFeatureAlerts() {
        return notificationOtherFeatureAlerts;
    }

    public void setNotificationOtherFeatureAlerts(String notificationOtherFeatureAlerts) {
        this.notificationOtherFeatureAlerts = notificationOtherFeatureAlerts;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
