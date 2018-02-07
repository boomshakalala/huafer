package com.huapu.huafen.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huapu.huafen.R;
/** 
 * @ClassName: ProgressDialog 
 * @Description: 网络请求等待提示
 * @author liang_xs
 * @date 2016-03-27
 */
public class ProgressDialog {
	private static CustomDialog mDialog = null;
	public static void showProgress(Context context) {
		showProgress(context, "", true);
	}

	public static void showProgress(Context context, CharSequence message) {
		showProgress(context, message, true);
	}
	public static void showProgress(Context context, boolean isCancel) {
		showProgress(context, "", isCancel);
	}
	public static void showProgress(Context context, CharSequence message, boolean isCancel) {
		if (context instanceof Activity && ((Activity) context).isFinishing()) {
			return;
		}

		try {
			if(null != mDialog && mDialog.isShowing()){
				mDialog.dismiss();
				mDialog.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mDialog = new CustomDialog(context, (String) message, isCancel);
		mDialog.show();

	}

	public static void closeProgress() {
		try {
			if(null != mDialog ){
				mDialog.dismiss();
				mDialog.cancel();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class CustomDialog extends Dialog {
		private TextView textViewContent;
		private String strContent;
		public CustomDialog(Context context, String strContent, boolean isCancel) {
			super(context, R.style.DialogProgress);
			setCancelable(isCancel);
			this.strContent = strContent;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.dialog_progress_loading);
			textViewContent = (TextView) findViewById(R.id.textViewContent);
			if (!TextUtils.isEmpty(strContent)) {
				textViewContent.setVisibility(View.VISIBLE);
				textViewContent.setText(strContent);
			} else {
				textViewContent.setVisibility(View.GONE);
			}
		}
	}
}
