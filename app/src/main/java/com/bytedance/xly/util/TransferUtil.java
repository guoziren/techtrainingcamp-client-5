package com.bytedance.xly.util;


import com.bytedance.xly.filetransfer.model.entity.FileInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    public static final int DEFAULT_SERVER_PORT = 34567;

    /**
     * UDP通信服务 默认端口
     */
    public static final int DEFAULT_SERVER_COM_PORT = 8099;
    /**
     * UDP广播端口
     */
    public static final int DEFAULT_SERVER_BROADCAST_PORT = 8181;
    public static  long begin = 0;
    public static  long end = 0;

    /**
     * 文件发送方 与 文件接收方 通信信息
     */
    public static final String MSG_FILE_RECEIVER_INIT = "MSG_FILE_RECEIVER_INIT";
    public static final String MSG_FILE_RECEIVER_INIT_SUCCESS = "MSG_FILE_RECEIVER_INIT_SUCCESS";
    public static final String MSG_FILE_SENDER_START = "MSG_FILE_SENDER_START";


    private ExecutorService mExecutorService = new ThreadPoolExecutor(1,20,3, TimeUnit.SECONDS,new SynchronousQueue<Runnable>());

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

    private List<FileInfo> mSendFileInfos = new ArrayList<>();
    private List<FileInfo> mReceiveFileInfos = new ArrayList<>();


    public List<FileInfo> getSendFileInfos() {
        return mSendFileInfos;
    }

    public void updateSendFileInfo(FileInfo fileInfo) {
        int index = mSendFileInfos.indexOf(fileInfo);
        if (index >= 0) {
            FileInfo f = mSendFileInfos.get(index);
            f.setProcceed(fileInfo.getProcceed());
            f.setResult(fileInfo.getResult());
        }
    }

    public void addSendFileInfo(FileInfo fileInfo) {
        if (!mSendFileInfos.contains(fileInfo)) {
            mSendFileInfos.add(fileInfo);
        }
    }

    public void clearSendFileInfo() {
        mSendFileInfos.clear();
    }

    public long getReceiveTotalSize(){
        long result = 0;
        for (FileInfo fileInfo : mReceiveFileInfos) {
            result += fileInfo.getSize();
        }
        return result;
    }
    public List<FileInfo> getReceiveFileInfos() {
        return mReceiveFileInfos;
    }

    public void updateReceiveFileInfo(FileInfo fileInfo) {
        int index = mReceiveFileInfos.indexOf(fileInfo);
        if (index >= 0) {
            FileInfo f = mReceiveFileInfos.get(index);
            f.setProcceed(fileInfo.getProcceed());
            f.setResult(fileInfo.getResult());
        }
    }

    public void addReceiveFileInfo(FileInfo fileInfo) {
        if (!mReceiveFileInfos.contains(fileInfo)) {
            mReceiveFileInfos.add(fileInfo);
        }
    }

    public void clearReceiveFileInfo() {
        mReceiveFileInfos.clear();
    }
    public long getSendTotalSize(){
        long result = 0;
        for (FileInfo fileInfo : mSendFileInfos) {
            result += fileInfo.getSize();
        }
        return result;
    }
}
