package com.bytedance.xly.thumbnail.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.view.ReceiveActivity;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.ToastUtil;
import com.bytedance.xly.thumbnail.fragment.AlbumFragment;
import com.bytedance.xly.util.TransferUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

/*
 * 包名：      com.bytedance.xly.activity
 * 文件名：      PhotoActivity
 * 创建时间：      2020/5/31 10:17 PM
 *
 */
public class PhotoActivity extends AppCompatActivity {
    private static final String TAG = "PhotoActivity";
    private static final String PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int READ_SD_REQUEST_CODE = 355;
    public static final int REQUEST_RECEIVE_FILE = 289;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_album);
        super.onCreate(savedInstanceState);
//        initView();
        requestPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_receive:
                Intent intent = new Intent(this, ReceiveActivity.class);
                startActivityForResult(intent,REQUEST_RECEIVE_FILE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private AlbumFragment albumFragment;

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        albumFragment = (AlbumFragment) getSupportFragmentManager().findFragmentByTag("album");
        if (albumFragment == null) {
            albumFragment = new com.bytedance.xly.thumbnail.fragment.AlbumFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flParent, albumFragment);
        ft.commit();
    }

    public void onChooseModeChange(boolean isChoose) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 监听主页返回键
     */
    @Override
    public void onBackPressed() {
        if (albumFragment.isChooseMode) {
            albumFragment.cancelChoose();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 申请读文件权限
     */
    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, PERMISSION) != PackageManager.PERMISSION_GRANTED) {
          //  LogUtil.d(TAG, "requestPermission: PERMISSION_denied");
            //检查权限是否被授予
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
                LogUtil.d(TAG, "requestPermission: shouldShowRequestPermissionRationale");
                new AlertDialog.Builder(this)
                        .setMessage("申请读取SD卡权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{PERMISSION}, READ_SD_REQUEST_CODE);
                            }
                        }).show();
            } else {
              //  LogUtil.d(TAG, "requestPermission: shouldnot");
                ActivityCompat.requestPermissions(PhotoActivity.this, new String[]{PERMISSION}, READ_SD_REQUEST_CODE);
            }
        } else {
            //权限已授予
           // LogUtil.d(TAG, "requestPermission: PERMISSION_GRANTED");
            initView();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_SD_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //LogUtil.d(TAG, "onRequestPermissionsResult: 权限已被授予");
                initView();
            }else {
               // LogUtil.d(TAG, "onRequestPermissionsResult: PERMISSION_DENIEDD");

                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)){
                    ToastUtil.showToast(PhotoActivity.this,1,"使用本应用须授予权限，请退出后，到权限管理处授予权限后再使用");
                    //用户勾选了不再询问
                  //  LogUtil.d(TAG, "onRequestPermissionsResult: 权限被禁止，");
                }else{
                    finish();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_RECEIVE_FILE){
            if (albumFragment != null){
                albumFragment.onActivityResult(requestCode,resultCode,data);
            }
        }
    }
}
