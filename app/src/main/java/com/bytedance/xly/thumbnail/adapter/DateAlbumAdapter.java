package com.bytedance.xly.thumbnail.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.thumbnail.interfaces.IAdapterListener;
import com.bytedance.xly.thumbnail.model.bean.AlbumBean;
import com.bytedance.xly.thumbnail.model.bean.DateAlbumBean;
import com.bytedance.xly.util.DateUtil;
import com.bytedance.xly.util.UITool;
import com.bytedance.xly.thumbnail.view.GridDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.adapter
 * 文件名：      DateAlbumAdapter
 * 创建时间：      2020/6/3 11:04 AM
 *
 */
public class DateAlbumAdapter extends RecyclerView.Adapter<DateAlbumAdapter.Holder> {
    private static final String TAG = "DateAlbumAdapter";
    private List<DateAlbumBean> mData = new ArrayList<>();
    private Context mContext;
    private IAdapterListener<AlbumBean> listener;

    public void setListener(IAdapterListener<AlbumBean> listener) {
        this.listener = listener;
    }

    public DateAlbumAdapter(List<DateAlbumBean> data,Context context) {
        mData = data;
        mContext = context;
    }
    public void setData(List<DateAlbumBean> data){
        if (mData != null && data != null){
            mData.clear();
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setData(mData.get(position));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
        public void setData(DateAlbumBean dateAlbumBean){
            if (dateAlbumBean == null){
                return;
            }
            TextView tv_date = itemView.findViewById(R.id.tv_date);
            tv_date.setText(DateUtil.converToString(dateAlbumBean.getDate()));

            RecyclerView recyclerView = itemView.findViewById(R.id.rc_list);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,4);
            recyclerView.setLayoutManager(gridLayoutManager);
            int space = UITool.dip2px(mContext,2);
            //外层recyclerview滑动时，嵌套的recyclerview会多次addItemDecoration导致间隙会逐渐变大
            if (recyclerView.getItemDecorationCount() == 0) {
                recyclerView.addItemDecoration(new GridDecoration(4,space,true ));
            }

            AlbumAdapter albumAdapter = new AlbumAdapter(dateAlbumBean.getItemList(),gridLayoutManager);
            albumAdapter.setSpace(space);
            albumAdapter.setListener(listener);
            recyclerView.setAdapter(albumAdapter);
        }
    }

}
