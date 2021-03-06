package com.dimoshka.ua.list_orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
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
import com.dimoshka.ua.classes.class_sqlite;

public class users extends class_activity_extends {

    private ListView listView;
    private SimpleCursorAdapter sca_list;
    private Cursor cursor;

    private int id_u_g = 0;
    private Cursor cursor_u_g;
    private SimpleCursorAdapter sca_u_g;
    private Spinner s_categoryes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.list_spinner);
        s_categoryes = (Spinner) findViewById(R.id.s_categoryes);
        listView = (ListView) findViewById(R.id.list);
        class_sqlite dbOpenHelper = new class_sqlite(this,
                getString(R.string.db_name),
                Integer.valueOf(getString(R.string.db_version)));
        database = dbOpenHelper.openDataBase();
        load_groups();

        id_u_g = extras.getInt("id_u_g");

        if (id_u_g > 0) {
            for (int i = 0; i < s_categoryes.getCount(); i++) {
                Cursor cc = (Cursor) s_categoryes.getItemAtPosition(i);
                if (cc.getInt(cc.getColumnIndex("_id")) == id_u_g) {
                    s_categoryes.setSelection(i);
                    break;
                }
            }

        }

        sca_list = new SimpleCursorAdapter(this,
                R.layout.row_list_3, cursor, new String[]{"name", "number",
                "name_u_g"}, new int[]{R.id.text1, R.id.text2,
                R.id.text3}, 0
        );
        listView.setAdapter(sca_list);
        registerForContextMenu(listView);

        s_categoryes.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                reload();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                cursor.moveToPosition(position);
                Intent i = new Intent(getBaseContext(), orders_details.class);
                i.putExtra("id_u", cursor.getInt(cursor.getColumnIndex("_id")));
                i.putExtra("user",
                        cursor.getString(cursor.getColumnIndex("name")));
                i.putExtra("id_cat", 0);
                i.putExtra("category", "");
                startActivity(i);
            }
        });

    }

    private void load_groups() {
        cursor_u_g = database.query("users_group",
                new String[]{"_id", "name"}, null, null, null, null, "name");
        sca_u_g = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item, cursor_u_g,
                new String[]{"name"}, new int[]{android.R.id.text1},
                0);
        sca_u_g.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_categoryes.setAdapter(sca_u_g);
    }

    private void get_cursor_all_users() {
        id_u_g = cursor_u_g.getInt(cursor_u_g.getColumnIndex("_id"));
        cursor = database
                .rawQuery(
                        "SELECT users._id, users.name, ord.number, users.id_group, users.id_contact, users_group.name as name_u_g from users left join (select count(*) as number, id_u from orders left join status on orders.id_st=status._id where status.show=1 group by orders.id_u) as ord on users._id=ord.id_u left join users_group on users.id_group=users_group._id where users.id_group="
                                + id_u_g + " order by users.name asc", null
                );
    }

    private void reload() {
        get_cursor_all_users();
        sca_list.changeCursor(cursor);
    }

    public void b_add() {
        Intent i = new Intent(getBaseContext(), users_manager.class);
        i.putExtra("id_u", 0);
        startActivity(i);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item0:
                b_add();
                break;
            case R.id.item1:
                reload();
                break;
            case R.id.item6:
                Intent a = new Intent(this, managment_select.class);
                a.putExtra("id_type", 1);
                startActivity(a);
                break;
            default:
                break;

        }

        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            menu.setHeaderTitle(R.string.management);
            android.view.MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit_delete, menu);
        }
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
                        database.delete("users", "_id=?", args);
                        database.delete("orders", "id_u=?", args);
                        reload();
                    }
                }).create().show();
    }

    public boolean onContextItemSelected(android.view.MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        cursor.moveToPosition(info.position);
        int itemId = item.getItemId();
        if (itemId == R.id.b_edit) {
            Intent i = new Intent(getBaseContext(), users_manager.class);
            i.putExtra("id_u", cursor.getLong(cursor.getColumnIndex("_id")));
            i.putExtra("id_u_g",
                    cursor.getInt(cursor.getColumnIndex("id_group")));
            i.putExtra("name_u",
                    cursor.getString(cursor.getColumnIndex("name")));
            i.putExtra("id_u_c",
                    cursor.getInt(cursor.getColumnIndex("id_contact")));
            startActivity(i);
        } else if (itemId == R.id.b_delete) {
            delete(cursor.getInt(cursor.getColumnIndex("_id")));
        }
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

}
