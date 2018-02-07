package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2017/5/2.
 */

public class ArticleDetailResult extends BaseResultNew{


    public ArticleDetailData obj ;


    public static class ArticleDetailData extends BaseData {
        public UserData user;
        public FloridData article;
        public Count count;
        public HPCommentsResult comment;
        public int commentable;
        public List<RecArticle> recPoems;

    }

    public static class Count implements Serializable {
        public String pv;
        public String like;
        public String comment;
        public String collection;
        public String reply;
    }

}
