package com.episodecountdown.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class TvWatchlistProvider extends ContentProvider {

	//URI
	private static final int TREDING = 100;
	private static final int TRENDING_ID = 101;
	private static final int WATCHLIST = 200;
	private static final int WATCHLIST_ID = 201;
	//private static final int WATCHLIST_TVRAGE_ID = 202;
	private static final int EPISODES = 300;
    private static final int EPISODES_WITH_WATCHLIST = 301;
    private static final int EPISODES_WITH_WATCHLIST_AND_DATE = 302;
    
	private TvWatchlistDbHelper mOpenHelper;
	
	private static final SQLiteQueryBuilder sEpisodesByTvrageIDQueryBuilder;
    
    static{
    	sEpisodesByTvrageIDQueryBuilder = new SQLiteQueryBuilder();
    	sEpisodesByTvrageIDQueryBuilder.setTables(
    			TvWatchlistContract.EpisodesEntry.TABLE_NAME + " INNER JOIN " + TvWatchlistContract.WatchlistEntry.TABLE_NAME
    			+ " ON " + TvWatchlistContract.EpisodesEntry.TABLE_NAME + "." + TvWatchlistContract.EpisodesEntry.COLUMN_TVRAGE_ID
    			+ "= " + TvWatchlistContract.WatchlistEntry.TABLE_NAME + "." + TvWatchlistContract.WatchlistEntry.COLUMN_TVRAGE_ID);
    }
    
    // ? is replaceable by query parameters
    private static final String sTvrageIDSelection = 
    		TvWatchlistContract.WatchlistEntry.TABLE_NAME+"."+TvWatchlistContract.WatchlistEntry.COLUMN_TVRAGE_ID
    		+ " = ? ";
    
    private static final String sTvrageIDWithStartDateSelection =
    		TvWatchlistContract.WatchlistEntry.TABLE_NAME+
                    "." + TvWatchlistContract.WatchlistEntry.COLUMN_TVRAGE_ID + " = ? AND " +
                    TvWatchlistContract.EpisodesEntry.COLUMN_AIRDATE + " >= ? ";
    
    private static final String sTvrageIDAndDaySelection =
    		TvWatchlistContract.WatchlistEntry.TABLE_NAME +
                    "." + TvWatchlistContract.WatchlistEntry.COLUMN_TVRAGE_ID + " = ? AND " +
                    TvWatchlistContract.EpisodesEntry.COLUMN_AIRDATE + " = ? ";
    
    private Cursor getEpisodesByTvrageID(Uri uri, String[] projection, String sortOrder) {
        String tvrage_id = TvWatchlistContract.EpisodesEntry.getTvrageIDFromUri(uri);
        String startDate = TvWatchlistContract.EpisodesEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sTvrageIDSelection;
            selectionArgs = new String[]{tvrage_id};
        } else {
            selectionArgs = new String[]{tvrage_id, startDate};
            selection = sTvrageIDWithStartDateSelection;
        }

        return sEpisodesByTvrageIDQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    
    private Cursor getEpisodesByTvrageIDAndDate(
            Uri uri, String[] projection, String sortOrder) {
        String tvrage_id = TvWatchlistContract.EpisodesEntry.getTvrageIDFromUri(uri);
        String date = TvWatchlistContract.EpisodesEntry.getDateFromUri(uri);

        return sEpisodesByTvrageIDQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTvrageIDAndDaySelection,
                new String[]{tvrage_id, date},
                null,
                null,
                sortOrder
        );
    }

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	public static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = TvWatchlistContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, TvWatchlistContract.PATH_TRENDING, TREDING);
		matcher.addURI(authority, TvWatchlistContract.PATH_TRENDING + "/#",
				TRENDING_ID);
		
		matcher.addURI(authority, TvWatchlistContract.PATH_WATCHLIST, WATCHLIST);
		matcher.addURI(authority, TvWatchlistContract.PATH_WATCHLIST + "/#",
				WATCHLIST_ID);
//		matcher.addURI(authority, TvWatchlistContract.PATH_WATCHLIST + "/#",
//				WATCHLIST_TVRAGE_ID);

		matcher.addURI(authority, TvWatchlistContract.PATH_EPISODES, EPISODES);
    	matcher.addURI(authority, TvWatchlistContract.PATH_EPISODES + "/*", EPISODES_WITH_WATCHLIST);
    	matcher.addURI(authority, TvWatchlistContract.PATH_EPISODES + "/*/*", EPISODES_WITH_WATCHLIST_AND_DATE);
    	
		return matcher;
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new TvWatchlistDbHelper(getContext());
		return true; // Telling that it has been created successfully
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case TREDING:
			return TvWatchlistContract.TrendingEntry.CONTENT_TYPE;
		case TRENDING_ID:
			return TvWatchlistContract.TrendingEntry.CONTENT_ITEM_TYPE;
		case WATCHLIST:
			return TvWatchlistContract.WatchlistEntry.CONTENT_TYPE;
		case WATCHLIST_ID:
			return TvWatchlistContract.WatchlistEntry.CONTENT_ITEM_TYPE;
//		case WATCHLIST_TVRAGE_ID:
//			return TvWatchlistContract.WatchlistEntry.CONTENT_ITEM_TYPE;
		case EPISODES_WITH_WATCHLIST_AND_DATE:
			return TvWatchlistContract.EpisodesEntry.CONTENT_ITEM_TYPE;
		case EPISODES_WITH_WATCHLIST:
			return TvWatchlistContract.EpisodesEntry.CONTENT_TYPE;
		case EPISODES:
			return TvWatchlistContract.EpisodesEntry.CONTENT_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown Uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		Uri returnUri;
		long _id;

		switch (match) {
		case TREDING:
			_id = db.insert(TvWatchlistContract.TrendingEntry.TABLE_NAME,
					null, contentValues);
			if (_id > 0)
				returnUri = TvWatchlistContract.TrendingEntry
						.buildTrendingUri(_id);
			else {
				throw new android.database.SQLException(
						"Failed to insert row into " + uri);
			}
			break;
		case WATCHLIST:
			_id = db.insert(TvWatchlistContract.WatchlistEntry.TABLE_NAME,
					null, contentValues);
			if (_id > 0)
				returnUri = TvWatchlistContract.WatchlistEntry
						.buildWatchlistUri(_id);
			else {
				throw new android.database.SQLException(
						"Failed to insert row into " + uri);
			}
			break;
		case EPISODES:
			_id = db.insert(TvWatchlistContract.EpisodesEntry.TABLE_NAME, null, contentValues);
			if(_id > 0)
				returnUri = TvWatchlistContract.EpisodesEntry.buildEpisodesUri(_id);
			else {
				throw new android.database.SQLException("Failed to insert row into " + uri);
			}
		default:
			throw new UnsupportedOperationException("Unknown Uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return returnUri;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor returncCursor;
		switch (sUriMatcher.match(uri)) {
		case TREDING: {
			returncCursor = mOpenHelper.getReadableDatabase().query(
					TvWatchlistContract.TrendingEntry.TABLE_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		}

		case TRENDING_ID: {
			returncCursor = mOpenHelper.getReadableDatabase().query(
					TvWatchlistContract.TrendingEntry.TABLE_NAME,
					projection,
					TvWatchlistContract.TrendingEntry._ID + " = '"
							+ ContentUris.parseId(uri) + "'", null, null, null,
					sortOrder);
			break;
		}
		case WATCHLIST: {
			returncCursor = mOpenHelper.getReadableDatabase().query(
					TvWatchlistContract.WatchlistEntry.TABLE_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		}

		case WATCHLIST_ID: {
			returncCursor = mOpenHelper.getReadableDatabase().query(
					TvWatchlistContract.WatchlistEntry.TABLE_NAME,
					projection,
					TvWatchlistContract.WatchlistEntry._ID + " = '"
							+ ContentUris.parseId(uri) + "'", null, null, null,
					sortOrder);
			break;
		}
//		case WATCHLIST_TVRAGE_ID: {
//			returncCursor = mOpenHelper.getReadableDatabase().query(
//					TvWatchlistContract.WatchlistEntry.TABLE_NAME,
//					projection,
//					TvWatchlistContract.WatchlistEntry.COLUMN_TVRAGE_ID + " = '"
//							+ WatchlistEntry.getTvrageIDFromUri(uri) + "'", null, null, null,
//					sortOrder);
//			break;
//		}
		case EPISODES_WITH_WATCHLIST_AND_DATE:
		{
			returncCursor = getEpisodesByTvrageIDAndDate(uri, projection, sortOrder);
			break;
		}
		
		case EPISODES_WITH_WATCHLIST:
		{
			returncCursor = getEpisodesByTvrageID(uri, projection, sortOrder);
			break;
		}
		
		case EPISODES:
		{
			returncCursor = mOpenHelper.getReadableDatabase().query(
					TvWatchlistContract.EpisodesEntry.TABLE_NAME,
					projection,
					selection,
					selectionArgs,
					null,
					null,
					sortOrder
				);
			break;
		}

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		returncCursor
				.setNotificationUri(getContext().getContentResolver(), uri);
		return returncCursor;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsDeleted;

		switch (match) {
		case TREDING:
			rowsDeleted = db.delete(TvWatchlistContract.TrendingEntry.TABLE_NAME,
					selection, selectionArgs);
			break;
		case WATCHLIST:
			rowsDeleted = db.delete(TvWatchlistContract.WatchlistEntry.TABLE_NAME,
					selection, selectionArgs);
			break;
		case EPISODES:
    		rowsDeleted = db.delete(TvWatchlistContract.EpisodesEntry.TABLE_NAME, selection, selectionArgs);
    		break;
    	default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		// Because a null deletes all rows
		if (selection == null || rowsDeleted != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection,
			String[] selectionArgs) {

		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		int rowsUpdated;

		switch (match) {
		case TRENDING_ID:
			rowsUpdated = db.update(TvWatchlistContract.TrendingEntry.TABLE_NAME,
					contentValues, selection, selectionArgs);
			break;
		case WATCHLIST_ID:
			rowsUpdated = db.update(TvWatchlistContract.WatchlistEntry.TABLE_NAME,
					contentValues, selection, selectionArgs);
			break;
		case EPISODES:
            rowsUpdated = db.update(TvWatchlistContract.EpisodesEntry.TABLE_NAME, contentValues, selection,
                    selectionArgs);
            break;
        
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		if (rowsUpdated != 0) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return rowsUpdated;
	}
	
	@Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case TREDING:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(TvWatchlistContract.TrendingEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case EPISODES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TvWatchlistContract.EpisodesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
