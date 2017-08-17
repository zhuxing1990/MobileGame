package com.vunke.mobilegame.manage;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.vunke.mobilegame.model.GameInfo;
import com.vunke.mobilegame.utils.FormatTools;
import com.vunke.mobilegame.utils.WorkLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhuxi on 2017/3/15.
 */
public class AppsManage {
    public static final int UPDATE_DATA = 0x10001;
    public static final int ADDED_APK = 0x10002;
    public static final int REMOVED_APK = 0x10003;
    private static final String TAG = "AppsManage";
    private static  Uri uri = Uri.parse("content://com.vunke.mobilegame.provider.gameinfo/game_info");
    /**
     * 获取所有应用信息
     *
     * @param context
     * @return
     */
    public static ArrayList<GameInfo> getAllAppList(Context context) {
        PackageManager manager = context.getPackageManager();
        Intent appIntent = new Intent(Intent.ACTION_MAIN, null);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = manager.queryIntentActivities(appIntent, 0);
        ArrayList<GameInfo> gameInfoArrayList = null;
        Iterator<ResolveInfo> localIterator = null;
        if (resolveInfoList != null) {
            gameInfoArrayList = new ArrayList<>();
            localIterator = resolveInfoList.iterator();
        }
        while (true) {
            if (!localIterator.hasNext()) {
                WorkLog.d(TAG, "getAllAppList: localIterator is Empty");
                break;
            }
            ResolveInfo localResolveInfo = (ResolveInfo) localIterator.next();
            GameInfo gameInfo = new GameInfo();
            gameInfo.setGame_Icon(localResolveInfo.activityInfo.loadIcon(manager));
            gameInfo.setGame_name(localResolveInfo.activityInfo.loadLabel(manager).toString());
            gameInfo.setGame_package(localResolveInfo.activityInfo.packageName);
            gameInfo.setGame_activity(localResolveInfo.activityInfo.name);
//            String publicSourceDir = localResolveInfo.activityInfo.applicationInfo.publicSourceDir;

            String pkgName = localResolveInfo.activityInfo.packageName;
            PackageInfo packagInfo;
            try {
                packagInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
                gameInfo.setVersion_code(packagInfo.versionCode);
                gameInfo.setVersion_name(packagInfo.versionName);
                gameInfo.setCreate_time(packagInfo.firstInstallTime);
                gameInfo.setUpdate_time(packagInfo.lastUpdateTime);
                gameInfo.setSystemApp(isSystemApp(packagInfo.applicationInfo));
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameInfoArrayList.add(gameInfo);
            WorkLog.d(TAG, "getAllAppList: " + "gameInfo:" + gameInfo.toString());
//            String appPackage = gameInfo.getGame_package();

//            if (!appPackage.equals("com.vunke.apptvstore") && !appPackage.equals("com.kxy.tl") && !appPackage.equals("com.vunken.tv_sharehome")) {
//                WorkLog.d(TAG, "getAllAppList: "+"not's vunke App");
//                if (!gameInfo.isSystemApp()){
//                    gameInfoArrayList.add(gameInfo);
//                }
//                WorkLog.d(TAG, "getAllAppList: "+"gameInfoArrayList:"+gameInfoArrayList.toString());
//            }else{
//                WorkLog.d(TAG, "getAllAppList: "+"is vunke App");
//            }
        }
        return gameInfoArrayList;
    }

    /**
     * 是否为系统应用
     *
     * @param
     * @return
     */
    public static boolean isSystemApp(ApplicationInfo info) {
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            WorkLog.i(TAG, "systemApp is true1");
            return true;
        } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {// 判断是不是系统应用
            WorkLog.i(TAG, "systemApp is true2");
            return true;
        }
        WorkLog.i(TAG, "systemApp is false");
        return false;
    }
    /**
     * 根据包名启动APK
     *
     * @param packageName
     * @param context
     */
    public static void StartPackager(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            WorkLog.e("tv_launcher", "包名为空");
            return;
        }
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = (ResolveInfo) apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static long InsertGameInfo(Context context, List<GameInfo> list) {
        long l = -1;
        WorkLog.d(TAG, "InsertGameInfo: start insert");
//        Uri uri = Uri.parse("content://com.vunke.mobilegame.provider.gameinfo/game_info");
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(uri,null,null);
//        ArrayList<ContentProviderOperation> ops;
        ContentValues values;
        try {
            for (int i = 0; i < list.size(); i++) {
                GameInfo gameInfo = list.get(i);
                values = new ContentValues();
                values.put("create_time", gameInfo.getCreate_time());
                values.put("game_name", gameInfo.getGame_name());
                values.put("game_icon", FormatTools.getInstance().Drawable2Bytes(gameInfo.getGame_Icon()));
                values.put("game_package", gameInfo.getGame_package());
                values.put("game_activity", gameInfo.getGame_activity());
                values.put("version_code", gameInfo.getVersion_code());
                values.put("update_time", gameInfo.getUpdate_time());
                values.put("version_name", gameInfo.getVersion_name());
                values.put("is_systemapp", gameInfo.isSystemApp());
                WorkLog.i(TAG, "InsertGameInfo: " +"values"+values.toString());
                contentResolver.insert(uri, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ops = new ArrayList<ContentProviderOperation>();
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("order_id", "").build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("create_time", gameInfo.getCreate_time()).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("used_time", "").build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("click", "").build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("game_name", gameInfo.getGame_name()).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("game_icon", FormatTools.getInstance().Drawable2Bytes(gameInfo.getGame_Icon())).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("game_package", gameInfo.getGame_package()).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("game_activity", gameInfo.getGame_activity()).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("version_code", gameInfo.getVersion_code()).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("update_time", gameInfo.getUpdate_time()).build());
//        ops.add(ContentProviderOperation.newInsert(uri).withValue("version_name", gameInfo.getVersion_name()).build());
//        contentResolver.applyBatch("com.vunke.mobilegame.provider.gameinfo", ops);
        WorkLog.d(TAG, "InsertGameInfo: end insert");
        return l;
    }

    public static List<GameInfo> qureyGameInfo(Context context){
        WorkLog.d(TAG, "qureyGameInfo: start qurey");
//        Uri uri = Uri.parse("content://com.vunke.mobilegame.provider.gameinfo/game_info");
        Cursor cursor = context.getContentResolver().query(uri,null,null,null,null);
        List<GameInfo> list = new ArrayList<>();
        GameInfo gameInfo;
        try {
            while (cursor.moveToNext()){
                gameInfo = new GameInfo();
                gameInfo.setCreate_time(cursor.getLong(cursor.getColumnIndex("create_time")));
                gameInfo.setGame_name(cursor.getString(cursor.getColumnIndex("game_name")));
                byte []  icon = cursor.getBlob(cursor.getColumnIndex("game_icon"));
                gameInfo.setGame_Icon(FormatTools.getInstance().Bytes2Drawable(icon));
                gameInfo.setGame_package(cursor.getString(cursor.getColumnIndex("game_package")));
                gameInfo.setGame_activity(cursor.getString(cursor.getColumnIndex("game_activity")));
                gameInfo.setVersion_code(cursor.getInt(cursor.getColumnIndex("version_code")));
                gameInfo.setUpdate_time(cursor.getLong(cursor.getColumnIndex("update_time")));
                gameInfo.setVersion_name(cursor.getString(cursor.getColumnIndex("version_name")));
                gameInfo.setGame_desc(cursor.getString(cursor.getColumnIndex("game_desc")));
                String isSystemApp = cursor.getString(cursor.getColumnIndex("is_systemapp"));
                if (isSystemApp.equals("0")){
                    gameInfo.setSystemApp(false);
                }else  if (isSystemApp.equals("1")){
                    gameInfo.setSystemApp(true);
                }
                WorkLog.i(TAG, "qureyGameInfo: "+"game_info"+gameInfo.toString());
                if (gameInfo.getGame_package().equals("com.tencent.tmgp.sgame")){
                    list.add(gameInfo);
                }else
                if (gameInfo.isSystemApp()){
                    list.add(gameInfo);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        return list;
    }
   public static  String getTopActivity(Context context){
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE) ;
       ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
//       String shortClassName = info.topActivity.getShortClassName(); //类名
//       String className = info.topActivity.getClassName(); //完整类名
       String packageName = info.topActivity.getPackageName(); //包名
//        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1) ;
//        if(runningTaskInfos != null)
//            return (runningTaskInfos.get(0).topActivity).toString() ;
//        else
//            return null ;
       return packageName;
    }
    public static void KillApp(Context context,String packageName){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        activityManager.restartPackage(packageName);
        activityManager.killBackgroundProcesses(packageName);
    }
}
