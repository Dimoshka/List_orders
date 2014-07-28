package com.dimoshka.ua.list_orders;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_sqlite;

public class orders_details extends class_activity_extends {

    private ListView listView;
    private Cursor cursor;
    private SimpleCursorAdapter scAdapter;
    private int id_u = 0;
    private int id_cat = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        id_cat = extras.getInt("id_cat");
        id_u = extras.getInt("id_u");
        setContentView(R.layout.list);
        listView = (ListView) findViewById(R.id.list);
        class_sqlite dbOpenHelper = new class_sqlite(this,
                getString(R.string.db_name),
                Integer.valueOf(getString(R.string.db_version)));
        database = dbOpenHelper.openDataBase();
        get_cursor_all_orders();
        scAdapter = new SimpleCursorAdapter(this,
                R.layout.row_list_5, cursor, new String[]{"name_it",
                "number", "name_st", "name_it_t", "date"}, new int[]{
                R.id.text1, R.id.text2, R.id.text3, R.id.text4,
                R.id.text5}, 0
        );
        listView.setAdapter(scAdapter);
        registerForContextMenu(listView);

    }

    private void get_cursor_all_orders() {
        try {
            String cat = "";
            if (id_cat > 0)
                cat = "orders.id_cat='" + id_cat + "' and ";
            else
                cat = "";
            cursor = database
                    .rawQuery(
                            "SELECT orders._id, items.name as name_it, items_type.name as name_it_t, number, status.name as name_st, date from orders left join items on orders.id_it=items._id left join items_type on items.id_it_t=items_type._id left join status on orders.id_st=status._id where "
                                    + cat
                                    + "id_u='"
                                    + id_u
                                    + "' and status.show=1 order by id_st asc, id_it_t asc, name_it asc",
                            null
                    );
            Cursor cursor_name = null;
             if (id_cat > 0)
                cursor_name = database
                        .rawQuery(
                                "SELECT users.name as name_u, categories.name as name_cat from users left join categories on categories._id="
                                        + id_cat + " where users._id=" + id_u,
                                null
                        );
            else
                cursor_name = database.rawQuery(
                        "SELECT name as name_u from users where _id=" + id_u,
                        null);
            cursor_name.moveToFirst();
            actionBar.setTitle(cursor_name.getString(cursor_name
                    .getColumnIndex("name_u")));
            if (id_cat > 0)
                actionBar.setSubtitle(cursor_name.getString(cursor_name
                        .getColumnIndex("name_cat")));
            else
                actionBar.setSubtitle(getResources().getString(R.string.category_all));
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
        cursor.moveToPosition(info.position);
        int itemId = item.getItemId();
        if (itemId == R.id.b_edit) {

            Intent i = new Intent(getBaseContext(), orders_manager.class);
            i.putExtra("id_or", cursor.getInt(cursor.getColumnIndex("_id")));
            startActivity(i);
        } else if (itemId == R.id.b_delete) {
            delete(cursor.getInt(cursor.getColumnIndex("_id")));
        } else if (itemId == R.id.b_status) {
            set_status(cursor.getInt(cursor.getColumnIndex("_id")));
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_deteils, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item0:
                b_add();
                break;
            case R.id.item1:
                reload();
                break;
            case R.id.item2:
                Intent i = new Intent(this, filtr.class);
                i.putExtra("table", "status");
                startActivity(i);
                break;

            default:
                break;
        }

        return false;
    }

    public void b_add() {
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
                        String[] args = {String.valueOf(id)};
                        database.delete("orders", "_id=?", args);
                        reload();
                    }
                }).create().show();
    }

    private void set_status(final long id_or) {
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

                                String[] args = {String.valueOf(id_or)};
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
