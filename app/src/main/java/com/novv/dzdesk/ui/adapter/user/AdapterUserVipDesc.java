package com.novv.dzdesk.ui.adapter.user;

import android.support.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.VipDesc;

import java.util.List;

public class AdapterUserVipDesc extends BaseQuickAdapter<VipDesc, BaseViewHolder> {

    public AdapterUserVipDesc(
            @Nullable List<VipDesc> data) {
        super(R.layout.item_vip_desc, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipDesc item) {
        helper.setImageResource(R.id.vip_cover, item.resId)
                .setText(R.id.vip_title, item.title)
                .setText(R.id.vip_desc, item.desc);

    }
}
