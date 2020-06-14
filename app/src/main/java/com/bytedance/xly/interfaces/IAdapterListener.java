package com.bytedance.xly.interfaces;

import android.view.View;

/*
 * 包名：      com.bytedance.xly.interfaces
 * 文件名：      IAdapterListener
 * 创建时间：      2020/6/3 3:23 PM
 *
 */
public interface IAdapterListener<T> {

    void onItemClick(T t, View v);

    void onItemLongClick(T t, View v);

    void onAlubumDelete(T t);
}
