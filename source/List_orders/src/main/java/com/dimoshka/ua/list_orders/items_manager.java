package com.dimoshka.ua.list_orders;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_barcode_intentintegrator;
import com.dimoshka.ua.classes.class_barcode_intentresult;
import com.dimoshka.ua.classes.class_simplecursoradapter_textsize;
import com.dimoshka.ua.classes.class_sqlite;

public class items_manager extends class_activity_extends {

    private Spinner s_items_type;
    private EditText et_name;
    private EditText et_code;
    private CheckBox check_show;

    private Cursor cursor_it_t;
    private int id_it_t;
    private long id_it;
    private String name_it;
    private String code_it;
    private Boolean show;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_manager);
         Bundle extras = getIntent().getExtras();
        id_it = extras.getLong("id_it");
        id_it_t = extras.getInt("id_it_t");
        name_it = extras.getString("name_it");
        code_it = extras.getString("code_it");
        if (extras.getInt("show") != 0)
            show = true;
        else
            show = false;

        s_items_type = (Spinner) findViewById(R.id.s_items_type);
        et_name = (EditText) findViewById(R.id.et_name);
        et_code = (EditText) findViewById(R.id.et_code);
        check_show = (CheckBox) findViewById(R.id.check_show);

        class_sqlite dbOpenHelper = new class_sqlite(this,
                getString(R.string.db_name),
                Integer.valueOf(getString(R.string.db_version)));
        database = dbOpenHelper.openDataBase();

        stopManagingCursor(cursor_it_t);

        cursor_it_t = database.query("items_type",
                new String[]{"_id", "name"}, null, null, null, null, "name");

        startManagingCursor(cursor_it_t);

        class_simplecursoradapter_textsize Adapt_users = new class_simplecursoradapter_textsize(
                this, android.R.layout.simple_spinner_item, cursor_it_t,
                new String[]{"name"}, new int[]{android.R.id.text1},
                prefs.getString("font_size", "2"));
        Adapt_users
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_items_type.setAdapter(Adapt_users);

        edit();
    }

    @SuppressWarnings("deprecation")
    private void edit() {
        if (id_it > 0) {
            Button b = (Button) findViewById(R.id.b_add);
            b.setText(R.string.save);
            et_name.setText(name_it);
            et_code.setText(code_it);
            check_show.setChecked(show);

            for (int u = 0; u < s_items_type.getCount(); u++) {
                Cursor cu = (Cursor) s_items_type.getItemAtPosition(u);
                startManagingCursor(cu);
                if (cu.getLong(cu.getColumnIndex("_id")) == id_it_t) {
                    s_items_type.setSelection(u);
                    break;
                }
            }

        }
    }

    public void b_scan(View v) {
        class_barcode_intentintegrator integrator = new class_barcode_intentintegrator(
                this);
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        class_barcode_intentresult scanResult = class_barcode_intentintegrator
                .parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                et_code.setText(contents.toString());
            }
        }
    }

    public void b_pref(View v) {
        Intent i = new Intent(this, preferences.class);
        startActivity(i);
    }

    public void b_add(View v) {
        ContentValues initialValues = new ContentValues();

        cursor_it_t.moveToPosition(s_items_type.getSelectedItemPosition());
        int id_it_t = cursor_it_t.getInt(cursor_it_t.getColumnIndex("_id"));

        initialValues.put("id_it_t", id_it_t);
        initialValues.put("name", et_name.getText().toString());
        initialValues.put("code", et_code.getText().toString());

        if (check_show.isChecked()) {
            initialValues.put("show", "1");
        } else {
            initialValues.put("show", "0");
        }

        if (id_it > 0) {
            String[] args = {String.valueOf(id_it)};
            database.update("items", initialValues, "_id=?", args);
        } else
            database.insert("items", null, initialValues);
        onBackPressed();
    }

    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }
}
