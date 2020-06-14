package com.bytedance.xly.filetransfer.model;

/*
 * 包名：      com.bytedance.xly.filetransfer.model
 * 文件名：      IReceiverIP
 * 创建时间：      2020/6/11 4:39 PM
 *
 */
public interface ISenderUDP {
    void searchReceiver(SenderUDP.SenderSearchListener receiveIPListene);
}
