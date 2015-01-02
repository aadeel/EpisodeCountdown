package com.episodecountdown;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

public class MainActivity extends ActionBarActivity {
	
//	AdView mAdView;

//	@Override
//	protected void onStop() {
//		GoogleAnalytics.getInstance(this).reportActivityStop(this);
//		super.onStop();
//	}
//
//	@Override
//	protected void onStart() {
//		GoogleAnalytics.getInstance(this).reportActivityStart(this);
//		super.onStart();
//	}

	MainActivityPagerAdapter myFragmentPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		((EpisodeCountdown) getApplication()).getTracker(EpisodeCountdown.TrackerName.APP_TRACKER);
		
		setContentView(R.layout.activity_main);
		// ViewPager and its adapters use support library
		// fragments, so use getSupportFragmentManager.
		myFragmentPagerAdapter = new MainActivityPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(myFragmentPagerAdapter);
		
		//Setting up the sliding Strip
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 int id = item.getItemId();
		 if (id == R.id.action_search) {
			 startActivity(new Intent(this, SearchActivity.class));
			 return true;
		 }
		return super.onOptionsItemSelected(item);
	}

	public class MainActivityPagerAdapter extends FragmentPagerAdapter {
		public MainActivityPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				return new WatchlistFragment();
			case 1:
				return new TrendingFragment();
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
				return getString(R.string.fragment_title_watchlist);
			case 1:
				return getString(R.string.fragment_title_trending);
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
