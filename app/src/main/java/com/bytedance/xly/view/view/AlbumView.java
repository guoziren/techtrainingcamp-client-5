package com.bytedance.xly.view.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bytedance.xly.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*
 * 包名：      com.bytedance.xly.view.view
 * 文件名：      AlbumView
 * 创建时间：      2020/6/2 5:04 PM
 *
 */
public class AlbumView extends FrameLayout {
    public static final int STYLE_PHOTO = 0;
    public static final int STYLE_VIDEO = 1;

    private CheckBox mCheckbox;
    private ImageView mIvFlag;

    private ImageView mIvThumb;;
    private ImageView mIvPlay;
    private View mMask;

    public CheckBox getCheckbox() {
        return mCheckbox;
    }

    public ImageView getIvThumb() {
        return mIvThumb;
    }

    public AlbumView(@NonNull Context context) {
        this(context,null);
    }

    public AlbumView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AlbumView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_media, this);
        initView();
    }

    private void initView() {
        mCheckbox = findViewById(R.id.cb_check);
        mIvThumb = findViewById(R.id.iv_thumb);
        mIvPlay = findViewById(R.id.iv_play);
        mMask = findViewById(R.id.v_mask);
        mIvFlag = findViewById(R.id.ivFlag);
    }

    public void setStyle(int style) {
        mIvPlay.setVisibility(style == STYLE_PHOTO ? GONE : VISIBLE);
    }
    public void setChooseStyle(boolean choose) {

        mCheckbox.setVisibility(choose ? VISIBLE : GONE);
    }
    public void setChecked(boolean checked) {
        mCheckbox.setChecked(checked);
    }
    public boolean isCheckMode(){
        return mCheckbox.getVisibility() == VISIBLE;
    }
    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener){
        mCheckbox.setOnCheckedChangeListener(listener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY){
            int dimensSpec = MeasureSpec.makeMeasureSpec(width > height ? width : height,MeasureSpec.EXACTLY);
            super.onMeasure(dimensSpec,dimensSpec);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
