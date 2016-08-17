package com.application.gritstone.utils;

/**
 * Created by guorenjie on 2016/6/19.
 */
public class IpToInt {

    public static String intToIp(int paramInt) {  //转换为IP地址格式
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }
}
