package com.novv.dzdesk.ui.adapter.user;

import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.VideoEntity;

import java.util.ArrayList;
import java.util.List;

public class AdapterUserMake extends BaseQuickAdapter<VideoEntity, BaseViewHolder> {

    private SparseBooleanArray mCheckStates = new SparseBooleanArray();
    private boolean showCheckBox;

    public boolean isShowCheckBox() {
        return showCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.showCheckBox = showCheckBox;
    }

    public AdapterUserMake(
            @Nullable List<VideoEntity> data) {
        super(R.layout.item_download, data);
    }

    public List<VideoEntity> getChecked() {
        List<VideoEntity> list = new ArrayList<>();
        for (int i = 0; i < getData().size(); i++) {
            if (mCheckStates.get(i, false)) {
                list.add(getData().get(i));
            }
        }
        return list;
    }

    public void setAllChecked(boolean checked) {
        if (checked) {
            for (int i = 0; i < getData().size(); i++) {
                mCheckStates.put(i, true);
            }
            notifyDataSetChanged();
        } else {
            mCheckStates.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, VideoEntity item) {
        final CheckBox mCbItem = helper.getView(R.id.checkbox);
        mCbItem.setTag(helper.getAdapterPosition());
        if (showCheckBox) {
            mCbItem.setVisibility(View.VISIBLE);
            mCbItem.setChecked(mCheckStates.get(helper.getAdapterPosition(), false));
        } else {
            mCbItem.setVisibility(View.GONE);
            mCbItem.setChecked(false);
            mCheckStates.clear();
        }
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showCheckBox) {
                    mCbItem.setChecked(!mCbItem.isChecked());
                } else if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onItemClick(helper.getAdapterPosition());
                }
            }
        });
        mCbItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int pos = (int) compoundButton.getTag();
                if (b) {
                    mCheckStates.put(pos, true);
                } else {
                    mCheckStates.delete(pos);
                }
                if (mOnCheckedChangeListener != null) {
                    mOnCheckedChangeListener.onCheckedChanged(b, mCheckStates.size());
                }
            }
        });
        Glide.with(mContext)
                .load(item.getVideoPath())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .centerCrop())
                .into((ImageView) helper.getView(R.id.cover_imgv));
    }

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(boolean check, int size);

        void onItemClick(int position);
    }
}
