package com.bytedance.xly.BigPicture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bytedance.xly.R;
import com.bytedance.xly.model.bean.AlbumBean;

import java.util.ArrayList;
import java.util.List;

public class Tuya extends AppCompatActivity implements View.OnClickListener{
    private Button baocun;
    private  Button chexiao;
    private Button color;
    List<AlbumBean> list;
    private tuyapath tuyapath;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuya);
        Intent i=getIntent();
       int path=i.getIntExtra("path",0);
      list= (List<AlbumBean>) i.getSerializableExtra("array");
      Bitmap bitmap= BitmapFactory.decodeFile(list.get(path).getPath());

        initView();
        initListener();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chexiao:
                tuyapath.undo();
                return;
            case R.id.color:
                tuyapath.resetPaintColor(Color.RED);
                return;
            case R.id.baocun:
                tuyapath.save();
                Toast.makeText(Tuya.this,"保存成功",Toast.LENGTH_SHORT).show();

        }
    }
    private void initListener() {
        chexiao.setOnClickListener(this);
        color.setOnClickListener(this);
        baocun.setOnClickListener(this);
    }
    private void initView() {
        tuyapath=findViewById(R.id.tuyapath);
        chexiao= findViewById(R.id.chexiao);
        baocun= findViewById(R.id.baocun);
        color=findViewById(R.id.color);
    }
}
