package com.bytedance.xly.filetransfer;

/*
 * 包名：      com.bytedance.xly.filetransfer
 * 文件名：      BaseTransfer
 * 创建时间：      2020/6/11 9:44 PM
 *
 */
public abstract class BaseTransfer implements Transferable {
    /**
     * 头部分割字符
     */
    public static final String SPERATOR = "::";

    public static final int BYTE_SIZE_HEADER = 1024 ;
    public static final int BYTE_SIZE_DATA = 1024 * 8;


    /**
     * 传输类型
     */
    public static final int TYPE_FILE = 1;//文件类型
    public static final int TYPE_MSG = 2;//消息类型

    /**
     * 传输字节类型
     */
    public static final String UTF_8 = "UTF-8";
}
