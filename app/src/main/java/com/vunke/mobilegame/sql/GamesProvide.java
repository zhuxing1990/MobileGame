package com.vunke.mobilegame.sql;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by zhuxi on 2017/3/9.
 */
public class GamesProvide extends ContentProvider{
    private final static String AUTHORITH = "com.vunke.mobilegame.games";
    private final static String PATH = "/game_info";
    private final static String PATHS = "/game_info/#";

    private final static String TABLE_NAME = GamesSQLite.DATABASE_NAME;
    private final static UriMatcher mUriMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    private static final int CODE_DIR = 1;
    private static final int CODE_ITEM = 2;
    static {
        mUriMatcher.addURI(AUTHORITH, PATH, CODE_DIR);
        mUriMatcher.addURI(AUTHORITH, PATHS, CODE_ITEM);
    }
    private GamesSQLite dbHelper;
    private SQLiteDatabase db;

    private static final String CREATE_TIME = "create_time";
    @Override
    public boolean onCreate() {
        dbHelper = new GamesSQLite(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        db = dbHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            case 1:
                String sql = "";
                if (selectionArgs != null && selectionArgs.length != 0) {

                }else{
                    sql = "select * from " + TABLE_NAME;
                }
                sql = sql + " order by "+CREATE_TIME+" desc";
                cursor = db.rawQuery(sql, null);
                break;

            default:
                break;
        }
            return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case CODE_DIR:
                return "vnd.android.cursor.dir/" + TABLE_NAME;
            case CODE_ITEM:
                return "vnd.android.cursor.item/" + TABLE_NAME;
            default:
                throw new IllegalArgumentException("异常参数");
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
