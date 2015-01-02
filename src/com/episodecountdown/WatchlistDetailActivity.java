package com.episodecountdown;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.picasso.Picasso;

public class WatchlistDetailActivity extends ActionBarActivity {
	
	public static final String WATCHLIST_DETAIL_POSTER_KEY = "DETAIL_POSTER_KEY";
	
//	AdView mAdView;
	
	WatchlistDetailPagerAdapter myFragmentPagerAdapter;
	ViewPager mViewPager;
	Bundle intentBundle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchlist_detail);
		
//		detailFragmentArguments.putString(WatchlistDetailFragment.WATCHLIST_TABLE_ID, getIntent().getExtras().getString(WatchlistDetailFragment.WATCHLIST_TABLE_ID));
//		detailFragmentArguments.putString(EpisodeListFragment.EPISODE_LIST_KEY, getIntent().getExtras().getString(EpisodeListFragment.EPISODE_LIST_KEY));
		
		intentBundle = getIntent().getExtras();
		
		//
		ImageView posterView = (ImageView) findViewById(R.id.watchlist_detail_poster);
		Picasso.with(this).load(intentBundle.getString(WATCHLIST_DETAIL_POSTER_KEY)).resize(0, 500)
		.into(posterView);
		
//		if (savedInstanceState == null) {
//			Bundle arguments = new Bundle();
//			arguments.putString(WatchlistDetailFragment.WATCHLIST_TABLE_ID, getIntent().getExtras().getString(WatchlistDetailFragment.WATCHLIST_TABLE_ID));
//			Fragment detailFragment = new WatchlistDetailFragment();
//        	detailFragment.setArguments(arguments);
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, detailFragment).commit();
//		}
		myFragmentPagerAdapter = new WatchlistDetailPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.watchlist_detail_pager);
		mViewPager.setAdapter(myFragmentPagerAdapter);
		
		//Setting up the sliding Strip
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.watchlist_detail_tabs);
		tabs.setBackgroundColor(getResources().getColor(R.color.light_blue));
		tabs.setShouldExpand(true);
		tabs.setTextColorResource(R.color.white);
//		tabs.setTextSize(25);
		tabs.setIndicatorColorResource(R.color.light_grey);
		tabs.setDividerColorResource(R.color.light_blue);
		tabs.setUnderlineColorResource(R.color.light_blue);
//		tabs.setDividerPadding(0);
		tabs.setIndicatorHeight(10);
		tabs.setUnderlineHeight(3);
		
		tabs.setViewPager(mViewPager);
		
		//
//		mAdView = (AdView) findViewById(R.id.adView);
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
	
	public class WatchlistDetailPagerAdapter extends FragmentPagerAdapter {
		public WatchlistDetailPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				Fragment detailFragment = new WatchlistDetailFragment();
	        	detailFragment.setArguments(intentBundle);
				return detailFragment;
			case 1:
				Fragment episodeFragment = new EpisodeListFragment();
	        	episodeFragment.setArguments(intentBundle);
				return episodeFragment;
			}	
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Overview";
			case 1:
				return "Episode List";
			}	
			return null;
		}
	}
	

//	@Override
//	protected void onDestroy() {
//		if (mAdView != null) {
//            mAdView.destroy();
//        }
//        super.onDestroy();
//	}
//
//	@Override
//	protected void onPause() {
//		if (mAdView != null) {
//            mAdView.pause();
//        }
//        super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//        if (mAdView != null) {
//            mAdView.resume();
//        }
//	}
}
