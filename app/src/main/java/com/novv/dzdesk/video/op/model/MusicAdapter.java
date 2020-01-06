package com.novv.dzdesk.video.op.model;

import android.content.Context;
import android.view.View;
import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.util.FileUtil;

import java.io.File;
import java.util.ArrayList;


public class MusicAdapter extends BaseAdapter {

    private static final String TAG = MusicAdapter.class.getSimpleName();

    private ArrayList<MusicItem> mItems;
    private ItemViewHolder mSelectedHolder;

    public MusicAdapter(Context context, ArrayList<MusicItem> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        if (mSelectedPosition != position) {
            holder.mIcon.setSelected(false);
            holder.mProgress.setVisibility(View.GONE);
        } else if (mSelectedPosition == position) {
            if (mSelectedHolder != null) {
                mSelectedHolder.mIcon.setSelected(false);
            }
            holder.mIcon.setSelected(true);
            mSelectedHolder = holder;
        }

        if (position == 0) {
            holder.mName.setText("无");
            holder.mIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedHolder != null) {
                        mSelectedHolder.mIcon.setSelected(false);
                    }
                    holder.mIcon.setSelected(true);
                    mSelectedHolder = holder;

                    if (mListener != null) {
                        mListener.onClick(position);
                    }
                }
            });
            if (mSelectedHolder == null) {
                mSelectedHolder = holder;
                mSelectedHolder.mIcon.setSelected(true);
                mSelectedPosition = 0;
            }
            return;
        }

        final MusicItem item = mItems.get(position - 1);

        holder.mName.setText(item.name);
        Glide.with(mContext).load(item.cover).into(holder.mIcon);
        holder.mIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!FileUtil.isFileExists(item.getFilePath())) {
                    downloadMusic(holder, item);
                    return;
                }
                if (mSelectedHolder != null) {
                    mSelectedHolder.mIcon.setSelected(false);
                }
                holder.mIcon.setSelected(true);
                mSelectedHolder = holder;

                if (mListener != null) {
                    mListener.onClick(position);
                }
                downloadMusic(holder, item);
                mSelectedPosition = position;
            }
        });
    }

    public void downloadMusic(final ItemViewHolder holder, final MusicItem item) {
        if (FileUtil.isFileExists(item.getFilePath())) {
            holder.mProgress.setVisibility(View.GONE);
            return;
        }
        holder.mProgress.setVisibility(View.VISIBLE);
        holder.mProgress.setText("0 %");
        FileDownloader.getImpl()
                .create(item.music)
                .setPath(item.getTempFilePath())
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask baseDownloadTask, int i, int i1) {

                    }

                    @Override
                    protected void progress(BaseDownloadTask baseDownloadTask, int i, int i1) {
                        float progress = (float) i / (float) i1 * 100;
                        holder.mProgress.setText((int) ((float) i / (float) i1 * 100) + " %");
                    }

                    @Override
                    protected void completed(BaseDownloadTask baseDownloadTask) {
                        new File(item.getTempFilePath()).renameTo(new File(item.getFilePath()));
                        holder.mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    protected void paused(BaseDownloadTask baseDownloadTask, int i, int i1) {
                    }

                    @Override
                    protected void error(BaseDownloadTask baseDownloadTask, Throwable throwable) {
                        throwable.printStackTrace();
                        holder.mProgress.setVisibility(View.GONE);
                    }

                    @Override
                    protected void warn(BaseDownloadTask baseDownloadTask) {
                        holder.mProgress.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() + 1 : 0;
    }
}