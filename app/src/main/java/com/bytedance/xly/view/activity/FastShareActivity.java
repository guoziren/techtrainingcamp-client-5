package com.bytedance.xly.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bytedance.xly.R;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.SocketManager;
import com.bytedance.xly.util.ToastUtil;
import com.bytedance.xly.view.fragment.AlbumFragment;
import com.bytedance.xly.view.fragment.TransmissionReceivFragment;
import com.bytedance.xly.view.fragment.TransmissionSendFragment;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import static com.bytedance.xly.view.fragment.TransmissionReceivFragment.TCP_PORT;

public class FastShareActivity extends AppCompatActivity implements TransmissionSendFragment.OnListFragmentInteractionListener, TransmissionReceivFragment.OnListFragmentInteractionListener {
    public static final int UPDATE_PROGRESSDIALOG = 1;
    public static final int RECEIVE_PROGRESSDIALOG_UPDATE = 2;
    public static final int SHOW_RECEIVEDIALOG = 0;
    public static final int RECEIVE_FINISH = 3;
    private static final String TAG = "FastShareActivity";
    private String mMode;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_PROGRESSDIALOG:
                    int progress = msg.arg1;
                    progressDialog.setProgress(progress);
                    break;
                case RECEIVE_PROGRESSDIALOG_UPDATE:
                    int receive_progress = msg.arg1;
                    recevieProgressDialog.setProgress(receive_progress);
                    break;
                case SHOW_RECEIVEDIALOG:
                    showRecieveProgressDialog();
                    break;
                case RECEIVE_FINISH:
                    if(recevieProgressDialog != null && recevieProgressDialog.isShowing()){
                        recevieProgressDialog.dismiss();
                    }
                    ToastUtil.showToast(FastShareActivity.this, Toast.LENGTH_LONG,"接收完成");
                    break;

            }
        }
    };
    private ServerSocket server;
    private String mTransFileName;
    private SocketManager socketManager;
    private String mTransFileType;
    private ProgressDialog recevieProgressDialog;
    private ProgressDialog progressDialog;
    private ArrayList<String> mPaths;
    private String mServerip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_share);

        initData();
        initView();

    //    initEvent();
    }

    private void initData() {
        mMode = getIntent().getStringExtra("mode");
        mPaths = getIntent().getStringArrayListExtra(AlbumFragment.PATHS);
    }

    private void initEvent() {


    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        mRecyclerView = findViewById(R.id.recyclerView);

       FragmentManager fragmentManager = getSupportFragmentManager();
       FragmentTransaction transaction = fragmentManager.beginTransaction();
       Fragment sendFragment = fragmentManager.findFragmentByTag("send");
       Fragment receiveFragment = fragmentManager.findFragmentByTag("receive");
       if (sendFragment == null){
           sendFragment = new TransmissionSendFragment();
       }
        if (receiveFragment == null){
            receiveFragment = new TransmissionReceivFragment();
        }
        if (!TextUtils.isEmpty(mMode) && mMode.equals("receive")){
            transaction.replace(R.id.framLayout,receiveFragment,"receive");
            startTcpServer();
        }else{
            transaction.replace(R.id.framLayout,sendFragment,"send");
        }
       transaction.commit();

        socketManager = new SocketManager();
        socketManager.setMainHandler(mHandler);
    }

    private void startTcpServer() {
        LogUtil.d(TAG, "文件接收端 startTcpServer: ");
        Thread listener = new Thread(new Runnable() {
            @Override
            public void run() {
                // 绑定端口


                try {
                    server = new ServerSocket(TCP_PORT);// 初始化server
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.d(TAG, TCP_PORT + " 端口被占用");
                    return;
                }


                if (server != null) { // 如果server不空
                    socketManager.setServerSocket(server);// 初始化socketManager
                    socketManager.setMainHandler(mHandler);
                    while (true) { // 接收文件，死循环
//							if (!out_recieve){
//								out_recieve=true;
//								break;
//							}
                        LogUtil.d(TAG, "run: 监听端口号 " + TCP_PORT);
                        socketManager.ReceiveFile();// 定义一个字符串response

                        // Message.obtain(handler3, 0, response).sendToTarget();
                    }
                } else {
                    Message.obtain(mHandler, 1, "未能绑定端口").sendToTarget();
                }
            }
        });
        listener.start();
    }

    @Override
    public void onListFragmentInteraction(String serverIp) {
        mServerip = serverIp;
        showProgressDialog();// 显示进度条
        LogUtil.d(TAG, "onListFragmentInteraction: serverIp " + serverIp);
        startTransferFile(serverIp);
    }

    /**
     * 开始发送文件
     * @param serverIp
     */
    private void startTransferFile(String serverIp) {
        if (mPaths == null || mPaths.size() == 0){
            return;
        }
        LogUtil.d(TAG, "startTransferFile: ");
        String response = socketManager.sendFile( mPaths.get(0), serverIp, TransmissionReceivFragment.TCP_PORT);
    }

    /**
     * 进度条对话框
     * 显示发送当前进度
     */
    protected void showProgressDialog() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("正在发送给" + mServerip+"  "+ TCP_PORT);
        // 设置进度条样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置进度条最大值
        progressDialog.setMax(100);
        // 完成按钮
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog 里的 点击事件
                //Toast.makeText(getApplicationContext(), "发送完成", 0).show();
                progressDialog.dismiss();

            }
        });
        // 取消按钮
        // progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new
        // DialogInterface.OnClickListener() {
        //
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // // dialog 里的 点击事件
        // progressDialog.dismiss();
        // }
        // });
        //
      //  if (!FilesTransActivityContent.mContent.isFinishing()) {
            progressDialog.show();
       // }


    }

    //显示接收进度
    protected void showRecieveProgressDialog() {

        recevieProgressDialog = new ProgressDialog(this);
//        recevieProgressDialog.setTitle("正在接收   "+File_Name+"...");
        recevieProgressDialog.setTitle("正在接收   ...");
        // 设置进度条样式
        recevieProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置进度条最大值
        recevieProgressDialog.setMax(100);
        // 完成按钮
        recevieProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog 里的 点击事件
                //Toast.makeText(getApplicationContext(), "接受完成", 0).show();
                recevieProgressDialog.dismiss();
            }
        });
//        if (!FilesTransActivityContent.mContent.isFinishing()) {
            recevieProgressDialog.show();
//        }

    }
}
