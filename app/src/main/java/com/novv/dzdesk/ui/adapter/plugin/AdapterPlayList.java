package com.novv.dzdesk.ui.adapter.plugin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.novv.dzdesk.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterPlayList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public boolean isEdit;//是否是编辑状态
    private Context mContext;
    private List<String> mSortedList=new ArrayList<>();
    private ChannelListener channelListener;

    public AdapterPlayList(Context mContext, List<String> list, ChannelListener channelListener) {
        this.mContext = mContext;
        if (list!=null){
            mSortedList.clear();
            mSortedList.addAll(list);
        }
        this.channelListener = channelListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_selected_videos, parent, false);
        return new ChannelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        setChannel((ChannelHolder) holder, mSortedList.get(position));
    }

    public void setNewData(List<String> list) {
        mSortedList.clear();
        mSortedList.addAll(list);
        notifyDataSetChanged();
    }

    public void cancelEdit(List<String> list) {
        isEdit = false;
        setNewData(list);
    }

    public void setEditState(boolean isEdit) {
        this.isEdit = isEdit;
        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return new ArrayList<>(mSortedList);
    }

    private void setChannel(final ChannelHolder holder, final String bean) {
        Glide.with(mContext)
                .load(bean)
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                        .centerCrop())
                .into(holder.imageView);
        final int position = holder.getLayoutPosition();
        //长按进入编辑状态
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isEdit = true;
                notifyDataSetChanged();
                if (channelListener != null) {
                    channelListener.onEdit(position);
                }
                //返回true 防止长按拖拽事件跟点击事件冲突
                return true;
            }
        });
        //点击X删除已选标签
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromSelected(holder);
                if (channelListener != null) {
                    channelListener.onDelete(position);
                }
            }
        });
        //是否是编辑状态
        if (isEdit) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
    }

    /**
     * 删除已选标签
     */
    private void removeFromSelected(ChannelHolder holder) {
        int position = holder.getLayoutPosition();
        holder.delete.setVisibility(View.GONE);
        notifyItemRemoved(position);
        try {
            mSortedList.remove(position);
        }catch (Exception e){
            //
        }
    }

    /**
     * 对拖拽的元素进行排序
     */
    public void itemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mSortedList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mSortedList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public interface ChannelListener {

        void onEdit(int pos);

        void onDelete(int pos);
    }

    public class ChannelHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageView delete;

        public ChannelHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.channel_imageview);
            delete = itemView.findViewById(R.id.channel_delete);
        }
    }
}
