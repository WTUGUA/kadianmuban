package com.novv.dzdesk.ui.adapter.ae;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.res.user.AEStateCallback;
import com.novv.dzdesk.res.user.LoginContext;

import java.util.List;

public class AdapterAERes extends BaseQuickAdapter<VModel, BaseViewHolder> {

    public AdapterAERes(@Nullable List<VModel> data) {
        super(R.layout.item_video_model_data, data);
    }

    public AdapterAERes(int layout,@Nullable List<VModel> data) {
        super(layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VModel item) {
        final ImageView icMask = helper.getView(R.id.ic_mask);
        ImageView icCover = helper.getView(R.id.ic_cover);
        RoundedCorners roundedCorners = new RoundedCorners(20);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        options.transform(new CenterCrop(), new RoundedCorners(20));
        Glide.with(mContext)
                .load(item.img)
                .apply(new RequestOptions().centerCrop().placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder))
                .apply(options)
                .into(icCover);
        icMask.setVisibility(View.GONE);
        if (item.hot!=null){
            //设置热度值
            //可调整
           helper.setText(R.id.tv_hot,item.hot);
        }
        LoginContext.getInstance().checkAEState(mContext, item, new AEStateCallback() {
            @Override
            public void onFree() {

            }

            @Override
            public void onLimitFree() {
                icMask.setVisibility(View.GONE);
                icMask.setImageResource(R.mipmap.ic_mask_limit_free);
            }

            @Override
            public void onVipFree(boolean isVip, boolean isLogin,int count) {
                icMask.setVisibility(View.GONE);
                icMask.setImageResource(R.mipmap.ic_mask_vip);
            }

            @Override
            public void onPriceNeed() {
                icMask.setVisibility(View.GONE);
                icMask.setImageResource(R.mipmap.ic_mask_pay);
            }

        });
    }
}
