package com.bytedance.xly.presenter.impl;

import android.content.Context;

import com.bytedance.xly.model.IDataCallback;
import com.bytedance.xly.model.bean.DateAlbumBean;
import com.bytedance.xly.model.impl.DateAlbumModelImpl;
import com.bytedance.xly.model.IDateAlbumModel;
import com.bytedance.xly.presenter.IAlbumPresenter;
import com.bytedance.xly.view.view.IDateAlbumViewCallback;

import java.util.List;

import androidx.annotation.Nullable;

/*
 * 包名：      com.bytedance.xly.presenter
 * 文件名：      AlbumPresenter
 * 创建时间：      2020/6/3 3:44 PM
 *
 */
public class AlbumPresenterImpl implements IAlbumPresenter {
    private IDateAlbumModel mDateAlbumModel;
    private IDateAlbumViewCallback mDateAlbumViewCallback;


    public AlbumPresenterImpl() {
        mDateAlbumModel = new DateAlbumModelImpl();
    }

    public void setDateAlbumViewCallback(IDateAlbumViewCallback dateAlbumViewCallback) {
        mDateAlbumViewCallback = dateAlbumViewCallback;
    }

    @Override
    public void getDateAlbumList(Context context) {
       mDateAlbumModel.getDateAlbumList(context, new IDataCallback<List<DateAlbumBean>>() {
            @Override
            public void onSuccess(@Nullable List<DateAlbumBean> dateAlbumList) {
                mDateAlbumViewCallback.onDateAlbumListLoaded(dateAlbumList);
            }

            @Override
            public void onError(int var1, String var2) {

            }
        });

    }
}
