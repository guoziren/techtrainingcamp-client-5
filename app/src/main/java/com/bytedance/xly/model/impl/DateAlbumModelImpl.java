package com.bytedance.xly.model.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import com.bytedance.xly.model.IDataCallback;
import com.bytedance.xly.model.bean.AlbumBean;
import com.bytedance.xly.model.bean.DateAlbumBean;
import com.bytedance.xly.model.IDateAlbumModel;
import com.bytedance.xly.util.DateUtil;
import com.bytedance.xly.util.LogUtil;

import java.io.File;
import java.text.ParseException;
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

       // Date fileDate = DateUtil.parseDate(file);
        long fileTimeMills = DateUtil.pasreFileTimeMills(file);
       //  Log.d(TAG, "convertFileToAlbumBean: " + fileDate);
        mCal1.setTimeInMillis(fileTimeMills);
        // 将时分秒,毫秒域清零
        mCal1.set(Calendar.HOUR_OF_DAY, 0);
        mCal1.set(Calendar.MINUTE, 0);
        mCal1.set(Calendar.SECOND, 0);
        mCal1.set(Calendar.MILLISECOND, 0);
        AlbumBean albumBean = new AlbumBean();
        albumBean.setDate(mCal1.getTime().getTime());
        albumBean.setPath( file.getAbsolutePath());
        long b3 = System.currentTimeMillis();
     //  LogUtil.e(TAG, "doInBackground: 步骤2 ms" + (b3 - b2));
        return albumBean;
    }
    private AlbumBean convertFileToAlbumBean(File file,String dataAdded) {

        long fileTimeMills = Long.parseLong(dataAdded);
        mCal1.setTimeInMillis(fileTimeMills);
        // 将时分秒,毫秒域清零
        mCal1.set(Calendar.HOUR_OF_DAY, 0);
        mCal1.set(Calendar.MINUTE, 0);
        mCal1.set(Calendar.SECOND, 0);
        mCal1.set(Calendar.MILLISECOND, 0);
        AlbumBean albumBean = new AlbumBean();
        albumBean.setDate(mCal1.getTime().getTime());
        albumBean.setPath( file.getAbsolutePath());
        return albumBean;
    }
    private AlbumBean convertFileToAlbumBean(String path,String thumbnailPath,String dataAdded) {

        long fileTimeMills = Long.parseLong(dataAdded);
        mCal1.setTimeInMillis(fileTimeMills);
        // 将时分秒,毫秒域清零
        mCal1.set(Calendar.HOUR_OF_DAY, 0);
        mCal1.set(Calendar.MINUTE, 0);
        mCal1.set(Calendar.SECOND, 0);
        mCal1.set(Calendar.MILLISECOND, 0);
        AlbumBean albumBean = new AlbumBean();
        albumBean.setDate(mCal1.getTime().getTime());
        albumBean.setPath(path);
        albumBean.setThumbPath(thumbnailPath);
        return albumBean;
    }




    class GetDataTask extends AsyncTask<Context,Void,List<DateAlbumBean>>{

        @Override
        protected List<DateAlbumBean> doInBackground(Context... contexts) {
            long begin = System.currentTimeMillis();
            List<DateAlbumBean> mData = new ArrayList<>();
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = contexts[0].getContentResolver();
            String[] projection = new String[]{MediaStore.Images.Media.DATA,MediaStore.Images.Media.DATE_TAKEN,MediaStore.Images.Thumbnails.DATA};
//            Cursor cursor = contentResolver.query(uri, projection, null, null, null);
            Cursor cursor = contentResolver.query(uri, projection, null, null, MediaStore.Images.Media.DATE_TAKEN+" desc");
            if (cursor == null || cursor.getCount() <= 0) {
                return null; // 没有图片
            }
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(projection[0]);
                int index_data_taken = cursor.getColumnIndexOrThrow(projection[1]);
                int index_data_thumbnails = cursor.getColumnIndexOrThrow(projection[2]);
                String path = cursor.getString(index); // 文件地址
                String data_taken = cursor.getString(index_data_taken); // 文件添加时间
                String data_thumbnails = cursor.getString(index_data_thumbnails); // 文件缩略图地址
                // Log.d(TAG, "getSystemPhotoList: path = " + path);
                File file = new File(path);
                if (file.exists()) {
<<<<<<< HEAD
//                    AlbumBean albumBean = convertFileToAlbumBean(file);
                    AlbumBean albumBean = convertFileToAlbumBean(path,data_thumbnails,data_taken);
                    DateAlbumBean DateAlbumBean = new DateAlbumBean();
                    DateAlbumBean.setDate(albumBean.getDate());
                    int index1 = mData.indexOf(DateAlbumBean);
                    if (index1 >= 0) {
                        mData.get(index1).getItemList().add(albumBean);
                    } else {
                        DateAlbumBean.getItemList().add(albumBean);
                        mData.add(DateAlbumBean);
                    }
                }
            }
           long mid = System.currentTimeMillis();
=======
                    AlbumBean albumBean = new AlbumBean();
                    albumBean.setThumbPath(data_thumbnails);
                    if (data_taken == null){
                        continue;
                    }

                    long date = Long.parseLong(data_taken);
                    albumBean.setDate(date);
                    albumBean.setPath(path);
//                    DateAlbumBean datealbumbean = new DateAlbumBean();
//                    datealbumbean.setDate(albumBean.getDate());
//                    int index1 = mData.indexOf(datealbumbean);
//                    if (index1 >= 0) {
//                        mData.get(index1).getItemList().add(albumBean);
//                    } else {
//                        datealbumbean.getItemList().add(albumBean);
//                        mData.add(datealbumbean);
//                    }
                    if (date / DateAlbumBean.day != before / DateAlbumBean.day){
                        if (before != -1){
                            mData.add(datealbumbean);
                        }
                        before = date;
                        //日期不一样的时候之前一天的存入mData,并new DateAlbumBean
                        datealbumbean = new DateAlbumBean();
                        datealbumbean.setDate(date);
                        datealbumbean.getItemList().add(albumBean);
                    }else{
                       datealbumbean.getItemList().add(albumBean);
                    }
                }
            }
            mData.add(datealbumbean);
>>>>>>> ad2b7fdffd0f1f004f67e13a651faffad087c843
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
