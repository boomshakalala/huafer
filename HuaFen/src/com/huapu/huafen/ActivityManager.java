package com.huapu.huafen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;

import com.huapu.huafen.utils.LogUtil;

/**
 * @ClassName: ActivityManager
 * @Description: activity管理器
 * @author liang_xs
 * @date 2016-03-27
 */
public class ActivityManager {
	private static List<Activity> activityList = new ArrayList<Activity>();

	public static void add(Activity activity) {
		activityList.add(activity);
	}

	public static void remove(Activity activity) {
		activityList.remove(activity);
	}

	public static void finishAllActivities() {
		for (Activity activity : activityList) {
			activity.finish();
		}
	}

	public static Activity getCurrActivity() {
		if (activityList.size() == 0) {
			return null;
		}
		return activityList.get(activityList.size() - 1);
	}

	private static Map<String, Activity> destroyMap = new HashMap<String, Activity>();

	/**
	 * 添加到销毁队列
	 * 
	 * @param activity
	 *            要销毁的activity
	 */

	public static void addDestoryActivity(Activity activity, String activityName) {
		destroyMap.put(activityName, activity);
	}

	/**
	 * 销毁指定Activity
	 */
	public static void destoryActivity(String activityName) {
		destroyMap.get(activityName).finish();
	}
	
	/**
	 * 销毁指定Activity
	 */
	public static void destoryAllActivities() {
		Set<String> keySet = destroyMap.keySet();
		for (String key : keySet) {
			destroyMap.get(key).finish();
		}
	}
}
