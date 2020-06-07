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
    private Handler mHandler;
    private DatagramSocket udpSocket;
    private DatagramPacket dataPacket;
    public static final int DEFAULT_PORT = 43708;

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
                        LogUtil.d(TAG, "handleMessage: BROADCAST");
                        broadcast();
                        break;
                }

            }
        };
    }

    private void broadcast() {
        LogUtil.d(TAG, "broadcast: ");
        while (true) {
            try {
                if(udpSocket==null){
                    udpSocket = new DatagramSocket(null);
                    udpSocket.setReuseAddress(true);
                    udpSocket.bind(new InetSocketAddress(DEFAULT_PORT));
                }
                // udpSocket = new DatagramSocket(DEFAULT_PORT);
                dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
                byte[] data = SystemInformationUtil.getLocalIPAddress().getBytes();
                dataPacket.setData(data);
                dataPacket.setLength(data.length);
                dataPacket.setPort(DEFAULT_PORT);
                InetAddress broadcastAddr;
                broadcastAddr = InetAddress.getByName("255.255.255.255");
                dataPacket.setAddress(broadcastAddr);
            } catch (Exception e) {
                LogUtil.e(TAG, "broadcast: e " + e.toString());
            }
            LogUtil.d(TAG, "broadcast: " + "开始广播发送udp请求");
            try {
                udpSocket.send(dataPacket);
                sleep(2000);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



//            udpSocket.close();
            /**计算时间标志*/

//            long et = System.currentTimeMillis();
//            /**8秒后次线程自动销毁*/
//            if ((et - st) > 8000) {
//                show = true;
//                break;
//            }
//            /**tcp返回值后停止发送udp*/
//            Log.i("tag", "show");
//            if (run) {
//                run = false;
//                break;
//            }
        }

    }

    public Handler getHandler() {
        return mHandler;
    }
}
