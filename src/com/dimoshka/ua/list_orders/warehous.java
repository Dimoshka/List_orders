package com.dimoshka.ua.list_orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


import com.actionbarsherlock.app.ActionBar;
import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class warehous extends class_activity_extends {

	private ListView listView;
	private class_simplecursoradapter_textsize scAdapter;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.orange));

		listView = (ListView) findViewById(R.id.list);
		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();
		get_cursor_all_items();

		scAdapter = new class_simplecursoradapter_textsize(this,
				R.layout.row_list_3, cursor, new String[] { "name", "number",
						"name_it_t" }, new int[] { R.id.text1, R.id.text2,
						R.id.text3 }, prefs.getString("font_size", "2"));
		listView.setAdapter(scAdapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent a = new Intent(getBaseContext(),
						orders_list_users_per_items.class);
				a.putExtra("id_it",
						cursor.getInt(cursor.getColumnIndex("id_it")));
				a.putExtra("name",
						cursor.getString(cursor.getColumnIndex("name")));
				a.putExtra("id_cat", 0);
				startActivity(a);
			}
		});
		registerForContextMenu(listView);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			menu.setHeaderTitle(R.string.management);
			android.view.MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_delete, menu);
		}
	}

	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int itemId = item.getItemId();
		if (itemId == R.id.b_delete) {
			delete(info.id);
		}
		return true;
	}

	private void delete(final long id) {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.delete_title))
				.setMessage(getString(R.string.delete_text))
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						String[] args = { String.valueOf(id) };
						database.delete("items_warehouse", "_id=?", args);
						reload();
					}
				}).create().show();
	}

	@SuppressWarnings("deprecation")
	private void get_cursor_all_items() {
		stopManagingCursor(cursor);
		cursor = database
				.rawQuery(
						"SELECT items_warehouse._id, items_type.name as name_it_t, items_warehouse.number, items.name, items._id as id_it from items_warehouse left join items on items_warehouse.id_it=items._id left join items_type on items.id_it_t=items_type._id order by items.name asc",
						null);
		startManagingCursor(cursor);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.add).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.refresh).setIcon(
				android.R.drawable.ic_menu_revert);
		return true;
	}

	private void reload() {
		get_cursor_all_items();
		scAdapter.changeCursor(cursor);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			b_add(listView);
			break;
		case 1:
			reload();
			break;
		default:
			break;
		}
		return false;
	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	public void b_add(View v) {
		Intent i = new Intent(getBaseContext(), warehous_manager.class);
		i.putExtra("id_war", 0);
		startActivity(i);
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

}
