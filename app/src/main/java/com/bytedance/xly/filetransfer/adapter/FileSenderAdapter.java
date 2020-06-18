package com.bytedance.xly.filetransfer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.xly.R;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.FileUtils;
import com.bytedance.xly.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.filetransfer.adapter
 * 文件名：      FileSenderAdapter
 * 创建时间：      2020/6/11 9:18 PM
 *
 */
public class FileSenderAdapter extends RecyclerView.Adapter<FileSenderAdapter.Holder> {
    private static final String TAG = "FileSenderAdapter";
    private List<FileInfo> mData = new ArrayList();

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transfer, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.setData(mData.get(position));
    }

    public void setDataAndNotify(List<FileInfo> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView iv_shortcut;
        TextView tv_name;
        TextView tv_progress;
        ProgressBar pb_file;

        Button btn_operation;
        ImageView iv_tick;

        public Holder(@NonNull View itemView) {
            super(itemView);
            iv_shortcut = (ImageView) itemView.findViewById(R.id.iv_shortcut);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_progress = (TextView) itemView.findViewById(R.id.tv_progress);
            pb_file = (ProgressBar) itemView.findViewById(R.id.pb_file);
            iv_tick = (ImageView) itemView.findViewById(R.id.iv_tick);
        }

        public void setData(FileInfo fileInfo) {
            if (fileInfo != null) {
                //初始化
                pb_file.setVisibility(View.VISIBLE);
                iv_tick.setVisibility(View.GONE);
                Glide.with(iv_shortcut)
                        .load(R.mipmap.icon_jpg)
                        .centerCrop()
                        .into(iv_shortcut);
                tv_name.setText(FileUtils.getFileName(fileInfo.getFilePath()));
                if (fileInfo.getResult() == FileInfo.FLAG_SUCCESS) { //文件传输成功
                  //  LogUtil.d(TAG, "setData: 文件传输成功" + fileInfo.getFilePath());
                    long total = fileInfo.getSize();
                    pb_file.setVisibility(View.GONE);
                    tv_progress.setText(FileUtils.getFileSize(total) + "/" + FileUtils.getFileSize(total));

                    iv_tick.setVisibility(View.VISIBLE);

                } else if (fileInfo.getResult() == FileInfo.FLAG_FAILURE) { //文件传输失败
                    pb_file.setVisibility(View.GONE);
                    tv_progress.setText("网络发生了异常，未传输成功");
                } else {
                    //文件传输中
                    long progress = fileInfo.getProcceed();
                    long total = fileInfo.getSize();
                    tv_progress.setText(FileUtils.getFileSize(progress) + "/" + FileUtils.getFileSize(total));
                    int percent = (int) (progress * 100 / total);
                    pb_file.setMax(100);
                    pb_file.setProgress(percent);

                }
            }
        }
    }
}
