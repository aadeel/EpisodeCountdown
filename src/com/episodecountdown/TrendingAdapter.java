package com.episodecountdown;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.episodecountdown.R;
import com.episodecountdown.library.SquareImageView;
import com.squareup.picasso.Picasso;

public class TrendingAdapter extends CursorAdapter{
	
	public TrendingAdapter(Context context, Cursor c, int flags){
		super(context, c, flags);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		final ViewHolder viewHolder = (ViewHolder) view.getTag();

		//Assigning Values to Views!
		// Using Picasso to download image
		Picasso.with(mContext).load(cursor.getString(TrendingFragment.COL_POSTER))
			.resize(0, 400)
			.into(viewHolder.banner);
		
		//Setting Textviews
		String titleString = cursor.getString(TrendingFragment.COL_TITLE);
		if (!titleString.contains(" ("))
			titleString = titleString + " (" + cursor.getString(TrendingFragment.COL_YEAR) + ")";
		viewHolder.title.setText(titleString);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.trending_grid_item_layout, parent, false);
		ViewHolder viewHolder = new ViewHolder(view);
		//Tag can be used to store any object!! But when reading, have to know what was storred there.
		view.setTag(viewHolder);
		return view;
	}

	public static class ViewHolder {
		public final SquareImageView banner;
		public final TextView title;
		
		public ViewHolder (View view) {
			banner = (SquareImageView) view.findViewById(R.id.list_trending_banner);
			title = (TextView) view.findViewById(R.id.trending_list_title);
		}
	}
}