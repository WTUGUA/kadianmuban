package com.novv.dzdesk.ui.adapter.vwp;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.CategoryBean;

import java.util.List;

/**
 * 分类数据源 Created by lijianglong on 2017/7/31.
 */

public class AdapterVwpCat extends BaseQuickAdapter<CategoryBean, BaseViewHolder> {

    public AdapterVwpCat(@Nullable List<CategoryBean> data) {
        super(R.layout.category_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CategoryBean item) {
        helper.setText(R.id.category_title_tv, item.getName() + "");
        Glide.with(mContext)
                .load(item.getCover())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_sq)
                        .error(R.drawable.placeholder)
                        .centerCrop())
                .into((ImageView) helper.getView(R.id.cover_imgv));
    }
}
