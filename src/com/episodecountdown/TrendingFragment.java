package com.episodecountdown;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.episodecountdown.data.TvWatchlistContract.TrendingEntry;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

public class TrendingFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	public static int TRENDING_LOADER = 0;
	public static final String POSTION_KEY = "TRENDING_POSITION_KEY";
	private static final long AUTO_UPDATE_INTERVAL = 1000*60*60*24; //24hours
	
	private View rootView;
	private TrendingAdapter mTrendingAdapter;
	
	private static final String[] TRENDING_COLUMNS = {
		TrendingEntry._ID,
		TrendingEntry.COLUMN_TITLE,
		TrendingEntry.COLUMN_YEAR, 
		TrendingEntry.COLUMN_FIRST_AIRED,
		TrendingEntry.COLUMN_COUNTRY,
		TrendingEntry.COLUMN_OVERVIEW,
		TrendingEntry.COLUMN_RUNTIME,
		TrendingEntry.COLUMN_AIR_DAY, 
		TrendingEntry.COLUMN_AIR_TIME,
		TrendingEntry.COLUMN_STATUS,
		TrendingEntry.COLUMN_POSTER,
		TrendingEntry.COLUMN_NETWORK,
		TrendingEntry.COLUMN_TVRAGE_ID,
		TrendingEntry.COLUMN_BANNER,
		TrendingEntry.COLUMN_RATINGS_PERCENTAGE,
		TrendingEntry.COLUMN_RATINGS_LOVED,
		TrendingEntry.COLUMN_RATINGS_HATED
	};
	
	public static final int COL_TRENDING_ID = 0;
	public static final int COL_TITLE = 1;
	public static final int COL_YEAR = 2;
	public static final int COL_FIRST_AIRED = 3;
	public static final int COL_COUNTRY = 4;
	public static final int COL_OVERVIEW = 5;
	public static final int COL_RUNTIME = 6;
	public static final int COL_AIRDAY = 7;
	public static final int COL_AIRTIME = 8;
	public static final int COL_STATUS = 9;
	public static final int COL_POSTER = 10;
	public static final int COL_NETWORK = 11;
	public static final int COL_TVRAGE_ID = 12;
	public static final int COL_BANNER = 13;
	public static final int COL_RATING_PERCENTAGE = 14;
	public static final int COL_RATING_LOVED = 15;
	public static final int COL_RATING_HATED = 16;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	rootView = inflater.inflate(R.layout.fragment_trending, container, false);
		
    	final PullToRefreshGridView gridView = (PullToRefreshGridView) rootView.findViewById(R.id.myGrid);
    	
    	//Setting adapter
    	mTrendingAdapter = new TrendingAdapter(getActivity(), null, 0);
    	
    	gridView.setAdapter(mTrendingAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Cursor cursor = mTrendingAdapter.getCursor();
				if(cursor != null && cursor.moveToPosition(position)) {
					Intent intent = new Intent(getActivity(), TrendingDetailActivity.class);
					intent.putExtra(POSTION_KEY, position);
					startActivity(intent);
//					FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//		        	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		        	fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
//		        	fragmentTransaction.addToBackStack(null);
//		        	fragmentTransaction.replace(R.id.container, new TrendingViewPagerFragment(position));
//		        	fragmentTransaction.commit();
				}
			}
		});
		
		gridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
			
        	@Override public void onRefresh(PullToRefreshBase<GridView> refreshView) {
        	    	updateTrending(false);
        	}
		});

        //Update everytime on createView!! Till a better alternative.
//        swipeLayout.setRefreshing(true);
//        swipeLayout.setEnabled(false);
//    	updateTrending(true);
        
        TextView emptyView = (TextView)rootView.findViewById(R.id.empty_trending_gridview);
    	gridView.setEmptyView(emptyView);
		
    	//Daily AutoUpdate
    	String lastUpdateKey = getString(R.string.last_trending_update_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        long lastUpdateTime = sharedPreferences.getLong(lastUpdateKey, 0l);
        boolean timeToUpdate = (System.currentTimeMillis() - lastUpdateTime >= AUTO_UPDATE_INTERVAL);
		if(timeToUpdate)
			updateTrending(true);
		
    	return rootView;
    }
    
    private void updateTrending(boolean silent){
    	TrendingAsyncTask asyncTask = new TrendingAsyncTask(getActivity(), rootView, silent);
    	asyncTask.execute();
    }
    
    @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(TRENDING_LOADER, null, this);
	}
    
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
	// Now create and return a CursorLoader that will take care of
	// creating a Cursor for the data being displayed.
	String sortOrder = TrendingEntry.COLUMN_POSITION + " ASC";	
	
	return new CursorLoader(getActivity(), TrendingEntry.CONTENT_URI,
			TRENDING_COLUMNS, null, null, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mTrendingAdapter.swapCursor(cursor);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mTrendingAdapter.swapCursor(null);
		
	}
}