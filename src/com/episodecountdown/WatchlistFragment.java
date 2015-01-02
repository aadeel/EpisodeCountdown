package com.episodecountdown;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;

public class WatchlistFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	public static int WATCHLIST_LOADER = 0;
	
	private View rootView;
	private WatchlistAdapter mWatchlistAdapter;
	
	private static final String[] WATCHLIST_COLUMNS = {
		WatchlistEntry.COLUMN_POSTER,
		WatchlistEntry.TABLE_NAME + "." + WatchlistEntry.COLUMN_TITLE,
		WatchlistEntry.COLUMN_YEAR, 
		WatchlistEntry.COLUMN_STATUS,
		WatchlistEntry.COLUMN_RUNTIME,
		WatchlistEntry.COLUMN_COUNTRY,
		WatchlistEntry.COLUMN_NETWORK,
		WatchlistEntry.COLUMN_AIR_TIME, 
		WatchlistEntry.COLUMN_AIR_DAY,
		WatchlistEntry.TABLE_NAME + "." + WatchlistEntry.COLUMN_TVRAGE_ID,
		WatchlistEntry._ID,
		WatchlistEntry.COLUMN_FIRST_AIRED,
		WatchlistEntry.COLUMN_GENRES,
		WatchlistEntry.COLUMN_SHOW_TIMEZONE,
	};
	
	public static final int COL_POSTER = 0;
	public static final int COL_TITLE = 1;
	public static final int COL_YEAR = 2;
	public static final int COL_STATUS = 3;
	public static final int COL_RUNTIME = 4;
	public static final int COL_COUNTRY = 5;
	public static final int COL_NETWORK = 6;
	public static final int COL_AIRTIME = 7;
	public static final int COL_AIRDAY = 8;
	public static final int COL_TVRAGE_ID = 9;
	public static final int COL_WATCHLIST_ID = 10;
	public static final int COL_FIRST_AIRED = 11;
	public static final int COL_GENRES = 12;
	public static final int COL_TIMEZONE = 13;
	
    public WatchlistFragment() {
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	rootView = inflater.inflate(R.layout.fragment_watchlist, container, false);
		
    	final GridView listView = (GridView) rootView.findViewById(R.id.listview_watchlist);
    	//Setting header, footer margins has to be before adapter.
//    	listView.addHeaderView(new View(getActivity()), null, false);
//    	listView.addFooterView(new View(getActivity()), null, false);
    	
    	//Setting adapter
    	mWatchlistAdapter = new WatchlistAdapter(getActivity(), null, 0);
    	
    	listView.setAdapter(mWatchlistAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// List header adjustment!!
//				position--;
				
				Cursor cursor = mWatchlistAdapter.getCursor();
				if(cursor != null && cursor.moveToPosition(position)) {
					Intent intent = new Intent(getActivity(), WatchlistDetailActivity.class);
					Bundle extras = new Bundle();
					extras.putString(WatchlistDetailFragment.WATCHLIST_TABLE_ID, cursor.getString(COL_WATCHLIST_ID));
					extras.putString(EpisodeListFragment.EPISODE_LIST_KEY, cursor.getString(COL_TVRAGE_ID));
					extras.putString(WatchlistDetailActivity.WATCHLIST_DETAIL_POSTER_KEY, cursor.getString(COL_POSTER));
					extras.putString(EpisodeListFragment.EPISODE_TIMEZONE_KEY, cursor.getString(COL_TIMEZONE));
					intent.putExtras(extras);
					startActivity(intent);
				}
			}
		});
		
		//Empty Listview Image
    	TextView emptyView = (TextView)rootView.findViewById(R.id.empty_watchlist_listview);
    	listView.setEmptyView(emptyView);
		
    	return rootView;
    }

    @Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(WATCHLIST_LOADER, null, this);
	}
    
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
	// Now create and return a CursorLoader that will take care of
	// creating a Cursor for the data being displayed.
	return new CursorLoader(getActivity(), WatchlistEntry.CONTENT_URI,
			WATCHLIST_COLUMNS, null, null, WatchlistEntry.COLUMN_TITLE + " ASC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mWatchlistAdapter.swapCursor(cursor);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mWatchlistAdapter.swapCursor(null);
		
	}
}