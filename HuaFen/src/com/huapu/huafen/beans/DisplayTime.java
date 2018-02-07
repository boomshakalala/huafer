package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017/2/6.
 */

public class DisplayTime implements Serializable {

    private int renewable;
    private int total = -1;
    private int remain = -1;
    private BtnHelp helpBtn;

    public int getRenewable() {
        return renewable;
    }

    public void setRenewable(int renewable) {
        this.renewable = renewable;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public BtnHelp getHelpBtn() {
        return helpBtn;
    }

    public void setHelpBtn(BtnHelp helpBtn) {
        this.helpBtn = helpBtn;
    }

    public static class BtnHelp implements Serializable{
        private String action;
        private String target;

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

}
