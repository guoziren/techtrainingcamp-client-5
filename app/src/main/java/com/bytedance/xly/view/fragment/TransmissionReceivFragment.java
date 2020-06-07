package com.bytedance.xly.view.fragment;

import android.content.Context;
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
import com.bytedance.xly.adapter.TransmissionReceivAdapter;
import com.bytedance.xly.share.ReceiveHandlerThread;
import com.bytedance.xly.util.LogUtil;
import com.bytedance.xly.util.SocketManager;
import com.bytedance.xly.util.ToastUtil;

import java.net.ServerSocket;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TransmissionReceivFragment extends Fragment {
    private static final String TAG = "TransmissionReceivFragm";
    // TODO: Customize parameter argument names
    private static final java.lang.String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private ReceiveHandlerThread mReceiveHandlerThread;
    private TransmissionReceivAdapter mAdapter;
    private Handler mHandler;
    private ServerSocket server;
    private SocketManager socketManager;
    public static int TCP_PORT = 9999;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransmissionReceivFragment() {
        mReceiveHandlerThread = new ReceiveHandlerThread("receive");
        mReceiveHandlerThread.start();
        mReceiveHandlerThread.initHandler();
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TransmissionReceivFragment newInstance(int columnCount) {
        TransmissionReceivFragment fragment = new TransmissionReceivFragment();
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
        View view = inflater.inflate(R.layout.fragment_transmission_receiv, container, false);


        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Button btn_wait =  view.findViewById(R.id.btn_wait);
        btn_wait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                broadcast();
            }
        });
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        mAdapter = new TransmissionReceivAdapter(mListener);
        recyclerView.setAdapter(mAdapter);

        return view;
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
        LogUtil.d(TAG, "onAttach: ");
        broadcast();
        mHandler= new Handler(getActivity().getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        //未能绑定端口
                        break;
                }
            }
        };

    }



    private void broadcast() {
        //发送upd广播，让发送方知道我的ip地址
        Handler receiveHandler = mReceiveHandlerThread.getHandler();
        Message msg = Message.obtain();
        msg.what = ReceiveHandlerThread.BROADCAST;
        receiveHandler.sendMessage(msg);
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
