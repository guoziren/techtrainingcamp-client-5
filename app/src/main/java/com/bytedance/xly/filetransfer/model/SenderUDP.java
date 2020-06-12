package com.bytedance.xly.filetransfer.model;

import com.bytedance.xly.filetransfer.BaseTransfer;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/*
 * 包名：      com.bytedance.xly.filetransfer.model
 * 文件名：      ReceiverIP
 * 创建时间：      2020/6/11 4:39 PM
 *
 */
public class SenderUDP implements ISenderUDP {
    private static final String TAG = "SenderUDP";
    private DatagramSocket udpSocket;
    private DatagramPacket udpPacket;
    private static final int SOCKETTIMEOUT = 8000;//默认一次搜索的超时时间
    private Runnable mRunnable;
    private String mReceiverIP;
    private List<String> mIPList = new ArrayList<>();
    private Runnable mNotifyRunnable;

    @Override
    public void searchReceiver(SenderSearchListener receiveIPListene) {
        setSenderSearchListener(receiveIPListene);
        Runnable searchRunnable = getSearchRunnable();
        TransferUtil.getInstance().getExecutorService().execute(searchRunnable);

    }

    private Runnable getSearchRunnable() {
        if (mRunnable != null){
            return mRunnable;
        }
        mRunnable = new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, "getSearchRunnable  run: ");
                while (true){
                    if (udpSocket == null){
                        try {
                            //创建socket
                            udpSocket = new DatagramSocket(TransferUtil.DEFAULT_SERVER_BROADCAST_PORT);
                            udpSocket.setSoTimeout(SOCKETTIMEOUT);
                        } catch (SocketException e) {
                            mSenderSearchListener.onError(e);
                            e.printStackTrace();
                            break;
                        }
                    }
                    byte[] data = new byte[256];
                    udpPacket = new DatagramPacket(data, data.length);

                    try {
                        LogUtil.d(TAG, "search: 开始搜索局域网可传设备 监听端口号 "  + TransferUtil.DEFAULT_SERVER_COM_PORT);
                        //阻塞接收
                        udpSocket.receive(udpPacket);
                    } catch (SocketTimeoutException e) {
                      mSenderSearchListener.onTimeout();
                        break;
                    }catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (null != udpPacket.getAddress()) {
                        final String quest_ip = udpPacket.getAddress().toString();
                        int port = udpPacket.getPort();
                        LogUtil.d(TAG, "search: quest_ip " + quest_ip + " port : " + port);
                        //quest_ip前面会有一个/符号，例如/192.168.0.1，这里对他进行截取，截取后就为真正的IP地址 如 192.168.0.1
                        if (!mIPList.contains(quest_ip.substring(1))){
                            mIPList.add(quest_ip.substring(1));
                            mSenderSearchListener.onFindReceiverSuccess(mIPList);
                        }
                    }
                    LogUtil.d(TAG, "search: 收到" + udpPacket.getAddress());
                }
                closeSocket();
                LogUtil.d(TAG, "run: runnable is over");
            }
        };
        return mRunnable;
    }
    private SenderSearchListener mSenderSearchListener;

    public SenderSearchListener getSenderSearchListener() {
        return mSenderSearchListener;
    }

    public void setSenderSearchListener(SenderSearchListener senderSearchListener) {
        mSenderSearchListener = senderSearchListener;
    }

    /**
     * 发消息通知接收方准备接收文件
     */
    public void notifyReceiverToPrepareTransfer(String receiverIP,SenderNotifyListener listener) {
        mSenderNotifyListener = listener;
        mReceiverIP = receiverIP;
        Runnable searchRunnable = getNotifyRunnable();
        TransferUtil.getInstance().getExecutorService().execute(searchRunnable);
    }

    private Runnable getNotifyRunnable() {
        if (mNotifyRunnable != null){
            return mNotifyRunnable;
        }
        mNotifyRunnable = new Runnable(){
            @Override
            public void run() {
                try {
                    startFileSenderServer(mReceiverIP, TransferUtil.DEFAULT_SERVER_COM_PORT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return mNotifyRunnable;

    }
    DatagramSocket mNotifyDatagramSocket;
    /**
     * 开启 文件发送方 通信服务 (必须在子线程执行)
     * @param targetIpAddr
     * @param serverPort
     * @throws Exception
     */
    private void startFileSenderServer(String targetIpAddr, int serverPort) throws Exception{
        LogUtil.d(TAG, "startFileSenderServer: 我是发送方：已发现接收方Ip地址，通知它建立连接以便发送文件 ");
//        Thread.sleep(3*1000);
        // 确保Wifi连接上之后获取得到IP地址
//        int count = 0;
//        while(targetIpAddr.equals(Constant.DEFAULT_UNKOWN_IP) && count < Constant.DEFAULT_TRY_TIME){
//            Thread.sleep(1000);
//            targetIpAddr = WifiMgr.getInstance(getContext()).getIpAddressFromHotspot();
//            Log.i(TAG, "receiver serverIp ----->>>" + targetIpAddr);
//            count ++;
//        }
//
//        // 即使获取到连接的热点wifi的IP地址也是无法连接网络 所以采取此策略
//        count = 0;
//        while(!NetUtils.pingIpAddress(targetIpAddr) && count < Constant.DEFAULT_TRY_TIME){
//            Thread.sleep(500);
//            Log.i(TAG, "try to ping ----->>>" + targetIpAddr + " - " + count );
//            count ++;
//        }

        mNotifyDatagramSocket = new DatagramSocket();
        byte[] receiveData = new byte[1024];
        InetAddress ipAddress = InetAddress.getByName(targetIpAddr);

//        0.发送 即将发送的文件列表 到文件接收方
        sendFileInfoListToFileReceiverWithUdp(serverPort, ipAddress);

        //1.发送 文件接收方 初始化
        byte[] sendData = null;
        sendData = TransferUtil.MSG_FILE_RECEIVER_INIT.getBytes(BaseTransfer.UTF_8);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, serverPort);

        //发送
        mNotifyDatagramSocket.send(sendPacket);
        LogUtil.d(TAG, "Send Msg To FileReceiver######>>>" + TransferUtil.MSG_FILE_RECEIVER_INIT);



        //2.接收 文件接收方 初始化 反馈
        while(true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            LogUtil.d(TAG, "已通知发送方  正在等待它的回应 ");
            mNotifyDatagramSocket.receive(receivePacket);
            String response = new String( receivePacket.getData(), BaseTransfer.UTF_8).trim();
            LogUtil.d(TAG, "收到消息 接收方初始化结果" + response);
            if(response != null && response.equals(TransferUtil.MSG_FILE_RECEIVER_INIT_SUCCESS)){
                // 进入文件发送列表界面 （并且通知文件接收方进入文件接收列表界面）
                //mHandler.obtainMessage(MSG_TO_FILE_SENDER_UI).sendToTarget();
                if (mSenderNotifyListener != null){
                    mSenderNotifyListener.onConnectedWithReceiver();
                }

            }
        }
    }
    /**
     * 发送即将发送的文件列表到文件接收方
     * @param serverPort
     * @param ipAddress
     * @throws IOException
     */
    private void sendFileInfoListToFileReceiverWithUdp(int serverPort, InetAddress ipAddress) throws IOException {
        //1.1将发送的List<FileInfo> 发送给 文件接收方

        List<FileInfo> fileInfoList = TransferUtil.getInstance().getFileInfos();
        //
        for(FileInfo  fileInfo: fileInfoList){
            String fileInfoStr = FileInfo.toJsonStr(fileInfo);
                DatagramPacket sendFileInfoListPacket =
                        new DatagramPacket(fileInfoStr.getBytes(), fileInfoStr.getBytes().length, ipAddress, serverPort);
                try{
                    mNotifyDatagramSocket.send(sendFileInfoListPacket);
                    LogUtil.d(TAG, "sendFileInfoListToFileReceiverWithUdp------>>>" + fileInfoStr + "=== Success!");
                }catch (Exception e){
                    LogUtil.d(TAG, "sendFileInfoListToFileReceiverWithUdp------>>>" + fileInfoStr + "=== Failure!");
                }

        }

    }

    public interface SenderSearchListener {
        void onFindReceiverSuccess(List<String> ipList);
        void onTimeout();
        void onError(Exception e);


    }
    private SenderNotifyListener mSenderNotifyListener;
    public interface SenderNotifyListener {

        void onConnectedWithReceiver();

    }
    private void closeSocket(){
        if (udpSocket != null){
            udpSocket.close();
            udpSocket.disconnect();
            udpSocket = null;
        }
    }
}
