package com.bytedance.xly.thumbnail.model;

import android.content.Context;

import com.bytedance.xly.thumbnail.model.bean.DateAlbumBean;

import java.util.List;

/*
 * 包名：      com.bytedance.xly.thumbnail.model.model
 * 文件名：      IDateAlbumModel
 * 创建时间：      2020/6/3 3:52 PM
 *
 */
public interface IDateAlbumModel {
    void getDateAlbumList(Context context,IDataCallback<List<DateAlbumBean>> callback);
}
