package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.huapu.huafen.R;
/** 
 * @ClassName: 相机 
 * @Description: 相机dialog
 * @author liang_xs
 * @date 2016-03-27
 */
public class PhotoDialog extends Dialog implements OnClickListener {
	private Context mContext;
	private DialogCallback btnCamera, btnAlbum;
	private TextView tvCamera, tvAlbum, tvCancel;
	private String first, second;

	public PhotoDialog(Context context) {
		super(context, R.style.photo_dialog);
		this.mContext = context;
		setCancelable(true);
	}
	public PhotoDialog(Context context, String first, String second) {
		super(context, R.style.photo_dialog);
		this.mContext = context;
		this.first = first;
		this.second = second;
		setCancelable(true);
	}

	public PhotoDialog(Context context, boolean isCancel) {
		super(context, R.style.photo_dialog);
		this.mContext = context;
		setCancelable(isCancel);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_photo);
		tvCamera = (TextView) findViewById(R.id.tvCamera);
		tvAlbum = (TextView) findViewById(R.id.tvAlbum);
		tvCancel = (TextView) findViewById(R.id.tvCancel);
		tvCamera.setOnClickListener(this);
		tvAlbum.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		if(!TextUtils.isEmpty(first)) {
			tvCamera.setText(first);
		}
		if(!TextUtils.isEmpty(second)) {
			tvAlbum.setText(second);
		}
		Window window = getWindow();
		android.view.WindowManager.LayoutParams lp = window.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		lp.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		lp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;
		window.setGravity(Gravity.BOTTOM);
		window.setAttributes(lp);
		window.setWindowAnimations(R.style.DialogBottomTransScaleStyle);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvCamera:
			btnCamera.Click();
			break;
		case R.id.tvAlbum:
			btnAlbum.Click();
			break;
		case R.id.tvCancel:
			break;
		}
		this.dismiss();
	}


	public void setCameraCall(DialogCallback btnCamera) {
		this.btnCamera = btnCamera;
	}

	public void setAlbumCall(DialogCallback btnAlbum) {
		this.btnAlbum = btnAlbum;
	}

}
