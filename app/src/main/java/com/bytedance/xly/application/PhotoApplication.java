package com.bytedance.xly.application;

import android.app.Application;

import com.bytedance.xly.util.LogUtil;

import java.io.File;

/*
 * 包名：      com.bytedance.xly.application
 * 文件名：      PhotoApplication
 * 创建时间：      2020/6/19 7:57 PM
 *
 */
public class PhotoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init("xly",false);
    }

}
