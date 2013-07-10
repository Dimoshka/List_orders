package com.dimoshka.ua.list_orders;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class users_manager extends class_activity_extends {

	private Spinner s_users_group;
	private Spinner s_users_contact;
	private CheckBox cb_users_contact_using;

	private EditText et_name;

	private Cursor cursor_u_g;
	private Cursor cursor_u_c;
	private int id_u_g;
	private long id_u;
	private String name_u;
	private int id_u_c;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.users_manager);
		Bundle extras = getIntent().getExtras();
		id_u = extras.getLong("id_u");
		id_u_g = extras.getInt("id_u_g");
		name_u = extras.getString("name_u");
		id_u_c = extras.getInt("id_u_c");

		s_users_group = (Spinner) findViewById(R.id.s_users_group);
		s_users_contact = (Spinner) findViewById(R.id.s_users_contact);
		cb_users_contact_using = (CheckBox) findViewById(R.id.cb_users_contact_using);
		et_name = (EditText) findViewById(R.id.et_name);

		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();

		stopManagingCursor(cursor_u_g);
		cursor_u_g = database.query("users_group",
				new String[] { "_id", "name" }, null, null, null, null, "name");
		startManagingCursor(cursor_u_g);

		class_simplecursoradapter_textsize Adapt_users = new class_simplecursoradapter_textsize(
				this, android.R.layout.simple_spinner_item, cursor_u_g,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		Adapt_users
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_users_group.setAdapter(Adapt_users);

		load_contacts();
		edit();

	}

	@SuppressWarnings("deprecation")
	private void edit() {
		if (id_u > 0) {
			Button b = (Button) findViewById(R.id.b_add);
			b.setText(R.string.save);
			et_name.setText(name_u);

			if (id_u_c != 0) {
				cb_users_contact_using.setChecked(true);

				for (int u = 0; u < s_users_contact.getCount(); u++) {
					Cursor cu = (Cursor) s_users_contact.getItemAtPosition(u);
					startManagingCursor(cu);
					if (cu.getInt(cu.getColumnIndex("_id")) == id_u_c) {
						s_users_contact.setSelection(u);
						break;
					}
				}

			} else {
				cb_users_contact_using.setChecked(false);
			}

			for (int u = 0; u < s_users_group.getCount(); u++) {
				Cursor cu = (Cursor) s_users_group.getItemAtPosition(u);
				startManagingCursor(cu);
				if (cu.getLong(cu.getColumnIndex("_id")) == id_u_g) {
					s_users_group.setSelection(u);
					break;
				}
			}

		}
	}

	@SuppressWarnings("deprecation")
	private void load_contacts() {

		final String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER };

		stopManagingCursor(cursor_u_c);
		cursor_u_c = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, projection, null, null,
				"DISPLAY_NAME ASC");

		startManagingCursor(cursor_u_c);

		class_simplecursoradapter_textsize Adapt_users_contact = new class_simplecursoradapter_textsize(
				this, android.R.layout.simple_spinner_item, cursor_u_c,
				new String[] { "DISPLAY_NAME" },
				new int[] { android.R.id.text1 }, prefs.getString("font_size",
						"2"));
		Adapt_users_contact
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_users_contact.setAdapter(Adapt_users_contact);

	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	public void b_add(View v) {
		ContentValues initialValues = new ContentValues();

        cursor_u_g.moveToPosition(s_users_group.getSelectedItemPosition());
		int id_ug = cursor_u_g.getInt(cursor_u_g.getColumnIndex("_id"));

        cursor_u_c.moveToPosition(s_users_contact.getSelectedItemPosition());
		int id_contact = cursor_u_c.getInt(cursor_u_c
				.getColumnIndex(ContactsContract.Contacts._ID));

		initialValues.put("id_group", id_ug);
		initialValues.put("name", et_name.getText().toString());

		if (cb_users_contact_using.isChecked()) {
			initialValues.put("id_contact", id_contact);
		} else {
			initialValues.put("id_contact", 0);
		}

		if (id_u > 0) {
			String[] args = { String.valueOf(id_u) };
			database.update("users", initialValues, "_id=?", args);
		} else
			database.insert("users", null, initialValues);
		onBackPressed();
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

}
