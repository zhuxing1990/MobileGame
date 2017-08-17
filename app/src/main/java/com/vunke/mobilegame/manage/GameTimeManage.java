package com.vunke.mobilegame.manage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.vunke.mobilegame.utils.WorkLog;

/**
 * Created by zhuxi on 2017/7/13.
 */
public class GameTimeManage {
    private static final String TAG = "GameTimeManage";
    private static final long defaultTime = 10;
    private static Uri uri = Uri.parse("content://com.vunke.mobilegame.provider.gametime/gametime");
    public static Uri InsertTime(Context context){
        WorkLog.i(TAG, "InsertTime: ");
        Uri l =null;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        try {
            if (cursor.moveToNext()){
                WorkLog.i(TAG, "InsertTime: get gametime is not null,return");
                return  l;
            }else {
                WorkLog.i(TAG, "InsertTime: get table data is null");
                ContentValues values = new ContentValues();
                values.put("gametime",defaultTime);
                values.put("update_time",System.currentTimeMillis());
                l = contentResolver.insert(uri,values);
                WorkLog.i(TAG, "InsertTime: InsertTime success:"+l);
            }
        }catch (Exception e){
            e.printStackTrace();
            return  l;
        }finally {
            if (cursor!=null)
                cursor.close();
        }
        return l;
    }
    public static int UpdateTime(Context context,long gametime){
        WorkLog.i(TAG, "UpdateTime: ");
        int update = -1;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        try {
        if (cursor.moveToNext()){
            WorkLog.i(TAG, "UpdateTime: update gametime");
            values.put("gametime",gametime);
            update = contentResolver.update(uri, values, null, null);
            WorkLog.i(TAG, "UpdateTime: update success:"+update);
            return update;
        }else{
            WorkLog.i(TAG, "UpdateTime: get table data is null,insert data");
            values.put("gametime",gametime);
            values.put("update_time",System.currentTimeMillis());
            Uri insert = contentResolver.insert(uri, values);
            WorkLog.i(TAG, "UpdateTime: insert success:"+insert);
        }
        }catch (Exception e){
            e.printStackTrace();
            return update;
        }finally {
            if (cursor!=null)
            cursor.close();
        }
        return update;
    }
    public static long QueryGameTime(Context context){
        WorkLog.i(TAG, "QueryGameTime: ");
        long gametime = defaultTime;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        try {
            while (cursor.moveToNext()){
                Log.i(TAG, "QueryGameTime: moveToFirst");
                 gametime = cursor.getLong(cursor.getColumnIndex("gametime"));
                WorkLog.i(TAG, "QueryGameTime: gemetime:"+gametime);
                 return gametime;
            }
        }catch (Exception e){
            e.printStackTrace();
            return gametime;
        }finally {
            if (cursor!=null)
                cursor.close();
        }
        return gametime;
    }
}
