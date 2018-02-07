package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.callbacks.SimpleTextWatcher;

/**
 * @ClassName: TextDialog 
 * @Description: 文本dialog
 * @author liang_xs
 * @date 2016-03-27
 */
public class EditDialog extends Dialog implements OnClickListener {
	private Context context;
	private EditText tvDialogContent;
	private Button btnDialogLeft, btnDialogRight;
	private String strTitle, strContent, strLeft, strRight;

	public EditDialog(Context context) {
		super(context, R.style.DialogText);
		this.context = context;
		setCancelable(false);
	}

	public EditDialog(Context context, boolean isCancel) {
		super(context, R.style.DialogText);
		this.context = context;
		setCancelable(isCancel);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_edit);
		tvDialogContent = (EditText) findViewById(R.id.tvDialogContent);
		btnDialogLeft = (Button) findViewById(R.id.btnDialogLeft);
		btnDialogRight = (Button) findViewById(R.id.btnDialogRight);
		btnDialogLeft.setOnClickListener(this);
		btnDialogRight.setOnClickListener(this);
		tvDialogContent.addTextChangedListener(new SimpleTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!=null&&s.toString().replaceAll("\\n","").replaceAll("\\t", "").trim().length()>0){
					btnDialogRight.setEnabled(true);
					btnDialogRight.setTextColor(Color.parseColor("#ff6677"));
				}else{
					btnDialogRight.setEnabled(false);
					btnDialogRight.setTextColor(Color.parseColor("#cccccc"));
				}
			}
		});

		btnDialogRight.setEnabled(false);
		btnDialogRight.setTextColor(Color.parseColor("#cccccc"));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDialogLeft:
			dismiss();
			break;
		case R.id.btnDialogRight:
			if(mOnBrandAdded!=null){
				mOnBrandAdded.onBrandAdded(tvDialogContent.getText().toString().trim());
			}
			dismiss();
			break;
		}
		this.dismiss();
	}

	public void setTitleText(String str) {
		strTitle = str;
	}
	public void setTitleText(int strId) {
		strTitle = context.getResources().getString(strId);
	}

	public void setRightText(String str) {
		strRight = str;
	}
	public void setRightText(int strId) {
		strRight = context.getResources().getString(strId);
	}

	public void setLeftText(String str) {
		strLeft = str;
	}
	public void setLeftText(int strId) {
		strLeft = context.getResources().getString(strId);
	}

	public void setContentText(String str) {
		strContent = str;
	}
	public void setContentText(int strId) {
		strContent = context.getResources().getString(strId);
	}

	public interface OnBrandAdded{
		void onBrandAdded(String brand);
	}

	private OnBrandAdded mOnBrandAdded;

	public void setOnBrandAdded(OnBrandAdded onBrandAdded) {
		this.mOnBrandAdded = onBrandAdded;
	}
}
