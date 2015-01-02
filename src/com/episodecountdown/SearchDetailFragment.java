package com.episodecountdown;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;
import com.episodecountdown.data.TvWatchlistDbHelper;
import com.episodecountdown.library.TextViewEx;
import com.squareup.picasso.Picasso;

public class SearchDetailFragment extends Fragment {

	View mRootView;
//	AdView mAdView; 
//	WatchlistShowAsyncTask task;
	
	public SearchDetailFragment() {
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 mRootView = inflater.inflate(R.layout.fragment_detail, container,
				false);
		 
		//Hide unused Views. Asyntask will make them visible again. 
		mRootView.findViewById(R.id.fragment_detail_countdown_linear_layout).setVisibility(View.GONE);
		mRootView.findViewById(R.id.fragment_detail_previous_next_episode_linear_layout).setVisibility(View.GONE);
		mRootView.findViewById(R.id.fragment_detail_countdown_title).setVisibility(View.GONE);
		
		// Get Intent Extras
		SearchResultModel searchResultModel = getActivity().getIntent().getParcelableExtra(SearchFragment.SEARCH_DETAIL_KEY);
		
		String posterUrl = searchResultModel.getPosterUrl();
		String bannerUrl = searchResultModel.getBannerUrl();
		String title = searchResultModel.getTitle();
		int year = searchResultModel.getYear();
		String overview = searchResultModel.getOveview();
		String percentageString = searchResultModel.getRatting_percentage();
		String lovedString = searchResultModel.getLoved();
		String hatedString = searchResultModel.getHated();
		String genresString = searchResultModel.getGenres();
		String primiered = Utility.getReadableDateString(searchResultModel.getFirstAired());
		boolean ended = searchResultModel.getEnded();
		int runtime = searchResultModel.getRuntime();
		final int tvdbId = searchResultModel.getTvdbId();
		final int tvRageId = searchResultModel.getTvRageId();
		String country = searchResultModel.getCountry();
		String network = searchResultModel.getNetwork();
		String airtime = searchResultModel.getAirTime();
		String airday = searchResultModel.getAirDay();
		
		//Set Action Bar Title
		((SearchDetailActivity)getActivity()).getSupportActionBar().setTitle(title);
		
		ImageView posterView = (ImageView) mRootView.findViewById(R.id.fragment_detail_poster);
		// Using Picasso to download image
		Picasso.with(getActivity()).load(posterUrl).resize(0, 500)
		.into(posterView);
		
		ImageView bannerView = (ImageView) mRootView.findViewById(R.id.fragment_detail_banner);
		// Using Picasso to download image
		Picasso.with(getActivity()).load(bannerUrl).placeholder(R.drawable.placeholder_banner)
		.resize(0, 100)
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
		
		TextView endedView = (TextView) mRootView.findViewById(R.id.fragment_detail_show_ended);
		endedView.setText((ended)? "Yes":"No");
		
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
		        
		String query = "SELECT COUNT(*) FROM " + WatchlistEntry.TABLE_NAME + " WHERE " + WatchlistEntry.COLUMN_TVDB_ID + " = '" + tvdbId + "'";
		      
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		int inWatchist = cursor.getInt(0);
		cursor.close();
		db.close();
		        
		Button addButton = (Button) mRootView.findViewById(R.id.fragment_detail_watchlist_button);
		if(inWatchist == 0) {
			addButton.setOnClickListener(new OnClickListener() {
						
				@Override
				public void onClick(View arg0) {
				WatchlistShowAsyncTask task = new WatchlistShowAsyncTask(getActivity(), tvdbId+"", true);
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

		//Task for the remaining views!
		SearchDetailAsyncTask detailAsyncTask = new SearchDetailAsyncTask(getActivity(),
				mRootView, tvRageId);
		detailAsyncTask.execute();
		return mRootView;
	}
	
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		mAdView = (AdView) getView().findViewById(R.id.adView);
//		mAdView.setAdListener(new AdListener() {
//	        @Override
//	        public void onAdLoaded() {
//	            super.onAdLoaded();
//	            mAdView.setVisibility(View.VISIBLE);
//	        }
//	    });
//		AdRequest adRequest = new AdRequest.Builder().build();
//		mAdView.loadAd(adRequest);
//	}
//	@Override
//	public void onDestroy() {
//		if (mAdView != null) {
//            mAdView.destroy();
//        }
//		if(task != null) {
//			try {
//				task.cancel(true);
//			} catch (Exception e) {
//				// exception
//			}
//		}
//        super.onDestroy();
//	}

//	@Override
//	public void onPause() {
//		if (mAdView != null) {
//            mAdView.pause();
//        }
//        super.onPause();
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//        if (mAdView != null) {
//            mAdView.resume();
//        }
//	}
}
