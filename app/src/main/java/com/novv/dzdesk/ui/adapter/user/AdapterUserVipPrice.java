package com.novv.dzdesk.ui.adapter.user;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.VipPrice;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.SelectorFactory;

import java.util.List;

public class AdapterUserVipPrice extends BaseQuickAdapter<VipPrice, BaseViewHolder> {

    private SparseBooleanArray sparseArray = new SparseBooleanArray();
    private int lastItem = -1;
    private OnItemSelectListener onItemSelectListener;

    public AdapterUserVipPrice(@Nullable List<VipPrice> data) {
        super(R.layout.item_vip_price, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, VipPrice item) {
        TextView tvOrigin = helper.getView(R.id.tv_price_origin);
        View llPrice = helper.getView(R.id.ll_price);
        View ivTag = helper.getView(R.id.iv_tag);
        ivTag.setVisibility(helper.getAdapterPosition() == 0 ? View.VISIBLE : View.GONE);
        tvOrigin.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        float price = (float) item.getPrice();
        int month = (int) Math.rint(item.getDays() / 30.f);
        float f = price / month;
        helper.setText(R.id.tv_price, "¥" + String.valueOf(item.getPrice()))
                .setText(R.id.tv_price_origin, "原价:¥" + String.valueOf(item.getOriginPrice()))
                .setText(R.id.tv_desc, item.getType() + "\n" + String.format("¥%.2f/月", f));
        View root = helper.getView(R.id.ll_root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyState(helper);
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelected(helper.getAdapterPosition());
                }
            }
        });
        helper.addOnClickListener(R.id.btn_action);
        boolean selected = sparseArray.get(helper.getAdapterPosition(), false);
        llPrice.setBackground(SelectorFactory.newShapeSelector()
                .setCornerRadius(DeviceUtil.dip2px(mContext, 5))
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setSelectedBgColor(Color.parseColor("#FEF0D7"))
                .setDefaultStrokeColor(Color.parseColor("#FEF0D7"))
                .setSelectedStrokeColor(Color.parseColor("#E3BB73"))
                .setStrokeWidth(DeviceUtil.dip2px(mContext, 2))
                .create());
        llPrice.setSelected(selected);
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

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public interface OnItemSelectListener {

        void onItemSelected(int position);
    }
}
