package com.episodecountdown;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {
	
	public static final String EST_TIMEZONE = "America/New_York";
	public static final String LONDON_TIMEZONE = "Europe/London";
	public static final SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US);
	public static final SimpleDateFormat smallsdf = new SimpleDateFormat("MMM d, yyyy", Locale.US);
	
	
	public static String getReadableDateString(long time){
        // Because the API returns a Unix time stamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy" , Locale.US);
        return format.format(date).toString();
    }
	
	public static String getTvrageReadableDateString(String dateString, String showTimeZone){
		if(showTimeZone != null && !showTimeZone.trim().isEmpty())
			sdf.setTimeZone(TimeZone.getTimeZone(showTimeZone));
		return sdf.format(new Date(Long.parseLong(dateString)));
	}
	
	public static String getSmallTvrageReadableDateString(String dateString, String showTimeZone){
		if(showTimeZone != null && !showTimeZone.trim().isEmpty())
			smallsdf.setTimeZone(TimeZone.getTimeZone(showTimeZone));
		return smallsdf.format(new Date(Long.parseLong(dateString)));
	}
	
	public static String getSXXEXXFormatedString(String season, String episode){
		episode = (episode.length()==1)? "0"+episode:episode;
		season = (season.length()==1)? "0"+season:season;
		return "S" + season + "E" + episode;
    }
}
