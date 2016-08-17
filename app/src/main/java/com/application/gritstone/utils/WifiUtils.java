package com.application.gritstone.utils;

import android.net.wifi.WifiManager;

/**
 * Created by administrator-ypc on 2016/5/30.
 */
public class WifiUtils {

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
}
