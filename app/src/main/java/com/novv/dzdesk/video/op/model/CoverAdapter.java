package com.novv.dzdesk.video.op.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.ImgUtil;
import com.novv.dzdesk.util.PathUtils;

import java.io.File;
import java.util.ArrayList;

public class CoverAdapter extends BaseAdapter {

    private static final String coverPath = PathUtils.getVideoEditCoverFilePath();
    private ArrayList<Bitmap> mItems;
    private View mSelectedV;

    public CoverAdapter(Context context, ArrayList<Bitmap> items) {
        this.mContext = context;
        this.mItems = items;
    }

    public static boolean saveCover(Bitmap bitmap) {
        String dirPath = Const.Dir.EDIT_VIDEO_PATH;
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return ImgUtil.saveBitmap(bitmap, coverPath, Bitmap.CompressFormat.JPEG, 90);
    }

    public static void deleteCover() {
        File file = new File(coverPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean hasCover() {
        File file = new File(coverPath);
        return file.exists();
    }

    public static String coverPath() {
        return coverPath;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.editor_adapter_cover_item, parent, false);
        return new ItemViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        if (mSelectedPosition != position) {
            holder.mRoot.setSelected(false);
        } else if (mSelectedPosition == position) {
            if (mSelectedV != null) {
                mSelectedV.setSelected(false);
            }
            holder.mRoot.setSelected(true);
            mSelectedV = holder.mRoot;
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.mIcon
                .getLayoutParams();
        float scale =
                (float) mItems.get(position).getWidth() / (float) mItems.get(position).getHeight();
        float width = scale * DeviceUtil.dip2px(mContext, 70);
        params.width = (int) width;
        holder.mIcon.setLayoutParams(params);
        holder.mIcon.setImageBitmap(mItems.get(position));
        holder.mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedV != null) {
                    mSelectedV.setSelected(false);
                }

                view.setSelected(true);
                mSelectedV = view;

                if (mListener != null) {
                    mListener.onClick(position);
                }
                saveCover(mItems.get(position));
                mSelectedPosition = position;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems != null ? mItems.size() : 0;
    }
}