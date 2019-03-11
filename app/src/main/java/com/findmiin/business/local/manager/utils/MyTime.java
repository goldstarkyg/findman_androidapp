package com.findmiin.business.local.manager.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class MyTime {
	public static String getCorrectTime(String time )
	{
		if( time == null )
			return getCurrentTime();
		
		if( time.length() > 19 )
			time = time.substring(0, 19);
		
		String yearPattern = "((19|20)\\d\\d)-";
		String monthPattern = "(0?[1-9]|1[012])-";
		String dayPattern = "(0?[1-9]|[12][0-9]|3[01]) ";
		String hourPattern = "(0?[0-9]|[1][0-9]|2[0-3]):";
		String minutePattern = "(0?[0-9]|[1-5][0-9]):";
		String secondePattern = "(0?[0-9]|[1-5][0-9])";
		
		String timePattern = yearPattern + monthPattern + dayPattern + hourPattern + minutePattern + secondePattern;
		
		if( CheckUtils.isEmpty(time) || time.matches(timePattern) == false )
		{
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			time = format.format(date);			
		}
		
		return time;
	}
	
	public static String getCurrentTime()
	{
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(date);
		
		return time;
	}
	
	public static String getCurrentDate()
	{
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String time = format.format(date);
		
		return time;
	}
	
	public static String getCurrentDateTimeForEnglish()
	{
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy HH:mm",  Locale.ENGLISH);
		String time = format.format(date);
		
		return time;
	}

	public static String getCurrentDateForEnglish()
	{
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy",  Locale.ENGLISH);
		String time = format.format(date);
		
		return time;
	}
	
	
	public static String getCurrentDateForEnglish(long timeMilli)
	{
		Date date = new Date(timeMilli);
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy",  Locale.ENGLISH);
		String time = format.format(date);
		
		return time;
	}
	
	public static String getCurrentDateTimeForEnglish(long timeMilli)
	{
		Date date = new Date(timeMilli);
		SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy HH:mm",  Locale.ENGLISH);
		String time = format.format(date);
		
		return time;
	}
	
	public static int getDateNumber(int year, int month){
		int iYear = 1999;
		int iMonth = Calendar.FEBRUARY;
		int iDay = 1;

		// Create a calendar object and set year and month
		Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

		// Get the number of days in that month
		int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
		
		return daysInMonth;
	}
	
	public static Date getYesterdayDate() {
		Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
	}
	
	public static String getTodayDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
	}
	
	public static String getYesterdayDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
	}
	
	public static String getOnlyTimePM(String datetime)
	{
		Date date = new Date(datetime);
		
		SimpleDateFormat format = new SimpleDateFormat("h:mm a");
		return format.format(date);
	}
	
	public static String getOnlyMonthDate(String datetime)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {  
		    Date date = format.parse(datetime);
			SimpleDateFormat chinalFormat = new SimpleDateFormat("MMM dd",  Locale.CHINA);
			return chinalFormat.format(date);
		}catch (ParseException e) {
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		}
		Date date = new Date();
		SimpleDateFormat chinalFormat = new SimpleDateFormat("MMM dd",  Locale.CHINA);
		return chinalFormat.format(date);
	}
	
	public static String getOnlyYear(String datetime)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {  
		    Date date = format.parse(datetime);
			SimpleDateFormat chinalFormat = new SimpleDateFormat("yyyy",  Locale.CHINA);
			return chinalFormat.format(date);
		}catch (ParseException e) {
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		}

		Date date = new Date();
		SimpleDateFormat chinalFormat = new SimpleDateFormat("yyyy",  Locale.CHINA);
		return chinalFormat.format(date);
	}
	
	public static String getChinaDate(String datetime)
	{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {  
		    Date date = format.parse(datetime);
			SimpleDateFormat chinaFormat = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
			return chinaFormat.format(date);
		}catch (ParseException e) {
		    // TODO Auto-generated catch block  
		    e.printStackTrace();  
		}
		
		return "";
	}
	
	public static String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }
	
	public static String getOnlyTime()
	{
		Date date = new Date();
		
		SimpleDateFormat format = new SimpleDateFormat("HH:MM");
		return format.format(date);
	}
}
