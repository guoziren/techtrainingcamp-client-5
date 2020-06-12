package com.bytedance.xly.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bytedance.xly.BigPicture.Main2Activity;
import com.bytedance.xly.R;
import com.bytedance.xly.adapter.DateAlbumAdapter;
import com.bytedance.xly.filetransfer.model.FileSender;
import com.bytedance.xly.filetransfer.model.entity.FileInfo;
import com.bytedance.xly.util.TransferUtil;
import com.bytedance.xly.filetransfer.view.SenderActivity;
import com.bytedance.xly.interfaces.IAdapterListener;
import com.bytedance.xly.interfaces.IDateAlbumListener;
import com.bytedance.xly.model.bean.AlbumBean;
import com.bytedance.xly.model.bean.DateAlbumBean;
import com.bytedance.xly.presenter.impl.AlbumPresenterImpl;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.ToastUtil;
import com.bytedance.xly.view.activity.PhotoActivity;
import com.bytedance.xly.view.view.AlbumBottomMenu;
import com.bytedance.xly.view.view.IDateAlbumViewCallback;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 包名：      com.bytedance.xly.view.fragment
 * 文件名：      AlbumFragment
 * 创建时间：      2020/6/3 10:57 AM
 *
 */
public class AlbumFragment extends Fragment implements IDateAlbumListener, IDateAlbumViewCallback,IAdapterListener<AlbumBean> {
    public static boolean isChooseMode = false;
    private List<DateAlbumBean> choosedCache = new ArrayList<>();
    private static final String TAG = "AlbumFragment";
    public static final String PATHS = "PATHS";
    private List<DateAlbumBean> mData = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private AlbumBottomMenu mBottomMenu;

    private DateAlbumAdapter mAdapter;
    private AlbumPresenterImpl mAlbumPresenter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.rc_list);
        mProgressBar = view.findViewById(R.id.pb_loading);
        mBottomMenu = view.findViewById(R.id.album_menu);

        mAdapter = new DateAlbumAdapter(mData,getContext());

        mAdapter.setListener(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        mAlbumPresenter = new AlbumPresenterImpl();
        mAlbumPresenter.setDateAlbumViewCallback(this);
        mAlbumPresenter.getDateAlbumList(getContext());

        mBottomMenu.setMenuListener(new AlbumBottomMenu.AlubmBottomMenuListener() {
            @Override
            public void onDeleteClick() {
            //    showConfirmDelete();
            }

            @Override
            public void onShareClick() {
                Intent intent = new Intent(getActivity(), SenderActivity.class);
                ArrayList<String> paths = new ArrayList<>();
                if (choosedCache.size() == 0){
                    ToastUtil.showToast(getActivity(), Toast.LENGTH_LONG,"尚未选择图片");
                    return;
                }
                for (DateAlbumBean dateAlbumBean : choosedCache) {
                    for (AlbumBean albumBean : dateAlbumBean.getItemList()) {
                        paths.add(albumBean.getPath());
                    }
                }
                createFileInfo(choosedCache);

                intent.putStringArrayListExtra(PATHS,paths);
                startActivity(intent);
            }
        });

    }

    private void createFileInfo(List<DateAlbumBean> choosedCache) {
        for (DateAlbumBean dateAlbumBean : choosedCache) {
            for (AlbumBean albumBean : dateAlbumBean.getItemList()) {
                FileInfo f = new FileInfo();
                f.setFilePath(albumBean.getPath());
                File file = new File(albumBean.getPath());
                f.setSize(file.length());
                f.setName(f.getName());
                f.setFileType(FileSender.TYPE_FILE);

                TransferUtil.getInstance().addFileInfo(f);
//                AppContext.getAppContext().addFileInfo(f);
            }
        }
    }


    /**
     * 取消选择
     */
    public void cancelChoose() {
        for (int i = 0; i < choosedCache.size(); i++) {
            for (AlbumBean mb : choosedCache.get(i).getItemList()) {
                mb.setChecked(false);
            }
        }
        choosedCache.clear();
        isChooseMode = false;
        onChooseModeChange(false);
        mAdapter.notifyDataSetChanged();
        mBottomMenu.setVisibility(View.GONE);
    }

    /**
     * 进入选择
     */
    public void enterChoose() {
        isChooseMode = true;
        onChooseModeChange(true);
        mAdapter.notifyDataSetChanged();
        mBottomMenu.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadOverrideImage(String path, ImageView iv) {

    }

    @Override
    public void loadImage(String path, ImageView iv) {

    }

    @Override
    public void onChooseModeChange(boolean isChoose) {
        ((PhotoActivity)getActivity()).onChooseModeChange(isChoose);
    }

    @Override
    public void onDateAlbumListLoaded(List<DateAlbumBean> dateAlbumList) {
        LogUtil.d(TAG, "onDateAlbumListLoaded: ");
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mAdapter.setData(dateAlbumList);
    }
    //一个相片缩略图的点击事件
    @Override
    public void onItemClick(AlbumBean albumBean, View v) {
        DateAlbumBean timeBean = new DateAlbumBean();
        timeBean.setDate(albumBean.getDate());
        if (isChooseMode) {
            int index = choosedCache.indexOf(timeBean);
            List<AlbumBean> mbList;
            if (index < 0) {
                //被选中
                mbList = new ArrayList<>();
                mbList.add(albumBean);
                timeBean.setItemList(mbList);
                choosedCache.add(timeBean);
            } else {
                mbList = choosedCache.get(index).getItemList();

                //如果被选中，则添加到缓存中
                if (albumBean.isChecked()) {
                    mbList.add(albumBean);
                } else {
                    mbList.remove(albumBean);
                    if (mbList.size() == 0) {
                        choosedCache.remove(index);
                    }
                }
//                        Log.d(TAG, "onItemClick: choosedCacheSize:"+mbList.size());
            }
        } else {
            int index = mData.indexOf(timeBean);
            DateAlbumBean ab = mData.get(index);
            index = ab.getItemList().indexOf(albumBean);
            if (index >= 0) {
                //start2Preview((ArrayList<AlbumBean>) ab.itemList, index);
                Intent intent = new Intent(getContext(), Main2Activity.class);
                intent.putExtra("picturePath", (Serializable) ab.getItemList());
                intent.putExtra("CurrentPage",index);
                Objects.requireNonNull(getContext()).startActivity(intent);//去激活Main2Activity
            }
        }
    }

    @Override
    public void onItemLongClick(AlbumBean albumBean, View v) {
        enterChoose();
    }

    @Override
    public void onAlubumDelete(AlbumBean albumBean) {

    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
