package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 17/4/25.
 */

public class EasyArticleDetail implements Serializable {

    /**
     * code : 200
     * msg : 返回成功
     * obj : {"itemType":"moment","count":{"pv":"5","like":"1","comment":"2","collection":"2"},"comment":{"comments":[{"comment":{"targetId":426,"targetType":8,"content":"616465","createdAt":1494053967000,"likeCount":0,"replyCount":0,"liked":false,"commentId":463}},{"comment":{"targetId":426,"targetType":8,"content":"64646464","createdAt":1494053921000,"likeCount":0,"replyCount":0,"liked":false,"commentId":462}}],"count":2},"user":{"gender":1,"zmCreditPoint":800,"fellowship":1,"userId":1242517,"avatarUrl":"http://imgs.huafer.cc/huafer/1242517/headIcon/2017/4/25/1493116728575_icon.jpg","userName":"沧海123","userLevel":3,"hasCredit":true},"commentable":1,"moment":{"title":"94494","content":"499494","createdAt":1494052148000,"updatedAt":1494052148000,"liked":true,"collected":false,"location":"","uid":1242517,"media":[{"mimeType":"image/jpeg","url":"http://imgs.huafer.cc/huafer/1242517/arbitration/2017/5/6/1494052116716.jpg","width":3456,"height":4608,"tags":[{"x":662,"y":512,"orientation":270,"type":2,"title":"A BATHING APE","tagId":479},{"x":1353,"y":1795,"orientation":270,"type":2,"title":"ABC Design","tagId":480}],"mediaId":2272},{"mimeType":"image/jpeg","url":"http://imgs.huafer.cc/huafer/1242517/arbitration/2017/5/6/1494052118622.jpg","width":3456,"height":4608,"tags":[{"x":704,"y":457,"orientation":270,"type":2,"title":"ABC Design","tagId":481},{"x":876,"y":1772,"orientation":270,"type":2,"title":"A.by BOM","tagId":482}],"mediaId":2273}],"momentId":426}}
     * errorLevel : -1
     */

    private int code;
    private String msg;
    private ObjBean obj;
    private int errorLevel;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public int getErrorLevel() {
        return errorLevel;
    }

    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    public static class ObjBean implements Serializable{
        //        private CommentBeanX comment;
        public HPCommentsResult comment;
        /**
         * itemType : moment
         * count : {"pv":"5","like":"1","comment":"2","collection":"2"}
         * comment : {"comments":[{"comment":{"targetId":426,"targetType":8,"content":"616465","createdAt":1494053967000,"likeCount":0,"replyCount":0,"liked":false,"commentId":463}},{"comment":{"targetId":426,"targetType":8,"content":"64646464","createdAt":1494053921000,"likeCount":0,"replyCount":0,"liked":false,"commentId":462}}],"count":2}
         * user : {"gender":1,"zmCreditPoint":800,"fellowship":1,"userId":1242517,"avatarUrl":"http://imgs.huafer.cc/huafer/1242517/headIcon/2017/4/25/1493116728575_icon.jpg","userName":"沧海123","userLevel":3,"hasCredit":true}
         * commentable : 1
         * moment : {"title":"94494","content":"499494","createdAt":1494052148000,"updatedAt":1494052148000,"liked":true,"collected":false,"location":"","uid":1242517,"media":[{"mimeType":"image/jpeg","url":"http://imgs.huafer.cc/huafer/1242517/arbitration/2017/5/6/1494052116716.jpg","width":3456,"height":4608,"tags":[{"x":662,"y":512,"orientation":270,"type":2,"title":"A BATHING APE","tagId":479},{"x":1353,"y":1795,"orientation":270,"type":2,"title":"ABC Design","tagId":480}],"mediaId":2272},{"mimeType":"image/jpeg","url":"http://imgs.huafer.cc/huafer/1242517/arbitration/2017/5/6/1494052118622.jpg","width":3456,"height":4608,"tags":[{"x":704,"y":457,"orientation":270,"type":2,"title":"ABC Design","tagId":481},{"x":876,"y":1772,"orientation":270,"type":2,"title":"A.by BOM","tagId":482}],"mediaId":2273}],"momentId":426}
         */

        private String itemType;
        private CountBean count;
        private UserData user;
        private int commentable;
        private MomentBean moment;
        public List<RecArticle> recPoems;

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

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

        public int getCommentable() {
            return commentable;
        }

        public void setCommentable(int commentable) {
            this.commentable = commentable;
        }

        public MomentBean getMoment() {
            return moment;
        }

        public void setMoment(MomentBean moment) {
            this.moment = moment;
        }

        public static class CountBean implements Serializable{
            /**
             * pv : 5
             * like : 1
             * comment : 2
             * collection : 2
             */

            private String pv;
            private String like;
            private String comment;
            private String collection;
            public String reply;

            public String getPv() {
                return pv;
            }

            public void setPv(String pv) {
                this.pv = pv;
            }

            public String getLike() {
                return like;
            }

            public void setLike(String like) {
                this.like = like;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }

            public String getCollection() {
                return collection;
            }

            public void setCollection(String collection) {
                this.collection = collection;
            }
        }


        public static class MomentBean implements Serializable{
            /**
             * title : 94494
             * content : 499494
             * createdAt : 1494052148000
             * updatedAt : 1494052148000
             * liked : true
             * collected : false
             * location :
             * uid : 1242517
             * media : [{"mimeType":"image/jpeg","url":"http://imgs.huafer.cc/huafer/1242517/arbitration/2017/5/6/1494052116716.jpg","width":3456,"height":4608,"tags":[{"x":662,"y":512,"orientation":270,"type":2,"title":"A BATHING APE","tagId":479},{"x":1353,"y":1795,"orientation":270,"type":2,"title":"ABC Design","tagId":480}],"mediaId":2272},{"mimeType":"image/jpeg","url":"http://imgs.huafer.cc/huafer/1242517/arbitration/2017/5/6/1494052118622.jpg","width":3456,"height":4608,"tags":[{"x":704,"y":457,"orientation":270,"type":2,"title":"ABC Design","tagId":481},{"x":876,"y":1772,"orientation":270,"type":2,"title":"A.by BOM","tagId":482}],"mediaId":2273}]
             * momentId : 426
             */

            private String title;
            private String content;
            private long createdAt;
            private long updatedAt;
            private boolean liked;
            private boolean collected;
            private String location;
            private long uid;
            private long momentId;
            private List<FloridData.TitleMedia> media;
            private String categoryName;
            private String categoryId;

            public String getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(String categoryId) {
                this.categoryId = categoryId;
            }

            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getCreatedAt() {
                return createdAt;
            }

            public void setCreatedAt(long createdAt) {
                this.createdAt = createdAt;
            }

            public long getUpdatedAt() {
                return updatedAt;
            }

            public void setUpdatedAt(long updatedAt) {
                this.updatedAt = updatedAt;
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

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public long getUid() {
                return uid;
            }

            public void setUid(long uid) {
                this.uid = uid;
            }

            public long getMomentId() {
                return momentId;
            }

            public void setMomentId(long momentId) {
                this.momentId = momentId;
            }

            public List<FloridData.TitleMedia> getMedia() {
                return media;
            }

            public void setMedia(List<FloridData.TitleMedia> media) {
                this.media = media;
            }

        }
    }
}
