package com.bytedance.xly.BigPicture;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bytedance.xly.R;
import com.bytedance.xly.model.bean.AlbumBean;
import com.bytedance.xly.view.activity.FastShareActivity;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {
    private List<String> picturePath;
    private ViewPager ViewPage;
    private int currentPage;
    private GestureDetector gd1;//手势
    private ScaleGestureDetector sgd1;
    private float scaleRatio=1f;
    private int Weight;
    private long downTime;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA = 1;

    Bitmap bitmap=null;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private ImageView im6;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    // 第一个按下的手指的点
    private PointF startPoint = new PointF();
    // 两个按下的手指的触摸点的中点
    private PointF midPoint = new PointF();
    // 初始的两个手指按下的触摸点的距离
    private float oriDis = 1f;
    private  boolean flag=true;
    private float dx=0;
    private float dy=0;
    private static final String TAG = "Main2Activity";

    private Button mBtn_share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //im6.setVisibility(View.GONE);
        setContentView(R.layout.activity_main2);
        Log.d(TAG,getClass().getSimpleName());
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

    }

    private void initEvent() {
        mBtn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
//
//                builder.setMessage("正在搜索局域网中的设备...");
//                AlertDialog dialog = builder.create();
//                dialog.show();
                 startActivity(new Intent(Main2Activity.this, FastShareActivity.class));
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gd1.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);

    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        gd1.onTouchEvent(ev);
//        return super.dispatchTouchEvent(ev);
//    }
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        Intent intent = getIntent();
        this.picturePath = (List<String>) intent.getSerializableExtra("picturePath");
        currentPage = intent.getIntExtra("CurrentPage", 0);
        ViewPage = findViewById(R.id.ViewPage);

        im6 = findViewById(R.id.imageView6);
        setPic(picturePath, currentPage);
        im6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        im6.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();
                downTime = System.currentTimeMillis();
                Log.d(TAG, "onTouch: x= " + x + "y=" + y);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        //单点触控
                        matrix.set(view.getImageMatrix());
                        savedMatrix.set(matrix);
                        startPoint.set(event.getRawX(), event.getRawY());
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        //多点触控
                        oriDis = distance(event);
                        if (oriDis > 10f) {
                            savedMatrix.set(matrix);
                            midPoint = midPoint(event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 手指滑动事件
                        if (mode == DRAG) {
                            // 是一个手指拖动
                            Log.d(TAG, "onTouch: dx=" + (event.getRawX() - startPoint.x - dx) + " dy=" + (event.getRawY()
                                    - startPoint.y - dy));
                            //matrix.set(savedMatrix);
                            matrix.postTranslate(event.getRawX() - startPoint.x - dx, event.getRawY() - startPoint.y - dy);
                            dx = event.getRawX() - startPoint.x;
                            dy = event.getRawY() - startPoint.y;

                        } else if (mode == ZOOM) {
                            // 两个手指滑动
                            float newDist = distance(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float scale = newDist / oriDis;
                                matrix.preScale(scale, scale, midPoint.x, midPoint.y);

                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "onTouch: ACTION_UP");
                        if (mode == DRAG) {
                            if (System.currentTimeMillis() - downTime < 100) {
                                if (event.getRawX() - startPoint.x > 300) {
                                    currentPage -= 1;
                                    matrix.reset();
                                    if (currentPage >= 0)
                                        setPic(picturePath, currentPage);
                                }
                                if (event.getRawX() - startPoint.x < -300) {
                                    currentPage += 1;
                                    matrix.reset();
                                    if (currentPage < picturePath.size())
                                        setPic(picturePath, currentPage);
                                }
                            }
                        }

                        dx = 0;
                        dy = 0;
                    case MotionEvent.ACTION_POINTER_UP:
                        // 手指放开事件
                        mode = NONE;

                        break;
                }

                view.setImageMatrix(matrix);
                return true;
            }
        });

    }
//            public Fragment getItem(int i) {
//                return SplashFragment.newInstance(Main2Activity.this.picturePath.get(i).getPath());
//            }
//
//        ViewPage.setOnClickListener(new View.OnClickListener() {
//            @Override
//<<<<<<< HEAD
//            public void onClick(View v) {
//                RelativeLayout layout = findViewById(R.id.layout);
//
//                if (layout.getVisibility() == View.VISIBLE) {
//                    layout.setVisibility(View.INVISIBLE);
//                } else {
//                    layout.setVisibility(View.VISIBLE);
//                }
//                Log.d(TAG, "onClick: "+getClass().getSimpleName());
//=======
//            public int getCount() {
//                return Main2Activity.this.picturePath.size();
//>>>>>>> albumByDate
//            }
//        });
//        ViewPage.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
//
//            public Fragment getItem(int i) {
//                return SplashFragment.newInstance(picturePath.get(i));
//            }
//
//            @Override
//            public int getCount() {
//                return picturePath.size();
//            }
//        });
//        ViewPage.setCurrentItem(currentPage);

//    }
    /**
     * 计算两个手指头之间的中心点的位置
     * x = (x1+x2)/2;
     * y = (y1+y2)/2;
     *
     * @param event 触摸事件
     * @return 返回中心点的坐标
     */
    private PointF midPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }


    /**
     * 计算两个手指间的距离
     *
     * @param event 触摸事件
     * @return 放回两个手指之间的距离
     */
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);//两点间距离公式
    }
    private void setPic(List<String> picturePath,int currentPage){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;

        Weight = options.outWidth;
        int Height = options.outHeight;
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        int WeightRatio = Math.round((float)Weight/dm2.widthPixels);
        int HeightRatio = Math.round((float)Height/dm2.heightPixels);
        options.inSampleSize = Math.max(WeightRatio,HeightRatio);
        options.inJustDecodeBounds =false;
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath.get(currentPage), options);
        im6.setImageBitmap(bitmap);
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
