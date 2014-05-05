package com.dimoshka.ua.list_orders;

import android.R.layout;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.dimoshka.ua.classes.class_activity_extends;
import com.dimoshka.ua.classes.class_function;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class backup_db extends class_activity_extends {

    private class_function funct = new class_function();
    private ListView listView;
    private List<String> files_arr = new ArrayList<String>();
    private List<String> name_arr = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_save);
        Button b = (Button) findViewById(R.id.b_add);
        b.setText(R.string.add);
        listView = (ListView) findViewById(R.id.list);
        registerForContextMenu(listView);
        load();
    }

    private void load() {
        browseTo(new File(Environment.getExternalStorageDirectory()
                + "/Backup/" + this.getPackageName() + "/"));

    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            menu.setHeaderTitle(R.string.management);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_revert_delete, menu);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        if (item.getItemId() == R.id.b_delete) {
            delete(info.id);
        }

        if (item.getItemId() == R.id.b_revert) {
            revert(info.id);
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
                        funct.file_delete(getBaseContext(),
                                files_arr.get((int) id));
                        browseTo(new File(Environment
                                .getExternalStorageDirectory()
                                + "/Backup/"
                                + getBaseContext().getPackageName() + "/"));
                    }
                }).create().show();
    }

    private void browseTo(final File aDirectory) {
        if (aDirectory.isDirectory()) {
            File[] files = aDirectory.listFiles();
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(
                            f2.lastModified());
                }
            });
            fill(files);
        }
    }

    private void fill(File[] files) {
        files_arr.clear();
        name_arr.clear();
        for (File file : files) {
            files_arr.add(file.getAbsolutePath());
            name_arr.add(file.getName().substring(0, 10));
        }

        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
                layout.simple_list_item_1, name_arr);

        listView.setAdapter(directoryList);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    public void revert(final long id) {
        funct.file_restory(this, files_arr.get((int) (id)),
                getString(R.string.db_name));
        onBackPressed();
    }

    public void b_add(View v) {
        funct.file_backup(this, getString(R.string.db_name));
        load();
    }

}
