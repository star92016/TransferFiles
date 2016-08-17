package com.application.gritstone.utils;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by administrator-ypc on 2016/5/30.
 */
public class BluetoothUtils {

    // 判断蓝牙设备的状态，打开或关闭
    public boolean bluetoothStatus(BluetoothAdapter blueAdapter) {
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

    // 打开蓝牙
    public void bluetoothOpen(BluetoothAdapter blueAdapter) {
        blueAdapter.enable();

    }

    public void bluetoothClose(BluetoothAdapter blueAdapter) { // 关闭蓝牙
        blueAdapter.disable();
    }
}
