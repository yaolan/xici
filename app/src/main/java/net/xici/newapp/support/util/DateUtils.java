package net.xici.newapp.support.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.xici.newapp.R;
import net.xici.newapp.app.XiciApp;


import android.text.TextUtils;


public class DateUtils {
	
	public static SimpleDateFormat FORMAT_DEFAULT_ALL = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss", Locale.CHINA);
	public static SimpleDateFormat FORMAT_MM_SS = new SimpleDateFormat("MM月dd日 hh:mm:ss", Locale.CHINA);
	/**
	 * format:yyyy年MM月dd日 hh:mm
	 */
	public static SimpleDateFormat FORMAT_DEFAULT = new SimpleDateFormat("yyyy年MM月dd日 hh:mm", Locale.CHINA);
	/**
	 * format:yyyy年MM月
	 */
	public static SimpleDateFormat FORMAT_YY_MM = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
	
	public static SimpleDateFormat FORMAT_SEND_SMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static SimpleDateFormat FORMAT_FEED = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String nowToString(SimpleDateFormat format) {
		if(format == null) {
			return null;
		}
		return format.format(new Date());
	}
	
	public static String nowToStringYYMM() {
		return nowToString(FORMAT_YY_MM);
	}
	
	public static String getSmsMonthRequestFormat(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		month++;
		return String.format("%d%02d", year, month);
	}
	
	public static String getSmsMonthShowFormat(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		month++;
		return String.format("%d - %d月", year, month);
	}
	
	public static String getRelativeDate(String str) {
		if(TextUtils.isEmpty(str))
			return "";
		try {
			Date date = FORMAT_FEED.parse(str);
			return getRelativeDate(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(str.endsWith(".0")) {
			return str.substring(0, str.length() - 2);
		}
		return str;
	}
	
	public static String getRelativeDate(Date date) {
		Date now = new Date();
		XiciApp app = XiciApp.getInstance();
        String sec = app.getString(R.string.created_at_beautify_sec);
        String min = app.getString(R.string.created_at_beautify_min);
        String hour = app.getString(R.string.created_at_beautify_hour);
        String day = app.getString(R.string.created_at_beautify_day);
        String suffix = app.getString(R.string.created_at_beautify_suffix);
        
        // seconds 
        long diff = (now.getTime() - date.getTime()) / 1000;
        
        if(diff < 0) {
        	diff = 0;
        }
        
        if(diff < 60)
        	return diff + sec + suffix;
        
        // minutes
        diff /= 60;
        if(diff < 60)
        	return diff + min + suffix;
        
        // hours
        diff /= 60;
        if(diff < 24)
        	return diff + hour + suffix;
        
        // days
        diff /= 24;
        if(diff < 7)
        	return diff + day + suffix;
        
        diff /= 365;
        if(diff < 1)
        	return	FORMAT_MM_SS.format(date);
        
        return FORMAT_DEFAULT_ALL.format(date);
	}
	
	public static boolean timeEanble(String starttime,String endtime){
		Date now = new Date();
		try {
			Date start = FORMAT_FEED.parse(starttime);
			if(start.after(now)){
				return false;
			}
			
			Date end = FORMAT_FEED.parse(endtime);
			if(end.before(now)){
				return false;
			}
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getNowTime(){
		return FORMAT_FEED.format(new Date());
	}
}
