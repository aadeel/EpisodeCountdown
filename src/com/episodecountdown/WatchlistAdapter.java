package com.episodecountdown;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.episodecountdown.R;
import com.squareup.picasso.Picasso;

public class WatchlistAdapter extends CursorAdapter{
	
	public WatchlistAdapter(Context context, Cursor c, int flags){
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		
		//Combining Title and Year
		String titleString = cursor.getString(WatchlistFragment.COL_TITLE);
		if (titleString.contains(" (")) {
			// Do nothing
		}
		else
			titleString = titleString.concat(" (" + cursor.getString(WatchlistFragment.COL_YEAR) + ")");
		viewHolder.title.setText(titleString);
		
		String country = cursor.getString(WatchlistFragment.COL_COUNTRY);
		if (country == "null")
			viewHolder.country.setText("N/A");
		else {
			viewHolder.country.setText(country);
		}
		
		viewHolder.runtime.setText(cursor.getString(WatchlistFragment.COL_RUNTIME) + " Minutes");
		
		//get Show status
		String statuString = cursor.getString(WatchlistFragment.COL_STATUS);
		viewHolder.status.setText(statuString);
		
		String genreString = cursor.getString(WatchlistFragment.COL_GENRES);
		viewHolder.genreTextView.setText(genreString);
		

		if (statuString.equalsIgnoreCase("ended")){
			viewHolder.airsTextView.setText(mContext.getString(R.string.show_ended_string));
			viewHolder.airsTextView.setBackgroundColor(mContext.getResources().getColor(R.color.palette_red_translucent));
		} else {
			viewHolder.airsTextView.setText("Airs " + cursor.getString(WatchlistFragment.COL_AIRDAY) + " at "
					+ cursor.getString(WatchlistFragment.COL_AIRTIME) + " on " + cursor.getString(WatchlistFragment.COL_NETWORK));
			viewHolder.airsTextView.setBackgroundColor(mContext.getResources().getColor(R.color.palette_green_translucent));
		}
		
//		String startedString = cursor.getString(WatchlistFragment.COL_FIRST_AIRED);
//		viewHolder.started.setText(Utility.getReadableDateString(Long.parseLong(startedString)));
		
		Picasso.with(mContext).load(cursor.getString(WatchlistFragment.COL_POSTER))
			.resize(0, 300)
			.into(viewHolder.poster);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.watchlist_list_item_layout, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		//Tag can be used to store any object!! But when reading, have to know what was storred there.
		view.setTag(viewHolder);
		return view;
	}

	public static class ViewHolder {
		public final ImageView poster;
		public final TextView title, status, country, runtime;
		public final TextView airsTextView, genreTextView;
		
		public ViewHolder (View view) {
			poster = (ImageView) view.findViewById(R.id.watchlist_list_show_poster);
			title = (TextView) view.findViewById(R.id.watchlist_list_show_title);
			status = (TextView) view.findViewById(R.id.watchlist_item_show_status);
			country = (TextView) view.findViewById(R.id.watchlist_item_show_origin_country);
			runtime = (TextView) view.findViewById(R.id.watchlist_item_show_runtime);
			//started = (TextView) view.findViewById(R.id.watchlist_item_show_started_textview);
			airsTextView = (TextView) view.findViewById(R.id.watchlist_list_show_airs_textview);
			genreTextView = (TextView) view.findViewById(R.id.watchlist_item_show_genre);
		}
	}
}