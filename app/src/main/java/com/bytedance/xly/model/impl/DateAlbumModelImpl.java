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
    private IDataCallback<List<DateAlbumBean>> mCallback;


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
                if (o1.date > o2.date) {
                    return -1;
                } else if (o1.date == o2.date) {
                    return 0;
                }
                return 1;
            }
        });

    }
    private AlbumBean convertFileToAlbumBean(File file) {
        Calendar cal1 = Calendar.getInstance();
        Date fileDate = DateUtil.parseDate(file);
        // Log.d(TAG, "convertFileToAlbumBean: " + fileDate);
        cal1.setTime(fileDate);
        // 将时分秒,毫秒域清零
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        AlbumBean albumBean = new AlbumBean();
        albumBean.setDate(cal1.getTime().getTime());
        albumBean.setPath( file.getAbsolutePath());
        albumBean.setFile(file);
        return albumBean;
    }


    class GetDataTask extends AsyncTask<Context,Void,List<DateAlbumBean>>{

        @Override
        protected List<DateAlbumBean> doInBackground(Context... contexts) {
            List<DateAlbumBean> mData = new ArrayList<>();
            // List<String> result = new ArrayList<String>();
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
                    // result.add(path);
                    //  Log.i(TAG, path);
                    AlbumBean albumBean = convertFileToAlbumBean(file);
                    albumBean.dateString = DateUtil.converToString(albumBean.date);
                    DateAlbumBean DateAlbumBean = new DateAlbumBean();
                    DateAlbumBean.setDate(albumBean.date);
                    DateAlbumBean.setDateString(albumBean.dateString);
                    int index1 = mData.indexOf(DateAlbumBean);
                    if (index1 >= 0) {
                        mData.get(index1).itemList.add(albumBean);
                    } else {
                        DateAlbumBean.itemList.add(albumBean);
                        mData.add(DateAlbumBean);
                    }
                }
            }
            sortList(mData);
            return mData;
        }

        @Override
        protected void onPostExecute(List<DateAlbumBean> dateAlbumBeanList) {
            mCallback.onSuccess(dateAlbumBeanList);
        }
    }
}
