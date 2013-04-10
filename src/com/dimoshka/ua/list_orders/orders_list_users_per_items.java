package com.dimoshka.ua.list_orders;

import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_export_to_csv;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class orders_list_users_per_items extends class_activity_extends {

	private ListView listView;
	TextView t1;
	TextView t2;
	private Cursor cursor;
	private class_simplecursoradapter_textsize scAdapter;

	private int id_it = 0;
	private int id_cat = 0;
	private String name = "";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		id_it = extras.getInt("id_it");
		id_cat = extras.getInt("id_cat");
		name = extras.getString("name");
		setContentView(R.layout.orders_details);
		listView = (ListView) findViewById(R.id.list);
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent i = new Intent(getBaseContext(), orders_details.class);
				i.putExtra("id_u", cursor.getInt(cursor.getColumnIndex("id_u")));
				i.putExtra("id_cat", id_cat);
				i.putExtra("user",
						cursor.getString(cursor.getColumnIndex("name_u")));
				i.putExtra("category", 0);
				startActivity(i);
			}
		});

		reload();
		registerForContextMenu(listView);
	}

	private void reload() {

		get_cursor_all_orders_of_items();
		scAdapter = new class_simplecursoradapter_textsize(this,
				R.layout.row_list_3, cursor, new String[] { "name_u", "number",
						"name_st" }, new int[] { R.id.text1, R.id.text2,
						R.id.text3 }, prefs.getString("font_size", "2"));
		listView.setAdapter(scAdapter);

		t1.setText(name);
		t2.setText(R.string.orders_list);
	}

	@SuppressWarnings("deprecation")
	private void get_cursor_all_orders_of_items() {
		stopManagingCursor(cursor);
		cursor = database
				.rawQuery(
						"SELECT orders._id, users.name as name_u, users._id as id_u, number, categories._id as id_cat, categories.name as name_cat, status.name as name_st from orders left join users on orders.id_u=users._id left join categories on orders.id_cat=categories._id left join status on orders.id_st=status._id where orders.id_it='"
								+ id_it
								+ "' and status.show=1 group by id_u order by name_u asc",
						null);
		startManagingCursor(cursor);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.refresh).setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, 5, Menu.NONE, R.string.export).setIcon(
				android.R.drawable.ic_menu_save);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			reload();
			break;
		case 5:
			try {
				if (new class_export_to_csv().execute(cursor).get()) {
					Toast.makeText(this, getString(R.string.all_done),
							Toast.LENGTH_SHORT).show();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

		return false;
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			menu.setHeaderTitle(R.string.management);
			android.view.MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_item_status, menu);
		}
	}

	public boolean onContextItemSelected(android.view.MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int itemId = item.getItemId();
		if (itemId == R.id.b_status) {
			set_status(info.id);
		}
		return true;
	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	@SuppressWarnings("deprecation")
	private void set_status(final long id_or) {
		try {
			final Cursor cursor_st;
			cursor_st = database.query("status",
					new String[] { "_id", "name" }, null, null, null, null,
					"_id");

			SimpleCursorAdapter Adapt_status = new SimpleCursorAdapter(this,
					android.R.layout.simple_spinner_item, cursor_st,
					new String[] { "name" }, new int[] { android.R.id.text1 });
			Adapt_status
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle(R.string.management);
			alert.setMessage(R.string.status);
			final Spinner input = new Spinner(this);
			input.setAdapter(Adapt_status);
			alert.setView(input);
			alert.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							int id_st = cursor_st.getInt(cursor_st
									.getColumnIndex("_id"));

							try {
								ContentValues initialValues = new ContentValues();
								initialValues.put("id_st", id_st);

								String[] args = { String.valueOf(id_or) };
								database.update("orders", initialValues,
										"_id=?", args);

							} catch (Exception e) {
								Log.w("Dimoshka", e.toString());
							} finally {
								Toast.makeText(getBaseContext(),
										R.string.all_done,
										Toast.LENGTH_SHORT).show();
								reload();
							}

						}
					});

			alert.setNegativeButton(android.R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							return;
						}
					});
			alert.show();
		} catch (Exception e) {
			Log.w("Dimoshka", e.toString());
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

}
