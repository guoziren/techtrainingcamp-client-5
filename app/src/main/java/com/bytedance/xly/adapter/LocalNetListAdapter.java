package com.bytedance.xly.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bytedance.xly.R;
import com.bytedance.xly.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.adapter
 * 文件名：      LocalNetListAdapter
 * 创建时间：      2020/5/30 8:50 AM
 *
 */
public class LocalNetListAdapter extends RecyclerView.Adapter<LocalNetListAdapter.Holder> {
    private static final String TAG = "LocalNetListAdapter";
    private List<String> mData = new ArrayList<>();
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_localnet,parent,false);

        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        LogUtil.d(TAG, "onBindViewHolder: position " + position);
        holder.setData(mData.get(position));
    }
    public void setData(List<String> data){
        if (mData != null){
            mData.clear();
            mData.addAll(data);
        }

        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }
    class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }
        public void setData(String ip){
//            if (!TextUtils.isEmpty(ip)){
                TextView tv_ip = itemView.findViewById(R.id.textView2);
                tv_ip.setText(ip);
//            }
        }
    }
}
