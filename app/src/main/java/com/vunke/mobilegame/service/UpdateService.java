package com.vunke.mobilegame.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.vunke.mobilegame.base.ConfigInfo;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;

import java.io.File;
import java.text.NumberFormat;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhuxi on 2016/12/1.
 */
public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    private final int downlaodProgressMsg = 0x1002;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x1002:
                    CharSequence progress = msg.getData().getCharSequence("progress", "0");
                    WorkLog.e(TAG, "当前下载进度:" + progress);
                    break;
                default:
                    break;
            }
        }
    };
    private Message message;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WorkLog.i(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("download_url")) {
            String download_url = intent.getStringExtra("download_url");
            WorkLog.e(TAG, "download_url:" + download_url);
            if (!TextUtils.isEmpty(download_url)) {
                String ApkName = "Mobilegame" + UiUtils.getVersionName(getApplicationContext())+".apk";
                OkGo.post(download_url).tag(this)
                        .readTimeOut(60000)
                        .execute(new FileCallback(ApkName) {
                            @Override
                            public void onSuccess(File file, Call call, Response response) {
                                WorkLog.e(TAG, "获取文件路径:"+file.getAbsolutePath());
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                WorkLog.e(TAG,"更新失败,服务器无法访问");
                            }

                            @Override
                            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                                // UI 线程，文件下载过程中回调
                                //参数含义同　上传相同
                                try {
                                    if (progress>0) {
                                        NumberFormat nt = NumberFormat.getPercentInstance();
                                        // 设置百分数精确度2即保留两位小数
                                        nt.setMinimumFractionDigits(0);
                                        message = Message.obtain();
                                        message.what = 0x1002;
                                        message.getData().putCharSequence("progress", nt.format(progress));
                                        handler.sendMessage(message);
                                        if (nt.format(progress).equals("100%")){
                                            WorkLog.e(TAG,"下载成功");
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onAfter(File file, Exception e) {
                                super.onAfter(file, e);
                                if (file!=null){
                                    ConfigInfo.intent = new Intent(Intent.ACTION_VIEW);
                                    ConfigInfo.intent .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ConfigInfo.intent.setDataAndType(Uri.parse("file://" + file.toString()),
                                            "application/vnd.android.package-archive");
                                    startActivity(ConfigInfo.intent);
                                }
//                                ConfigInfo.intent = new Intent(getApplicationContext(), GuideActivity.class);
//                                ConfigInfo.intent .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(ConfigInfo.intent);
                                stopSelf();
                            }
                        });
            } else {
                WorkLog.e(TAG, "下载URL不存在");
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

}
