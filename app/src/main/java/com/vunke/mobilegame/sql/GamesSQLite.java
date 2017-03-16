package com.vunke.mobilegame.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhuxi on 2017/3/9.
 */
public class GamesSQLite extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "moblie_game.db";
    public static final int DATABASE_VERSION = 1;
    private String CreateTable = "create table moblie_game (_id integer not null primary key autoincrement,order_id integer,create_time integer,used_time integer,click integer,game_name varchar,game_icon varchar,game_desc varchar,game_package varchar,game_activity varchar,version_code varchar,version_name varchar,update_time integer,Other1 varchar,Other2 varchar,Other3 varchar)";
    public GamesSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "drop table if exists mobile_game";
        db.execSQL(sql);
    }
}
