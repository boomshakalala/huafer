package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/11/11.
 */
public class CommentDetailData implements Serializable {

    private OrderData orderData;

    private GoodsData goodsData;

    private ArrayList<Comments> comments;

    public GoodsData getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
    }

    public ArrayList<Comments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comments> comments) {
        this.comments = comments;
    }

    public OrderData getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderData orderData) {
        this.orderData = orderData;
    }


    public static class Comments implements Serializable{
        private UserData userData;

        private CommentData commentData;

        public UserData getUserData() {
            return userData;
        }

        public void setUserData(UserData userData) {
            this.userData = userData;
        }

        public CommentData getCommentData() {
            return commentData;
        }

        public void setCommentData(CommentData commentData) {
            this.commentData = commentData;
        }
    }

}
