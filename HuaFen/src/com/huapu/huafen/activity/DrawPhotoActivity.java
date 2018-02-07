package com.huapu.huafen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.ImageUtils;
import com.huapu.huafen.utils.ToastUtil;
import com.huapu.huafen.views.MosaicImageView;

import java.io.File;
import java.io.IOException;

/**
 * 对图片进行编辑的界面（弃用）
 * 
 **/

public class DrawPhotoActivity extends BaseActivity {
	/** 传递动作,为takephoto 表示拍照，否则传递过来的是图片的路径 */
	public static final String FILEPATH = "filepath";
	public static final String ACTION_INIT = "action_init";


	public static final String FROM = "from";

	/** 动作 */
	public static final String TAKEPHOTO = "takephoto";

	public static final String REDRAW = "ReDraw";

	/** 涂鸦控件的容器 **/
	public LinearLayout imageContent;
	/** 操纵图片的路径 **/
	private String filePath = "";
	/** 涂鸦控件 **/
	private MosaicImageView touchView;
	/** 完成按钮 **/
	public TextView overBt;
	/** 返回按钮（左上角）*/
	public ImageButton backIB = null;
	/** 完成按钮（右上角）*/
	public Button finishBtn = null;
	/** 撤销文字 **/
	public TextView cancelText;
	private GetImage handler;
//	private ProgressDialog progressDialog = null;
//	private ProgressDialog dialog = null;
	/** 是否为涂鸦 如果是涂鸦 不能删除之前的照片 **/
	public boolean isReDraw = false;
	Intent intent = null;
	public BroadcastReceiver broadcastReceiver = null;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_draw_photo);
		initView();
		// 获取传递过来的图片路径
		intent = getIntent();
		broadcastReceiver = new BroadcastReceiver(){
			public void onReceive(Context context, Intent intent) {
					ProgressDialog.closeProgress();
			};
		};
		
		registerReceiver(broadcastReceiver, new IntentFilter(ACTION_INIT));
		filePath = intent.getExtras().getString(FILEPATH);
		if (!TextUtils.isEmpty(filePath)) {
			ImageThread thread = new ImageThread();
			thread.start();
		}
		
	}

	private void initView() {
		getTitleBar().
				setTitle("编辑").
				setOnLeftButtonClickListener(R.drawable.btn_title_left_states, new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							touchView.sourceBitmap.recycle();
							touchView.sourceBitmapCopy.recycle();
							touchView.destroyDrawingCache();
						} catch (Exception e) {
							e.printStackTrace();
						}
						finish();
					}
				});
		imageContent = (LinearLayout) findViewById(R.id.draw_photo_view);
		
		handler = new GetImage();
		overBt = (TextView) findViewById(R.id.draw_ok_text);
		cancelText = (TextView) findViewById(R.id.draw_photo_cancel);
		overBt.setOnClickListener(new View.OnClickListener() {// 完成编辑按钮
			@Override
			public void onClick(View v) {
				overBt.setEnabled(false);
				// 新建一个文件保存照片
				File f = new File(FileUtils.getIconDir() + System.currentTimeMillis() + ".jpg");
				try {
					Bitmap saveBitmap = touchView.combineBitmap(touchView.sourceBitmapCopy, touchView.sourceBitmap);
					ImageUtils.saveMyBitmap(f, saveBitmap);// 将图片重新存入SD卡
					if (touchView.sourceBitmapCopy != null) {
						touchView.sourceBitmapCopy.recycle();
					}
					touchView.sourceBitmap.recycle();
					saveBitmap.recycle();
					touchView.destroyDrawingCache();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.putExtra(MyConstants.EXTRA_DRAW_PHOTO_PATH, f.getAbsolutePath());
				setResult(Activity.RESULT_OK, intent);
				ToastUtil.toast(DrawPhotoActivity.this, "已保存至SD卡"+FileUtils.getIconDir()+"目录下");
				finish();
			}
		});

		cancelText.setOnClickListener(new View.OnClickListener() {// 撤销按钮
					@Override
					public void onClick(View v) {
						cancelDrawImage();
					}
				});
	}

	/** 撤销方法 **/
	@SuppressWarnings("deprecation")
	@SuppressLint("HandlerLeak")
	public void cancelDrawImage() {
			touchView.destroyDrawingCache();
			WindowManager manager = DrawPhotoActivity.this.getWindowManager();
			int ww = manager.getDefaultDisplay().getWidth();// 这里设置高度
			int hh = manager.getDefaultDisplay().getHeight();// 这里设置宽度为
			touchView.revocation(filePath, ww, hh);
			// OME--
			if(imageContent.getChildCount() == 0){
				imageContent.addView(touchView);
			}
	}

	@SuppressLint("HandlerLeak")
	private class GetImage extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0: {
				ProgressDialog.showProgress(DrawPhotoActivity.this);
			}
				break;
			case 1: {
				if (touchView != null) {
					imageContent.removeView(touchView);
				}
				touchView = (MosaicImageView) msg.obj;
				touchView.destroyDrawingCache();
				imageContent.addView(touchView);
			}
				break;
			case 2: {
				// 获取新的图片路径
				filePath = (String) msg.obj;
				// 开启图片和处理线程
				ImageThread thread = new ImageThread();
				thread.start();
			}
				break;
			case 3: {
				ProgressDialog.closeProgress();
			}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	}

	private class ImageThread extends Thread {
		@SuppressWarnings("deprecation")
		public void run() {
			// 打开进度条
			Message msg = new Message();
			msg.what = 0;
			handler.sendMessage(msg);
			// 获取屏幕大小
			WindowManager manager = DrawPhotoActivity.this.getWindowManager();
			int ww = manager.getDefaultDisplay().getWidth();// 这里设置高度
			int hh = manager.getDefaultDisplay().getHeight();// 这里设置宽度为
			// 生成画图视图
			touchView = new MosaicImageView(DrawPhotoActivity.this, null, filePath, ww, hh);
			Message msg1 = new Message();
			msg1.what = 1;
			msg1.obj = touchView;
			handler.sendMessage(msg1);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(broadcastReceiver != null){
			unregisterReceiver(broadcastReceiver);
		}
	}
}
