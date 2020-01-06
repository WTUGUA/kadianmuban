package com.novv.dzdesk.ui.adapter.avatar;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.AvatarCategoryBean;

import java.util.List;

public class AdapterAvatarCat extends BaseQuickAdapter<AvatarCategoryBean, BaseViewHolder> {

    public AdapterAvatarCat(@Nullable List<AvatarCategoryBean> data) {
        super(R.layout.resource_item_layout_avatar_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AvatarCategoryBean item) {
        helper.setText(R.id.text, item.getName());
        Glide.with(mContext)
                .load(item.getImg())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_sq)
                        .centerCrop())
                .into((ImageView) helper.getView(R.id.image));
    }
}
