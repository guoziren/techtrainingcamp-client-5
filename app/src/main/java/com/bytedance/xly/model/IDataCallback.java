package com.bytedance.xly.model;

import androidx.annotation.Nullable;

/*
 * 包名：      com.bytedance.xly.model
 * 文件名：      IDataCallback
 * 创建时间：      2020/6/3 4:50 PM
 *
 */
public interface IDataCallback<T> {

    void onSuccess(@Nullable T var1);

    void onError(int var1, String var2);

}
