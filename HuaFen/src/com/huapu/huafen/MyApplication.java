package com.huapu.huafen;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Pair;

import com.alibaba.fastjson.JSON;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVMixpushManager;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.Signature;
import com.avos.avoscloud.SignatureFactory;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.huapu.huafen.activity.MainActivity;
import com.huapu.huafen.activity.SplashActivity;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.beans.KvsdData;
import com.huapu.huafen.beans.LeanCloudSign;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.Screen;
import com.huapu.huafen.beans.SplashScreen;
import com.huapu.huafen.callcenter.CallCenterHelper;
import com.huapu.huafen.chatim.IMUserProfileProvider;
import com.huapu.huafen.chatim.handler.LCIMMessageHandler;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.db.DBHelper;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.RequestManagerSys;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.utils.CommonPreference;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.Config;
import com.huapu.huafen.utils.ConfigUtil;
import com.huapu.huafen.utils.LocationHelper;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.weixin.WeChatPayHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.ffmpeg.android.FFMPEGController;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.handler.LCIMClientEventHandler;
import de.greenrobot.event.EventBus;

/**
 * Application
 */
public class MyApplication extends MultiDexApplication {

    private String CHATKIT_ID = "btbuVwvFMiwXMdsfI4PwKYap-gzGzoHsz";
    private String CHATKIT_KEY = "ydftg00dqjHeGzbo05uM7sjN";

    private static int sessionDepth = 0;
    private static final String TAG = "Init";
    private final static String ALI_APPKEY = "23476665";
    private final static String ALI_APPSECRET = "08264364f0e6f3423cd9e1a59e78cbe8";
    public static int fromVersionCode;
    public String userId;
    private static MyApplication mApplication;
    private static List<Pair<String, List<Brand>>> amazingListData;
    //    private static boolean isFirst = true;
//    private  long backTime;
//    private  long currentTime;
    //切到前台显示闪屏时间间隔 30min
//    private  long restTime=1000*60*30;
// 10s
//    private  long restTime=1000*10;
    public int count = 0;
    /**
     * 12306 https证书
     */
    private String CER_12306 = "-----BEGIN CERTIFICATE-----\n" +
            "MIICmjCCAgOgAwIBAgIIbyZr5/jKH6QwDQYJKoZIhvcNAQEFBQAwRzELMAkGA1UEBhMCQ04xKTAn\n" +
            "BgNVBAoTIFNpbm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMB4X\n" +
            "DTA5MDUyNTA2NTYwMFoXDTI5MDUyMDA2NTYwMFowRzELMAkGA1UEBhMCQ04xKTAnBgNVBAoTIFNp\n" +
            "bm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMIGfMA0GCSqGSIb3\n" +
            "DQEBAQUAA4GNADCBiQKBgQDMpbNeb34p0GvLkZ6t72/OOba4mX2K/eZRWFfnuk8e5jKDH+9BgCb2\n" +
            "9bSotqPqTbxXWPxIOz8EjyUO3bfR5pQ8ovNTOlks2rS5BdMhoi4sUjCKi5ELiqtyww/XgY5iFqv6\n" +
            "D4Pw9QvOUcdRVSbPWo1DwMmH75It6pk/rARIFHEjWwIDAQABo4GOMIGLMB8GA1UdIwQYMBaAFHle\n" +
            "tne34lKDQ+3HUYhMY4UsAENYMAwGA1UdEwQFMAMBAf8wLgYDVR0fBCcwJTAjoCGgH4YdaHR0cDov\n" +
            "LzE5Mi4xNjguOS4xNDkvY3JsMS5jcmwwCwYDVR0PBAQDAgH+MB0GA1UdDgQWBBR5XrZ3t+JSg0Pt\n" +
            "x1GITGOFLABDWDANBgkqhkiG9w0BAQUFAAOBgQDGrAm2U/of1LbOnG2bnnQtgcVaBXiVJF8LKPaV\n" +
            "23XQ96HU8xfgSZMJS6U00WHAI7zp0q208RSUft9wDq9ee///VOhzR6Tebg9QfyPSohkBrhXQenvQ\n" +
            "og555S+C3eJAAVeNCTeMS3N/M5hzBRJAoffn3qoYdAO1Q8bTguOi+2849A==\n" +
            "-----END CERTIFICATE-----";
    private boolean isLoc;
    private FFMPEGController fc;
    private Context context;

    public static List<Pair<String, List<Brand>>> getAmazingListData() {
        return amazingListData;
    }

    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static MyApplication getApplication() {
        return mApplication;
    }

    private void initFFmpeg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File fileAppRoot = new File(
                        getApplicationInfo().dataDir);
                try {
                    fc = new FFMPEGController(
                            context, fileAppRoot);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LogUtil.i("liang", "application----onTerminate");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        long curTime = System.currentTimeMillis();
        mApplication = this;
        context = this.getApplicationContext();
        DBHelper.getSqliteHelper(this).getWritableDatabase();
        FeedbackAPI.init(this, ALI_APPKEY, ALI_APPSECRET);
        // OkHttpClientManager.getInstance().setCertificates(new Buffer().writeUtf8(CER_12306).inputStream());

        int versionCode = CommonUtils.getVersionCode(getApplicationContext());
        int cacheVersionCode = CommonPreference.getVersionCode();
        fromVersionCode = cacheVersionCode;
        if (versionCode > cacheVersionCode) {
            CommonPreference.setVersionCode(versionCode);
            CommonPreference.clearKVSDObject();
            CommonPreference.setBooleanValue("first_search", true);
            CommonPreference.setBooleanValue("first_mine", true);
            CommonPreference.setBooleanValue("first_flower", true);
            CommonPreference.setBooleanValue("first_following", true);
            CommonPreference.setBooleanValue("first_remarks", true);
            CommonPreference.setBooleanValue("first_release_postpaid", true);
            CommonPreference.setBooleanValue("first_release_postpaid_one", true);
            CommonPreference.setBooleanValue(CommonPreference.IS_UPDATE, true);
        }

        registerActivity();

        long endCurTime = System.currentTimeMillis();
        LogUtil.e("liang", "application cur:---" + (endCurTime - curTime));
        String pro = getCurProcessName(getApplicationContext());
        LogUtil.e("liang", "pro name:---" + pro);
//		initDirs();
        initFFmpeg();
        initFresco();
        WeChatPayHelper.register(this);
        initLeanCloudChatKit();
        initLeanCloudPushService();
        initLeanCloudStatistics();
        // call center
        CallCenterHelper.init(this);
    }

    /**
     * 初始化 LeanCloud ChatKit
     */
    private void initLeanCloudChatKit() {
        if (Config.isDebug(this)) {
            CHATKIT_ID = "btbuVwvFMiwXMdsfI4PwKYap-gzGzoHsz";
            CHATKIT_KEY = "ydftg00dqjHeGzbo05uM7sjN";
//            MyConstants.COMMENT_ID = "59fc33beee920a003cf998fd";//留言会话
//            MyConstants.NOTICE_ID = "59fc33db44d904003ee70061";//通知会话id
//            MyConstants.CONV_ORDER_ID = "59fc33a1ee920a003cf998ca";//订单会话id
//            MyConstants.ACTIVITY_ID = "59fc33e9128fe100445d5ed1";//广播系统id
        }
        LCChatKit.getInstance().setProfileProvider(new IMUserProfileProvider());
        LCIMClientEventHandler eventHandler = LCIMClientEventHandler.getInstance();
        eventHandler.setOnClientOfflineListener(new LCIMClientEventHandler.OnClientOfflineListener() {
            @Override
            public void onClientOffline(AVIMClient avimClient, int errCode) {
                // 下线
                DialogManager.loginAndDialog(com.huapu.huafen.ActivityManager.getCurrActivity());
            }
        });
        LCChatKit.getInstance().init(getApplicationContext(),
                MyConstants.CHATKIT_ID, MyConstants.CHATKIT_KEY, new LCIMMessageHandler(this), eventHandler);

        AVIMClient.setAutoOpen(false);
        AVIMClient.setSignatureFactory(new SignatureFactory() {
            @Override
            public Signature createSignature(String peerId, List<String> watchIds)
                    throws SignatureFactory.SignatureException {
                try {
                    LogUtil.i(TAG, "createSignature: ", peerId, watchIds);
                    Response response = OkHttpClientManager.post(MyConstants.LEANCLOUD_SIGN);
                    String body = response.body().string();
                    LogUtil.i(TAG, "createSignature result: " + body);
                    BaseResult baseResult = JSON.parseObject(body, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        final LeanCloudSign leanCloudSign = JSON.parseObject(baseResult.obj, LeanCloudSign.class);
                        Signature signature = new Signature();
                        signature.setNonce(leanCloudSign.nonce);
                        signature.setTimestamp(leanCloudSign.ts);
                        signature.setSignature(leanCloudSign.sign);
                        return signature;
                    }
                } catch (IOException e) {
                    LogUtil.e(e);
                }
                return null;
            }

            @Override
            public Signature createGroupSignature(String s, String s1, List<String> list, String s2) throws SignatureException {
                return null;
            }

            @Override
            public Signature createConversationSignature(String s, String s1, List<String> list, String s2) throws SignatureException {
                return null;
            }
        });
    }

    /**
     * 初始化 LeanCloud 推送服务
     * <p>
     * 重要：必须在 initLeanCloudChatKit 后调用
     */
    private void initLeanCloudPushService() {
        AVMixpushManager.registerFlymePush(getApplicationContext(), MyConstants.FLYME_ID, MyConstants.FLYME_KEY);
        AVMixpushManager.registerXiaomiPush(getApplicationContext(), MyConstants.MI_ID, MyConstants.MI_KEY);
        AVMixpushManager.registerHuaweiPush(getApplicationContext());
        // 设置默认打开的 Activity
        PushService.setDefaultPushCallback(this, MainActivity.class);
        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
        PushService.subscribe(this, "public", MainActivity.class);
        // 保存 installation 到服务器
        String installationId = AVInstallation.getCurrentInstallation().getInstallationId();

        LogUtil.d("LeanCloud", "installationId=" + installationId);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null && Config.DEBUG) {
                    LogUtil.e(e, TAG, "AVInstallation saveInBackground Error");
                }
            } // ~ done
        });// ~ saveInBackground
    }

    /**
     * 初始化LeanCloud统计
     */
    private void initLeanCloudStatistics() {
        AVOSCloud.initialize(this, MyConstants.CHATKIT_ID, CHATKIT_KEY);
        AVAnalytics.enableCrashReport(this, true);
    }

    private void initFresco() {
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this).
                setMaxCacheSize(100 * ByteConstants.MB).build();

        Supplier<MemoryCacheParams> bitmapCacheParamsSupplier = new Supplier<MemoryCacheParams>() {

            @Override
            public MemoryCacheParams get() {
                MemoryCacheParams param = new MemoryCacheParams(
                        20 * ByteConstants.MB
                        , Integer.MAX_VALUE
                        , 20 * ByteConstants.MB
                        , Integer.MAX_VALUE
                        , Integer.MAX_VALUE);
                return param;
            }
        };

        // 当内存紧张时采取的措施
        MemoryTrimmableRegistry registry = NoOpMemoryTrimmableRegistry.getInstance();
        registry.registerMemoryTrimmable(new MemoryTrimmable() {
            @Override
            public void trim(MemoryTrimType trimType) {
                final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio
                        ) {
                    //清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches();
                }
            }
        });

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setDownsampleEnabled(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .setMemoryTrimmableRegistry(registry)
                .build();

        Fresco.initialize(this, config);
    }

    /**
     * 应用生命周期回调的注册方法
     */
    private void registerActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtil.i("liang", activity + "onActivityStopped");
                count--;
                if (count == 0) {
                    CommonPreference.saveSleepMillions();
                    MyConstants.ONSTAGE = false;
                    LogUtil.i("liang", ">>>>>>>>>>>>>>>>>>>切到后台  lifecycle");
//                    backTime= System.currentTimeMillis();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtil.i("liang", activity + "onActivityStarted");
                if (count == 0) {
                    LogUtil.i("liang", ">>>>>>>>>>>>>>>>>>>切到前台  lifecycle");
                    MyConstants.ONSTAGE = true;

                    startRequestForKvsd();

                    long localMillions = CommonPreference.getLocationMillions();
                    long current = System.currentTimeMillis();
                    boolean isEnough = (current - localMillions) > 1000 * 60 * 30 ? true : false;
                    if (!isLoc && isEnough) {
                        isLoc = true;
                        LocationHelper.startLocation(new LocationHelper.OnLocationListener() {

                            @Override
                            public void onLocationComplete(LocationData locationData) {
                                isLoc = true;
                                CommonPreference.saveLocationMillions();
                                CommonPreference.saveLocalData(locationData);
                            }

                            @Override
                            public void onLocationFailed() {
                                isLoc = true;
                            }
                        });
                    }
                    SplashScreen splashScreen = CommonPreference.getSplashScreen();
                    if (splashScreen != null) {
                        Screen screen = splashScreen.getScreen();
                        if (screen != null) {
                            long sleepMillions = CommonPreference.getSleepMillions();
                            long time = System.currentTimeMillis();
                            long start = screen.getStart();
                            long end = screen.getEnd();
                            if (start < time && time < end) {
                                if (splashScreen.getRepeat() == 1) {
                                    if (time - sleepMillions >= splashScreen.getRepeatTime()) {
                                        ActivityManager am = (ActivityManager) MyApplication.getApplication().getSystemService(Context.ACTIVITY_SERVICE);
                                        if (am != null) {
                                            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
                                            if (cn != null) {
                                                if (!cn.getClassName().equals(SplashActivity.class.getName()) &&
                                                        cn.getPackageName().equals(MyApplication.getApplication().getPackageName())) { // 判断顶部activity为广告页
                                                    Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
                                                    intent.putExtra(MyConstants.EXTRA_START_MAIN, false);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
//                    if (!isFirst) {
//                        // 从后台返回
//                        currentTime = System.currentTimeMillis();
//                        if (currentTime > (backTime + restTime)) {
//                            Intent sActivity = new Intent(getApplicationContext(), SplashActivity.class);
//                            startActivity(sActivity);
//                        }
//
//                    } else {
//                        isFirst = false;
//                    }
                }
                count++;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtil.i("liang", activity + "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtil.i("liang", activity + "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtil.i("liang", activity + "onActivityPaused");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtil.i("liang", activity + "onActivityDestroyed");
            }

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtil.i("liang", activity + "onActivityCreated");
            }
        });
    }

    public FFMPEGController getFc() {
        return fc;
    }

    private void startRequestForKvsd() {
        OkHttpClientManager.StringCallback callBack = new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("laloErrorApp", "init:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e("laloResponseApp", "init:" + response.toString());
                try {
                    boolean flag = CommonUtils.parseEvent(context, response, null);
                    if (flag) {
                        return;
                    }
                    BaseResult baseResult = JSON.parseObject(response, BaseResult.class);
                    if (baseResult.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        if (!TextUtils.isEmpty(baseResult.obj)) {
                            KvsdData data = JSON.parseObject(baseResult.obj, KvsdData.class);
                            CommonPreference.setKVSDObject(data);
                            MyConstants.UNREAD_ORDER_COUNT = data.getUnreadMessageCounts().getOrder();
                            MessageUnReadCountEvent eventComment = new MessageUnReadCountEvent();
                            eventComment.isUpdate = true;
                            EventBus.getDefault().post(eventComment);
                            CommonUtils.getBrandListDataFromServer();
                            ConfigUtil.loginLeanCloud(getApplication());
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("lalo", "init crash:" + e.getMessage());
                }
            }
        };

        RequestManagerSys.startRequestForKvsd(CommonPreference.getAppToken(), callBack);
    }
}
