package com.novv.dzdesk.ui.adapter.plugin;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;

import java.util.List;

public class AdapterSimpleRes extends BaseQuickAdapter<ResourceBean, BaseViewHolder> {

    public AdapterSimpleRes(Context context, @LayoutRes int layoutResId,
                            @Nullable List<ResourceBean> data) {
        super(layoutResId, data);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, ResourceBean item) {
        Glide.with(mContext)
                .load(item.getCoverURL())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                        .centerCrop())
                .into((ImageView) helper.getView(R.id.cover_imgv));
    }
}
