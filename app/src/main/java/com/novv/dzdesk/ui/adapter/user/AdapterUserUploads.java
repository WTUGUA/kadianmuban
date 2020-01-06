package com.novv.dzdesk.ui.adapter.user;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;

import java.util.List;

import static com.novv.dzdesk.res.model.MultipleItem.*;

/**
 * 投稿列表数据源 Created by lijianglong on 2017/8/25.
 */

public class AdapterUserUploads extends BaseQuickAdapter<ResourceBean, BaseViewHolder> {

    public AdapterUserUploads(@Nullable List<ResourceBean> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<ResourceBean>() {
            @Override
            protected int getItemType(ResourceBean bean) {
                if (bean.isReview()) {
                    return TYPE_REVIEW;
                } else if (bean.isPass()) {
                    return TYPE_PASS;
                } else if (bean.isReject()) {
                    return TYPE_REFUSED;
                } else {
                    return TYPE_REVIEW;
                }
            }
        });
        getMultiTypeDelegate()
                .registerItemType(TYPE_REVIEW, R.layout.resource_item_review_layout)
                .registerItemType(TYPE_REFUSED, R.layout.resource_item_refused_layout)
                .registerItemType(TYPE_PASS, R.layout.resource_item_pass_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResourceBean item) {
        if (!TextUtils.isEmpty(item.getCoverURL())) {
            Glide.with(mContext)
                    .load(item.getCoverURL())
                    .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder)
                            .centerCrop())
                    .into((ImageView) helper.getView(R.id.cover_imgv));
        }
    }
}
