package com.dimoshka.ua.list_orders;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class orders_manager extends class_activity_extends {

	private Spinner s_items;
	private Spinner s_items_type;
	private Spinner s_status;
	private Spinner s_categoryes;
	private Spinner s_users;
	private EditText et_number;

	private Cursor cursor_u;
	private Cursor cursor_it;
	private Cursor cursor_it_t;
	private Cursor cursor_cat;
	private Cursor cursor_st;

	private int id_or = 0;
	private int id_u = 0;
	private int id_cat = 0;

	private int id_it_edit = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orders_manager);
		Bundle extras = getIntent().getExtras();
		if (extras.isEmpty() == false) {
			id_or = extras.getInt("id_or");
			id_u = extras.getInt("id_u");
			id_cat = extras.getInt("id_cat");
		}

		s_items = (Spinner) findViewById(R.id.s_items);
		s_items_type = (Spinner) findViewById(R.id.s_items_type);
		s_status = (Spinner) findViewById(R.id.s_status);
		s_categoryes = (Spinner) findViewById(R.id.s_categoryes);
		s_users = (Spinner) findViewById(R.id.s_users);
		et_number = (EditText) findViewById(R.id.et_number);

		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();

		stopManagingCursor(cursor_u);

		stopManagingCursor(cursor_cat);
		stopManagingCursor(cursor_st);

		cursor_u = database.query("users", new String[] { "_id", "name" },
				null, null, null, null, "name");

		cursor_cat = database.query("categories",
				new String[] { "_id", "name" }, null, null, null, null, "name");
		cursor_st = database.query("status", new String[] { "_id", "name" },
				null, null, null, null, "_id");

		startManagingCursor(cursor_u);
		startManagingCursor(cursor_cat);
		startManagingCursor(cursor_st);

		class_simplecursoradapter_textsize Adapt_users = new class_simplecursoradapter_textsize(
				this, R.layout.spinner_layout_item, cursor_u,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		Adapt_users.setDropDownViewResource(R.layout.spinner_dropdown_item);
		s_users.setAdapter(Adapt_users);
		class_simplecursoradapter_textsize Adapt_status = new class_simplecursoradapter_textsize(
				this, R.layout.spinner_layout_item, cursor_st,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		Adapt_status.setDropDownViewResource(R.layout.spinner_dropdown_item);
		s_status.setAdapter(Adapt_status);
		class_simplecursoradapter_textsize Adapt_categoryes = new class_simplecursoradapter_textsize(
				this, R.layout.spinner_layout_item, cursor_cat,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		Adapt_categoryes
				.setDropDownViewResource(R.layout.spinner_dropdown_item);
		s_categoryes.setAdapter(Adapt_categoryes);

		s_items_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				load_items();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		stopManagingCursor(cursor_it_t);
		cursor_it_t = database.query("items_type",
				new String[] { "_id", "name" }, null, null, null, null, "name");
		startManagingCursor(cursor_it_t);
		class_simplecursoradapter_textsize Adapt_items_type = new class_simplecursoradapter_textsize(
				this, R.layout.spinner_layout_item, cursor_it_t,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		Adapt_items_type
				.setDropDownViewResource(R.layout.spinner_dropdown_item);
		s_items_type.setAdapter(Adapt_items_type);

		edit();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * menu.add("b_add").setIcon(android.R.drawable.ic_menu_save)
		 * .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		 */
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			save();
			break;
		default:
			break;

		}

		return false;
	}

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

			if (id_it_edit > 0) {
				for (int i = 0; i < s_items.getCount(); i++) {
					Cursor cc = (Cursor) s_items.getItemAtPosition(i);
					startManagingCursor(cc);
					if (cc.getInt(cc.getColumnIndex("_id")) == id_it_edit) {
						s_items.setSelection(i);
						break;
					}
				}
			}

		} catch (Exception e) {
			Log.w("Dimoshka", e.toString());
		}
	}

	private void edit() {

		for (int i = 0; i < s_users.getCount(); i++) {
			Cursor cc = (Cursor) s_users.getItemAtPosition(i);
			startManagingCursor(cc);
			if (cc.getInt(cc.getColumnIndex("_id")) == id_u) {
				s_users.setSelection(i);
				break;
			}
		}

		for (int i = 0; i < s_categoryes.getCount(); i++) {
			Cursor cc = (Cursor) s_categoryes.getItemAtPosition(i);
			startManagingCursor(cc);
			if (cc.getInt(cc.getColumnIndex("_id")) == id_cat) {
				s_categoryes.setSelection(i);
				break;
			}
		}

		if (id_or > 0) {

			Cursor c = database
					.rawQuery(
							"SELECT id_u, id_it, id_it_t, id_cat, id_st, number from orders left join items on orders.id_it=items._id left join items_type on items.id_it_t=items_type._id where orders._id='"
									+ id_or + "' limit 1", null);
			startManagingCursor(c);

			if (c.moveToFirst()) {

				int id_u = c.getColumnIndex("id_u");
				int id_it = c.getColumnIndex("id_it");
				int id_it_t = c.getColumnIndex("id_it_t");
				int id_cat = c.getColumnIndex("id_cat");
				int id_st = c.getColumnIndex("id_st");
				int id_number = c.getColumnIndex("number");

				do {

					for (int i = 0; i < s_users.getCount(); i++) {
						Cursor cc = (Cursor) s_users.getItemAtPosition(i);
						startManagingCursor(cc);
						if (cc.getInt(cc.getColumnIndex("_id")) == c
								.getInt(id_u)) {
							s_users.setSelection(i);
							break;
						}
					}

					for (int i = 0; i < s_items_type.getCount(); i++) {
						Cursor cc = (Cursor) s_items_type.getItemAtPosition(i);
						startManagingCursor(cc);
						if (cc.getInt(cc.getColumnIndex("_id")) == c
								.getInt(id_it_t)) {
							s_items_type.setSelection(i);
							break;
						}
					}

					id_it_edit = c.getInt(id_it);

					for (int i = 0; i < s_categoryes.getCount(); i++) {
						Cursor cc = (Cursor) s_categoryes.getItemAtPosition(i);
						startManagingCursor(cc);
						if (cc.getInt(cc.getColumnIndex("_id")) == c
								.getInt(id_cat)) {
							s_categoryes.setSelection(i);
							break;
						}
					}

					for (int i = 0; i < s_status.getCount(); i++) {
						Cursor cc = (Cursor) s_status.getItemAtPosition(i);
						startManagingCursor(cc);
						if (cc.getInt(cc.getColumnIndex("_id")) == c
								.getInt(id_st)) {
							s_status.setSelection(i);
							break;
						}
					}

					et_number.setText(c.getString(id_number));

				} while (c.moveToNext());
			}
		}

	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	public void b_add(View v) {
		save();
	}

	void save() {

		// show_dialog();

		ContentValues initialValues = new ContentValues();

		int id_u = 0;
		id_u = cursor_u.getInt(cursor_u.getColumnIndex("_id"));
		int id_cat = 0;
		id_cat = cursor_cat.getInt(cursor_cat.getColumnIndex("_id"));
		int id_it = 0;
		id_it = cursor_it.getInt(cursor_it.getColumnIndex("_id"));
		int id_st = 0;
		id_st = cursor_st.getInt(cursor_st.getColumnIndex("_id"));

		initialValues.put("id_u", id_u);
		initialValues.put("id_cat", id_cat);
		initialValues.put("id_it", id_it);
		initialValues.put("id_st", id_st);
		initialValues.put("number", et_number.getText().toString());
		initialValues.put("id_u", id_u);

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		initialValues.put("date_edit", dateFormat.format(date));

		if (id_or > 0) {
			String[] args = { String.valueOf(id_or) };
			database.update("orders", initialValues, "_id=?", args);
		} else
			database.insert("orders", null, initialValues);

		// pdialog.dismiss();
		Toast.makeText(getApplicationContext(), R.string.saved,
				Toast.LENGTH_SHORT).show();

	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}
}
