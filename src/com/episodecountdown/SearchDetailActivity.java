package com.episodecountdown;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.episodecountdown.R;

public class SearchDetailActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new SearchDetailFragment()).commit();
		}
	}
}
