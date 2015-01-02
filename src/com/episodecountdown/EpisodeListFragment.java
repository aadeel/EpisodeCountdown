package com.episodecountdown;

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
import android.widget.ListView;

import com.episodecountdown.R;
import com.episodecountdown.data.TvWatchlistContract.EpisodesEntry;

public class EpisodeListFragment extends Fragment implements
		LoaderCallbacks<Cursor> {
	
	public static final String EPISODE_LIST_KEY = "TVRAGE_ID_FOR_EPISODE";
	public static final String EPISODE_TIMEZONE_KEY = "TIMEZONE_FOR_EPISODE";
	public static int EPISODE_LIST_LOADER = 0;
	
	public String mTimezoneString;
	
	private String mTVRAGE_ID;
	private View mRootView;
	private EpisodeListAdapter mEpisodeListAdapter;
	
	private static final String[] EPISODE_LIST_COLUMNS = { EpisodesEntry._ID,
			EpisodesEntry.COLUMN_AIRDATE, EpisodesEntry.COLUMN_EPISODE_NO,
			EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER,
			EpisodesEntry.COLUMN_SCREENCAP, EpisodesEntry.COLUMN_SEASON_NO,
			EpisodesEntry.COLUMN_TITLE };

	public static final int COL_DB_ID = 0;
	public static final int COL_AIRDATE = 1;
	public static final int COL_EPISODE = 2;
	public static final int COL_EPISODE_FROM_START = 3;
	public static final int COL_SCREENCAP = 4;
	public static final int COL_SEASON = 5;
	public static final int COL_TITLE = 6;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Bundle arguments = getArguments();
		if(arguments != null)
			mTVRAGE_ID = arguments.getString(EPISODE_LIST_KEY);
			mTimezoneString = arguments.getString(EPISODE_TIMEZONE_KEY);
		
		mRootView = inflater.inflate(R.layout.fragment_episode_list, container,
				false);

		ListView listView = (ListView) mRootView
				.findViewById(R.id.listview_episode_list);
		// Setting header, footer margins has to be before adapter.
		// listView.addHeaderView(new View(getActivity()), null, false);
		// listView.addFooterView(new View(getActivity()), null, false);

		// Setting adapter
		mEpisodeListAdapter = new EpisodeListAdapter(getActivity(), null, 0, mTimezoneString);

		listView.setAdapter(mEpisodeListAdapter);
		// listView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position,
		// long id) {
		// // List header adjustment!!
		// // position--;
		//
		// Cursor cursor = mWatchlistAdapter.getCursor();
		// if(cursor != null && cursor.moveToPosition(position)) {
		// Intent intent = new Intent(getActivity(),
		// WatchlistDetailActivity.class);
		// intent.putExtra(WatchlistDetailFragment.WATCHLIST_TABLE_ID,
		// cursor.getString(COL_WATCHLIST_ID));
		// startActivity(intent);
		// }
		// }
		// });

		// Empty Listview Image
		// TextView emptyView =
		// (TextView)rootView.findViewById(R.id.empty_watchlist_listview);
		// listView.setEmptyView(emptyView);

		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle arguments = getArguments();
		if(arguments != null && arguments.containsKey(EPISODE_LIST_KEY)){
			getLoaderManager().initLoader(EPISODE_LIST_LOADER, null, this);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), EpisodesEntry.CONTENT_URI,
				EPISODE_LIST_COLUMNS, EpisodesEntry.COLUMN_TVRAGE_ID + "= ?", new String[]{mTVRAGE_ID},
				EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mEpisodeListAdapter.swapCursor(cursor);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mEpisodeListAdapter.swapCursor(null);
	}
}