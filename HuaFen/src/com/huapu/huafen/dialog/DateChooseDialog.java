package com.huapu.huafen.dialog;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.wheelview.NumericWheelAdapter;
import com.huapu.huafen.wheelview.OnWheelScrollListener;
import com.huapu.huafen.wheelview.WheelView;

/**
 * @author sks 日期选择对话框（年月日）
 *
 */
public class DateChooseDialog extends Dialog implements OnClickListener  {
	
	private Context context;
	private LayoutInflater inflater = null;
	private String currentDate;
	private TextView dailogTitle;//标题
	private TextView tvTrue;//确定按钮
	private TextView tvCencal;//取消按钮
	private WheelView wvYear;
	private WheelView wvMonth;
	private WheelView wvDay;
	private int smallYear = 2000;
	private int mYear = 1996;
	private int mMonth = 0;
	private int mDay = 1;
	private String birthdayStr = "";
	private String resultDate;
	private String birthday_arr[];
	private String titleName;
	private ChooseDateCallback chooseDateCallback;
//	private NumericWheelAdapter monthAdapter;

	public DateChooseDialog(Context context, int theme) {
		super(context, R.style.photo_dialog);
		this.context = context;
	}

	/**
	 * @param context
	 * @param currentDate//默认显示时间  格式2015-12-31 可传""
	 */
	public DateChooseDialog(Context context,String currentDate) {
		super(context, R.style.photo_dialog);
		this.context = context;
		this.currentDate = currentDate;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initDate();
		setCancelable(true);
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
		dailogTitle = (TextView) findViewById(R.id.date_title);
		dailogTitle.setText(titleName);
		tvTrue.setOnClickListener(this);
		tvCencal.setOnClickListener(this);
	}
	
	public void setDialogTitle(String titleName){
		this.titleName = titleName;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_customer_date_true:
			resultDate = birthdayStr.equals("")?currentDate:birthdayStr;
			chooseDateCallback.dateItemOnClic(resultDate);
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
	 * @param chooseSexCallback 设置点击回调
	 */
	public void setOnClickListener(ChooseDateCallback chooseDateCallback){
		this.chooseDateCallback = chooseDateCallback;
	}
	
	/** 
	 * @author 确定点击事件回调
	 */
	public interface ChooseDateCallback {
		void dateItemOnClic(String dateString);
	}
	
	/**
	 * 初始化默认显示日期
	 */
	private void initDate() {
		Calendar c = Calendar.getInstance();
//		mYear = c.get(Calendar.YEAR);
//		mMonth = c.get(Calendar.MONTH);
//		mDay = c.get(Calendar.DAY_OF_MONTH);
		if (TextUtils.isEmpty(currentDate)) {
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);
		} else {
			birthday_arr = currentDate.split("-");
			if (birthday_arr.length == 3) {
				String lastyears = birthday_arr[0];
				String lastmonth = birthday_arr[1];
				String lastday = birthday_arr[2];
				mYear = Integer.valueOf(lastyears);
				mMonth = (lastmonth.substring(0, 1).equals("0")?Integer.valueOf(lastmonth.replace("0", "")):Integer.valueOf(lastmonth))-1;
				mDay = lastday.substring(0, 1).equals("0")?Integer.valueOf(lastday.replace("0", "")):Integer.valueOf(lastday);
			}
		}
	}

	/**
	 * 初始化年月日选项
	 * @return
	 */
	private View intSexView() {
		Calendar c = Calendar.getInstance();
		int norYear = c.get(Calendar.YEAR);

		int curYear = mYear;
		int curMonth = mMonth + 1;
		int curDate = mDay;

		final View view = inflater.inflate(R.layout.wheel_date_picker, null);

		wvYear = (WheelView) view.findViewById(R.id.year);
		wvYear.setAdapter(new NumericWheelAdapter(smallYear, norYear));
		wvYear.setLabel("年");
		wvYear.setCyclic(true);
		wvYear.addScrollingListener(scrollListener);

		wvMonth = (WheelView) view.findViewById(R.id.month);
		Calendar ca = Calendar.getInstance();
		int maxMonth=ca.get(Calendar.MONTH);
		NumericWheelAdapter monthAdapter = new NumericWheelAdapter(1, maxMonth+1, "%02d");
		wvMonth.setAdapter(monthAdapter);
		wvMonth.setLabel("月");
		wvMonth.setCyclic(true);
		wvMonth.addScrollingListener(scrollListener);

		wvDay = (WheelView) view.findViewById(R.id.day);
//		initDay(curYear, maxMonth);
		wvDay.setAdapter(new NumericWheelAdapter(1, ca.get(Calendar.DATE), "%02d"));
		wvDay.setLabel("日");
		wvDay.setCyclic(true);
		wvDay.addScrollingListener(scrollListener);

		wvYear.setCurrentItem(curYear - smallYear);
		wvMonth.setCurrentItem(maxMonth);
		wvDay.setCurrentItem(curDate - 1);
		birthdayStr = new StringBuilder()
		.append((wvYear.getCurrentItem() + smallYear))
		.append("-")
		.append((wvMonth.getCurrentItem() + 1) < 10 ? "0"
				+ (wvMonth.getCurrentItem() + 1) : (wvMonth
				.getCurrentItem() + 1))
		.append("-")
		.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
				+ (wvDay.getCurrentItem() + 1) : (wvDay
				.getCurrentItem() + 1)).toString();
		return view;
	}
	
	/**
	 * 根据年月 动态算出 每月天数
	 */
	private void initDay(int arg1, int arg2) {
		wvDay.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}
	
	/**
	 * 根据年月 动态算出 每月天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	private int getDay(int year, int month) {
		int day = 30;
		boolean flag = false;
		switch (year % 4) {
		case 0:
			flag = true;
			break;
		default:
			flag = false;
			break;
		}
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = 31;
			break;
		case 2:
			day = flag ? 29 : 28;
			break;
		default:
			day = 30;
			break;
		}
		return day;
	}
	
	/**
	 * 年月日滑动监听
	 */
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			int n_year = wvYear.getCurrentItem() + smallYear;//
			
			int n_month = wvMonth.getCurrentItem() + 1;//
			
			Calendar ca=Calendar.getInstance();
			int y = ca.get(Calendar.YEAR);
			//TODO
			
			if(n_year>=y){
				NumericWheelAdapter monthAdapter = new NumericWheelAdapter(1, ca.get(Calendar.MONTH)+1, "%02d");
				wvMonth.setAdapter(monthAdapter);
				if(wvMonth.getCurrentItem()+1>ca.get(Calendar.MONTH)){
					wvMonth.setCurrentItem(ca.get(Calendar.MONTH));
					wvDay.setAdapter(new NumericWheelAdapter(1, ca.get(Calendar.DATE), "%02d"));			
					if(wheel!=wvDay){
						int date=ca.get(Calendar.DATE);
						Log.e("date", date+"");
						wvDay.setCurrentItem(ca.get(Calendar.DATE)-1);
					}
				}		
			}else{
				NumericWheelAdapter monthAdapter = new NumericWheelAdapter(1, 12, "%02d");
				wvMonth.setAdapter(monthAdapter);
				initDay(n_year, n_month);
			}
			
			
			
			birthdayStr = new StringBuilder()
					.append((wvYear.getCurrentItem() + smallYear))
					.append("-")
					.append((wvMonth.getCurrentItem() + 1) < 10 ? "0"
							+ (wvMonth.getCurrentItem() + 1) : (wvMonth
							.getCurrentItem() + 1))
					.append("-")
					.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
							+ (wvDay.getCurrentItem() + 1) : (wvDay
							.getCurrentItem() + 1)).toString();
			
			String oldStr = "年龄             " + calculateDatePoor(resultDate)
					+ "岁";
			
		}
	};

	/**
	 * 根据年月日算出岁数
	 */
	public static final String calculateDatePoor(String birthday) {
		try {
			if (TextUtils.isEmpty(birthday))
				return "0";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date birthdayDate = sdf.parse(birthday);
			String currTimeStr = sdf.format(new Date());
			Date currDate = sdf.parse(currTimeStr);
			if (birthdayDate.getTime() > currDate.getTime()) {
				return "0";
			}
			long age = (currDate.getTime() - birthdayDate.getTime())
					/ (24 * 60 * 60 * 1000) + 1;
			String year = new DecimalFormat("0.00").format(age / 365f);
			if (TextUtils.isEmpty(year))
				return "0";
			return String.valueOf(new Double(year).intValue());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "0";
	}

}
