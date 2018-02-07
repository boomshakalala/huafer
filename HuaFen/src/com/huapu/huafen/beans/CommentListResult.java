package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by admin on 2017/5/12.
 */

public class CommentListResult extends BaseResultNew {


    public CommentListData obj;


    public static class CommentListData extends BaseData {
        public Item item;
        public List<HPCommentData> comments;
        public int commentable;
        public int page;
    }
}
