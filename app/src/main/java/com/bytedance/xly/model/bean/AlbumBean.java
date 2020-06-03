package com.bytedance.xly.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * 包名：      com.bytedance.xly.model.bean
 * 文件名：      AlbumBean
 * 创建时间：      2020/6/3 10:42 AM
 *
 */
public class AlbumBean implements Parcelable {
    public String path;
    public long date;
    public String dateString;
    public boolean isChecked;

    public AlbumBean() {

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

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    protected AlbumBean(Parcel in) {
        path = in.readString();
        date = in.readLong();
        dateString = in.readString();
        isChecked = in.readByte() != 0;
    }

    public static final Creator<AlbumBean> CREATOR = new Creator<AlbumBean>() {
        @Override
        public AlbumBean createFromParcel(Parcel in) {
            return new AlbumBean(in);
        }

        @Override
        public AlbumBean[] newArray(int size) {
            return new AlbumBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(date);
        dest.writeString(dateString);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AlbumBean){
            AlbumBean albumBean = (AlbumBean) obj;
            return albumBean.path.equals(path);
        }
        return super.equals(obj);
    }
}
