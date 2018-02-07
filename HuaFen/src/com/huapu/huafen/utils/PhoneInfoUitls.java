package com.huapu.huafen.utils;

import android.app.Activity;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import com.huapu.huafen.MyApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * @author liang_xs
 * @ClassName: PhoneInfoUitl
 * @Description: 获取设备信息
 * @date 2014-7-7
 */
public class PhoneInfoUitls {
    public static String TAG = "PhoneInfoUitl";

    /**
     * @return Map<String,String>
     * @Title: getPhoneInfo
     * @Description: 获取手机信息
     * @author liang_xs
     */
    public static Map<String, String> getPhoneInfo(Context mContext, Activity mActivity) {
        Map<String, String> mapInfos = new HashMap<String, String>();
        TelephonyManager tm = (TelephonyManager) mContext
                .getSystemService(mContext.TELEPHONY_SERVICE);
        String strmobleBrand = android.os.Build.BRAND;// 手机品牌
        String strmobleType = android.os.Build.MODEL; // 手机型号
        String strImei = tm.getDeviceId();
        String strImsi = tm.getSubscriberId();
        String strProvidersName = null;
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (!TextUtils.isEmpty(strImsi)) {
            if (strImsi.startsWith("46000") || strImsi.startsWith("46002")) {
                strProvidersName = "中国移动";
            } else if (strImsi.startsWith("46001")) {
                strProvidersName = "中国联通";
            } else if (strImsi.startsWith("46003")) {
                strProvidersName = "中国电信";
            }
        }
        String strMobleNumber = tm.getLine1Number(); // 手机号码
        String strExternalMemory = formatSize(getAvailableExternalMemorySize());
        String strInternalMemory = formatSize(getAvailableInternalMemorySize());
//		String strRootMemory = formatSize(getAvailableRootMemorySize());
        String strCpu = getCpuInfo();
        int intCoresCount = getCoresCount();
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;// 宽度
        int height = dm.heightPixels;//高度
        String strRom = getRomversion();
        String strVersionName = CommonUtils.getAppVersionName();
//		String str = "品牌: " + strmobleBrand + "\n" + "型号: " + strmobleType + "\n"
//				+ "版本: Android " + android.os.Build.VERSION.RELEASE + "\n"
//				+ "IMEI: " + strImei + "\n" + "IMSI: " + strImsi + "\n" + "手机号码: "
//				+ strMobleNumber + "\n" + "运营商: " + strProvidersName + "\n" + "SD卡可用空间: " + strExternalMemory + "\n"
//				+ "手机可用空间: " + strInternalMemory + "\n" /*+ "手机分辨率: " + wh + "\n"*/
//				+ "CPU: " + strCpu + "\n" + "内核: " + intCoresCount;
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("品牌", strmobleBrand);
//			obj.put("型号", strmobleType);
//			obj.put("Android版本", android.os.Build.VERSION.RELEASE);
//			obj.put("IMEI", strImei);
//			obj.put("IMSI", strImsi);
//			obj.put("手机号码", strMobleNumber);
//			obj.put("运营商", strProvidersName);
//			obj.put("SD卡可用空间", strExternalMemory);
//			obj.put("手机可用空间", strInternalMemory);
//			obj.put("CPU型号", strCpu);
//			obj.put("屏幕尺寸", width+"*"+height);
//			obj.put("时间", System.currentTimeMillis());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
        mapInfos.put("手机ROM", strRom);
        mapInfos.put("品牌", strmobleBrand);
        mapInfos.put("型号", strmobleType);
        mapInfos.put("版本 Android", android.os.Build.VERSION.RELEASE);
        mapInfos.put("手机IMEI", strImei);
        mapInfos.put("手机IMSI", strImsi);
        mapInfos.put("手机号码", strMobleNumber);
        mapInfos.put("运营商", strProvidersName);
        mapInfos.put("手机SD卡可用空间", strExternalMemory);
        mapInfos.put("手机可用空间", strInternalMemory);
        mapInfos.put("手机CPU型号", strCpu);
        mapInfos.put("屏幕尺寸", width + "*" + height);
        mapInfos.put("时间", getDate());
        mapInfos.put("apk版本名称", strVersionName);
        return mapInfos;
    }

    public static TimeZone getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        String s = "TimeZone   " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID();
        return tz;
    }

    /**
     * 获取Rom版本
     */
    public static String getRomversion() {
        String rom = "";
        try {
            String modversion = getSystemProperty("ro.modversion");
            String displayId = getSystemProperty("ro.build.display.id");
            if (modversion != null && !modversion.equals("")) {
                rom = modversion;
            }
            if (displayId != null && !displayId.equals("")) {
                rom = displayId;
            }
        } catch (Exception e) {
        }
        return rom;
    }

    /**
     * 获取系统配置参数
     */
    public static String getSystemProperty(String key) {
        String pValue = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method m = c.getMethod("get", String.class);
            pValue = m.invoke(null, key).toString();
        } catch (Exception e) {
//			LogUtils.e(e);
        }
        return pValue;
    }

    /**
     * @return long
     * @Title: getAvailableExternalMemorySize
     * @Description: 获取sd卡存储空间大小
     * @author liang_xs
     */
    public static long getAvailableExternalMemorySize() {
        String strState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(strState)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long availableBlocks = sf.getAvailableBlocks();
            long unUsed = availableBlocks * blockSize;
            return unUsed;
        }
        return -1;
    }

    /**
     * @return long
     * @Title: getAvailableInternalMemorySize
     * @Description: 获取手机内部存储空间大小
     * @author liang_xs
     */
    public static long getAvailableInternalMemorySize() {
        File filePath = Environment.getDataDirectory();
        StatFs stat = new StatFs(filePath.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long unUsed = availableBlocks * blockSize;
        return unUsed;
    }

    /**
     * @return long
     * @Title: getAvailableInternalMemorySize
     * @Description: 获取手机内部存储空间大小
     * @author liang_xs
     */
    public static long getAvailableRootMemorySize() {
        File filePath = Environment.getRootDirectory();
        StatFs stat = new StatFs(filePath.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        long unUsed = availableBlocks * blockSize;
        return unUsed;
    }

    /**
     * @return String
     * @Title: formatSize
     * @Description: 转换大小
     * @author liang_xs
     */
    public static String formatSize(long size) {
        String strSuffix = null;
        float fSize = 0;

        if (size >= 1024) {
            strSuffix = "KB";
            fSize = size / 1024;
            if (fSize >= 1024) {
                strSuffix = "MB";
                fSize /= 1024;
            }
            if (fSize >= 1024) {
                strSuffix = "GB";
                fSize /= 1024;
            }
        } else {
            fSize = size;
        }
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
        StringBuilder resultBuffer = new StringBuilder(df.format(fSize));
        if (strSuffix != null)
            resultBuffer.append(strSuffix);
        return resultBuffer.toString();
    }

    /**
     * @return String
     * @Title: getCpuInfo
     * @Description: 获得CPU信息
     * @author liang_xs
     */
    public static String getCpuInfo() {
        String strPath = "/proc/cpuinfo";
        String strRead = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(strPath);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            strRead = localBufferedReader.readLine();
            arrayOfString = strRead.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            strRead = localBufferedReader.readLine();
            arrayOfString = strRead.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        String strResult = cpuInfo[0];
//		String strResult = "CPU型号 " + cpuInfo[0] + "\n" + "CPU频率: " + cpuInfo[1];
        return strResult;
    }

    /**
     * @return int
     * @Title: getCoresCount
     * @Description: 获得CPU核数
     * @author liang_xs
     */
    public static int getCoresCount() {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                // Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            // Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            // Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            // Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            // Default to return 1 core
            return 1;
        }
    }

    /**
     * @return String
     * @Title: getVersionName
     * @Description: 获取版本号
     * @author liang_xs
     */
    public static String getVersionName(Context mContext) {
        String versionName = null;
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    mContext.getPackageName(), 0);
            versionName = packInfo.versionName;
//			versionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * @return String
     * @Title: getVersionName
     * @Description: 获取版本号
     * @author liang_xs
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            // 获取packagemanager的实例
            PackageManager packageManager = mContext.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    mContext.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * @return String
     * @Title: getDate
     * @Description: 获取系统时间
     * @author liang_xs
     */
    public static String getDate() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = formatter.format(new Date());
        return time;
    }

    /**
     * @return boolean
     * @Title: isNetworkConnected
     * @Description: 判断是否有网络连接
     * @author liang_xs
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

//	/**
//	 * @Title: existSDCard
//	 * @Description: 判断sd卡是否存在
//	 * @return boolean
//	 * @author liang_xs
//	 */
//	public static boolean existSDCard() {
//		if (android.os.Environment.getExternalStorageState().equals(
//				android.os.Environment.MEDIA_MOUNTED)) {
//			return true;
//		} else
//			return false;
//	}

    public static String getNetworkType() {
        String strNetworkType = "";

        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                strNetworkType = "WIFI";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        strNetworkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                        strNetworkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        strNetworkType = "4G";
                        break;
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            strNetworkType = "3G";
                        } else {
                            strNetworkType = _strSubTypeName;
                        }

                        break;
                }

            }
        }
        return strNetworkType;
    }

    private String getAvailMemory(Activity mActivity) {
        // 获取android当前可用内存大小
        android.app.ActivityManager am = (android.app.ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //当前系统的可用内存
        long availMem = mi.availMem;
        return Formatter.formatFileSize(mActivity.getBaseContext(), mi.availMem);// 将获取的内存大小规格化        }
    }


}
