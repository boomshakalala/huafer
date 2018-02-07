package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/2/24.
 */

public class OrderJointResult implements Serializable{

    private int page;
    private List<OrderJointData> batches;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<OrderJointData> getBatches() {
        return batches;
    }

    public void setBatches(List<OrderJointData> batches) {
        this.batches = batches;
    }

    public static class OrderJointData implements Serializable{
        private UserData userData;
        private Consignee consignee;
        private List<Orders> orders;

        public UserData getUserData() {
            return userData;
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
        }

        public Consignee getConsignee() {
            return consignee;
        }

        public void setConsignee(Consignee consignee) {
            this.consignee = consignee;
        }

        public List<Orders> getOrders() {
            return orders;
        }

        public void setOrders(List<Orders> orders) {
            this.orders = orders;
        }
    }


}
