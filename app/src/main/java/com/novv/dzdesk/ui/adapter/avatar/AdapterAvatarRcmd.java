package com.novv.dzdesk.ui.adapter.avatar;

import android.view.ViewGroup;
import android.widget.ImageView;
import com.ark.adkit.polymers.polymer.ADTool;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.Level0Item;
import com.novv.dzdesk.res.model.Level1Item;
import com.novv.dzdesk.res.user.LoginContext;
import java.util.List;

public class AdapterAvatarRcmd extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

  public static final int TYPE_FOLDER = 111;
  public static final int TYPE_DATA = 222;
  public static final int TYPE_AD = 333;

  /**
   * Same as QuickAdapter#QuickAdapter(Context,int) but with some initialization data.
   *
   * @param data A new list is created out of this one to avoid mutable list
   */
  public AdapterAvatarRcmd(List<MultiItemEntity> data) {
    super(data);
    addItemType(TYPE_FOLDER, R.layout.item_title);
    addItemType(TYPE_DATA, R.layout.sdk_item_square_layout);
    addItemType(TYPE_AD, R.layout.item_ad_container);
  }

  @Override
  protected void convert(BaseViewHolder helper, MultiItemEntity item) {
    switch (helper.getItemViewType()) {
      case TYPE_FOLDER:
        if (item instanceof Level0Item) {
          Level0Item lv0 = (Level0Item) item;
          helper.setText(R.id.tv_title, lv0.title)
              .setText(R.id.tv_day, lv0.day)
              .setText(R.id.tv_month, lv0.month);
        }
        break;
      case TYPE_DATA:
        if (item instanceof Level1Item) {
          Level1Item lv1 = (Level1Item) item;
          Glide.with(mContext)
              .load(lv1.thumb)
              .apply(new RequestOptions().placeholder(R.drawable.placeholder_sq)
                  .centerCrop())
              .into((ImageView) helper.getView(R.id.imageView));
        }
        break;
      case TYPE_AD:
        if (item instanceof Level0Item) {
          if (!LoginContext.getInstance().isVip() && ((Level0Item) item).isAd) {
            final ViewGroup container = helper.getView(R.id.rootView);
            ADTool.getADTool().getManager()
                .getNativeWrapper()
                .loadVideoView(mContext, container);
          }
        }
        break;
    }
  }
}
