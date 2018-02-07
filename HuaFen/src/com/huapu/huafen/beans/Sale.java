package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/11.
 */
public class Sale implements Serializable {
    private String idAuthRequiredFor;

    public String getIdAuthRequiredFor() {
        return idAuthRequiredFor;
    }

    public void setIdAuthRequiredFor(String idAuthRequiredFor) {
        this.idAuthRequiredFor = idAuthRequiredFor;
    }
}
