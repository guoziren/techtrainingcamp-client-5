package com.bytedance.xly.util;

import android.content.Context;
import android.widget.Toast;

/*
 * 包名：      com.bytedance.xly.util
 * 文件名：      ToastUtil
 * 创建时间：      2020/6/6 4:34 PM
 *
 */
public class ToastUtil {
    /* private Context context;
  public ToastUtil(Context context) {
  this.context=context;
  }*/
    private static Toast toast;
    public static void showToast(Context context, int code, String content)
    {
        //code=1时Toast显示的时间长，code=0时显示的时间短。
        if (toast==null)
        {
            if (code ==0)
                toast=Toast.makeText(context,content,Toast.LENGTH_SHORT);
            if (code==1)
                toast=Toast.makeText(context,content,Toast.LENGTH_LONG);
        }
        else
        {
            toast.setText(content);
        }
        toast.show();
    }
}
