package com.bytedance.xly.BigPicture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.xly.R;
import com.bytedance.xly.model.bean.AlbumBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Bianji extends AppCompatActivity {
    List<AlbumBean> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bianji);
        final ImageView imageView=findViewById(R.id.bjimage);
        Button button=findViewById(R.id.bjxz);
        Button button1=findViewById(R.id.bjcj);
       Intent i=getIntent();
        final int path=i.getIntExtra("path",0);
         list= (List<AlbumBean>) i.getSerializableExtra("array");
        final Bitmap bitmap= BitmapFactory.decodeFile(list.get(path).getPath());
        imageView.setImageBitmap(bitmap);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(Bianji.this).load(bitmap).apply(getRotateOptions(Bianji.this)).into(imageView);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveBmpToPath(bitmap,list.get(path).getPath());
                Toast.makeText(Bianji.this,"保存成功",Toast.LENGTH_SHORT).show();
            }
        });


    }
    public static RequestOptions getRotateOptions(Context context){
        return RequestOptions.bitmapTransform(new Transfrom(context,90));
    }
    public boolean saveBmpToPath(final Bitmap bitmap, final String filePath) {
        if (bitmap == null || filePath == null) {
            return false;
        }
        boolean result = false; //默认结果
        File file = new File(filePath);
        OutputStream outputStream = null; //文件输出流
        try {
            outputStream = new FileOutputStream(file);
            result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //将图片压缩为JPEG格式写到文件输出流，100是最大的质量程度
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream!= null) {
                try {
                    outputStream.close(); //关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
