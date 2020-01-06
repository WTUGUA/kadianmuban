package com.novv.dzdesk.ui.adapter.edit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.video.op.PLBuiltinFilterImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class AdapterVideoFilter extends BaseQuickAdapter<PLBuiltinFilterImpl, BaseViewHolder> {

    private SparseBooleanArray sparseArray = new SparseBooleanArray();
    private int lastItem = -1;
    private OnItemSelectListener onItemSelectListener;
    private boolean needSelect;

    public AdapterVideoFilter(
            @Nullable List<PLBuiltinFilterImpl> data) {
        super(R.layout.editor_adapter_item, data);
        needSelect = true;
    }

    public AdapterVideoFilter(int layout, @Nullable List<PLBuiltinFilterImpl> data) {
        super(layout, data);
        needSelect = false;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PLBuiltinFilterImpl item) {
        RelativeLayout mItemRootRl = helper.getView(R.id.item_root_rl);
        TextView mName = helper.getView(R.id.name);
        ImageView mIcon = helper.getView(R.id.icon);
        TextView mProgressTv = helper.getView(R.id.progress_tv);
        FrameLayout flMask = helper.getView(R.id.fl_mask);
        mProgressTv.setVisibility(View.GONE);
        if (needSelect){
            mItemRootRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyState(helper);
                    if (onItemSelectListener != null) {
                        onItemSelectListener.onItemSelected(helper.getAdapterPosition());
                    }
                }
            });
        }
        String[] names = item.getName().split("_");
        if (names.length > 1) {
            mName.setText(names[1]);
        } else {
            mName.setText(item.getName());
        }
        try {
            InputStream is = mContext.getAssets().open(item.getAssetFilePath());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            mIcon.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mIcon.setSelected(sparseArray.get(helper.getAdapterPosition()));
        flMask.setVisibility(item.isVip() ? View.VISIBLE : View.GONE);
    }

    private void notifyState(BaseViewHolder helper) {
        if (lastItem != -1) {
            sparseArray.put(lastItem, false);
            notifyItemChanged(lastItem);
        }
        lastItem = helper.getAdapterPosition();
        if (lastItem != -1) {
            sparseArray.put(lastItem, true);
            notifyItemChanged(lastItem);
        }
    }

    public void setSelectItem(int selectItem) {
        lastItem = selectItem;
        sparseArray.clear();
        sparseArray.put(lastItem, true);
        notifyDataSetChanged();
    }

    public void resetState() {
        lastItem = -1;
        sparseArray.clear();
        notifyDataSetChanged();
    }

    public int getLastSelect() {
        return lastItem;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {

        void onItemSelected(int position);
    }
}
