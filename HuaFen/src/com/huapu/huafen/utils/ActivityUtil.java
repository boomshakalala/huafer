package com.huapu.huafen.utils;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityUtil {
    private static ArrayList<Activity> activities = new ArrayList<Activity>();


    /**
     * 添加Acitivty列表
     */
    public static void addActivities(Activity activity) {
        ActivityUtil.activities.add(activity);
    }

    /**
     * 关闭队列中的activity
     *
     * @return
     */
    public static Boolean finishActivity() {
        for (int i = 0; i < activities.size(); i++) {
            if (!activities.get(i).isFinishing()) {

                activities.get(i).finish();
            }
        }
        return true;
    }


}
