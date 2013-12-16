package com.dimoshka.ua.list_orders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.bugsense.trace.BugSenseHandler;
import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_function;

import java.io.File;

public class main extends class_activity_extends {

	private class_function funct = new class_function();

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "11fa22af");
        BugSenseHandler.setLogging(100);
        setContentView(R.layout.main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange));
		font_size();
	}

	void font_size() {
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		TextView textView2 = (TextView) findViewById(R.id.textView2);
		TextView textView3 = (TextView) findViewById(R.id.textView3);
		TextView textView4 = (TextView) findViewById(R.id.textView4);
		String str = prefs.getString("font_size", "2");
		if (str.equals("1")) {
			textView1.setTextSize(getResources().getDimension(
					R.dimen.text_very_small));
			textView2.setTextSize(getResources().getDimension(
					R.dimen.text_very_small));
			textView3.setTextSize(getResources().getDimension(
					R.dimen.text_very_small));
			textView4.setTextSize(getResources().getDimension(
					R.dimen.text_very_small));
		} else if (str.equals("2")) {
			textView1.setTextSize(getResources().getDimension(
					R.dimen.text_small));
			textView2.setTextSize(getResources().getDimension(
					R.dimen.text_small));
			textView3.setTextSize(getResources().getDimension(
					R.dimen.text_small));
			textView4.setTextSize(getResources().getDimension(
					R.dimen.text_small));
		} else if (str.equals("3")) {
			textView1.setTextSize(getResources().getDimension(
					R.dimen.text_medium));
			textView2.setTextSize(getResources().getDimension(
					R.dimen.text_medium));
			textView3.setTextSize(getResources().getDimension(
					R.dimen.text_medium));
			textView4.setTextSize(getResources().getDimension(
					R.dimen.text_medium));
		} else {
			textView1.setTextSize(getResources().getDimension(
					R.dimen.text_large));
			textView2.setTextSize(getResources().getDimension(
					R.dimen.text_large));
			textView3.setTextSize(getResources().getDimension(
					R.dimen.text_large));
			textView4.setTextSize(getResources().getDimension(
					R.dimen.text_large));
		}
	}

	public void show_orders(View v) {
		Intent i = new Intent(this, orders.class);
		startActivity(i);
	}

	public void show_users(View v) {
		Intent i = new Intent(this, users.class);
		i.putExtra("id_u_g", 0);
		startActivity(i);
	}

	public void show_warehouse(View v) {
		Intent i = new Intent(this, warehous.class);
		startActivity(i);
	}

	public void show_items(View v) {
		Intent i = new Intent(this, items.class);
		startActivity(i);
	}

	void backup_db() {
		if (prefs.getBoolean("c_db_backup", false)) {
			funct.file_backup(this, getString(R.string.db_name));
		}

		if (prefs.getBoolean("c_db_backups_all", false)) {
			funct.file_delete_old(new File(Environment
					.getExternalStorageDirectory()
					+ "/Backup/"
					+ getBaseContext().getPackageName() + "/"));
		}
	}

	public void onBackPressed() {
		backup_db();
		System.exit(0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
				menu.add(Menu.NONE, 2, Menu.NONE, R.string.restory).setIcon(
				android.R.drawable.ic_menu_recent_history);
		menu.add(Menu.NONE, 4, Menu.NONE, R.string.preference).setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.exit).setIcon(
				android.R.drawable.ic_lock_power_off);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			System.exit(0);
			break;
		case 2:
			Intent a = new Intent(this, backup_db.class);
			startActivity(a);
			break;
		case 4:
			Intent i = new Intent(this, preferences.class);
			startActivity(i);
			break;
		default:
			break;
		}

		return false;
	}

}