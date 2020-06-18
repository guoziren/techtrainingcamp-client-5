package com.bytedance.xly.filetransfer.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.adapter.FileSenderAdapter;
import com.bytedance.xly.filetransfer.model.FileReceiverRunnable;
import com.bytedance.xly.filetransfer.model.FileSenderRunnable;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FileReceiverActivity extends AppCompatActivity {
    private static final String TAG = "FileReceiverActivity";
    TextView tv_title;

    private FileSenderAdapter mFileReceiverAdapter;
    List<FileReceiverRunnable> mFileReceiverRunnables = new ArrayList<>();
    public static final int MSG_FILE_RECEIVER_INIT_SUCCESS = 0X4444;
    public static final int MSG_ADD_FILE_INFO = 0X5555;
    public static final int MSG_UPDATE_FILE_INFO = 0X6666;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_FILE_RECEIVER_INIT_SUCCESS){
//                sendMsgToFileSender(mIpPortInfo);
            }else if(msg.what == MSG_UPDATE_FILE_INFO){
                if(mFileReceiverAdapter != null) mFileReceiverAdapter.setDataAndNotify(TransferUtil.getInstance().getReceiveFileInfos());
            }
        }
    };
    private ServerRunnable mReceiverServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_receiver);

        init();
    }

    private void init() {

        RecyclerView recyclerView = findViewById(R.id.lv_result);
        mFileReceiverAdapter = new FileSenderAdapter();
        recyclerView.setAdapter(mFileReceiverAdapter);
        mFileReceiverAdapter.setDataAndNotify(TransferUtil.getInstance().getReceiveFileInfos());
        LogUtil.d(TAG, "init: 待接收文件数量"  + TransferUtil.getInstance().getReceiveFileInfos().size() + "  待接收文件总大小" + TransferUtil.getInstance().getReceiveTotalSize());

        tv_title = findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_file_receive));

        TextView tv_directory = findViewById(R.id.directory);
        tv_directory.setText(R.string.receive_directory);

        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initServer(); //启动接收服务
    }

    /**
     * 开启文件接收端服务
     */
    private void initServer() {
        mReceiverServer = new ServerRunnable(TransferUtil.DEFAULT_SERVER_PORT);
        new Thread(mReceiverServer).start();

    }
    /**
     * ServerSocket启动线程
     */
    class ServerRunnable implements Runnable {
        ServerSocket serverSocket;
        private int port;


        public ServerRunnable(int port) {
            this.port = port;
        }

        @Override
        public void run() {
          LogUtil.d(TAG, "------>>>接收文件服务已经开启");
            try {
                if (serverSocket == null){
                    serverSocket = new ServerSocket();
                }
                serverSocket.setReuseAddress(true);
                serverSocket.bind(new InetSocketAddress(TransferUtil.DEFAULT_SERVER_PORT));
                mHandler.obtainMessage(MSG_FILE_RECEIVER_INIT_SUCCESS).sendToTarget();

                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();

                    //
                    FileReceiverRunnable fileReceiverRunnable = new FileReceiverRunnable(socket);
                    fileReceiverRunnable.setOnReceiveListener(new FileReceiverRunnable.OnReceiveListener() {
                        private FileInfo mCurFileInfo;
                        private long mCurOffset = 0;
                        private long mLastUpdateLen = 0;
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onGetFileInfo(FileInfo fileInfo) {
                            mHandler.obtainMessage(MSG_ADD_FILE_INFO, fileInfo).sendToTarget();
                            LogUtil.d(TAG, "onGetFileInfo: " + fileInfo.getSize() + "B  文件名:" + fileInfo.getFilePath());
                            mCurFileInfo = fileInfo;
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }


                        @Override
                        public void onProgress(long progress, long total) {
                            mCurFileInfo.setProcceed(progress);
                            TransferUtil.getInstance().updateReceiveFileInfo(mCurFileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onSuccess(FileInfo fileInfo, File destination) {

                            fileInfo.setProcceed(fileInfo.getSize());
                            fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                            TransferUtil.getInstance().updateReceiveFileInfo(fileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                            // 通知相册有新图片
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(destination);
                            intent.setData(uri);
                            FileReceiverActivity.this.sendBroadcast(intent);
                        }

                        @Override
                        public void onFailure(Throwable t, FileInfo fileInfo) {
                            LogUtil.d(TAG, "onFailure: ");
                            fileInfo.setResult(FileInfo.FLAG_FAILURE);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }
                    });
                    mFileReceiverRunnables.add(fileReceiverRunnable);
                    TransferUtil.getInstance().getExecutorService().execute(fileReceiverRunnable);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /**
         * 关闭Socket 通信 (避免端口占用)
         */
        public void close() {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    serverSocket = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (!hasFileSending()){
            super.onBackPressed();
        }else{
            new AlertDialog.Builder(this).setMessage("文件传输中,是否要中断传输")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopFileTransfer();
                            finish();
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
    }
    private void stopFileTransfer() {
        for (FileReceiverRunnable fileSenderRunnable : mFileReceiverRunnables) {
            if (fileSenderRunnable.isRunning()){
                fileSenderRunnable.stop();
            }
        }
    }
    /**
     * 判断是否有文件在传送
     */
    private boolean hasFileSending(){
        for(FileReceiverRunnable fileReceiverRunnable : mFileReceiverRunnables){
            if(fileReceiverRunnable.isRunning()){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
        if (mReceiverServer != null){
            mReceiverServer.close();
        }
        TransferUtil.getInstance().clearReceiveFileInfo();
    }
}
