package com.dimoshka.ua.list_orders;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ListView;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class managment_select extends class_activity_extends {

    private ListView listView;
    private class_simplecursoradapter_textsize scAdapter;
    private Cursor cursor;
    private long id_edit = 0;
    private int id_type = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        Bundle extras = getIntent().getExtras();
        id_type = extras.getInt("id_type");

        listView = (ListView) findViewById(R.id.list);
        class_sqlite dbOpenHelper = new class_sqlite(this,
                getString(R.string.db_name),
                Integer.valueOf(getString(R.string.db_version)));
        database = dbOpenHelper.openDataBase();
        get_cursor_all_data();
        scAdapter = new class_simplecursoradapter_textsize(this,
                R.layout.row_list_2_horizontal, cursor, new String[]{"name",
                "number"}, new int[]{R.id.text1, R.id.text2},
                prefs.getString("font_size", "2"));
        listView.setAdapter(scAdapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (id_type) {
                    case 1:
                        Intent i = new Intent(getBaseContext(), users.class);
                        i.putExtra("id_u_g",
                                cursor.getInt(cursor.getColumnIndex("_id")));
                        startActivity(i);
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                }

            }
        });

    }

    @SuppressWarnings("deprecation")
    private void get_cursor_all_data() {
        stopManagingCursor(cursor);

        switch (id_type) {
            case 1:
                cursor = database
                        .rawQuery(
                                "SELECT users_group._id, users_group.name, ord.number from users_group left join (select count(*) as number, id_group from users group by id_group) as ord on users_group._id=ord.id_group order by users_group.name asc",
                                null);
                break;
            case 2:
                cursor = database
                        .rawQuery(
                                "SELECT _id, name, ord.number from items_type left join (select count(*) as number, id_it_t from items group by id_it_t) as ord on items_type._id=ord.id_it_t order by items_type.name asc",
                                null);
                break;
            case 3:
                cursor = database
                        .rawQuery(
                                "SELECT _id, name, ord.number from status left join (select count(*) as number, id_st from orders group by id_st) as ord on status._id=ord.id_st order by status.name asc",
                                null);
                break;
        }

        startManagingCursor(cursor);
    }

    private void reload() {
        get_cursor_all_data();
        scAdapter.changeCursor(cursor);
    }

    public void b_add(View v) {
        alert_input_dialog(R.string.add_title, R.string.add_text, "");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, Menu.NONE, R.string.add).setIcon(
                android.R.drawable.ic_menu_add);
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
            default:
                break;

        }

        return false;
    }

    public void alert_input_dialog(int title, int mess, String text) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(mess);

        final EditText input = new EditText(this);
        input.setText(text);
        alert.setView(input);

        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String txt = input.getText().toString();
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("name", txt);
                        if (id_edit > 0) {
                            String[] args = {String.valueOf(id_edit)};
                            switch (id_type) {
                                case 1:
                                    database.update("users_group", initialValues,
                                            "_id=?", args);
                                    break;
                                case 2:
                                    database.update("items_type", initialValues,
                                            "_id=?", args);
                                    break;
                                case 3:
                                    database.update("status", initialValues,
                                            "_id=?", args);
                                    break;
                            }

                        } else {
                            switch (id_type) {
                                case 1:
                                    database.insert("users_group", null,
                                            initialValues);
                                    break;
                                case 2:
                                    database.insert("items_type", null,
                                            initialValues);
                                    break;
                                case 3:
                                    database.insert("status", null, initialValues);
                                    break;
                            }
                        }
                        reload();
                    }
                });

        alert.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        id_edit = 0;
                        return;
                    }
                });

        alert.show();
        return;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            menu.setHeaderTitle(R.string.management);
            android.view.MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit_delete, menu);
        }
    }

    private void delete(final long id) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_title))
                .setMessage(getString(R.string.delete_text))
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        String[] args = {String.valueOf(id)};
                        switch (id_type) {
                            case 1:
                                database.delete("users_group", "_id=?", args);
                                break;
                            case 2:
                                database.delete("items_type", "_id=?", args);
                                break;
                            case 3:
                                database.delete("status", "_id=?", args);
                                break;
                        }
                        reload();
                    }
                }).create().show();
    }

    public void b_pref(View v) {
        Intent i = new Intent(this, preferences.class);
        startActivity(i);
    }

    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        int itemId = item.getItemId();
        if (itemId == R.id.b_edit) {
            id_edit = info.id;
            alert_input_dialog(R.string.edit_title, R.string.edit_text,
                    cursor.getString(cursor.getColumnIndex("name")));
            reload();
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
