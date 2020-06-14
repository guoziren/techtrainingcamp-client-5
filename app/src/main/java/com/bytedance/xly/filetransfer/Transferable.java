package com.bytedance.xly.filetransfer;

/*
 * 包名：      com.bytedance.xly.filetransfer
 * 文件名：      Transferable
 * 创建时间：      2020/6/11 9:42 PM
 *
 */
public interface Transferable {
    void init() throws Exception;
    void parseHeader() throws Exception;
    void parseBody() throws Exception;
    void finish() throws Exception;
}
