package com.bytedance.xly.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.bytedance.xly.R;
import com.bytedance.xly.adapter.LocalNetListAdapter;
import com.bytedance.xly.share.ReceiveHandlerThread;
import com.bytedance.xly.share.SendHandleThread;
import com.bytedance.xly.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class FastShareActivity extends AppCompatActivity {
    private static final String TAG = "FastShareActivity";
    private LocalNetListAdapter mAdapter;
    private List<String> ipList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public static final int RECEIVE_DATA = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RECEIVE_DATA:
                    LogUtil.d(TAG, "handleMessage: RECEIVE_DATA");
                    String ip = (String) msg.obj;
                    ipList.add(ip);
                    mAdapter.setData(ipList);
                    break;
            }
        }
    };
    private Button mBtn_receive;
    private Button mBtn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_share);
        initView();


        initEvent();
    }

    private void initEvent() {
        mBtn_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiveHandlerThread receiveHandlerThread = new ReceiveHandlerThread("receive");
                receiveHandlerThread.start();
                receiveHandlerThread.initHandler();

                Handler receiveHandler = receiveHandlerThread.getHandler();
                Message msg = Message.obtain();
                msg.what = ReceiveHandlerThread.BROADCAST;
                receiveHandler.sendMessage(msg);
            }
        });
        mBtn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendHandleThread sendHandleThread = new SendHandleThread("search");
                sendHandleThread.start();
                sendHandleThread.initHandler();
                Handler searchHandler = sendHandleThread.getHandler();
                sendHandleThread.setMainHandler(mHandler);
                Message msg = Message.obtain();
                msg.what = SendHandleThread.SEARCH;
                searchHandler.sendMessage(msg);
            }
        });
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mBtn_receive = findViewById(R.id.btn_share);
        mBtn_send = findViewById(R.id.btn_send);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new LocalNetListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

}
