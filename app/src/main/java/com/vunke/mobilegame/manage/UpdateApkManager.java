package com.vunke.mobilegame.manage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.vunke.mobilegame.base.ConfigInfo;
import com.vunke.mobilegame.bean.UpdateBean;
import com.vunke.mobilegame.service.UpdateService;
import com.vunke.mobilegame.utils.SharedPreferencesUtil;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhuxi on 2016/12/1.
 */
public class UpdateApkManager {
    private static final String TAG = "UpdateApkManager";
    private Context context;
    /**
     * 获取更新的数据的 JAVABean
     */
    private UpdateBean updateBean;

    /**
     * 接收消息
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x100://强制升级
                    ConfigInfo.intent = new Intent(context, UpdateService.class);
                    ConfigInfo.intent.putExtra("download_url", updateBean.get_result().getDownload_url());
                    context.startService(ConfigInfo.intent);
                    break;
                default:

                    break;
            }
        }
    };

    private  static UpdateApkManager updateApkManager;
    public static UpdateApkManager getInstance(Context context){
        if (updateApkManager == null){
            synchronized (UpdateApkManager.class){
                if (updateApkManager == null){
                    updateApkManager = new UpdateApkManager(context);
                }
            }
        }
        return updateApkManager;
    }
    public UpdateApkManager(Context context) {
        this.context = context;
    }

    public void GetUpdateInfo() {
        final int versionCode = UiUtils.getVersionCode(context);
        OkGo.post(UrlManage.BaseUrl + UrlManage.UpdataUrl).tag(this)
//        OkGo.post(UrlManage.TestUrl + UrlManage.UpdataUrl).tag(this)
//                .params("ctype", "1").params("version", "" + versionCode)
                .params("ctype",1).params("version", "" + versionCode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        WorkLog.i(TAG, "获取数据:" + s);
                        try {
                            JSONObject json = new JSONObject(s);
                            if (json.has("_errcode")) {
                                Gson gson = new Gson();
                                updateBean = gson.fromJson(s, UpdateBean.class);

                                int errcode = updateBean.get_errcode();
                                if (errcode == 0) {// 0-请求成功
                                    WorkLog.e(TAG, "客户端版本号:" + versionCode + "\n服务器版本号:" + updateBean.get_result().getVersion());
                                    if (versionCode < updateBean.get_result().getVersion()) {
                                        WorkLog.e(TAG, "服务器版本号大于客户端版本号，需要升级");
                                        int update_state = updateBean.get_result().getUpdate_state();
                                        if (update_state == 1) {//1-需要升级
                                            WorkLog.e(TAG, "需要升级");
                                            int update_type = updateBean.get_result().getUpdate_type();
                                            if (update_type == 1) { //1-强制
                                                handler.sendEmptyMessage(0x100);
                                            } else if (update_type == 2) { //2-手动
                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                builder.setTitle("更新")
                                                        .setMessage("发现新版本，是否立即更新")
                                                        .setCancelable(true)
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                ConfigInfo.intent = new Intent(context, UpdateService.class);
                                                                ConfigInfo.intent.putExtra("download_url", updateBean.get_result().getDownload_url());
                                                                context.startService(ConfigInfo.intent);
                                                            }
                                                        })
                                                        .setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })

                                                        .setNeutralButton("暂不提醒", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                /**暂不更新**/
                                                                long time = System.currentTimeMillis();
                                                                SharedPreferencesUtil.setLongValue(context,
                                                                        ConfigInfo.UPDATE_TOMORROW, time);
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                Dialog updateDialog = builder.create();
                                                updateDialog.show();
                                            }

                                        } else if (update_state == 0) { //0-不需要升级
                                            WorkLog.e(TAG, "不需要升级");
                                        }
                                    } else {
                                        WorkLog.e(TAG, "服务器版本号小于或者等于客户端版本号，不升级");
                                    }
                                } else {//!= 0 - 请求失败
                                    WorkLog.e(TAG, "请求失败");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            WorkLog.e(TAG, "解析数据异常");
                        }

                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        WorkLog.e(TAG, "OnError");
                    }
                });
    }
}
