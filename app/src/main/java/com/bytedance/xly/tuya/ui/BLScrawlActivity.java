package com.bytedance.xly.tuya.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.tuya.model.BLPaint;
import com.bytedance.xly.tuya.model.BLScrawlParam;
import com.bytedance.xly.tuya.scrawl.DrawingBoardView;
import com.bytedance.xly.tuya.scrawl.ScrawlTools;
import com.bytedance.xly.tuya.ui.holocolorpicker.ColorPicker;
import com.bytedance.xly.tuya.ui.holocolorpicker.OpacityBar;
import com.bytedance.xly.tuya.ui.holocolorpicker.SVBar;
import com.bytedance.xly.tuya.util.BLConfigManager;
import com.bytedance.xly.tuya.util.BLSelectedStateListDrawable;
import com.bytedance.xly.util.FileUtils;
import com.bytedance.xly.util.LogUtil;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;

/**
 * Created by Administrator on 2017/4/21.
 * 涂鸦
 */

public class BLScrawlActivity extends BLToolBarActivity implements View.OnClickListener {
    private static final String TAG = "BLScrawlActivity";
    private DrawingBoardView mDrawingView;
    private HorizontalListView mHlvPaint;
    private SeekBar mSbPaintSize;
    private TextView mTvPaint, mTvPaintSize, mTvPaintColor;

    private ScrawlTools mScrawlTools;
    private BLScrawlParam mParam;
    private Bitmap mSource;
    private PaintAdapter mPaintAdapter;
    private List<BLPaint> mData = new ArrayList<>();
    private int mPaintSize;
    private PopupWindow mPopupWindow;
    private int mPaintColor;
    private ProgressBar mProgressBar;
    private static final int ON_SACN_COMPLETED = 1;
    private Handler mHandler ;
    private static class BLScrawlHandler extends Handler{

        private WeakReference<BLScrawlActivity> mWeakReference;

        public BLScrawlHandler(BLScrawlActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
             BLScrawlActivity activity = mWeakReference.get();
            if (msg.what == ON_SACN_COMPLETED){
                activity.onBackPressed();
            }
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.bl_activity_scrawl;
    }

    @Override
    protected void customToolBarStyle() {
        mToolbar.setTitle("涂鸦");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



    }

    @Override
    protected void initView() {
        mDrawingView = getViewById(R.id.scrawl_draw_view);
        mHlvPaint = getViewById(R.id.scrawl_paint_list);
        mSbPaintSize = getViewById(R.id.scrawl_paint_size_sb);
        mTvPaint = getViewById(R.id.scrawl_paint);
        mTvPaintSize = getViewById(R.id.scrawl_paint_size);
        mTvPaintColor = getViewById(R.id.scrawl_paint_color);
        mProgressBar = getViewById(R.id.progress_bar);
    }

    @Override
    protected void otherLogic() {
        mHandler = new BLScrawlHandler(this);
        mPaintColor = BLConfigManager.getPrimaryColor();
        mParam = getIntent().getParcelableExtra(BLScrawlParam.KEY);
        mSource = BLScrawlParam.bitmap;
        mScrawlTools = new ScrawlTools(mInstance, mDrawingView, mSource);
        mData = BLPaint.getPaints();
        mPaintAdapter = new PaintAdapter(mInstance, mData);
        mHlvPaint.setAdapter(mPaintAdapter);
        mPaintSize = mSbPaintSize.getMax() - mSbPaintSize.getProgress();
        onPaintClick();
        mPaintAdapter.selectedPaint(0);
    }


    @Override
    protected void setListener() {
        mHlvPaint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPaintAdapter.selectedPaint(position);
            }
        });
        mTvPaintColor.setOnClickListener(this);
        mTvPaint.setOnClickListener(this);
        mTvPaintSize.setOnClickListener(this);
        mSbPaintSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPaintSize = seekBar.getMax() - seekBar.getProgress();
                mPaintAdapter.selectedPaint(mPaintAdapter.getCurPosition());
            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 创建颜色选择器弹出框
     */
    private void showPopupColor() {

        View view = getLayoutInflater().inflate(R.layout.camerasdk_popup_colorpick, null);

        ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        SVBar svBar = (SVBar) view.findViewById(R.id.svbar);
        OpacityBar opacityBar = (OpacityBar) view.findViewById(R.id.opacitybar);

        picker.setColor(mPaintColor);

        picker.addSVBar(svBar);
        picker.addOpacityBar(opacityBar);

        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {

            @Override
            public void onColorChanged(int color) {
                // TODO Auto-generated method stub
                getPickColor(color);
            }
        });

        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mInstance);
            mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
        }

        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });

        mPopupWindow.setContentView(view);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        mPopupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void getPickColor(int color) {
        mPaintColor = color;
        mPaintAdapter.selectedPaint(mPaintAdapter.getCurPosition());
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        if (resId == R.id.scrawl_paint) {
            onPaintClick();
        } else if (resId == R.id.scrawl_paint_size) {
            onPaintSizeClick();
        } else if (resId == R.id.scrawl_paint_color) {
            onPaintColorClick();
        }
    }

    private void onPaintClick() {
        selectedBottomTab(mTvPaint);
        mHlvPaint.setVisibility(View.VISIBLE);
        mSbPaintSize.setVisibility(View.GONE);
    }

    private void onPaintSizeClick() {
        selectedBottomTab(mTvPaintSize);
        mHlvPaint.setVisibility(View.GONE);
        mSbPaintSize.setVisibility(View.VISIBLE);
    }

    private void onPaintColorClick() {
        showPopupColor();
    }

    private void selectedBottomTab(TextView tv) {
        mTvPaintSize.setTextColor(Color.BLACK);
        mTvPaint.setTextColor(Color.BLACK);
        mTvPaintColor.setTextColor(Color.BLACK);
        tv.setTextColor(BLConfigManager.getPrimaryColor());
    }

    class PaintAdapter extends BaseAdapter {
        private Context mContext;
        private List<BLPaint> paintData;
        private BLPaint mSelectedPaint;
        private int mCurPosition;

        public PaintAdapter(Context context, List<BLPaint> list) {
            mContext = context;
            paintData = list;
        }

        public int getCurPosition() {
            return mCurPosition;
        }

        public void selectedPaint(int position) {
            if (paintData == null)
                return;
            mSelectedPaint = paintData.get(position);
            mSelectedPaint.setColor(mPaintColor);
            mCurPosition = position;
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = mPaintSize;
            Bitmap paintBitmap = BitmapFactory.decodeResource(mContext.getResources(), mSelectedPaint.getPaintId(), option);
            mScrawlTools.creatDrawPainter(mSelectedPaint.getDrawStatus(), paintBitmap, mSelectedPaint.getColor());
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return paintData == null ? 0 : paintData.size();
        }

        @Override
        public Object getItem(int position) {
            return paintData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.bl_item_scrawl_paint_lv, parent, false);
                holder = new ViewHolder();
                holder.ivPaint = getViewById(R.id.item_scrawl_paint_img, convertView);
                holder.tvPaint = getViewById(R.id.item_scrawl_paint, convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (paintData.get(position).getImgResId() > 0) {
                holder.ivPaint.setImageDrawable(new BLSelectedStateListDrawable(mContext.getResources().getDrawable(paintData.get(position).getImgResId()), BLConfigManager.getPrimaryColor()));
            }
            holder.tvPaint.setText(paintData.get(position).getName());

            if (mCurPosition == position) {
                holder.tvPaint.setTextColor(BLConfigManager.getPrimaryColor());
                holder.ivPaint.setSelected(true);
            } else {
                holder.tvPaint.setTextColor(Color.BLACK);
                holder.ivPaint.setSelected(false);
            }


            return convertView;
        }

        class ViewHolder {
            ImageView ivPaint;
            TextView tvPaint;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.preview_menu:
                    //涂鸦完成
                BLScrawlParam.bitmap = mScrawlTools.getBitmap();
                String path = FileUtils.getRootDirPath() + "tuya_" + System.currentTimeMillis() + ".jpg";
                saveImageAndExit(path);
                    break;
        }

        return super.onOptionsItemSelected(item);
    }

    private  class SaveImageToSdcardTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {
            String resultPath = FileUtils.bitmapToSDCard(BLScrawlParam.bitmap,strings[0]);
            LogUtil.d(TAG, "onOptionsItemSelected: resultPath " + resultPath);
            return resultPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//                mProgressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(s)) {
                MediaScannerConnection.scanFile(BLScrawlActivity.this, new String[]{s}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        if (uri != null){
                           setResult(RESULT_OK);
                        }
                        mHandler.sendEmptyMessage(ON_SACN_COMPLETED);
                    }
                });

            }else{
              onBackPressed();
            }

        }
    }
    private void saveImageAndExit(String path) {
        mProgressBar.setVisibility(View.VISIBLE);
        new SaveImageToSdcardTask().execute(path);
    }

}
