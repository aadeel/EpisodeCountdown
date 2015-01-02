package com.episodecountdown;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CursorPagerAdapter<F extends Fragment> extends
        FragmentStatePagerAdapter {
    private final Class<F> fragmentClass;
    private final String[] projection;
    private Cursor cursor;
    private String[] titleStrings;

    public CursorPagerAdapter(FragmentManager fm, Class<F> fragmentClass,
            String[] projection, Cursor cursor, String[] titleStrings) {
        super(fm);
        this.fragmentClass = fragmentClass;
        this.projection = projection;
        this.cursor = cursor;
        this.titleStrings = titleStrings;
    }

    @Override
    public F getItem(int position) {
        if (cursor == null) 
            return null;

        cursor.moveToPosition(position);
        F frag;
        try {
            frag = fragmentClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        Bundle args = new Bundle();
        for (int i = 0; i < projection.length; ++i) {
            args.putString(projection[i], cursor.getString(i));
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public int getCount() {
        if (cursor == null)
            return 0;
        else
            return cursor.getCount();
    }

    public void swapCursor(Cursor c) {
        if (cursor == c)
            return;

        this.cursor = c;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return cursor;
    }
    
    @Override
	public CharSequence getPageTitle(int position) {
    	
    	//Check for array index. Should never be out of bound. 
    	if(position<0 || position>titleStrings.length-1)
    		return "Hot Right Now";
    	
		return titleStrings[position];
	}
}