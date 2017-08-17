package com.vunke.mobilegame.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhuxi on 2017/7/13.
 */
public class GameTimeSQLite extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "gametime.db";
    public static final int DATABASE_VERSION = 1;
    private String CreateTable = "create table gametime (_id integer not null primary key autoincrement,gametime integer,update_time integer)";
    public GameTimeSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists gametime";
        db.execSQL(sql);
    }
}
