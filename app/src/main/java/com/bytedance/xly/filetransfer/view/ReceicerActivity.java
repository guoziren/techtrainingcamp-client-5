package com.bytedance.xly.filetransfer.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.IReceiverViewCallback;
import com.bytedance.xly.filetransfer.presenter.ReceiverPresenter;
import com.bytedance.xly.util.LogUtil;

public class ReceicerActivity extends AppCompatActivity implements IReceiverViewCallback {
    private static final String TAG = "ReceicerActivity";
    private TextView mTvTop;
    private ReceiverPresenter mReceiverPresenter;

    private static final int TIMEOUT = 0;
    public static final int MSG_TO_FILE_RECEIVER_UI = 1;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case TIMEOUT:
                    mTvTop.setClickable(true);
                    mTvTop.setText("继续等待");
                    break;
                case MSG_TO_FILE_RECEIVER_UI:
                    //跳转到文件接收列表UI
                    Intent intent = new Intent(ReceicerActivity.this, FileReceiverActivity.class);
//                    intent.putExtras(bundle);
                    ReceicerActivity.this.startActivity(intent);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receicer);
        init();
        initEvent();
    }

    private void initEvent() {
        mTvTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReceiverPresenter.waitSender();
                mTvTop.setClickable(false);
                mTvTop.setText("正在等待发送方");
            }
        });
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.topbar);
        toolbar.setTitle("局域网快传");

        setSupportActionBar(toolbar);
        mTvTop = findViewById(R.id.tv_top_tip);
        mReceiverPresenter = new ReceiverPresenter(this);
        //广播ip
        mReceiverPresenter.waitSender();
        //等待接收方通知
        mReceiverPresenter.waitNotificationToConnect();
    }

    @Override
    public void timeout() {
        LogUtil.d(TAG, "timeout: ");
       mHandler.sendEmptyMessage(TIMEOUT);
    }

    @Override
    public void onEnterFileReceiveUI() {
        LogUtil.d(TAG, "onEnterFileReceiveUI: ");
        mHandler.sendEmptyMessage(MSG_TO_FILE_RECEIVER_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
        mReceiverPresenter.destroy();
    }
}
