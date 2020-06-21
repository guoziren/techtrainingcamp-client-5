package com.bytedance.xly.bigpicture;

import android.annotation.SuppressLint;

import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import androidx.viewpager.widget.ViewPager;

import com.bytedance.xly.R;
import com.bytedance.xly.thumbnail.model.bean.AlbumBean;
import com.bytedance.xly.tuya.model.BLScrawlParam;
import com.bytedance.xly.tuya.ui.BLScrawlActivity;
import com.bytedance.xly.util.FileUtils;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.TransferUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;


import java.io.File;

import java.io.FileNotFoundException;
import java.io.Serializable;

import java.lang.ref.WeakReference;
import java.util.List;

public class PreviewActivity extends AppCompatActivity {
    private List<AlbumBean> picturePath;
    private ViewPager ViewPage;
    private int currentPage;
    private GestureDetector gd1;//手势

    private ScaleView[] mScaleViews;
    private ScalePagerAdapter mAdapter;
    private static final String TAG = "PreviewActivity";
    private boolean isFirst=true;
    private TextView mBtnTuya;
    private TextView mBtnCaiJian;
    private static final int ON_SACN_COMPLETED = 1;
    private Handler mHandler ;
    private static class ActivityHandler extends Handler{


        private WeakReference<PreviewActivity> mWeakReference;

        public ActivityHandler(PreviewActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            PreviewActivity activity = mWeakReference.get();
            if (msg.what == ON_SACN_COMPLETED){
                activity.onBackPressed();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        TextView button=findViewById(R.id.huaban);//这是涂鸦
        TextView button1=findViewById(R.id.bianji);//这是旋转功能的入口
        mBtnTuya = findViewById(R.id.tuya);
        mBtnCaiJian = findViewById(R.id.caijian);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PreviewActivity.this,Tuya.class);//转到涂鸦的界面
                intent.putExtra("path",currentPage);
                intent.putExtra("array", (Serializable) picturePath);
                startActivity(intent);
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PreviewActivity.this,Bianji.class);
                intent.putExtra("path",currentPage);
                intent.putExtra("array", (Serializable) picturePath);
                startActivity(intent);
            }
        });//转到编辑的界面
        gd1 = new GestureDetector(this,new SimpleOnGestureListener());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initEvent();
        LogUtil.d(TAG, "onCreate: ");
    }

    private void initEvent() {
        mBtnTuya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScrawlClick();
            }
        });
        mBtnCaiJian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUCropActivity();
            }
        });
    }
    private void onScrawlClick() {
        BLScrawlParam.bitmap = BitmapFactory.decodeFile(picturePath.get(currentPage).getPath());
//        Intent intent = new Intent(mInstance, BLScrawlActivity.class);
//        intent.putExtra(BLScrawlParam.KEY, new BLScrawlParam());
//        ActivityUtils.startActivityForResult(mInstance, intent, BLScrawlParam.REQUEST_CODE_SCRAWL);
        BLScrawlParam.startActivity(PreviewActivity.this, new BLScrawlParam());
    }
    private void gotoUCropActivity() {
        File sourceFile = new File(picturePath.get(currentPage).getPath());
        Uri source = Uri.fromFile(sourceFile);
        Uri destination = Uri.fromFile(new File(FileUtils.getRootDirPath(), "caijian_"+sourceFile.getName()));
        UCrop uCrop = UCrop.of(source, destination);

        uCrop.useSourceImageAspectRatio();
//        uCrop.withAspectRatio(3,2);
        UCrop.Options options = new UCrop.Options();
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.SCALE);
////        设置裁剪框自由移动
        options.setFreeStyleCropEnabled(true);
//        //设置裁剪比例
//        options.setAspectRatioOptions(1, new AspectRatio(null, 5, 4), new AspectRatio(null, 16,9),new AspectRatio(getString(R.string.ucrop_label_original).toUpperCase(),
//                CropImageView.SOURCE_IMAGE_ASPECT_RATIO, CropImageView.SOURCE_IMAGE_ASPECT_RATIO), new AspectRatio(null, 3, 4), new AspectRatio(null, 6, 7));
        options.setStatusBarColor(Color.BLACK);
        options.setToolbarColor(Color.BLACK);
        options.setActiveWidgetColor(Color.BLACK);
        uCrop.withOptions(options);
        uCrop.start(PreviewActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "onActivityResult: resultCode == RESULT_OK " + (resultCode == RESULT_OK));
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case UCrop.REQUEST_CROP:
                    LogUtil.d(TAG, "onActivityResult: 裁剪成功");
                    Uri croppedUri = UCrop.getOutput(data);
                    // 通知相册有新图片
                    MediaScannerConnection.scanFile(this, new String[]{croppedUri.getPath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (uri != null){
                                setResult(RESULT_OK);
                            }
                            mHandler.sendEmptyMessage(ON_SACN_COMPLETED);
                        }
                    });

                    break;
                case BLScrawlParam.REQUEST_CODE_SCRAWL:
                    LogUtil.d(TAG, "onActivityResult: 涂鸦成功");
                    setResult(RESULT_OK);
                    onBackPressed();
                    break;
            }//switch
        }
    }

    private class ScalePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //Log.d(TAG, "getCount: "+picturePath.size());
            return picturePath.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

           LogUtil.d(TAG, "destroyItem: ");
            container.removeView(mScaleViews[position]);
        }

        @Override
        public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            super.setPrimaryItem(container, currentPage, object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ScaleView scaleView = new ScaleView(getApplicationContext());
            LogUtil.d(TAG, "instantiateItem: "+currentPage+" position:"+position);
            scaleView.setImageBitmap(load_picture(picturePath.get(position).getPath()));
            //scaleView.setImageURI(Uri.parse(picturePath.get(position).getPath()));
            mScaleViews[position] = scaleView;
            container.addView(scaleView);
            return scaleView;
        }

    }
    

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gd1.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(){
        Intent intent = getIntent();
        mHandler = new ActivityHandler(this);
        this.picturePath = (List<AlbumBean>) intent.getSerializableExtra("picturePath");
        Log.d(TAG, "initView: "+this.picturePath);
        currentPage = intent.getIntExtra("CurrentPage",0);
        ViewPage = findViewById(R.id.ViewPage);
        mScaleViews = new ScaleView[picturePath.size()];
        mAdapter = new ScalePagerAdapter();
        ViewPage.setAdapter(mAdapter);
        ViewPage.setCurrentItem(currentPage);
    }

    class SimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int verticalMinDistance = 300;
            int minVelocity = 10;
            if (Math.abs(e1.getY() - e2.getY()) > verticalMinDistance && Math.abs(velocityX) > minVelocity) {
                //上下滑动
                PreviewActivity.this.finish();
            }

            return true;
        }

    }
    private Bitmap load_picture(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int Weight = options.outWidth;
        int Height = options.outHeight;

        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        int WeightRatio = Math.round((float)Weight/dm2.widthPixels);
        int HeightRatio = Math.round((float)Height/dm2.heightPixels);

        options.inSampleSize = 1+Math.max(WeightRatio,HeightRatio);
        LogUtil.d(TAG,"inSampleSize:"+options.inSampleSize);
        options.inJustDecodeBounds =false;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }
}
