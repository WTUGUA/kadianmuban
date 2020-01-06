package com.novv.dzdesk.ui.adapter.local;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AdapterLocalVideoPicker extends BaseQuickAdapter<String, BaseViewHolder> {

  private List<String> inputList = new ArrayList<>();
  private OnItemClickListener onItemClickListener;
  private List<String> selectedList = new ArrayList<>();

  public AdapterLocalVideoPicker(@Nullable List<String> data, List<String> inputList) {
    super(R.layout.item_video_picker_photo, data);
    if (inputList != null) {
      this.inputList.addAll(inputList);
    }
  }

  @Override
  protected void convert(final BaseViewHolder helper, final String videoPath) {
    ImageView imageView = helper.getView(R.id.video_picker_im_photo);
    View mask = helper.getView(R.id.video_picker_v_photo_mask);
    CheckBox checkBox = helper.getView(R.id.video_picker_cb_select);
    Glide.with(mContext)
        .load(videoPath)
        .apply(new RequestOptions().placeholder(R.drawable.placeholder)
            .centerCrop().skipMemoryCache(true))
        .into(imageView);
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (selectedList.contains(videoPath)) {
          selectedList.remove(videoPath);
        } else {
          selectedList.add(videoPath);
        }
        if (onItemClickListener != null) {
          onItemClickListener.onVideoClick(helper.getAdapterPosition(), selectedList);
        }
        notifyItemChanged(helper.getAdapterPosition());
      }
    });
    checkBox.setVisibility(View.VISIBLE);
    if (selectedList.contains(videoPath)) {
      mask.setVisibility(View.VISIBLE);
      checkBox.setChecked(true);
      checkBox.setButtonDrawable(R.drawable.picker_select_checked);
    } else {
      mask.setVisibility(View.GONE);
      checkBox.setChecked(false);
      checkBox.setButtonDrawable(R.drawable.picker_select_unchecked);
    }
  }

  public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
    this.onItemClickListener = onItemClickListener;
    notifyDataSetChanged();
  }

  @Override public void replaceData(@NonNull Collection<? extends String> data) {
    for (String videoPath : data) {
      if (inputList.contains(videoPath)) {
        if (!selectedList.contains(videoPath)) {
          selectedList.add(videoPath);
          inputList.remove(videoPath);
        }
      }
    }
    super.replaceData(data);
    if (onItemClickListener != null) {
      onItemClickListener.onVideoClick(0, selectedList);
    }
  }

  public List<String> getSelectedList() {
    return selectedList;
  }

  public interface OnItemClickListener {

    void onVideoClick(int position, List<String> selectVideo);
  }
}
