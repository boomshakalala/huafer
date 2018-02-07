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
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.wheelview.NumericWheelAdapter;
import com.huapu.huafen.wheelview.OnWheelScrollListener;
import com.huapu.huafen.wheelview.WheelView;

/**
 * @author sks 日期选择对话框（年月日）
 *
 */
public class DateChooseDialogNew extends Dialog implements OnClickListener  {
	
	private Context context;
	private LayoutInflater inflater = null;
	private String currentDate;
	private TextView dailogTitle;
	private TextView tvTrue;
	private TextView tvCencal;
	private WheelView wvYear;
	private WheelView wvMonth;
	private WheelView wvDay;
	private String birthdayStr = "";
	private String resultDate;
//	private String birthday_arr[];
	private String titleName;
	private ChooseDateCallback chooseDateCallback;
	private Calendar targetTime;
	private int DEFAULT_TARGET_YEAR=Calendar.getInstance().get(Calendar.YEAR)-5;
	private boolean isResetMonthAdapter = false;
	
	public DateChooseDialogNew(Context context,int dateMode,int targetYear,String currentDate) {
		super(context, R.style.photo_dialog);
		this.context = context;
		this.dateMode=dateMode;
		if(targetYear<0){
			targetYear=DEFAULT_TARGET_YEAR;
		}
		if(this.dateMode==PASTTIME_MODE){
			this.targetTime=Calendar.getInstance();
			this.targetTime.set(targetYear, 0, 1);
		}else if(this.dateMode==FUTURE_MODE){
			this.targetTime=Calendar.getInstance();
			this.targetTime.set(targetYear, 11, 31);
		}else {
			throw new RuntimeException("dateMode just can be PASTTIME_MODE or FUTURE_MODE,can not equals other value");
		}
		
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.currentDate=currentDate;
	}
	
	public void setTargetTime(Calendar target){
		this.targetTime=target;
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
	
	
	public void setOnClickListener(ChooseDateCallback chooseDateCallback){
		this.chooseDateCallback = chooseDateCallback;
	}
	
	
	public interface ChooseDateCallback {
		void dateItemOnClic(String dateString);
	}
	
	
	public static final int PASTTIME_MODE=0x123;
	public static final int FUTURE_MODE=PASTTIME_MODE+1;
	private int dateMode;
	private Calendar now;
	private boolean isResetDayAdapter;
	
	public void setDateMode(int dateMode){
		this.dateMode=dateMode;
	}
	
	private View intSexView() {
		
		now = Calendar.getInstance();

		final View view = inflater.inflate(R.layout.wheel_date_picker, null);
		/**
		 * 年轮
		 */
		wvYear = (WheelView) view.findViewById(R.id.year);
		wvYear.setLabel("年");
		wvYear.setCyclic(true);
		wvYear.addScrollingListener(scrollListener);
		
		/**
		 * 月轮
		 */
		wvMonth = (WheelView) view.findViewById(R.id.month);
		wvMonth.setLabel("月");
		wvMonth.setCyclic(true);
		wvMonth.addScrollingListener(scrollListener);
		wvMonth = (WheelView) view.findViewById(R.id.month);
		/**
		 * 日轮
		 */
		wvDay = (WheelView) view.findViewById(R.id.day);
		wvDay.setLabel("日");
		wvDay.setCyclic(true);
		wvDay.addScrollingListener(scrollListener);
		
		NumericWheelAdapter yAdapter = null;
		NumericWheelAdapter mAdapter =null;
		NumericWheelAdapter dAdapter =null;
		if(dateMode==PASTTIME_MODE){
			if(targetTime.getTimeInMillis()>=now.getTimeInMillis()){
				throw new RuntimeException("target time can not be bigger than now time, while dateMode is PASTTIME_MODE");
			}
			yAdapter=new NumericWheelAdapter(targetTime.get(Calendar.YEAR), now.get(Calendar.YEAR));
			mAdapter=new NumericWheelAdapter(1, now.get(Calendar.MONTH)+1, "%02d");
			dAdapter=new NumericWheelAdapter(1, now.get(Calendar.DATE), "%02d");
			
			wvYear.setAdapter(yAdapter);
			wvMonth.setAdapter(mAdapter);
			wvDay.setAdapter(dAdapter);
			
			wvYear.setCurrentItem(now.get(Calendar.YEAR)-targetTime.get(Calendar.YEAR));
			wvMonth.setCurrentItem(now.get(Calendar.MONTH));
			wvDay.setCurrentItem(now.get(Calendar.DAY_OF_MONTH) - 1);
		}else if(dateMode==FUTURE_MODE){
			if(targetTime.getTimeInMillis()<=now.getTimeInMillis()){
				throw new RuntimeException("target time can not be smaller than now time, while dateMode is FUTURE_MODE");
			}
			yAdapter=new NumericWheelAdapter(now.get(Calendar.YEAR),targetTime.get(Calendar.YEAR));
			mAdapter=new NumericWheelAdapter(now.get(Calendar.MONTH)+1, 12, "%02d");
			dAdapter=new NumericWheelAdapter(now.get(Calendar.DATE), getDay(now.get(Calendar.YEAR), now.get(Calendar.MONTH)), "%02d");
			
			wvYear.setAdapter(yAdapter);
			wvMonth.setAdapter(mAdapter);
			wvDay.setAdapter(dAdapter);
			
			wvYear.setCurrentItem(0);
			wvMonth.setCurrentItem(0);
			wvDay.setCurrentItem(0);
		}
		
		
		resetDate();
		if(dateMode==PASTTIME_MODE){
			birthdayStr = new StringBuilder()
					.append((wvYear.getCurrentItem() + targetTime.get(Calendar.YEAR)))
					.append("-")
					.append((wvMonth.getCurrentItem() + 1) < 10 ? "0"
							+ (wvMonth.getCurrentItem() + 1) : (wvMonth
							.getCurrentItem() + 1))
					.append("-")
					.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
							+ (wvDay.getCurrentItem() + 1) : (wvDay
							.getCurrentItem() + 1)).toString();
		}else if(dateMode==FUTURE_MODE){
			if(isResetMonthAdapter){
				birthdayStr = new StringBuilder()
						.append((wvYear.getCurrentItem() + now.get(Calendar.YEAR)))
						.append("-")
						.append((wvMonth.getCurrentItem() + 1) < 10 ? "0"
								+ (wvMonth.getCurrentItem() + 1) : (wvMonth
								.getCurrentItem() + 1))
						.append("-")
						.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
								+ (wvDay.getCurrentItem() + 1) : (wvDay
								.getCurrentItem() + 1)).toString();
			}else{
				
				if(isResetDayAdapter){
					birthdayStr = new StringBuilder()
							.append((now.get(Calendar.YEAR)))
							.append("-")
							.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
									+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
									.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
							.append("-")
							.append(((wvDay.getCurrentItem() +1) < 10) ? "0"
									+ (wvDay.getCurrentItem() +1) : (wvDay
									.getCurrentItem() + 1)).toString();
				}else{
					birthdayStr = new StringBuilder()
							.append((now.get(Calendar.YEAR)))
							.append("-")
							.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
									+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
									.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
							.append("-")
							.append(((wvDay.getCurrentItem() + now.get(Calendar.DATE)) < 10) ? "0"
									+ (wvDay.getCurrentItem() + now.get(Calendar.DATE)) : (wvDay
									.getCurrentItem() + now.get(Calendar.DATE))).toString();
				}
			}
			
		}
		
		return view;
	}
	/**
	 * example:2016-8-17
	 * @param date
	 */
	private void resetDate(){
		if(TextUtils.isEmpty(currentDate)){
			return;
		}
		
		String[] birthday_arr = currentDate.split("-");
		if(birthday_arr.length != 3){
			return;
		}
		try {
			String lastyears = birthday_arr[0];
			String lastmonth = birthday_arr[1];
			String lastday = birthday_arr[2];
			Integer mYear = Integer.valueOf(lastyears);
			int mMonth = (lastmonth.substring(0, 1).equals("0")?Integer.valueOf(lastmonth.replace("0", "")):Integer.valueOf(lastmonth))-1;
			Integer mDay = lastday.substring(0, 1).equals("0")?Integer.valueOf(lastday.replace("0", "")):Integer.valueOf(lastday);
			
			Calendar ca = Calendar.getInstance();
			ca.set(mYear, mMonth-1, mDay);
			
			if(dateMode==PASTTIME_MODE){
				if(ca.getTimeInMillis()>=targetTime.getTimeInMillis()&&ca.getTimeInMillis()<=now.getTimeInMillis()){
					wvYear.setCurrentItem(mYear-targetTime.get(Calendar.YEAR));
					if(mYear!=now.get(Calendar.YEAR)){
						wvMonth.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
					}
					wvMonth.setCurrentItem(mMonth);
					if(mYear!=now.get(Calendar.YEAR)||mMonth!=now.get(Calendar.MONTH)+1){
						initDay(mYear, mMonth);
					}
					wvDay.setCurrentItem(mDay-1);
				}
				
			}else if(dateMode==FUTURE_MODE){
				if(ca.getTimeInMillis()>=now.getTimeInMillis()&&ca.getTimeInMillis()<=targetTime.getTimeInMillis()){
					wvYear.setCurrentItem(mYear-now.get(Calendar.YEAR));
					if(mYear!=now.get(Calendar.YEAR)){
						wvMonth.setAdapter(new NumericWheelAdapter(1, 12, "%02d"));
						wvMonth.setCurrentItem(mMonth);
						isResetMonthAdapter=true;
					}else{
						wvMonth.setCurrentItem(mMonth-now.get(Calendar.MONTH));
						isResetMonthAdapter=false;
					}
					LogUtil.e("resetDate mMonth",mMonth);
					
					if(mYear!=now.get(Calendar.YEAR)||mMonth!=now.get(Calendar.MONTH)+1){
						initDay(mYear, mMonth);
						isResetDayAdapter=true;
					}
					wvDay.setCurrentItem(mDay-1);
				}
			}
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		}
	}
	
	private void initDay(int arg1, int arg2) {
		wvDay.setAdapter(new NumericWheelAdapter(1, getDay(arg1, arg2), "%02d"));
	}
	
	/**
	 * 根据传入的year和month 确定当月的天数	
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
	
	
	OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
		@Override
		public void onScrollingStarted(WheelView wheel) {

		}

		@Override
		public void onScrollingFinished(WheelView wheel) {
			if(dateMode==PASTTIME_MODE){
				int n_year = wvYear.getCurrentItem() + targetTime.get(Calendar.YEAR);
				int n_month = wvMonth.getCurrentItem() + 1;
				int y = now.get(Calendar.YEAR);
				if(n_year>=y){
					if(wheel==wvYear){
						NumericWheelAdapter monthAdapter = new NumericWheelAdapter(1, now.get(Calendar.MONTH)+1, "%02d");
						wvMonth.setAdapter(monthAdapter);
						wvMonth.setCurrentItem(0);
						initDay(n_year, 1);
						wvDay.setCurrentItem(0);
					}
					
					if(wheel==wvMonth){//
						LogUtil.e("month",(wvMonth.getCurrentItem())+" = "+now.get(Calendar.MONTH));
						if(wvMonth.getCurrentItem()==now.get(Calendar.MONTH)){
//							initDay(n_year, now.get(Calendar.MONTH)+1);
							LogUtil.e("DATE", now.get(Calendar.DATE)+"");
							wvDay.setAdapter(new NumericWheelAdapter(1, now.get(Calendar.DATE), "%02d"));
							wvDay.setCurrentItem(0);
						}else{
							initDay(n_year, wvMonth.getCurrentItem()+1);
							wvDay.setCurrentItem(0);
						}
						
					}
					
				}else{
					if(wheel==wvYear){
						NumericWheelAdapter monthAdapter = new NumericWheelAdapter(1, 12, "%02d");
						wvMonth.setAdapter(monthAdapter);
						wvMonth.setCurrentItem(0);
						initDay(n_year, 1);
						wvDay.setCurrentItem(0);
					}
					
					if(wheel==wvMonth){
						initDay(n_year, wvMonth.getCurrentItem()+1);
						wvDay.setCurrentItem(0);
					}
					
					
				}
				
				birthdayStr = new StringBuilder()
						.append((wvYear.getCurrentItem() + targetTime.get(Calendar.YEAR)))
						.append("-")
						.append((wvMonth.getCurrentItem() + 1) < 10 ? "0"
								+ (wvMonth.getCurrentItem() + 1) : (wvMonth
								.getCurrentItem() + 1))
						.append("-")
						.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
								+ (wvDay.getCurrentItem() + 1) : (wvDay
								.getCurrentItem() + 1)).toString();
				
			}else if(dateMode==FUTURE_MODE){
				int n_year = wvYear.getCurrentItem() + now.get(Calendar.YEAR);//
				
				int y = now.get(Calendar.YEAR);
				if(n_year<=y){//当时间滚动到最小年
					if(wheel==wvYear){
						NumericWheelAdapter monthAdapter = new NumericWheelAdapter(now.get(Calendar.MONTH)+1, 12, "%02d");
						wvMonth.setAdapter(monthAdapter);
						wvMonth.setCurrentItem(0);
						wvDay.setAdapter(new NumericWheelAdapter(now.get(Calendar.DATE), getDay(now.get(Calendar.YEAR), now.get(Calendar.MONTH)), "%02d"));		
						wvDay.setCurrentItem(0);
						birthdayStr = new StringBuilder()
								.append((now.get(Calendar.YEAR)))
								.append("-")
								.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
										+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
										.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
								.append("-")
								.append(((wvDay.getCurrentItem() + now.get(Calendar.DATE)) < 10) ? "0"
										+ (wvDay.getCurrentItem() + now.get(Calendar.DATE)) : (wvDay
										.getCurrentItem() + now.get(Calendar.DATE))).toString();
					}
					
					if(wheel==wvMonth){
						if(wvMonth.getCurrentItem()==0){
							wvDay.setAdapter(new NumericWheelAdapter(now.get(Calendar.DATE), getDay(now.get(Calendar.YEAR), now.get(Calendar.MONTH)), "%02d"));
							wvDay.setCurrentItem(0);
							birthdayStr = new StringBuilder()
									.append((now.get(Calendar.YEAR)))
									.append("-")
									.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
											+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
											.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
									.append("-")
									.append(((wvDay.getCurrentItem() + now.get(Calendar.DATE)) < 10) ? "0"
											+ (wvDay.getCurrentItem() + now.get(Calendar.DATE)) : (wvDay
											.getCurrentItem() + now.get(Calendar.DATE))).toString();
						}else{
							initDay(n_year, wvMonth.getCurrentItem() + 1+now.get(Calendar.MONTH));
							wvDay.setCurrentItem(0);
							birthdayStr = new StringBuilder()
									.append((now.get(Calendar.YEAR)))
									.append("-")
									.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
											+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
											.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
									.append("-")
									.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
											+ (wvDay.getCurrentItem() + 1) : (wvDay
											.getCurrentItem() + 1)).toString();
						}
						
					}
					
					if(wheel==wvDay){
						if(wvMonth.getCurrentItem()==0){
							birthdayStr = new StringBuilder()
									.append((now.get(Calendar.YEAR)))
									.append("-")
									.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
											+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
											.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
									.append("-")
									.append(((wvDay.getCurrentItem() + now.get(Calendar.DATE)) < 10) ? "0"
											+ (wvDay.getCurrentItem() + now.get(Calendar.DATE)) : (wvDay
											.getCurrentItem() + now.get(Calendar.DATE))).toString();
						}else{
							birthdayStr = new StringBuilder()
									.append((now.get(Calendar.YEAR)))
									.append("-")
									.append((wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) < 10 ? "0"
											+ (wvMonth.getCurrentItem() + now.get(Calendar.MONTH)+1) : (wvMonth
											.getCurrentItem() +now.get(Calendar.MONTH)+ 1))
									.append("-")
									.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
											+ (wvDay.getCurrentItem() + 1) : (wvDay
											.getCurrentItem() + 1)).toString();
						}
						
					}
					
				}else{
					if(wheel==wvYear){
						NumericWheelAdapter monthAdapter = new NumericWheelAdapter(1, 12, "%02d");
						wvMonth.setAdapter(monthAdapter);
						initDay(n_year, 1);
						wvMonth.setCurrentItem(0);
						wvDay.setCurrentItem(0);
					}
					
					if(wheel==wvMonth){
						initDay(n_year, wvMonth.getCurrentItem()+1);
						wvDay.setCurrentItem(0);
					}
					birthdayStr = new StringBuilder()
							.append((wvYear.getCurrentItem() + now.get(Calendar.YEAR)))
							.append("-")
							.append((wvMonth.getCurrentItem() + 1) < 10 ? "0"
									+ (wvMonth.getCurrentItem() + 1) : (wvMonth
									.getCurrentItem() + 1))
							.append("-")
							.append(((wvDay.getCurrentItem() + 1) < 10) ? "0"
									+ (wvDay.getCurrentItem() + 1) : (wvDay
									.getCurrentItem() + 1)).toString();
				}
				
				
			}
			
			LogUtil.e("birthdayStr", birthdayStr+"");
		}
	};

	
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
