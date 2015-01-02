package com.episodecountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.http.HttpStatus;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.episodecountdown.EpisodeCountdown.TrackerName;
import com.episodecountdown.data.TvWatchlistContract.EpisodesEntry;
import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;
import com.episodecountdown.data.TvWatchlistDbHelper;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class WatchlistEpisodeAsyncTask extends AsyncTask<Void, Void, Integer> {

//	private final String LOG_TAG = WatchlistEpisodeAsyncTask.class.getSimpleName();
	private final String searchUrl = "http://services.tvrage.com/feeds/full_show_info.php?sid=";
	private final int tvrageId;
	private final String tvdbId;
	private final Context mContext;
	private TvRageShowModel showDetails;
	
	public WatchlistEpisodeAsyncTask(Context context, int tvrageID, String tvdbID) {
		mContext = context;
		this.tvrageId = tvrageID;
		showDetails = new TvRageShowModel();
		tvdbId = tvdbID;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;
		int httpStatusCode = -1;

		try {
			// Construct the URL for the query
			String finalUrl = searchUrl + tvrageId;
//			Log.d(LOG_TAG, finalUrl);
			URL url = new URL(finalUrl);

			// Create the request to 
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			httpStatusCode = urlConnection.getResponseCode();
//			Log.v(LOG_TAG, "httpStatusCode: " + httpStatusCode);

			// Read the input stream into a String
			InputStream inputStream = urlConnection.getInputStream();
			if (inputStream == null) {
				// Nothing to do.
				return httpStatusCode;
			}
			TvRageXmlParser xmlParser = new TvRageXmlParser();
			showDetails = xmlParser.parse(inputStream);

		} catch (IOException e) {
//			Log.e(LOG_TAG, "Error ", e);
			// If the code didn't successfully get the weather data, there's
			// no point in attemping
			// to parse it.
			return -1;
			
		} catch (XmlPullParserException e) {
//			Log.e(LOG_TAG, "Error ", e);
			// If the code didn't successfully get the weather data, there's
			// no point in attemping
			// to parse it.
			return -1;
		
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
//					Log.e(LOG_TAG, "Error closing stream", e);
				}
			}
		}

			ArrayList<TvRageEpisodeModel> episodes = showDetails.getEpisodeList();
			// Get and insert the new weather information into the database
	        Vector<ContentValues> cVVector = new Vector<ContentValues>(episodes.size());
	        
	        // Calculating timeinmils for this episode
	        String showTimeZoneString = showDetails.getTimezone();
    		SimpleDateFormat sdfAirTimeFormat = new SimpleDateFormat("hh:mm",Locale.US);
    		TimeZone showTimeZone = TimeZone.getTimeZone(showTimeZoneString);
    		// Gets the correct timezone. CHECKED
    		sdfAirTimeFormat.setTimeZone(showTimeZone);
    		Calendar calAirTimeTemp = new GregorianCalendar(showTimeZone, Locale.US);
    		try {
    			calAirTimeTemp.setTime(sdfAirTimeFormat.parse(showDetails.getAirtime()));
    			// Timezone now is still showtimezone, day,month,year, reset.. understandable
    		} catch (ParseException e) {
    			return -1;
    		} catch (NullPointerException e) {
    			return -1;
    		}

	        for(int i = 0; i < episodes.size(); i++) {
	        	TvRageEpisodeModel episode = episodes.get(i);
	        	
	        	ContentValues showValues = new ContentValues();
	        	
	        	// construct calendar object with correct time zone
	    		Calendar calAirTime = new GregorianCalendar(showTimeZone, Locale.US);
	    		calAirTime.setTime(episode.getAirDate());
	    		calAirTime.set(Calendar.HOUR_OF_DAY,calAirTimeTemp.get(Calendar.HOUR_OF_DAY));
	    		calAirTime.set(Calendar.MINUTE, calAirTimeTemp.get(Calendar.MINUTE));
	    		calAirTime.set(Calendar.SECOND, 0);
	    		calAirTime.set(Calendar.MILLISECOND, 0);
	        	
	            //Saving Values
	            showValues.put(EpisodesEntry.COLUMN_TITLE, episode.getTitle());
	            showValues.put(EpisodesEntry.COLUMN_AIRDATE, calAirTime.getTimeInMillis()+"");
	            showValues.put(EpisodesEntry.COLUMN_EPISODE_NO, episode.getEpisode());
	            showValues.put(EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER, episode.getEpisodeFromStart());
	            showValues.put(EpisodesEntry.COLUMN_SCREENCAP, episode.getScreenCap());
	            showValues.put(EpisodesEntry.COLUMN_SEASON_NO, episode.getSeason());
	            showValues.put(EpisodesEntry.COLUMN_TVRAGE_ID, tvrageId);
	            
	            cVVector.add(showValues);

	        }
	        if (cVVector.size() > 0) {
	            ContentValues[] cvArray = new ContentValues[cVVector.size()];
	            cVVector.toArray(cvArray);
//	            int rowsInserted = 
	            		mContext.getContentResolver()
	                    .bulkInsert(EpisodesEntry.CONTENT_URI, cvArray);
//	            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows of episodes");
	        }
	        
	        TvWatchlistDbHelper mHelper = new TvWatchlistDbHelper(mContext.getApplicationContext());
            SQLiteDatabase db = mHelper.getWritableDatabase();
            
            String updateQuery = "UPDATE " + WatchlistEntry.TABLE_NAME + " SET " + WatchlistEntry.COLUMN_SHOW_TIMEZONE
	        		+ " = '" + showTimeZoneString + "' WHERE " + WatchlistEntry.COLUMN_TVRAGE_ID + " = '" + tvrageId + "'";
	        
//            Log.v(LOG_TAG, updateQuery);
	        
            Cursor cursor = db.rawQuery(updateQuery, null);
            cursor.moveToFirst();
            cursor.close();
            db.close();
            
            return httpStatusCode;
	    }

	@Override
	public void onPostExecute(Integer result) {
		
		if(result == HttpStatus.SC_OK){
			// Nacho 
			 
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.putLong(mContext.getString(R.string.watchlist_show_id_key)+tvdbId, 0);
            editor.commit();
        
		}
		else {
			// !Nacho
			//Update 
//			Log.i(LOG_TAG,"Rare case. Episode task failure. Resetting update time for " + tvdbId);
			// Analytics
        	Tracker t = ((EpisodeCountdown) ((Activity)mContext).getApplication()).getTracker(
                TrackerName.APP_TRACKER);
            // Set screen name.
            t.setScreenName("In Episode Asynctask: Rare case. Episode task failure for " + tvdbId);
            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
	}
}
