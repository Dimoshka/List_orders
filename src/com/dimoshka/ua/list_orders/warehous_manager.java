package com.dimoshka.ua.list_orders;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class warehous_manager extends class_activity_extends {
	private Spinner s_items_type;
	private Spinner s_items;
	private EditText et_number;
	private Cursor cursor_it_t;
	private Cursor cursor_it;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.warehous_manager);

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange));

		s_items_type = (Spinner) findViewById(R.id.s_items_type);
		s_items = (Spinner) findViewById(R.id.s_items);
		et_number = (EditText) findViewById(R.id.et_number);

		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();

		stopManagingCursor(cursor_it_t);

		cursor_it_t = database.query("items_type",
				new String[] { "_id", "name" }, null, null, null, null, "name");

		startManagingCursor(cursor_it_t);

		class_simplecursoradapter_textsize Adapt_users = new class_simplecursoradapter_textsize(
				this, R.layout.spinner_layout_item, cursor_it_t,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		Adapt_users.setDropDownViewResource(R.layout.spinner_dropdown_item);
		s_items_type.setAdapter(Adapt_users);

		s_items_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				load_items();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@SuppressWarnings("deprecation")
	private void load_items() {
		try {
			stopManagingCursor(cursor_it);
			cursor_it = database.rawQuery(
					"SELECT _id, name from items where id_it_t='"
							+ cursor_it_t.getInt(cursor_it_t
									.getColumnIndex("_id"))
							+ "' order by name asc", null);
			startManagingCursor(cursor_it);

			class_simplecursoradapter_textsize Adapt_items = new class_simplecursoradapter_textsize(
					this, R.layout.spinner_layout_item, cursor_it,
					new String[] { "name" }, new int[] { android.R.id.text1 },
					prefs.getString("font_size", "2"));
			Adapt_items.setDropDownViewResource(R.layout.spinner_dropdown_item);
			s_items.setAdapter(Adapt_items);

		} catch (Exception e) {
			Log.w("Dimoshka", e.toString());
		}
	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	@SuppressWarnings("deprecation")
	public void b_add(View v) {
		int id_it = cursor_it.getInt(cursor_it.getColumnIndex("_id"));
		Cursor cur = null;
		stopManagingCursor(cur);
		cur = database.rawQuery("SELECT _id from items_warehouse where id_it="
				+ id_it, null);
		startManagingCursor(cur);
		cur.moveToFirst();
		ContentValues initialValues = new ContentValues();

		if (cur.getCount() < 1) {
			initialValues.put("id_it", id_it);
			initialValues.put("number", et_number.getText().toString());
			database.insert("items_warehouse", null, initialValues);
		} else {
			initialValues.put("number", et_number.getText().toString());
			String[] args = { String.valueOf(id_it) };
			database.update("items_warehouse", initialValues, "id_it=?", args);
		}
		onBackPressed();
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}
}
