package com.huapu.huafen.beans;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/15.
 */
public class PushMessage implements Serializable {
    private String action;
    private String target;

    private String hp_action;
    private String hp_target;

    private int type;

    public String getHp_action() {
        return hp_action;
    }

    public void setHp_action(String hp_action) {
        this.hp_action = hp_action;
    }

    public String getHp_target() {
        return hp_target;
    }

    public void setHp_target(String hp_target) {
        this.hp_target = hp_target;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAction() {
        return TextUtils.isEmpty(action) ? getHp_action() : action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTarget() {
        return TextUtils.isEmpty(target) ? getHp_target() : target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
