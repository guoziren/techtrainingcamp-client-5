package com.bytedance.xly.model.bean;

import android.os.Parcel;
import android.os.Parcelable;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * 包名：      com.bytedance.xly.model.bean
 * 文件名：      TimeBean
 * 创建时间：      2020/6/3 11:10 AM
 *
 */
public class DateAlbumBean   {
    public static final long day = 1000 * 60 * 60 * 24;
    private long date;
    private String dateString;

    private List<AlbumBean> itemList = new ArrayList<>();

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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateAlbumBean) {
            DateAlbumBean ab = (DateAlbumBean) obj;
//            return this.date  == ab.date
            return this.date / day == ab.date / day;
        }
        return super.equals(obj);
    }

}
