
package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by liang_xs by 2016/11/02
 */
public class OrdersResult implements Serializable {

    private int page;
    private List<Orders> orders;
    private OrderStatusCounts orderStatusCounts;
    private int hasBatchShipment;

    public int getHasBatchShipment() {
        return hasBatchShipment;
    }

    public void setHasBatchShipment(int hasBatchShipment) {
        this.hasBatchShipment = hasBatchShipment;
    }

    public OrderStatusCounts getOrderStatusCounts() {
        return orderStatusCounts;
    }

    public void setOrderStatusCounts(OrderStatusCounts orderStatusCounts) {
        this.orderStatusCounts = orderStatusCounts;
    }

    public static class OrderStatusCounts implements Serializable{
        public int payPending;
        public int shipPending;
        public int receiptPending;
        public int ratePending;
        public int completed;
        public int refund;
        public int report;
    }
    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
