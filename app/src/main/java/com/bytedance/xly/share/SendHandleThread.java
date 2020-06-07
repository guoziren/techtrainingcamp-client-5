package com.bytedance.xly.share;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.bytedance.xly.view.activity.FastShareActivity;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.view.fragment.TransmissionSendFragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import androidx.annotation.NonNull;

/*
 * 包名：      com.bytedance.xly.share
 * 文件名：      SendHandleThread
 * 创建时间：      2020/5/30 1:44 AM
 *
 */
public class SendHandleThread extends HandlerThread {
    private static final String TAG = "SendHandleThread";
    public static final int SEARCH = 1;
    private Handler mHandler;
    private DatagramSocket udpSocket;
    private DatagramPacket udpPacket;
    private Handler mainHandler;

    public void setMainHandler(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    public SendHandleThread(String name) {
        super(name);
        byte[] data = new byte[256];
        udpPacket = new DatagramPacket(data, data.length);
    }
    public void initHandler(){
        mHandler = new Handler(getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case SEARCH:
                        search(8000);
                        break;
                }

            }
        };
    }

    private void search(int millSecond) {
        LogUtil.d(TAG, "search: "  );
        mainHandler.sendEmptyMessage(TransmissionSendFragment.BEGIN_SEARCH);
        while (true){
            if (udpSocket == null){
                try {
                    udpSocket = new DatagramSocket(ReceiveHandlerThread.DEFAULT_PORT);
                    udpSocket.setSoTimeout(millSecond);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
            try {
                LogUtil.d(TAG, "search: 开始搜索局域网可传设备"  );
                udpSocket.receive(udpPacket);
            } catch (SocketTimeoutException e) {
               // e.printStackTrace();
                Message msg = Message.obtain();
                msg.what = TransmissionSendFragment.SOCKETTIMEOUT;
                mainHandler.sendMessage(msg);
                break;
            }catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (null != udpPacket.getAddress()) {
                final String quest_ip = udpPacket.getAddress().toString();
                int port = udpPacket.getPort();
                LogUtil.d(TAG, "search: quest_ip " + quest_ip + " port : " + port);
                Message msg = new Message();
                msg.what = TransmissionSendFragment.RECEIVE_DATA;
                //quest_ip前面会有一个/符号，例如/192.168.0.1，这里对他进行截取，截取后就为真正的IP地址 如 192.168.0.1
                msg.obj = quest_ip.substring(1);
                mainHandler.sendMessage(msg);
            }
            LogUtil.d(TAG, "search: 收到" + udpPacket.getAddress());
        }
        if (udpSocket != null){
            udpSocket.close();
            udpSocket.disconnect();
            udpSocket = null;
        }
        mainHandler.sendEmptyMessage(TransmissionSendFragment.END_SEARCH);
        LogUtil.e(TAG, "search: + 超时" );
    }

    public Handler getHandler() {
        return mHandler;
    }
}
