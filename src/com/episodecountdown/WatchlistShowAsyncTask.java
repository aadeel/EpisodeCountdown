package com.episodecountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.episodecountdown.EpisodeCountdown.TrackerName;
import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class WatchlistShowAsyncTask extends AsyncTask<String, Void, Integer> {

//	private final String LOG_TAG = WatchlistShowAsyncTask.class.getSimpleName();
	private final String Url = "http://api.trakt.tv/show/summary.json/";
	private final String apiKey = "693ddf698060090412b79e469fd65638";
	private static final long AUTO_UPDATE_INTERVAL = 1000*60*60*24*3; //3 days
	private final Context mContext;
	private final String tvdbID;
	private final SharedPreferences sharedPreferences;
	private boolean manual = false;
	private String sharedPreferenceKey;
	private String tvShowNameString;
	
	//For episodes asyncTask
	int tvRageId;
	
	public WatchlistShowAsyncTask(Context context, String tvdbID) {
		mContext = context;
		this.tvdbID = tvdbID;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		sharedPreferenceKey = mContext.getString(R.string.watchlist_show_id_key) + tvdbID;
    }
	
	public WatchlistShowAsyncTask(Context context, String tvdbID, boolean manual) {
		mContext = context;
		this.tvdbID = tvdbID;
		this.manual = manual;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		sharedPreferenceKey = mContext.getString(R.string.watchlist_show_id_key) + tvdbID;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		
		//Only update if updates due in case of auto. 
		if(!manual) {
			long lastUpdateTime = sharedPreferences.getLong(sharedPreferenceKey, 0l);
	        boolean timeToUpdate = (System.currentTimeMillis() - lastUpdateTime >= AUTO_UPDATE_INTERVAL);
			if(!timeToUpdate)
				return -9;
		}
		
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		// Will contain the raw JSON response as a string.
		String searchJsonStr = null;
		int httpStatusCode = -1;

		try {
			// Construct the URL for the query
			String finalUrl = Url + apiKey + "/" + tvdbID;
//			Log.d(LOG_TAG, finalUrl);
			URL url = new URL(finalUrl);

			// Create the request to OpenWeatherMap, and open the connection
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			httpStatusCode = urlConnection.getResponseCode();

			// Read the input stream into a String
			InputStream inputStream = urlConnection.getInputStream();
			StringBuffer buffer = new StringBuffer();
			if (inputStream == null) {
				// Nothing to do.
				return httpStatusCode;
			}
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				// Since it's JSON, adding a newline isn't necessary (it
				// won't affect parsing)
				// But it does make debugging a *lot* easier if you print
				// out the completed
				// buffer for debugging.
				buffer.append(line + "\n");
			}

			if (buffer.length() == 0) {
				// Stream was empty. No point in parsing.
				return httpStatusCode;
			}

			searchJsonStr = buffer.toString();
			// Log.v(LOG_TAG, "Forecast JSON String: "+forecastJsonStr);
		} catch (IOException e) {
//			Log.e(LOG_TAG, "Error ", e);
			// If the code didn't successfully get the data, there's
			// no point in attempting
			// to parse it.
			return httpStatusCode;
		} catch (Exception e) {
//			Log.e(LOG_TAG, "Error ", e);
			// If the code didn't successfully get the data, there's
			// no point in attempting
			// to parse it.
			return httpStatusCode;
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

		try {
			// Parsing Data to ArrayList
			JSONObject show = new JSONObject(searchJsonStr);
			
	        	//The values that need to be saved
	        	String titleString = show.getString("title");
				int year = show.getInt("year");
				String poster = show.getJSONObject("images").getString("poster");
				String firstAired = show.getString("first_aired");
				String airDayUTC = show.getString("air_day_utc");
				
				String airDay = "";
				if(!show.isNull("air_day"))
					airDay = show.getString("air_day");
				
				String airTimeString = "";
				if(!show.isNull("air_time"))
					airTimeString = show.getString("air_time");
				
				String airTimeStringUTC = show.getString("air_time_utc");
				String certification = show.getString("certification");
				String network = show.getString("network");
				String overview = show.getString("overview");
				int runtime = show.getInt("runtime");
				String country = show.getString("country");
				String status = show.getString("status");
				tvRageId = show.getInt("tvrage_id");
				int tvdbIDString = show.getInt("tvdb_id");
				String banner = show.getJSONObject("images").getString("banner");
				int ratting_percentage = show.getJSONObject("ratings").getInt("percentage");
				int ratting_votes = show.getJSONObject("ratings").getInt("votes");
				int ratting_loved = show.getJSONObject("ratings").getInt("loved");
				int ratting_hated = show.getJSONObject("ratings").getInt("hated");
				JSONArray genresJSONArray = show.getJSONArray("genres");
				String genresString = "";
				if(genresJSONArray != null){
					boolean first = true;
					for (int j = 0; j<genresJSONArray.length(); j++)
					{
						if(first) {
							genresString += genresJSONArray.getString(j);
							first = false;
						}
						else
							genresString += " | " + genresJSONArray.getString(j);;
					}
				}
				
	            ContentValues showValues = new ContentValues();

	            //Saving Values
	            showValues.put(WatchlistEntry.COLUMN_TITLE, titleString);
	            showValues.put(WatchlistEntry.COLUMN_YEAR, year);
	            showValues.put(WatchlistEntry.COLUMN_COUNTRY, country);
	            showValues.put(WatchlistEntry.COLUMN_CERTIFICATION, certification);
	            showValues.put(WatchlistEntry.COLUMN_FIRST_AIRED, firstAired);
	            showValues.put(WatchlistEntry.COLUMN_OVERVIEW, overview);
	            showValues.put(WatchlistEntry.COLUMN_RUNTIME, runtime);
	            showValues.put(WatchlistEntry.COLUMN_STATUS, status);
	            showValues.put(WatchlistEntry.COLUMN_NETWORK, network);
	            showValues.put(WatchlistEntry.COLUMN_AIR_DAY, airDay);
	            showValues.put(WatchlistEntry.COLUMN_AIR_TIME, airTimeString);
	            showValues.put(WatchlistEntry.COLUMN_AIR_DAY_UTC, airDayUTC);
	            showValues.put(WatchlistEntry.COLUMN_AIR_TIME_UTC, airTimeStringUTC);
	            showValues.put(WatchlistEntry.COLUMN_TVRAGE_ID, tvRageId);
	            showValues.put(WatchlistEntry.COLUMN_TVDB_ID, tvdbIDString);
	            showValues.put(WatchlistEntry.COLUMN_POSTER, poster);
	            showValues.put(WatchlistEntry.COLUMN_BANNER, banner);
	            showValues.put(WatchlistEntry.COLUMN_RATINGS_PERCENTAGE, ratting_percentage);
	            showValues.put(WatchlistEntry.COLUMN_RATINGS_VOTES, ratting_votes);
	            showValues.put(WatchlistEntry.COLUMN_RATINGS_LOVED, ratting_loved);
	            showValues.put(WatchlistEntry.COLUMN_RATINGS_HATED, ratting_hated);
	            showValues.put(WatchlistEntry.COLUMN_GENRES, genresString);
	            
	            ContentResolver contentResolver = mContext.getContentResolver();
	            if(contentResolver !=null)
	            	contentResolver.insert(WatchlistEntry.CONTENT_URI, showValues);
	            
	            tvShowNameString = titleString;	
	            return httpStatusCode;

		} catch (JSONException e) {
//			Log.e(LOG_TAG, e.getMessage(), e);
			e.printStackTrace();
		}
		return httpStatusCode;
	}

	@Override
	public void onPostExecute(Integer result) {
		if(result == -9){
			// Not Time to Update
//			Log.i(LOG_TAG, "no time to update this show");
		}
		else if(result == HttpStatus.SC_OK){
			//refreshing last sync
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(sharedPreferenceKey, System.currentTimeMillis());
            editor.commit();
            
            // Analytics
        	Tracker t = ((EpisodeCountdown) ((Activity)mContext).getApplication()).getTracker(
                TrackerName.APP_TRACKER);
            // Set screen name.
            t.setScreenName("In Watchlist "+ tvShowNameString + " tvdb: " + tvdbID);
            // Send a screen view.
            t.send(new HitBuilders.ScreenViewBuilder().build());
            
			if(tvRageId != 0){
				WatchlistEpisodeAsyncTask task = new WatchlistEpisodeAsyncTask(mContext, tvRageId, tvdbID);
				task.execute();
			}
		}
		else if(manual) { 
			Toast.makeText(mContext.getApplicationContext(),
					mContext.getString(R.string.Async_error_message), Toast.LENGTH_LONG).
					show();
		}
	}
}
