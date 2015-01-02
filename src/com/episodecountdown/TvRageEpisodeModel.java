package com.episodecountdown;

import java.util.Date;

public class TvRageEpisodeModel {
	
	private String title;
	private String screenCap;
	private int season;
	private int episode;
	private int episodeFromStart;
	private int tvrage_id;
	private Date airDate;
	
	public String getScreenCap() {
		return screenCap;
	}
	public void setScreenCap(String screenCap) {
		this.screenCap = screenCap;
	}
	public int getEpisodeFromStart() {
		return episodeFromStart;
	}
	public void setEpisodeFromStart(int episodeFromStart) {
		this.episodeFromStart = episodeFromStart;
	}
	public int getTvrage_id() {
		return tvrage_id;
	}
	public void setTvrage_id(int tvrage_id) {
		this.tvrage_id = tvrage_id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getSeason() {
		return season;
	}
	public void setSeason(int season) {
		this.season = season;
	}
	public int getEpisode() {
		return episode;
	}
	public void setEpisode(int episode) {
		this.episode = episode;
	}
	public Date getAirDate() {
		return airDate;
	}
	public void setAirDate(Date airDate) {
		this.airDate = airDate;
	}
}
