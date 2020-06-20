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
    private static final String TAG = "DateUtil";
    private static DateFormat sFormat1 = new SimpleDateFormat("yyyy:MM:dd");
    private static DateFormat sFormat2 = new SimpleDateFormat("yyyy年MM月dd日");
    ;

    public static Date parseDate(File file) throws ParseException {
        ExifInterface exif = null;
        Date date1 = null;
        String date = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
            date = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Log.d(TAG, "parseDate: " + date);

        if (!TextUtils.isEmpty(date)) {
            date1 = convertToDate(date);
        } else {
//                date1 = DateUtils.convertToDate("1995:03:13 22:38:20");
            date1 = new Date(file.lastModified());
        }
        return date1;
    }
    public static long pasreFileTimeMills(File file) {
        ExifInterface exif = null;
        long date1 = 0;
        String date = null;
        try {
            exif = new ExifInterface(file.getAbsolutePath());
            date = exif.getAttribute(ExifInterface.TAG_DATETIME);
        } catch (IOException e) {
            e.printStackTrace();
        }
//         Log.e(TAG, "parseDate: " + date);

        if (!TextUtils.isEmpty(date)) {
            date1 = convertToTimeMills(date);
        } else {
//                date1 = DateUtils.convertToDate("1995:03:13 22:38:20");
            date1 = file.lastModified();
        }
        return date1;
    }

    public static String converToString(Object date) {
        return sFormat1.format(date);
    }


    public static Date convertToDate(String strDate) throws ParseException {
        try {
            return sFormat1.parse(strDate);
        } catch (ParseException e) {
             return  new Date(Long.parseLong(strDate));
        }
    }
    public static long convertToTimeMills(String strDate) {
        try {
            return sFormat1.parse(strDate).getTime();
        } catch (ParseException e) {
            return Long.parseLong(strDate);
        }
    }

    public static String converToString(long timeMillion) {
        return sFormat2.format(timeMillion);
    }


}
