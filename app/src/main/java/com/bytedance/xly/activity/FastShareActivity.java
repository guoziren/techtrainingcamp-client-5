package com.bytedance.xly.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.bytedance.xly.R;
import com.bytedance.xly.adapter.LocalNetListAdapter;
import com.bytedance.xly.share.SendHandleThread;

public class FastShare extends AppCompatActivity {

    private LocalNetListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_share);
        initView();
        SendHandleThread sendHandleThread = new SendHandleThread("search");
        Handler sendHandleThreadHandler = sendHandleThread.getHandler();

        sendHandleThreadHandler.sendMessage();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new LocalNetListAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
}
