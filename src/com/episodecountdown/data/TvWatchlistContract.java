package com.episodecountdown.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TvWatchlistContract {
	
	// A convenient String?? Can be anything? 
	public static final String CONTENT_AUTHORITY = "com.episodecountdown.app";
	
	//Base Uri
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	//Paths for appending to Uri
	public static final String PATH_TRENDING = "trending";
	public static final String PATH_WATCHLIST = "watchlist";
	public static final String PATH_EPISODES = "episodes";

	// Inner class defining table contents.
	// Implementing BaseColumns mean _ID column is implicit
	public static final class TrendingEntry implements BaseColumns {
		
		public static final Uri CONTENT_URI = 
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRENDING).build();
		
		//.dir indicates returns directory/list of items.
		public static final String CONTENT_TYPE = 
						"vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TRENDING;
				
		public static final String CONTENT_ITEM_TYPE = 
						"vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +PATH_TRENDING;
		
		public static final String TABLE_NAME = "trending";
		//Position of the show in Array
		public static final String COLUMN_POSITION = "position";
		//Title of the show
		public static final String COLUMN_TITLE = "title";
		//Year as integer
		public static final String COLUMN_YEAR = "year";
		//
		public static final String COLUMN_TRAKT_URL = "url";
		//
		public static final String COLUMN_FIRST_AIRED = "first_aired";
		//
		public static final String COLUMN_COUNTRY = "country";
		//
		public static final String COLUMN_OVERVIEW = "overview";
		//
		public static final String COLUMN_RUNTIME = "runtime";
		//
		public static final String COLUMN_STATUS = "status";
		//
		public static final String COLUMN_NETWORK = "network";
		//
		public static final String COLUMN_AIR_DAY = "air_day";
		//
		public static final String COLUMN_AIR_TIME = "air_time";
		//
		public static final String COLUMN_CERTIFICATION = "certification";
		//
		public static final String COLUMN_IMDB_ID = "imdb_id";
		//
		public static final String COLUMN_TVDB_ID = "tvdb_id";
		//
		public static final String COLUMN_TVRAGE_ID = "tvrage_id";
		//
		public static final String COLUMN_POSTER = "poster";
		//
		public static final String COLUMN_BANNER = "banner";
		//
		public static final String COLUMN_FANART = "fanart";
		//
		public static final String COLUMN_RATINGS_PERCENTAGE = "percentage";
		//
		public static final String COLUMN_RATINGS_VOTES = "votes";
		//
		public static final String COLUMN_RATINGS_LOVED = "loved";
		//
		public static final String COLUMN_RATINGS_HATED = "hated";
		// NOTE: Returned as an array from API. 
		public static final String COLUMN_GENRES = "genres";
		
		//URI builder and decoder functions.  
		public static Uri buildTrendingUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI,  id);
		}
	}
	
	public static final class WatchlistEntry implements BaseColumns {
		
		public static final Uri CONTENT_URI = 
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_WATCHLIST).build();
		
		//.dir indicates returns directory/list of items.
		public static final String CONTENT_TYPE = 
						"vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WATCHLIST;
				
		public static final String CONTENT_ITEM_TYPE = 
						"vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +PATH_WATCHLIST;
		
		public static final String TABLE_NAME = "watchlist";
		//Title of the show
		public static final String COLUMN_TITLE = "title";
		//Year as integer
		public static final String COLUMN_YEAR = "year";
		//
		public static final String COLUMN_COUNTRY = "country";
		//
		public static final String COLUMN_OVERVIEW = "overview";
		//
		public static final String COLUMN_RUNTIME = "runtime";
		//
		public static final String COLUMN_STATUS = "status";
		//
		public static final String COLUMN_NETWORK = "network";
		//
		public static final String COLUMN_FIRST_AIRED = "first_aired";
		//
		public static final String COLUMN_AIR_DAY = "air_day";
		//
		public static final String COLUMN_AIR_TIME = "air_time";
		//
		public static final String COLUMN_AIR_DAY_UTC = "air_day_utc";
		//
		public static final String COLUMN_AIR_TIME_UTC = "air_time_utc";
		//
		public static final String COLUMN_CERTIFICATION = "certification";
		//
		public static final String COLUMN_TVDB_ID = "tvdb_id";
		//
		public static final String COLUMN_TVRAGE_ID = "tvrage_id";
		//
		public static final String COLUMN_POSTER = "poster";
		//
		public static final String COLUMN_BANNER = "banner";
		//
		public static final String COLUMN_RATINGS_PERCENTAGE = "percentage";
		//
		public static final String COLUMN_RATINGS_VOTES = "votes";
		//
		public static final String COLUMN_RATINGS_LOVED = "loved";
		//
		public static final String COLUMN_RATINGS_HATED = "hated";
		// 
		public static final String COLUMN_NEXT_EPISODE_DATE = "next_episode_date";
		// 
		public static final String COLUMN_SHOW_TIMEZONE = "timezone";
		// NOTE: Returned as an array from API. 
		public static final String COLUMN_GENRES = "genres";
				
		//URI builder and decoder functions.  
		public static Uri buildWatchlistUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI,  id);
		}
		
//		public static Uri buildWatchlistTvrageIdUri(int tvrage_id) {
//			return CONTENT_URI.buildUpon().appendPath(tvrage_id+"").build();
//		}
//		
//		//Helper Functions
//		public static String getTvrageIDFromUri(Uri uri) {
//			return uri.getPathSegments().get(1);
//		}
	}
	
public static final class EpisodesEntry implements BaseColumns {
		
		public static final Uri CONTENT_URI = 
				BASE_CONTENT_URI.buildUpon().appendPath(PATH_EPISODES).build();
		
		//.dir indicates returns directory/list of items.
		public static final String CONTENT_TYPE = 
						"vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EPISODES;
				
		public static final String CONTENT_ITEM_TYPE = 
						"vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +PATH_EPISODES;
		
		public static final String TABLE_NAME = "episodes";
		//Title of the show
		public static final String COLUMN_TITLE = "title";
		//DEPRICATED. DONT USE THIS. USE THE ONE WITH INTEGER
		public static final String COLUMN_EPISODE_NO_FROM_START = "epnum";
		//
		public static final String COLUMN_EPISODE_NO_FROM_START_INTEGER = "epnum_integer";
		//
		public static final String COLUMN_EPISODE_NO = "seasonnum";
		// number of milliseconds since Jan 1, 1970 GMT
		public static final String COLUMN_AIRDATE = "airdate";
		//
		public static final String COLUMN_SCREENCAP = "screencap";
		// Column with foreign key into watchlist table. 
		public static final String COLUMN_TVRAGE_ID = "tvrage_id";
		// 
		public static final String COLUMN_SEASON_NO = "seasonno";
						
		//URI builder and decoder functions.  
		public static Uri buildEpisodesUri(long id) {
			return ContentUris.withAppendedId(CONTENT_URI,  id);
		}
		
		public static Uri buildEpisodeWatchlist(int tvrage_id){
			return CONTENT_URI.buildUpon().appendPath(tvrage_id + "").build();
		}
		
		//Query Parameters, in this case parameter is for join between the two tables.
		//Take care of date format!!!!
		public static Uri buildEpisodeWatchlistWithStartDate(
				int tvrage_id, String startDate) {
			return CONTENT_URI.buildUpon().appendPath(tvrage_id+"").appendQueryParameter(COLUMN_AIRDATE, startDate).build();
		}
		
		public static Uri buildEpisodeWatchlistWithDate(int tvrage_id, String date) {
			return CONTENT_URI.buildUpon().appendPath(tvrage_id+"").appendPath(date).build();
		}
		
		//Helper Functions
		public static String getTvrageIDFromUri(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static String getDateFromUri(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		
		public static String getStartDateFromUri(Uri uri) {
			return uri.getQueryParameter(COLUMN_AIRDATE);
		}
	}
}