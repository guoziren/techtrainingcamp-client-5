package com.bytedance.xly.bigpicture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bytedance.xly.R;
import com.bytedance.xly.thumbnail.model.bean.AlbumBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Bianji extends AppCompatActivity {
    private static int rate;//旋转角度
    private static boolean is_caijian = false;//是否裁剪过
    List<AlbumBean> list;
    private static Bitmap bitmap;
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
        //Bitmap bitmap = BitmapFactory.decodeFile(list.get(path).getPath());
        bitmap = load_picture(list.get(path).getPath());
        //is_too_small();//如果图片太小先放大。
        resetView(is_caijian);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Glide.with(Bianji.this).load(bitmap).apply(getRotateOptions(Bianji.this)).into(imageView);
                Matrix matrix = new Matrix();
                //rate = (rate+90)%360;
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                //imageView.setImageBitmap(bitmap);
                is_too_small();//旋转后若想放大图片，用此函数。
                resetView(is_caijian);
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_caijian = true;
                resetView(is_caijian);
                is_caijian = false;
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
        if (bitmap == null || filePath == null) {
            return false;
        }
        if(is_caijian){
            bitmap = mCropImageView.getCropImage();
            mCropImageView.setBitmap(bitmap);
            mCropImageView.setVisibility(View.VISIBLE);
        }
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rate);
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    //重新设置ImageView，如果false，便只加载图片
    private void resetView(boolean flag) {
        mCropImageView.setVisibility(View.GONE);
        if(flag)
            mCropImageView.setDrawable(300, 300);
        else
            mCropImageView.setBitmap(bitmap);
        mCropImageView.setVisibility(View.VISIBLE);

    }

    //针对缩略图，图片太小无法容纳屏幕时用（放大图片的功能）
    private void is_too_small(){
        int Weight = bitmap.getWidth();
        int Height = bitmap.getHeight();
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        float WeightRatio = (float)dm2.widthPixels/Weight;
        float HeightRatio = (float)dm2.heightPixels/Height;
        float inSampleSize = Math.min(WeightRatio,HeightRatio);
        if(WeightRatio>1&&HeightRatio>1){
            //如果屏幕像素宽和高/图片像素宽和高 大于1 证明 图片比屏幕小，要放大到图片大小
            Matrix matrix = new Matrix();
            matrix.postScale(inSampleSize,inSampleSize);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, Weight, Height, matrix, true);
        }

    }

    //加载图片，防止加载大图崩了。
    private Bitmap load_picture(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int Weight = options.outWidth;
        int Height = options.outHeight;
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        int WeightRatio = Math.round((float)Weight/dm2.widthPixels);
        int HeightRatio = Math.round((float)Height/dm2.heightPixels);
        options.inSampleSize = Math.max(WeightRatio,HeightRatio);
        options.inJustDecodeBounds =false;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }


//    private void tran_bitmap(){
//        int Weight = bitmap.getWidth();
//        int Height = bitmap.getHeight();
//        DisplayMetrics dm2 = getResources().getDisplayMetrics();
//        int WeightRatio = Math.round((float)dm2.widthPixels/Weight);
//        int HeightRatio = Math.round((float)dm2.heightPixels/Height);
//        float inSampleSize = Math.min(WeightRatio,HeightRatio);
//    }


}
