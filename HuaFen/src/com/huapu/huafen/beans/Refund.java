
package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by liang_xs by 2016/11/02
 */
public class Refund implements Serializable {

    private long rid;
    private String title;
    private long userId;
    private String summary;
    private long residualTime;
    private int refundStatus;
    public long getResidualTime() {
        return residualTime;
    }

    public void setResidualTime(long residualTime) {
        this.residualTime = residualTime;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(int refundStatus) {
        this.refundStatus = refundStatus;
    }
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
