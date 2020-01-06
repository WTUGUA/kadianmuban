package com.novv.dzdesk.ui.adapter.avatar;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.ark.adkit.polymers.polymer.ADTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.AvatarBean;

import com.novv.dzdesk.res.user.LoginContext;
import java.util.List;

import static com.novv.dzdesk.res.model.MultipleItem.TYPE_AD;
import static com.novv.dzdesk.res.model.MultipleItem.TYPE_NORMAL;

public class AdapterAvatarRes extends BaseQuickAdapter<AvatarBean, BaseViewHolder> {

    private boolean isDetail;

    public AdapterAvatarRes(@Nullable List<AvatarBean> data, boolean detail) {
        super(data);
        isDetail = detail;
        setMultiTypeDelegate(new MultiTypeDelegate<AvatarBean>() {
            @Override
            protected int getItemType(AvatarBean avatarBean) {
                return avatarBean.getFlagIsAd() ? TYPE_AD : TYPE_NORMAL;
            }
        });
        getMultiTypeDelegate()
                .registerItemType(TYPE_AD, R.layout.resource_item_layout_avatar)
                .registerItemType(TYPE_NORMAL, R.layout.resource_item_layout_avatar);
    }

    @Override
    protected void convert(BaseViewHolder helper, AvatarBean item) {
        switch (helper.getItemViewType()) {
            case TYPE_AD:
                if (!LoginContext.getInstance().isVip()){
                    helper.setGone(R.id.cover_imgv, false);
                    final FrameLayout container = helper.getView(R.id.rootView);
                    ADTool.getADTool().getManager()
                        .getNativeWrapper()
                        .loadVideoView(mContext, container);
                }
                break;
            case TYPE_NORMAL:
                if (!TextUtils.isEmpty(item.getThumb())) {
                    if (isDetail) {
                        Glide.with(mContext)
                                .load(item.getThumb())
                                .apply(new RequestOptions().placeholder(R.drawable.placeholder_sq)
                                        .override(750, 750)
                                        .centerCrop())
                                .into((ImageView) helper.getView(R.id.cover_imgv));
                    } else {
                        Glide.with(mContext)
                                .load(item.getThumb())
                                .apply(new RequestOptions().placeholder(R.drawable.placeholder_sq)
                                        .centerCrop())
                                .into((ImageView) helper.getView(R.id.cover_imgv));
                    }
                }
                break;
        }
    }
}
