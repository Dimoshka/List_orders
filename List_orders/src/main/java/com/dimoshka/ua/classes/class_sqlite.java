package com.dimoshka.ua.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class class_sqlite extends SQLiteOpenHelper {

    public SQLiteDatabase database;

    public class_sqlite(Context context, String databaseName, int db_version) {
        super(context, databaseName, null, db_version);
        database = this.getWritableDatabase();
    }

    public SQLiteDatabase openDataBase() {
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.i(getClass().getName(), "Start create SQLITE");
        try {
            //items_type
            database.execSQL("CREATE TABLE items_type (_id INTEGER PRIMARY KEY, name TEXT, show BOOLEAN DEFAULT (1), sync BOOLEAN DEFAULT (0));");
            database.execSQL("INSERT INTO [items_type] ([_id], [name], [show]) VALUES (1, 'Default', 1);");
            //status
            database.execSQL("CREATE TABLE status (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, show BOOLEAN DEFAULT (1), sync BOOLEAN DEFAULT (0));");
            database.execSQL("INSERT INTO [status] ([_id], [name], [show]) VALUES (1, 'Default', 1);");
            //categories
            database.execSQL("CREATE TABLE categories (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, show BOOLEAN DEFAULT (1), sync BOOLEAN DEFAULT (0));");
            database.execSQL("INSERT INTO [categories] ([_id], [name], [show]) VALUES (1, 'Default', 1);");
            //items_warehouse
            database.execSQL("CREATE TABLE items_warehouse (_id INTEGER PRIMARY KEY, id_it INTEGER UNIQUE, number NUMERIC DEFAULT (1), sync BOOLEAN DEFAULT (0));");
            //users
            database.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, id_contact INTEGER DEFAULT (0), id_group INTEGER DEFAULT (1), sync BOOLEAN DEFAULT (0));");
            database.execSQL("INSERT INTO [users] ([_id], [name], [id_contact], [id_group]) VALUES (1, 'Default', 0, 1);");
            //users_group
            database.execSQL("CREATE TABLE users_group (_id  INTEGER PRIMARY KEY, name TEXT UNIQUE, sync BOOLEAN DEFAULT (0));");
            database.execSQL("INSERT INTO [users_group] ([_id], [name]) VALUES (1, 'Default');");
            //orders
            database.execSQL("CREATE TABLE orders (_id INTEGER  PRIMARY KEY, id_cat INTEGER, id_u INTEGER, id_it INTEGER, number NUMERIC DEFAULT (1), date DATE DEFAULT (CURRENT_DATE), date_edit DATETIME DEFAULT (CURRENT_TIMESTAMP), id_st INTEGER, sync BOOLEAN DEFAULT (0));");
            //items
            database.execSQL("CREATE TABLE items (_id INTEGER PRIMARY KEY, name TEXT, id_it_t INTEGER, code VARCHAR(32), show BOOLEAN DEFAULT (1), sync BOOLEAN DEFAULT (0));");
            database.execSQL("INSERT INTO [items] ([_id], [name], [id_it_t], [code]) VALUES (1, 'Default', 1, '');");

            //index
            database.execSQL("CREATE INDEX idx_items_type ON items_type (_id ASC);");
            database.execSQL("CREATE INDEX idx_status ON status (_id ASC);");
            database.execSQL("CREATE INDEX idx_categories ON categories (_id ASC);");
            database.execSQL("CREATE INDEX idx_users ON users (_id ASC);");
            database.execSQL("CREATE INDEX idx_orders ON orders (id_it  ASC, id_u ASC, _id ASC, id_st ASC, id_cat ASC);");
            database.execSQL("CREATE INDEX idx_items ON items (_id ASC, id_it_t ASC);");
        } catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.i(getClass().getName(), "Start update SQLITE");
        try {
            // database.execSQL("DROP TABLE IF EXISTS items_type");
            // database.execSQL("DROP TABLE IF EXISTS status");
            // database.execSQL("DROP TABLE IF EXISTS categories");
            // database.execSQL("DROP TABLE IF EXISTS items_warehouse");
            // database.execSQL("DROP TABLE IF EXISTS users");
            // database.execSQL("DROP TABLE IF EXISTS users_group");
            // database.execSQL("DROP TABLE IF EXISTS orders");
            // database.execSQL("DROP TABLE IF EXISTS items");

            switch (oldVersion) {
                case 1:
                    database.execSQL("ALTER TABLE items ADD COLUMN show BOOLEAN DEFAULT (1);");
                    break;
                case 2:
                    database.execSQL("ALTER TABLE items_type ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE status ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE categories ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE items_warehouse ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE users ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE users_group ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE orders ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    database.execSQL("ALTER TABLE items ADD COLUMN sync BOOLEAN DEFAULT (0);");
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }

        // onCreate(database);
    }
}