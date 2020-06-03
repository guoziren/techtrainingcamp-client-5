package com.bytedance.xly.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Objects;

/*
 * 包名：      com.bytedance.xly.model.bean
 * 文件名：      AlbumBean
 * 创建时间：      2020/6/3 10:42 AM
 *
 */
public class AlbumBean   {
    public String path;
    public long date;
    public String dateString;
    private File file;
    public boolean isChecked;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumBean albumBean = (AlbumBean) o;
        return path.equals(albumBean.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
