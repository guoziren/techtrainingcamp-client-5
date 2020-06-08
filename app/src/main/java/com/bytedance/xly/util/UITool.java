package com.bytedance.xly.util;

import android.content.Context;

/*
 * 包名：      com.bytedance.xly.util
 * 文件名：      UITool
 * 创建时间：      2020/6/2 6:23 PM
 *
 */
public class UITool {
    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
