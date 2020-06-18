package com.bytedance.xly.filetransfer.model;

import android.graphics.Bitmap;

import com.bytedance.xly.filetransfer.BaseTransfer;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.FileUtils;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * 包名：      com.bytedance.xly.filetransfer.model
 * 文件名：      FileReceiver
 * 创建时间：      2020/6/11 10:46 PM
 *
 */
public class FileReceiverRunnable extends BaseTransfer implements Runnable {
    private static final String TAG = "FileReceiver";
    /**
     * Socket的输入输出流
     */
    private Socket mSocket;
    private InputStream mInputStream;

    /**
     * 传送文件的信息
     */
    private FileInfo mFileInfo;

    /**
     * 控制线程暂停 恢复
     */
    private final Object LOCK = new Object();
    boolean mIsPaused = false;
    /**
     * 判断此任务是否完毕
     */
    private boolean mIsFinished = false;

    /**
     * 停止任务的标识
     */
    private boolean mIsStop = false;
    /**
     * 文件接收的监听
     */
    OnReceiveListener mOnReceiveListener;

    public FileReceiverRunnable(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public void setOnReceiveListener(OnReceiveListener mOnReceiveListener) {
        this.mOnReceiveListener = mOnReceiveListener;
    }


    @Override
    public void init() throws Exception {
        if (this.mSocket != null) {
            this.mInputStream = mSocket.getInputStream();
        }
    }

    @Override
    public void parseHeader() throws Exception {
//读取header部分
        byte[] headerBytes = new byte[BYTE_SIZE_HEADER];
        int headTotal = 0;
        int readByte = -1;
        //开始读取header
        while ((readByte = mInputStream.read()) != -1) {
            headerBytes[headTotal] = (byte) readByte;
            headTotal++;
            if (headTotal == headerBytes.length) {
                break;
            }
        }

        //解析header
        String jsonStr = new String(headerBytes, UTF_8);
        String[] strArray = jsonStr.split(SPERATOR);
        jsonStr = strArray[1].trim();
        mFileInfo = FileInfo.toObject(jsonStr);

        if (mOnReceiveListener != null) mOnReceiveListener.onGetFileInfo(mFileInfo);
    }

    @Override
    public void parseBody() throws Exception {
        LogUtil.d(TAG, "parseBody: ");
        //写入文件
        long fileSize = mFileInfo.getSize();
        File destination = FileUtils.gerateLocalFile(mFileInfo.getFilePath());
        OutputStream fos = new FileOutputStream(destination);

        //记录文件开始写入时间
        long startTime = System.currentTimeMillis();

        byte[] bytes = new byte[BYTE_SIZE_DATA];
        long total = 0;
        int len = 0;

        long sTime = System.currentTimeMillis();
        long eTime = 0;
        while ((len = mInputStream.read(bytes)) != -1) {
            synchronized (LOCK) {
                if (mIsPaused) {
                    try {
                        LOCK.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mIsStop){
                throw new IOException("传输被中断");
            }
            fos.write(bytes, 0, len);
            total = total + len;
            eTime = System.currentTimeMillis();
            if (eTime - sTime > 300) { //大于500ms 才进行一次监听
                sTime = eTime;
                if (mOnReceiveListener != null) mOnReceiveListener.onProgress(total, fileSize);
            }


        }
        fos.flush();
        fos.close();
        //记录文件结束写入时间
        long endTime = System.currentTimeMillis();

        LogUtil.d(TAG, "FileReceiver body receive######>>>" + (TimeUtils.formatTime(endTime - startTime)));
        LogUtil.d(TAG, "FileReceiver body receive######>>>" + total);

        LogUtil.d(TAG, "parseBody######>>>end");

        if (mOnReceiveListener != null) mOnReceiveListener.onSuccess(mFileInfo,destination);

    }

    @Override
    public void finish() throws Exception {
         mIsFinished = true;
        if (mInputStream != null) {
            try {
                mInputStream.close();
            } catch (IOException e) {

            }
        }

        if (mSocket != null && mSocket.isConnected()) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 停止线程下载
     */
    public void pause() {
        synchronized (LOCK) {
            mIsPaused = true;
            LOCK.notifyAll();
        }
    }

    /**
     * 重新开始线程下载
     */
    public void resume() {
        synchronized (LOCK) {
            mIsPaused = false;
            LOCK.notifyAll();
        }
    }


    @Override
    public void run() {
        LogUtil.d(TAG, "run: 开始接收文件ing");
        //初始化
        try {
            if (mOnReceiveListener != null) mOnReceiveListener.onStart();
            init();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }

        //解析头部
        try {
            parseHeader();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }


        //解析主体
        try {
            parseBody();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }

        //结束
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnReceiveListener != null) mOnReceiveListener.onFailure(e, mFileInfo);
        }
    }
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

    /**
     * 文件接收的监听
     */
    public interface OnReceiveListener {
        void onStart();

        void onGetFileInfo(FileInfo fileInfo);

        void onProgress(long progress, long total);

        void onSuccess(FileInfo fileInfo, File f);

        void onFailure(Throwable t, FileInfo fileInfo);
    }
}
