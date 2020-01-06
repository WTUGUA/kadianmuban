package com.novv.dzdesk.ui.adapter.rings;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.RingsCategoryBean;

import java.util.List;

public class AdapterRingsCat extends BaseQuickAdapter<RingsCategoryBean, BaseViewHolder> {

    public AdapterRingsCat(@Nullable List<RingsCategoryBean> data) {
        super(R.layout.category_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RingsCategoryBean item) {
        helper.setText(R.id.category_title_tv, item.getName() + "");
        Glide.with(mContext)
                .load(item.getCoverid())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                        .centerCrop())
                .into((ImageView) helper.getView(R.id.cover_imgv));
    }
}
