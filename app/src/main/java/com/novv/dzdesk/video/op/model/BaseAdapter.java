package com.novv.dzdesk.video.op.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.novv.dzdesk.R;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ItemViewHolder> {

    protected Context mContext;
    protected OnItemClickListener mListener;
    protected int mSelectedPosition = -1;

    public OnItemClickListener getOnItemClickListener() {
        return mListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.editor_adapter_item, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(contactView);
        return viewHolder;
    }

    public interface OnItemClickListener {

        public void onClick(int position);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        public View mRoot;
        public ImageView mIcon;
        public TextView mName;
        public TextView mProgress;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mRoot = itemView.findViewById(R.id.item_root_rl);
            mIcon = (ImageView) itemView.findViewById(R.id.icon);
            mName = (TextView) itemView.findViewById(R.id.name);
            mProgress = (TextView) itemView.findViewById(R.id.progress_tv);
        }
    }
}