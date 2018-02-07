package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2016/11/10.
 */
public class Comments implements Serializable{

    private OrderData orderData;
    private GoodsData goodsData;
    private Comment comment;
    private Comment reply;

    public OrderData getOrderData() {
        return orderData;
    }

    public void setOrderData(OrderData orderData) {
        this.orderData = orderData;
    }

    public GoodsData getGoodsData() {
        return goodsData;
    }

    public void setGoodsData(GoodsData goodsData) {
        this.goodsData = goodsData;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Comment getReply() {
        return reply;
    }

    public void setReply(Comment reply) {
        this.reply = reply;
    }

    public static class Comment implements Serializable{

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
