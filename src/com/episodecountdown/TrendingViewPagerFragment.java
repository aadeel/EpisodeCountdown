package com.episodecountdown;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.episodecountdown.data.TvWatchlistContract.TrendingEntry;

public class TrendingViewPagerFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private ViewPager mPager;
	private View mRootView;
	int mCursorPosition;
//	AdView mAdView;
	private CursorPagerAdapter<TrendingDetailFragment> mCursorPagerAdapter;
	
	private static final String CURRENT_POSITION_KEY = "currentPosition";

	public static int TRENDING_LOADER = 0;

	private static final String[] TRENDING_COLUMNS = { TrendingEntry._ID,
			TrendingEntry.COLUMN_TITLE, TrendingEntry.COLUMN_YEAR,
			TrendingEntry.COLUMN_FIRST_AIRED, TrendingEntry.COLUMN_COUNTRY,
			TrendingEntry.COLUMN_OVERVIEW, TrendingEntry.COLUMN_RUNTIME,
			TrendingEntry.COLUMN_AIR_DAY, TrendingEntry.COLUMN_AIR_TIME,
			TrendingEntry.COLUMN_STATUS, TrendingEntry.COLUMN_POSTER,
			TrendingEntry.COLUMN_NETWORK, TrendingEntry.COLUMN_TVRAGE_ID,
			TrendingEntry.COLUMN_TVDB_ID, TrendingEntry.COLUMN_BANNER,
			TrendingEntry.COLUMN_GENRES, TrendingEntry.COLUMN_STATUS,
			TrendingEntry.COLUMN_RATINGS_PERCENTAGE,
			TrendingEntry.COLUMN_RATINGS_LOVED,
			TrendingEntry.COLUMN_RATINGS_HATED };

	public TrendingViewPagerFragment() {
	}

	public TrendingViewPagerFragment(int position) {
		mCursorPosition = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (savedInstanceState != null)
			mCursorPosition = savedInstanceState.getInt(CURRENT_POSITION_KEY);
		
		mRootView = inflater.inflate(R.layout.fragment_trending_viewpager,
				container, false);
		
		String[] titleStrings = null;
		
		//GETTING The titles here. 
		Cursor titlesCursor = getActivity().getContentResolver().query(TrendingEntry.CONTENT_URI, TRENDING_COLUMNS, null, null, TrendingEntry.COLUMN_POSITION + " ASC");
		if(titlesCursor!= null && titlesCursor.moveToFirst()){
			titleStrings = new String[titlesCursor.getCount()];
			int i = 0;
			do {
			    String title = titlesCursor.getString(titlesCursor.getColumnIndex(TrendingEntry.COLUMN_TITLE));
			    titleStrings[i] = title;
			    i++;
			} while(titlesCursor.moveToNext());
		}
		if(titlesCursor != null && !titlesCursor.isClosed())
			titlesCursor.close();
		
		mPager = (ViewPager) mRootView.findViewById(R.id.pager);
		mCursorPagerAdapter = new CursorPagerAdapter<TrendingDetailFragment>(
				getChildFragmentManager(), TrendingDetailFragment.class,
				TRENDING_COLUMNS, null, titleStrings);
		mPager.setAdapter(mCursorPagerAdapter);
		mPager.setOffscreenPageLimit(1);
		
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(TRENDING_LOADER, null, this);
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
	}
	
//	@Override
//	public void onDestroy() {
//		if (mAdView != null) {
//            mAdView.destroy();
//        }
//        super.onDestroy();
//	}
//
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(CURRENT_POSITION_KEY, mPager.getCurrentItem());
		super.onSaveInstanceState(outState);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		String sortOrder = TrendingEntry.COLUMN_POSITION + " ASC";
		return new CursorLoader(getActivity().getApplicationContext(),
				TrendingEntry.CONTENT_URI, TRENDING_COLUMNS, null, null,
				sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mCursorPagerAdapter.swapCursor(cursor);
		mPager.setCurrentItem(mCursorPosition, false);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mCursorPagerAdapter.swapCursor(null);

	}
}
