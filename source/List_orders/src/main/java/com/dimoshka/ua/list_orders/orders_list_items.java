package com.dimoshka.ua.list_orders;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_export_to_csv;
import com.dimoshka.ua.classes.class_sqlite;

import java.util.concurrent.ExecutionException;

public class orders_list_items extends class_activity_extends {

    private ListView listView;
    private Cursor cursor;
    private SimpleCursorAdapter sca_list;

    private int id_cat = 0;
    private String name = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        id_cat = extras.getInt("id_cat");
        name = extras.getString("name");
        actionBar.setTitle(name);
        actionBar.setSubtitle(R.string.orders_list);

        setContentView(R.layout.list);
        listView = (ListView) findViewById(R.id.list);


        class_sqlite dbOpenHelper = new class_sqlite(this,
                getString(R.string.db_name),
                Integer.valueOf(getString(R.string.db_version)));
        database = dbOpenHelper.openDataBase();

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent a = new Intent(getBaseContext(),
                        orders_list_users_per_items.class);
                a.putExtra("id_it",
                        cursor.getInt(cursor.getColumnIndex("id_it")));
                a.putExtra("name",
                        cursor.getString(cursor.getColumnIndex("name_it")));
                a.putExtra("id_cat", id_cat);
                startActivity(a);
            }
        });

        reload();
        registerForContextMenu(listView);
    }

    private void reload() {
        get_cursor_all_orders();
        sca_list = new SimpleCursorAdapter(this,
                R.layout.row_list_3, cursor, new String[]{"name_it",
                "number", "name_it_t"}, new int[]{R.id.text1,
                R.id.text2, R.id.text3}, 0
        );

        listView.setAdapter(sca_list);
    }

    private void get_cursor_all_orders() {
        cursor = database
                .rawQuery(
                        "SELECT id_it as _id, items.name as name_it, items._id as id_it, sum(number) as number, items_type.name as name_it_t from orders left join items on orders.id_it=items._id left join items_type on items.id_it_t=items_type._id left join status on orders.id_st=status._id where orders.id_cat='"
                                + id_cat
                                + "' and status.show=1 group by id_it order by name_it_t asc, name_it asc",
                        null
                );
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, R.string.refresh).setIcon(
                android.R.drawable.ic_menu_revert);
        menu.add(Menu.NONE, 5, Menu.NONE, R.string.export).setIcon(
                android.R.drawable.ic_menu_save);
        return true;
    }

    public void b_pref(View v) {
        Intent i = new Intent(this, preferences.class);
        startActivity(i);
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
            MenuInflater inflater = getMenuInflater();
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

    private void set_status(final long id_it) {
        try {
            final Cursor cursor_st;
            cursor_st = database.query("status",
                    new String[]{"_id", "name"}, null, null, null, null,
                    "_id");

            SimpleCursorAdapter Adapt_status = new SimpleCursorAdapter(this,
                    android.R.layout.simple_spinner_item, cursor_st,
                    new String[]{"name"}, new int[]{android.R.id.text1});
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
                                Cursor cur = null;
                                cur = database
                                        .rawQuery(
                                                "SELECT orders._id from orders left join status on orders.id_st=status._id where orders.id_cat='"
                                                        + id_cat
                                                        + "' and orders.id_it="
                                                        + id_it
                                                        + " and status.show=1",
                                                null
                                        );
                                cur.moveToFirst();

                                for (int i = 0; i < cur.getCount(); i++) {
                                    String[] args = {cur.getString(cur
                                            .getColumnIndex("_id"))};
                                    database.update("orders", initialValues,
                                            "_id=?", args);
                                    cur.moveToNext();
                                }
                            } catch (Exception e) {
                                Log.w("Dimoshka", e.toString());
                            } finally {
                                Toast.makeText(getBaseContext(),
                                        R.string.all_done,
                                        Toast.LENGTH_SHORT).show();
                                reload();
                            }

                        }
                    }
            );

            alert.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }
            );
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
