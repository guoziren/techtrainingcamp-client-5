package com.bytedance.xly.thumbnail.view;

import com.bytedance.xly.thumbnail.model.bean.DateAlbumBean;

import java.util.List;

/*
 * 包名：      com.bytedance.xly.view.view
 * 文件名：      IDateAlbumViewCallback
 * 创建时间：      2020/6/3 4:08 PM
 *
 */
public interface IDateAlbumViewCallback {
    void onDateAlbumListLoaded(List<DateAlbumBean> dateAlbumList);
}
