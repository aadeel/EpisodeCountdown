package com.episodecountdown;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.episodecountdown.data.TvWatchlistContract;
import com.episodecountdown.data.TvWatchlistContract.EpisodesEntry;
import com.episodecountdown.data.TvWatchlistContract.WatchlistEntry;
import com.episodecountdown.data.TvWatchlistDbHelper;
import com.episodecountdown.library.TextViewEx;
import com.squareup.picasso.Picasso;

public class WatchlistDetailFragment extends Fragment implements
		LoaderCallbacks<Cursor> {

	private static int WATCHLIST_DETAIL_LOADER = 0;
	public static final String WATCHLIST_TABLE_ID = "DETAIL_ID_KEY";
	private String mWatchlistTable_ID;
	private View mRootView;
	// AdView mAdView;

	private static final String[] WATCHLIST_COLUMNS = {
			WatchlistEntry.COLUMN_POSTER, WatchlistEntry.COLUMN_BANNER,
			WatchlistEntry.TABLE_NAME + "." + WatchlistEntry.COLUMN_TITLE,
			WatchlistEntry.COLUMN_YEAR, WatchlistEntry.COLUMN_OVERVIEW,
			WatchlistEntry.COLUMN_RATINGS_PERCENTAGE,
			WatchlistEntry.COLUMN_RATINGS_LOVED,
			WatchlistEntry.COLUMN_RATINGS_HATED, WatchlistEntry.COLUMN_GENRES,
			WatchlistEntry.COLUMN_FIRST_AIRED, WatchlistEntry.COLUMN_STATUS,
			WatchlistEntry.COLUMN_RUNTIME, WatchlistEntry.COLUMN_COUNTRY,
			WatchlistEntry.COLUMN_NETWORK, WatchlistEntry.COLUMN_AIR_TIME,
			WatchlistEntry.COLUMN_AIR_DAY, WatchlistEntry.COLUMN_TVDB_ID,
			WatchlistEntry.TABLE_NAME + "." + WatchlistEntry.COLUMN_TVRAGE_ID,
			WatchlistEntry.COLUMN_SHOW_TIMEZONE };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle arguments = getArguments();
		if (arguments != null)
			mWatchlistTable_ID = arguments.getString(WATCHLIST_TABLE_ID);

		if (savedInstanceState != null) {
            mWatchlistTable_ID = savedInstanceState.getString(WATCHLIST_TABLE_ID);
        }
		
		mRootView = inflater
				.inflate(R.layout.fragment_detail, container, false);

		return mRootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
            mWatchlistTable_ID = savedInstanceState.getString(WATCHLIST_TABLE_ID);
        }
		Bundle arguments = getArguments();
		if (arguments != null && arguments.containsKey(WATCHLIST_TABLE_ID)) {
			getLoaderManager().initLoader(WATCHLIST_DETAIL_LOADER, null, this);
		}
		// mAdView = (AdView) getView().findViewById(R.id.adView);
		// mAdView.setAdListener(new AdListener() {
		// @Override
		// public void onAdLoaded() {
		// super.onAdLoaded();
		// mAdView.setVisibility(View.VISIBLE);
		// }
		// });
		// AdRequest adRequest = new AdRequest.Builder().build();
		// mAdView.loadAd(adRequest);
	}

	@Override
	public void onResume() {
		super.onResume();
		Bundle arguments = getArguments();
		if (arguments != null && arguments.containsKey(WATCHLIST_TABLE_ID)) {
			getLoaderManager().restartLoader(WATCHLIST_DETAIL_LOADER, null,
					this);
		}
	}
	
	@Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(WATCHLIST_TABLE_ID, mWatchlistTable_ID);
        super.onSaveInstanceState(outState);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri watchlistForTvrageIdUri = TvWatchlistContract.WatchlistEntry
				.buildWatchlistUri(Integer.parseInt(mWatchlistTable_ID));

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(getActivity(), watchlistForTvrageIdUri,
				WATCHLIST_COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (data != null && data.moveToFirst()) {

			// String posterUrl =
			// data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_POSTER));
			String bannerUrl = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_BANNER));
			String title = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_TITLE));
			String year = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_YEAR));
			String overview = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_OVERVIEW));
			String percentageString = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_RATINGS_PERCENTAGE));
			String lovedString = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_RATINGS_LOVED));
			String hatedString = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_RATINGS_HATED));
			String genresString = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_GENRES));
			String primiered = Utility
					.getReadableDateString(Integer.parseInt(data.getString(data
							.getColumnIndex(WatchlistEntry.COLUMN_FIRST_AIRED))));
			String status = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_STATUS));
			String runtime = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_RUNTIME));
			String country = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_COUNTRY));
			String network = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_NETWORK));
			String airtime = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_AIR_TIME));
			String airday = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_AIR_DAY));
			// String utc_airtime =
			// data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_AIR_TIME_UTC));
			// String utc_airday =
			// data.getString(data.getColumnIndex(WatchlistEntry.COLUMN_AIR_DAY_UTC));
			final String tvrageIdString = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_TVRAGE_ID));
			final String tvdbIdString = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_TVDB_ID));
			String showTimeZone = data.getString(data
					.getColumnIndex(WatchlistEntry.COLUMN_SHOW_TIMEZONE));
			if (showTimeZone == null) {
				// MAKE PROPER FALLBACK HERE.
			}

			// Update Asynctask. Will check itself.
			if (!status.equalsIgnoreCase("ended")) {
				WatchlistShowAsyncTask task = new WatchlistShowAsyncTask(
						getActivity(), tvdbIdString);
				task.execute();
			}

			// Set Action Bar Title
			((WatchlistDetailActivity) getActivity()).getSupportActionBar()
					.setTitle(title);

			// Hide Progress Bar
			mRootView.findViewById(R.id.detail_show_progress_bar)
					.setVisibility(View.GONE);

			// ImageView posterView = (ImageView)
			// mRootView.findViewById(R.id.fragment_detail_poster);
			// // Using Picasso to download image
			// Picasso.with(getActivity()).load(posterUrl).resize(0, 500)
			// .into(posterView);

			ImageView bannerView = (ImageView) mRootView
					.findViewById(R.id.fragment_detail_banner);
			// Using Picasso to download image
			Picasso.with(getActivity()).load(bannerUrl)
					.placeholder(R.drawable.placeholder_banner).resize(0, 140)
					.into(bannerView);

			TextView titleView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_title);
			if (!title.contains(" ("))
				title = title + " (" + year + ")";
			titleView.setText(title);

			TextViewEx overviewView = (TextViewEx) mRootView
					.findViewById(R.id.fragment_detail_overview);
			overviewView.setText(overview, true);

			// Ratings
			TextView percentage = (TextView) mRootView
					.findViewById(R.id.fragment_detail_ratting);
			TextView loved = (TextView) mRootView
					.findViewById(R.id.fragment_detail_ratting_positive);
			TextView hated = (TextView) mRootView
					.findViewById(R.id.fragment_detail_ratting_negative);

			percentage.setText(percentageString + "%");
			loved.setText(lovedString);
			hated.setText(hatedString);

			TextView genresView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_genre);
			genresView.setText(genresString);

			TextView premieredView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_started);
			premieredView.setText(primiered);

			// Changing ended View to STATUS view
			TextView statusTitleView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_ended_title);
			statusTitleView.setText("Status");

			TextView statusView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_ended);
			statusView.setText(status);

			TextView runtimeView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_runtime);
			runtimeView.setText(runtime);

			TextView countryView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_origin_country);
			countryView.setText(country);

			TextView networkView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_network);
			networkView.setText(network);

			TextView airtimeView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_airtime);
			airtimeView.setText(airtime);

			TextView airdayView = (TextView) mRootView
					.findViewById(R.id.fragment_detail_show_airday);
			airdayView.setText(airday);

			// Hiding Some Views
			Button addButton = (Button) mRootView
					.findViewById(R.id.fragment_detail_watchlist_button);
			addButton.setText("Remove From Watchlist");
			addButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					new AlertDialog.Builder(getActivity())
							.setTitle("Remove From Watchlist")
							.setMessage(
									"Are you sure you want to remove this show from your watchlist?")
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											new Thread(new Runnable() {
												public void run() {
													getActivity()
															.getContentResolver()
															.delete(WatchlistEntry.CONTENT_URI,
																	WatchlistEntry.COLUMN_TVDB_ID
																			+ " = ? ",
																	new String[] { tvdbIdString });
													getActivity()
															.getContentResolver()
															.delete(EpisodesEntry.CONTENT_URI,
																	EpisodesEntry.COLUMN_TVRAGE_ID
																			+ " = ? ",
																	new String[] { tvrageIdString });
													SharedPreferences sharedPreferences = PreferenceManager
															.getDefaultSharedPreferences(getActivity());
													SharedPreferences.Editor editor = sharedPreferences
															.edit();
													editor.remove(getString(R.string.watchlist_show_id_key)
															+ tvdbIdString);
													editor.commit();
												}
											}).start();
											getActivity().finish();
										}
									})
							.setNegativeButton(android.R.string.no, null)
							.setIcon(R.drawable.ic_dialog_alert_holo_light)
							.show();
				}
			});

			// -------------------------
			// Setting local airtime and airday

			// -------------------------
			if (showTimeZone == null)
				return;
			if (showTimeZone.trim().isEmpty())
				return;

			Cursor cursor = null;
			TvWatchlistDbHelper mHelper = new TvWatchlistDbHelper(getActivity()
					.getApplicationContext());
			SQLiteDatabase db = mHelper.getWritableDatabase();

			// Trying to get the last episode cursor.
			// THIS SUCCEDDED? WOW!
			String queryString = "select * "
					+ "from "
					+ EpisodesEntry.TABLE_NAME
					+ " where "
					+ EpisodesEntry.COLUMN_TVRAGE_ID
					+ "="
					+ tvrageIdString
					+ " and "
					+ EpisodesEntry.COLUMN_AIRDATE
					+ " < "
					+ Calendar.getInstance(TimeZone.getTimeZone(showTimeZone))
							.getTimeInMillis() + " order by "
					+ EpisodesEntry.COLUMN_AIRDATE + " DESC, "
					+ EpisodesEntry.COLUMN_EPISODE_NO_FROM_START_INTEGER
					+ " DESC " + " LIMIT 1";
			// Log.d("queryString", queryString.toString());

			try {
				cursor = db.rawQuery(queryString, null);
				if (cursor.moveToLast()) {

					String titleString = cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_TITLE));
					String numberString = cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_EPISODE_NO));
					String seasonString = cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_SEASON_NO));
					TextView previousEpisodeTitle = (TextView) mRootView
							.findViewById(R.id.fragment_detail_show_last_episode_title);
					previousEpisodeTitle
							.setText((Utility.getSXXEXXFormatedString(
									seasonString, numberString))
									+ "\n"
									+ titleString);

					TextView previousEpisodeDate = (TextView) mRootView
							.findViewById(R.id.fragment_detail_show_last_episode_date);
					previousEpisodeDate
							.setText(Utility.getTvrageReadableDateString(
									cursor.getString(cursor
											.getColumnIndex(EpisodesEntry.COLUMN_AIRDATE)),
									showTimeZone));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}

			// Trying to get the next episode cursor.
			// THIS SUCCEDDED? WOW!
			queryString = "select * "
					+ "from "
					+ EpisodesEntry.TABLE_NAME
					+ " where "
					+ EpisodesEntry.COLUMN_TVRAGE_ID
					+ "="
					+ tvrageIdString
					+ " and "
					+ EpisodesEntry.COLUMN_AIRDATE
					+ " > "
					+ Calendar.getInstance(TimeZone.getTimeZone(showTimeZone))
							.getTimeInMillis() + " order by "
					+ EpisodesEntry.COLUMN_AIRDATE + " ASC LIMIT 1";
			// Log.d("queryString", queryString.toString());

			try {
				cursor = db.rawQuery(queryString, null);
				if (cursor.moveToFirst()) {

					String titleString = cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_TITLE));
					String numberString = cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_EPISODE_NO));
					numberString = (numberString.length() == 1) ? "0"
							+ numberString : numberString;
					String seasonString = cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_SEASON_NO));
					seasonString = (seasonString.length() == 1) ? "0"
							+ seasonString : seasonString;
					TextView nextEpisodeTitle = (TextView) mRootView
							.findViewById(R.id.fragment_detail_show_next_episode_title);
					nextEpisodeTitle.setText("S" + seasonString + "E"
							+ numberString + "\n" + titleString);

					TextView nextEpisodeDate = (TextView) mRootView
							.findViewById(R.id.fragment_detail_show_next_episode_date);
					nextEpisodeDate
							.setText(Utility.getTvrageReadableDateString(
									cursor.getString(cursor
											.getColumnIndex(EpisodesEntry.COLUMN_AIRDATE)),
									showTimeZone));

					// Trying to set the countdown Timer
					// Set first and last episodes for this show
					long waitTime = Long.parseLong(cursor.getString(cursor
							.getColumnIndex(EpisodesEntry.COLUMN_AIRDATE)))
							- Calendar.getInstance(
									TimeZone.getTimeZone(showTimeZone))
									.getTimeInMillis();
					if (waitTime > 0) {
						final TextView countdownDaysView = (TextView) mRootView
								.findViewById(R.id.fragment_detail_countdown_days);
						final TextView countdownHoursView = (TextView) mRootView
								.findViewById(R.id.fragment_detail_countdown_hours);
						final TextView countdownMinsView = (TextView) mRootView
								.findViewById(R.id.fragment_detail_countdown_minutes);
						final TextView countdownSecondsView = (TextView) mRootView
								.findViewById(R.id.fragment_detail_countdown_seconds);

						new CountDownTimer(waitTime, 1000) {
							public void onTick(long millisUntilFinished) {

								countdownDaysView.setText(TimeUnit.MILLISECONDS
										.toDays(millisUntilFinished) + "");

								countdownHoursView
										.setText(TimeUnit.MILLISECONDS
												.toHours(millisUntilFinished)
												- TimeUnit.DAYS
														.toHours(TimeUnit.MILLISECONDS
																.toDays(millisUntilFinished))
												+ "");

								countdownMinsView
										.setText(TimeUnit.MILLISECONDS
												.toMinutes(millisUntilFinished)
												- TimeUnit.HOURS
														.toMinutes(TimeUnit.MILLISECONDS
																.toHours(millisUntilFinished))
												+ "");

								countdownSecondsView
										.setText(TimeUnit.MILLISECONDS
												.toSeconds(millisUntilFinished)
												- TimeUnit.MINUTES
														.toSeconds(TimeUnit.MILLISECONDS
																.toMinutes(millisUntilFinished))
												+ "");
							}

							public void onFinish() {
								// Do NOTHIng
							}
						}.start();
					} else {
						// Here if next episode found. But some error in wait
						// time calculation
						mRootView.findViewById(
								R.id.fragment_detail_countdown_linear_layout)
								.setVisibility(View.GONE);
						mRootView.findViewById(
								R.id.fragment_detail_countdown_title)
								.setVisibility(View.GONE);
					}
				} else {
					// Here if next episode not found.
					if (status.equalsIgnoreCase("ended")) {
						TextView textView = (TextView) mRootView
								.findViewById(R.id.fragment_detail_countdown_title);
						textView.setText(getString(R.string.show_ended_string));
					} else {
						TextView textView = (TextView) mRootView
								.findViewById(R.id.fragment_detail_countdown_title);
						textView.setText(getString(R.string.show_tba_string));
					}
					mRootView.findViewById(
							R.id.fragment_detail_countdown_linear_layout)
							.setVisibility(View.GONE);
					// mRootView.findViewById(R.id.fragment_detail_countdown_title).setVisibility(View.GONE);
					mRootView.findViewById(
							R.id.fragment_detail_show_next_episode_title)
							.setVisibility(View.GONE);
					mRootView.findViewById(
							R.id.fragment_detail_next_episode_heading)
							.setVisibility(View.GONE);
					mRootView.findViewById(
							R.id.fragment_detail_show_next_episode_date)
							.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				if (db != null)
					db.close();
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Nothing
	}

	// @Override
	// public void onDestroy() {
	// if (mAdView != null) {
	// mAdView.destroy();
	// }
	// super.onDestroy();
	// }
	//
	// @Override
	// public void onPause() {
	// if (mAdView != null) {
	// mAdView.pause();
	// }
	// super.onPause();
	// }
	//
	// @Override
	// public void onResume() {
	// super.onResume();
	// if (mAdView != null) {
	// mAdView.resume();
	// }
	// }
}
