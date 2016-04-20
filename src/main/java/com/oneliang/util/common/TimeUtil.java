package com.oneliang.util.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * com.tjsoft.lwx.util.common.TimeUtil.java
 * @author Dandelion
 * This class is all static method to support the current date,time,month and so on 
 * @since 2008-08-09
 */
public final class TimeUtil{

	private TimeUtil(){}

	public static final String YEAR_MONTH="yyyy-MM";//year month
	public static final String YEAR_MONTH_DAY="yyyy-MM-dd";//year-month-day
	public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND="yyyy-MM-dd HH:mm:ss";//common detail time for dataBase
	public static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND="yyyy-MM-dd HH:mm:ss,SSS";//detail time for log
	public static final String DEFAULT_DATE_FORMAT="EEE MMM dd HH:mm:ss zzz yyyy";//EEE MMM dd HH:mm:ss zzz yyyy

	/**
	 * format date to string format:yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return String
	 */
	public static String dateToString(final Date date){
		return dateToString(date,YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
	}

	/**
	 * format date to string
	 * @param date
	 * @param format
	 * @return String
	 */
	public static String dateToString(final Date date,final String format){
		return dateToString(date,format,null);
	}

	/**
	 * format date to string
	 * @param date
	 * @param format
	 * @param locale
	 * @return String
	 */
	public static String dateToString(final Date date,final String format,Locale locale){
		SimpleDateFormat sdf=new SimpleDateFormat(format,locale==null?Locale.getDefault():locale);
		return sdf.format(date);
	}

	/**
	 * parse the string to date format:yyyy-MM-dd HH:mm:ss
	 * @param string
	 * @return Date
	 */
	public static Date stringToDate(final String string){
		return stringToDate(string,YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
	}

	/**
	 * parse the string to date
	 * @param string
	 * @param format
	 * @return Date
	 */
	public static Date stringToDate(final String string,final String format){
		return stringToDate(string,format,null);
	}

	/**
	 * parse the string to date
	 * @param string
	 * @param format
	 * @param locale
	 * @return Date
	 */
	public static Date stringToDate(final String string,final String format,final Locale locale){
		Date date=null;
		SimpleDateFormat sdf=new SimpleDateFormat(format,locale==null?Locale.getDefault():locale);
		try{
			date=sdf.parse(string);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
		return date;
	}

	/**
	 * format the current time
	 * @param format
	 * @return String
	 */
	public static String formatCurrentTime(final String format){
		return dateToString(getTime(),format);
	}

	/**
	 * <p>Method: get current day[year-month-day] type string</p>
	 * @return String
	 */
	public static String getCurrentDay(){
		return formatCurrentTime(YEAR_MONTH_DAY);
	}

	/**
	 * <p>Method: get current time[year-month-day hour-minute-second-millsecond] type string</p>
	 * @return String
	 */
	public static String getCurrentMillisTime(){
		return formatCurrentTime(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND);
	}

	/**
	 * <p>Method: get current time[year-month-day hour-minute-second] type string</p>
	 * @return String
	 */
	public static String getCurrentSecondTime(){
		return formatCurrentTime(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
	}

	/**
	 * <p>Method: get current year and month type string</p>
	 * @return String
	 */
	public static String getCurrentYearMonth(){
		return formatCurrentTime(YEAR_MONTH);
	}

	/**
	 * <p>Method: get current date type Date</p>
	 * @return java.util.Date
	 */
	public static Date getTime(){
		Calendar calendar=Calendar.getInstance(Locale.getDefault());
		return calendar.getTime();
	}

	/**
	 * <p>Method: get current date type Date</p>
	 * @param locale
	 * @return java.util.Date
	 */
	public static Date getTime(Locale locale){
		if(locale==null){
			locale=Locale.getDefault();
		}
		Calendar calendar=Calendar.getInstance(locale);
		return calendar.getTime();
	}

	/**
	 * <p>Method: get current timestamp type Timestamp,use for dataBase</p>
	 * @return java.sql.Timestamp
	 */
	public static Timestamp getCurrentTimestamp(){
		return (new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * <p>Method: get current day of week in Arabia return type int</p>
	 * @return String
	 */
	public static int getCurrentDayOfWeekArabia(){
		Calendar calendar=Calendar.getInstance(Locale.getDefault());
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * <p>Method: get current day of week in Chinese return type String</p>
	 * @return String
	 */
	public static String getCurrentDayOfWeekChinese(){
		String[] dayOfWeek={"日","一","二","三","四","五","六"};
		return dayOfWeek[getCurrentDayOfWeekArabia()-1];
	}

	/**
	 * <p>Method: get current day of week in English return type String</p>
	 * @return String
	 */
	public static String getCurrentDayOfWeekEnglish(){
		String[] dayOfWeek={"Sunday","Monday","Tuseday","Wednesday","Thursday","Friday","Saturday"};
		return dayOfWeek[getCurrentDayOfWeekArabia()-1];
	}

	/**
	 * use time generate the id
	 * @return String
	 */
	public static String getMillisId(){
		return formatCurrentTime("yyyyMMddHHmmssSSS");
	}

	/**
	 * time millis to date
	 * @param timeMillis
	 * @return Date
	 */
	public static Date timeMillisToDate(final long timeMillis){
		return new Date(timeMillis);
	}

	public static void main(String[] args){
		System.out.println(getCurrentDayOfWeekArabia());
		System.out.println(getCurrentDayOfWeekChinese());
		System.out.println(System.currentTimeMillis());
		System.out.println(getCurrentSecondTime());
		System.out.println(getMillisId());
		System.out.println(dateToString(new Date(),"yyyy-MM-dd"));
		System.out.println(timeMillisToDate(Long.parseLong("1291883095078")));
	}
}
