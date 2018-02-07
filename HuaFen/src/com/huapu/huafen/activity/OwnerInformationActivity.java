package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.R2;
import com.huapu.huafen.beans.Area;
import com.huapu.huafen.beans.Baby;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CreditInfo;
import com.huapu.huafen.beans.UserInfo;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ArrayUtil;
import com.huapu.huafen.utils.BindSinaUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.DateTimeUtils;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.Logger;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.BabyInfoLayout;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 店主资料
 * Created by admin on 2017/3/15.
 */
public class OwnerInformationActivity extends BaseActivity implements BindSinaUtil.RefreshSinaUI {
    private static final String PREF_FILE_NAME = "common_settings";
    @BindView(R2.id.ivHeader)
    ImageView ivHeader;
    @BindView(R2.id.rlNickName)
    RelativeLayout rlNickName;
    @BindView(R2.id.tvNickName)
    TextView tvNickName;
    @BindView(R2.id.rlAuth)
    RelativeLayout rlAuth;
    @BindView(R2.id.tvAuth)
    TextView tvAuth;
    @BindView(R2.id.rlRegisteredTime)
    RelativeLayout rlRegisteredTime;
    @BindView(R2.id.tvRegisteredTime)
    TextView tvRegisteredTime;
    @BindView(R2.id.rlBabyInformation)
    RelativeLayout rlBabyInformation;
    //    @BindView(R2.id.tvBabiesInformation)
//    TextView tvBabiesInformation;
    @BindView(R2.id.babyInfoLayout)
    BabyInfoLayout babyInfoLayout;
    @BindView(R2.id.rlResident)
    RelativeLayout rlResident;
    @BindView(R2.id.tvResident)
    TextView tvResident;
    @BindView(R2.id.tvZm)
    TextView tvZm;
    @BindView(R2.id.tvZmDesc)
    TextView tvZmDesc;
    @BindView(R2.id.sinaWeiBoStatus)
    TextView sinaWeiBoStatus;
    @BindView(R2.id.sinaWeiBo)
    TextView sinaWeiBo;
    @BindView(R.id.bindZhimaLayout)
    RelativeLayout bindZhimaLayout;
    @BindView(R.id.tv_verified)
    TextView tvVerified;
    @BindView(R.id.tv_verifiedStatus)
    TextView tvVerifiedStatus;
    private UserInfo userInfo;
    private long myUserId;
    private int REQUEST_CODE_FOR_GET_CREDIT_SCORE = 0x1311;
    private Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_information);
        ButterKnife.bind(this);
        myUserId = CommonPreference.getUserId();
        if (getIntent().hasExtra(MyConstants.EXTRA_USER_INFO)) {
            userInfo = (UserInfo) getIntent().getSerializableExtra(MyConstants.EXTRA_USER_INFO);
            System.out.println("get user info:" + userInfo.toString());
        }
        initUserInfo();
    }

    @Override
    public void initTitleBar() {
        setTitleString("店主资料");
    }

    private void initUserInfo() {
        if (userInfo == null) {
            return;
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivHeader.getLayoutParams();
        params.width = CommonUtils.getScreenWidth();
        params.height = params.width * 21 / 75;


        String userName = userInfo.getUserName();
        if (!TextUtils.isEmpty(userName)) {
            rlNickName.setVisibility(View.VISIBLE);
            tvNickName.setText(userName);
        } else {
            rlNickName.setVisibility(View.GONE);
        }

        String startUserTitle = userInfo.getStarUserTitle();
        if (!TextUtils.isEmpty(startUserTitle)) {
            String tmp = startUserTitle.substring(6, startUserTitle.length());
            rlAuth.setVisibility(View.VISIBLE);
            tvAuth.setText(tmp);
        } else {
            rlAuth.setVisibility(View.GONE);
        }

        long registeredTime = userInfo.getRegisterTime();
        if (registeredTime > 0) {
            rlRegisteredTime.setVisibility(View.VISIBLE);
            tvRegisteredTime.setText(DateTimeUtils.getDateStr(registeredTime, "yyyy年MM月dd日"));
        } else {
            rlRegisteredTime.setVisibility(View.GONE);
        }

        ArrayList<Baby> babies = userInfo.getBabys();
        if (!ArrayUtil.isEmpty(babies)) {
            rlBabyInformation.setVisibility(View.VISIBLE);
            babyInfoLayout.setBabies(babies);
        } else {
            rlBabyInformation.setVisibility(View.GONE);
        }

        if (userInfo.getArea() != null) {
            Area area = userInfo.getArea();
            String city = area.getCity();
            String dist = area.getArea();
            if (!TextUtils.isEmpty(city) && !TextUtils.isEmpty(dist)) {
                rlResident.setVisibility(View.VISIBLE);
                tvResident.setText(city + "-" + dist);
            } else {
                rlResident.setVisibility(View.GONE);
            }
        } else {
            rlResident.setVisibility(View.GONE);
        }

        int zmPoint = userInfo.getZmCreditPoint();

        if (zmPoint > 0 || userInfo.hasVerified) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_mine_zm);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvZm.setCompoundDrawables(drawable, null, null, null);

            tvZmDesc.setText("查看");
            tvZmDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_enter, 0);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_mine_zm_grey);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvZm.setCompoundDrawables(drawable, null, null, null);

            if (myUserId > 0 && myUserId == userInfo.getUserId()) {
                tvZmDesc.setText("去绑定");
            } else {
                tvZmDesc.setText("未绑定");
            }
            tvZmDesc.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        if (!userInfo.isBindWeibo()) {
            Drawable drawable = getResources().getDrawable(R.drawable.wd_weibo_grey);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            sinaWeiBo.setCompoundDrawables(drawable, null, null, null);

            sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            if (myUserId > 0 && myUserId == userInfo.getUserId()) {
                sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.base_pink));
                sinaWeiBoStatus.setText("去绑定");
//                sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));*/
//                sinaWeiBoStatus.setText("未绑定");
            } else {
                sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
                sinaWeiBoStatus.setText("未绑定");
            }

            sinaWeiBoStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.wd_weibo_open);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            sinaWeiBo.setCompoundDrawables(drawable, null, null, null);

            sinaWeiBoStatus.setText("查看微博");
            sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            sinaWeiBoStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_enter, 0);
        }
        if (userInfo.hasVerified) {
            drawable = getResources().getDrawable(R.drawable.icon_is_verified);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvVerified.setCompoundDrawables(drawable, null, null, null);
            tvVerifiedStatus.setText("已认证");
            tvVerifiedStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
        } else {
            drawable = getResources().getDrawable(R.drawable.icon_not_verified);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvVerified.setCompoundDrawables(drawable, null, null, null);
            tvVerifiedStatus.setText("去认证");
            tvVerifiedStatus.setTextColor(getResources().getColor(R.color.base_circle_border));
        }
    }

    private boolean isCredited() {
        return userInfo.getZmCreditPoint() > 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_FOR_GET_CREDIT_SCORE) {
                if (data == null) {
                    return;
                }

                int cancelCode = data.getIntExtra(MyConstants.AUTH_ZM_CANCEL, 0);
                if (cancelCode == 1234) {
                    userInfo = CommonPreference.getUserInfo();
                    if (userInfo != null) {
                        initUserInfo();
                    }
                }
            } else if (requestCode == MyConstants.REQUEST_CODE_FOR_WEB) {
                if (data == null) {
                    return;
                }
                String flag = data.getStringExtra(MyConstants.EXTRA_GET_CREDIT_SCORE);
                if (flag.equals(MyConstants.GET_CREDIT_SCORE_VALUE)) {
                    userInfo = CommonPreference.getUserInfo();
                    if (userInfo != null) {
                        initUserInfo();
                        if (isCredited()) {
                            bindZhimaLayout.setEnabled(true);
                            bindZhimaLayout.performClick();
                        }
                    }
                }
            }
        }
    }

    @OnClick(R.id.rl_verified)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        
        Intent intent;
        switch (v.getId()) {
            case R.id.tvZmDesc:
                if (userInfo == null) {
                    return;
                }
                if (isCredited()) {
                    intent = new Intent();
                    intent.setClass(this, CreditPanelActivity.class);
                    intent.putExtra("zmCreditPoint", userInfo.getZmCreditPoint());
                    intent.putExtra("isNotMine", userInfo.getUserId() == CommonPreference.getUserId() ? false : true);
                    startActivityForResult(intent, REQUEST_CODE_FOR_GET_CREDIT_SCORE);
                } else {
                    startRequestForCreditZM();
                }
                break;
            case R.id.rl_verified:
                if (ConfigUtil.isToVerify()) {
                    intent = new Intent(this, VerifiedActivity.class);
                    startActivityForResult(intent, MyConstants.FROM_FLAGS_PERSONALDATAACTIVITY);
                }
                break;
        }
    }

    private void startRequestForCreditZM() {
        if (!CommonUtils.isNetAvaliable(this)) {
            ToastUtil.toast(this, "请检查网络连接");
            return;
        }

        ProgressDialog.showProgress(this);
        HashMap<String, String> params = new HashMap<>();

        OkHttpClientManager.postAsyn(MyConstants.CREDIT_FOR_ZMXY, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                ProgressDialog.closeProgress();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        CreditInfo info = JSON.parseObject(baseResult.obj, CreditInfo.class);
                        String url = info.getCredential();
                        Intent intent = new Intent();
                        intent.setClass(OwnerInformationActivity.this, WebViewActivity.class);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, url);
                        intent.putExtra(MyConstants.EXTRA_WEBVIEW_TITLE, "芝麻信用");
                        startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_WEB);

                    } else {
                        CommonUtils.error(baseResult, OwnerInformationActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }


    @OnClick({R.id.bindZhimaLayout, R.id.sinaWeiBoLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bindZhimaLayout:
                if (userInfo == null) {
                    return;
                }
                if (isCredited()) {
                    Intent intent = new Intent();
                    intent.setClass(this, CreditPanelActivity.class);
                    intent.putExtra("zmCreditPoint", userInfo.getZmCreditPoint());
                    intent.putExtra("isNotMine", userInfo.getUserId() == CommonPreference.getUserId() ? false : true);
                    startActivityForResult(intent, REQUEST_CODE_FOR_GET_CREDIT_SCORE);
                } else {
                    if (myUserId > 0 && myUserId != userInfo.getUserId()) {
                        return;
                    }

                    if (TextUtils.isEmpty(CommonPreference.getAccessToken())) {
                        return;
                    }

                    startRequestForCreditZM();
                }
                break;
            case R.id.sinaWeiBoLayout:
                if (userInfo.isBindWeibo()) {
                    Intent webViewIntent = new Intent();
                    webViewIntent.setClass(this, WebViewActivity.class);
                    webViewIntent.putExtra(MyConstants.EXTRA_WEBVIEW_URL, MyConstants.BASE_URL + "/social/v1/goToUserWeibo?uid=" + userInfo.getWeiboUserId());
                    startActivity(webViewIntent);
                } else {
                    if (myUserId > 0 && myUserId != userInfo.getUserId()) {
                        return;
                    }

                    Logger.e("get message:" + myUserId);

                    if (TextUtils.isEmpty(CommonPreference.getAccessToken())) {
                        return;
                    }
                    BindSinaUtil bindSinaUtil = new BindSinaUtil(this);
                    bindSinaUtil.bindSina();
                }
                break;
        }

    }


    private void freshWeiBoInfo(UserInfo userInfo) {
        if (!userInfo.isBindWeibo()) {
            Drawable drawable = getResources().getDrawable(R.drawable.wd_weibo_grey);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            sinaWeiBo.setCompoundDrawables(drawable, null, null, null);

            sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            if (myUserId > 0 && myUserId == userInfo.getUserId()) {
                sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.base_pink));
                sinaWeiBoStatus.setText("去绑定");
//                sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));*/
//                sinaWeiBoStatus.setText("未绑定");
            } else {

                sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
                sinaWeiBoStatus.setText("未绑定");
            }

            sinaWeiBoStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.wd_weibo_open);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            sinaWeiBo.setCompoundDrawables(drawable, null, null, null);

            sinaWeiBoStatus.setText("查看微博");
            sinaWeiBoStatus.setTextColor(getResources().getColor(R.color.text_color_gray));
            sinaWeiBoStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_enter, 0);
        }
    }

    @Override
    public void refreshSinaUI(String unionId) {
        userInfo.setBindWeibo(true);
        userInfo.setWeiboUserId(unionId);
        CommonPreference.setUserInfo(userInfo);
        SharedPreferences sp = MyApplication.getApplication()
                .getSharedPreferences(PREF_FILE_NAME, PreferenceActivity.MODE_PRIVATE);
        sp.edit().putString(CommonPreference.SINA_UID, unionId).apply();
        freshWeiBoInfo(userInfo);
    }
}
