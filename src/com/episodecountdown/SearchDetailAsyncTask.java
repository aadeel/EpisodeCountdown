package com.episodecountdown;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpStatus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SearchDetailAsyncTask extends AsyncTask<String, Void, Integer> {

	private final String LOG_TAG = SearchDetailAsyncTask.class.getSimpleName();
	SimpleDateFormat OUTPUTDATEFORMAT = new SimpleDateFormat("MMMM d, yyyy" , Locale.US);
	SimpleDateFormat TVRAGEDATEFORMAT = new SimpleDateFormat("MMM/dd/yyyy" , Locale.US);
	private final String searchUrl = "http://services.tvrage.com/feeds/full_show_info.php?sid=";
	private int tvRageId;
	private final Context mContext;
	private final View mRootView;
	private ProgressDialog proDialog;
	TvRageShowModel showDetails;
	private boolean silent= false;

	public SearchDetailAsyncTask(Context context, View rootView, int tvRageId) {
		mContext = context;
		mRootView = rootView;
		this.tvRageId = tvRageId;
		showDetails = new TvRageShowModel();
	}
	
	public SearchDetailAsyncTask(Context context, View rootView, int tvRageId, boolean silent) {
		mContext = context;
		mRootView = rootView;
		this.tvRageId = tvRageId;
		showDetails = new TvRageShowModel();
		this.silent = silent;
	}

	@Override
	protected Integer doInBackground(String... params) {
		// These two need to be declared outside the try/catch
		// so that they can be closed in the finally block.
		HttpURLConnection urlConnection = null;
		BufferedReader reader = null;

		try {
			//From trending detail fragment
			if(tvRageId == 0)
				return -9;
			// Construct the URL for the query
			String finalUrl = searchUrl + tvRageId;
//			Log.d(LOG_TAG, finalUrl);
			URL url = new URL(finalUrl);

			// Create the request to OpenWeatherMap, and open the connection
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			int httpStatusCode = urlConnection.getResponseCode();
//			Log.v(LOG_TAG, "httpStatusCode: " + httpStatusCode);

			// Read the input stream into a String
			InputStream inputStream = urlConnection.getInputStream();
			if (inputStream == null) {
				// Nothing to do.
				return httpStatusCode;
			}
			TvRageXmlParser xmlParser = new TvRageXmlParser();
			showDetails = xmlParser.parse(inputStream);

			return httpStatusCode;
		} catch (Exception e) {
//			Log.e(LOG_TAG, "Error ", e);
			// If the code didn't successfully get the data, there's
			// no point in attempting
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
	}

	@Override
	public void onPostExecute(Integer httpStatusCode) {

		// Handling Dialog across orientation change
		Log.e(LOG_TAG, "HTTP Status Code Returned: " + httpStatusCode);
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
		
		if(httpStatusCode == -9){
			// Hide Progress Bar
			mRootView.findViewById(R.id.detail_show_progress_bar).setVisibility(View.GONE);
			
			//Return, nothing to do here
			return;
		} else if (httpStatusCode != HttpStatus.SC_OK && !silent) {
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
		} else if (httpStatusCode == HttpStatus.SC_OK) {
			try {
				// Hide Progress Bar
				mRootView.findViewById(R.id.detail_show_progress_bar).setVisibility(View.GONE);
				
				//Enable views hidden before. 
				mRootView.findViewById(R.id.fragment_detail_countdown_linear_layout).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.id.fragment_detail_previous_next_episode_linear_layout).setVisibility(View.VISIBLE);
				mRootView.findViewById(R.id.fragment_detail_countdown_title).setVisibility(View.VISIBLE);
				
				long waitTime = showDetails.setLastAndNextEpisode();
	   			if (waitTime > 0) {
	   				final TextView countdownDaysView = (TextView) mRootView.findViewById(R.id.fragment_detail_countdown_days);
	   				final TextView countdownHoursView = (TextView) mRootView.findViewById(R.id.fragment_detail_countdown_hours);
	   				final TextView countdownMinsView = (TextView) mRootView.findViewById(R.id.fragment_detail_countdown_minutes);
	   				final TextView countdownSecondsView = (TextView) mRootView.findViewById(R.id.fragment_detail_countdown_seconds);
	         		
	         		new CountDownTimer(waitTime, 1000) {
	       				public void onTick(long millisUntilFinished) {
	       					
	       					countdownDaysView.setText(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)+"");
	         			
	         				countdownHoursView.setText(TimeUnit.MILLISECONDS
	         						.toHours(millisUntilFinished)
	         						- TimeUnit.DAYS
	         						.toHours(TimeUnit.MILLISECONDS
	         								.toDays(millisUntilFinished))+"");
	         				
	         				countdownMinsView.setText(
	         						TimeUnit.MILLISECONDS
	         						.toMinutes(millisUntilFinished)
	         						- TimeUnit.HOURS
	         						.toMinutes(TimeUnit.MILLISECONDS
	         								.toHours(millisUntilFinished))+"");
	         				
	         				countdownSecondsView.setText(
	         						TimeUnit.MILLISECONDS
	         						.toSeconds(millisUntilFinished)
	         						- TimeUnit.MINUTES
	         						.toSeconds(TimeUnit.MILLISECONDS
	         								.toMinutes(millisUntilFinished))+"");
	         			}
	
	         			public void onFinish() {
	         				// Do NOTHIng
	         			}
	         		}.start();
	     		} else if(showDetails.getEnded().trim().isEmpty()){
	     			mRootView.findViewById(R.id.fragment_detail_countdown_linear_layout).setVisibility(View.GONE);
	     			TextView textView = (TextView)mRootView.findViewById(R.id.fragment_detail_countdown_title);
	     			textView.setText(mContext.getString(R.string.show_tba_string));
	     		}
	     		else {
	     			mRootView.findViewById(R.id.fragment_detail_countdown_linear_layout).setVisibility(View.GONE);
	     			TextView textView = (TextView)mRootView.findViewById(R.id.fragment_detail_countdown_title);
	 				textView.setText(mContext.getString(R.string.show_ended_string));
				}		
							
				TextView nextEpisodeTitleView = (TextView) mRootView
						.findViewById(R.id.fragment_detail_show_next_episode_title);
				TextView nextEpisodeHeadingView = (TextView) mRootView
						.findViewById(R.id.fragment_detail_next_episode_heading);
				TextView nextEpisodeAirDateView = (TextView) mRootView
						.findViewById(R.id.fragment_detail_show_next_episode_date);
				if (showDetails.getNextEpisode() != null) {
					String nextEpisodeSeason = (showDetails.getNextEpisode().getSeason()>9)? showDetails.getNextEpisode().getSeason()+"":"0" + showDetails.getNextEpisode().getSeason();
					String nextEpisodeNumber = (showDetails.getNextEpisode().getEpisode()>9)? showDetails.getNextEpisode().getEpisode()+"":"0" + showDetails.getNextEpisode().getEpisode();
					nextEpisodeTitleView.setText(
							"S"+ nextEpisodeSeason + "E" + nextEpisodeNumber + "\n" + 
							showDetails.getNextEpisode()
							.getTitle());
					nextEpisodeAirDateView.setText((OUTPUTDATEFORMAT.format(showDetails.getNextEpisode().getAirDate())));
				}
				else {
					nextEpisodeTitleView.setVisibility(View.GONE);
					nextEpisodeAirDateView.setVisibility(View.GONE);
					nextEpisodeHeadingView.setVisibility(View.GONE);
				}
	
				if (showDetails.getLastEpisode() != null) {
					TextView lastEpisodeTitleView = (TextView) mRootView
							.findViewById(R.id.fragment_detail_show_last_episode_title);
					TextView lastEpisodeAirDateView = (TextView) mRootView
							.findViewById(R.id.fragment_detail_show_last_episode_date);
					
					String lastEpisodeSeason = (showDetails.getLastEpisode().getSeason()>9)? showDetails.getLastEpisode().getSeason()+"":"0" + showDetails.getLastEpisode().getSeason();
					String lastEpisodeNumber = (showDetails.getLastEpisode().getEpisode()>9)? showDetails.getLastEpisode().getEpisode()+"":"0" + showDetails.getLastEpisode().getEpisode();
					lastEpisodeTitleView.setText(
							"S"+ lastEpisodeSeason + "E" + lastEpisodeNumber + "\n" + 
							showDetails.getLastEpisode()
							.getTitle());
					lastEpisodeAirDateView.setText((OUTPUTDATEFORMAT.format(showDetails.getLastEpisode().getAirDate())));
				}
			}
			catch (NullPointerException e) {
				// Might be thrown cz view was missing
				Log.e(LOG_TAG, "Null pointer exception thrown for " + tvRageId);
			}
		}
	}
}