package com.bytedance.xly.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bytedance.xly.R;
import com.bytedance.xly.interfaces.IAdapterListener;
import com.bytedance.xly.model.bean.AlbumBean;
import com.bytedance.xly.view.fragment.AlbumFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.adapter
 * 文件名：      AlbumAdapter
 * 创建时间：      2020/6/3 10:36 AM
 *
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.Holder> {
    private static final String TAG = "AlbumAdapter";
    private List<AlbumBean> mData ;
    private GridLayoutManager mGridLayoutManager;

    public void setListener(IAdapterListener<AlbumBean> listener) {
        this.listener = listener;
    }

    private IAdapterListener<AlbumBean> listener;
    private int space = 0;//gridview  item的间隙

    public void setSpace(int space) {
        this.space = space;
    }

    public AlbumAdapter(List<AlbumBean> data, GridLayoutManager gridLayoutManager) {

        mData = data;
        mGridLayoutManager = gridLayoutManager;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData != null){
            return mData.size();
        }
        return 0;
    }

     class Holder extends RecyclerView.ViewHolder{
         private CheckBox mCheckbox;
         private ImageView mIvFlag;
         private ImageView mIvThumb;;
         private ImageView mIvPlay;
         private View mMask;
        public Holder(@NonNull View itemView) {
            super(itemView);
        }
        public void bind(AlbumBean albumBean){
            mCheckbox = itemView.findViewById(R.id.cb_check);
            setChooseStyle(AlbumFragment.isChooseMode);
            //缩略图
            mIvThumb = itemView.findViewById(R.id.iv_thumb);
            ViewGroup.LayoutParams ivThumbLayoutParams = mIvThumb.getLayoutParams();
            int width = (mGridLayoutManager.getWidth() - mGridLayoutManager.getPaddingLeft() - mGridLayoutManager.getPaddingRight()
                    - 3 * space)/ mGridLayoutManager.getSpanCount();

            ivThumbLayoutParams.height = width;
            ivThumbLayoutParams.width = width;
            mIvThumb.setLayoutParams(ivThumbLayoutParams);
            loadOverrideImage(albumBean.path,mIvThumb,width);

            //  LogUtil.d(TAG, "bind: 图片width " + ivThumbLayoutParams.width + " height " + ivThumbLayoutParams.height);

            //视频缩略图,暂时不用
            mIvPlay = itemView.findViewById(R.id.iv_play);

            mMask = itemView.findViewById(R.id.v_mask);
            mIvFlag = itemView.findViewById(R.id.ivFlag);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AlbumFragment.isChooseMode) {
                        albumBean.setChecked(!albumBean.isChecked);
                        setChecked(albumBean.isChecked);
                    }else if (listener != null) {
                        listener.onItemClick(albumBean, v);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (AlbumFragment.isChooseMode) {
                        return false;
                    }
                    if (listener != null && !isCheckMode()) {
                        listener.onItemLongClick(albumBean, v);
                    }
                    return true;
                }
            });
            setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null&& AlbumFragment.isChooseMode) {
                        listener.onItemClick(albumBean, buttonView);
                    }
                }
            });

        }
         public void setChooseStyle(boolean choose) {
            mCheckbox.setVisibility(choose ? View.VISIBLE : View.GONE);
         }
         public boolean isCheckMode(){
             return mCheckbox.getVisibility() == View.VISIBLE;

         }
         public void setChecked(boolean checked) {
             mCheckbox.setChecked(checked);
         }
         public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener listener){
             mCheckbox.setOnCheckedChangeListener(listener);
         }

    }
    public void loadOverrideImage(String path, ImageView iv) {
        Glide.with(iv)
                .load(path)
                .thumbnail(0.1f)
                .apply(buildOptions())
                .into(iv);
    }
    public void loadOverrideImage(String path, ImageView iv,int size) {
        Glide.with(iv)
                .load(path)
                .thumbnail(0.1f)
                .apply(buildOptions(size))
                .into(iv);
    }
    public  RequestOptions buildOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(100, 100);
//        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        return requestOptions;
    }
    public  RequestOptions buildOptions(int size) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(size, size);
//        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        return requestOptions;
    }
    public void loadImage(String path, ImageView iv) {
        Glide.with(iv)
                .load(path)
                .thumbnail(0.1f)
                .into(iv);
    }
}
