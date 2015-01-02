package com.episodecountdown;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchResultModel implements Parcelable {

	private String title;
	private String posterUrl;
	private String bannerUrl;
	private String traktUrl;
	private String oveview;
	private String network;
	private String airDay;
	private String airTime;
	private String certification;
	private String country;
	private String ratting_percentage;
	private String loved;
	private String hated;
	private String genres;
	private int year;
	private int firstAired;
	private int runtime;
	private int tvRageId;
	private int tvdbId;
	private boolean ended;
	
	public String getGenres() {
		return genres;
	}

	public void setGenres(String genres) {
		this.genres = genres;
	}

	public String getRatting_percentage() {
		return ratting_percentage;
	}

	public void setRatting_percentage(String ratting_percentage) {
		this.ratting_percentage = ratting_percentage;
	}

	public String getLoved() {
		return loved;
	}

	public void setLoved(String loved) {
		this.loved = loved;
	}

	public String getHated() {
		return hated;
	}

	public void setHated(String hated) {
		this.hated = hated;
	}

	public String getBannerUrl() {
		return bannerUrl;
	}

	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}

	public int getTvRageId() {
		return tvRageId;
	}

	public void setTvRageId(int tvRageId) {
		this.tvRageId = tvRageId;
	}

	public int getTvdbId() {
		return tvdbId;
	}

	public void setTvdbId(int tvdbId) {
		this.tvdbId = tvdbId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPosterUrl() {
		return posterUrl;
	}

	public void setPosterUrl(String posterUrl) {
		this.posterUrl = posterUrl;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getTraktUrl() {
		return traktUrl;
	}

	public void setTraktUrl(String traktUrl) {
		this.traktUrl = traktUrl;
	}

	public int getFirstAired() {
		return firstAired;
	}

	public void setFirstAired(int firstAired) {
		this.firstAired = firstAired;
	}

	public String getOveview() {
		return oveview;
	}

	public void setOveview(String oveview) {
		this.oveview = oveview;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getAirDay() {
		return airDay;
	}

	public void setAirDay(String airDay) {
		this.airDay = airDay;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getAirTime() {
		return airTime;
	}

	public void setAirTime(String airTime) {
		this.airTime = airTime;
	}

	public int getRuntime() {
		return runtime;
	}

	public void setRuntime(int runtime) {
		this.runtime = runtime;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean getEnded() {
		return ended;
	}

	public void setEnded(boolean ended) {
		this.ended = ended;
	}
	
	//Empty Construcitor
	public SearchResultModel(){
		
	}

	// Methods for implementing Parcelable
	public SearchResultModel(Parcel in) {
		this.title = in.readString();
		this.airDay = in.readString();
		this.airTime = in.readString();
		this.bannerUrl = in.readString();
		this.certification = in.readString();
		this.country = in.readString();
		this.network = in.readString();
		this.oveview = in.readString();
		this.posterUrl = in.readString();
		this.traktUrl = in.readString();
		this.ratting_percentage = in.readString();
		this.loved = in.readString();
		this.hated = in.readString();
		this.genres = in.readString();
		this.firstAired = in.readInt();
		this.runtime = in.readInt();
		this.tvRageId = in.readInt();
		this.tvdbId = in.readInt();
		this.year = in.readInt();
		this.ended = in.readByte() != 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(airDay);
		dest.writeString(airTime);
		dest.writeString(bannerUrl);
		dest.writeString(certification);
		dest.writeString(country);
		dest.writeString(network);
		dest.writeString(oveview);
		dest.writeString(posterUrl);
		dest.writeString(traktUrl);
		dest.writeString(ratting_percentage);
		dest.writeString(loved);
		dest.writeString(hated);
		dest.writeString(genres);
		dest.writeInt(firstAired);
		dest.writeInt(runtime);
		dest.writeInt(tvRageId);
		dest.writeInt(tvdbId);
		dest.writeInt(year);
		dest.writeByte((byte) (ended ? 1 : 0));
	}

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<SearchResultModel> CREATOR = new Parcelable.Creator<SearchResultModel>() {

		public SearchResultModel createFromParcel(Parcel in) {
			return new SearchResultModel(in);
		}

		public SearchResultModel[] newArray(int size) {
			return new SearchResultModel[size];
		}
	};

}
