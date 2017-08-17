package com.vunke.mobilegame.manage;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.vunke.mobilegame.activity.AppSettingsActivity;
import com.vunke.mobilegame.activity.MainActivity;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zhuxi on 2017/7/25.
 */
public class LoginManage {
    private static final String TAG = "LoginManage";
    private static final Uri uri = Uri.parse("content://com.vunke.mobilegame.provider.login/login");
    public static void startLogin(final String userName, final String password, final Activity context){
        try {
            JSONObject json = new JSONObject();
            json.put("username",userName);
            json.put("password",password);
            WorkLog.i(TAG, "startLogin: json:"+json.toString());
            OkGo.post(UrlManage.BaseUrl+UrlManage.Login).tag(context)
                    .params("json",json.toString()).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    WorkLog.i(TAG, "onSuccess: ------------------------------------"+s);
                    try {
                        JSONObject js = new JSONObject(s);
                        if (js.has("code")){
                            int code = js.getInt("code");
//                            WorkLog.i(TAG, "onSuccess: code:"+code);
                            switch (code){
                                case 200:
                                    Intent intent = new Intent(context,MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    InsertLoginData(context,userName,password);
                                    break;
                                case 400:
//                                    WorkLog.i(TAG, "onSuccess: reqest failed  code:"+code);
                                    break;
                                case 500:
                                    UiUtils.showToast(js.getString("message"),context);
//                                    WorkLog.i(TAG, "onSuccess: reqest failed code:"+code);
                                    break;
                                case 1001:
//                                    WorkLog.i(TAG, "onSuccess: reqest failed code:"+code);
                                    UiUtils.showToast(js.getString("message"),context);
                                    break;
                                case 1002:
                                    UiUtils.showToast(js.getString("message"),context);
//                                    WorkLog.i(TAG, "onSuccess: reqest failed code:"+code);
                                    break;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    WorkLog.i(TAG, "onError:------------------------ ");
                    UiUtils.showToast("登录失败,服务器繁忙",context);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void LoginSetting(final String userName, final String password, final Activity context){
        try {
            final JSONObject[] json = {new JSONObject()};
            json[0].put("username",userName);
            json[0].put("password",password);
            WorkLog.i(TAG, "startLogin: json:"+ json[0].toString());
            OkGo.post(UrlManage.BaseUrl+UrlManage.Login).tag(context)
                    .params("json", json[0].toString()).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    WorkLog.i(TAG, "onSuccess: ------------------------------------"+s);
                    try {
                        JSONObject js = new JSONObject(s);
                        if (js.has("code")){
                            int code = js.getInt("code");
//                            WorkLog.i(TAG, "onSuccess: code:"+code);
                            switch (code){
                                case 200:
                                    Intent intent = new Intent(context,AppSettingsActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                    InsertLoginData(context,userName,password);
                                    break;
                                case 400:
//                                    WorkLog.i(TAG, "onSuccess: reqest failed  code:"+code);
                                    break;
                                case 500:
                                    UiUtils.showToast(js.getString("message"),context);
//                                    WorkLog.i(TAG, "onSuccess: reqest failed code:"+code);
                                    break;
                                case 1001:
//                                    WorkLog.i(TAG, "onSuccess: reqest failed code:"+code);
                                    UiUtils.showToast(js.getString("message"),context);
                                    break;
                                case 1002:
                                    UiUtils.showToast(js.getString("message"),context);
//                                    WorkLog.i(TAG, "onSuccess: reqest failed code:"+code);
                                    break;

                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    WorkLog.i(TAG, "onError:------------------------ ");
                    UiUtils.showToast("登录失败,服务器繁忙",context);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void AutoMaticLogin(Activity context,String userName){
        if (TextUtils.isEmpty(userName)){
            WorkLog.i(TAG, "AutoMaticLogin: userName is null");
            return;
        }
      String password =  QueryPassword(context,userName);
        if (TextUtils.isEmpty(password)){
            UiUtils.showToast("自动登录失败",context);
        }else{
            startLogin(userName,password,context);
        }
    }

    private static String QueryPassword(Activity context, String userName) {
        String password = "";
        String[] strings = new String[] { userName.trim() };
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null,null , strings, null);
            while (cursor.moveToNext()){
                 password = cursor.getString(cursor.getColumnIndex("password"));
                WorkLog.i(TAG, "QueryPassword: password:"+password);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (cursor!=null)
            cursor.close();
        }
        return password;
    }
    
    private static void InsertLoginData(Activity context,String userName,String password){
        ContentValues values = new ContentValues();
        values.put("user_name",userName);
        values.put("password",password);
        values.put("login_time",System.currentTimeMillis());
        Uri insert = context.getContentResolver().insert(uri, values);
        WorkLog.i(TAG, "InsertLoginData: insert:"+insert);
    }
}
