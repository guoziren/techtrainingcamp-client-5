package com.bytedance.xly.thumbnail.model.impl;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.bytedance.xly.thumbnail.model.IDataCallback;
import com.bytedance.xly.thumbnail.model.bean.AlbumBean;
import com.bytedance.xly.thumbnail.model.bean.DateAlbumBean;
import com.bytedance.xly.thumbnail.model.IDateAlbumModel;
import com.bytedance.xly.util.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/*
 * 包名：      com.bytedance.xly.thumbnail.model.impl
 * 文件名：      DateAlbumModelImpl
 * 创建时间：      2020/6/3 3:53 PM
 *
 */
public class DateAlbumModelImpl  implements IDateAlbumModel {
    private static final String TAG = "DateAlbumModelImpl";
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
                if (o1.getDate() > o2.getDate()) {
                    return -1;
                } else if (o1.getDate() == o2.getDate()) {
                    return 0;
                }
                return 1;
            }
        });

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
            long before = -1;
            DateAlbumBean datealbumbean = null;
            LogUtil.d(TAG, "cursor size: " + cursor.getCount());
            int count = 0;
            while (cursor.moveToNext()) {
                int index = cursor.getColumnIndexOrThrow(projection[0]);
                int index_data_taken = cursor.getColumnIndexOrThrow(projection[1]);
                int index_data_thumbnails = cursor.getColumnIndexOrThrow(projection[2]);
                String path = cursor.getString(index); // 图片路径
                String data_taken = cursor.getString(index_data_taken); // 图片添加时间
                String data_thumbnails = cursor.getString(index_data_thumbnails); // 图片缩略图路径
                // Log.d(TAG, "getSystemPhotoList: path = " + path);
                File file = new File(path);
                File file_thumb = new File(data_thumbnails);

                if (file.exists() && file_thumb.exists()) {
                    AlbumBean albumBean = new AlbumBean();
                    albumBean.setThumbPath(data_thumbnails);
                    if (data_taken == null){
                        continue;
                    }

                    long date = Long.parseLong(data_taken);
                    albumBean.setDate(date);
                    albumBean.setPath(path);

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
            sortList(mData);
            return mData;
        }

        @Override
        protected void onPostExecute(List<DateAlbumBean> dateAlbumBeanList) {
            mCallback.onSuccess(dateAlbumBeanList);
        }
    }
}
