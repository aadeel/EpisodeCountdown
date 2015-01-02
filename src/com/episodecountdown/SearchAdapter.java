package com.episodecountdown;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.episodecountdown.R;
import com.squareup.picasso.Picasso;

public class SearchAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<SearchResultModel> mShows;

	public SearchAdapter(Context context, ArrayList<SearchResultModel> shows) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mShows = shows;
	}
	
	//Custom method to get all for saving in savedInstanceState
	public ArrayList<SearchResultModel> getShowList(){
		return mShows;
	}

	@Override
	public int getCount() {
		return mShows.size();
	}

	@Override
	public Object getItem(int position) {
		return mShows.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		final ViewHolder holder;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.search_list_item_layout, parent,
					false);
			holder = new ViewHolder();
			holder.banner = (ImageView) view
					.findViewById(R.id.list_show_banner);
			holder.title = (TextView) view.findViewById(R.id.list_show_title);
			holder.firstAired = (TextView) view
					.findViewById(R.id.list_show_first_aired);
			holder.country = (TextView) view
					.findViewById(R.id.list_show_country);
			holder.overview = (TextView) view
					.findViewById(R.id.list_show_overview);
			holder.runtime = (TextView) view
					.findViewById(R.id.list_show_runtime);
			holder.status = (TextView) view.findViewById(R.id.list_show_status);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		//Assigning Values to Views!
		SearchResultModel show = mShows.get(position);

		//Combining Title and Year
		String titleString;
		if (show.getTitle().contains(" ("))
			titleString = show.getTitle();
		else
			titleString = show.getTitle() + " (" + show.getYear() + ")";
		holder.title.setText(titleString);
		int firstAired = show.getFirstAired();
		if (firstAired == 0)
			holder.firstAired.setText("N/A");
		else
			holder.firstAired.setText(""
					+ Utility.getReadableDateString(show.getFirstAired()));
		String country = show.getCountry();
		if (country == "null")
			holder.country.setText("N/A");
		else {
			holder.country.setText(country);
		}
		holder.overview.setText(show.getOveview());
		holder.runtime.setText(show.getRuntime() + " min");
		if (show.getEnded()){
			holder.status.setText("This show has ended");
			holder.status.setTextColor(mContext.getResources().getColor(R.color.palette_red));
		}
		else {
			holder.status.setText("Airs " + show.getAirDay() + " at "
					+ show.getAirTime() + " on " + show.getNetwork());
			holder.status.setTextColor(mContext.getResources().getColor(R.color.palette_green));
		}
		
		// Using Picasso to download image
		// Using .fit() still doing fine here, due to small size. 
		Picasso.with(mContext).load(show.getBannerUrl()).placeholder(R.drawable.placeholder_banner)
			.fit()
			.into(holder.banner);

		return view;
	}

	private class ViewHolder {
		public ImageView banner;
		public TextView title, status, firstAired, country, runtime, overview;
	}
}