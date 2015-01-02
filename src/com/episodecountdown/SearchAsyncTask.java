package com.episodecountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.view.View;
import android.widget.GridView;

public class SearchAsyncTask extends AsyncTask<String, Void, Integer> implements OnDismissListener{

//	private final String LOG_TAG = SearchAsyncTask.class.getSimpleName();
	private final String searchUrl = "http://api.trakt.tv/search/shows.json/";
	private final String apiKey = "693ddf698060090412b79e469fd65638";
	private final String query = "?query=";
	private final String searchString;
	private final String limit = "&limit=";
	private final String searchLimit = "15";
	private final Context mContext;
	private final View rootView;
	private ProgressDialog proDialog;
	ArrayList<SearchResultModel> showsArrayList;

	public SearchAsyncTask(Context context, View rootView, String searchString) {
		mContext = context;
		this.rootView = rootView;
		this.searchString = searchString;
		showsArrayList = new ArrayList<SearchResultModel>();
	}
	
	@Override
	protected void onPreExecute() {
		 proDialog = new ProgressDialog(mContext, R.style.ProgressTheme);
	     proDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
	     proDialog.setCancelable(true);
	     proDialog.setCanceledOnTouchOutside(false);
	     proDialog.setOnDismissListener(this);
	     proDialog.show();
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
			String encodedQueryString = URLEncoder.encode(searchString, "UTF-8");
			String finalUrl = searchUrl + apiKey + query + encodedQueryString + limit + searchLimit;
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
			for (int i = 0 ; i<showsArray.length() ; i++){
				
				JSONObject show = showsArray.getJSONObject(i);
				
				SearchResultModel s = new SearchResultModel();
				s.setTitle(show.getString("title"));
				s.setYear(show.getInt("year"));
				s.setPosterUrl((show.getJSONObject("images").getString("poster")));
				
				String airDay = "";
				if(!show.isNull("air_day"))
					airDay = show.getString("air_day");
				s.setAirDay(airDay);
				
				String airTimeString = "";
				if(!show.isNull("air_time"))
					airTimeString = show.getString("air_time");
				s.setAirTime(airTimeString);
				
				s.setCertification(show.getString("certification"));
				s.setFirstAired(show.getInt("first_aired"));
				s.setNetwork(show.getString("network"));
				s.setOveview(show.getString("overview"));
				s.setRuntime(show.getInt("runtime"));
				s.setTraktUrl(show.getString("url"));
				s.setCountry(show.getString("country"));
				s.setEnded(show.getBoolean("ended"));

				int tvRageId = 0;
				if(!show.isNull("tvrage_id"))
					tvRageId = show.getInt("tvrage_id");
				s.setTvRageId(tvRageId);
				
				s.setTvdbId(show.getInt("tvdb_id"));
				s.setBannerUrl((show.getJSONObject("images").getString("banner")));
				s.setRatting_percentage(show.getJSONObject("ratings").getString("percentage"));
				s.setLoved(show.getJSONObject("ratings").getString("loved"));
				s.setHated(show.getJSONObject("ratings").getString("hated"));
				
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
				s.setGenres(genresString);
				
				showsArrayList.add(s);
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
		
		//Handling Dialog across orientation change.
		try {
	        if ((this.proDialog != null) && this.proDialog.isShowing()) {
	            this.proDialog.dismiss();
	        }
	    } catch (final IllegalArgumentException e) {
	        // Handle or log or ignore
	    } catch (final Exception e) {
	        // Handle or log or ignore
	    } finally {
	        this.proDialog = null;
	    }
		
		if(result != HttpStatus.SC_OK){
			// Dialog Box Builder!!
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
								mContext);
						alertDialogBuilder.setTitle(R.string.Async_error_title);
						alertDialogBuilder.setMessage(R.string.Async_error_message);
						alertDialogBuilder.setPositiveButton("Ok", null);
						// Create Alert DialogBox!!
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.setCanceledOnTouchOutside(true);
						alertDialog.show();
		}
		else if (showsArrayList.isEmpty()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					mContext);
			alertDialogBuilder.setTitle(R.string.search_list_nothing_found_title);
			alertDialogBuilder.setMessage(R.string.search_list_nothing_found_message);
			alertDialogBuilder.setPositiveButton("Ok", null);
			// Create Alert DialogBox!!
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.setCanceledOnTouchOutside(true);
			alertDialog.show();
		} else {
			GridView listView = (GridView) rootView.findViewById(R.id.listview_search);
			listView.setAdapter(new SearchAdapter(mContext, showsArrayList));
		}
	}
}
