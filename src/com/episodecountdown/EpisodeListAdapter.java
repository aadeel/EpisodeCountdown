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

public class EpisodeListAdapter extends CursorAdapter {
	
	private String mTimezoneString;

	public EpisodeListAdapter(Context context, Cursor c, int flags, String timezone) {
		super(context, c, flags);
		mTimezoneString = timezone;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.episode_list_item_layout, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		final ViewHolder viewHolder = (ViewHolder) view.getTag();
		
		switch (cursor.getInt(EpisodeListFragment.COL_EPISODE_FROM_START)%5) {
		case 0:
			viewHolder.title.setBackgroundResource(R.color.palette_blue_translucent);
			break;
		case 1:
			viewHolder.title.setBackgroundResource(R.color.palette_green_translucent);
			break;
		case 2:
			viewHolder.title.setBackgroundResource(R.color.palette_orange_translucent);
			break;
		case 3:
			viewHolder.title.setBackgroundResource(R.color.palette_purple_translucent);
			break;
		case 4:
			viewHolder.title.setBackgroundResource(R.color.palette_red_translucent);
			break;
		}

		String season = cursor.getString(EpisodeListFragment.COL_SEASON);
		String episode = cursor.getString(EpisodeListFragment.COL_EPISODE);
		viewHolder.season.setText(Utility.getSXXEXXFormatedString(season, episode));
		
		String titleString = cursor.getString(EpisodeListFragment.COL_TITLE);
		viewHolder.title.setText(Utility.getSXXEXXFormatedString(season, episode) + ": " + titleString);
		
		viewHolder.airdate.setText(Utility.getSmallTvrageReadableDateString(cursor
				.getString(EpisodeListFragment.COL_AIRDATE), mTimezoneString));

		String episodeNumberFromStartString = cursor.getString(EpisodeListFragment.COL_EPISODE_FROM_START);
		viewHolder.episode.setText(episodeNumberFromStartString);

		String screenCap = cursor.getString(EpisodeListFragment.COL_SCREENCAP);
		if(screenCap == null){
			Picasso.with(mContext)
			.load(R.drawable.no_episode_image)
			.resize(0, 200).into(viewHolder.screenCap);
		}
		else {
			Picasso.with(mContext)
				.load(screenCap)
				.resize(0, 100).into(viewHolder.screenCap);
		}
	}

	public static class ViewHolder {
		public final ImageView screenCap;
		public final TextView title, season, airdate, episode;

		public ViewHolder(View view) {
			screenCap = (ImageView) view
					.findViewById(R.id.episode_list_screencap);
			title = (TextView) view.findViewById(R.id.episode_list_title);
			season = (TextView) view.findViewById(R.id.episode_list_season_no);
			episode = (TextView) view
					.findViewById(R.id.episode_list_episode_no);
			airdate = (TextView) view.findViewById(R.id.episode_list_airdate);
		}
	}
}