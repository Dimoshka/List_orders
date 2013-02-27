package com.dimoshka.ua.list_orders;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;


import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class orders extends class_activity_extends {
	private ListView listView;
	private Spinner s_categoryes;
	private Cursor cursor;
	private Cursor cursor_cat;
	private class_simplecursoradapter_textsize scAdapter;
	private class_simplecursoradapter_textsize sc_categoryes;
	private int id_cat = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_spinner);

		listView = (ListView) findViewById(R.id.list);
		s_categoryes = (Spinner) findViewById(R.id.s_categoryes);
		class_sqlite dbOpenHelper = new class_sqlite(this,
				getString(R.string.db_name),
				Integer.valueOf(getString(R.string.db_version)));
		database = dbOpenHelper.openDataBase();
		load_categoryes();

		scAdapter = new class_simplecursoradapter_textsize(this,
				R.layout.row_list_2_horizontal, cursor, new String[] { "name",
						"count(*)" }, new int[] { R.id.text1, R.id.text2 },
				prefs.getString("font_size", "2"));

		listView.setAdapter(scAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(getBaseContext(), orders_details.class);
				i.putExtra("id_u", cursor.getInt(cursor.getColumnIndex("id_u")));
				i.putExtra("id_cat",
						cursor_cat.getInt(cursor_cat.getColumnIndex("_id")));
				startActivity(i);
			}
		});

		s_categoryes.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				reload();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, Menu.NONE, R.string.add).setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(Menu.NONE, 3, Menu.NONE, R.string.orders_list).setIcon(
				android.R.drawable.ic_menu_more);
		menu.add(Menu.NONE, 2, Menu.NONE, R.string.filtr).setIcon(
				android.R.drawable.ic_menu_crop);
		menu.add(Menu.NONE, 6, Menu.NONE, R.string.orders_status).setIcon(
				android.R.drawable.ic_menu_directions);
		menu.add(Menu.NONE, 1, Menu.NONE, R.string.refresh).setIcon(
				android.R.drawable.ic_menu_revert);
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
		case 3:
			Intent a = new Intent(this, orders_list_items.class);
			a.putExtra("id_cat",
					cursor_cat.getInt(cursor_cat.getColumnIndex("_id")));
			a.putExtra("name",
					cursor_cat.getString(cursor_cat.getColumnIndex("name")));
			startActivity(a);
			break;
		case 6:
			Intent q = new Intent(this, managment_select.class);
			q.putExtra("id_type", 3);
			startActivity(q);
			break;
		default:
			break;
		}

		return false;
	}

	private void load_categoryes() {
		stopManagingCursor(cursor_cat);
		cursor_cat = database.query("categories",
				new String[] { "_id", "name" }, null, null, null, null, "name");
		startManagingCursor(cursor_cat);
		sc_categoryes = new class_simplecursoradapter_textsize(this,
				android.R.layout.simple_spinner_item, cursor_cat,
				new String[] { "name" }, new int[] { android.R.id.text1 },
				prefs.getString("font_size", "2"));
		sc_categoryes
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s_categoryes.setAdapter(sc_categoryes);
	}

	private void get_cursor_all_orders() {
		stopManagingCursor(cursor);
		id_cat = cursor_cat.getInt(cursor_cat.getColumnIndex("_id"));
		cursor = database
				.rawQuery(
						"SELECT orders._id, users.name, id_u, count(*) from orders left join users on orders.id_u=users._id left join status on orders.id_st=status._id where id_cat='"
								+ id_cat
								+ "' and status.show=1 group by orders.id_u order by users.name asc",
						null);
		startManagingCursor(cursor);
	}

	private void reload() {
		get_cursor_all_orders();
		scAdapter.changeCursor(cursor);
	}

	public void b_add(View v) {
		Intent i = new Intent(getBaseContext(), orders_manager.class);
		i.putExtra("id_cat", id_cat);
		startActivity(i);
	}

	public void b_pref(View v) {
		Intent i = new Intent(this, preferences.class);
		startActivity(i);
	}

	protected void onDestroy() {
		super.onDestroy();
		database.close();
	}
}