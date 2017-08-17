package com.vunke.mobilegame.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vunke.mobilegame.utils.WorkLog;

/**
 * Created by zhuxi on 2017/3/8.
 */
public class NetWorkConnectChange extends BroadcastReceiver {
    private static final String TAG = "NetWorkConnectChange";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            WorkLog.i(TAG, "onReceive: network connect  changed");
        }
    }
}
