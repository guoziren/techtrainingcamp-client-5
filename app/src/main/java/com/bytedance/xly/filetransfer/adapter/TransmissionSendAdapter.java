package com.bytedance.xly.filetransfer.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bytedance.xly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TransmissionSendAdapter extends RecyclerView.Adapter<TransmissionSendAdapter.ViewHolder> {

    private final List<String> mValues = new ArrayList<>();



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_send, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mIP.setText(mValues.get(position));

        holder.mIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(mValues.get(position));
            }
        });
    }
    public void setDataAndNotify(List<String> ips){
        mValues.clear();
        mValues.addAll(ips);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public  TextView mIP;
        public ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            mIP =  view.findViewById(R.id.ip);
            mImageView =  view.findViewById(R.id.touxiang);
        }

        @Override
        public java.lang.String toString() {
            return super.toString() + " '" + mIP.getText() + "'";
        }
    }
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(String ip);
    }
}
