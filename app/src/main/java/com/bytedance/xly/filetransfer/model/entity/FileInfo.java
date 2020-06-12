package com.bytedance.xly.filetransfer.model.entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.annotation.Nullable;

/*
 * 包名：      com.bytedance.xly.filetransfer.model.entity
 * 文件名：      FileInfo
 * 创建时间：      2020/6/11 9:25 PM
 *
 */
public class FileInfo {
    /**
     * 文件传输的标识
     */
// 1 成功  -1 失败
    public static final int FLAG_SUCCESS = 1;
    public static final int FLAG_DEFAULT = 0;
    public static final int FLAG_FAILURE = -1;

    //必要属性
    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型
     */
    private int fileType;

    /**
     * 文件大小
     */
    private long size;
    /**
     * 文件传送的结果
     */
    private int result;
    /**
     * 已经处理的（读或者写）
     */
    private long procceed;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static FileInfo toObject(String jsonStr) {
        FileInfo fileInfo = new FileInfo();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String filePath = jsonObject.getString("filePath");
            long size = jsonObject.getLong("size");
            int type = jsonObject.getInt("fileType");
            fileInfo.setFilePath(filePath);
            fileInfo.setSize(size);
            fileInfo.setFileType(type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fileInfo;
    }

    public long getProcceed() {
        return procceed;
    }

    public void setProcceed(long procceed) {
        this.procceed = procceed;
    }

    public FileInfo(){

    }

    public FileInfo(String filePath, long size) {
        this.filePath = filePath;
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
    public static String toJsonStr(FileInfo f){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("filePath",f.getFilePath());
            jsonObject.put("size",f.getSize());
            jsonObject.put("fileType",f.getFileType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileInfo fileInfo = (FileInfo) o;
        return Objects.equals(filePath, fileInfo.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filePath);
    }
}
