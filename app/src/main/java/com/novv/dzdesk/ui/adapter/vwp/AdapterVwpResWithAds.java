package com.novv.dzdesk.ui.adapter.vwp;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ark.adkit.basics.data.ADMetaData;
import com.ark.adkit.polymers.polymer.ADTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.res.user.LoginContext;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import static com.novv.dzdesk.res.model.MultipleItem.TYPE_AD;
import static com.novv.dzdesk.res.model.MultipleItem.TYPE_DIY;
import static com.novv.dzdesk.res.model.MultipleItem.TYPE_NORMAL;

public class AdapterVwpResWithAds extends BaseQuickAdapter<ResourceBean, BaseViewHolder> {

    private WeakReference<Activity> activityRef;

    public AdapterVwpResWithAds(@Nullable List<ResourceBean> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<ResourceBean>() {
            @Override
            protected int getItemType(ResourceBean resourceBean) {
                int type = TYPE_NORMAL;
                if (resourceBean.getvModel() != null) {
                    type = TYPE_DIY;
                }
                if (resourceBean.getContentType() == Const.OnlineKey.CONTENT_TYPE_AD) {
                    type = TYPE_AD;
                }
                return type;
            }
        });
        getMultiTypeDelegate()
                .registerItemType(TYPE_AD, R.layout.resource_item_layout)
                .registerItemType(TYPE_NORMAL, R.layout.resource_item_layout)
                .registerItemType(TYPE_DIY, R.layout.resource_item_layout)
        ;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final ResourceBean item) {
        if (mContext instanceof Activity && activityRef == null) {
            activityRef = new WeakReference<>((Activity) mContext);
        }
        switch (helper.getItemViewType()) {
            case TYPE_AD:
                final FrameLayout container = helper.getView(R.id.rootView);
                if (activityRef == null) {
                    return;
                }
                Activity activity = activityRef.get();
                if (activity == null || activity.isFinishing()) {
                    return;
                }
                ADMetaData nativeAD = ADTool.getADTool().getManager()
                        .getNativeWrapper()
                        .getListNative(activity);
                if (!LoginContext.getInstance().isVip() && nativeAD != null) {
                    helper.setGone(R.id.cover_imgv, false);
                    ADTool.getADTool().getManager()
                            .getNativeWrapper()
                            .loadSmallNativeView(activity, container, nativeAD, null);
                } else if (!TextUtils.isEmpty(item.getCoverURL())) {
                    helper.setGone(R.id.cover_imgv, true);
                    Glide.with(mContext)
                            .load(item.getCoverURL())
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .centerCrop())
                            .into((ImageView) helper.getView(R.id.cover_imgv));
                }
                Random random = new Random();
                int max = 15000;
                int min = 5000;
                int s = random.nextInt(max) % (max - min + 1) + min;
                TextView tv_hot = (TextView) helper.itemView.findViewById(R.id.tv_hot);

                if (tv_hot != null) {

                    if (s < 10000) {
                        tv_hot.setText(String.valueOf(s));
                    } else {
                        DecimalFormat df = new DecimalFormat("0.0");
                        String result = df.format(s / 10000.0f) + "w";
                        tv_hot.setText(result);
                    }
                }

                break;
            case TYPE_NORMAL:
                if (!TextUtils.isEmpty(item.getCoverURL())) {
                    Glide.with(mContext)
                            .load(item.getCoverURL())
                            .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .centerCrop())
                            .into((ImageView) helper.getView(R.id.cover_imgv));
                }
                if (item.getHot() != null) {
                    helper.setText(R.id.tv_hot, item.getHot());
                }
                break;
            case TYPE_DIY:
                helper.setVisible(R.id.iv_tag, true);
                VModel vModel = item.getvModel();
                Glide.with(mContext)
                        .load(vModel.img)
                        .apply(new RequestOptions().placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .centerCrop())
                        .into((ImageView) helper.getView(R.id.cover_imgv));

                if (item.getvModel() != null) {
                    helper.setText(R.id.tv_hot, item.getvModel().hot);
                }
                break;
        }
    }
}
