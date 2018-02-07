package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by admin on 2016/11/7.
 */
public class FlowerData implements Serializable{
    private ArrayList<ArticleData> list;
    private int page;
    private UserData user;
    private CountBean count;


    public CountBean getCount() {
        return count;
    }

    public void setCount(CountBean count) {
        this.count = count;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public ArrayList<ArticleData> getList() {
        return list;
    }

    public void setList(ArrayList<ArticleData> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public static class CountBean implements Serializable {
        private String pv;
        private String sold;
        private String focus;
        private String selling;
        private String fans;
        private String collection;//收藏
        private String like;//点赞

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public String getLike() {
            return like;
        }

        public void setLike(String like) {
            this.like = like;
        }

        public String getSold() {
            return sold;
        }

        public void setSold(String sold) {
            this.sold = sold;
        }

        public String getFocus() {
            return focus;
        }

        public void setFocus(String focus) {
            this.focus = focus;
        }

        public String getSelling() {
            return selling;
        }

        public void setSelling(String selling) {
            this.selling = selling;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public String getPv() {
            return pv;
        }

        public void setPv(String pv) {
            this.pv = pv;
        }
    }
}
