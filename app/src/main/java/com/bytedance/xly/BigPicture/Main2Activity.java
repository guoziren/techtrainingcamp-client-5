package com.bytedance.xly.BigPicture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bytedance.xly.R;


import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    private ArrayList<String> picturePath;
    private ViewPager ViewPage;
    private int currentPage;
    private GestureDetector gd1;//手势
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        gd1 = new GestureDetector(this,new SimpleOnGestureListener());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gd1.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initView(){
        Intent intent = getIntent();
        picturePath = intent.getStringArrayListExtra("picturePath");
        currentPage = intent.getIntExtra("CurrentPage",0);
        ViewPage = findViewById(R.id.ViewPage);

        ViewPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            RelativeLayout layout = findViewById(R.id.layout);
            if(event.getAction()==MotionEvent.ACTION_UP){
                if(layout.getVisibility()== View.VISIBLE){
                    layout.setVisibility(View.INVISIBLE);
                }else {
                    layout.setVisibility(View.VISIBLE);
                }
            }
                return false;
            }
        });
        ViewPage.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            public Fragment getItem(int i) {
                return SplashFragment.newInstance(picturePath.get(i));
            }

            @Override
            public int getCount() {
                return picturePath.size();
            }
        });
        ViewPage.setCurrentItem(currentPage);
    }

    class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int verticalMinDistance = 300;
            int minVelocity = 10;
            if (Math.abs(e1.getY() - e2.getY()) > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                //上下滑动
                Main2Activity.this.finish();
            }
            return true;
        }

    }
}
