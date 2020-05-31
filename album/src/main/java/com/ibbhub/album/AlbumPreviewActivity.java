package com.ibbhub.album;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author ：chezi008 on 2018/8/9 21:40
 * @description ：
 * @email ：chezi008@qq.com
 */
public class AlbumPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_preview);

        Fragment fragment = buildAlbumFragment();
        ArrayList<AlbumBean> data = getIntent().getParcelableArrayListExtra("data");
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("data", data);
        bundle.putInt("pos", getIntent().getIntExtra("pos", 0));
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.clParent, fragment, "preview");
        ft.commit();
    }

    protected Fragment buildAlbumFragment() {
        AlbumPreviewFragment fragment = (AlbumPreviewFragment) getSupportFragmentManager().findFragmentByTag("preview");
        if (fragment == null) {
            fragment = new AlbumPreviewFragment();
        }
        return fragment;
    }


    public static void start(Context context, ArrayList<AlbumBean> data, int pos) {
        Intent starter = new Intent(context, AlbumPreviewActivity.class);
        starter.putParcelableArrayListExtra("data", data);
        starter.putExtra("pos", pos);
        context.startActivity(starter);
    }


}
