package com.episodecountdown.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.episodecountdown.data.TvWatchlistContract.EpisodesEntry;
import com.episodecountdown.data.TvWatchlistContract.TrendingEntry;
import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;

public class TvWatchlistDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 10;
	public static String DATABASE_NAME = "episodecountdown.db";

	public TvWatchlistDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String SQL_CREATE_TRENDING_TABLE = "CREATE TABLE "
				+ TrendingEntry.TABLE_NAME + " (" + TrendingEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ TrendingEntry.COLUMN_POSITION + " INTEGER NOT NULL, "
				+ TrendingEntry.COLUMN_TITLE + " TEXT NOT NULL, "
				+ TrendingEntry.COLUMN_YEAR + " INTEGER, "
				+ TrendingEntry.COLUMN_TRAKT_URL + " TEXT, "
				+ TrendingEntry.COLUMN_FIRST_AIRED + " INTEGER, "
				+ TrendingEntry.COLUMN_COUNTRY + " TEXT, "
				+ TrendingEntry.COLUMN_OVERVIEW + " TEXT, "
				+ TrendingEntry.COLUMN_RUNTIME + " INTEGER, "
				+ TrendingEntry.COLUMN_STATUS + " TEXT, "
				+ TrendingEntry.COLUMN_NETWORK + " TEXT, "
				+ TrendingEntry.COLUMN_AIR_DAY + " TEXT, "
				+ TrendingEntry.COLUMN_AIR_TIME + " TEXT, "
				+ TrendingEntry.COLUMN_CERTIFICATION + " TEXT, "
				+ TrendingEntry.COLUMN_IMDB_ID + " TEXT, "
				+ TrendingEntry.COLUMN_TVDB_ID + " TEXT, "
				+ TrendingEntry.COLUMN_TVRAGE_ID + " INTEGER, "
				+ TrendingEntry.COLUMN_POSTER + " TEXT, "
				+ TrendingEntry.COLUMN_FANART + " TEXT, "
				+ TrendingEntry.COLUMN_BANNER + " TEXT, "
				+ TrendingEntry.COLUMN_RATINGS_PERCENTAGE + " INTEGER, "
				+ TrendingEntry.COLUMN_RATINGS_VOTES + " INTEGER, "
				+ TrendingEntry.COLUMN_RATINGS_LOVED + " INTEGER, "
				+ TrendingEntry.COLUMN_RATINGS_HATED + " INTEGER, "
				+ TrendingEntry.COLUMN_GENRES + " TEXT, "

				+ " UNIQUE (" + TrendingEntry.COLUMN_POSITION + ") ON CONFLICT REPLACE);";
		
		final String SQL_CREATE_WATCHLIST_TABLE = "CREATE TABLE "
				+ WatchlistEntry.TABLE_NAME + " (" + WatchlistEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ WatchlistEntry.COLUMN_TITLE + " TEXT NOT NULL, "
				+ WatchlistEntry.COLUMN_YEAR + " INTEGER, "
				+ WatchlistEntry.COLUMN_COUNTRY + " TEXT, "
				+ WatchlistEntry.COLUMN_OVERVIEW + " TEXT, "
				+ WatchlistEntry.COLUMN_RUNTIME + " INTEGER, "
				+ WatchlistEntry.COLUMN_STATUS + " TEXT, "
				+ WatchlistEntry.COLUMN_FIRST_AIRED + " INTEGER, "
				+ WatchlistEntry.COLUMN_NETWORK + " TEXT, "
				+ WatchlistEntry.COLUMN_AIR_DAY + " TEXT, "
				+ WatchlistEntry.COLUMN_AIR_TIME + " TEXT, "
				+ WatchlistEntry.COLUMN_AIR_DAY_UTC + " TEXT, "
				+ WatchlistEntry.COLUMN_AIR_TIME_UTC + " TEXT, "
				+ WatchlistEntry.COLUMN_CERTIFICATION + " TEXT, "
				+ WatchlistEntry.COLUMN_TVDB_ID + " TEXT, "
				+ WatchlistEntry.COLUMN_TVRAGE_ID + " INTEGER, "
				+ WatchlistEntry.COLUMN_POSTER + " TEXT, "
				+ WatchlistEntry.COLUMN_BANNER + " TEXT, "
				+ WatchlistEntry.COLUMN_RATINGS_PERCENTAGE + " INTEGER, "
				+ WatchlistEntry.COLUMN_RATINGS_VOTES + " INTEGER, "
				+ WatchlistEntry.COLUMN_RATINGS_LOVED + " INTEGER, "
				+ WatchlistEntry.COLUMN_RATINGS_HATED + " INTEGER, "
				+ WatchlistEntry.COLUMN_NEXT_EPISODE_DATE + " INTEGER, "
				+ WatchlistEntry.COLUMN_SHOW_TIMEZONE + " TEXT, "
				+ WatchlistEntry.COLUMN_GENRES + " TEXT, "

				+ " UNIQUE (" + WatchlistEntry.COLUMN_TVDB_ID + ") ON CONFLICT REPLACE);";
		
		final String SQL_CREATE_EPISODES_TABLE = "CREATE TABLE "
				+ EpisodesEntry.TABLE_NAME + " (" + EpisodesEntry._ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ EpisodesEntry.COLUMN_TITLE + " TEXT, "
				+ EpisodesEntry.COLUMN_EPISODE_NO_FROM_START + " TEXT, "
				+ EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER + " INTEGER, "
				+ EpisodesEntry.COLUMN_AIRDATE + " INTEGER, "
				+ EpisodesEntry.COLUMN_EPISODE_NO + " INTEGER, "
				+ EpisodesEntry.COLUMN_SEASON_NO + " INTEGER, "
				+ EpisodesEntry.COLUMN_SCREENCAP + " TEXT, "
				+ EpisodesEntry.COLUMN_TVRAGE_ID + " INTEGER, "

				+ " FOREIGN KEY (" + EpisodesEntry.COLUMN_TVRAGE_ID + ") REFERENCES " +
				WatchlistEntry.TABLE_NAME + "(" + WatchlistEntry.COLUMN_TVRAGE_ID + "), "
				
				+ " UNIQUE (" + EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER + ", " +
				EpisodesEntry.COLUMN_TVRAGE_ID + ") ON CONFLICT REPLACE);";

		db.execSQL(SQL_CREATE_TRENDING_TABLE);
		db.execSQL(SQL_CREATE_WATCHLIST_TABLE);
		db.execSQL(SQL_CREATE_EPISODES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// onUpgrade only fires when we change database version
		// Dropping table and creating a new one. 
		// WARNING: CZ no user data here! Just some cache!
		
		switch (oldVersion) {
		case 7:
		case 8:
			db.execSQL("ALTER TABLE " + EpisodesEntry.TABLE_NAME + " ADD COLUMN " +EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER + " INTEGER");
		case 9:
			db.execSQL("UPDATE " + EpisodesEntry.TABLE_NAME + " SET " + EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER + " = CAST(" + EpisodesEntry.COLUMN_EPISODE_NO_FROM_START + " AS INTEGER)");
			break;

		default:
			
//			db.execSQL("DROP TABLE IF EXISTS " + TrendingEntry.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS " + WatchlistEntry.TABLE_NAME);
//			db.execSQL("DROP TABLE IF EXISTS " + EpisodesEntry.TABLE_NAME);
//			onCreate(db);

			break;
		}
		
	}
}