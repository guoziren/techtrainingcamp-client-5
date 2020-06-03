package com.bytedance.xly.util;

import android.media.ExifInterface;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 包名：      com.bytedance.xly.util
 * 文件名：      DateUtil
 * 创建时间：      2020/6/3 11:19 AM
 *
 */
public class DateUtil {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
    private static final String TAG = "DateUtil";
    public static Date parseDate(File file) {

        ExifInterface exif = null;
        Date date1 = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String date = exif.getAttribute(ExifInterface.TAG_DATETIME);

       // Log.d(TAG, "parseDate: " + date);
        try {
            if (!TextUtils.isEmpty(date)) {
                date1 = convertToDate(date);
            } else {
//                date1 = DateUtils.convertToDate("1995:03:13 22:38:20");
                date1 = new Date(file.lastModified());
            }
            // Log.i("date", date);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return date1;
    }

    public static String converToString(Object date) {
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd");

        return df.format(date);
    }


    public static Date convertToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
//        return df.parse(strDate);
        try {
            return   df.parse(strDate);
        }catch (ParseException e){
            Date date = new Date();
            date.setTime(Long.parseLong(strDate));

            return date;
        }


    }

    public static String converToString(long timeMillion) {
        DateFormat df = new SimpleDateFormat("yyyy:MM:dd");
        return df.format(timeMillion);
    }
}
