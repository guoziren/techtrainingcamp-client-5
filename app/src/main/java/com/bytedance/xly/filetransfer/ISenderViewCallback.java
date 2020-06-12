package com.bytedance.xly.filetransfer;

import java.util.List;

/*
 * 包名：      com.bytedance.xly.filetransfer.activity
 * 文件名：      ISenderViewCallback
 * 创建时间：      2020/6/11 4:30 PM
 *
 */
public interface ISenderViewCallback {
    /**
     * 搜索到接收方
     * @param receiverIp 接收方ip地址列表
     */
    void onSearchSuccess(List<String> receiverIp);

    /**
     * 未发现接收方
     */
    void onSearchFailed();

    /**
     * 和接收方建立连接，打开文件传输界面
     */
    void onConnectedReceiver( );

    /**
     * 1次搜索时间到
     */
    void onSearchTimeout();
}
