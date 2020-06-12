package com.bytedance.xly.filetransfer.presenter;

import com.bytedance.xly.filetransfer.IReceiverViewCallback;
import com.bytedance.xly.filetransfer.model.ReceiverUDP;
import com.bytedance.xly.util.LogUtil;

/*
 * 包名：      com.bytedance.xly.filetransfer.presenter
 * 文件名：      ReceiverPresenter
 * 创建时间：      2020/6/11 5:37 PM
 *
 */
public class ReceiverPresenter {
    private static final String TAG = "ReceiverPresenter";
    private IReceiverViewCallback mIReceiverViewCallback;
    private ReceiverUDP mReceiverUDP;

    public ReceiverPresenter(IReceiverViewCallback IReceiverViewCallback) {
        mIReceiverViewCallback = IReceiverViewCallback;
        mReceiverUDP = new ReceiverUDP();
    }

    /**
     * 发送udp广播，等待发送方
     */
    public void waitSender(){
        mReceiverUDP.waitSender(new ReceiverUDP.ReceiverUDPListener() {
            @Override
            public void onTimeout() {
                mIReceiverViewCallback.timeout();
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
    public void waitNotificationToConnect(){
        LogUtil.d(TAG, "waitNotificationToConnect: ");
        mReceiverUDP.waitNotificationToConnect(new ReceiverUDP.ReceiverNotifyUDPListener() {
            @Override
            public void onReceiveFileUI() {
                LogUtil.d(TAG, "onReceiveFileUI: ");
                mIReceiverViewCallback.onEnterFileReceiveUI();
            }
        });
    }

    public void destroy() {
       // mReceiverUDP.destroy();
    }
}
