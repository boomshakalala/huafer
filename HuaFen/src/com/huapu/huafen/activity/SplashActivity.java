package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.CheckUpdateInfo;
import com.huapu.huafen.beans.KvsdData;
import com.huapu.huafen.beans.Screen;
import com.huapu.huafen.beans.SplashScreen;
import com.huapu.huafen.callbacks.BitmapCallback;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.events.PushEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.RequestManagerSys;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.ActionUtil;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.JsonValidator;
import com.huapu.huafen.utils.LogUtil;
import com.squareup.okhttp.Request;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * 欢迎界面
 *
 * @author liang_xs
 *         每次启动时都调用配置同步信息接口，并判断本地是否有token，如果本地有token则执行跳转；
 *         如果本地无token，则不跳转，等待config接口返回，在config接口成功回调中判断：
 *         本地如果没有token则将token保存到sp，并再次调用检测跳转方法；
 *         如果本地有token则不处理；
 *         如果服务器返回异常或网络异常，则提示用户检查网络重试；
 *         <p>
 *         原因：每次调用config接口，服务器返回token都会发生变化，
 *         而且每个接口都需要token作为参数，如果在调用其他接口时检测本地token信息，再次调用config会出现
 *         1. config调用 token1未返回时，调用other接口，会再次调用config，如果此时返回token2，
 *         则other接口使用token2作为参数向服务器请求，此时token1返回，覆盖本地sp，程序在调用other1接口时，
 *         会使用token1作为参数，问题未得到解决，故有此设计。待完善
 */
public class SplashActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    private boolean isFirst;
    private TextDialog dialog;
    private CheckUpdateInfo configBean;
    private SplashScreen splashScreen;

    private Screen screen;
    //	private ConfigBean configBean;
    private String extraMap;
    private View layoutAd;
    private View layoutCountdown;
    private ImageView ivAd;
    private boolean startMain = true;
    private boolean isClick = false;
    private Animation animation;
    private int count = 3;
    private TextView tvCountdown;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                tvCountdown.setText(getCount() + "\"");
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    private int getCount() {
        count--;
        if (count == 0) {
            if (!isClick) {
                nextActivity();
            }
        }
        if (count < 0) {
            return 0;
        }
        return count;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        long curTime = System.currentTimeMillis();
        EventBus.getDefault().register(this);
        layoutAd = findViewById(R.id.layoutAd);
        layoutCountdown = findViewById(R.id.layoutCountdown);
        ivAd = (ImageView) findViewById(R.id.ivAd);
        tvCountdown = (TextView) findViewById(R.id.tvCountdown);
        layoutCountdown.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(MyConstants.EXTRA_MAP)) {
                extraMap = intent.getStringExtra(MyConstants.EXTRA_MAP);
            }
        }
        if (intent != null) {
            if (intent.hasExtra(MyConstants.EXTRA_START_MAIN)) {
                startMain = intent.getBooleanExtra(MyConstants.EXTRA_START_MAIN, true);
            }
        }

        LogUtil.e("SplashActivity", "extraMap:--" + extraMap);
        fromWeb();
        long endCurTime = System.currentTimeMillis();
        LogUtil.e("liang", "splash cur:---" + (endCurTime - curTime));
    }

    public void onEventMainThread(final Object obj) {
        if (obj == null) {
            return;
        }
        if (obj instanceof PushEvent) {
            LogUtil.e("onEventMainThread..", "PushEvent");
            PushEvent event = (PushEvent) obj;
            extraMap = event.extraMap;
        }
    }


    private void showAd() {
        handler.sendEmptyMessageDelayed(0, 1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.animation_ad_alpha);
                splashScreen = CommonPreference.getSplashScreen();
                if (splashScreen != null) {
                    screen = splashScreen.getScreen();
                }

                if (screen != null) {
                    long time = System.currentTimeMillis();
                    long start = screen.getStart();
                    long end = screen.getEnd();
                    String lastImageUrl = CommonPreference.getStringValue(CommonPreference.ACTION_IMAGE_URL, "");
//                    if (screen.getImage() != null && screen.getImage().equals(lastImageUrl)) {
                    if (screen.getImage() != null) {
                        Bitmap bmp = FileUtils.getBitmap(screen.getName());
                        if (bmp != null && start < time && time < end) {
                            // 显示网络图片
//								BitmapDrawable bd=new BitmapDrawable(bmp);
//								ivAd.setBackground(bd);
                            ivAd.setImageBitmap(bmp);
                            layoutAd.setOnClickListener(SplashActivity.this);
                            screen.setHasRead(1);
                            CommonPreference.setSplashScreen(splashScreen);
                        } else {
                            // 显示本地图片
//								ivAd.setBackgroundResource(R.drawable.ad);
                            ivAd.setImageResource(R.drawable.ad);
                            layoutCountdown.setVisibility(View.GONE);
                            count = 2;
                        }
                    } else {
                        // 显示本地图片，并重新下载
//							ivAd.setBackgroundResource(R.drawable.ad);
                        ivAd.setImageResource(R.drawable.ad);
                        layoutCountdown.setVisibility(View.GONE);
                        count = 2;
                        if (!TextUtils.isEmpty(screen.getImage()) && !TextUtils.isEmpty(screen.getName())) {
                            final String imgUrl = screen.getImage();
                            final String name = screen.getName();

                            ImageLoader.loadBitmap(SplashActivity.this, Uri.parse(imgUrl),
                                    new BitmapCallback() {
                                        @Override
                                        public void onBitmapDownloaded(final Bitmap bitmap) {
                                            if (bitmap != null) {
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        boolean isSaved = FileUtils.saveBitmap(name, bitmap);
//                                                if (isSaved) {
//                                                    CommonPreference.setStringValue(CommonPreference.ACTION_IMAGE_URL, screen.getImage());
//                                                }
                                                    }
                                                }).start();
                                            }
                                        }
                                    });
                        }
                    }
                } else {
                    // 显示本地图片
                    ivAd.setImageResource(R.drawable.ad);
                    layoutCountdown.setVisibility(View.GONE);
                    count = 2;
                }
                tvCountdown.setText(count + "\"");
                layoutAd.setVisibility(View.VISIBLE);
                layoutAd.startAnimation(animation);
            }
        }, 0);

    }

    private void nextActivity() {
        if (!TextUtils.isEmpty(CommonPreference.getActivationSecret()) && !TextUtils.isEmpty(CommonPreference.getActivationToken())) {
            isFirst = CommonPreference.getBooleanValue(CommonPreference.IS_FIRST, true);
            if (!isFirst) {
                boolean isUpdate = CommonPreference.getBooleanValue(CommonPreference.IS_UPDATE, true);
                // 如果不需要更新引导图，将其设置为false
//				isUpdate = false;
                if (isUpdate) {
                    Intent intent = new Intent(SplashActivity.this, GuideUpdateActivity.class);
                    intent.putExtra(MyConstants.EXTRA_MAP, extraMap);
                    startActivity(intent);
                } else {
                    if (startMain) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra(MyConstants.EXTRA_MAP, extraMap);
                        if (configBean != null) {
                            intent.putExtra(MyConstants.EXTRA_APP_LEVEL, configBean.getLevel());
                            intent.putExtra(MyConstants.EXTRA_APP_URL, configBean.getUrl());
                            intent.putExtra(MyConstants.EXTRA_APP_CONTENT, configBean.getNote());
                        }
                        startActivity(intent);
                    }
                }
            } else {
                Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                intent.putExtra(MyConstants.EXTRA_MAP, extraMap);
                startActivity(intent);
            }
            if (dialog != null) {
                dialog.dismiss();
            }
            SplashActivity.this.finish();
        }
    }

    private String targetType = "";
    private String targetId = "";

    /**
     * web唤起APP
     */
    private void fromWeb() {
        Intent intentValue = getIntent();
        String action = intentValue.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            if (TextUtils.isEmpty(CommonPreference.getActivationSecret()) || TextUtils.isEmpty(CommonPreference.getActivationToken())) {
                startRequestForKvsd();
            } else {
                Uri uri = getIntent().getData();
                if (uri != null) {
                    //ToastUtil.toast(this, "url " + uri.toString());
                    // 唤起页面类型（商品详情或个人首页）
                    //List<String> paths = uri.getPathSegments();
                    String host = uri.getHost();
                    String path = uri.getPath();
//                    targetType = uri.getQueryParameter(MyConstants.SHARE_TARGET_TYPE);
//                    targetId = uri.getQueryParameter(MyConstants.SHARE_TARGET_ID);
                    if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(path)) {
                        // 唤起页面id
                        targetType = uri.toString();
                        // 唤起页面id
                        targetId = path;
                    }
                    //ToastUtil.toast(this, "targetType:" + targetType + " targetId:" + targetId);
                    LogUtil.d("danielluan", "targetType:" + targetType + " targetId:" + targetId);
                    if (!TextUtils.isEmpty(targetId) && !TextUtils.isEmpty(targetType)) {
                        if (TextUtils.isEmpty(CommonPreference.getActivationSecret()) || TextUtils.isEmpty(CommonPreference.getActivationToken())) {
                            startRequestForKvsd();
                        } else {
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.putExtra(MyConstants.SHARE_TARGET_TYPE, targetType);
                            intent.putExtra(MyConstants.SHARE_TARGET_ID, targetId);
                            startActivity(intent);
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            finish();
                        }
                    }
                } else {
                    startRequestForKvsd();
                }
            }
        } else {
            startRequestForKvsd();
        }
    }

    private void startRequestForKvsd() {
        if (!CommonUtils.isNetAvaliable(this)) {
            if (TextUtils.isEmpty(CommonPreference.getActivationSecret()) || TextUtils.isEmpty(CommonPreference.getActivationToken())) {
                dialog = new TextDialog(SplashActivity.this);
                dialog.setContentText("请联网后重试");
                dialog.setLeftText("确定");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        SplashActivity.this.finish();
                    }
                });
                if (SplashActivity.this != null) {
                    dialog.show();
                }
            } else {
                showAd();
            }
            return;
        }

        OkHttpClientManager.StringCallback callBack = new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("laloError", "init:" + e.toString());
                ProgressDialog.closeProgress();
                kvsError();
            }

            @Override
            public void onResponse(String response) {
                ProgressDialog.closeProgress();
                LogUtil.e("laloResponse", "init:" + response.toString());

                try {
                    boolean flag = CommonUtils.parseEvent(SplashActivity.this, response, null);
                    if (flag) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            configBean = ParserUtils.parserCheckUpdate(baseResult.obj);
                            KvsdData data = JSON.parseObject(baseResult.obj, KvsdData.class);
                            CommonPreference.setKVSDObject(data);
                            startRequestForUploadPushDevice();
                            kvsError();
                        }
                    } else {
                        kvsError();
                        CommonUtils.error(baseResult, SplashActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("lalo", "init crash:" + e.getMessage());
                    kvsError();
                }
            }
        };

        RequestManagerSys.startRequestForKvsd(CommonPreference.getAppToken(), callBack);
    }


    //上传推送deviceId
    private void startRequestForUploadPushDevice() {
        if (!CommonUtils.isNetAvaliable(this)) {
            return;
        }

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("aliyunDeviceId", CommonPreference.getStringValue(CommonPreference.ALI_PUSH_TOKEN, ""));
        LogUtil.e("lalo", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.UPLOAD_PUSH_DEVICE, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("laloErrorApp", "startRequestForUploadPushDevice:" + e.toString());
            }


            @Override
            public void onResponse(String response) {
                LogUtil.e("laloResponseApp", "startRequestForUploadPushDevice:" + response.toString());
                JsonValidator validator = new JsonValidator();
                boolean isJson = validator.validate(response);
                if (!isJson) {
                    return;
                }
                try {
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {

                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("lalo", "startRequestForUploadPushDevice crash:" + e.getMessage());
                }
            }
        });

    }

    private void kvsError() {
        if (TextUtils.isEmpty(CommonPreference.getActivationSecret()) || TextUtils.isEmpty(CommonPreference.getActivationToken())) {
            dialog = new TextDialog(SplashActivity.this);
            dialog.setContentText("服务器异常，请重试");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    SplashActivity.this.finish();
                }
            });
            if (SplashActivity.this != null) {
                dialog.show();
            }
        } else {
            showAd();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeMessages(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutCountdown:
                isClick = true;
                nextActivity();
                break;
            case R.id.layoutAd:
                isClick = true;
                nextActivity();
                if (splashScreen != null) {
                    ActionUtil.dispatchAction(this, screen.getAction(), screen.getTarget());
                }
                break;
        }

    }
}
