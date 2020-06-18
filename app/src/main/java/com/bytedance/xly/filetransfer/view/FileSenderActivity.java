package com.bytedance.xly.filetransfer.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.adapter.FileSenderAdapter;
import com.bytedance.xly.filetransfer.model.FileSenderRunnable;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.filetransfer.presenter.FileSenderPresenter;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FileSenderActivity extends AppCompatActivity {
    private static final String TAG = "FileSenderActivity";
    TextView tv_title;
    private FileSenderPresenter mPresenter;

    private String mServerIp;


    public static final int MSG_UPDATE_FILE_INFO = 0X6666;

    private  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what == MSG_UPDATE_FILE_INFO){
                if(mFileSenderAdapter != null)
                    mFileSenderAdapter.setDataAndNotify(TransferUtil.getInstance().getSendFileInfos());
            }
        }
    };
    private FileSenderAdapter mFileSenderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_sender);
        init();
    }

    private void init() {
        mServerIp = getIntent().getStringExtra("serverIp");
        LogUtil.d(TAG, "init: mServerIp " + mServerIp);
        RecyclerView recyclerView = findViewById(R.id.lv_result);
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFileSenderAdapter = new FileSenderAdapter();
        recyclerView.setAdapter(mFileSenderAdapter);
        mFileSenderAdapter.setDataAndNotify(TransferUtil.getInstance().getSendFileInfos());
        LogUtil.d(TAG, "init: 待发送文件数量"  + TransferUtil.getInstance().getSendFileInfos().size() + "  待接收文件总大小" + TransferUtil.getInstance().getSendTotalSize());
        tv_title = findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_file_sender));
        mPresenter = new FileSenderPresenter();
        initSendServer(TransferUtil.getInstance().getSendFileInfos(),mServerIp);
    }


    /**
     * 开始传送文件
     * @param fileInfoList
     * @param serverIp
     */
    private void initSendServer(List<FileInfo> fileInfoList, String serverIp) {

        for(FileInfo fileInfo : fileInfoList){

            FileSenderRunnable fileSenderRunnable = new FileSenderRunnable( serverIp, TransferUtil.DEFAULT_SERVER_PORT,fileInfo);
            fileSenderRunnable.setOnSendListener(new FileSenderRunnable.OnSendListener() {
                @Override
                public void onStart() {


                }

                @Override
                public void onProgress(long progress, long total) {
                    //更新文件传送进度的ＵＩ
                    fileInfo.setProcceed(progress);
                    TransferUtil.getInstance().updateSendFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }

                @Override
                public void onSuccess(FileInfo fileInfo) {
                    //TODO 成功
                    fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                    TransferUtil.getInstance().updateSendFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }

                @Override
                public void onFailure(Throwable t, FileInfo fileInfo) {
                    LogUtil.d(TAG, "onFailure: ");

                    fileInfo.setResult(FileInfo.FLAG_FAILURE);
                    TransferUtil.getInstance().updateSendFileInfo(fileInfo);

                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }
            });

            mFileSenderRunnableList.add(fileSenderRunnable);
            TransferUtil.getInstance().getExecutorService().execute(fileSenderRunnable);
        }
    }
    List<FileSenderRunnable> mFileSenderRunnableList = new ArrayList<FileSenderRunnable>();
    /**
     * 判断是否有文件在传送
     */
    private boolean hasFileSending(){
        for(FileSenderRunnable fileSenderRunnable : mFileSenderRunnableList){
            if(fileSenderRunnable.isRunning()){
                return true;
            }
        }
        return false;
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
        for (FileSenderRunnable fileSenderRunnable : mFileSenderRunnableList) {
            if (fileSenderRunnable.isRunning()){
                fileSenderRunnable.stop();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TransferUtil.getInstance().clearSendFileInfo();
    }
}
