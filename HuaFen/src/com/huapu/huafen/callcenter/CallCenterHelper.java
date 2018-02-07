package com.huapu.huafen.callcenter;

import android.content.Context;

import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.LogUtil;
import com.qiyukf.unicorn.api.ConsultSource;
import com.qiyukf.unicorn.api.SavePowerConfig;
import com.qiyukf.unicorn.api.UICustomization;
import com.qiyukf.unicorn.api.Unicorn;
import com.qiyukf.unicorn.api.YSFOptions;
import com.qiyukf.unicorn.api.YSFUserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by danielluan on 2017/9/19.
 */

public class CallCenterHelper {


    // 本地图片协议
    private static final String RES = "res://";

    public static void setUserInfo(Context context) {

        UserInfo huaUserInfo = CommonPreference.getUserInfo();

        if (huaUserInfo == null) {
            return;
        }

        JSONArray js = new JSONArray();
        try {
            JSONObject jo = new JSONObject();
            jo.put("key", "real_name");
            jo.put("value", huaUserInfo.getUserName());
            js.put(jo);
            JSONObject jo2 = new JSONObject();
            jo2.put("key", "avatar");
            jo2.put("value", huaUserInfo.getUserIcon());
            js.put(jo2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        YSFUserInfo userInfo = new YSFUserInfo();
        userInfo.userId = "" + huaUserInfo.getUserId();

        String data = js.toString();
        data = data.replace("\\", "");
        LogUtil.d("danielluan", "set user info" + data);
        userInfo.data = data;
        Unicorn.setUserInfo(userInfo);
        YSFOptions newOptions = options();
        newOptions.uiCustomization.leftAvatar = MyConstants.Drawable + R.drawable.ic_head;
        newOptions.uiCustomization.rightAvatar = huaUserInfo.getUserIcon();
        Unicorn.updateOptions(newOptions);


    }

    public static void init(Context context) {
        Unicorn.init(context, "2b999b8106596902631fa9f3d536ca1f", options(), new UILImageLoader());

    }

    // 如果返回值为null，则全部使用默认参数。
    private static YSFOptions options() {
        YSFOptions options = new YSFOptions();
        //options.statusBarNotificationConfig = new StatusBarNotificationConfig();
        //options.statusBarNotificationConfig.
        options.savePowerConfig = new SavePowerConfig();
        options.uiCustomization = new UICustomization();
        return options;
    }


    public static void start(Context context, String sourceUrl, String sourceTitle, String custom) {

        String title = "客服";
        /**
         * 设置访客来源，标识访客是从哪个页面发起咨询的，用于客服了解用户是从什么页面进入。
         * 三个参数分别为：来源页面的url，来源页面标题，来源页面额外信息（可自由定义）。
         * 设置来源后，在客服会话界面的"用户资料"栏的页面项，可以看到这里设置的值。
         */
        ConsultSource source = new ConsultSource(sourceUrl, sourceTitle, "custom information string");
        /**
         * 请注意： 调用该接口前，应先检查Unicorn.isServiceAvailable()，
         * 如果返回为false，该接口不会有任何动作
         *
         * @param context 上下文
         * @param title   聊天窗口的标题
         * @param source  咨询的发起来源，包括发起咨询的url，title，描述信息等
         */

        setUserInfo(context);
        if (Unicorn.isServiceAvailable()) {
            Unicorn.openServiceActivity(context, title, source);
        }


    }


}
