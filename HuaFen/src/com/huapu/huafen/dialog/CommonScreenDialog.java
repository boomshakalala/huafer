package com.huapu.huafen.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.wheelview.ArrayWheelAdapter;
import com.huapu.huafen.wheelview.OnWheelScrollListener;
import com.huapu.huafen.wheelview.WheelView;


/**
 *
 */
public class CommonScreenDialog extends Dialog implements OnClickListener {

	private Context context;
	private TextView tvTitle;
	private String resultStr;//如(当前性别0女 1男)
	private TextView tvTrue;//确定按钮
	private TextView tvCencal;//取消按钮
	private LayoutInflater inflater = null;
	private WheelView wvScreen;
	private String[] contentArray = {"女","男"};
	private String titleName = "";//
	private int position = 0;//默认停留在第一个选项
	private ChooseSexCallback chooseSexCallback;
	
	public CommonScreenDialog(Context context, int theme) {
		super(context, R.style.photo_dialog);
		this.context = context;
	}

	/**
	 * @param context
	 * @param position 默认选项  如 传0或1 （女男）
	 */
	public CommonScreenDialog(int position,Context context) {
		super(context, R.style.photo_dialog);
		this.context = context;
		this.position = position;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setCancelable(true);
	}
	
	/**
	 * 设置对话框标题 选项内容
	 * @param titleName
	 * @param contentArray
	 */
	public void setTitleAndContent(String titleName,String[] contentArray){
		this.titleName = titleName;
		this.contentArray = contentArray;
		resultStr = contentArray[position] ;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date_chose_layout);

		Window window = this.getWindow();
		LayoutParams lp = window.getAttributes();
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		lp.width = dm.widthPixels;
		window.setGravity(Gravity.BOTTOM);
		window.setWindowAnimations(R.style.DialogBottomStyle);

		LinearLayout rl_date_content = (LinearLayout) findViewById(R.id.rl_date_content);
		LayoutParams pm = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		pm.gravity = Gravity.CENTER_HORIZONTAL;
		rl_date_content.addView(intSexView(), pm);

		tvTrue = (TextView) findViewById(R.id.tv_customer_date_true);
		tvTrue.setText("确定");
		tvCencal = (TextView) findViewById(R.id.tv_customer_date_cencal);
		tvCencal.setText("取消");
		tvTitle.setText(titleName);
		tvTrue.setOnClickListener(this);
		tvCencal.setOnClickListener(this);
	}

	/**
	 * 初始化性别选项
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private View intSexView() {
		final View view = inflater.inflate(R.layout.sex_choose_picker, null);
		
		tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		wvScreen = (WheelView) view.findViewById(R.id.wheelView_sex);
		wvScreen.setAdapter(new ArrayWheelAdapter(contentArray));
		wvScreen.addScrollingListener(scrollListener);
		wvScreen.setCurrentItem(position);
		
		return view;
	}

	
	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_customer_date_true:
			chooseSexCallback.sexItemOnClic(position,resultStr);
			this.dismiss();
			break;
		case R.id.tv_customer_date_cencal:
			this.dismiss();
			break;

		default:
			break;
		}		
	}
	
	/**
	 * 滑动监听
	 */
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			position = wheel.getCurrentItem();
			resultStr = contentArray[wheel.getCurrentItem()] ;
//			DialogUitls.showToast(context, sex + " - " + position, true);
		}
	};
	
	/**
	 * @param chooseSexCallback 设置点击回调
	 */
	public void setOnClickListener(ChooseSexCallback chooseSexCallback){
		this.chooseSexCallback = chooseSexCallback;
	}
	
	/** 
	 * @author 确定点击事件回调
	 */
	public interface ChooseSexCallback {
		void sexItemOnClic(int position, String resultStr);
	}
}
