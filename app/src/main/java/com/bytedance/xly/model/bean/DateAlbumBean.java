package com.bytedance.xly.model.bean;

import android.os.Parcel;
import android.os.Parcelable;



import java.util.ArrayList;
import java.util.List;

/*
 * 包名：      com.bytedance.xly.model.bean
 * 文件名：      TimeBean
 * 创建时间：      2020/6/3 11:10 AM
 *
 */
public class DateAlbumBean implements Parcelable {
    public long date;
    public String dateString;
    public List<AlbumBean> itemList = new ArrayList<>();

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

    public List<AlbumBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<AlbumBean> itemList) {
        this.itemList = itemList;
    }
    public DateAlbumBean(){

    }
    public DateAlbumBean(Parcel in) {
        date = in.readLong();
        dateString = in.readString();
        itemList = in.createTypedArrayList(AlbumBean.CREATOR);
    }

    public static final Creator<DateAlbumBean> CREATOR = new Creator<DateAlbumBean>() {
        @Override
        public DateAlbumBean createFromParcel(Parcel in) {
            return new DateAlbumBean(in);
        }

        @Override
        public DateAlbumBean[] newArray(int size) {
            return new DateAlbumBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date);
        dest.writeString(dateString);
        dest.writeTypedList(itemList);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateAlbumBean) {
            DateAlbumBean ab = (DateAlbumBean) obj;
            return this.date == ab.date;
        }
        return super.equals(obj);
    }
}
