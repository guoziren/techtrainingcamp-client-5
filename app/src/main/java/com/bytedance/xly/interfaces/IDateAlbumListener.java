package com.bytedance.xly.interfaces;

import android.widget.ImageView;

/*
 * 包名：      com.bytedance.xly.interfaces
 * 文件名：      IDateAlbumListener
 * 创建时间：      2020/6/3 3:02 PM
 *
 */
public interface IDateAlbumListener {
    /**
     * 加载图片，覆盖原来图片的大小
     * 预览多个小图片的时候使用
     *
     * @param path
     * @param iv
     */
    void loadOverrideImage(String path, ImageView iv);

    /**
     * 加载图片
     * 单个图片预览的时候使用
     *
     * @param path
     * @param iv
     */
    void loadImage(String path, ImageView iv);

    /**
     * 选择模式改变
     *
     * @param isChoose
     */
    void onChooseModeChange(boolean isChoose);
}
