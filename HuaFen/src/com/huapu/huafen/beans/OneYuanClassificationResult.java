package com.huapu.huafen.beans;

import java.util.List;

/**
 * Created by admin on 2017/5/18.
 */

public class OneYuanClassificationResult extends BaseResultNew {

    public OneYuanClassificationData obj;

    public static class OneYuanClassificationData extends BaseData {
        public List<SecondaryClassification> classifications;
        public String cacheVersion;
    }
}
