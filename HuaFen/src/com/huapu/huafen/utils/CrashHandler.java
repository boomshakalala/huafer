package com.huapu.huafen.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.huapu.huafen.common.MyConstants;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

/**
 * @ClassName: CrashHandler 
 * @Description: UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 	在程序刚启动时调用以下代码：
 * 		CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext(), this);
 * @author liang_xs
 * @date 2014-5-15
 */
public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";
	public static boolean isLog = MyConstants.printLog; // true保存log
	
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	// CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context mContext;
	private Activity mActivity;
	private Map<String, String> mapPhoneInfo;
	
	private Dialog dialog;

	// 用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context, Activity activity) {
		mActivity = activity;
		mContext = context;
		if (isLog) {
			// 获取系统默认的UncaughtException处理器
			mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
			// 设置该CrashHandler为程序的默认处理器
			Thread.setDefaultUncaughtExceptionHandler(this);
		}
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			mActivity.finish();
			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				ToastUtil.toast(mContext, "很抱歉,程序出现异常");
//				dialog = DialogUitls.showConfirmDialog(mActivity, "错误提示", "很抱歉,程序出现异常", "继续使用", "退出程序", new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						
//						if(null != dialog){
//							dialog.dismiss();
//						}
//						mActivity.finish();
//						// 退出程序
//						android.os.Process.killProcess(android.os.Process.myPid());
//						System.exit(1);
//					}
//				});
//				Intent intetn =new Intent(mActivity,MainActivity.class);
//				mActivity.startActivity(intetn);
				Looper.loop();
			}
		}.start();
		// 收集设备参数信息
		collectDeviceInfo(mContext);
		// 保存日志文件
		saveCrashInfo2File(ex);
		return true;
	}
	
	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	public void collectDeviceInfo(Context mContext) {
		mapPhoneInfo = PhoneInfoUitls.getPhoneInfo(mContext, mActivity);
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File2Json(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		// 封装为str后log无法打印日志
		StringBuffer sbLog = new StringBuffer();
		JSONObject obj = new JSONObject();
		for (Map.Entry<String, String> entry : mapPhoneInfo.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sbLog.append(key + "=" + value + "\n");
			try {
				obj.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		try {
			obj.put("日志Caused by", result);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		sbLog.append(result);
		sb.append(obj.toString());
			// 将错误信息加入数据库
//			LogBean logBean = new LogBean();
//			logBean.logContent = sb.toString();
//			logBean.logFlag = "1";// 未上传
//			logBean.insert();
			Log.e(TAG, sbLog.toString());
		return sb.toString();
	}
	
	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		//提交友盟错误日志
//		MobclickAgent.reportError(mContext, ex);
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : mapPhoneInfo.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";
			String crashPath;
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				crashPath = FileUtils.SD_APP_PATH + File.separator + "crash/";
				File dir = new File(crashPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			} else {
				crashPath = FileUtils.DATA_APP_PATH + File.separator + "crash/";
				File dir = new File(crashPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			}
			FileOutputStream fos = new FileOutputStream(crashPath + fileName);
			fos.write(sb.toString().getBytes());
			fos.close();
			Log.e(TAG, sb.toString());
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
}
