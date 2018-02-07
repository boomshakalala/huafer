package com.huapu.huafen.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.huapu.huafen.common.MyConstants;
/** 
 * @author liang_xs
 * @date 2016-03-27
 */
public class DateTimeUtils {
	public static String getMonthDayHourMinute(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(new Date(timeMillis));
	}

	public static String getHourMinute(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		return sdf.format(new Date(timeMillis));
	}
	public static String getHour(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		return sdf.format(new Date(timeMillis));
	}

	public static String getMinute(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		return sdf.format(new Date(timeMillis));
	}
	public static String getYearMonthDayText(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(new Date(timeMillis));
	}
	public static String getYearMonthDay(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(timeMillis));
	}
	public static String getYearMonthDayHourMinuteSecond(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(timeMillis));
	}
	public static String getYearMonthDayHourMinute(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(timeMillis));
	}
	public static String getYearMonthDayHourMinute2(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年 MM月dd日 HH:mm");
		return sdf.format(new Date(timeMillis));
	}
	public static String getMonthDay(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
		return sdf.format(new Date(timeMillis));
	}
	public static String getMonthDayHourMinute2(long timeMillis) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(MM_DD_HH_MM);
		return sdf.format(new Date(timeMillis));
	}
	public static final String YYYY_MM_DD_HH_MM_CHINESE = "yyyy年MM月dd日 HH:mm";
	public static final String MM_DD_HH_MM = "MM月dd日 HH:mm";
	public static String getStringDate(long timeMillis, String format) {
		if (timeMillis == 0) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(timeMillis));
	}

	 public static String dtFormat(Date date, String dateFormat){
	        return getFormat(dateFormat).format(date);
	    }

	    private static final DateFormat getFormat(String format) {
	        return new SimpleDateFormat(format);
	    }
	
	/**
	 * 将日期字符串转换日期类型
	 * 
	 * @param dateStr
	 *            日期字符串
	 * @param format
	 *            日期格式
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(String dateStr, String format){
		Date date = null;
		if (dateStr == null || dateStr.equals("")) {
			return null;
		}
		SimpleDateFormat dateformate = new SimpleDateFormat(format);

		try {
			date = dateformate.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 返回字符串类型的日期对象
	 * @param time
	 * @param format
	 * @return
	 */
	public static String getDateStr(long time, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 将字符串转换为时间戳
	 * @param format 字符串时间格式
	 * @param time 字符串时间
	 * @return
	 */
	public static String getTime(String format, String time) {
		String parseTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date d;
		try {
			d = sdf.parse(time);
			long l = d.getTime();
			parseTime = String.valueOf(l);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parseTime;
	}  
	
	/**
	 * 将字符串转换为时间戳
	 * @param format 字符串时间格式
	 * @param time 字符串时间
	 * @return
	 */
	public static long getLongTime(String format, String time) {
		long parseTime = 0;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date d;
		try {
			d = sdf.parse(time);
			parseTime = d.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parseTime;
	}  
	
	 /**  
     * 计算两个日期之间相差的天数  
     * @param smdate 较小的时间 
     * @param bdate  较大的时间 
     * @return 相差天数 
     * @throws ParseException  
     */    
    public static int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    } 
    
    public static String getBabyAge(long time){
    	String startDate = getDateStr(time, "yyyy-MM-dd");
		String[] startDateArray = startDate.split("-");
		String[] currentDateStrArray = getDateStr(System.currentTimeMillis(), "yyyy-MM-dd").split("-");
		//开始时间
		int sY = Integer.parseInt(startDateArray[0]);
		int sM = Integer.parseInt(startDateArray[1]);
		int sD = Integer.parseInt(startDateArray[2]);
		//结束时间
		int eY = Integer.parseInt(currentDateStrArray[0]);
		int eM = Integer.parseInt(currentDateStrArray[1]);
		int eD = Integer.parseInt(currentDateStrArray[2]);
		
		int monthCount = 0;
		if(eD < sD){
			eM--;
		}
		
		monthCount = (eY - sY) * 12 + eM - sM;
		int yearCount = 0;
		if(monthCount < 0) {
			monthCount = 0;
		}
		if(monthCount < 24){
//			System.out.println("宝宝年龄：" + monthCount + "个月");
			return monthCount + "月";
		}else {
			yearCount = monthCount / 12;
			monthCount = monthCount % 12;
//			System.out.println("宝宝年龄：" + yearCount + "岁" + monthCount + "个月");
			return yearCount + "岁" + monthCount + "月";
		}
    }


	public static String getBabyAgeWithoutMonth(long time){
		String startDate = getDateStr(time, "yyyy-MM-dd");
		String[] startDateArray = startDate.split("-");
		String[] currentDateStrArray = getDateStr(System.currentTimeMillis(), "yyyy-MM-dd").split("-");
		//开始时间
		int sY = Integer.parseInt(startDateArray[0]);
		int sM = Integer.parseInt(startDateArray[1]);
		int sD = Integer.parseInt(startDateArray[2]);
		//结束时间
		int eY = Integer.parseInt(currentDateStrArray[0]);
		int eM = Integer.parseInt(currentDateStrArray[1]);
		int eD = Integer.parseInt(currentDateStrArray[2]);

		int monthCount;
		if(eD < sD){
			eM--;
		}

		monthCount = (eY - sY) * 12 + eM - sM;
		int yearCount = 0;
		if(monthCount < 0) {
			monthCount = 0;
		}
		if(monthCount < 12){
			return monthCount + "月";
		}else {
			yearCount = monthCount / 12;
			return yearCount + "岁";
		}
	}

	/**
	 *
	 * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
	 * @author fy.zhang
	 */
	public static String getResidue(long mss) {
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		String residueTime;
		if(days != 0) {
			residueTime = days + "天";
		} else {
			if(hours != 0) {
				residueTime = hours + "小时";
			} else {
				if(minutes != 0) {
					residueTime =  minutes + "分钟";
				} else {
					residueTime =  seconds + "秒";
				}
			}
		}
		return residueTime;
	}

    /** 
     *  
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang 
     */ 
    public static String getResidueTime(long mss) {
    	long days = mss / (1000 * 60 * 60 * 24);  
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);  
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);  
        long seconds = (mss % (1000 * 60)) / 1000;
		String residueTime;
		if(days != 0) {
			residueTime = days + "天" + hours + "小时" + minutes + "分" + seconds + "秒";
		} else {
			if(hours != 0) {
				residueTime = hours + "小时" + minutes + "分" + seconds + "秒";
			} else {
				if(minutes != 0) {
					residueTime =  minutes + "分" + seconds + "秒";
				} else {
					residueTime =  seconds + "秒";
				}
			}
		}
		return residueTime;
    }
    
    public static String getYearMonthDayFolder(String folderType) {
    	String uid = String.valueOf(CommonPreference.getUserId());
    	Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		String picKey = MyConstants.OSS_FOLDER_BUCKET + "/" + uid + folderType + year + "/" + month + "/" + day + "/";
		return picKey;
    }

    public static String getOSSVideoFolder(String folderType) {
    	String uid = String.valueOf(CommonPreference.getUserId());
    	Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		String picKey = folderType + uid + "/" + year + "/" + month + "/" + day + "/";
		return picKey;
    }

	public static int daysOfTwo(Date originalDate, Date compareDateDate) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(originalDate);
		int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(compareDateDate);
		int compareDay = aCalendar.get(Calendar.DAY_OF_YEAR);

		return originalDay - compareDay;
	}

	public static String systemDate(long timeMillis){
		Date compareDate = getDate(getDateStr(timeMillis, "yyyy-MM-dd"), "yyyy-MM-dd");
		Date nowDate = new Date();
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(nowDate);
		int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(compareDate);
		int compareDay = aCalendar.get(Calendar.DAY_OF_YEAR);
		int dayDiff = originalDay - compareDay;
		if (dayDiff <= 0)
			return getHourMinute(timeMillis);
		else
			return new SimpleDateFormat("MM月dd日").format(compareDate);
	}
	public static String FriendlyDate(String time) {
		Date compareDate = getDate(time, "yyyy-MM-dd");
		Date nowDate = new Date();
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(nowDate);
		int originalDay = aCalendar.get(Calendar.DAY_OF_YEAR);
		aCalendar.setTime(compareDate);
		int compareDay = aCalendar.get(Calendar.DAY_OF_YEAR);
		int dayDiff = originalDay - compareDay;
		if (dayDiff <= 0)
			return time;
		else if (dayDiff == 1)
			return "昨日";
		else if (dayDiff == 2)
			return "前日";
		else
			return new SimpleDateFormat("M月d日 E").format(compareDate);
	}

	private static String getWeek(long timeStamp) {
		int mydate = 0;
		String week = null;
		Calendar cd = Calendar.getInstance();
		cd.setTime(new Date(timeStamp));
		mydate = cd.get(Calendar.DAY_OF_WEEK);
		// 获取指定日期转换成星期几
		if (mydate == 1) {
			week = "周日";
		} else if (mydate == 2) {
			week = "周一";
		} else if (mydate == 3) {
			week = "周二";
		} else if (mydate == 4) {
			week = "周三";
		} else if (mydate == 5) {
			week = "周四";
		} else if (mydate == 6) {
			week = "周五";
		} else if (mydate == 7) {
			week = "周六";
		}
		return week;
	}
	/**
	 * 输入时间戳变星期
	 *
	 * @param time
	 * @return
	 */
	public static String changeWeek(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
		long lcc = Long.valueOf(time);
		int i = Integer.parseInt(time);
		String times = sdr.format(new Date(i * 1000L));
		Date date = null;
		int mydate = 0;
		String week = null;
		try {
			date = sdr.parse(times);
			Calendar cd = Calendar.getInstance();
			cd.setTime(date);
			mydate = cd.get(Calendar.DAY_OF_WEEK);
			// 获取指定日期转换成星期几
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (mydate == 1) {
			week = "星期日";
		} else if (mydate == 2) {
			week = "星期一";
		} else if (mydate == 3) {
			week = "星期二";
		} else if (mydate == 4) {
			week = "星期三";
		} else if (mydate == 5) {
			week = "星期四";
		} else if (mydate == 6) {
			week = "星期五";
		} else if (mydate == 7) {
			week = "星期六";
		}
		return week;

	}
	public static String getDistance(long timeStamp) {
		long curTime =System.currentTimeMillis() / (long) 1000 ;
		long time = curTime - timeStamp;
		if(time < 3600 * 24){
			return  getHourMinute(timeStamp);
		}else if (time >= 3600 * 24 && time < 3600 * 24 * 2) {
			return "昨天";
		} else if (time >= 3600 * 24 * 2 && time < 3600 * 24 * 3) {
			 return "前天";
		} else {
			return getMonthDay(timeStamp);
		}
	}

	public static long getMidnightInMillis(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
