package com.bytedance.xly.view.activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.view.ReceicerActivity;
import com.bytedance.xly.view.fragment.AlbumFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

/*
 * 包名：      com.bytedance.xly.activity
 * 文件名：      PhotoActivity
 * 创建时间：      2020/5/31 10:17 PM
 *
 */
public class PhotoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_album);
        super.onCreate(savedInstanceState);
        initView();
    }
    private MenuItem chooseMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        chooseMenu = menu.findItem(R.id.action_choose);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_choose:
                if (item.getTitle().equals("选择")) {
                    albumFragment.enterChoose();
                } else {
                    albumFragment.cancelChoose();
                }
                break;
            case R.id.action_receive:
                Intent intent = new Intent(this, ReceicerActivity.class);
//                intent.putExtra("mode","receive");
                startActivity(intent);
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
//            albumFragment = new MyAlbumFragment();
            albumFragment = new com.bytedance.xly.view.fragment.AlbumFragment();
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flParent, albumFragment);
        ft.commit();
    }

    public void onChooseModeChange(boolean isChoose) {
        chooseMenu.setTitle(isChoose ? "取消" : "选择");

    }

    public static void start(Context context) {
        Intent starter = new Intent(context, PhotoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
