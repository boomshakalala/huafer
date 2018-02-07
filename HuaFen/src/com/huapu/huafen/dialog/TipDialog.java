package com.huapu.huafen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.huapu.huafen.R;
/** 
 * @ClassName: TextDialog 
 * @Description: 文本dialog
 * @author liang_xs
 * @date 2016-03-27
 */
public class TipDialog extends Dialog implements OnClickListener {
	private Button btnCancel;

	public TipDialog(Context context) {
		super(context, R.style.DialogText);
	}

	public TipDialog(Context context, boolean isCancel) {
		super(context, R.style.DialogText);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tip);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancel:
			this.dismiss();
			break;
		}
	}
}
