package com.bytedance.xly.model.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.bytedance.xly.model.IDataCallback;
import com.bytedance.xly.model.bean.AlbumBean;
import com.bytedance.xly.model.bean.DateAlbumBean;
import com.bytedance.xly.model.IDateAlbumModel;
import com.bytedance.xly.util.DateUtil;
import com.bytedance.xly.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/*
 * 包名：      com.bytedance.xly.model.impl
 * 文件名：      DateAlbumModelImpl
 * 创建时间：      2020/6/3 3:53 PM
 *
 */
public class DateAlbumModelImpl  implements IDateAlbumModel {
    private static final String TAG = "DateAlbumModelImpl";
    private IDataCallback<List<DateAlbumBean>> mCallback;
    private Calendar mCal1 = Calendar.getInstance();


    @Override
    public void getDateAlbumList(Context context, IDataCallback<List<DateAlbumBean>> callback) {
        mCallback = callback;
        GetDataTask task = new GetDataTask();
        task.execute(context);
    }
    /**
     * 数据根据时间进行排序
     * @param mData
     */
    private void sortList(List<DateAlbumBean> mData) {
        Collections.sort(mData, new Comparator<DateAlbumBean>() {
            @Override
            public int compare(DateAlbumBean o1, DateAlbumBean o2) {
                if (o1.getDate() > o2.getDate()) {
                    return -1;
                } else if (o1.getDate() == o2.getDate()) {
                    return 0;
                }
                return 1;
            }
        });

    }
    private AlbumBean convertFileToAlbumBean(File file) {

//        Date fileDate = DateUtil.parseDate(file);
        long fileTimeMills = DateUtil.pasreFileTimeMills(file);
        // Log.d(TAG, "convertFileToAlbumBean: " + fileDate);
        mCal1.setTimeInMillis(fileTimeMills);
        // 将时分秒,毫秒域清零
        mCal1.set(Calendar.HOUR_OF_DAY, 0);
        mCal1.set(Calendar.MINUTE, 0);
        mCal1.set(Calendar.SECOND, 0);
        mCal1.set(Calendar.MILLISECOND, 0);
        AlbumBean albumBean = new AlbumBean();
        albumBean.setDate(mCal1.getTime().getTime());
        albumBean.setPath( file.getAbsolutePath());
        albumBean.setFile(file);
        long b3 = System.currentTimeMillis();
//        LogUtil.e(TAG, "doInBackground: 步骤2 ms" + (b3 - b2));
        return albumBean;
    }


    class GetDataTask extends AsyncTask<Context,Void,List<DateAlbumBean>>{

        @Override
        protected List<DateAlbumBean> doInBackground(Context... contexts) {
            long begin = System.currentTimeMillis();
            List<DateAlbumBean> mData = new ArrayList<>();
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = contexts[0].getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor == null || cursor.getCount() <= 0) {
                return null; // 没有图片
            }
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(index); // 文件地址
                // Log.d(TAG, "getSystemPhotoList: path = " + path);
                File file = new File(path);
                if (file.exists()) {
                    long b1 = System.currentTimeMillis();
                    AlbumBean albumBean = convertFileToAlbumBean(file);
                    long b2 = System.currentTimeMillis();
//                    LogUtil.e(TAG, "doInBackground: 步骤1 ms" + (b2 - b1));
                    DateAlbumBean DateAlbumBean = new DateAlbumBean();
                    DateAlbumBean.setDate(albumBean.getDate());
                    long b3 = System.currentTimeMillis();
//                    LogUtil.e(TAG, "doInBackground: 步骤2 ms" + (b3 - b2));
                    int index1 = mData.indexOf(DateAlbumBean);
                    long b4 = System.currentTimeMillis();
//                    LogUtil.e(TAG, "doInBackground: 步骤3 ms" + (b4 - b3));
                    if (index1 >= 0) {
                        mData.get(index1).getItemList().add(albumBean);
                    } else {
                        DateAlbumBean.getItemList().add(albumBean);
                        mData.add(DateAlbumBean);
                    }
                    long b5 = System.currentTimeMillis();
//                    LogUtil.e(TAG, "doInBackground: 步骤4 ms" + (b5 - b4));
                }
            }
            long mid = System.currentTimeMillis();
            sortList(mData);
            long end = System.currentTimeMillis();
            LogUtil.e(TAG, "doInBackground: 读取数据ms " + (mid - begin) );
            LogUtil.e(TAG, "doInBackground: 排序时间ms " + (end - mid));
            LogUtil.e(TAG, "doInBackground: 总时间  ms "+(end - begin));
            return mData;
        }

        @Override
        protected void onPostExecute(List<DateAlbumBean> dateAlbumBeanList) {
            mCallback.onSuccess(dateAlbumBeanList);
        }
    }
}
