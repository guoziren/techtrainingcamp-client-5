package com.bytedance.xly.BigPicture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
    private static int rate;//旋转角度
    private static boolean is_caijian = false;//是否裁剪过
    List<AlbumBean> list;
    private static Bitmap bitmap_duplicate;
    private CropImageView mCropImageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rate = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bianji);
        mCropImageView = (CropImageView) findViewById(R.id.bjimage);
        //final ImageView imageView=findViewById(R.id.bjimage);
        Button button=findViewById(R.id.bjxz);
        Button button1=findViewById(R.id.bjcj);
        Button button2=findViewById(R.id.bjbc);
        Intent i=getIntent();
        final int path=i.getIntExtra("path",0);
        list= (List<AlbumBean>) i.getSerializableExtra("array");
        Bitmap bitmap = BitmapFactory.decodeFile(list.get(path).getPath());
        bitmap_duplicate = bitmap;
        is_too_small();//如果图片太小先放大。
        resetView(is_caijian);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Glide.with(Bianji.this).load(bitmap).apply(getRotateOptions(Bianji.this)).into(imageView);
                Matrix matrix = new Matrix();
                //rate = (rate+90)%360;
                matrix.postRotate(90);
                bitmap_duplicate = Bitmap.createBitmap(bitmap_duplicate, 0, 0, bitmap_duplicate.getWidth(), bitmap_duplicate.getHeight(), matrix, true);
                //imageView.setImageBitmap(bitmap_duplicate);
                resetView(is_caijian);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_caijian = true;
                resetView(is_caijian);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path1 = list.get(path).getPath();
                saveBmpToPath(path1);
                Toast.makeText(Bianji.this,"保存成功啦",Toast.LENGTH_SHORT).show();
            }
        });


    }
//    public static RequestOptions getRotateOptions(Context context){
//        rate = (rate+90)%360;//每次点击旋转角度加90°;
//        return RequestOptions.bitmapTransform(new Transfrom(context,rate));
//    }

    public boolean saveBmpToPath(String filePath) {
        if (bitmap_duplicate == null || filePath == null) {
            return false;
        }
        if(is_caijian){
            bitmap_duplicate = mCropImageView.getCropImage();
            mCropImageView.setBitmap(bitmap_duplicate);
            mCropImageView.setVisibility(View.VISIBLE);
            is_caijian = false;
        }
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rate);
//        bitmap_duplicate = Bitmap.createBitmap(bitmap_duplicate, 0, 0, bitmap_duplicate.getWidth(), bitmap_duplicate.getHeight(), matrix, true);
        boolean result = false; //默认结果
        File file = new File(filePath);
        OutputStream outputStream = null; //文件输出流
        try {
            outputStream = new FileOutputStream(file);
            result = bitmap_duplicate.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //将图片压缩为JPEG格式写到文件输出流，100是最大的质量程度
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

    //重新设置ImageView，如果false，便只加载图片
    private void resetView(boolean flag) {
        mCropImageView.setVisibility(View.GONE);
        if(flag)
            mCropImageView.setDrawable(300, 300);
        else
            mCropImageView.setBitmap(bitmap_duplicate);
        mCropImageView.setVisibility(View.VISIBLE);

    }

    //针对缩略图，图片太小无法容纳屏幕时用
    private void is_too_small(){
        int Weight = bitmap_duplicate.getWidth();
        int Height = bitmap_duplicate.getHeight();
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        float WeightRatio = (float)dm2.widthPixels/Weight;
        float HeightRatio = (float)dm2.heightPixels/Height;
        float inSampleSize = Math.min(WeightRatio,HeightRatio);
        if(WeightRatio>1&&HeightRatio>1){
            //如果屏幕像素宽和高/图片像素宽和高 大于1 证明 图片比屏幕小，要放大到图片大小
            Matrix matrix = new Matrix();
            matrix.postScale(inSampleSize,inSampleSize);
            bitmap_duplicate = Bitmap.createBitmap(bitmap_duplicate, 0, 0, Weight, Height, matrix, true);
        }

    }

//    private void tran_bitmap(){
//        int Weight = bitmap_duplicate.getWidth();
//        int Height = bitmap_duplicate.getHeight();
//        DisplayMetrics dm2 = getResources().getDisplayMetrics();
//        int WeightRatio = Math.round((float)dm2.widthPixels/Weight);
//        int HeightRatio = Math.round((float)dm2.heightPixels/Height);
//        float inSampleSize = Math.min(WeightRatio,HeightRatio);
//    }


}
