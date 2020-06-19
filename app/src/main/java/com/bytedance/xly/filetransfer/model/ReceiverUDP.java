package com.bytedance.xly.filetransfer.model;

import android.util.Log;

import com.bytedance.xly.filetransfer.BaseTransfer;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.SystemInformationUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/*
 * 包名：      com.bytedance.xly.filetransfer.model
 * 文件名：      Receiver
 * 创建时间：      2020/6/11 5:40 PM
 *
 */
public class ReceiverUDP {
    private static final String TAG = "ReceiverUDP";
    private Runnable mWaitRunnable;
    private Runnable mBoradcastRunnable;
    private DatagramSocket udpSocket;
    private DatagramPacket dataPacket;

    private static final long INTERVALTIME = 1000;//广播间隔毫秒值
    private static final int MAX_DATA_PACKET_LENGTH = 40;
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];


    /**
     * 等待发送方的1次等待时间
     */
    private static final int WAITTIME = 10000;

    public void waitSender(ReceiverUDPListener listener) {
        mReceiverUDPListener = listener;
        Runnable searchRunnable = getBroadcastRunnable();
        TransferUtil.getInstance().getExecutorService().execute(searchRunnable);
    }

    private Runnable getBroadcastRunnable() {
        if (mBoradcastRunnable != null){
            return mBoradcastRunnable;
        }
        mBoradcastRunnable = new Runnable() {
                @Override
                public void run() {
                    broadcast(WAITTIME);
                }
            };
        return mBoradcastRunnable;
    }
    /*
     *    
     */
    private boolean broadcastSwicht = true;
    private boolean receiveSwicht = true;
    
    
    private void broadcast(long waitTimemills) {
        LogUtil.d(TAG, "broadcast: ");
        long begin = System.currentTimeMillis();
        long current = 0;
        while (broadcastSwicht) {
            current = System.currentTimeMillis();
            if (current - begin >= waitTimemills) {
                LogUtil.d(TAG, "broadcast: 超时");
                mReceiverUDPListener.onTimeout();
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

                    dataPacket.setPort(TransferUtil.DEFAULT_SERVER_BROADCAST_PORT);
                    InetAddress broadcastAddr = InetAddress.getByName("255.255.255.255");
                    dataPacket.setAddress(broadcastAddr);
                }

            } catch (SocketException e) {
                LogUtil.e(TAG, "broadcast: e " + e.toString());
                mReceiverUDPListener.onError(e);
                break;
            } catch (UnknownHostException e) {
                mReceiverUDPListener.onError(e);
                return;
            }
            LogUtil.d(TAG, "broadcast: " + "开始广播自己的ip地址 目的端口号 " + TransferUtil.DEFAULT_SERVER_BROADCAST_PORT);
            try {
                udpSocket.send(dataPacket);
                Thread.sleep(INTERVALTIME);
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
    private ReceiverUDPListener mReceiverUDPListener;

    public void destroy() {
        LogUtil.d(TAG, "destroy: ");
        broadcastSwicht = false;
        receiveSwicht = false;
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
//            udpSocket.disconnect();
            udpSocket = null;
            dataPacket = null;
        }
        if (mDatagramSocket != null && !mDatagramSocket.isClosed()) {
            mDatagramSocket.close();
//            mDatagramSocket.disconnect();
            mDatagramSocket = null;
        }
    }

    public interface ReceiverUDPListener {
        void onTimeout();
        void onError(Exception e);
    }
    private ReceiverNotifyUDPListener mReceiverNotifyUDPListener;
    public interface ReceiverNotifyUDPListener {
        void onReceiveFileUI();
    }

    public void waitNotificationToConnect(ReceiverNotifyUDPListener listener){
        mReceiverNotifyUDPListener = listener;
        Runnable runnable = getWaitNotificationToConnectrRunnable();
        TransferUtil.getInstance().getExecutorService().execute(runnable);
    }




    /**
     * 创建发送UDP消息等待 文件发送方通知建立连接 的Runnable任务
     */
    private Runnable getWaitNotificationToConnectrRunnable(){
        if (mWaitRunnable != null){
            return mWaitRunnable;
        }

        mWaitRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    startFileReceiverServer(TransferUtil.DEFAULT_SERVER_COM_PORT);
                } catch (Exception e) {
                    LogUtil.d(TAG, "run: " + e.toString());
                }
            }
        };
        return mWaitRunnable;
    }


    /**
     * 开启 文件接收方 通信服务 (必须在子线程执行)
     * @param serverPort
     * @throws Exception
     */
    DatagramSocket mDatagramSocket;
    private void startFileReceiverServer(int serverPort) throws Exception{
        LogUtil.d(TAG, "startFileReceiverServer: 开启socket接收 发送方发来的建立连接的消息");
        mDatagramSocket = new DatagramSocket(TransferUtil.DEFAULT_SERVER_COM_PORT);
        byte[] receiveData = new byte[1024];
        byte[] sendData = null;
        while(true) {
            //1.接收 文件发送方的消息
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            mDatagramSocket.receive(receivePacket);
            LogUtil.d(TAG, "startFileReceiverServer: 收到发送方建立连接的消息");
            String msg = new String( receivePacket.getData()).trim();
            InetAddress inetAddress = receivePacket.getAddress();
            int port = receivePacket.getPort();
//            Log.i(TAG, "Get the msg from FileReceiver######>>>" + Constant.MSG_FILE_RECEIVER_INIT);
            if(msg != null && msg.startsWith(TransferUtil.MSG_FILE_RECEIVER_INIT)){
                LogUtil.d(TAG, "Get the msg from FileReceiver######>>>" + TransferUtil.MSG_FILE_RECEIVER_INIT);
                mReceiverNotifyUDPListener.onReceiveFileUI();
                // 进入文件接收列表界面 (文件接收列表界面需要 通知 文件发送方发送 文件开始传输UDP通知)
//                mainHandler.obtainMessage(TransmissionReceivFragment.MSG_TO_FILE_RECEIVER_UI, new IpPortInfo(inetAddress, port)).sendToTarget();

                //2.反馈 文件发送方的消息
                sendData = TransferUtil.MSG_FILE_RECEIVER_INIT_SUCCESS.getBytes(BaseTransfer.UTF_8);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, inetAddress, port);
                mDatagramSocket.send(sendPacket);
            }else{ //接收发送方的 文件列表
                if(msg != null){
                    parseFileInfo(msg);
                }
            }


        }
    }
    /**
     * 解析FileInfo
     * @param msg
     */
    private void parseFileInfo(String msg) {
        FileInfo fileInfo = FileInfo.toObject(msg);
        if(fileInfo != null && fileInfo.getFilePath() != null){
            TransferUtil.getInstance().addReceiveFileInfo(fileInfo);
        }
    }

}
