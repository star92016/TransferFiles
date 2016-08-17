package com.application.gritstone.utils;

/**
 * Created by administrator-ypc on 2016/5/30.
 */
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * @author administrator-ypc 判断设备的状态，打开或者关闭设备 设备包括蓝牙、热点、WiFi、流量
 *
 */
public class DeviceStatusOperation {

    // -------------------------------蓝牙设备
    public boolean bluetoothStatus(BluetoothAdapter blueAdapter) { // 判断蓝牙设备的状态，打开或关闭
        if (blueAdapter == null) {
            return false;
        } else {
            if (blueAdapter.isEnabled()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public void bluetoothOpen(BluetoothAdapter blueAdapter) { // 打开蓝牙
        blueAdapter.enable();

    }

    public void bluetoothClose(BluetoothAdapter blueAdapter) { // 关闭蓝牙
        blueAdapter.disable();
    }

    // -------------------------------热点设备

    private boolean isWifiApEnabled(WifiManager wifiManager) {
        return getWifiApState(wifiManager) == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState(WifiManager wifiManager) {
        int tmp;
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");
            tmp = ((Integer) method.invoke(wifiManager));
            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    private enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
    }

    private boolean setWifiApEnabled(boolean enabled, WifiManager wifiManager) {
        try {
            // 热点的配置类
            WifiConfiguration apConfig = new WifiConfiguration();
            // 通过反射调用设置热点
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            // 返回热点打开状态
            return (Boolean) method.invoke(wifiManager, apConfig, enabled);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean wifiApStatus(WifiManager wifiManager) { // 判断热点设备的状态，打开或关闭
        if (isWifiApEnabled(wifiManager) == true) {
            return true;
        } else {
            return false;
        }
    }

    public void wifiApOpen(WifiManager wifiManager) { // 打开热点
        setWifiApEnabled(true, wifiManager);
    }

    public void WifiApClose(WifiManager wifiManager) { // 关闭热点
        setWifiApEnabled(false, wifiManager);
    }

    // -------------------------------wifi设备
    public boolean wifiStatus(WifiManager wifiManager) { // 判断wifi设备的状态，打开或关闭
        if (!wifiManager.isWifiEnabled()) {
            return false;
        } else {
            return true;
        }
    }

    public void wifiOpen(WifiManager wifiManager) { // 打开wifi
        wifiManager.setWifiEnabled(true);
    }

    public void wifiClose(WifiManager wifiManager) { // 关闭wifi
        wifiManager.setWifiEnabled(false);
    }

    // -------------------------------GPRS设备
    // 检测GPRS是否打开
    private boolean gprsIsOpenMethod(ConnectivityManager mCM, String methodName) {
        Class cmClass = mCM.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;

        Boolean isOpen = false;
        try {
            Method method = cmClass.getMethod(methodName, argClasses);

            isOpen = (Boolean) method.invoke(mCM, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isOpen;
    }

    // 开启/关闭GPRS
    private void setGprsEnabled(ConnectivityManager mCM, String methodName,
                                boolean isEnable) {
        Class cmClass = mCM.getClass();
        Class[] argClasses = new Class[1];
        argClasses[0] = boolean.class;

        try {
            Method method = cmClass.getMethod(methodName, argClasses);
            method.invoke(mCM, isEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean GPRSStatus(ConnectivityManager gprsManager) { // 判断GPRS设备的状态，打开或关闭
        if (gprsIsOpenMethod(gprsManager, "getMobileDataEnabled")) {
            return true;
        } else {
            return false;
        }
    }

    public void GPRSOpen(ConnectivityManager gprsManager) { // 打开GPRS
        setGprsEnabled(gprsManager, "setMobileDataEnabled", true);
    }

    public void GPRSClose(ConnectivityManager gprsManager) { // 关闭GPRS
        setGprsEnabled(gprsManager, "setMobileDataEnabled", false);
    }

}
