package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/12/3.
 */
public class ExpressContactListResult implements Serializable {

    private int page;

    private List<Express> expressList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Express> getExpressList() {
        return expressList;
    }

    public void setExpressList(List<Express> expressList) {
        this.expressList = expressList;
    }
}
