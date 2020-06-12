package com.bytedance.xly.filetransfer.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.adapter.FileSenderAdapter;
import com.bytedance.xly.filetransfer.model.FileSender;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.filetransfer.presenter.FileSenderPresenter;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.util.FileUtils;
import com.bytedance.xly.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FileSenderActivity extends AppCompatActivity {
    private static final String TAG = "FileSenderActivity";
    TextView tv_title;
    ProgressBar pb_total;
    private FileSenderPresenter mPresenter;
    TextView tv_value_storage;
    TextView tv_unit_storage;
    TextView tv_value_time;
    TextView tv_unit_time;
    private String[] mStorageArray;
    private long mTotalTime;
    private String[] mTimeArray;
    private String mServerIp;

    long mCurTimeOffset = 0;
    long mLastUpdateTime = 0;
    long mTotalLen = 0;     //所有总文件的进度
    long mCurOffset = 0;    //每次传送的偏移量
    long mLastUpdateLen = 0; //每个文件传送onProgress() 之前的进度

    int mHasSendedFileCount = 0;
    public static final int MSG_UPDATE_FILE_INFO = 0X6666;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //TODO 未完成 handler实现细节以及封装
            if(msg.what == MSG_UPDATE_FILE_INFO){
                updateTotalProgressView();

                if(mFileSenderAdapter != null) mFileSenderAdapter.setDataAndNotify(TransferUtil.getInstance().getFileInfos());
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
        mFileSenderAdapter = new FileSenderAdapter();
        recyclerView.setAdapter(mFileSenderAdapter);
        mFileSenderAdapter.setDataAndNotify(TransferUtil.getInstance().getFileInfos());

        tv_title = findViewById(R.id.tv_title);
        pb_total = findViewById(R.id.pb_total);
        tv_value_storage = findViewById(R.id.tv_value_storage);
         tv_unit_storage = findViewById(R.id.tv_unit_storage);
        tv_value_time = findViewById(R.id.tv_value_time);;
        tv_unit_time = findViewById(R.id.tv_unit_time);;


        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getResources().getString(R.string.title_file_transfer));

        pb_total.setMax(100);

        mPresenter = new FileSenderPresenter();
        initSendServer(TransferUtil.getInstance().getFileInfos(),mServerIp);
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
    /**
     * 开始传送文件
     * @param fileInfoList
     * @param serverIp
     */
    private void initSendServer(List<FileInfo> fileInfoList, String serverIp) {

        for(FileInfo fileInfo : fileInfoList){

            FileSender fileSender = new FileSender( mServerIp, TransferUtil.DEFAULT_SERVER_PORT,fileInfo);
            fileSender.setOnSendListener(new FileSender.OnSendListener() {
                @Override
                public void onStart() {
                    mLastUpdateLen = 0;
                    mLastUpdateTime = System.currentTimeMillis();

                }

                @Override
                public void onProgress(long progress, long total) {
                    //TODO 更新
                    //=====更新进度 流量 时间视图 start ====//
                    mCurOffset = progress - mLastUpdateLen > 0 ? progress - mLastUpdateLen : 0;
                    mTotalLen = mTotalLen + mCurOffset;
                    mLastUpdateLen = progress;

                    mCurTimeOffset = System.currentTimeMillis() - mLastUpdateTime > 0 ? System.currentTimeMillis() - mLastUpdateTime : 0;
                    mTotalTime = mTotalTime + mCurTimeOffset;
                    mLastUpdateTime = System.currentTimeMillis();
                    //=====更新进度 流量 时间视图 end ====//

                    //更新文件传送进度的ＵＩ
                    fileInfo.setProcceed(progress);

                    TransferUtil.getInstance().updateFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }

                @Override
                public void onSuccess(FileInfo fileInfo) {
                    //=====更新进度 流量 时间视图 start ====//
                    mHasSendedFileCount ++;

                    mTotalLen = mTotalLen + (fileInfo.getSize() - mLastUpdateLen);
                    mLastUpdateLen = 0;
                    mLastUpdateTime = System.currentTimeMillis();
                    //=====更新进度 流量 时间视图 end ====//

                    System.out.println(Thread.currentThread().getName());
                    //TODO 成功
                    fileInfo.setResult(FileInfo.FLAG_SUCCESS);
                    TransferUtil.getInstance().updateFileInfo(fileInfo);
                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }

                @Override
                public void onFailure(Throwable t, FileInfo fileInfo) {
                    mHasSendedFileCount ++;//统计发送文件
                    //TODO 失败
                    fileInfo.setResult(FileInfo.FLAG_FAILURE);
                    TransferUtil.getInstance().updateFileInfo(fileInfo);

                    mHandler.sendEmptyMessage(MSG_UPDATE_FILE_INFO);
                }
            });

            mFileSenderList.add(fileSender);
            TransferUtil.getInstance().getExecutorService().execute(fileSender);
        }
    }
    List<FileSender> mFileSenderList = new ArrayList<FileSender>();
    /**
     * 判断是否有文件在传送
     */
    private boolean hasFileSending(){
        for(FileSender fileSender : mFileSenderList){
            if(fileSender.isRunning()){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!hasFileSending()){
           finish();
           TransferUtil.getInstance().clearFileInfo();
        }
    }
}
