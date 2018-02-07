package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/10/11.
 */
public class AuditResult implements Serializable {
    private String reason;
    private String action;
    private String target;
    /**
     * 1：通过上首页、2：通过不上首页、3：轻微拒绝、4：严重拒绝、5：未审核
     */
    private int auditStatus;

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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
}
