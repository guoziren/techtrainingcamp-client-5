package com.bytedance.xly.bigpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class tuyapath extends View {

    Bitmap b1=null;
    public class DrawPath {

        Path path;

        Paint paint;

    }
    private Paint paint;
    private Path path;
    private float downX, downY;
    private float tempX, tempY;
    private int paintWidth = 10;
    private int width=0;
    private  int height=0;
    Bitmap bitmap;
    Canvas canvas1;
    Rect boundary;
    Boolean isdraw=true;
    private List<DrawPath> drawPathList;
    private List<DrawPath> savePathList;
    public tuyapath(Context context) {

        this(context, null);


    }
    public tuyapath(Context context, AttributeSet attrs) {

        this(context, attrs, 0);

    }
    public tuyapath(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        drawPathList = new ArrayList<>();
        savePathList = new ArrayList<>();
        initPaint();
    }
    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(paintWidth);
        paint.setStyle(Paint.Style.STROKE);    }
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawPathList != null && !drawPathList.isEmpty())
        {
            for (DrawPath drawPath : drawPathList) {
                if (drawPath.path != null) {
                    canvas.drawPath(drawPath.path, drawPath.paint);
                }
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                path = new Path();//每次手指下去都是一条新的路径
                path.moveTo(downX, downY);
                DrawPath drawPath = new DrawPath();
                drawPath.paint = paint;
                drawPath.path = path;
                drawPathList.add(drawPath);
                invalidate();
                tempX = downX;
                tempY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                path.quadTo(tempX, tempY, moveX, moveY);
                invalidate();
                tempX = moveX;
                tempY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                initPaint();//每次手指抬起都要重置下画笔,不然画笔会保存了之前的设置什么画笔的属性会引起bug
                break;
        }
        return true;
    }
    public void undo() {
        if (drawPathList != null && drawPathList.size() >= 1) {
            savePathList.add(drawPathList.get(drawPathList.size() - 1));
            drawPathList.remove(drawPathList.size() - 1);
            invalidate();
        }
    }

    /* public void reundo() {
         if (savePathList != null && !savePathList.isEmpty()) {
             drawPathList.add(savePathList.get(savePathList.size() - 1));
             savePathList.remove(savePathList.size() - 1);
             invalidate();
         }
     }*/
    public void resetPaintColor(int color) {

        paint.setColor(color);

    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        bitmap = Bitmap.createBitmap(width - 8, height - 8, Bitmap.Config.ARGB_8888);
        canvas1 = new Canvas(bitmap);
        boundary = new Rect(8, 8, width - 8, height - 8);
    }

    public Bitmap save(){
        if(!isdraw) return null;
        return bitmap;
    }
   /* public void resetPaintWidth() {

        paintWidth += 2;

        paint.setStrokeWidth(paintWidth);

    }*/
   /* public void eraser() {

        paint.setColor(Color.WHITE);//这是view背景的颜色

        paint.setStrokeWidth(paintWidth + 6);

    }*/


}
