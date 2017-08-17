package com.vunke.mobilegame.activity;

import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.vunke.mobilegame.service.FloatWindowsService;
import com.vunke.mobilegame.usbdevice.USBUitls;
import com.vunke.mobilegame.utils.WorkLog;

/**
 * Created by zhuxi on 2017/7/7.
 */
public class USBLinsterActivity extends AppCompatActivity {
    private static final String TAG = "USBLinsterActivity";
    private TextView usbconnect_status_text1,usbconnect_status_text2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        initAction(intent.getAction());
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    private void initAction(String action ) {
        if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
            Log.i(TAG, "initAction:  usb is connect");
            Toast.makeText(getApplicationContext(),"USB已连接",Toast.LENGTH_SHORT).show();
            USBUitls.disconnect_USB(getApplicationContext());
            USBUitls.initUSB(getApplicationContext());
//            usb_receiveData();
//            usb_sendData();
        }else if(action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
            Log.i(TAG, "initAction:  usb is disconnect");
            Toast.makeText(getApplicationContext(),"USB已断开",Toast.LENGTH_SHORT).show();
            USBUitls.disconnect_USB(getApplicationContext());
        }else{
            Log.i(TAG, "initAction:  get action:"+action);
        }
        Intent intent = new Intent(getApplicationContext(), FloatWindowsService.class);
        intent.setAction(action);
        startService(intent);
        finish();
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
        if(USBUitls.getUsbDeviceConnection(getApplicationContext()).claimInterface(USBUitls.getDeviceInterface(getApplicationContext()), true)) {
            if (USBUitls.receive_Message(getApplicationContext(), USBUitls.receiveBytes) >= 0) {
                Log.v("receiveBytes: ", "is OK ");
                try {
                    toHexString = USBUitls.toHexString(USBUitls.receiveBytes, 12);
                    WorkLog.i(TAG, "get data:"+toHexString);
                    msg = Message.obtain();
                    msg.what = 0x113;
                    msg.obj = toHexString;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.v("claimInterface: ", "failed");
            }
        }
    }
    private long getMSG= 0;
    private long toubi = 0;
    private android.os.Handler handler = new android.os.Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x113:
                    String data = (String) msg.obj;
                    data= data.replace(" ","");
//                    Toast.makeText(getApplicationContext(),"data:"+data.trim(),Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "handleMessage: data:"+data);
                    if (data.contains("020b2001")){
                        getMSG++;
                        WorkLog.i(TAG,"接收次数"+getMSG);
                    }else if(data.contains("020b2010")){
                        toubi++;
                        WorkLog.i(TAG,"投币次数"+toubi);
                    }else{
                        Toast.makeText(getApplicationContext(),"异常的返回参数",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;

            }
        }
    };
    private boolean isSend = true;
    class SendThread implements Runnable{

        @Override
        public void run() {
            synchronized(USBUitls.mWriteBufferLock){
                while (!Thread.currentThread().isInterrupted()){
                    if (isSend){
                        if (USBUitls.getUsbDeviceConnection(getApplicationContext())!=null) {
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
