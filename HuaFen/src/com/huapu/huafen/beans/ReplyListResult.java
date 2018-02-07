package com.huapu.huafen.beans;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/14.
 */

public class ReplyListResult extends BaseResultNew {

    public ReplyListData obj;

    public static class ReplyListData extends BaseData {
        public Item item;
        public ArrayList<HPReplyData> replies;
        public HPComment comment;
        public UserData user;
        public int page;
        public int commentable;
    }

}
