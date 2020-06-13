package com.bytedance.xly.filetransfer.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.adapter.TransmissionSendAdapter;
import com.bytedance.xly.filetransfer.ISenderViewCallback;
import com.bytedance.xly.filetransfer.presenter.SenderPresenter;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.SystemInformationUtil;
import com.bytedance.xly.util.UITool;
import com.bytedance.xly.view.view.RadarScanView;

import java.util.ArrayList;
import java.util.List;

public class SenderActivity extends AppCompatActivity implements ISenderViewCallback {
    private static final String TAG = "SenderActivity";
    private TransmissionSendAdapter mAdapter;
    private List<String> ipList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TextView mBtnWait;
    private RadarScanView mRadarScanView;
    private SenderPresenter mSenderPresenter;

    private static final int SEARCH_SUCCESS = 0;
    private static final int START_FILE_TRANSFER_ACTIVITY = 1;
    private static final int SEARCH_TIMEOUT = 2;

    private List<String> mIPList = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEARCH_SUCCESS:
                    mAdapter.setDataAndNotify(mIPList);
                    break;
                case SEARCH_TIMEOUT:
                    mRadarScanView.stopScan();
                    mBtnWait.setClickable(true);
                    mBtnWait.setTextColor(Color.parseColor("#aaaaaa"));
                    break;
                case START_FILE_TRANSFER_ACTIVITY:
                    Intent intent = new Intent(SenderActivity.this, FileSenderActivity.class);
                    intent.putExtra("serverIp",mServerIp);
                    SenderActivity.this.startActivity(intent);

                    finish();
                    break;
            }
        }
    };
    private String mServerIp;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        init();
        initEvent();
    }

    private void initEvent() {
        mBtnWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnWait.setClickable(false);
                mBtnWait.setTextColor(Color.parseColor("#eeeeee"));
                mRadarScanView.startScan();
                mSenderPresenter.search();

            }
        });
        mAdapter.setOnItemClickListener(new TransmissionSendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String ip) {
                LogUtil.d(TAG, "onItemClick: ip " + ip);
                mServerIp = ip;
                mSenderPresenter.notifyReceiverToPrepareTransfer(ip);
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSenderPresenter != null){
                    mSenderPresenter.finish();
                }
                finish();
            }
        });
    }

    private void init() {
        mToolbar = findViewById(R.id.topbar);
        mToolbar.setTitle("局域网快传-发送方");
        mToolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //雷达
        mRadarScanView = findViewById(R.id.radarView);
        mRadarScanView.startScan();

        TextView tv_ip = findViewById(R.id.textv);
        String ip = SystemInformationUtil.getIpAddress(this);
        if (!TextUtils.isEmpty(ip)){
            tv_ip.setText("本机IP地址：" +ip);
        }

        mBtnWait = findViewById(R.id.btn_wait);
        mBtnWait.setTextColor(Color.parseColor("#eeeeee"));
        mBtnWait.setClickable(false);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        mAdapter = new TransmissionSendAdapter();
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = UITool.dip2px(SenderActivity.this, 4);
                outRect.bottom = UITool.dip2px(SenderActivity.this, 4);
                outRect.right = UITool.dip2px(SenderActivity.this, 4);
                outRect.top = UITool.dip2px(SenderActivity.this, 4);
            }
        });
        mSenderPresenter = new SenderPresenter( this);
        mSenderPresenter.search();
    }

    @Override
    public void onSearchSuccess(List<String> receiverIp) {
        mIPList = receiverIp;
        mHandler.sendEmptyMessage(SEARCH_SUCCESS);
    }

    @Override
    public void onSearchFailed() {

    }

    @Override
    public void onConnectedReceiver() {
        LogUtil.d(TAG, "onConnectedReceiver: 即将启动文件传输界面");

        mHandler.sendEmptyMessage(START_FILE_TRANSFER_ACTIVITY);
    }

    @Override
    public void onSearchTimeout() {
        mHandler.sendEmptyMessage(SEARCH_TIMEOUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSenderPresenter.finish();
        finish();
    }
}
