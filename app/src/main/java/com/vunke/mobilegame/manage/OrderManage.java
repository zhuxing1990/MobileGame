package com.vunke.mobilegame.manage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.vunke.mobilegame.activity.LoginActivity;
import com.vunke.mobilegame.utils.SharedPreferencesUtil;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by zhuxi on 2017/8/14.
 */
public class OrderManage {
    private static final String TAG = "OrderManage";

    private static final Uri uri = Uri.parse("content://com.vunke.mobilegame.provider.order_info/order_info");
    public static void AddOrder(final Context context, int fee, int payType){
        final long createtime = System.currentTimeMillis();
        WorkLog.i(TAG, "AddOrder: createtime:"+ createtime);
        String transaction_id = getTransactionID("",21);
//        WorkLog.i(TAG, "AddOrder: transaction_id:"+transaction_id);
        InsertOrder(context,fee,payType,transaction_id,createtime);
//        QueryAllOrder(context);
        try {
            JSONObject json = new JSONObject();
            json.put("userId", SharedPreferencesUtil.getStringValue(context, LoginActivity.LoginKey,""));
            json.put("fee",fee);
            json.put("payType",payType);
            json.put("createTime", UiUtils.getDateTime(createtime));
            json.put("transactionid",transaction_id);
            WorkLog.i(TAG, "AddOrder: json="+json.toString());
            OkGo.post(UrlManage.BaseUrl+UrlManage.Order).tag(context).params("json",json.toString()).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    WorkLog.i(TAG, "onSuccess: s:"+s);
                    DeleteOrder(context,createtime);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    WorkLog.i(TAG, "onError: ");
                }

                @Override
                public void onAfter(String s, Exception e) {
                    super.onAfter(s, e);
                    QueryAllOrder(context);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void AddOrder(final Context context,final JSONObject json){
        try {
            WorkLog.i(TAG, "AddOrder: json="+json.toString());
            OkGo.post(UrlManage.BaseUrl+UrlManage.Order).tag(context).params("json",json.toString()).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    WorkLog.i(TAG, "onSuccess: s:"+s);
                    try {
                        DeleteOrder(context,json.getLong("createTime"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    WorkLog.i(TAG, "onError: ");

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
  public static Uri InsertOrder(Context context,int fee,int payType,String transaction_id,long create_time){
      try {
          ContentValues values = new ContentValues();
          values.put("user_name",SharedPreferencesUtil.getStringValue(context,LoginActivity.LoginKey,""));
          values.put("fee",fee);
          values.put("pay_tpye",payType);
          values.put("transaction_id",transaction_id);
          values.put("create_time",create_time);
          Uri insert = context.getContentResolver().insert(uri, values);
//          WorkLog.i(TAG, "InsertOrder: insert:"+insert);
          return insert;
      }catch (Exception e){
          e.printStackTrace();
      }
         return null;
  }
    public static void QueryAllOrder(final Context context){
        Cursor cursor = null;
        JSONObject json ;
          try {
               cursor = context.getContentResolver().query(uri, null, null, null, null);
              if (cursor!=null){
                  List<JSONObject> jsonList = new ArrayList<>();
                  while (cursor.moveToNext()){
                      String user_name = cursor.getString(cursor.getColumnIndex("user_name"));
                      int fee = cursor.getInt(cursor.getColumnIndex("fee"));
                      int payType = cursor.getInt(cursor.getColumnIndex("pay_tpye"));
                      String transaction_id = cursor.getString(cursor.getColumnIndex("transaction_id"));
                      long create_time = cursor.getLong(cursor.getColumnIndex("create_time"));
                      json = new JSONObject();
                      json.put("userId", user_name);
                      json.put("fee",fee);
                      json.put("payType",payType);
                      json.put("createTime", UiUtils.getDateTime(create_time));
                      json.put("transactionid",transaction_id);
                      WorkLog.i(TAG, "QueryAllOrder: json="+json.toString());
                      jsonList.add(json);
                  }
                  if (jsonList!=null&&jsonList.size()!=0){
                      Observable.from(jsonList)
                              .subscribeOn(Schedulers.newThread())
                              .observeOn(Schedulers.io())
                              .subscribe(new Subscriber<JSONObject>() {
                          @Override
                          public void onCompleted() {
                                this.unsubscribe();
                          }

                          @Override
                          public void onError(Throwable e) {
                              this.unsubscribe();
                          }

                          @Override
                          public void onNext( JSONObject jsonObject) {
                              AddOrder(context,jsonObject);
                          }
                      });
                  }
              }
          }catch (Exception e){
              e.printStackTrace();
          }finally {
              if (cursor!=null)cursor.close();
          }
    }


   public static void QueryOrder(Context context){
       Cursor cursor = null;
       try {
           String userName = SharedPreferencesUtil.getStringValue(context, LoginActivity.LoginKey, "");
           String[] strings = new String[] { userName.trim() };
           cursor = context.getContentResolver().query(uri,null,null,strings,null);
           if (cursor.moveToNext()){
               String user_name = cursor.getString(cursor.getColumnIndex("user_name"));
               int fee = cursor.getInt(cursor.getColumnIndex("fee"));
               int paytype = cursor.getInt(cursor.getColumnIndex("pay_tpye"));
               String transaction_id = cursor.getString(cursor.getColumnIndex("transaction_id"));
               long create_time = cursor.getLong(cursor.getColumnIndex("create_time"));
               WorkLog.i(TAG, "QueryAllOrder: userName:"+user_name+"\t fee:"+fee+"\t paytype:"+paytype+"\t transaction+id:"+transaction_id+"\t createTime:"+create_time);
           }
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           if (cursor!=null)cursor.close();
       }
   }
  public static void DeleteOrder(Context context,long createtime){
     try {
//         String where  =  "create_time = '"+createtime+" ' and user_name = '"+SharedPreferencesUtil.getStringValue(context,LoginActivity.LoginKey,"")+"'";
         String where = "create_time ='"+createtime+"'";
         WorkLog.i(TAG, "DeleteOrder:  where:"+where);
//         String where  = " user_name = '"+SharedPreferencesUtil.getStringValue(context,LoginActivity.LoginKey,"")+"'";
         int delete = context.getContentResolver().delete(uri, where, null);
         WorkLog.i(TAG, "DeleteOrder: delete:"+delete);
     }catch (Exception e){
         e.printStackTrace();
     }
  }
  public static void DeleteAllOrder(Context context){
      try{
          int delete = context.getContentResolver().delete(uri,null,null);
          WorkLog.i(TAG,"DeleteAllOrder: delete:"+delete);
      }catch (Exception e){
            e.printStackTrace();
      }
  }
    /**
     * 获取TransactionID,,length要大于pixStr的长度+20
     *
     * @return
     */
    public static String getTransactionID(String pixStr, int length) {
        if (length<=pixStr.length()+20) {
            return new Random().nextInt(length)+"";
        }
       /* if (length < 20) {
            return Tools.getRandomNum(length);
        }*/
        long currentTimeMillis = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); //格式化获取年月日
        String mydate = format.format(currentTimeMillis);
//    		java.util.Random();
//        String mydate = Tools.getDate("yyyyMMddHHmmss");
        int tmp = length - mydate.length();
        int pixsize = 0;
        if (pixStr != null) {
            pixsize = pixStr.length();
        }
        tmp = tmp - pixsize;

        String ret = "";
        String tmpStr = "0123456789";
        for (int i = 0; i < tmp; i++) {
            ret += tmpStr.charAt(((int) (Math.random() * tmpStr.length())));
        }

        return pixStr + mydate + ret;

    }
}
