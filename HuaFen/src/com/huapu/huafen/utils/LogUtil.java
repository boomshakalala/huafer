package com.huapu.huafen.utils;

import static android.util.Log.DEBUG;
import static android.util.Log.ERROR;
import static android.util.Log.INFO;
import static android.util.Log.WARN;

import android.os.Debug;

import com.huapu.huafen.common.MyConstants;

/**
 * @author liang_xs
 * @date 2016-03-27
 */
public final class LogUtil {

    public static final String TAG = LogUtil.class.getSimpleName();

    private static boolean isLoggable(int level) {
        return MyConstants.printLog /*|| android.util.Log.isLoggable(TAG, level)*/;
    }

    public static void i(Object... objects) {
        if (isLoggable(INFO)) {
            android.util.Log.i(TAG, toString(objects));
        }
    }

    public static void i(String tag, Object... objects) {
        if (isLoggable(INFO)) {
            android.util.Log.i(tag, toString(objects));
        }
    }

    public static void i(Throwable t, Object... objects) {
        if (isLoggable(INFO)) {
            android.util.Log.i(TAG, toString(objects), t);
        }
    }

    public static void d(Object... objects) {
        if (isLoggable(DEBUG)) {
            android.util.Log.d(TAG, toString(objects));
        }
    }

    public static void d(String tag, Object... objects) {
        if (isLoggable(DEBUG)) {
            android.util.Log.d(tag, toString(objects));
        }
    }

    /**
     * 截断输出日志
     *
     * @param msg
     */
    public static void d(String tag, String msg) {

        if (!isLoggable(DEBUG)) {
            return;
        }

        if (tag == null || tag.length() == 0
                || msg == null || msg.length() == 0)
            return;

        int segmentSize = 3 * 1024;
        long length = msg.length();
        if (length <= segmentSize) {// 长度小于等于限制直接打印
            android.util.Log.d(tag, msg);
        } else {
            while (msg.length() > segmentSize) {// 循环分段打印日志
                String logContent = msg.substring(0, segmentSize);
                msg = msg.replace(logContent, "");
                android.util.Log.d(tag, logContent);
            }
            android.util.Log.d(tag, msg);// 打印剩余日志
        }
    }

    public static void d(Throwable t, Object... objects) {
        if (isLoggable(DEBUG)) {
            android.util.Log.d(TAG, toString(objects), t);
        }
    }

    public static void w(Object... objects) {
        if (isLoggable(WARN)) {
            android.util.Log.w(TAG, toString(objects));
        }
    }

    public static void w(String tag, Object... objects) {
        if (isLoggable(WARN)) {
            android.util.Log.w(tag, toString(objects));
        }
    }

    public static void w(Throwable t, Object... objects) {
        if (isLoggable(WARN)) {
            android.util.Log.w(TAG, toString(objects), t);
        }
    }

    public static void e(Object... objects) {
        if (isLoggable(ERROR)) {
            android.util.Log.e(TAG, toString(objects));
        }
    }

    public static void e(String tag, Object... objects) {
        if (isLoggable(ERROR)) {
            android.util.Log.e(tag, toString(objects));
        }
    }

    public static void e(Throwable t, Object... objects) {
        if (isLoggable(ERROR)) {
            android.util.Log.e(TAG, toString(objects), t);
        }
    }

    /**
     * @throws RuntimeException always throw after logging the error message.
     */
    public static void tRE(Object... objects) {
        if (isLoggable(ERROR)) {
            android.util.Log.e(TAG, toString(objects));
            throw new RuntimeException("Fatal error : " + toString(objects));
        }
    }

    public static void tRE(String tag, Object... objects) {
        if (isLoggable(ERROR)) {
            android.util.Log.e(tag, toString(objects));
            throw new RuntimeException(tag + " : Fatal error : " + toString(objects));
        }
    }

    /**
     * @throws RuntimeException always throw after logging the error message.
     */
    public static void tRE(Throwable t, Object... objects) {
        if (isLoggable(ERROR)) {
            android.util.Log.e(TAG, toString(objects), t);
            throw new RuntimeException("Fatal error : " + toString(objects), t);
        }
    }

    private static String toString(Object... objects) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objects) {
            sb.append(o);
        }
        return sb.toString();
    }

    private static boolean isTraceview = false;

    /**
     * traceview performance test in main thread
     *
     * @param name
     */
    public static void DebugStart(String name) {
        if (isTraceview) {
            Debug.startMethodTracing(name);
        }
    }

    public static void DebugStop() {
        if (isTraceview) {
            Debug.stopMethodTracing();
        }
    }

    public static void ePrint(Exception e) {
        if (isLoggable(ERROR)) {
            e.printStackTrace();
        }
    }
}