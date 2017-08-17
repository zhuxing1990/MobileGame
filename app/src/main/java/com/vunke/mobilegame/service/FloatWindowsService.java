package com.vunke.mobilegame.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vunke.mobilegame.R;
import com.vunke.mobilegame.activity.LoginActivity;
import com.vunke.mobilegame.activity.MainActivity;
import com.vunke.mobilegame.base.RxBus;
import com.vunke.mobilegame.manage.AppsManage;
import com.vunke.mobilegame.manage.GameTimeManage;
import com.vunke.mobilegame.manage.OrderManage;
import com.vunke.mobilegame.model.UpdateGameSetting;
import com.vunke.mobilegame.usbdevice.USBUitls;
import com.vunke.mobilegame.utils.SuUtil;
import com.vunke.mobilegame.utils.UiUtils;
import com.vunke.mobilegame.utils.WorkLog;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhuxi on 2017/3/16.
 */
public class FloatWindowsService extends Service{
    private static final String TAG = "FloatWindowsService";
    
    private WindowManager windowManager;
    private WindowManager.LayoutParams  windowLayoutParams;
    private LinearLayout linearLayout;
    private TextView close_text;
    private TextView endTime_text;
    private TextView coin_text;
//    public static final String SHOW_FLOAT_WINDOW = "show_float_window";
//    public static final String HIDE_FLOAT_WINDOW = "hide_float_window";
    public static final String UPDATE_GAME_TIME ="update_game_time";
    private long gametime=-1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GameTimeManage.InsertTime(getApplicationContext());
        UpdateGameTime();
    }

    private void UpdateGameTime() {
        Log.i(TAG, "UpdateGameTime: ");
        gametime = GameTimeManage.QueryGameTime(getApplicationContext());
        if (gametime!=-1){
            WorkLog.i(TAG, "onCreate: get gametime success  gametime:"+gametime);
        }else{
            WorkLog.i(TAG, "onCreate: get gametime failed");
            gametime = 1;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)){
                WorkLog.i(TAG,"onStartCommand get action:"+action);
//                if (action.equals(SHOW_FLOAT_WINDOW)){
//                    if (linearLayout!=null){
//                        windowManager.removeView(linearLayout);
//                    }
//                }else if(action.equals(HIDE_FLOAT_WINDOW)){
//                    HideView();
//                }else {
                    initAction(action);
//                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void CreateFloatView(){
        if (linearLayout!=null){
            linearLayout.setVisibility(View.VISIBLE);
            return;
        }
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        windowLayoutParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置窗口权重
        windowLayoutParams.gravity = Gravity.CENTER | Gravity.TOP;
        windowLayoutParams.x = 0;
        windowLayoutParams.y = 0;
        windowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        windowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        linearLayout = (LinearLayout)layoutInflater.inflate(R.layout.window_float_small, null);
        close_text = (TextView) linearLayout.findViewById(R.id.close_text);
        endTime_text = (TextView) linearLayout.findViewById(R.id.endTime_text);
        coin_text = (TextView) linearLayout.findViewById(R.id.coin_text);
        coin_text.setText("投币即可上机");
//        initCloseTime(120,close_text);
        windowManager.addView(linearLayout,windowLayoutParams);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isSend = false;
        RemoveView();
        unSubscribe();
    }

    private void HideView() {
        if (linearLayout!=null){
            linearLayout.setVisibility(View.INVISIBLE);
        }
    }
    private void RemoveView(){
        if (linearLayout!=null){
            windowManager.removeView(linearLayout);
            linearLayout = null;
        }
        unSubscribe();
    }
    private void initAction(String action ) {
        if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
            WorkLog.i(TAG, "initAction:  usb is connect");
            CreateFloatView();
            USBUitls.initUSB(getApplicationContext());
            usb_receiveData();
            usb_sendData();
        }else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
            WorkLog.i(TAG, "initAction:  usb is disconnect");
//            HideView();
            USBUitls.disconnect_USB(getApplicationContext());
        }else if(action.equals(UPDATE_GAME_TIME)){
            WorkLog.i(TAG, "initAction: game time is update");
            UpdateGameTime();
            if (toubi>0){
                UpdateCloseTime(toubi*(gametime*60));
            }
        }else{
            WorkLog.i(TAG, "initAction:  get action:"+action);
        }
    }

    public void usb_receiveData(){
        isReceive = true;
        ReceiveThread receiveThread = new ReceiveThread();
        Thread thread = new Thread(receiveThread);
        thread.start();
    }
    public void usb_sendData(){
        isSend = true;
        SendThread sendThread = new SendThread();
        Thread send = new Thread(sendThread);
        send.start();
    }
    Message msg;
    private boolean isReceive = true;
    class ReceiveThread implements Runnable{

        @Override
        public void run() {
            synchronized(USBUitls.mReadBufferLock){
                while (!Thread.currentThread().isInterrupted()){
                    if (isReceive) {
                        if (USBUitls.getUsbDeviceConnection(getApplicationContext()) != null) {
//                            WorkLog.i(TAG, "ReceiveThread: getUsbDeviceConnection");
                            receiveMSG();
                        } else {
                            isReceive = false;
                        }
                    }
                }
            }
        }
    }

    private  String toHexString;
    private void receiveMSG() {
//        WorkLog.d(TAG, "receiveMSG: ");
        if(USBUitls.getUsbDeviceConnection(getApplicationContext()).claimInterface(USBUitls.getUsbInterface(getApplicationContext()), true)) {
//            WorkLog.i(TAG, "receiveMSG: claimInterface    getUsbDeviceConnection   getDeviceInterface ");
            if (USBUitls.receive_Message(getApplicationContext(), USBUitls.receiveBytes) >= 0) {
//                WorkLog.d(TAG, "receiveMSG: receive_Message  is OK");
                try {
                    toHexString = USBUitls.toHexString(USBUitls.receiveBytes, 12);
//                    WorkLog.d(TAG,"get msg:"+ toHexString);
                    msg = Message.obtain();
                    msg.what = 0x113;
                    msg.obj = toHexString;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                WorkLog.i("claimInterface: ", "failed");
            }
        }
    }
//    private long getMSG= 0;
    private int toubi = 0;
    private int consumptionTime = 0;
    private Handler handler = new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x113:
                    String data = (String) msg.obj;
                    data= data.replace(" ","");
//                    WorkLog.i(TAG, "handleMessage: data:"+data);
//                    if (data.contains("020b2001")){
//                        getMSG++;
////                        usbconnect_status_text1.setText("接收次数"+getMSG);
//                    }else
                    if(data.contains("020b2010")){
                        toubi++;
                        if (coin_text!=null){
                            if (toubi>0){
                                coin_text.setText("总投币"+toubi+"个");
                                UpdateCloseTime(toubi*(gametime*60));
                                UpdateSettings(true);
                                RequestUpdate();
                            }
                        }
                    }else{
//                        WorkLog.d(TAG, "handleMessage: data:"+data);
                    }
                    break;
                default:
                    break;

            }
        }
    };

    private void RequestUpdate() {
        OrderManage.AddOrder(getApplication(),1,0);
//        try {
//            JSONObject json = new JSONObject();
//            json.put("createTime",System.currentTimeMillis());
//            json.put("userId", SharedPreferencesUtil.getStringValue(getApplication(), LoginActivity.LoginKey,""));
//            json.put("fee",1);
//            json.put("payType",0);
//            OkGo.post("").tag(this).params("json",json.toString()).execute(new StringCallback() {
//                @Override
//                public void onSuccess(String s, Call call, Response response) {
//
//                }
//
//                @Override
//                public void onError(Call call, Response response, Exception e) {
//                    super.onError(call, response, e);
//                }
//
//                @Override
//                public void onAfter(String s, Exception e) {
//                    super.onAfter(s, e);
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    private UpdateGameSetting gameSettings;
    private void UpdateSettings(boolean hasTime) {
        gameSettings = new UpdateGameSetting();
        gameSettings.setUpdateCode(AppsManage.UPDATE_DATA);
        gameSettings.setHasTime(hasTime);
        gameSettings.setToubi(toubi);
        RxBus.getInstance().post(gameSettings);
    }

    private void UpdateCloseTime(long closeTime){
        WorkLog.i(TAG, "UpdateCloseTime: 获得总共时间"+closeTime);
        if (endTime>=0&& HasEndTime>=0){
            WorkLog.i(TAG, "UpdateCloseTime:  用户剩余消费时间："+endTime);
            WorkLog.i(TAG, "UpdateCloseTime:  用户上次消费总时间："+HasEndTime);
            updateTime+= (HasEndTime-endTime);
            WorkLog.i(TAG, "UpdateCloseTime: updateTime:"+updateTime);
            closeTime = closeTime-updateTime;
            WorkLog.i(TAG, "UpdateCloseTime:  重新计算总消费时间:"+closeTime);
        }
        initCloseTime(closeTime,close_text);
    }
    private long updateTime= 0;
    private long endTime =0;
    private long HasEndTime = 0;
    private Subscription subscription;
    private void initCloseTime(final long closeTime,final TextView close_text) {
        HasEndTime = closeTime;
        unSubscribe();
        if (closeTime<=0){
            subscription.unsubscribe();
            endTime_text.setText("投币即可上机");
            coin_text.setText("总投币0个");
            close_text.setText("等待上机……");
            toubi = 0;
            endTime = 0;
            HasEndTime = 0;
            updateTime = 0;
            try {
                String topPackage = AppsManage.getTopActivity(getApplicationContext());
                if (!TextUtils.isEmpty(topPackage)){
                    if (topPackage.equals(getPackageName())){

                    }else {
                        WorkLog.i(TAG, "initCloseTime: topPackage:"+topPackage);
                        SuUtil.kill(topPackage);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            UpdateSettings(false);
            return;
        }
        subscription = Observable.interval(0, 1, TimeUnit.SECONDS).filter(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                return aLong <= HasEndTime;
            }
        }).map(new Func1<Long, Long>() {
            @Override
            public Long call(Long aLong) {
                return -(aLong - HasEndTime);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        subscription.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        subscription.unsubscribe();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        endTime = aLong;
//                        WorkLog.i(TAG, "onNext: along:"+aLong);
                        if(aLong>120){
                            endTime_text.setText(Html.fromHtml("上机中……\t 剩余时间<font color='#FF0000'><small>"+ UiUtils.getDate(aLong)+"</small></font>"));
//                            endTime_text.setText("上机中…… \t剩余时间:"+ UiUtils.getDate(aLong));
//                            WorkLog.i(TAG, "onNext: getSysteTime:"+UiUtils.getDateTime());
                        }else{
                            endTime_text.setText(+aLong+"秒");//尊敬的用户,
                            endTime_text.setText(Html.fromHtml("您的余额不足,请及时投币!!\t 剩余时间：<font color='#FF0000'><small>"+ aLong+"</small></font>"));
//                            endTime_text.setText("您的余额不足,请及时投币!!\t 剩余时间："+aLong+"秒");//尊敬的用户,
                        }
                        if (aLong == 0 ){
                            subscription.unsubscribe();
                            endTime_text.setText("投币即可上机");
                            coin_text.setText("总投币0个");
                            close_text.setText("等待上机……");
                            toubi = 0;
                            endTime = 0;
                            HasEndTime = 0;
                            updateTime = 0;
                            try {
                                String topPackage = AppsManage.getTopActivity(getApplicationContext());
                                if (!TextUtils.isEmpty(topPackage)){
                                    if (topPackage.equals(getPackageName())){

                                    }else{
                                        WorkLog.i(TAG, "initCloseTime: topPackage:"+topPackage);
//                                        SuUtil.kill(topPackage);
                                        AppsManage.KillApp(getApplicationContext(),topPackage);
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            UpdateSettings(false);
//                            RestartAPPTool.restartAPP(getApplicationContext());
//                            stopSelf();
                        }
                    }
                });

    }

    private void unSubscribe() {
        if (subscription!=null &&!subscription.isUnsubscribed()){
            subscription.unsubscribe();
            subscription= null;
        }
    }

    private boolean isSend = true;
    class SendThread implements Runnable{

        @Override
        public void run() {
            synchronized(USBUitls.mWriteBufferLock){
                while (!Thread.currentThread().isInterrupted()){
                    if (isSend){
                        if (USBUitls.getUsbDeviceConnection(getApplicationContext())!=null) {
//                            WorkLog.i(TAG, "SendThread: getUsbDeviceConnection");
                            try {
                                USBUitls.sendMSG(getApplicationContext());
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            isSend = false;
                        }
                    }
                }
            }
        }
    }

}
