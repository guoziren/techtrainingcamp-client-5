package com.bytedance.xly.filetransfer;

/*
 * 包名：      com.bytedance.xly.filetransfer
 * 文件名：      ISenderPresenter
 * 创建时间：      2020/6/11 4:26 PM
 *
 */
public interface ISenderPresenter {
    /**
     * 搜索接收方
     */
    void search();

    /**
     * 通知接收方建立连接准备传输
     * @receiverIp 接收端IP地址
     */
    void notifyReceiverToPrepareTransfer(String receiverIp);
}
