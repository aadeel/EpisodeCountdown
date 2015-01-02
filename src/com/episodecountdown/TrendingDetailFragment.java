package com.episodecountdown;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.episodecountdown.EpisodeCountdown.TrackerName;
import com.episodecountdown.data.TvWatchlistContract.TrendingEntry;
import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;
import com.episodecountdown.data.TvWatchlistDbHelper;
import com.episodecountdown.library.TextViewEx;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

public class TrendingDetailFragment extends Fragment {
	
	private View mRootView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
//		// Analytics first
//    	// Get tracker.
//        Tracker t = ((EpisodeCountdown) getActivity().getApplication()).getTracker(
//            TrackerName.APP_TRACKER);
//        // Set screen name.
//        t.setScreenName("EpisodeCountdown TrendingDetailFragment");
//        // Send a screen view.
//        t.send(new HitBuilders.AppViewBuilder().build());
        
		mRootView = inflater.inflate(R.layout.fragment_detail, container,
				false);
		
		//Hide unused Views
		mRootView.findViewById(R.id.fragment_detail_countdown_linear_layout).setVisibility(View.GONE);
		mRootView.findViewById(R.id.fragment_detail_previous_next_episode_linear_layout).setVisibility(View.GONE);
		mRootView.findViewById(R.id.fragment_detail_countdown_title).setVisibility(View.GONE);
		
		// Get Arguments
		Bundle args = getArguments();
				
		String posterUrl = args.getString(TrendingEntry.COLUMN_POSTER);
		String bannerUrl = args.getString(TrendingEntry.COLUMN_BANNER);
		String title = args.getString(TrendingEntry.COLUMN_TITLE);
		String year = args.getString(TrendingEntry.COLUMN_YEAR);
		String overview = args.getString(TrendingEntry.COLUMN_OVERVIEW);
		String percentageString = args.getString(TrendingEntry.COLUMN_RATINGS_PERCENTAGE);
		String lovedString = args.getString(TrendingEntry.COLUMN_RATINGS_LOVED);
		String hatedString = args.getString(TrendingEntry.COLUMN_RATINGS_HATED);
		String genresString = args.getString(TrendingEntry.COLUMN_GENRES);
		String primiered = Utility.getReadableDateString(Integer.parseInt(args.getString(TrendingEntry.COLUMN_FIRST_AIRED)));
		String status = args.getString(TrendingEntry.COLUMN_STATUS);
		String runtime = args.getString(TrendingEntry.COLUMN_RUNTIME);
		String country = args.getString(TrendingEntry.COLUMN_COUNTRY);
		String network = args.getString(TrendingEntry.COLUMN_NETWORK);
		String airtime = args.getString(TrendingEntry.COLUMN_AIR_TIME);
		String airday = args.getString(TrendingEntry.COLUMN_AIR_DAY);
		final String tvdb_ID = args.getString(TrendingEntry.COLUMN_TVDB_ID);
		
		ImageView posterView = (ImageView) mRootView.findViewById(R.id.fragment_detail_poster);
		// Using Picasso to download image
		Picasso.with(getActivity()).load(posterUrl).resize(0, 300)
		.into(posterView);
		
		ImageView bannerView = (ImageView) mRootView.findViewById(R.id.fragment_detail_banner);
		// Using Picasso to download image
		Picasso.with(getActivity()).load(bannerUrl).placeholder(R.drawable.placeholder_banner)
		.resize(0, 140)
		.into(bannerView);
		
		TextView titleView = (TextView) mRootView
				.findViewById(R.id.fragment_detail_title);
		if (!title.contains(" ("))
			title = title + " (" + year + ")";
		titleView.setText(title);
		
		TextViewEx overviewView = (TextViewEx) mRootView.findViewById(R.id.fragment_detail_overview);
		overviewView.setText(overview, true);
		
		//Ratings
		TextView percentage = (TextView) mRootView.findViewById(R.id.fragment_detail_ratting);
		TextView loved = (TextView) mRootView.findViewById(R.id.fragment_detail_ratting_positive);
		TextView hated = (TextView) mRootView.findViewById(R.id.fragment_detail_ratting_negative);
		
		percentage.setText(percentageString + "%");
		loved.setText(lovedString);
		hated.setText(hatedString);

		TextView genresView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_genre);
		genresView.setText(genresString);

		TextView premieredView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_started);
		premieredView.setText(primiered);
		
		//Changing ended View to STATUS view
		TextView statusTitleView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_ended_title);
		statusTitleView.setText("Status");
		
		TextView statusView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_ended);
		statusView.setText(status);
		
		TextView runtimeView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_runtime);
		runtimeView.setText(runtime + " Minutes");

		TextView countryView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_origin_country);
		countryView.setText(country);

		TextView networkView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_network);
		networkView.setText(network);

		TextView airtimeView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_airtime);
		airtimeView.setText(airtime);

		TextView airdayView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_airday);
		airdayView.setText(airday);

		//Check if show already in watchlist
		TvWatchlistDbHelper mHelper = new TvWatchlistDbHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        
        String query = "SELECT COUNT(*) FROM " + WatchlistEntry.TABLE_NAME + " WHERE " + WatchlistEntry.COLUMN_TVDB_ID + " = '" + tvdb_ID + "'";
        
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int inWatchist = cursor.getInt(0);
        cursor.close();
        db.close();
        
        Button addButton = (Button) mRootView.findViewById(R.id.fragment_detail_watchlist_button);
        if(Integer.parseInt(getArguments().getString(TrendingEntry.COLUMN_TVRAGE_ID))==0) {
        	addButton.setText(getString(R.string.no_tvrageID_watchlist_button));
			addButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Toast.makeText(getActivity().getApplicationContext(),getString(R.string.no_tvrageID_add_watchlist_error),Toast.LENGTH_LONG).show();
				}
			});
        }
        else if(inWatchist == 0) {
	        addButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					WatchlistShowAsyncTask task = new WatchlistShowAsyncTask(getActivity(), tvdb_ID, true);
					task.execute();
					Toast.makeText(getActivity().getApplicationContext(),getString(R.string.adding_to_watchlist),Toast.LENGTH_LONG).show();
					//getActivity().finish();
				}
			});
		} else {
			addButton.setText(getString(R.string.already_in_watchlist_button));
			addButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Toast.makeText(getActivity().getApplicationContext(),getString(R.string.already_in_watchlist),Toast.LENGTH_LONG).show();
				}
			});
		}
				
		return mRootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
//	@Override
//	public void onDestroy() {
//		if(task != null) {
//			try {
//				task.cancel(true);
//			} catch (Exception e) {
//				// exception
//			}
//		}
//		super.onDestroy();
//	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
	  super.setUserVisibleHint(isVisibleToUser);
	  if (isVisibleToUser) 
	  {
		  final Handler handler = new Handler();
		  handler.postDelayed(new Runnable() {
		      @Override
		      public void run() {
		          // Do something after 1s = 500ms
		    	  try{
						int tvRageIdInt = Integer.parseInt(getArguments().getString(TrendingEntry.COLUMN_TVRAGE_ID));
						//Task for the remaining views!
						if(mRootView==null){
//							Log.i("WTF", "Still no rootview");
							// Analytics
				        	Tracker t = ((EpisodeCountdown) getActivity().getApplication()).getTracker(
				                TrackerName.APP_TRACKER);
				            // Set screen name.
				            t.setScreenName("Jugaar Failed. Still no rootview");
				            // Send a screen view.
				            t.send(new HitBuilders.ScreenViewBuilder().build());
				            
						}
						
						else {
							SearchDetailAsyncTask detailAsyncTask = new SearchDetailAsyncTask(getActivity(),
									mRootView, tvRageIdInt, true );
							detailAsyncTask.execute();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
		      }
		  }, 500);  
	  }
	  else {
		  
	  }
}

}
