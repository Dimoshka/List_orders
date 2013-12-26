package com.dimoshka.ua.classes;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.google.analytics.tracking.android.EasyTracker;


public class class_activity_extends extends SherlockActivity {

	public SharedPreferences prefs;
	public SQLiteDatabase database;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setHomeButtonEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (prefs.getBoolean("c_mn_analytics", true)) {
			EasyTracker.getInstance(this).activityStart(this);
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		if (prefs.getBoolean("c_mn_analytics", true)) {
			EasyTracker.getInstance(this).activityStop(this);
		}

	}

		
	/*
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

	    int itemId = item.getItemId();
	    switch (itemId) {
	    case android.R.id.home:
	    	onBackPressed();
	        break;
	    }
	    return true;
	}
*/
}
