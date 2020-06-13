package com.bytedance.xly.filetransfer.presenter;

import com.bytedance.xly.filetransfer.ISenderPresenter;
import com.bytedance.xly.filetransfer.ISenderViewCallback;
import com.bytedance.xly.filetransfer.model.SenderUDP;

import java.util.List;

/*
 * 包名：      com.bytedance.xly.filetransfer.activity
 * 文件名：      SenderPresenter
 * 创建时间：      2020/6/11 4:26 PM
 *
 */
public class SenderPresenter implements ISenderPresenter {

    private ISenderViewCallback mSenderViewCallback;
    private SenderUDP mSenderUDP;


    public SenderPresenter(ISenderViewCallback senderViewCallback) {
        mSenderViewCallback = senderViewCallback;
        mSenderUDP = new SenderUDP();
    }

    @Override
    public void search() {
        mSenderUDP.setSearchSwitch(true);
        mSenderUDP.searchReceiver(new SenderUDP.SenderSearchListener() {
            @Override
            public void onFindReceiverSuccess(List<String> ip) {
                mSenderViewCallback.onSearchSuccess(ip);
            }

            @Override
            public void onTimeout() {
                mSenderViewCallback.onSearchTimeout();
            }

            @Override
            public void onError(Exception e) {
                mSenderViewCallback.onSearchFailed();
            }
        });
    }

    /**
     * 通知接收方建立连接准备传输
     */
    @Override
    public void notifyReceiverToPrepareTransfer(String receiverIp) {
        mSenderUDP.notifyReceiverToPrepareTransfer(receiverIp, new SenderUDP.SenderNotifyListener() {
            @Override
            public void onConnectedWithReceiver() {
                if (mSenderViewCallback != null){
                    mSenderViewCallback.onConnectedReceiver();
                }
            }
        });
    }

    public void finish() {
        //结束搜索任务
        mSenderUDP.setSearchSwitch(false);
    }
}
