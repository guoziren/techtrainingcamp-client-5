package com.bytedance.xly.share;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.SystemInformationUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import androidx.annotation.NonNull;

/*
 * 包名：      com.bytedance.xly.share
 * 文件名：      ReceiveHandlerThread
 * 创建时间：      2020/5/30 1:45 AM
 *
 */
public class ReceiveHandlerThread extends HandlerThread {
    private static final String TAG = "ReceiveHandlerThread";
    public static final int BROADCAST = 1;
    private static final long INTERVALTIME = 1000;//广播间隔毫秒值
    private static final long SENDTIMEOUT = 8000;//广播超时时间
    private Handler mHandler;
    private DatagramSocket udpSocket;
    private DatagramPacket dataPacket;
    public static final int DEFAULT_PORT = 40001;

    private static final int MAX_DATA_PACKET_LENGTH = 40;
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];

    public ReceiveHandlerThread(String name) {
        super(name);
    }
    public void initHandler(){
        mHandler = new Handler(getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case BROADCAST:
                        LogUtil.i(TAG, "handleMessage: BROADCAST");
                        broadcast(SENDTIMEOUT);
                        break;
                }

            }
        };
    }

    private void broadcast(long waitTimemills) {
        LogUtil.i(TAG, "broadcast: ");
        long begin = System.currentTimeMillis();
        long current = 0;
        while (true) {
            current = System.currentTimeMillis();
            if (current - begin >= waitTimemills) {
                LogUtil.i(TAG, "broadcast: 超时");
                break;
            }
            try {
                if (udpSocket == null ) {
                    udpSocket = new DatagramSocket();
//                    udpSocket.setReuseAddress(true);
                }
                // udpSocket = new DatagramSocket(DEFAULT_PORT);
                if (dataPacket == null){
                    dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
                    byte[] data = SystemInformationUtil.getLocalIPAddress().getBytes();
                    dataPacket.setData(data);
                    dataPacket.setLength(data.length);

                    dataPacket.setPort(DEFAULT_PORT);
                    InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
                    dataPacket.setAddress(broadcastAddr);
                }

            } catch (SocketException e) {
                LogUtil.e(TAG, "broadcast: e " + e.toString());
                break;
            } catch (UnknownHostException e) {
                return;
            }
            LogUtil.i(TAG, "broadcast: " + "开始广播自己的ip地址");
            try {
                udpSocket.send(dataPacket);
                sleep(INTERVALTIME);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogUtil.i(TAG, "broadcast: 关闭 断开连接");
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
            udpSocket.disconnect();
            udpSocket = null;
            dataPacket = null;
        }
    }

    public Handler getHandler() {
        return mHandler;
    }
}
