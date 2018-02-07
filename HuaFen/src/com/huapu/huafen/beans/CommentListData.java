package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by lalo on 2016/10/20.
 */
public class CommentListData implements Serializable {

    public CommentListData(){
        comments = new ArrayList<Comments>();
    }

    private TreeMap<String,Integer> count;

    private List<Comments> comments;

    private int page;

    public TreeMap<String, Integer> getCount() {
        return count;
    }

    public void setCount(TreeMap<String, Integer> count) {
        this.count = count;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
