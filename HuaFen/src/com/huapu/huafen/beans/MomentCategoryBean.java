package com.huapu.huafen.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by qwe on 2017/4/26.
 */

public class MomentCategoryBean implements Serializable {

    /**
     * code : 200
     * msg : 返回成功
     * obj : {"cats":[{"name":"好物","sequence":1,"catId":1},{"name":"穿搭","sequence":2,"catId":2},{"name":"烹饪","sequence":3,"catId":3},{"name":"旅行","sequence":4,"catId":4},{"name":"护理","sequence":5,"catId":5},{"name":"其他","sequence":6,"catId":6}],"version":"1.0"}
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

    public static class ObjBean implements Serializable {
        /**
         * cats : [{"name":"好物","sequence":1,"catId":1},{"name":"穿搭","sequence":2,"catId":2},{"name":"烹饪","sequence":3,"catId":3},{"name":"旅行","sequence":4,"catId":4},{"name":"护理","sequence":5,"catId":5},{"name":"其他","sequence":6,"catId":6}]
         * version : 1.0
         */

        private String version;
        private List<CatsBean> cats;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<CatsBean> getCats() {
            return cats;
        }

        public void setCats(List<CatsBean> cats) {
            this.cats = cats;
        }

        public static class CatsBean implements Serializable {
            /**
             * name : 好物
             * sequence : 1
             * catId : 1
             */

            private String name;
            private int sequence;
            private int catId;
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getSequence() {
                return sequence;
            }

            public void setSequence(int sequence) {
                this.sequence = sequence;
            }

            public int getCatId() {
                return catId;
            }

            public void setCatId(int catId) {
                this.catId = catId;
            }
        }
    }
}
