package com.bytedance.xly.util;


import com.bytedance.xly.filetransfer.model.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
 * 包名：      com.bytedance.xly.filetransfer.util
 * 文件名：      TransferUtil
 * 创建时间：      2020/6/11 4:42 PM
 *
 */
public class TransferUtil {
    private static final String TAG = "TransferUtil";
    /**
     * 文件传输监听 默认端口
     */
    public static final int DEFAULT_SERVER_PORT = 8080;

    /**
     * UDP通信服务 默认端口
     */
    public static final int DEFAULT_SERVER_COM_PORT = 8099;
    /**
     * UDP广播端口
     */
    public static final int DEFAULT_SERVER_BROADCAST_PORT = 8181;

    /**
     * 文件发送方 与 文件接收方 通信信息
     */
    public static final String MSG_FILE_RECEIVER_INIT = "MSG_FILE_RECEIVER_INIT";
    public static final String MSG_FILE_RECEIVER_INIT_SUCCESS = "MSG_FILE_RECEIVER_INIT_SUCCESS";
    public static final String MSG_FILE_SENDER_START = "MSG_FILE_SENDER_START";


    private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

    public ExecutorService getExecutorService() {
        return mExecutorService;
    }

    private TransferUtil() {
    }

    private static TransferUtil INSTANCE;

    public static TransferUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (TransferUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransferUtil();
                }
            }
        }
        return INSTANCE;
    }

    private List<FileInfo> mFileInfos = new ArrayList<>();


    public List<FileInfo> getFileInfos() {
        return mFileInfos;
    }

    public void updateFileInfo(FileInfo fileInfo) {
        int index = mFileInfos.indexOf(fileInfo);
        if (index >= 0) {
            FileInfo f = mFileInfos.get(index);
            f.setProcceed(fileInfo.getProcceed());
            f.setResult(fileInfo.getResult());
        }
    }

    public void addFileInfo(FileInfo fileInfo) {
        if (!mFileInfos.contains(fileInfo)) {
            mFileInfos.add(fileInfo);
        }
    }

    public void clearFileInfo() {
        mFileInfos.clear();
    }
    public long getTotalSize(){
        long result = 0;
        for (FileInfo fileInfo : mFileInfos) {
            result += fileInfo.getSize();
        }
        return result;
    }
}
