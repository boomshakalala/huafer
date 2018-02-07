package com.huapu.huafen.beans;

import java.io.Serializable;

/**
 * Created by admin on 2017
 */
public class ArticleData implements Serializable {
    private String itemType;
    private items item;
    private ucounts counts;

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public items getItem() {
        return item;
    }

    public void setItem(items item) {
        this.item = item;
    }

    public ucounts getCounts() {
        return counts;
    }

    public void setCounts(ucounts counts) {
        this.counts = counts;
    }


    public static class items implements Serializable{
        private String title;
        private String summary;
        private String titleMediaType;
        private String titleMediaUrl;
        private int width;
        private int height;
        private String categoryId;
        private boolean liked;
        private boolean collected;
        private String articleId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getTitleMediaType() {
            return titleMediaType;
        }

        public void setTitleMediaType(String titleMediaType) {
            this.titleMediaType = titleMediaType;
        }

        public String getTitleMediaUrl() {
            return titleMediaUrl;
        }

        public void setTitleMediaUrl(String titleMediaUrl) {
            this.titleMediaUrl = titleMediaUrl;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public boolean isLiked() {
            return liked;
        }

        public void setLiked(boolean liked) {
            this.liked = liked;
        }

        public boolean isCollected() {
            return collected;
        }

        public void setCollected(boolean collected) {
            this.collected = collected;
        }

        public String getArticleId() {
            return articleId;
        }

        public void setArticleId(String articleId) {
            this.articleId = articleId;
        }

    }

    public static class ucounts implements Serializable{
        private String pv;
        private String collection;

        public String getCollection() {
            return collection;
        }

        public void setCollection(String collection) {
            this.collection = collection;
        }

        public String getPv() {
            return pv;
        }

        public void setPv(String pv) {
            this.pv = pv;
        }
    }


}
