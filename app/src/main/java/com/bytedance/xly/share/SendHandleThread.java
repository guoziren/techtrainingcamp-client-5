package com.bytedance.xly.share;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.bytedance.xly.activity.FastShareActivity;
import com.bytedance.xly.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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
    }
    public void initHandler(){
        mHandler = new Handler(getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what){
                    case SEARCH:
                        search();
                        break;
                }

            }
        };
    }

    private void search() {
        LogUtil.d(TAG, "search: "  );
        byte[] data = new byte[256];
        try {
            udpSocket = new DatagramSocket(43708);
            udpPacket = new DatagramPacket(data, data.length);

        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        while (true){
            try {
            LogUtil.d(TAG, "search: 开始搜索局域网可传设备"  );
                udpSocket.receive(udpPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != udpPacket.getAddress()) {
                final String quest_ip = udpPacket.getAddress().toString();
                LogUtil.d(TAG, "search: ");
                Message msg = new Message();
                msg.what = FastShareActivity.RECEIVE_DATA;
                //quest_ip前面会有一个/符号，例如/192.168.0.1，这里对他进行截取，截取后就为真正的IP地址 如 192.168.0.1
                msg.obj = quest_ip.substring(1);
                mainHandler.sendMessage(msg);
            }else {

            }
            LogUtil.d(TAG, "search: 收到" + udpPacket.getAddress());
        }


    }

    public Handler getHandler() {
        return mHandler;
    }
}
