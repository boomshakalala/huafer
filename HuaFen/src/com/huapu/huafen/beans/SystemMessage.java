package com.huapu.huafen.beans;

/**
 * Created by liang_xs on 2016/10/13.
 */
public class SystemMessage extends BaseResult{
    private String title;
    private String msg;
    private String image;
    private String action;
    private String target;
    private long sentAt;

    public SystemMessage() {
        super();
    }

    public SystemMessage(String title, String msg, String image, String action, String target, long sentAt) {
        this.title = title;
        this.msg = msg;
        this.image = image;
        this.action = action;
        this.target = target;
        this.sentAt = sentAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public long getSentAt() {
        return sentAt;
    }

    public void setSentAt(long sentAt) {
        this.sentAt = sentAt;
    }
}
