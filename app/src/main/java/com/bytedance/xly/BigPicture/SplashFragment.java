package com.bytedance.xly.BigPicture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bytedance.xly.R;

public class SplashFragment extends Fragment {

    private ImageView mIvContent;
    private String mResId;
    private static final String BUNDLE_KEY_RES_ID = "bundle_key_res_id";

    public static SplashFragment newInstance(String resId){
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_RES_ID,resId);

        SplashFragment fragment = new SplashFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null){
            mResId = arguments.getString(BUNDLE_KEY_RES_ID);
        }
    }

    @Nullable
    //初始化Layout
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash,container,false);
    }
    
    //通过根View为内部的View赋值
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIvContent = view.findViewById(R.id.iv_content);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds =true;
        Bitmap bitmap = BitmapFactory.decodeFile(mResId, options);
        int Weight = options.outWidth;
        int Height = options.outHeight;
        DisplayMetrics dm2 = getResources().getDisplayMetrics();
        int WeightRatio = Math.round((float)Weight/dm2.widthPixels);
        int HeightRatio = Math.round((float)Height/dm2.heightPixels);
        options.inSampleSize = Math.max(WeightRatio,HeightRatio);
        options.inJustDecodeBounds =false;
        bitmap = BitmapFactory.decodeFile(mResId, options);
        mIvContent.setImageBitmap(bitmap);
    }
}
