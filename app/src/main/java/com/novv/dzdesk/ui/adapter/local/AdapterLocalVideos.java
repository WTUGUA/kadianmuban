package com.novv.dzdesk.ui.adapter.local;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.ark.tools.medialoader.VideoItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;
import java.io.File;
import java.util.List;

public class AdapterLocalVideos extends BaseQuickAdapter<VideoItem, BaseViewHolder> {

  public AdapterLocalVideos(@Nullable List<VideoItem> data) {
    super(R.layout.item_selected_videos, data);
  }

  @Override
  protected void convert(BaseViewHolder helper, VideoItem item) {
    Glide.with(mContext)
        .load(new File(item.path))
        .apply(new RequestOptions().placeholder(R.drawable.placeholder)
            .centerCrop()
            .skipMemoryCache(true)
        )
        .into((ImageView) helper.getView(R.id.channel_imageview));
    helper.setText(R.id.video_time, timeParse(item.timeLong))
        .setVisible(R.id.rl_bottom, true);
  }

  public String timeParse(long duration) {
    String time = "";
    long minute = duration / 60000L;
    long seconds = duration % 60000L;
    long second = (long) Math.round((float) seconds / 1000.0F);
    if (minute < 10L) {
      time = time + "0";
    }

    time = time + minute + ":";
    if (second < 10L) {
      time = time + "0";
    }

    time = time + second;
    return time;
  }
}
