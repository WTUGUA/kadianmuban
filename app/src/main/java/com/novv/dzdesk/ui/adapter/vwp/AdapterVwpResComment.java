package com.novv.dzdesk.ui.adapter.vwp;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.CommentModel;
import com.novv.dzdesk.util.DeviceUtil;

import java.util.List;

/**
 * 评论数据源 Created by lijianglong on 2017/9/4.
 */

public class AdapterVwpResComment extends BaseQuickAdapter<CommentModel, BaseViewHolder> {

    public AdapterVwpResComment(@Nullable List<CommentModel> data) {
        super(R.layout.item_comment_layout, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, CommentModel item) {
        int loadingResId = R.drawable.user_avatar_default_small;
        Glide.with(mContext).load(item.getCommentPicture())
                .apply(new RequestOptions()
                        .dontAnimate()
                        .placeholder(loadingResId)
                        .centerCrop())
                .into((ImageView) helper.getView(R.id.img_avatar));
        helper.setText(R.id.tv_comment_name, item.getCommentName())
                .setText(R.id.tv_content, item.getCommentContent());
        TextView tvPraise = helper.getView(R.id.btn_praise);
        if (!TextUtils.isEmpty(item.getCommentPraiseNum())
                && Integer.parseInt(item.getCommentPraiseNum()) != 0) {
            helper.setText(R.id.btn_praise, item.getCommentPraiseNum());
        } else {
            helper.setText(R.id.btn_praise, "");
        }
        if (TextUtils.isEmpty(item.getCommentId())) {
            helper.setVisible(R.id.btn_praise, false);
        } else {
            helper.setVisible(R.id.btn_praise, true);
        }
        if (item.isPraise(mContext)) {
            Drawable checked = ContextCompat.getDrawable(mContext, R.drawable.user_praise_checked);
            checked.setBounds(0, 0, DeviceUtil.dip2px(mContext, 18),
                    DeviceUtil.dip2px(mContext, 18));
            tvPraise.setCompoundDrawables(checked, null, null, null);
        } else {
            Drawable normal = ContextCompat.getDrawable(mContext, R.drawable.user_praise_normal);
            normal.setBounds(0, 0, DeviceUtil.dip2px(mContext, 18),
                    DeviceUtil.dip2px(mContext, 18));
            tvPraise.setCompoundDrawables(normal, null, null, null);
        }
        helper.addOnClickListener(R.id.img_avatar);
        if (!TextUtils.isEmpty(item.getCommentId())) {
            //新添加的评论无 id, 点赞会出错
            helper.addOnClickListener(R.id.rl_praise);
        }
    }
}
