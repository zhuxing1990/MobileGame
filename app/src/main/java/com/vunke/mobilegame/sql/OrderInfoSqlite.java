package com.vunke.mobilegame.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhuxi on 2017/8/15.
 */
public class OrderInfoSqlite extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "order.db";
    private static final int DATABASE_VERSION = 1;
    private String sql = "create table order_info (_id integer not null primary key autoincrement,user_name varchar,fee integer,pay_tpye integer,transaction_id varchar,create_time varchar)";
    public OrderInfoSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists order_info";
        db.execSQL(sql);
    }
}
