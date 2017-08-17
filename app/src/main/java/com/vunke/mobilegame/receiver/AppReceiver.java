package com.vunke.mobilegame.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.vunke.mobilegame.base.RxBus;
import com.vunke.mobilegame.manage.AppsManage;
import com.vunke.mobilegame.model.UpdateGameSetting;
import com.vunke.mobilegame.utils.WorkLog;

public class AppReceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private UpdateGameSetting updateGameSetting ;
    @Override  
    public void onReceive(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
          
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();  
            WorkLog.i(TAG, "--------APK安装成功" + packageName);
//            Toast.makeText(context, "安装成功" + packageName, Toast.LENGTH_LONG).show();
            updateGameSetting = new UpdateGameSetting();
            updateGameSetting.setUpdateCode(AppsManage.ADDED_APK);
            RxBus.getInstance().post(updateGameSetting);
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {  
            String packageName = intent.getData().getSchemeSpecificPart();
            WorkLog.i(TAG, "--------APK更新成功" + packageName);
//            Toast.makeText(context, "替换成功" + packageName, Toast.LENGTH_LONG).show();
            updateGameSetting = new UpdateGameSetting();
            updateGameSetting.setUpdateCode(AppsManage.ADDED_APK);
            RxBus.getInstance().post(updateGameSetting);
        } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            WorkLog.i(TAG, "--------APK卸载成功" + packageName);
//            Toast.makeText(context, "卸载成功" + packageName, Toast.LENGTH_LONG).show();
            updateGameSetting = new UpdateGameSetting();
            updateGameSetting.setUpdateCode(AppsManage.REMOVED_APK);
            RxBus.getInstance().post(updateGameSetting);
        }
    }  
  
}  