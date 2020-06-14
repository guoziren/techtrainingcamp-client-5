package com.bytedance.xly.filetransfer;

/*
 * 包名：      com.bytedance.xly.filetransfer
 * 文件名：      IReceiverViewCallback
 * 创建时间：      2020/6/11 5:37 PM
 *
 */
public interface IReceiverViewCallback {
    void timeout();
    void onEnterFileReceiveUI();
}
