package com.bytedance.xly.filetransfer.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.adapter.FileSenderAdapter;
import com.bytedance.xly.filetransfer.model.FileReceiver;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.util.FileUtils;
import com.bytedance.xly.util.LogUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileReceiverActivity extends AppCompatActivity {
    private static final String TAG = "FileReceiverActivity";
    FileInfo mCurFileInfo;
    TextView tv_title;
    ProgressBar pb_total;
    TextView tv_value_storage;//值
    TextView tv_unit_storage;//单位
    TextView tv_value_time;
    TextView tv_unit_time;
    private FileSenderAdapter mFileReceiverAdapter;
    long mTotalLen = 0;     //所有总文件的进度
    long mCurOffset = 0;    //每次传送的偏移量
    long mLastUpdateLen = 0; //每个文件传送onProgress() 之前的进度
    String[] mStorageArray = null;

    private
    long mTotalTime = 0;
    long mCurTimeOffset = 0;
    long mLastUpdateTime = 0;
    String[] mTimeArray = null;

    int mHasSendedFileCount = 0;

    public static final int MSG_FILE_RECEIVER_INIT_SUCCESS = 0X4444;
    public static final int MSG_ADD_FILE_INFO = 0X5555;
    public static final int MSG_UPDATE_FILE_INFO = 0X6666;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_FILE_RECEIVER_INIT_SUCCESS){
//                sendMsgToFileSender(mIpPortInfo);
            }else if(msg.what == MSG_ADD_FILE_INFO){
                //ADD FileInfo 到 Adapter
                FileInfo fileInfo = (FileInfo) msg.obj;
//                ToastUtils.show(getContext(), "收到一个任务：" + (fileInfo != null ? fileInfo.getFilePath() : ""));
            }else if(msg.what == MSG_UPDATE_FILE_INFO){
                //ADD FileInfo 到 Adapter
                updateTotalProgressView();
                if(mFileReceiverAdapter != null) mFileReceiverAdapter.setDataAndNotify(TransferUtil.getInstance().getFileInfos());
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
        mFileReceiverAdapter.setDataAndNotify(TransferUtil.getInstance().getFileInfos());

        tv_title = findViewById(R.id.tv_title);
        pb_total = findViewById(R.id.pb_total);
        tv_value_storage = findViewById(R.id.tv_value_storage);
        tv_unit_storage = findViewById(R.id.tv_unit_storage);
        tv_value_time = findViewById(R.id.tv_value_time);;
        tv_unit_time = findViewById(R.id.tv_unit_time);;


        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_file_transfer));

        pb_total.setMax(100);

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
                serverSocket = new ServerSocket(TransferUtil.DEFAULT_SERVER_PORT);
                mHandler.obtainMessage(MSG_FILE_RECEIVER_INIT_SUCCESS).sendToTarget();
                while (!Thread.currentThread().isInterrupted()) {
                    Socket socket = serverSocket.accept();

                    //
                    FileReceiver fileReceiver = new FileReceiver(socket);
                    fileReceiver.setOnReceiveListener(new FileReceiver.OnReceiveListener() {
                        private FileInfo mCurFileInfo;
                        private long mCurOffset = 0;
                        private long mLastUpdateLen = 0;
                        @Override
                        public void onStart() {
//                            handler.obtainMessage(MSG_SHOW_PROGRESS).sendToTarget();
                            mLastUpdateLen = 0;
                            mLastUpdateTime = System.currentTimeMillis();
                        }

                        @Override
                        public void onGetFileInfo(FileInfo fileInfo) {
                            mHandler.obtainMessage(MSG_ADD_FILE_INFO, fileInfo).sendToTarget();
                            LogUtil.d(TAG, "onGetFileInfo: " + fileInfo.getSize() + "B  文件名:" + fileInfo.getName());
                            mCurFileInfo = fileInfo;
//                            AppContext.getAppContext().addReceiverFileInfo(mCurFileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onGetScreenshot(Bitmap bitmap) {
//                            handler.obtainMessage(MSG_SHOW_PROGRESS, bitmap).sendToTarget();
                        }

                        @Override
                        public void onProgress(long progress, long total) {
                            //=====更新进度 流量 时间视图 start ====//
                            mCurOffset = progress - mLastUpdateLen > 0 ? progress - mLastUpdateLen : 0;
                            mTotalLen = mTotalLen + mCurOffset;
                            mLastUpdateLen = progress;

                            mCurTimeOffset = System.currentTimeMillis() - mLastUpdateTime > 0 ? System.currentTimeMillis() - mLastUpdateTime : 0;
                            mTotalTime = mTotalTime + mCurTimeOffset;
                            mLastUpdateTime = System.currentTimeMillis();
                            //=====更新进度 流量 时间视图 end ====//

                            mCurFileInfo.setProcceed(progress);
                            TransferUtil.getInstance().updateFileInfo(mCurFileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onSuccess(FileInfo fileInfo) {

                            //=====更新进度 流量 时间视图 start ====//
                            mHasSendedFileCount++;
                            mTotalLen = mTotalLen + (fileInfo.getSize() - mLastUpdateLen);
                            LogUtil.d(TAG, "onSuccess: " + mHasSendedFileCount + " 已传输总文件大小 = " + mTotalLen  + "  总文件大小:" + TransferUtil.getInstance().getTotalSize() );
                            mLastUpdateLen = 0;
                            mLastUpdateTime = System.currentTimeMillis();
                            //=====更新进度 流量 时间视图 end ====//
                            fileInfo.setProcceed(fileInfo.getSize());
                            fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                            TransferUtil.getInstance().updateFileInfo(fileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }

                        @Override
                        public void onFailure(Throwable t, FileInfo fileInfo) {
                            mHasSendedFileCount++;//统计发送文件

                            fileInfo.setResult(FileInfo.FLAG_FAILURE);
//                            AppContext.getAppContext().updateFileInfo(fileInfo);
                            mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                        }
                    });

//                    mFileReceiver = fileReceiver;
//                    new Thread(fileReceiver).start();
                    TransferUtil.getInstance().getExecutorService().execute(fileReceiver);
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
                }
            }
        }
    }
    private void updateTotalProgressView(){
        //设置传送的总容量大小
        mStorageArray = FileUtils.getFileSizeArrayStr(mTotalLen);
        tv_value_storage.setText(mStorageArray[0]);
        tv_unit_storage.setText(mStorageArray[1]);

        //设置传送的时间情况
        mTimeArray = FileUtils.getTimeByArrayStr(mTotalTime);
        tv_value_time.setText(mTimeArray[0]);
        tv_unit_time.setText(mTimeArray[1]);


        //设置传送的进度条情况
        if(mHasSendedFileCount == TransferUtil.getInstance().getFileInfos().size()){
            pb_total.setProgress(0);
            tv_value_storage.setTextColor(getResources().getColor(R.color.color_yellow));
            tv_value_time.setTextColor(getResources().getColor(R.color.color_yellow));
            return;
        }

        long total = TransferUtil.getInstance().getFileInfos().size();
        int percent = (int)(mTotalLen * 100 /  total);
        pb_total.setProgress(percent);

        if(total  == mTotalLen){
            pb_total.setProgress(0);
            tv_value_storage.setTextColor(getResources().getColor(R.color.color_yellow));
            tv_value_time.setTextColor(getResources().getColor(R.color.color_yellow));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TransferUtil.getInstance().clearFileInfo();
        finish();
    }
}
