package com.dimoshka.ua.list_orders;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class orders_details extends class_activity_extends {

	private ListView listView;
	private Cursor cursor;
	private class_simplecursoradapter_textsize scAdapter;
	private int id_u = 0;
	private int id_cat = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		id_cat = extras.getInt("id_cat");
		id_u = extras.getInt("id_u");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange));

		setContentView(R.layout.list);
		listView = (ListView) findViewById(R.id.list);
		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();
		get_cursor_all_orders();
		scAdapter = new class_simplecursoradapter_textsize(this,
				R.layout.row_list_5, cursor, new String[] { "name_it",
						"number", "name_st", "name_it_t", "date" }, new int[] {
						R.id.text1, R.id.text2, R.id.text3, R.id.text4,
						R.id.text5 }, prefs.getString("font_size", "2"));
		listView.setAdapter(scAdapter);
		registerForContextMenu(listView);

	}

	@SuppressWarnings("deprecation")
	private void get_cursor_all_orders() {
		try {

			String cat = "";
			if (id_cat > 0)
				cat = "orders.id_cat='" + id_cat + "' and ";
			else
				cat = "";
			stopManagingCursor(cursor);
			cursor = database
					.rawQuery(
							"SELECT orders._id, items.name as name_it, items_type.name as name_it_t, number, status.name as name_st, date from orders left join items on orders.id_it=items._id left join items_type on items.id_it_t=items_type._id left join status on orders.id_st=status._id where "
									+ cat
									+ "id_u='"
									+ id_u
									+ "' and status.show=1 order by id_st asc, id_it_t asc, name_it asc",
							null);

			startManagingCursor(cursor);

			Cursor cursor_name = null;
			stopManagingCursor(cursor_name);
			if (id_cat > 0)
				cursor_name = database
						.rawQuery(
								"SELECT users.name as name_u, categories.name as name_cat from users left join categories on categories._id="
										+ id_cat + " where users._id=" + id_u,
								null);
			else

				cursor_name = database.rawQuery(
						"SELECT name as name_u from users where _id=" + id_u,
						null);
			startManagingCursor(cursor_name);
			cursor_name.moveToFirst();

			TextView user;
			TextView category;

			user = (TextView) findViewById(R.id.text1);
			category = (TextView) findViewById(R.id.text2);

			user.setText(cursor_name.getString(cursor_name
					.getColumnIndex("name_u")));

			if (id_cat > 0)
				category.setText(cursor_name.getString(cursor_name
						.getColumnIndex("name_cat")));
			else
				category.setText(R.string.category_all);
		} catch (Exception e) {
			Log.w("Dimoshka", e.toString());
		}

	}

	private void reload() {
		get_cursor_all_orders();
		scAdapter.changeCursor(cursor);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			menu.setHeaderTitle(R.string.management);
			android.view.MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_status_edit_delete, menu);
		}
	}

	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int itemId = item.getItemId();
		if (itemId == R.id.b_edit) {
			Intent i = new Intent(getBaseContext(), orders_manager.class);
			i.putExtra("id_or", cursor.getInt(cursor.getColumnIndex("_id")));
			startActivity(i);
		} else if (itemId == R.id.b_delete) {
			delete(info.id);
		} else if (itemId == R.id.b_status) {
			set_status(info.id);
		}
		return true;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.add).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.refresh).setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.filtr).setIcon(
				android.R.drawable.ic_menu_crop);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			b_add(listView);
			break;
		case 1:
			reload();
			break;
		case 2:
			Intent i = new Intent(this, filtr.class);
			i.putExtra("table", "status");
			startActivity(i);
			break;

		default:
			break;
		}

		return false;
	}

	public void b_add(View v) {
		Intent i = new Intent(getBaseContext(), orders_manager.class);
		i.putExtra("id_u", id_u);
		i.putExtra("id_cat", id_cat);
		startActivity(i);
	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	private void delete(final long id) {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.delete_title))
				.setMessage(getString(R.string.delete_text))
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						String[] args = { String.valueOf(id) };
						database.delete("orders", "_id=?", args);
						reload();
					}
				}).create().show();
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
