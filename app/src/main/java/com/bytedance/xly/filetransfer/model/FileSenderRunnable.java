package com.bytedance.xly.filetransfer.model;

import android.content.Context;

import com.bytedance.xly.filetransfer.BaseTransfer;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.TimeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * 包名：      com.bytedance.xly.filetransfer.model
 * 文件名：      FileSender
 * 创建时间：      2020/6/11 9:48 PM
 *
 */
public class FileSenderRunnable extends BaseTransfer implements Runnable {

    private static final String TAG = "FileSender";



    /**
     * 传送文件目标的地址以及端口
     */
    private String mServerIP;
    private int mPort;
    /**
     * 传送的文件信息
     */
    private FileInfo mFileInfo;

    private Socket mSocket;
    /**
     * socket的输出流
     */
    private OutputStream mOutputStream;

    /**
     * 控制现场暂停 恢复
     */
   // private final Object mObject = new Object();

    //private boolean mIsPaused = false;
    /**
     * 判断此任务是否完毕
     */
    private boolean mIsFinished = false;

    /**
     * 停止任务的标识
     */
    private boolean mIsStop = false;

    private OnSendListener mOnSendListener;

    public FileSenderRunnable(String serverIP, int port, FileInfo fileInfo) {
        mServerIP = serverIP;
        mPort = port;
        mFileInfo = fileInfo;
    }


    public void setOnSendListener(OnSendListener onSendListener) {
        mOnSendListener = onSendListener;
    }

    @Override
    public void init() throws Exception {
        mSocket = new Socket(mServerIP,mPort);
        OutputStream os = mSocket.getOutputStream();
        mOutputStream = new BufferedOutputStream(os);
    }

    @Override
    public void parseHeader() throws Exception {
        LogUtil.d(TAG, "parseHeader: ");

        StringBuilder headerBuilder = new StringBuilder();
        String jsonStr = FileInfo.toJsonStr(mFileInfo);
        jsonStr = TYPE_FILE + SPERATOR + jsonStr;
        headerBuilder.append(jsonStr);
        //对于英文是一个字母对应一个字节，中文的情况下对应两个字节。剩余字节数不应该是字节数
        int leftLen = BYTE_SIZE_HEADER - jsonStr.getBytes(UTF_8).length;
        for (int i = 0; i < leftLen; i++) {
            headerBuilder.append(" ");
        }
        byte[] headBytes = headerBuilder.toString().getBytes(UTF_8);

        //写入header
        mOutputStream.write(headBytes);

    }

    @Override
    public void parseBody() throws Exception {
        long size = mFileInfo.getSize();
        LogUtil.d(TAG, "parseBody: size " + size + "B");

        BufferedInputStream bis =  new BufferedInputStream(new FileInputStream(new File(mFileInfo.getFilePath())));
        //记录文件开始写入时间
        long startTime = System.currentTimeMillis();

        byte[] bytes = new byte[BYTE_SIZE_DATA];
        long total = 0;
        int len = 0;
        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while ((len = bis.read(bytes)) != -1){
            //暂停传输的代码,暂不实现该功能
//            synchronized (mObject){
//                if (mIsPaused){
//                    mObject.wait();
//                }
//            }
            if (mIsStop){
                throw new IOException("传输被中断");
            }
            mOutputStream.write(bytes,0,len);
            total = total + len;
            eTime = System.currentTimeMillis();
            //大于下面的数值才进行一次监听,更新进度
            if (eTime - sTime > 150){
                sTime = eTime;
                if (mOnSendListener != null)mOnSendListener.onProgress(total,size);
            }
        }
        //记录文件结束写入时间
        long endTime = System.currentTimeMillis();
        LogUtil.d(TAG, "parseBody: " + TimeUtils.formatTime(endTime - startTime)  + "  write total = " + total / 1000 + "kb");
        //每一次socket连接就是一个通信，如果当前Outputstream不关闭的话，FileReceiver端会阻塞在那里
        bis.close();
        mOutputStream.flush();
        mOutputStream.close();

        if(mOnSendListener != null) mOnSendListener.onSuccess(mFileInfo);
        mIsFinished = true;
    }

    @Override
    public void finish() throws Exception {
        LogUtil.d(TAG, "finish: ");
        if (mOutputStream != null){
            mOutputStream.close();
        }
        if(mSocket != null && mSocket.isConnected()){
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void run() {

        //1.初始化
        if (mOnSendListener != null){
            mOnSendListener.onStart();
        }
        try {
            init();
        } catch (Exception e) {
            if (mOnSendListener != null){
                mOnSendListener.onFailure(e,mFileInfo);
            }
            e.printStackTrace();
        }
        //2.解析头部
        try {
            parseHeader();
        } catch (Exception e) {
            if (mOnSendListener != null){
                mOnSendListener.onFailure(e,mFileInfo);
            }
            e.printStackTrace();
        }
        //3.解析主体
        try {
            parseBody();
        } catch (Exception e) {
            e.printStackTrace();
            if(mOnSendListener != null) mOnSendListener.onFailure(e, mFileInfo);
        }
        //结束
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            if(mOnSendListener != null) mOnSendListener.onFailure(e, mFileInfo);
        }
    }

    /**
     * 文件传送的监听
     */
    public interface OnSendListener {
        void onStart();

        void onProgress(long progress, long total);

        void onSuccess(FileInfo fileInfo);

        void onFailure(Throwable t, FileInfo f);
    }

    /**
     * 暂停传输
     */
//    public void pause(){
//        synchronized (mObject){
//            mIsPaused = true;
//            mObject.notifyAll();
//        }
//    }

    /**
     * 继续传输
     */
//    public void resume(){
//        synchronized (mObject){
//            mIsPaused = false;
//            mObject.notifyAll();
//        }
//    }

    /**
     * 停止当前的发送任务
     */
    public void stop(){
        mIsStop = true;
    }
    /**
     * 文件是否在传送中
     */
    public boolean isRunning(){
        return !mIsFinished;
    }
}
