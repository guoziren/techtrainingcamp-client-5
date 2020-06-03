package com.bytedance.xly.view.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bytedance.xly.adapter.AlbumAdapter;
import com.bytedance.xly.model.bean.AlbumBean;
import com.bytedance.xly.util.UITool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.view.view
 * 文件名：      TimeAlbumView 相册列表中1天中的所有照片的layout
 * 创建时间：      2020/6/2 6:21 PM
 *
 */
public class TimeAlbumView extends LinearLayout {
    private List<AlbumBean> data = new ArrayList<>();
    private AlbumAdapter mAlbumAdapter;

    public TimeAlbumView(Context context) {
        this(context,null);
    }

    public TimeAlbumView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimeAlbumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        int space = UITool.dip2px(getContext(),5);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initLLDate(getContext());
        addView(llParent,params);

        RecyclerView recyclerView = new RecyclerView(getContext());
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        params.weight = 1;
        recyclerView.setPadding(space,0,space,0);
        recyclerView.setBackgroundColor(Color.BLACK);
        addView(recyclerView, params);

//        mAlbumAdapter = new AlbumAdapter(data);
        final int spanCount = 4;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mAlbumAdapter);
        
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = position % spanCount; // item column

                if (includeEdge) {
                    outRect.left = space - column * space / spanCount; // space - column * ((1f / spanCount) * space)
                    outRect.right = (column + 1) * space / spanCount; // (column + 1) * ((1f / spanCount) * space)

                    if (position < spanCount) { // top edge
                        outRect.top = space;
                    }
                    outRect.bottom = space; // item bottom
                } else {
                    outRect.left = column * space / spanCount; // column * ((1f / spanCount) * space)
                    outRect.right = space - (column + 1) * space / spanCount; // space - (column + 1) * ((1f /    spanCount) * space)
                    if (position >= spanCount) {
                        outRect.top = space; // item top
                    }
                }
            }
        });
        

    }


    private TextView mTvDate, mTvNum;
    private LinearLayout llParent;//日期layout

    public void initLLDate(Context ctx) {
        int lPadding = UITool.dip2px(ctx, 10);
        int tPadding = UITool.dip2px(ctx, 5);
        llParent = new LinearLayout(ctx);
        llParent.setOrientation(LinearLayout.HORIZONTAL);
        llParent.setPadding(lPadding, tPadding, lPadding, tPadding);

        mTvDate = new TextView(ctx);
        mTvDate.setTextSize(16);
        mTvNum = new TextView(ctx);
        mTvNum.setTextSize(16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;
        llParent.addView(mTvDate, params);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = lPadding;
        llParent.addView(mTvNum, params);

        llParent.setBackgroundColor(Color.parseColor("#eeeeee"));
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");

    public void showDate(long date) {
        String sDate = dateFormat.format(date);
        mTvDate.setText(sDate);
    }

    public void showNum(int num) {
        String sNum = num > 0 ? String.format("%d", num) : "";
        mTvNum.setText(sNum);
    }
}
