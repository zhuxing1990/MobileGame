package com.vunke.mobilegame.usbdevice;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.vunke.mobilegame.utils.WorkLog;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by zhuxi on 2017/7/7.
 */
public class USBUitls {
    private static final String ACTION_USB_PERMISSION ="android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static  byte[] receiveBytes = new  byte[32];
    public static final Object mReadBufferLock = new Object();
    public static final Object mWriteBufferLock = new Object();
    private static final String TAG = "USBUitls";
    private static UsbManager usbManager;
    private static UsbDevice usbDevice;
    private static UsbEndpoint usbEndpointIn;
    private static UsbEndpoint usbEndpointOut;
    private static UsbDeviceConnection usbDeviceConnection;
    private static byte[] MsgData = new byte[] { 0x02, 0x08, 0x10, 0x7F,0x10,0x00,0x03,0x77 };//02 08 10 7F 10 00 03 77
    private static boolean isOpen = false;
    public static void initUSB(Context context){
        usbManager = USBUitls.getUsbManager(context);
        if (usbManager==null){
            WorkLog.i(TAG, "initUSB: get usbManager failed");
            return;
        }
        usbDevice = USBUitls.enumerateDevice(context);
        if (usbDevice==null){
            WorkLog.i(TAG, "initUSB: get usbDevice failed");
            return;
        }
        if(!usbManager.hasPermission(usbDevice)){
            WorkLog.i(TAG, "initUSB: usbManager is not permission");
//            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
//            usbManager.requestPermission(usbDevice,mPermissionIntent);
            return;
        }
        usbInterface = USBUitls.getUsbInterface(context);
        if (usbInterface== null){
            WorkLog.i(TAG, "initUSB: usbInterface is null");
            return;
        }
        usbDeviceConnection = USBUitls.getUsbDeviceConnection(context);
        if (usbDeviceConnection == null){
            WorkLog.i(TAG, "initUSB: get usbDeviceConnection is null");
            return;
        }
        USBUitls.configUsb340(9600,context);
    }

    public static UsbManager getUsbManager(Context context) {
    if (usbManager == null){
        WorkLog.i(TAG, "getUsbManager: usbManager is null,in it usbManager");
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
      }
        WorkLog.i(TAG, "getUsbManager: get usbManager success");
        return usbManager;
    }

    public static UsbDevice getUsbDevice(Context context) {
        if (usbDevice==null) {
            WorkLog.i(TAG, "getUsbDevice: usbDevice is null,in it usbDevice");
             usbDevice = enumerateDevice(context);
        }
        WorkLog.i(TAG, "getUsbDevice: get usbDevice success");
        return usbDevice;
    }
    // 枚举设备函数
    public static UsbDevice enumerateDevice(Context context) {
        getUsbManager(context);
        WorkLog.i(TAG,"开始进行枚举设备!");
        if (usbManager == null) {
            WorkLog.i(TAG,"创建UsbManager失败，请重新启动应用！");
            return null;
        }
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        if (!(deviceList.isEmpty())) {
            // deviceList不为空
            WorkLog.i(TAG,"deviceList is not null!");
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                usbDevice = deviceIterator.next();
                WorkLog.i(TAG, "DeviceInfo: " + usbDevice.getVendorId() + " , "+ usbDevice.getProductId());
                // 保存设备VID和PID
                int   VendorID = usbDevice.getVendorId();
                int ProductID = usbDevice.getProductId();
                // 保存匹配到的设备
                if (VendorID == 6790 && ProductID == 29987) {
                    isOpen = true;
                    // 获取USBDevice
                    WorkLog.i(TAG,"发现待匹配设备:" + usbDevice.getDeviceName()
                            + usbDevice.getVendorId() + ","
                            + usbDevice.getProductId());
                    return usbDevice;
                }
            }
        } else {
            WorkLog.i(TAG,"deviceList is null!");
            WorkLog.i(TAG,"请连接USB设备至手机！");
            return null;
        }
        return null;
    }
    private static UsbInterface usbInterface;

    public static UsbInterface getUsbInterface(Context context) {
        if (usbInterface==null) {
            WorkLog.i(TAG, "getUsbInterface: get usbInterface is null,in it usbInterface");
            usbInterface = getDeviceInterface(context);
        }
//        WorkLog.i(TAG, "getUsbInterface: get usbInterface success");
        return usbInterface;
    }

    // 寻找设备接口
    public static UsbInterface getDeviceInterface(Context context) {
        getUsbDevice(context);
        if (usbDevice != null) {
            WorkLog.i(TAG, "interfaceCounts : " + usbDevice.getInterfaceCount());
            for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
                UsbInterface intf = usbDevice.getInterface(i);
                if (i == 0) {
                    usbInterface = intf;
                    WorkLog.i(TAG,"成功获得设备接口1:" + usbInterface.getId());
                }
                if (i == 1) {
                    usbInterface = intf;
                    WorkLog.i(TAG,"成功获得设备接口2:" + usbInterface.getId());
                }
            }
            return usbInterface;
        } else {
            WorkLog.i(TAG,"设备为空！");
            return usbInterface;
        }
    }

    public static UsbEndpoint getUsbEndpointOut(Context context) {
        if (usbEndpointOut == null){
            WorkLog.i(TAG, "getUsbEndpointOut: get usbEndpointOut is null,init usbEndpointOut");
            initUsbEndpointOut(context);
        }
//        WorkLog.i(TAG, "getUsbEndpointOut:  get usbEndpointOut  success");
        return usbEndpointOut;
    }

    public static UsbEndpoint getUsbEndpointIn(Context context) {
        if (usbEndpointIn == null){
            WorkLog.i(TAG, "getUsbEndpointIn: get usbEndpointIn is null,init getUsbEndpointIn");
            initUsbEndpointIn(context);
        }
//        WorkLog.i(TAG, "getUsbEndpointIn:  get usbEndpointIn  success");
        return usbEndpointIn;
    }
   public static UsbEndpoint initUsbEndpointOut(Context context){
       getUsbInterface(context);
       if (usbInterface!=null){
           WorkLog.i(TAG, "initUsbEndpointOut: get usbEndpointOut is null , in it usbEndpointOut");
           usbEndpointOut = usbInterface.getEndpoint(1);
       }
       WorkLog.i(TAG, "initUsbEndpointOut: get usbEndpointOut success");
       return usbEndpointOut;
   }

    public static UsbEndpoint initUsbEndpointIn(Context context){
        getUsbInterface(context);
        if (usbInterface!=null){
            WorkLog.i(TAG, "initUsbEndpointIn: get usbEndpointIn is null , in it usbEndpointIn");
            usbEndpointIn =usbInterface.getEndpoint(0);
        }
        WorkLog.i(TAG, "initUsbEndpointIn: get usbEndpointIn success");
        return usbEndpointIn;
    }

    public static UsbDeviceConnection getUsbDeviceConnection(Context context) {
        if (usbDeviceConnection == null){
            WorkLog.i(TAG, "getUsbDeviceConnection: get usbDeviceConnection is null ,initUsbDeviceConnection");
            initUsbDeviceConnection(context);
        }
//        WorkLog.i(TAG, "getUsbDeviceConnection: get usbDeviceConnection success");
        return usbDeviceConnection;
    }
    public static UsbDeviceConnection initUsbDeviceConnection(Context context){
        getUsbManager(context);
        getUsbDevice(context);
        if (usbManager!=null&& usbDevice!=null){
            if (usbManager.hasPermission(usbDevice)) {
                WorkLog.i(TAG, "initUsbDeviceConnection: open usbDevice ,initUsbDeviceConnection");
                usbDeviceConnection = usbManager.openDevice(usbDevice);
            }else{
                WorkLog.i(TAG, "initUsbDeviceConnection: get usbManager permission failed");
//                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
//                usbManager.requestPermission(usbDevice,mPermissionIntent);
            }
        }else{
            WorkLog.i(TAG, "initUsbDeviceConnection: get usbDeviceConnection failed  usbManager or usbDevice is null");
        }
        return usbDeviceConnection;
    }

    public static boolean configUsb340(int paramInt,Context context) {
        getUsbDeviceConnection(context);
        if (usbDeviceConnection!=null) {
            byte[] arrayOfByte = new byte[8];
            usbDeviceConnection.controlTransfer(192, 95, 0, 0, arrayOfByte, 8, 1000);
            usbDeviceConnection.controlTransfer(64, 161, 0, 0, null, 0, 1000);
            long l1 = 1532620800 / paramInt;
            for (int i = 3; ; i--) {
                if ((l1 <= 65520L) || (i <= 0)) {
                    long l2 = 65536L - l1;
                    int j = (short) (int) (0xFF00 & l2 | i);
                    int k = (short) (int) (0xFF & l2);
                    usbDeviceConnection.controlTransfer(64, 154, 4882, j, null, 0, 1000);
                    usbDeviceConnection.controlTransfer(64, 154, 3884, k, null, 0, 1000);
                    usbDeviceConnection.controlTransfer(192, 149, 9496, 0, arrayOfByte, 8, 1000);
                    usbDeviceConnection.controlTransfer(64, 154, 1304, 80, null, 0, 1000);
                    usbDeviceConnection.controlTransfer(64, 161, 20511, 55562, null, 0, 1000);
                    usbDeviceConnection.controlTransfer(64, 154, 4882, j, null, 0, 1000);
                    usbDeviceConnection.controlTransfer(64, 154, 3884, k, null, 0, 1000);
                    usbDeviceConnection.controlTransfer(64, 164, 0, 0, null, 0, 1000);
                    return true;
                }
                l1 >>= 3;
            }
        }else{
            return false;
        }
    }
    private static int TIMEOUT = 5000;
    /*发送数据*/
    public static  int send_Message(Context context, byte[] sendBytes){
        getUsbEndpointOut(context);
        int ret = -1;
        if (usbEndpointOut == null){
            WorkLog.i(TAG, "send_Message: failed");
            return ret;
        }
        ret = usbDeviceConnection.bulkTransfer(usbEndpointOut, sendBytes, sendBytes.length, TIMEOUT);
        return ret;

    }
    public static  void sendMSG(Context context) {
        getUsbDeviceConnection(context);
        getUsbInterface(context);
        if (usbDeviceConnection == null){
            WorkLog.i(TAG, "sendMSG: usbDeviceConnection is null");
            return;
        }
        if (usbInterface==null){
            WorkLog.i(TAG, "sendMSG: usbInterface is null");
            return;
        }
        if(usbDeviceConnection.claimInterface(usbInterface, true)){
            //0208107F10000377
            if((USBUitls.send_Message(context,MsgData)) >= 0){
//                WorkLog.i("sendBytes: ","is OK ");
            }else {
                WorkLog.i("Send Data: ","failed");
            }
        }else {
            WorkLog.i("claimInterface: ","failed");
        }
    }
    /**
     * 将byte[]数组转化为String类型
     * @param arg
     *            需要转换的byte[]数组
     * @param length
     *            需要转换的数组长度
     * @return 转换后的String队形
     */
    public static String toHexString(byte[] arg, int length) {
        String result = new String();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result = result
                        + (Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])) + " ";
            }
            return result;
        }
        return "";
    }
    /*接收数据*/
    public static int receive_Message(Context context, byte[] receiveBytes){
        int ret = -1;
        getUsbEndpointIn(context);
        if (usbEndpointIn == null){
            WorkLog.i("receiveBytes: ", "failed");
            return ret;
        }
        ret = usbDeviceConnection.bulkTransfer(usbEndpointIn, receiveBytes, receiveBytes.length, TIMEOUT);
        return ret;
    }
    /*断开已连接的USB设备*/
    public static void disconnect_USB(Context context){
        if (isOpen){
            if (usbDeviceConnection != null&&usbInterface!=null) {
                usbDeviceConnection.releaseInterface(usbInterface);
                usbDeviceConnection.close();
                usbDevice = null;
                usbManager = null;
                usbInterface = null;
                usbDeviceConnection = null;
                usbEndpointIn = null;
                usbEndpointOut = null;
            }
        }
        isOpen = false;
    }
    public static void getEndTime(long time){
        while (time%60 == 0){

        }
    }
}
