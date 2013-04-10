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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class items extends class_activity_extends {

	private ListView listView;
	private class_simplecursoradapter_textsize scAdapter;
	private class_simplecursoradapter_textsize sc_t;
	private Cursor cursor;

	private Spinner s_type;
	private Cursor cursor_t;
	private int id_it_t;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_spinner);

		listView = (ListView) findViewById(R.id.list);
		s_type = (Spinner) findViewById(R.id.s_categoryes);
		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();

		load_types();

		get_cursor_all_items();
		scAdapter = new class_simplecursoradapter_textsize(this,
				R.layout.row_list_3, cursor, new String[] { "name", "number",
						"code" }, new int[] { R.id.text1, R.id.text2,
						R.id.text3 }, prefs.getString("font_size", "2"));
		listView.setAdapter(scAdapter);
		registerForContextMenu(listView);

		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getBaseContext(),
						orders_list_users_per_items.class);
				i.putExtra("id_it", cursor.getInt(cursor.getColumnIndex("_id")));
				i.putExtra("name",
						cursor.getString(cursor.getColumnIndex("name")));
				startActivity(i);
			}
		});

		s_type.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				reload();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	@SuppressWarnings("deprecation")
	private void load_types() {
		stopManagingCursor(cursor_t);
		cursor_t = database.query("items_type", new String[] { "_id", "name" },
				null, null, null, null, "name");
		startManagingCursor(cursor_t);
		sc_t = new class_simplecursoradapter_textsize(this,
				android.R.layout.simple_spinner_item, cursor_t,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		sc_t.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_type.setAdapter(sc_t);
	}

	@SuppressWarnings("deprecation")
	private void get_cursor_all_items() {
		stopManagingCursor(cursor);
		id_it_t = cursor_t.getInt(cursor_t.getColumnIndex("_id"));
		cursor = database
				.rawQuery(

						"SELECT items._id, items.name, items.code, ord.number, items.show from items left join items_type on items.id_it_t=items_type._id left join (select sum(number) as number, id_it from orders left join status on orders.id_st=status._id where status.show=1 group by orders.id_it) as ord on items._id=ord.id_it where items_type.show=1 and items.id_it_t="
								+ id_it_t + " order by items.name asc", null);

		startManagingCursor(cursor);
	}

	private void reload() {
		get_cursor_all_items();
		scAdapter.changeCursor(cursor);
	}

	public void b_add(View v) {
		Intent i = new Intent(getBaseContext(), items_manager.class);
		i.putExtra("id_it", 0);
		startActivity(i);
	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			menu.setHeaderTitle(R.string.management);
			android.view.MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.menu_edit_delete, menu);
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.add).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.refresh).setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, 6, Menu.NONE, R.string.items_type).setIcon(
				android.R.drawable.ic_menu_directions);
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
			i.putExtra("table", "items_type");
			startActivity(i);
			break;
		case 6:
			Intent a = new Intent(this, managment_select.class);
			a.putExtra("id_type", 2);
			startActivity(a);
			break;
		default:
			break;
		}

		return false;
	}

	private void delete(final long id) {
		new AlertDialog.Builder(this)
				.setTitle(getString(R.string.delete_title))
				.setMessage(getString(R.string.delete_text))
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						String[] args = { String.valueOf(id) };
						database.delete("items", "_id=?", args);
						reload();
					}
				}).create().show();
	}

	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int itemId = item.getItemId();
		if (itemId == R.id.b_edit) {
			Intent i = new Intent(getBaseContext(), items_manager.class);
			i.putExtra("id_it", info.id);
			i.putExtra("id_it_t", cursor.getInt(cursor.getColumnIndex("_id")));
			i.putExtra("name_it",
					cursor.getString(cursor.getColumnIndex("name")));
			i.putExtra("code_it",
					cursor.getString(cursor.getColumnIndex("code")));
			i.putExtra("show", cursor.getInt(cursor.getColumnIndex("show")));

			startActivity(i);
		} else if (itemId == R.id.b_delete) {
			delete(info.id);
		}
		return true;
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}

}
