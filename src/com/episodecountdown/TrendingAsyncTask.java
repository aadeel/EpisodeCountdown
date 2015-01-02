package com.episodecountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;

import com.episodecountdown.data.TvWatchlistContract.TrendingEntry;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class TrendingAsyncTask extends AsyncTask<String, Void, Integer> implements OnDismissListener{

//	private final String LOG_TAG = TrendingAsyncTask.class.getSimpleName();
	private final String Url = "http://api.trakt.tv/shows/trending.json/";
	private final String apiKey = "693ddf698060090412b79e469fd65638";
	private final Context mContext;
	private final View mRootView;
	private boolean mSilent = false;
	private final SharedPreferences sharedPreferences;
	
	public TrendingAsyncTask(Context context, View rootView) {
		mContext = context;
		mRootView = rootView;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	public TrendingAsyncTask(Context context, View rootView, boolean isSilent) {
		mContext = context;
		mRootView = rootView;
		mSilent = isSilent;
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	@Override
	protected void onPreExecute() {
		//Doing Nothing
	}
	
	@Override
	public void onDismiss(DialogInterface arg0) {
		this.cancel(true);
	};

	@Override
	protected Integer doInBackground(String... params) {
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		// Will contain the raw JSON response as a string.
		String searchJsonStr = null;
		int httpStatusCode = -1;

		try {
			// Construct the URL for the query
			String finalUrl = Url + apiKey;
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
			// If the code didn't successfully get the weather data, there's
			// no point in attemping
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
			JSONArray showsArray = new JSONArray(searchJsonStr);
			
			// Get and insert the new weather information into the database
	        Vector<ContentValues> cVVector = new Vector<ContentValues>(showsArray.length());
	        
	        int showsToRead;
	        try {
	        String default_shows = mContext.getString(R.string.pref_most_popular_shows_default);
	        String popular_shows_preference = sharedPreferences.getString(mContext.getString(R.string.pref_most_popular_shows_key), default_shows);
	        int parsingPreference = Integer.parseInt(popular_shows_preference);
	        showsToRead = (showsArray.length() < parsingPreference)? showsArray.length(): Integer.parseInt(popular_shows_preference); 
	        } catch (Exception e) {
	        	showsToRead = 20;
	        }
	        for(int i = 0; i < showsToRead; i++) {
	        	JSONObject show = showsArray.getJSONObject(i);
				
	        	//The values that need to be saved
	        	int position = i;
	        	String titleString = show.getString("title");
				int year = show.getInt("year");
				String poster = show.getJSONObject("images").getString("poster");
				
				String airDay = "";
				if(!show.isNull("air_day"))
					airDay = show.getString("air_day");
				
				String airTimeString = "";
				if(!show.isNull("air_time"))
					airTimeString = show.getString("air_time");
				
				String certification = show.getString("certification");
				int first_aired = show.getInt("first_aired");
				String network = show.getString("network");
				String overview = show.getString("overview");
				int runtime = show.getInt("runtime");
				String traktUrl = show.getString("url");
				String country = show.getString("country");
				String status = show.getString("status");
				
				int tvRageId = 0;
				if(!show.isNull("tvrage_id"))
					tvRageId = show.getInt("tvrage_id");
				
				int tvdbId = show.getInt("tvdb_id");
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
	            showValues.put(TrendingEntry.COLUMN_POSITION, position);
	            showValues.put(TrendingEntry.COLUMN_TITLE, titleString);
	            showValues.put(TrendingEntry.COLUMN_YEAR, year);
	            showValues.put(TrendingEntry.COLUMN_TRAKT_URL, traktUrl);
	            showValues.put(TrendingEntry.COLUMN_FIRST_AIRED, first_aired);
	            showValues.put(TrendingEntry.COLUMN_COUNTRY, country);
	            showValues.put(TrendingEntry.COLUMN_CERTIFICATION, certification);
	            showValues.put(TrendingEntry.COLUMN_OVERVIEW, overview);
	            showValues.put(TrendingEntry.COLUMN_RUNTIME, runtime);
	            showValues.put(TrendingEntry.COLUMN_STATUS, status);
	            showValues.put(TrendingEntry.COLUMN_NETWORK, network);
	            showValues.put(TrendingEntry.COLUMN_AIR_DAY, airDay);
	            showValues.put(TrendingEntry.COLUMN_AIR_TIME, airTimeString);
	            showValues.put(TrendingEntry.COLUMN_TVRAGE_ID, tvRageId);
	            showValues.put(TrendingEntry.COLUMN_TVDB_ID, tvdbId);
	            showValues.put(TrendingEntry.COLUMN_POSTER, poster);
	            showValues.put(TrendingEntry.COLUMN_BANNER, banner);
	            showValues.put(TrendingEntry.COLUMN_RATINGS_PERCENTAGE, ratting_percentage);
	            showValues.put(TrendingEntry.COLUMN_RATINGS_VOTES, ratting_votes);
	            showValues.put(TrendingEntry.COLUMN_RATINGS_LOVED, ratting_loved);
	            showValues.put(TrendingEntry.COLUMN_RATINGS_HATED, ratting_hated);
	            showValues.put(TrendingEntry.COLUMN_GENRES, genresString);
	            
	            cVVector.add(showValues);

	        }
	        if (cVVector.size() > 0) {
	            ContentValues[] cvArray = new ContentValues[cVVector.size()];
	            cVVector.toArray(cvArray);
//	            int rowsDeleted =
	            		mContext.getContentResolver().delete(TrendingEntry.CONTENT_URI, null, null);
//	            int rowsInserted = 
	            		mContext.getContentResolver()
	                    .bulkInsert(TrendingEntry.CONTENT_URI, cvArray);
//	            Log.v(LOG_TAG, "inserted " + rowsInserted + " rows deleted " + rowsDeleted + " rows of trending show data");
	        }
	            return httpStatusCode;

		} catch (JSONException e) {
//			Log.e(LOG_TAG, e.getMessage(), e);
			e.printStackTrace();
		}
		return httpStatusCode;
	}

	@Override
	public void onPostExecute(Integer result) {
		
		PullToRefreshGridView gridView = (PullToRefreshGridView) mRootView.findViewById(R.id.myGrid);
		gridView.onRefreshComplete();
		if(result == HttpStatus.SC_OK){
			//refreshing last sync
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(mContext.getString(R.string.last_trending_update_key), System.currentTimeMillis());
            editor.commit();
   		}
		else {

			//No alert dialogs in silent mode.
			if(!mSilent){
				// Dialog Box Builder!!
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
				alertDialogBuilder.setTitle(R.string.Async_error_title);
				alertDialogBuilder.setMessage(R.string.Async_error_message);
				alertDialogBuilder.setPositiveButton("Ok", null);
				// Create Alert DialogBox!!
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.setCanceledOnTouchOutside(true);
				alertDialog.show();
			}
		}
	}
}
