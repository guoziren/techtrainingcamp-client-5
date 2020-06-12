package com.bytedance.xly.filetransfer.adapter;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.filetransfer.adapter
 * 文件名：      ReceiveFileAdapter
 * 创建时间：      2020/6/12 9:36 AM
 *
 */
public class ReceiveFileAdapter extends RecyclerView.Adapter<ReceiveFileAdapter.Holder> {
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class Holder extends RecyclerView.ViewHolder{
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
        btn_operation = (Button) itemView.findViewById(R.id.btn_operation);
        iv_tick = (ImageView) itemView.findViewById(R.id.iv_tick);
    }
    public void setData(FileInfo fileInfo){
        if(fileInfo != null){
            //初始化
            pb_file.setVisibility(View.VISIBLE);
            iv_tick.setVisibility(View.GONE);

            Glide.with(iv_shortcut)
                    .load(fileInfo.getFilePath())
                    .centerCrop()
                    .placeholder(R.mipmap.icon_jpg)
//                        .crossFade()
                    .into(iv_shortcut);


            tv_name.setText(FileUtils.getFileName(fileInfo.getFilePath()));


            if(fileInfo.getResult() == FileInfo.FLAG_SUCCESS){ //文件传输成功
                long total = fileInfo.getSize();
                pb_file.setVisibility(View.GONE);
                tv_progress.setText(FileUtils.getFileSize(total) + "/" + FileUtils.getFileSize(total));

                btn_operation.setVisibility(View.INVISIBLE);
                iv_tick.setVisibility(View.VISIBLE);

            }else if(fileInfo.getResult() == FileInfo.FLAG_FAILURE) { //文件传输失败
                pb_file.setVisibility(View.GONE);
            }else{//文件传输中
                long progress = fileInfo.getProcceed();
                long total = fileInfo.getSize();
                tv_progress.setText(FileUtils.getFileSize(progress) + "/" + FileUtils.getFileSize(total));

                int percent = (int)(progress *  100 / total);
                pb_file.setMax(100);
                pb_file.setProgress(percent);

                //TODO 传输过程中取消的问题
//                    btn_operation.setText(mContext.getString(R.string.str_cancel));
                btn_operation.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //可否通过广播来实现？
                    }
                });
            }
        }
    }
}
}
