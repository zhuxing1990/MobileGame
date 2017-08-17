package com.vunke.mobilegame.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhuxi on 2017/7/28.
 */
public class LoginSqlite extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "login.db";
    private static final int DATABASE_VERSION = 1;
    private String CreateTable = "create table login (_id integer not null primary key autoincrement ,user_name varchar,password varchar,login_time varchar)";
    public LoginSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists login";
        db.execSQL(sql);
    }
}
