package com.dimoshka.ua.list_orders;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_sqlite;

public class orders extends class_activity_extends {
    private ListView listView;
    private Spinner spiner_categoryes;

    private Cursor cursor_list;
    private Cursor cursor_categoryes;

    private SimpleCursorAdapter sca_list;
    private SimpleCursorAdapter sca_categoryes;

    private int id_cat = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_spinner);
        listView = (ListView) findViewById(R.id.list);
        spiner_categoryes = (Spinner) findViewById(R.id.s_categoryes);
        class_sqlite dbOpenHelper = new class_sqlite(this,
                getString(R.string.db_name),
                Integer.valueOf(getString(R.string.db_version)));
        database = dbOpenHelper.openDataBase();
        load_categoryes();

        sca_list = new SimpleCursorAdapter(this,
                R.layout.row_list_2_horizontal, cursor_list, new String[]{"name",
                "count(*)"}, new int[]{R.id.text1, R.id.text2}, 0
        );

        listView.setAdapter(sca_list);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getBaseContext(), orders_details.class);
                i.putExtra("id_u", cursor_list.getInt(cursor_list.getColumnIndex("id_u")));
                i.putExtra("id_cat",
                        cursor_categoryes.getInt(cursor_categoryes.getColumnIndex("_id")));
                startActivity(i);
            }
        });

        spiner_categoryes.setOnItemSelectedListener(new OnItemSelectedListener() {
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
        getMenuInflater().inflate(R.menu.orders, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                b_add(listView);
                break;
            case R.id.refresh:
                reload();
                break;
            case R.id.filtr:
                Intent i = new Intent(this, filtr.class);
                i.putExtra("table", "status");
                startActivity(i);
                break;
            case R.id.orders_list:
                Intent a = new Intent(this, orders_list_items.class);
                a.putExtra("id_cat",
                        cursor_categoryes.getInt(cursor_categoryes.getColumnIndex("_id")));
                a.putExtra("name",
                        cursor_categoryes.getString(cursor_categoryes.getColumnIndex("name")));
                startActivity(a);
                break;
            case R.id.orders_status:
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
        cursor_categoryes = database.query("categories",
                new String[]{"_id", "name"}, null, null, null, null, "name");
        sca_categoryes = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item, cursor_categoryes,
                new String[]{"name"}, new int[]{android.R.id.text1}, 0);
        sca_categoryes
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiner_categoryes.setAdapter(sca_categoryes);
    }


    private void get_cursor_all_orders() {
        id_cat = cursor_categoryes.getInt(cursor_categoryes.getColumnIndex("_id"));
        cursor_list = database
                .rawQuery(
                        "SELECT orders._id, users.name, id_u, count(*) from orders left join users on orders.id_u=users._id left join status on orders.id_st=status._id where id_cat='"
                                + id_cat
                                + "' and status.show=1 group by orders.id_u order by users.name asc",
                        null
                );
    }

    private void reload() {
        get_cursor_all_orders();
        sca_list.changeCursor(cursor_list);
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