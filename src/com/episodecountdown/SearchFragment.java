package com.episodecountdown;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.episodecountdown.EpisodeCountdown.TrackerName;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SearchFragment extends Fragment {
	
	public static final String SEARCH_DETAIL_KEY = "search_detail_key";
	public static final String SEARCH_LIST_KEY = "search_list_key";
	private View rootView;
	private GridView listView;

    public SearchFragment() {
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	rootView = inflater.inflate(R.layout.fragment_search, container, false);
		
    	listView = (GridView) rootView.findViewById(R.id.listview_search);
    	//Setting header, footer margins has to be before adapter.
    	View headerView = new View(getActivity());
    	headerView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
//    	listView.addHeaderView(headerView, null, false);
//    	listView.addFooterView(headerView, null, false);
    	
    	ArrayList<SearchResultModel> showsArrayList;
    	if(savedInstanceState == null || !savedInstanceState.containsKey(SEARCH_LIST_KEY)) {
            showsArrayList = new ArrayList<SearchResultModel>();
//            Log.v(SEARCH_LIST_KEY, "saveinstance state not found");
        }
        else {
            showsArrayList = savedInstanceState.getParcelableArrayList(SEARCH_LIST_KEY);
            //Log.v(SEARCH_LIST_KEY, "saveinstance state found. First result title is" + showsArrayList.get(0).getTitle());
        }
    	
    	//Setting adapter
    	SearchAdapter searchAdapter = new SearchAdapter(getActivity(), showsArrayList);
		listView.setAdapter(searchAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				SearchResultModel clickedShow = (SearchResultModel)listView.getItemAtPosition(position);
				if(clickedShow.getTvRageId() == 0){
					// Dialog Box Builder!!
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							getActivity());
					alertDialogBuilder.setTitle(R.string.search_list_detail_not_found_title);
					alertDialogBuilder.setMessage(R.string.search_list_detail_not_found_message);
					alertDialogBuilder.setPositiveButton("Ok", null);
					// Create Alert DialogBox!!
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.setCanceledOnTouchOutside(true);
					alertDialog.show();
					
					// Analytics
		        	Tracker t = ((EpisodeCountdown) getActivity().getApplication()).getTracker(
		                TrackerName.APP_TRACKER);
		            // Set screen name.
		            t.setScreenName("Search Tvrage Error "+ clickedShow.getTitle() + " tvdb: " + clickedShow.getTvdbId());
		            // Send a screen view.
		            t.send(new HitBuilders.ScreenViewBuilder().build());
		        }
				else {
				Intent intent = new Intent(getActivity(),
						 SearchDetailActivity.class)
						 .putExtra(SEARCH_DETAIL_KEY,clickedShow);
						 startActivity(intent);
				}
			}
		});
		
		//Empty Listview Image
    	ImageView emptyView = (ImageView)rootView.findViewById(R.id.empty_listview_image);
    	listView.setEmptyView(emptyView);
		return rootView;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	SearchAdapter adapter = (SearchAdapter)(listView.getAdapter());
    	ArrayList<SearchResultModel> arrayList = adapter.getShowList();
    	if(!arrayList.isEmpty()){
    		outState.putParcelableArrayList(SEARCH_LIST_KEY, arrayList);
    		//Log.v(SEARCH_LIST_KEY, "saveinstance state called. First result title is" + arrayList.get(0).getTitle());
    	}
    	super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
		// Search View
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final ArrayAdapterSearchView searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(searchItem);
        
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
        		R.layout.autocomplete_layout, getActivity().getResources().getStringArray(R.array.autocomplete_array));
        
        searchView.setAdapter(adapter);
        searchView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	searchView.setQuery(adapter.getItem(position).toString(), true);
            }
        });
        
        searchView.setQueryHint(getString(R.string.searchview_hint));
        MenuItemCompat.expandActionView(searchItem);
        MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
			
			@Override
			public boolean onMenuItemActionExpand(MenuItem arg0) {
				//Do nothing
				return false;
			}
			
			@Override
			public boolean onMenuItemActionCollapse(MenuItem arg0) {
				getActivity().finish();
				return false;
			}
		});
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				
				searchView.clearFocus();
				SearchAsyncTask searchTask = new SearchAsyncTask(getActivity(), rootView, query);
		    	searchTask.execute();
				
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String arg0) {
				return true;
			}
		});
        super.onCreateOptionsMenu(menu,inflater);
    }
}