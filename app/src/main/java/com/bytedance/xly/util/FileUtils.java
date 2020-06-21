package com.bytedance.xly.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by mayubao on 2016/11/12.
 * Contact me 345269374@qq.com
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    /**
     * 默认的根目录
     */
    public static final String DEFAULT_ROOT_PATH = "/mnt/download/chakanqi/";

    /**
     * 默认的缩略图目录
     */

    /**
     * 小数的格式化
     */
    public static final DecimalFormat FORMAT = new DecimalFormat("####.##");

    /**
     * 根据文件路径获取文件的名称
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath){
        if(filePath == null || filePath.equals("")) return "";
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }

    /**
     * 获取文件的根目录
     * @return
     */
    public static String getRootDirPath(){
        String path = DEFAULT_ROOT_PATH;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            path = Environment.getExternalStorageDirectory().getPath() + "/chakanqi/";
        }
        return path;
    }

    /**
     * Bitmap 写入到SD卡
     *
     * @param bitmap
     * @param resPath
     * @return
     */
    public static String bitmapToSDCard(Bitmap bitmap, String resPath){
        if(bitmap == null){
            return "";
        }
        File resFile = new File(resPath);
        try {
            FileOutputStream fos = new FileOutputStream(resFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return resPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 生成本地文件路径
     *
     * @param url
     * @return
     */
    public static File gerateLocalFile(String url){
        String fileName = getFileName(url);
        String dirPath =  getRootDirPath();
        File dirFile = new File(dirPath);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        File file = new File(dirFile, fileName);

        return file;
    }



    /**
     * 根据传入的byte数量转换为对应的byte, Kbyte, Mbyte, Gbyte单位的字符串
     *
     * @param size byte数量
     * @return
     */
    public static String getFileSize(long size){
        if(size < 0){ //小于0字节则返回0
            return "0B";
        }

        double value = 0f;
        if((size / 1024) < 1){ //0 ` 1024 byte
            return  size + "B";
        }else if((size / (1024 * 1024)) < 1){//0 ` 1024 kbyte

            value = size / 1024f;
            return  FORMAT.format(value) + "KB";
        }else if(size / (1024 * 1024 * 1024) < 1){                  //0 ` 1024 mbyte
            value = (size*100 / (1024 * 1024)) / 100f ;
            return  FORMAT.format(value) + "MB";
        }else {                  //0 ` 1024 mbyte
            value = (size * 100l / (1024l * 1024l  * 1024l) ) / 100f ;
            return  FORMAT.format(value) + "GB";
        }
    }
}
