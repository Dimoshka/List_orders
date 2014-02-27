package com.dimoshka.ua.classes;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.google.analytics.tracking.android.EasyTracker;


public class class_activity_extends extends ActionBarActivity {

    public SharedPreferences prefs;
    public SQLiteDatabase database;
    public ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        actionBar = getSupportActionBar();
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
}
