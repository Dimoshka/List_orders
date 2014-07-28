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

            //-- Table: update_table
            database.execSQL("CREATE TABLE update_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ));");
            database.execSQL("INSERT INTO [update_table] ([_id], [last_update]) VALUES (1, '2014-01-01 01:01:01');");
            //-- Table: items_type
            database.execSQL("CREATE TABLE items_type ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR( 64 ) NOT NULL UNIQUE ON CONFLICT FAIL,show BOOLEAN DEFAULT ( 1 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ));");
            database.execSQL("INSERT INTO [items_type] ([_id], [name]) VALUES (1, 'Default');");
            //-- Table: items_warehouse
            database.execSQL("CREATE TABLE items_warehouse ( _id INTEGER PRIMARY KEY AUTOINCREMENT, id_it INTEGER NOT NULL UNIQUE ON CONFLICT FAIL, number NUMERIC NOT NULL DEFAULT ( 1 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ));");
            //-- Table: orders
            database.execSQL("CREATE TABLE orders (_id INTEGER PRIMARY KEY AUTOINCREMENT, id_cat INTEGER NOT NULL, id_u INTEGER NOT NULL, id_it INTEGER NOT NULL, number NUMERIC NOT NULL DEFAULT ( 1 ), date DATE DEFAULT ( CURRENT_DATE ), date_edit DATETIME DEFAULT ( CURRENT_TIMESTAMP ), id_st INTEGER NOT NULL DEFAULT ( 1 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ) );");
            //-- Table: status
            database.execSQL("CREATE TABLE status (_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR( 64 ) NOT NULL UNIQUE ON CONFLICT FAIL, show BOOLEAN DEFAULT ( 1 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ));");
            database.execSQL("INSERT INTO [status] ([_id], [name], [show], [last_update]) VALUES (1, 'Принят', 1, 0);");
            database.execSQL("INSERT INTO [status] ([_id], [name], [show], [last_update]) VALUES (2, 'Заказан', 1, 0);");
            database.execSQL("INSERT INTO [status] ([_id], [name], [show], [last_update]) VALUES (3, 'Нету', 1, 0);");
            database.execSQL("INSERT INTO [status] ([_id], [name], [show], [last_update]) VALUES (4, 'Не вручено', 1, 0);");
            database.execSQL("INSERT INTO [status] ([_id], [name], [show], [last_update]) VALUES (5, 'Выполненно', 0, 0);");
            // -- Table: users
            database.execSQL("CREATE TABLE users ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR( 128 ) NOT NULL UNIQUE ON CONFLICT FAIL, id_contact INTEGER DEFAULT ( 0 ), id_group INTEGER NOT NULL DEFAULT ( 1 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ) );");
            database.execSQL("INSERT INTO [users] ([_id], [name], [id_contact], [id_group]) VALUES (1, 'Default', 0, 1);");
            //-- Table: users_group
            database.execSQL("CREATE TABLE users_group ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR( 64 ) NOT NULL UNIQUE ON CONFLICT FAIL, last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ) );");
            database.execSQL("INSERT INTO [users_group] ([_id], [name], [last_update]) VALUES (1, 'Default', 0);");
            //-- Table: items
            database.execSQL("CREATE TABLE items ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR( 128 ) NOT NULL UNIQUE ON CONFLICT FAIL, id_it_t INTEGER NOT NULL, code VARCHAR( 32 ) NOT NULL, show BOOLEAN DEFAULT ( 1 ), individual BOOLEAN DEFAULT ( 0 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ) );");
            database.execSQL("INSERT INTO [items] ([_id], [name], [id_it_t], [code]) VALUES (1, 'Default', 1, '1');");
            //-- Table: categories
            database.execSQL("CREATE TABLE categories ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR( 64 ) NOT NULL UNIQUE ON CONFLICT FAIL, show BOOLEAN DEFAULT ( 1 ), last_update DATETIME DEFAULT ( CURRENT_TIMESTAMP ) );");
            database.execSQL("INSERT INTO [categories] ([_id], [name], [show], [last_update]) VALUES (1, 'Спец. заказ', 1, 0);");
            database.execSQL("INSERT INTO [categories] ([_id], [name], [show], [last_update]) VALUES (2, 'Личный заказ', 1, 0);");
            database.execSQL("INSERT INTO [categories] ([_id], [name], [show], [last_update]) VALUES (3, 'Постоянные', 1, 0);");

            //-- Index: idx_items_type
            database.execSQL("CREATE INDEX idx_items_type ON items_type ( _id ASC, show ASC );");
            //-- Index: idx_items_warehouse
            database.execSQL("CREATE INDEX idx_items_warehouse ON items_warehouse ( _id ASC, id_it ASC );");
            //-- Index: idx_orders
            database.execSQL("CREATE INDEX idx_orders ON orders ( _id ASC, id_u ASC, id_st ASC, id_cat ASC, id_it ASC );");
            //-- Index: idx_status
            database.execSQL("CREATE INDEX idx_status ON status ( _id ASC, show ASC );");
            //-- Index: idx_users
            database.execSQL("CREATE INDEX idx_users ON users ( _id ASC, id_group ASC );");
            //-- Index: idx_users_group
            database.execSQL("CREATE INDEX idx_users_group ON users_group ( _id ASC );");
            //-- Index: idx_orders2
            database.execSQL("CREATE INDEX idx_orders2 ON orders ( id_cat ASC, id_st ASC );");
            //-- Index: idx_items
            database.execSQL("CREATE INDEX idx_items ON items ( _id ASC, show ASC );");
            //-- Index: idx_items2
            database.execSQL("CREATE INDEX idx_items2 ON items ( id_it_t ASC );");
            //-- Index: idx_categories
            database.execSQL("CREATE INDEX idx_categories ON categories ( _id ASC );");
            //-- Trigger: update_items_type
            database.execSQL("CREATE TRIGGER update_items_type AFTER UPDATE ON items_type FOR EACH ROW BEGIN UPDATE items_type SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_items_warehouse
            database.execSQL("CREATE TRIGGER update_items_warehouse AFTER UPDATE ON items_warehouse FOR EACH ROW BEGIN UPDATE items_warehouse SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_orders
            database.execSQL("CREATE TRIGGER update_orders AFTER UPDATE ON orders FOR EACH ROW BEGIN UPDATE orders SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_status
            database.execSQL("CREATE TRIGGER update_status AFTER UPDATE ON status FOR EACH ROW BEGIN UPDATE status SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_users
            database.execSQL("CREATE TRIGGER update_users AFTER UPDATE ON users FOR EACH ROW BEGIN UPDATE users SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_users_group
            database.execSQL("CREATE TRIGGER update_users_group AFTER UPDATE ON users_group FOR EACH ROW BEGIN UPDATE users_group SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_items_type
            database.execSQL("CREATE TRIGGER insert_items_type AFTER INSERT ON items_type FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
           //-- Trigger: insert_items_warehouse
            database.execSQL("CREATE TRIGGER insert_items_warehouse AFTER INSERT ON items_warehouse FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_orders
            database.execSQL("CREATE TRIGGER insert_orders AFTER INSERT ON orders FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_status
            database.execSQL("CREATE TRIGGER insert_status AFTER INSERT ON status FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_users
            database.execSQL("CREATE TRIGGER insert_users AFTER UPDATE ON users FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_users_group
            database.execSQL("CREATE TRIGGER insert_users_group AFTER INSERT ON users_group FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_items
            database.execSQL("CREATE TRIGGER update_items AFTER UPDATE ON items FOR EACH ROW BEGIN UPDATE items SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_items
            database.execSQL("CREATE TRIGGER insert_items AFTER INSERT ON items FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: update_categories
            database.execSQL("CREATE TRIGGER update_categories AFTER UPDATE ON categories FOR EACH ROW BEGIN UPDATE categories SET last_update = CURRENT_TIMESTAMP WHERE _id = old._id; UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");
            //-- Trigger: insert_categories
            database.execSQL("CREATE TRIGGER insert_categories AFTER INSERT ON categories FOR EACH ROW BEGIN UPDATE update_table SET last_update = CURRENT_TIMESTAMP WHERE _id = 1; END;");

        } catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        Log.i(getClass().getName(), "Start update SQLITE");
        try {
             database.execSQL("DROP TABLE IF EXISTS items_type");
             database.execSQL("DROP TABLE IF EXISTS status");
             database.execSQL("DROP TABLE IF EXISTS categories");
             database.execSQL("DROP TABLE IF EXISTS items_warehouse");
             database.execSQL("DROP TABLE IF EXISTS users");
             database.execSQL("DROP TABLE IF EXISTS users_group");
             database.execSQL("DROP TABLE IF EXISTS orders");
             database.execSQL("DROP TABLE IF EXISTS items");
            database.execSQL("DROP TABLE IF EXISTS update_table");

/*
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
            */
        } catch (Exception ex) {
            Log.e(getClass().getName(), ex.toString());
        }

        // onCreate(database);
    }


}