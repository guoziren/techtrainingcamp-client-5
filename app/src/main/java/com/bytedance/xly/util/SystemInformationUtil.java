package com.bytedance.xly.util;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/*
 * 包名：      com.bytedance.xly.util
 * 文件名：      SystemInformationUtil
 * 创建时间：      2020/5/30 8:25 AM
 *
 */
public class SystemInformationUtil {
    private static final String TAG = "SystemInformationUtil";
    /**
     * 获取本机ip方法
     */
    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.e(TAG, ex.toString());
        }
        return null;
    }
}
