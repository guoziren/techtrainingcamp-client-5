package com.bytedance.xly.filetransfer.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.IReceiverViewCallback;
import com.bytedance.xly.filetransfer.presenter.ReceiverPresenter;
import com.bytedance.xly.thumbnail.activity.PhotoActivity;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.SystemInformationUtil;
import com.bytedance.xly.thumbnail.view.RadarScanView;
import com.bytedance.xly.util.TransferUtil;

import java.lang.ref.WeakReference;

public class ReceiveActivity extends AppCompatActivity implements IReceiverViewCallback {
    private static final String TAG = "ReceicerActivity";
    private TextView mTvTop;
    private ReceiverPresenter mReceiverPresenter;

    private static final int TIMEOUT = 0;
    public static final int MSG_TO_FILE_RECEIVER_UI = 1;
    private Handler mHandler ;
    private  static class ReceiveHandler extends Handler{
        private WeakReference<ReceiveActivity> mWeakReference;
        public ReceiveHandler(ReceiveActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            ReceiveActivity activity = mWeakReference.get();
            switch (msg.what){
                case TIMEOUT:
                    activity.mTvTop.setClickable(true);
                    activity.mRadarView.stopScan();
                    activity.mTvTop.setText("继续等待");
                    break;
                case MSG_TO_FILE_RECEIVER_UI:
                    //跳转到文件接收列表UI
                    Intent intent = new Intent(activity, FileReceiverActivity.class);
//                    intent.putExtras(bundle);
                    activity.startActivityForResult(intent, PhotoActivity.REQUEST_RECEIVE_FILE);
//                    activity.finish();
                    break;

            }
        }
    };
    private Toolbar mToolbar;
    private RadarScanView mRadarView;

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
                mRadarView.startScan();
                mTvTop.setClickable(false);
                mTvTop.setText("正在等待发送方");
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        //toolbar
        mToolbar = findViewById(R.id.topbar);
        mToolbar.setTitle("局域网快传-接收方");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTvTop = findViewById(R.id.tv_top_tip);

        TextView tv_ip = findViewById(R.id.tv_ip);
        tv_ip.setText("本机IP地址:" + SystemInformationUtil.getIpAddress(this));

        mRadarView = findViewById(R.id.radarView);
        mHandler = new ReceiveHandler(this);
        mRadarView.startScan();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            LogUtil.d(TAG, "onActivityResult: RESULT_OK");
            if (requestCode == PhotoActivity.REQUEST_RECEIVE_FILE){
                setResult(RESULT_OK);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }
}
