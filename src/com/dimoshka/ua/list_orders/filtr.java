package com.dimoshka.ua.list_orders;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_sqlite;

import java.util.ArrayList;

public class filtr extends class_activity_extends {

	private ListView listView;
	private Cursor cursor;
	private String table;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_save);
		Bundle extras = getIntent().getExtras();
		if (extras.isEmpty() == false) {
			table = extras.getString("table");
		}

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange));

		listView = (ListView) findViewById(R.id.list);
		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();

		get_cursor_all();
	}

	@SuppressWarnings("deprecation")
	private void get_cursor_all() {
		stopManagingCursor(cursor);
		cursor = database.query(table, new String[] { "_id", "name", "show" },
				null, null, null, null, "name");
		startManagingCursor(cursor);

		cursor.moveToFirst();
		ArrayList<String> todoItems = new ArrayList<String>();
		for (int i = 0; i < cursor.getCount(); i++) {
			todoItems.add(cursor.getString(cursor.getColumnIndex("name")));
			cursor.moveToNext();
		}

		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, todoItems));
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			if (cursor.getInt(cursor.getColumnIndex("show")) == 1) {
				listView.setItemChecked(i, true);
			}
			cursor.moveToNext();
		}

	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	public void b_add(View v) {
		cursor.moveToFirst();
		for (int i = 0; i < cursor.getCount(); i++) {
			ContentValues initialValues = new ContentValues();

			if (listView.isItemChecked(i)) {
				initialValues.put("show", 1);
			} else {
				initialValues.put("show", 0);
			}
			String[] args = { String.valueOf(cursor.getInt(cursor
					.getColumnIndex("_id"))) };
			database.update(table, initialValues, "_id=?", args);
			cursor.moveToNext();
		}
		onBackPressed();
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

}
