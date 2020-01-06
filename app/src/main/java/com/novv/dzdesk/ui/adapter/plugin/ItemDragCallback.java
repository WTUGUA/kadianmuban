package com.novv.dzdesk.ui.adapter.plugin;

import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.ark.dict.Utils;
import com.novv.dzdesk.R;

import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;

public class ItemDragCallback extends ItemTouchHelper.Callback {

    private AdapterPlayList mAdapter;
    private boolean mEdit;

    public ItemDragCallback(AdapterPlayList mAdapter) {
        this.mAdapter = mAdapter;
    }


    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        AdapterPlayList adapter = (AdapterPlayList) recyclerView.getAdapter();
        mEdit = adapter.isEdit;
        //是否是编辑状态
        if (!mEdit) {
            return 0;
        }
        int position = viewHolder.getLayoutPosition();
        //第一个item不用交换
        if (position == 0) {
            return 0;
        }
        int dragFlags =
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT
                        | ItemTouchHelper.RIGHT;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();   //拖动的position
        int toPosition = target.getAdapterPosition();     //释放的position
        int position = viewHolder.getLayoutPosition();
        //第一个item不用交换
        if (position == 0) {
            return false;
        }
        mAdapter.itemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }


    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
            RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
            boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (dX != 0 && dY != 0 || isCurrentlyActive) {
            AdapterPlayList adapter = (AdapterPlayList) recyclerView.getAdapter();
            mEdit = adapter.isEdit;
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ACTION_STATE_DRAG) {
            //长按时调用
            AdapterPlayList.ChannelHolder holder = (AdapterPlayList.ChannelHolder) viewHolder;
            holder.imageView.setBackgroundColor(Color.parseColor("#FDFDFE"));
            holder.imageView.setBackground(
                    ContextCompat
                            .getDrawable(Utils.getContext(), R.drawable.shape_channel_bg));
            holder.delete.setVisibility(View.GONE);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //松手时调用
        AdapterPlayList.ChannelHolder holder = (AdapterPlayList.ChannelHolder) viewHolder;
        holder.imageView.setBackground(
                ContextCompat.getDrawable(Utils.getContext(), R.drawable.shape_channel_bg));
        holder.delete.setVisibility(View.VISIBLE);
    }
}

