package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/11/10.
 */
public class CommentData implements Serializable {

    private long orderId;

    private int cid;

    private String content;

    private int satisfaction;

    private boolean replied;

    private ArrayList<String> imgs;

    private long ratedAt;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(int satisfaction) {
        this.satisfaction = satisfaction;
    }

    public boolean getReplied() {
        return replied;
    }

    public void setReplied(boolean replied) {
        this.replied = replied;
    }

    public ArrayList<String> getImgs() {
        return imgs;
    }

    public void setImgs(ArrayList<String> imgs) {
        this.imgs = imgs;
    }

    public long getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(long ratedAt) {
        this.ratedAt = ratedAt;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }
}
