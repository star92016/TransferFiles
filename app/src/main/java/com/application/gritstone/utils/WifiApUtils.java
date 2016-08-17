package com.application.gritstone.utils;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

/**
 * Created by administrator-ypc on 2016/5/30.
 */
public class WifiApUtils {

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
}
