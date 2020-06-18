package com.bytedance.xly.thumbnail.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bytedance.xly.bigpicture.Main2Activity;
import com.bytedance.xly.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.adapter
 * 文件名：      MainAdapter
 * 创建时间：      2020/5/22 8:24 PM
 *
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.Holder> {
    private static final String TAG = "MainAdapter";
    private Context mContext;
    private List<String> mStrings;
    public MainAdapter(Context context,List<String> strings){
        this.mContext = context;
        this.mStrings = strings;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_page,parent,false);
        final Holder holder = new Holder(view);
        //我修改的地方，用到lambda表达式，得用JDK8。
        holder.imageView1.setOnClickListener((v)->{actviteMain2(holder.getAdapterPosition() * 4);});
        holder.imageView2.setOnClickListener((v)->{actviteMain2(holder.getAdapterPosition() * 4 + 1);});
        holder.imageView3.setOnClickListener((v)->{actviteMain2(holder.getAdapterPosition() * 4 + 2);});
        holder.imageView4.setOnClickListener((v)->{actviteMain2(holder.getAdapterPosition() * 4 + 3);});
        return holder;
    }
    private void actviteMain2(int position_num){
        Intent intent = new Intent(mContext, Main2Activity.class);
        intent.putStringArrayListExtra("picturePath", (ArrayList<String>) mStrings);
        intent.putExtra("CurrentPage",position_num);
        mContext.startActivity(intent);//去激活Main2Activity
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");

        if (position * 4 + 3 < mStrings.size()){
            Glide.with(mContext).load(new File(mStrings.get(position * 4 + 0))).into(holder.imageView1);
            Glide.with(mContext).load(new File(mStrings.get(position * 4 + 1))).into(holder.imageView2);
            Glide.with(mContext).load(new File(mStrings.get(position * 4 + 2))).into(holder.imageView3);
            Glide.with(mContext).load(new File(mStrings.get(position * 4 + 3))).into(holder.imageView4);
        }

    }

    @Override
    public int getItemCount() {
        return mStrings.size() / 4;
    }
    class Holder extends RecyclerView.ViewHolder{
        ImageView imageView1;
        ImageView imageView2;
        ImageView imageView3;
        ImageView imageView4;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.imageView);
            imageView2 = itemView.findViewById(R.id.imageView2);
            imageView3 = itemView.findViewById(R.id.imageView3);
            imageView4 = itemView.findViewById(R.id.imageView4);
        }
    }
}
