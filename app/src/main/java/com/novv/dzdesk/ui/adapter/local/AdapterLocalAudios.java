package com.novv.dzdesk.ui.adapter.local;

import android.support.annotation.Nullable;
import com.ark.tools.medialoader.AudioItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.novv.dzdesk.R;

import java.util.List;

public class AdapterLocalAudios extends BaseQuickAdapter<AudioItem, BaseViewHolder> {

    public AdapterLocalAudios(@Nullable List<AudioItem> data) {
        super(R.layout.item_selected_audio, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AudioItem item) {
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
