package com.bytedance.xly.view.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bytedance.xly.R;
import com.bytedance.xly.adapter.TransmissionSendAdapter;
import com.bytedance.xly.share.SendHandleThread;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.ToastUtil;
import com.bytedance.xly.util.UITool;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TransmissionSendFragment extends Fragment {
    public static final int RECEIVE_DATA = 0;
    public static final int SOCKETTIMEOUT = 1;
    public static final int BEGIN_SEARCH = 2;
    public static final int END_SEARCH = 3;

    private static final java.lang.String TAG = "TransmissionSendFragmen";
    // TODO: Customize parameter argument names
    private static final java.lang.String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private SendHandleThread mSendHandleThread;

    private TransmissionSendAdapter mAdapter;
    private List<String> ipList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Button mBtnWait;


    public Handler getHandler() {
        return mHandler;
    }

    private Handler mHandler;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransmissionSendFragment() {
        mSendHandleThread = new SendHandleThread("search");
        mSendHandleThread.start();
        mSendHandleThread.initHandler();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TransmissionSendFragment newInstance(int columnCount) {
        TransmissionSendFragment fragment = new TransmissionSendFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_transmission_send, container, false);
            Context context = view.getContext();

        mBtnWait = view.findViewById(R.id.btn_wait);
            mBtnWait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchReceiverIP();

                }
            });

            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new TransmissionSendAdapter(mListener);
            recyclerView.setAdapter(mAdapter);
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = UITool.dip2px(getActivity(),4);
                outRect.bottom = UITool.dip2px(getActivity(),4);
                outRect.right = UITool.dip2px(getActivity(),4);
                outRect.top = UITool.dip2px(getActivity(),4);
            }
        });
        return view;
    }

    private void searchReceiverIP() {
        Handler searchHandler = mSendHandleThread.getHandler();
        mSendHandleThread.setMainHandler(mHandler);
        Message msg = Message.obtain();
        msg.what = SendHandleThread.SEARCH;
        searchHandler.sendMessage(msg);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
         mHandler= new Handler(getActivity().getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case RECEIVE_DATA:
                        LogUtil.d(TAG, "handleMessage: RECEIVE_DATA");
                        String ip = (String) msg.obj;
//                        if (!ipList.contains(ip)){
                            ipList.add(ip);
//                        }
                        mAdapter.setDataAndNotify(ipList);
                        break;
                    case SOCKETTIMEOUT:
                       // ToastUtil.showToast(getActivity(), Toast.LENGTH_LONG,"未搜索到接收方");
                        break;
                    case BEGIN_SEARCH:
                        mBtnWait.setClickable(false);
                        break;
                    case END_SEARCH:
                        if (ipList.size() == 0){
                            ToastUtil.showToast(getActivity(), Toast.LENGTH_LONG,"未搜索到接收方");
                        }
                        mBtnWait.setClickable(true);
                        break;
                }
            }
        };
        searchReceiverIP();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(String item);
    }
}
