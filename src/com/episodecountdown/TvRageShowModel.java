package com.episodecountdown;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class TvRageShowModel {

	// Things to save
	private String name;
	private String started;
	private String ended;
	private String origin_country;
	private String status;
	private String network;
	private String airtime;
	private String airday;
	private String timezone;
	private int totalseasons;
	private int showid;
	private int runtime;
	private TvRageEpisodeModel lastEpisode;
	private TvRageEpisodeModel nextEpisode;
	private ArrayList<String> genres;
	private ArrayList<TvRageEpisodeModel> episodeList;

	// Getters and setters
	public ArrayList<String> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<String> genres) {
		this.genres = genres;
	}

	public TvRageEpisodeModel getLastEpisode() {
		return lastEpisode;
	}

	public void setLastEpisode(TvRageEpisodeModel lastEpisode) {
		this.lastEpisode = lastEpisode;
	}

	public void setNextEpisode(TvRageEpisodeModel nextEpisode) {
		this.nextEpisode = nextEpisode;
	}

	public TvRageEpisodeModel getNextEpisode() {
		return nextEpisode;
	}

	public ArrayList<TvRageEpisodeModel> getEpisodeList() {
		return episodeList;
	}

	public void setEpisodeList(ArrayList<TvRageEpisodeModel> episodeList) {
		this.episodeList = episodeList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStarted() {
		return started;
	}

	public void setStarted(String started) {
		this.started = started;
	}

	public String getEnded() {
		return ended;
	}

	public void setEnded(String ended) {
		this.ended = ended;
	}

	public String getOrigin_country() {
		return origin_country;
	}

	public void setOrigin_country(String origin_country) {
		this.origin_country = origin_country;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getAirtime() {
		return airtime;
	}

	public void setAirtime(String airtime) {
		this.airtime = airtime;
	}

	public String getAirday() {
		return airday;
	}

	public void setAirday(String airday) {
		this.airday = airday;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getTotalseasons() {
		return totalseasons;
	}

	public void setTotalseasons(int totalseasons) {
		this.totalseasons = totalseasons;
	}

	public int getShowid() {
		return showid;
	}

	public void setShowid(int showid) {
		this.showid = showid;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	// Current and Next Episodes Calculation. RETURNS time to next episode in
	// long or -1 on error
	public long setLastAndNextEpisode() {
		// find next and current episode info in episodeList
		this.setLastEpisode(null);
		this.setNextEpisode(null);
		if (this.episodeList == null) {
			return -1;
		}

		Iterator<TvRageEpisodeModel> iterator = this.episodeList.iterator();

		// Calculating next, last using Calendar
		SimpleDateFormat sdfAirTimeFormat = new SimpleDateFormat("hh:mm",
				Locale.US);
		TimeZone showTimeZone = TimeZone.getTimeZone(this.getTimezone());
		// Gets the correct timezone. CHECKED
		sdfAirTimeFormat.setTimeZone(showTimeZone);
		Calendar calAirTimeTemp = new GregorianCalendar(showTimeZone, Locale.US);
		try {
			calAirTimeTemp.setTime(sdfAirTimeFormat.parse(this.getAirtime()));
			// Timezone now is still showtimezone, day,month,year, reset.. understandable
		} catch (ParseException e) {
			return -1;
		} catch (NullPointerException e) {
			return -1;
		}

		while (iterator.hasNext()) {
			// Get Next
			TvRageEpisodeModel episodeInfo = iterator.next();

			// construct calendar object with correct time zone
			Calendar calAirTime = new GregorianCalendar(showTimeZone, Locale.US);
			calAirTime.setTime(episodeInfo.getAirDate());
			calAirTime.set(Calendar.HOUR_OF_DAY,calAirTimeTemp.get(Calendar.HOUR_OF_DAY));
			calAirTime.set(Calendar.MINUTE, calAirTimeTemp.get(Calendar.MINUTE));
			calAirTime.set(Calendar.SECOND, 0);
			calAirTime.set(Calendar.MILLISECOND, 0);
			// Sets hours without touching anything else. Everything set here
			
			Calendar nowCalendar = Calendar.getInstance(showTimeZone);
			// now time in newyork timezone correct
			
			if (calAirTime.compareTo(nowCalendar) < 0) {
				this.setLastEpisode(episodeInfo);
			} else {
				this.setNextEpisode(episodeInfo);
				return calAirTime.getTimeInMillis()
						- nowCalendar.getTimeInMillis();
			}
		}
		// If reached here, then no next episode
		return -1;
	}
}
