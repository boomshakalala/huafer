package com.huapu.huafen.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.beans.Preferences;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.OkHttpClientManager.StringCallback;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.views.SlideSwitch;
import com.huapu.huafen.views.SlideSwitch.OnCheckedChangeListener;
import com.squareup.okhttp.Request;

import java.util.HashMap;


/**
 * 设置
 *
 * @author liang_xs
 */
public class MessageFreeActivity extends BaseActivity implements OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    private String TAG = "MessageFreeActivity";
    private SlideSwitch checkBoxOrderDisturb, checkBoxCommentDisturb, sNotice, sCampaign, sxSwitch;
    private View layoutPersonalData, layoutAbout, layoutBlack;
    private TextView tvBtnLogout;
    private TextDialog dialog;
    private RadioGroup rgDisturb;
    private RadioButton rbDisturbAll, rbDisturbNight, rbDisturbNone;
    private RadioButton rgGoodsCommentAll, rgGoodsCommentFriendOnly, rgGoodsCommentNone;
    private int currRBDisturb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_free);
        initView();
    }

    private void initView() {
        setTitleString("消息提醒设置");

        checkBoxOrderDisturb = (SlideSwitch) findViewById(R.id.checkBoxOrderDisturb);
        checkBoxCommentDisturb = (SlideSwitch) findViewById(R.id.checkBoxCommentDisturb);
        sNotice = (SlideSwitch) findViewById(R.id.ss_notice);
        sCampaign = (SlideSwitch) findViewById(R.id.ss_campaign);
        sxSwitch = (SlideSwitch) findViewById(R.id.sx_notice);
        checkBoxOrderDisturb.setOnCheckedChangeListener(this);
        checkBoxCommentDisturb.setOnCheckedChangeListener(this);
        sNotice.setOnCheckedChangeListener(this);
        sCampaign.setOnCheckedChangeListener(this);
        rgDisturb = (RadioGroup) findViewById(R.id.rgDisturb);
        rbDisturbAll = (RadioButton) findViewById(R.id.rbDisturbAll);
        rbDisturbNight = (RadioButton) findViewById(R.id.rbDisturbNight);
        rbDisturbNone = (RadioButton) findViewById(R.id.rbDisturbNone);
        rgDisturb.setOnCheckedChangeListener(MessageFreeActivity.this);
        setUpConfigUI();
        startRequestForGetPreferences();
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(View v, boolean checked) {
        switch (v.getId()) {
            case R.id.checkBoxCommentDisturb:
                if (checked) {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_COMMENT, "1");
                } else {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_COMMENT, "0");
                }
                break;

            case R.id.checkBoxOrderDisturb:
                if (checked) {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_ORDER, "1");
                } else {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_ORDER, "0");
                }
                break;
            case R.id.ss_notice:
                LogUtil.e(TAG, MyConstants.REQUEST_KEY_NOTIFY_NOTICE + "   " + checked);
                if (checked) {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_NOTICE, "1");
                } else {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_NOTICE, "0");
                }
                break;
            case R.id.ss_campaign:
                if (checked) {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_CAMPAIGN, "1");
                } else {
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_CAMPAIGN, "0");
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rbDisturbAll:
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_OTHER_FEATURE_ALERTS,"1");
                    currRBDisturb = R.id.rbDisturbAll;
                    break;
                case R.id.rbDisturbNight:
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_OTHER_FEATURE_ALERTS,"2");
                    currRBDisturb = R.id.rbDisturbNight;
                    break;
                case R.id.rbDisturbNone:
                    startRequestForSetPreferences(MyConstants.REQUEST_KEY_NOTIFY_OTHER_FEATURE_ALERTS,"0");
                    currRBDisturb = R.id.rbDisturbNone;
                    break;
            }
    }

    /**
     * 设置偏好设置
     */
    private void startRequestForSetPreferences(final String key, final String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            toast("请求异常");
            return;
        }
        if (!CommonUtils.isNetAvaliable(this)) {
            toast("请检查网络连接");
            requestSetPreferenceError(key, value);
            return;
        }
        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();
        params.put(key, value);
        LogUtil.i("liang", "设置偏好设置params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.SETPREFERENCES, params, new StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "设置偏好设置：" + e.toString());
                toast("设置失败，请重试");
                requestSetPreferenceError(key, value);
                ProgressDialog.closeProgress();
            }


            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "设置偏好设置：" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    toast("设置失败，请重试");
                    requestSetPreferenceError(key, value);
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    LogUtil.i("liang",key);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (MyConstants.REQUEST_KEY_NOTIFY_OTHER_FEATURE_ALERTS.equals(key)) {
                            CommonPreference.setNotifyOtherFeatureAlerts(value);
                            if ("1".equals(value)) {
                                currRBDisturb = R.id.rbDisturbAll;
                            } else if ("2".equals(value)) {
                                currRBDisturb = R.id.rbDisturbNight;
                            } else if ("3".equals(value)) {
                                currRBDisturb = R.id.rbDisturbNone;
                            }
                        } else if (MyConstants.REQUEST_KEY_NOTIFY_COMMENT.equals(key)) {
                            CommonPreference.setNotifyComment(value);
                        } else if (MyConstants.REQUEST_KEY_NOTIFY_ORDER.equals(key)) {
                            CommonPreference.setNotifyOrder(value);
                        } else if (MyConstants.REQUEST_KEY_NOTIFY_NOTICE.equals(key)){
                            CommonPreference.setNotifyNotice(value);
                        } else if (MyConstants.REQUEST_KEY_NOTIFY_CAMPAIGN.equals(key)){
                            CommonPreference.setNotifyCampaign(value);
                        }
                    } else {
                        requestSetPreferenceError(key, value);
                        CommonUtils.error(baseResult, MessageFreeActivity.this, "");
                    }
                } catch (Exception e) {
                    toast("设置失败，请重试");
                    requestSetPreferenceError(key, value);
                    e.printStackTrace();
                }
            }
        });
    }

    private void requestSetPreferenceError(String key, String value) {
        if (MyConstants.REQUEST_KEY_NOTIFY_OTHER_FEATURE_ALERTS.equals(key)) {
            rgDisturb.setOnCheckedChangeListener(null);
            rgDisturb.check(currRBDisturb);
            rgDisturb.setOnCheckedChangeListener(MessageFreeActivity.this);
        } else if (MyConstants.REQUEST_KEY_NOTIFY_COMMENT.equals(key)) {
            if ("1".equals(value)) {
                checkBoxCommentDisturb.setChecked(false);
            } else if ("0".equals(value)) {
                checkBoxCommentDisturb.setChecked(true);
            }
        } else if (MyConstants.REQUEST_KEY_NOTIFY_ORDER.equals(key)) {
            if ("1".equals(value)) {
                checkBoxOrderDisturb.setChecked(false);
            } else if ("0".equals(value)) {
                checkBoxOrderDisturb.setChecked(true);
            }
        } else if (MyConstants.REQUEST_KEY_NOTIFY_NOTICE.equals(key)) {
            if ("1".equals(value)) {
                sNotice.setChecked(false);
            } else if ("0".equals(value)) {
                sNotice.setChecked(true);
            }
        } else if (MyConstants.REQUEST_KEY_NOTIFY_CAMPAIGN.equals(key)) {
            if ("1".equals(value)) {
                sCampaign.setChecked(false);
            } else if ("0".equals(value)) {
                sCampaign.setChecked(true);
            }
        }
    }

    private void startRequestForGetPreferences(){
        if (!CommonUtils.isNetAvaliable(this)){
            toast("请检查网络连接");
            return;
        }
        ProgressDialog.showProgress(this);
        OkHttpClientManager.postAsyn(MyConstants.GETPREFERENCES, null, new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                LogUtil.i("liang", "获取偏好设置：" + e.toString());
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.i("liang", "获取偏好设置：" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response.toString(),BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE){
                        Preferences preferences = JSON.parseObject(baseResult.obj,Preferences.class);
                        if (preferences != null){
                            updateConfig(preferences);
                        }
                    }else {
                        CommonUtils.error(baseResult, MessageFreeActivity.this, "");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateConfig(Preferences preferences){
        CommonPreference.setNotifyNotice(preferences.getNotificationOther());
        CommonPreference.setNotifyOtherFeatureAlerts(preferences.getNotificationOtherFeatureAlerts());
        CommonPreference.setNotifyCampaign(preferences.getNotificationCampaign());
        CommonPreference.setNotifyComment(preferences.getNotificationComment());
        CommonPreference.setNotifyOrder(preferences.getNotificationOrder());
    }

    private void setUpConfigUI(){
        String notifyComment = CommonPreference.getNotifyComment();
        String notifyOrder = CommonPreference.getNotifyOrder();
        String notifyCampaign = CommonPreference.getNotifyCampaign();
        String notifyNotice = CommonPreference.getNotifyNotice();
        String notifyOtherFeatureAlerts = CommonPreference.getNotifyOtherFeatureAlerts();
        LogUtil.d("chenguanxi","notifyComment="+notifyComment);
        LogUtil.d("chenguanxi","notifyOrder="+notifyOrder);
        LogUtil.d("chenguanxi","notifyCampaign="+notifyCampaign);
        LogUtil.d("chenguanxi","notifyNotice="+notifyNotice);
        LogUtil.d("chenguanxi","notifyOtherFeatureAlerts="+notifyOtherFeatureAlerts);
        if ("0".equals(notifyComment)) {
            checkBoxCommentDisturb.setChecked(false);
        } else if ("1".equals(notifyComment)) {
            checkBoxCommentDisturb.setChecked(true);
        }
        if ("0".equals(notifyOrder)) {
            checkBoxOrderDisturb.setChecked(false);
        } else if ("1".equals(notifyOrder)) {
            checkBoxOrderDisturb.setChecked(true);
        }
        if ("1".equals(notifyNotice)) {
            sNotice.setChecked(true);
        } else {
            sNotice.setChecked(false);
        }
        if ("1".equals(notifyCampaign)) {
            sCampaign.setChecked(true);
        } else {
            sCampaign.setChecked(false);
        }

        if ("1".equals(notifyOtherFeatureAlerts)){
            currRBDisturb = R.id.rbDisturbAll;
        }else if ("2".equals(notifyOtherFeatureAlerts)){
            currRBDisturb = R.id.rbDisturbNight;
        }else {
            currRBDisturb = R.id.rbDisturbNone;
        }
        rgDisturb.check(currRBDisturb);
    }

}
