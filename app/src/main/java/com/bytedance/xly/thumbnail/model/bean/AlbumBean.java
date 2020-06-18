package com.bytedance.xly.thumbnail.model.bean;

import java.io.Serializable;
import java.util.Objects;

/*
 * 包名：      com.bytedance.xly.thumbnail.model.bean
 * 文件名：      AlbumBean
 * 创建时间：      2020/6/3 10:42 AM
 *
 */
public class AlbumBean implements Serializable {
    private String path;//原图路径
    private String thumbPath;//缩略图路径
    private long date;//图片日期
    private boolean isChecked;//是否被选中

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

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
