package com.vunke.mobilegame.manage;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import com.vunke.mobilegame.model.GameInfo;
import com.vunke.mobilegame.utils.WorkLog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhuxi on 2017/3/15.
 */
public class AppsManage {
    private static final String TAG = "AppsManage";
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
                gameInfo.setSystemApp(isSystemApp(packagInfo));
            } catch (Exception e) {
                e.printStackTrace();
            }
            WorkLog.d(TAG, "getAllAppList: "+"gameInfo:"+gameInfo.toString());
            String appPackage = gameInfo.getGame_package();
//            if (!appPackage.equals("com.vunke.apptvstore") && !appPackage.equals("com.kxy.tl") && !appPackage.equals("com.vunken.tv_sharehome")) {
//                WorkLog.d(TAG, "getAllAppList: "+"not's vunke App");
//                if (!gameInfo.isSystemApp()){
//                    gameInfoArrayList.add(gameInfo);
//                }
//                WorkLog.d(TAG, "getAllAppList: "+"gameInfoArrayList:"+gameInfoArrayList.toString());
//            }else{
//                WorkLog.d(TAG, "getAllAppList: "+"is vunke App");
//            }
            if (!appPackage.equals("com.vunke.mobilegame")){
                gameInfoArrayList.add(gameInfo);
            }
        }
        return gameInfoArrayList;
    }

    /**
     * 是否为系统应用
     *
     * @param pInfo
     * @return
     */
    public static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
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

}
