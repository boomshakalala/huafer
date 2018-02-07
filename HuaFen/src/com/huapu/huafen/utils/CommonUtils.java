package com.huapu.huafen.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.R;
import com.huapu.huafen.activity.AlbumNewActivity;
import com.huapu.huafen.activity.DownloadActivity;
import com.huapu.huafen.activity.FullServerActivity;
import com.huapu.huafen.activity.LoginActivity;
import com.huapu.huafen.activity.MainActivity;
import com.huapu.huafen.activity.MontageActivity;
import com.huapu.huafen.activity.ReleaseActivity;
import com.huapu.huafen.beans.BannerData;
import com.huapu.huafen.beans.BaseResult;
import com.huapu.huafen.beans.BaseResultNew;
import com.huapu.huafen.beans.Brand;
import com.huapu.huafen.beans.BrandsResult;
import com.huapu.huafen.beans.Campaign;
import com.huapu.huafen.beans.CampaignBanner;
import com.huapu.huafen.beans.Cat;
import com.huapu.huafen.beans.City;
import com.huapu.huafen.beans.Commodity;
import com.huapu.huafen.beans.District;
import com.huapu.huafen.beans.FilterAreaData;
import com.huapu.huafen.beans.GoodsData;
import com.huapu.huafen.beans.GoodsInfo;
import com.huapu.huafen.beans.ImageItem;
import com.huapu.huafen.beans.KvsdData;
import com.huapu.huafen.beans.LocationData;
import com.huapu.huafen.beans.Region;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.callbacks.OnBrandListDataLoaded;
import com.huapu.huafen.callbacks.OnRequestRetryListener;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.DialogManager;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.dialog.TextLeftDialog;
import com.huapu.huafen.events.MessageUnReadCountEvent;
import com.huapu.huafen.http.OkHttpClientManager;
import com.huapu.huafen.http.RequestManagerSys;
import com.huapu.huafen.parser.ParserUtils;
import com.huapu.huafen.views.ClassBannerView;
import com.squareup.okhttp.Request;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import de.greenrobot.event.EventBus;

/**
 * @author liang_xs
 * @date 2016-03-27
 */
public class CommonUtils {

    public static final String CTWAP = "ctwap";
    public static final String CTNET = "ctnet";
    public static final String CMWAP = "cmwap";
    public static final String CMNET = "cmnet";
    public static final String NET_3G = "3gnet";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    public static final String UNINET = "uninet";
    public static final int TYPE_CT_WAP = 5;
    public static final int TYPE_CT_NET = 6;
    public static final int TYPE_CT_WAP_2G = 7;
    public static final int TYPE_CT_NET_2G = 8;
    public static final int TYPE_CM_WAP = 9;
    public static final int TYPE_CM_NET = 10;
    public static final int TYPE_CM_WAP_2G = 11;
    public static final int TYPE_CM_NET_2G = 12;
    public static final int TYPE_CU_WAP = 13;
    public static final int TYPE_CU_NET = 14;
    public static final int TYPE_CU_WAP_2G = 15;
    public static final int TYPE_CU_NET_2G = 16;
    public static final int TYPE_OTHER = 17;
    /**
     * 没有网络
     */
    public static final int TYPE_NET_WORK_DISABLED = 0;
    /**
     * wifi网络
     */
    public static final int TYPE_WIFI = 4;
    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    private final static String TAG = CommonUtils.class.getSimpleName();
    public static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");
    protected static String uuid;
    private static DisplayMetrics displayMetrics = null;
    private static TextLeftDialog dialogUpdate;

    private static String getRandomString(int length, String base) {
        Random random = new Random(System.currentTimeMillis());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static boolean isExpressNum(String expressNum) {
        if (TextUtils.isEmpty(expressNum)) {
            return false;
        }
        String regex = "^[a-z0-9A-Z]*$";
        return expressNum.matches(regex);
    }

    public static boolean isNickName(String name) {
        if (name == null || "".equals(name)) {
            return false;
        }
        //		String regex = "[\u4e00-\u9fa5\\w]+";
        //		String regex = "http(s)?:\\/\\/([\\w-]+\\.)+[\\w-]+(\\/[\\w- .\\/?%&=]*)?";
        String regex = "^[a-zA-Z0-9\u4e00-\u9fa5]+$";

        return name.matches(regex);
    }

    /**
     * @return boolean
     * @Title: emailFormat
     * @Description: 验证邮箱格式
     * @author liang_xs
     */
    public static boolean emailFormat(String email) {
        boolean tag = true;
        String pattern1 = "[_a-z\\d\\-\\./]+@[_a-z\\d\\-]+(\\.[_a-z\\d\\-]+)*(\\.(info|biz|com|edu|gov|net|am|bz|cn|cx|hk|jp|tw|vc|vn))$";
        Pattern pattern = Pattern.compile(pattern1);
        Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }

    public static boolean isNetAvaliable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void checkAccess(final Activity activity) {
        CommonPreference.cleanUserInfoAndAccess();
        CommonPreference.setActivationSecret("");
        CommonPreference.setActivationToken("");
        CommonPreference.setIntValue("savedMoney", -1);//钱包金额
        CommonPreference.setUserId(0);
        authForKvsd(activity);
    }

    public static void copy(Context context, String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    public static void error(BaseResult baseResult, Activity activity, String msg) {
        if (baseResult.code == ParserUtils.SERVER_IS_FULL) {
            activity.startActivity(new Intent(activity, FullServerActivity.class));
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_TOAST) {
            if (!TextUtils.isEmpty(baseResult.msg))
                ToastUtil.toast(activity,baseResult.msg);

        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGIN) {
            ActionUtil.loginAndToast(activity);
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGOUT) {
            DialogManager.loginAndDialog(activity);
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
            checkAccess(activity);
        }
    }

    public static void error(BaseResultNew baseResult, Activity activity, String msg) {
        if (baseResult.code == ParserUtils.SERVER_IS_FULL) {
            activity.startActivity(new Intent(activity, FullServerActivity.class));
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_TOAST) {
            if (!baseResult.msg.equals("参数错误")) {
                ToastUtil.toast(activity, baseResult.msg);
            } else {
                ToastUtil.toast(activity, "请退出重试");
            }
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGIN) {
            ActionUtil.loginAndToast(activity);
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGOUT) {
            DialogManager.loginAndDialog(activity);
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
            checkAccess(activity);
        } else {
            ToastUtil.toast(activity, baseResult.msg);
        }
    }

    public static void error(BaseResult baseResult, Activity activity) {
        if (baseResult.code == ParserUtils.SERVER_IS_FULL) {
            activity.startActivity(new Intent(activity, FullServerActivity.class));
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_TOAST) {
            if (!baseResult.msg.equals("参数错误")) {
                ToastUtil.toast(activity, baseResult.msg);
            } else {
                ToastUtil.toast(activity, "请退出重试");
            }
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGIN) {
            ActionUtil.loginAndToast(activity);
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_LOGOUT) {
            DialogManager.loginAndDialog(activity);
        } else if (BaseResult.getErrorType(baseResult.code) == BaseResult.ERROR_TYPE_FOR_DATA_ERROR) {
            checkAccess(activity);
        } else {
            ToastUtil.toast(activity, baseResult.msg);
        }
    }

    /**
     * 返回当前程序版本号
     */
    public static String getAppVersionName() {
        String versionName = "";
        try {

            PackageManager pm = MyApplication.getApplication().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(MyApplication.getApplication().getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }

        } catch (Exception e) {

        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void showKeyBoard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager.isActive()) {
            inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideKeyBoard(Context context) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void hideInput(Activity activity) {
        View view = activity.getWindow().peekDecorView();
        if (view != null && view.getWindowToken() != null) {
            /* 隐藏软键盘 */
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void downloadImage(final Context context, String url, final OkHttpClientManager.StringCallback callback) {

        if (FileUtils.isSDCardExist()) {
            ProgressDialog.showProgress(context);
            OkHttpClientManager.downloadAsync(url, FileUtils.getHDir(), System.currentTimeMillis() + ".jpg", new OkHttpClientManager.StringCallback() {

                @Override
                public void onError(Request request, Exception e) {
                    if (callback != null) {
                        callback.onResponse("failed");
                    }
                    ToastUtil.toast(context, "图片保存失败");
                    ProgressDialog.closeProgress();
                }

                @Override
                public void onResponse(String response) {
                    ProgressDialog.closeProgress();
                    ContentValues values = new ContentValues(7);
                    values.put(MediaStore.Images.Media.TITLE, response);
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, response);
                    values.put(MediaStore.Images.Media.DATE_TAKEN,
                            new Date().getTime());
                    values.put(MediaStore.Images.Media.MIME_TYPE,
                            "image/jpeg");
                    values.put(MediaStore.Images.ImageColumns.BUCKET_ID,
                            response.hashCode());
                    values.put(
                            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                            response);
                    values.put("_data", response);
                    ContentResolver contentResolver = MyApplication.getApplication().getContentResolver();
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Uri uri = Uri.parse(response);
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    if (context != null) {
                        if (callback != null) {
                            callback.onResponse("success");
                        }
                        ToastUtil.toast(context, "图片保存成功");
                    }
                }
            });
        } else {
            if (context != null) {
                if (callback != null) {
                    callback.onResponse("failed");
                }
                ToastUtil.toast(context, "sd卡不可用，无法保存");
            }
        }

    }

    public static float getScreenDensity() {
        if (displayMetrics == null) {
            setDisplayMetrics(MyApplication.getApplication().getResources().getDisplayMetrics());
        }
        return displayMetrics.density;
    }

    public static int getScreenHeight() {
        if (displayMetrics == null) {
            setDisplayMetrics(MyApplication.getApplication().getResources().getDisplayMetrics());
        }
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth() {
        if (displayMetrics == null) {
            setDisplayMetrics(MyApplication.getApplication().getResources().getDisplayMetrics());
        }
        return displayMetrics.widthPixels;
    }

    public static int getMyHeight() {
        return (int) (13.0F * getScreenWidth() / 32.0F);
    }

    public static int getHomeBnnerHeight() {
        return (int) (432.0F * getScreenWidth() / 750.0F);
    }

    public static int getMinHeight() {
        return (int) (24.0F * getScreenWidth() / 75.0F);
    }

    public static void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        displayMetrics = DisplayMetrics;
    }

    public static int dp2px(float f) {
        return (int) (0.5F + f * getScreenDensity());
    }

    public static int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    public static void pickImage(Activity activity, int maxPickerCount, ArrayList<ImageItem> list) {
        if (maxPickerCount <= 0) {
            maxPickerCount = MyConstants.MATCH_SELECT_DYNAMIC_PHOTO;
        }
        Intent intent = new Intent(activity, AlbumNewActivity.class);
        intent.putExtra(MyConstants.EXTRA_SELECT_BITMAP, list);
        intent.putExtra(MyConstants.MAX_ALBUM_COUNT, maxPickerCount);
        activity.startActivityForResult(intent, MyConstants.INTENT_FOR_RESULT_TO_ALBUM);
    }

    public static void pickImage(Activity activity, ArrayList<ImageItem> list) {
        pickImage(activity, 0, list);
    }

    public static void pickSingleImage(Activity activity) {
        pickImage(activity, 1, new ArrayList<ImageItem>());
    }

    /**
     * 获取和保存当前屏幕的截图
     */
    public static String getAndSaveCurrentImage(Activity activity) {
        // 1.构建Bitmap
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int w = display.getWidth();
        int h = display.getHeight();

        Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);

        // 2.获取屏幕
        View decorview = activity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        Bmp = decorview.getDrawingCache();

        String SavePath = FileUtils.SD_APP_PATH + "/AndyDemo/ScreenImage";
        String filepath = "";
        // 3.保存Bitmap
        try {
            File path = new File(SavePath);
            // 文件
            filepath = SavePath + "/Screen_1.png";
            File file = new File(filepath);
            if (!path.exists()) {
                path.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = null;
            fos = new FileOutputStream(file);
            if (null != fos) {
                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
                Toast.makeText(activity, "截屏文件已保存至SDCard/AndyDemo/ScreenImage/下",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filepath;
    }

    public static String getUDID(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
        final String id = prefs.getString(PREFS_DEVICE_ID, null);
        if (id != null) {
            // Use the ids previously computed and stored in the prefs file
            uuid = id;
        } else {
            final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
            // Use the Android ID unless it's broken, in which case fallback on deviceId,
            // unless it's not available, then fallback on a random number which we store
            // to a prefs file
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                } else {
                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            // Write the value out to the prefs file
            prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
        }
        return uuid;
    }

    public static void updateAPP(final Activity mActivity, final String url, final String content, int appLevel) {
        if (appLevel == 2) {
            dialogUpdate = new TextLeftDialog(mActivity, true);
            dialogUpdate.setLeftText("取消");
            dialogUpdate.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialogUpdate.dismiss();

                }
            });
        } else if (appLevel == 3) {
            dialogUpdate = new TextLeftDialog(mActivity);
        }
        dialogUpdate.setRightText("确定");
        dialogUpdate.setRightCall(new DialogCallback() {

            @Override
            public void Click() {
                //				Uri uri = Uri.parse(url);
                //                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                //                mActivity.startActivity(intent);
                //                dialogUpdate.dismiss();
                mActivity.startActivity(new Intent(mActivity, DownloadActivity.class));
            }
        });
        dialogUpdate.setTitleText("版本提示");
        dialogUpdate.setContentText(content);
        dialogUpdate.show();
    }

    /**
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为1,英文字符长度为0.5
     *
     * @param s 需要得到长度的字符串
     * @return int 得到的字符串长度
     */
    public static int getLength(String s) {
        double valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < s.length(); i++) {
            // 获取一个字符
            String temp = s.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chinese)) {
                // 中文字符长度为1
                valueLength += 1;
            } else {
                // 其他字符长度为0.5
                valueLength += 0.5;
            }
        }
        //进位取整
        return (int) Math.ceil(valueLength);
    }

    public static String getImei(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        LogUtil.i("liang", "imei:" + imei);
        return imei;
    }

    public static String getImsi(Context mContext) {
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        String imsi = tm.getSubscriberId();
        LogUtil.i("liang", "imsi:" + imsi);
        return imsi;
    }

    public static String getMac() {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            macSerial = "02:00:00:00:00:00";
        }
        LogUtil.i("liang", "macSerial:" + macSerial);
        return macSerial;
    }

    public static void buildPtr(PullToRefreshBase<? extends View> ptrView) {
        ptrView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载...");
        ptrView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        ptrView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开加载更多...");
        ptrView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        ptrView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        ptrView.getLoadingLayoutProxy(true, false).setReleaseLabel("松开加载更多...");
    }

    public static void buildPtr(PullToRefreshBase<? extends View> ptrView, PullToRefreshBase.Mode mode) {
        ptrView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载...");
        ptrView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
        ptrView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开加载更多...");
        ptrView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新...");
        ptrView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在加载...");
        ptrView.getLoadingLayoutProxy(true, false).setReleaseLabel("松开加载更多...");
        ptrView.setMode(mode);
    }

    public static String SHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值，或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    public static boolean isAppRunning(Context context) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals("com.huapu.huafen") &&
                    info.baseActivity.getPackageName().equals("com.huapu.huafen")) {
                isAppRunning = true;
                //find it, break
                break;
            }
        }
        return isAppRunning;
    }

    public static boolean isExistActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }

    public static ArrayList<String> getOSSStyle(List<String> list, String style) {
        if (ArrayUtil.isEmpty(list) || TextUtils.isEmpty(style)) {
            return null;
        }
        ArrayList<String> images = new ArrayList<String>();
        for (String img : list) {
            if (img.contains("@!logo")) {
                String image = StringUtils.substringBeforeLast(img, "@!logo");
                image = image + style;
                images.add(image);
            } else {
                String image = img + style;
                images.add(image);
            }
        }
        return images;
    }

    public static String getOSSStyle(String image, String style) {
        if (TextUtils.isEmpty(image) || TextUtils.isEmpty(style)) {
            return null;
        }
        String img = "";
        if (image.contains("@!logo")) {
            img = StringUtils.substringBeforeLast(image, "@!logo");
            img = img + MyConstants.OSS_SMALL_STYLE;
        } else {
            img = image + MyConstants.OSS_SMALL_STYLE;
        }
        return img;
    }

    /**
     * 更新系统消息未读数
     */
    public static void updateSystemUnreadCount() {
        MyConstants.UNREAD_SYSTEM_COUNT = 0;
        MessageUnReadCountEvent event = new MessageUnReadCountEvent();
        event.isUpdate = true;
        EventBus.getDefault().post(event);
    }

    /**
     * 更新系统消息未读数
     */
    public static void updateCommentUnreadCount() {
        MyConstants.UNREAD_COMMENT_MSG_COUNT = 0;
        MessageUnReadCountEvent event = new MessageUnReadCountEvent();
        event.isUpdate = true;
        EventBus.getDefault().post(event);
    }

    public static void editGoodsInfo(Context mContext, GoodsInfo goodsInfo, String from) {
        if (goodsInfo == null) {
            return;
        }
        if ((goodsInfo.getPrivileges() & MyConstants.P_EDIT) == 0) {
            final TextDialog dialog = new TextDialog(mContext, false);
            dialog.setContentText("商品已参与了活动，不可编辑哦~");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        // 编辑
        Intent intent = new Intent(mContext, ReleaseActivity.class);
        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsInfo.getGoodsId()));
        intent.putExtra(MyConstants.EXTRA_GOODS_EDIT_FROM, from);
        if (goodsInfo.getCampaignId() != 0) {
            ArrayList<Campaign> campaigns = CommonPreference.getCampaigns();
            if (!ArrayUtil.isEmpty(campaigns)) {
                for (Campaign campaign : campaigns) {
                    if (campaign.getCid() == goodsInfo.getCampaignId()){
                        intent.putExtra(MyConstants.EXTRA_CAMPAIGN_BEAN, campaign);
                        break;
                    }
                }
            }
        }
        mContext.startActivity(intent);
    }

    public static void editGoodsData(Activity mContext, GoodsData goodsData, int requestCode) {
        if (goodsData == null) {
            return;
        }
        if ((goodsData.getPrivileges() & MyConstants.P_EDIT) == 0) {
            final TextDialog dialog = new TextDialog(mContext, false);
            dialog.setContentText("商品已参与了活动，不可编辑哦~");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return;
        }
        // 编辑
        Intent intent = new Intent(mContext, ReleaseActivity.class);
        intent.putExtra(MyConstants.EXTRA_GOODS_DETAIL_ID, String.valueOf(goodsData.getGoodsId()));
        if (goodsData.getCampaignId() != 0) {
            ArrayList<Campaign> campaigns = CommonPreference.getCampaigns();
            if (!ArrayUtil.isEmpty(campaigns)) {
                for (Campaign campaign : campaigns) {
                    if (campaign.getCid() == goodsData.getCampaignId()){
                        intent.putExtra(MyConstants.EXTRA_CAMPAIGN_BEAN, campaign);
                        break;
                    }
                }
            }
        }
        mContext.startActivityForResult(intent, requestCode);
    }

    public static String getIntegerCount(int count, int integer) {
        String strCount = "";
        if (count > integer) {
            double douCount = Double.valueOf(count);
            douCount = douCount / 10000.00;
            int result = (int) Math.rint(douCount);
            strCount = result + "万";
        } else {
            strCount = String.valueOf(count);
        }
        return strCount;
    }

    public static String getDoubleCount(int count, int integer) {
        String strCount = "";
        if (count > integer) {
            double douCount = Double.valueOf(count);
            douCount = count / 10000.00;
            BigDecimal bd = new BigDecimal(douCount).setScale(0, BigDecimal.ROUND_HALF_UP);
            strCount = bd + "万";
        } else {
            strCount = String.valueOf(count);
        }
        return strCount;
    }

    public static void buildMontage(Context context, String key) {
        if (CommonPreference.getBooleanValue(key, false)) {
            Intent intent = new Intent(context, MontageActivity.class);
            intent.putExtra(MyConstants.EXTRA_MONTAGE, key);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            } else {
                throw new RuntimeException("context must be a Activity");
            }
            CommonPreference.setBooleanValue(key, false);
        }
    }

    public static void buildMontage(Context context, Intent intent, String key) {
        if (CommonPreference.getBooleanValue(key, false)) {
            intent.setClass(context, MontageActivity.class);
            intent.putExtra(MyConstants.EXTRA_MONTAGE, key);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            } else {
                throw new RuntimeException("context must be a Activity");
            }
            CommonPreference.setBooleanValue(key, false);
        }
    }

    public static void setAutoLoop(BannerData data, ClassBannerView bannerView) {
        if (data == null || bannerView == null) {
            return;

        }
        boolean autoLoop = data.getAutoLoop();
        ArrayList<CampaignBanner> banners = data.getBanners();

        if (autoLoop) {
            bannerView.setCanLoop(true);
            bannerView.startTurning(3000);
        } else {
            if (!ArrayUtil.isEmpty(banners)) {
                if (banners.size() > 1) {
                    bannerView.setCanLoop(true);
                } else {
                    bannerView.setCanLoop(false);
                }
            }
        }
    }

    public static void saveHistoryBrand(Brand brand) {
        ArrayList<Brand> brandsHistory = CommonPreference.getHistoryBrands();
        if (!ArrayUtil.isEmpty(brandsHistory)) {
            if (!brandsHistory.contains(brand)) {
                brandsHistory.add(0, brand);
            }
        } else {
            brandsHistory = new ArrayList<>();
            brandsHistory.add(brand);
        }

        ArrayList<Brand> var = new ArrayList<>();
        if (!ArrayUtil.isEmpty(brandsHistory) && brandsHistory.size() > 10) {
            List<Brand> tmp = brandsHistory.subList(0, 9);
            for (Brand b : tmp) {
                var.add(b);
            }
        } else {
            var = brandsHistory;
        }
        CommonPreference.setHistoryBrand(var);
    }

    public static void setAutoLoop(BannerData data, ConvenientBanner bannerView) {
        if (data == null || bannerView == null) {
            return;

        }
        boolean autoLoop = data.getAutoLoop();
        ArrayList<CampaignBanner> banners = data.getBanners();

        if (autoLoop) {
            bannerView.setCanLoop(true);
            bannerView.startTurning(3000);
        } else {
            if (!ArrayUtil.isEmpty(banners)) {
                if (banners.size() > 1) {
                    bannerView.setCanLoop(true);
                } else {
                    bannerView.setCanLoop(false);
                }
            }

        }
    }

    public static void stopLoop(ConvenientBanner banner) {
        if (banner != null) {
            banner.stopTurning();
        }
    }

    public static void stopLoop(ClassBannerView banner) {
        if (banner != null) {
            banner.stopTurning();
        }
    }

    /***
     * 判断Network具体类型（联通移动wap，电信wap，其他net）
     *
     * */
    public static int getNetState(Context mContext) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager
                    .getActiveNetworkInfo();
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
                // 注意一：
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
                // 但是有些电信机器，仍可以正常联网，
                // 所以当成net网络处理依然尝试连接网络。
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。
                return TYPE_NET_WORK_DISABLED;
            } else {
                // NetworkInfo不为null开始判断是网络类型
                int netType = mobNetInfoActivity.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    // wifi net处理
                    return TYPE_WIFI;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    // 注意二：
                    // 判断是否电信wap:
                    // 不要通过getExtraInfo获取接入点名称来判断类型，
                    // 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
                    // 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
                    // 所以可以通过这个进行判断！

                    boolean is3G = isFastMobileNetwork(mContext);

                    final Cursor c = mContext.getContentResolver().query(
                            PREFERRED_APN_URI, null, null, null, null);
                    if (c != null) {
                        c.moveToFirst();
                        final String user = c.getString(c
                                .getColumnIndex("user"));
                        if (!TextUtils.isEmpty(user)) {
                            if (user.startsWith(CTWAP)) {
                                return is3G ? TYPE_CT_WAP : TYPE_CT_WAP_2G;
                            } else if (user.startsWith(CTNET)) {
                                return is3G ? TYPE_CT_NET : TYPE_CT_NET_2G;
                            }
                        }
                    }
                    c.close();

                    // 注意三：
                    // 判断是移动联通wap:
                    // 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
                    // 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
                    // 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
                    // 所以采用getExtraInfo获取接入点名字进行判断

                    String netMode = mobNetInfoActivity.getExtraInfo();
                    if (netMode != null) {
                        // 通过apn名称判断是否是联通和移动wap
                        netMode = netMode.toLowerCase();

                        if (netMode.equals(CMWAP)) {
                            return is3G ? TYPE_CM_WAP : TYPE_CM_WAP_2G;
                        } else if (netMode.equals(CMNET)) {
                            return is3G ? TYPE_CM_NET : TYPE_CM_NET_2G;
                        } else if (netMode.equals(NET_3G)
                                || netMode.equals(UNINET)) {
                            return is3G ? TYPE_CU_NET : TYPE_CU_NET_2G;
                        } else if (netMode.equals(WAP_3G)
                                || netMode.equals(UNIWAP)) {
                            return is3G ? TYPE_CU_WAP : TYPE_CU_WAP_2G;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return TYPE_OTHER;
        }

        return TYPE_OTHER;

    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;

        }
    }

    public static ArrayList<VBanner> covertOriginalBanners(ArrayList<String> imgs) {

        ArrayList<VBanner> banners = new ArrayList<VBanner>();
        ArrayList<String> images = CommonUtils.getOSSStyle(imgs, MyConstants.OSS_ORIGINAL_STYLE);


        for (String banner : images) {
            VBanner vBanner = new VBanner(2, banner);
            banners.add(vBanner);
        }

        for (int i = 0; i < banners.size(); i++) {
            VBanner banner = banners.get(i);
            banner.position = i;
        }

        return banners;
    }

    public static boolean isQRCode(Bitmap obmp) {
        if (obmp == null) {
            return false;
        }
        //		Bitmap obmp = ((BitmapDrawable) (bigImage).getDrawable()).getBitmap();
        int width = obmp.getWidth();
        int height = obmp.getHeight();
        int[] data = new int[width * height];
        obmp.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result re = null;
        try {
            re = reader.decode(bitmap1);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        if (re == null) {
            //			showAlert(obmp);
            return false;
        } else {
            //			showSelectAlert(obmp, re.getText());
            return true;
        }
    }

    public static Bitmap createVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        return bitmap;
    }

    public static void setPriceSizeData(TextView tvIsFreeDelivery, String str, int postage) {
        float textSize = tvIsFreeDelivery.getTextSize();
        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(str + "¥" + postage);
        //style 为0 即是正常的，还有Typeface.BOLD(粗体) Typeface.ITALIC(斜体)等
        //size  为0 即采用原始的正常的 size大小
        int index;
        if (TextUtils.isEmpty(str)) {
            index = 0;
        } else {
            index = str.length();
        }
        spanBuilder.setSpan(new TextAppearanceSpan(null, 0, (int) textSize * 4 / 5, null, null), index, (index + 1), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvIsFreeDelivery.setText(spanBuilder);
    }

    /**
     * 统计发生次数
     * 示例：统计微博应用中"转发"事件发生的次数，那么在转发的函数里调用:
     *
     * @param context
     * @param eventId
     */
    public static void computeCount(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
    }

    /**
     * 统计点击行为各属性被触发的次数
     * 示例：统计电商应用中“购买”事件发生的次数，以及购买的商品类型及数量，那么在购买的函数里调用：
     *
     * @param context
     * @param eventId
     * @param map
     */
    public static void computeCountProperty(Context context, String eventId, HashMap<String, String> map) {
        MobclickAgent.onEvent(context, eventId, map);
    }

    /**
     * 统计数值型变量的值的分布
     * 示例：统计一次音乐播放，包括音乐类型，作者和播放时长，可以在音乐播放结束后这么调用：
     *
     * @param context
     * @param id
     * @param m
     * @param du
     */
    public static void computeChange(Context context, String id, HashMap<String, String> m, int du) {
        MobclickAgent.onEventValue(context, id, m, du);
    }

    public static boolean checkCommentCommit(int goodsState, int auditStatus) {
        switch (goodsState) {
            case 1:
                switch (auditStatus) {
                    case 1:
                        return true;
                    case 2:
                        return true;
                    case 3:
                        return false;
                    case 4:
                        return false;
                    case 5:
                        return false;
                }
                break;

            case 2:
                switch (auditStatus) {
                    case 1:
                        return true;
                    case 2:
                        return true;
                    case 3:
                        return false;
                    case 4:
                        return false;
                    case 5:
                        return false;
                }
                break;

            case 3:
                return false;
            case 4:
                return false;

            case 5:
                return false;

            default:
                return false;
        }
        return false;
    }

    public static void commentClickFailed(Context mContext) {
        ToastUtil.toast(mContext, "商品已出售或还未上架时不能留言");
    }

    public static String getUUID() {
        String uuidStr = UUID.randomUUID().toString();
        //		uuidStr = uuidStr.substring(0, 8) + uuidStr.substring(9, 13)
        //				+ uuidStr.substring(14, 18) + uuidStr.substring(19, 23)
        //				+ uuidStr.substring(24);
        uuidStr = uuidStr.replaceAll("-", "");
        return uuidStr;
    }

    /**
     * 获取 hmacSha1
     *
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static String hmacSha1(String base, String key) {
        if (TextUtils.isEmpty(base) || TextUtils.isEmpty(key)) {
            return "";
        }
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac;
        try {
            mac = Mac.getInstance(type);
            mac.init(secret);
            byte[] digest = mac.doFinal(base.getBytes());
            return Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (InvalidKeyException e) {
            return "";
        }
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }

    private static void authForKvsd(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.Dialog_Fullscreen);
        dialog.setContentView(R.layout.data_error_layout);
        dialog.setCancelable(false);
        dialog.show();
        if (!CommonUtils.isNetAvaliable(context)) {
            dialog.dismiss();
            return;
        }

        OkHttpClientManager.StringCallback callBack = new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                dialog.dismiss();
                LogUtil.e("laloError", "init:" + e.toString());
            }

            @Override
            public void onResponse(final String response) {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dialog.dismiss();
                        LogUtil.e("laloResponse", "init:" + response.toString());
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
                                    Intent intent0 = new Intent(context, MainActivity.class);
                                    Intent intent1 = new Intent(context, LoginActivity.class);
                                    Intent[] intents = new Intent[]{intent0, intent1};
                                    context.startActivities(intents);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e("lalo", "init crash:" + e.getMessage());
                        }
                    }
                }, 2000);
            }
        };

        RequestManagerSys.startRequestForKvsd(null, callBack);
    }

    public static void getBrandListDataFromServer(final Activity context, final OnBrandListDataLoaded onBrandListDataLoaded) {

        HashMap<String, String> params = new HashMap<>();
        params.put("cacheVersion", CommonPreference.getBrandCacheVersion());
        LogUtil.e("getBrandListDataFromServer", "params:" + params.toString());
        OkHttpClientManager.postAsyn(MyConstants.GET_BRANDS, params, new OkHttpClientManager.StringCallback() {

            @Override
            public void onError(Request request, Exception e) {
                LogUtil.e("getBrandListDataFromServer", "init:" + e.toString());
            }

            @Override
            public void onResponse(String response) {
                LogUtil.e("getBrandListDataFromServer", "onResponse:" + response.toString());
                try {
                    BrandsResult result = JSON.parseObject(response, BrandsResult.class);
                    if (result == null)
                        return;
                    if (result.code == ParserUtils.RESPONSE_SUCCESS_CODE) {
                        final ArrayList<Brand> brands = result.obj.brandList;
                        final String brandCacheVersion = result.obj.cacheVersion;
                        if (!ArrayUtil.isEmpty(brands)) {
                            for (Brand b : brands) {
                                SortToken sortToken = PinYinUtils.parseSortKey(b.brandName);
                                b.sortToken = sortToken;
                            }
                            CommonPreference.setBrands(brands);
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    ArrayList<Pair<String, ArrayList<Brand>>> amazingListData = genAmazingListData(brands);
                                    CommonPreference.setBrandGroups(amazingListData);
                                    ArrayList<Pair<String, ArrayList<Brand>>> logBrandGroups = CommonPreference.getBrandGroups();
                                    LogUtil.e("getBrandListDataFromServer", "brand size" + brands.size() + ",logBrandGroups size" + logBrandGroups.size());
                                    if (onBrandListDataLoaded != null) {
                                        onBrandListDataLoaded.onLoad(amazingListData);
                                    }
                                    if (!TextUtils.isEmpty(brandCacheVersion)) {
                                        CommonPreference.setBrandCacheVersion(brandCacheVersion);
                                    }
                                }
                            }).start();

                        }


                    } else {
                        if (context != null) {
                            CommonUtils.error(result, context, "");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "onResponse crash:" + e.getMessage());
                }
            }
        });
    }

    public static void getBrandListDataFromServer() {
        getBrandListDataFromServer(null, null);
    }

    private static ArrayList<Pair<String, ArrayList<Brand>>> genAmazingListData(List<Brand> brands) {
        if (ArrayUtil.isEmpty(brands)) {
            return null;
        }
        Comparator<Brand> comparator = new Comparator<Brand>() {
            @Override
            public int compare(Brand lhs, Brand rhs) {
                return PinYinUtils.compareName(lhs.brandName, rhs.brandName);
            }
        };
        ArrayList<Pair<String, ArrayList<Brand>>> pairList = new ArrayList<>();
        Collections.sort(brands, comparator);
        List<String> letters = new ArrayList<>();
        for (Brand b : brands) {
            String pinyin = PinYinUtils.getPinYin(b.brandName);
            if (!TextUtils.isEmpty(pinyin)) {
                String letter = pinyin.substring(0, 1).toUpperCase();
                if (!PinYinUtils.isLetter(letter)) {
                    letter = "#";
                }

                if (!letters.contains(letter)) {
                    ArrayList<Brand> list = new ArrayList<>();
                    list.add(b);
                    Pair<String, ArrayList<Brand>> pair = new Pair<>(letter, list);
                    pairList.add(pair);
                    letters.add(letter);
                } else {
                    for (Pair<String, ArrayList<Brand>> pair : pairList) {
                        if (pair.first.equals(letter)) {
                            pair.second.add(b);
                        }
                    }
                }
            }
        }

        return pairList;
    }

    public static ArrayList<Commodity> searchCommod(final String str, List<Commodity> commodities) {
        ArrayList<Commodity> filterList = new ArrayList<>();// 过滤后的list
        for (Commodity commodity : commodities) {
            if (commodity.getGoodsData().getName() != null) {
                if (commodity.getGoodsData().getName().contains(str)) {
                    if (!filterList.contains(commodity)) {
                        filterList.add(commodity);
                    }
                }
            }
        }
        return filterList;
    }


    public static List<FilterAreaData> covertRegions() {
        ArrayList<Region> regions = CommonPreference.getRegions();
        if (regions == null) {
            regions = new ArrayList<>();
        }
        Region region = new Region();
        region.setDid(0);
        region.setName("全国");

        for (Region r : regions) {
            if (r.getDc() == 1) {//
                ArrayList<District> dists = r.getDistricts();
                District district = new District();
                district.setDid(0);
                String ss = "全%s";
                String name = String.format(ss, r.getName());
                district.setName(name);
                dists.add(0, district);
            } else {
                ArrayList<City> cities = r.getCities();
                if (cities == null) {
                    cities = new ArrayList<City>();
                }
                City city = new City();
                city.setDid(0);
                String ss = "全%s";
                String name = String.format(ss, r.getName());
                city.setName(name);
                cities.add(0, city);
                for (City c : cities) {
                    ArrayList<District> dists = c.getDistricts();
                    if (dists != null) {
                        District district = new District();
                        district.setDid(0);
                        String res = "全%s";
                        String dName = String.format(res, c.getName());
                        district.setName(dName);
                        dists.add(0, district);
                    }

                }
            }
        }

        regions.add(0, region);

        String json = JSON.toJSONString(regions);
        List<FilterAreaData> list = JSON.parseArray(json, FilterAreaData.class);
//        FilterAreaData filterAreaData = findLocationCityByFilterAreaData(list);
//        if (filterAreaData != null) {
//            list.add(0, filterAreaData);
//        }

        return list;
    }

    public static List<FilterAreaData> covertRegionsWithoutQuan() {
        ArrayList<Region> regions = CommonPreference.getRegions();
        String json = JSON.toJSONString(regions);
        List<FilterAreaData> list = JSON.parseArray(json, FilterAreaData.class);
        return list;
    }


    public static FilterAreaData findLocationCityByFilterAreaData(List<FilterAreaData> list) {
        LocationData locationData = CommonPreference.getLocalData();
        FilterAreaData locationCity = null;
        if (locationData != null) {
            String locCity = locationData.city;
            for (FilterAreaData r : list) {
                if (r.getDc() == 1) {
                    if (r.getName().equals(locCity)) {
                        locationCity = new FilterAreaData();
                        locationCity.setDistricts(r.getDistricts());
                        locationCity.setDid(r.getDid());
                        locationCity.setName(r.getName());
                        locationCity.setDc(r.getDc());
                        locationCity.isShowLocationIcon = true;
                        break;
                    }
                } else {
                    ArrayList<FilterAreaData> cities = r.getCities();
                    if (cities != null) {
                        for (FilterAreaData c : cities) {
                            if (c.getName().equals(locCity)) {
                                locationCity = new FilterAreaData();
                                locationCity.setCities(c.getDistricts());
                                locationCity.setDid(c.getDid());
                                locationCity.setName(c.getName());
                                locationCity.setDc(c.getDc());
                                locationCity.isLocationCity = true;
                                locationCity.provinceDid = r.getDid();
                                locationCity.isShowLocationIcon = true;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return locationCity;
    }

    public static ArrayList<Cat> getSecondCats(long bigId) {
        ArrayList<Cat> cats = CommonPreference.getCats();
        ArrayList<Cat> list = null;
        Cat cat = null;
        if (!ArrayUtil.isEmpty(cats)) {
            for (int i = 0; i < cats.size(); i++) {
                if (bigId == cats.get(i).getCid()) {
                    cat = cats.get(i);
                    break;
                }
            }
        }

        if (cat != null && !ArrayUtil.isEmpty(cat.getCats())) {
            list = cat.getCats();
        }
        return list;
    }

    public static ArrayList<Cat> getSecondCats(String condition) {

        ArrayList<Cat> cats = CommonPreference.getCats();
        String[] cids = condition.split(",");
        ArrayList<Cat> tcats = new ArrayList<Cat>();

        if (!ArrayUtil.isEmpty(cats)) {
            for (int i = 0; i < cats.size(); i++) {
                Cat cat = cats.get(i);
                ArrayList<Cat> seconds = cat.getCats();
                for (int j = 0; seconds != null && j < seconds.size(); j++) {
                    Cat scat = seconds.get(j);
                    for (int k = 0; cids != null && k < cids.length; k++) {
                        if ((scat.getCid() + "").equals(cids[k])) {
                            tcats.add(scat);
                        }
                    }
                }
            }
        }
        if (tcats.size() > 0) {
            Cat zero = new Cat();
            zero.setName("全部");
            zero.setCid(0);
            tcats.add(0, zero);
        }

        return tcats;
    }

    public static List<Cat> getSecondCats(long bigId, long smallId) {
        ArrayList<Cat> cats = CommonPreference.getCats();
        ArrayList<Cat> list = null;
        Cat cat = null;
        if (!ArrayUtil.isEmpty(cats)) {
            for (int i = 0; i < cats.size(); i++) {
                if (bigId == cats.get(i).getCid()) {
                    cat = cats.get(i);
                    break;
                }
            }
        }

        if (cat != null && !ArrayUtil.isEmpty(cat.getCats())) {
            list = cat.getCats();
            for (int i = 0; i < list.size(); i++) {
                Cat c = list.get(i);
                if (c.getCid() == smallId) {
                    c.isCheck = true;
                    break;
                }
            }
        }
        return list;
    }

    public static ArrayList<Cat> getCats() {
        ArrayList<Cat> cats = CommonPreference.getCats();
        if (cats != null) {
            Cat cat = new Cat();
            cat.setName("全部分类");
            cat.setCid(0);
            cats.add(0, cat);
        }
        return cats;
    }

    public static ArrayList<Cat> getCats(String condition) {

        ArrayList<Cat> cats = CommonPreference.getCats();
        if (TextUtils.isEmpty(condition)) {
            if (cats != null) {
                Cat cat = new Cat();
                cat.setName("全部分类");
                cat.setCid(0);
                cats.add(0, cat);
            }
            return cats;
        }
        String[] cids = condition.split(",");
        ArrayList<Cat> tcats = new ArrayList<Cat>();

        if (!ArrayUtil.isEmpty(cats)) {
            Cat zero = new Cat();
            zero.setName("全部");
            zero.setCid(0);
            tcats.add(0, zero);
            for (int i = 0; i < cats.size(); i++) {
                Cat cat = cats.get(i);
                for (int j = 0; cids != null && j < cids.length; j++) {
                    if ((cat.getCid() + "").equals(cids[j])) {
                        tcats.add(cat);
                    }
                }
            }
        }

        return tcats;
    }

    public static void saveImage(View view, Context context, String fileName) {
        view.setDrawingCacheEnabled(true);
        try {
            String sdCardDir = context.getApplicationContext().getCacheDir().toString();
            File file = new File(sdCardDir, fileName);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            view.getDrawingCache().compress(Bitmap.CompressFormat.PNG, 80, bos);
            bos.flush();
            bos.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), "temp.png", "description");
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.getApplicationContext().sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(false);
    }

    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getScreenLocationY(float ratio, int pixelHeight, int y, int measureWidth) {
        int screenWidth = measureWidth;
        int h = (int) (screenWidth / ratio);
        return h * y / pixelHeight;
    }

    public static int getScreenLocationX(int pixelWidth, int x, int measureWidth) {
        int screenWidth = measureWidth;
        return x * screenWidth / pixelWidth;
    }

    public static int getPixelY(float ratio, int screenLocationY, int pixelHeight, int measureWidth) {
        float h = measureWidth / ratio;
        return (int) (screenLocationY * pixelHeight / h);
    }

    public static int getPixelX(int screenLocationX, int pixelWidth, int measureWidth) {
        return screenLocationX * pixelWidth / measureWidth;
    }

    public static int[] measureBitmap(String localPath) {
        int[] arr = new int[2];
        if (!TextUtils.isEmpty(localPath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(localPath, options);
            arr[0] = options.outWidth;
            arr[1] = options.outHeight;
            options.inJustDecodeBounds = false;
        }
        return arr;
    }

    /**
     * 读取照片exif信息中的旋转角度
     *
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap toTurn(Bitmap img) {
        Matrix matrix = new Matrix();
        matrix.postRotate(+90); /*翻转90度*/
        int width = img.getWidth();
        int height = img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    public static Bitmap shotActivity(Activity ctx) {
        View view = ctx.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bmpCache = view.getDrawingCache();
        return bmpCache;
    }

    public static boolean parseEvent(final Context context, String response, final OnRequestRetryListener listener) {
        boolean flag = false;
        try {
            BaseResultNew result = JSON.parseObject(response, BaseResultNew.class);
            if (!ArrayUtil.isEmpty(result.events)) {
                List<BaseResultNew.Event> events = result.events;
                for (BaseResultNew.Event event : events) {
                    if (BaseResultNew.SHOW_DIALOG.equals(event.name)) {

                        if (!ArrayUtil.isEmpty(event.opts.buttons)) {
                            final TextDialog dialog = new TextDialog(context, false);
                            dialog.setContentText(event.opts.content);
                            if (event.opts.buttons.size() == 1) {
                                final BaseResultNew.Event.Option.Button buttonLeft = event.opts.buttons.get(0);
                                dialog.setLeftText(buttonLeft.title);
                                dialog.setLeftCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        dialog.dismiss();
                                        if ("redo".equals(buttonLeft.action)) {
                                            if (listener != null) {
                                                listener.onRetry();
                                            }
                                        } else {
                                            ActionUtil.dispatchAction(context, buttonLeft.action, buttonLeft.target);
                                        }
                                    }
                                });

                            } else if (event.opts.buttons.size() == 2) {
                                final BaseResultNew.Event.Option.Button buttonLeft = event.opts.buttons.get(0);
                                final BaseResultNew.Event.Option.Button buttonRight = event.opts.buttons.get(1);
                                dialog.setLeftText(buttonLeft.title);
                                dialog.setLeftCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        dialog.dismiss();
                                        if ("redo".equals(buttonLeft.action)) {
                                            if (listener != null) {
                                                listener.onRetry();
                                            }
                                        } else {
                                            ActionUtil.dispatchAction(context, buttonLeft.action, buttonLeft.target);
                                        }
                                    }
                                });
                                dialog.setRightText(buttonRight.title);
                                dialog.setRightCall(new DialogCallback() {

                                    @Override
                                    public void Click() {
                                        dialog.dismiss();
                                        if ("redo".equals(buttonRight.action)) {
                                            if (listener != null) {
                                                listener.onRetry();
                                            }
                                        } else {
                                            ActionUtil.dispatchAction(context, buttonRight.action, buttonRight.target);
                                        }
                                    }
                                });
                            }
                            dialog.show();
                        }

                        flag = true;
                    } else if (BaseResultNew.UPDATE_USER_PROPERTY.equals(event.name)) {
                        CommonPreference.setGrantedCampaigns(event.opts.grantedCampaigns);
                        CommonPreference.setGrantedOneYun(event.opts.grantedOneYuan);
                    }
                }
            }
            return flag;
        } catch (Exception e) {
            return false;
        }
    }

    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }

}