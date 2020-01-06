package com.novv.dzdesk.ui.adapter.rings;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.ark.adkit.polymers.polymer.ADTool;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.novv.dzdesk.R;
import com.novv.dzdesk.player.PlayState;
import com.novv.dzdesk.res.model.RingsBean;

import com.novv.dzdesk.res.user.LoginContext;
import java.text.DecimalFormat;
import java.util.List;

import static com.novv.dzdesk.res.model.MultipleItem.TYPE_AD;
import static com.novv.dzdesk.res.model.MultipleItem.TYPE_NORMAL;

public class AdapterRingsRes extends BaseQuickAdapter<RingsBean, BaseViewHolder> {

    private int selectedIndex = -1;
    private boolean showLoading = true;

    public AdapterRingsRes(@Nullable List<RingsBean> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<RingsBean>() {
            @Override
            protected int getItemType(RingsBean resourceBean) {
                return resourceBean.isAdFlag() ? TYPE_AD : TYPE_NORMAL;
            }
        });
        getMultiTypeDelegate()
                .registerItemType(TYPE_AD, R.layout.item_rings_ad)
                .registerItemType(TYPE_NORMAL, R.layout.item_rings_data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, RingsBean item) {
        switch (helper.getItemViewType()) {
            case TYPE_AD:
                if (!LoginContext.getInstance().isVip()){
                    final ViewGroup container = helper.getView(R.id.rootView);
                    ADTool.getADTool().getManager()
                        .getNativeWrapper()
                        .loadVideoView(mContext, container);
                }
                break;
            case TYPE_NORMAL:
                String str = mContext.getString(R.string.format_ring_data);
                String desc = String.format(str, transSize(item.getSize()), item.getDuring());
                int res = R.drawable.rings_btn_pause;
                int playState = item.getPlayState();
                if (playState != PlayState.STATE_PLAYING) {
                    res = R.drawable.rings_btn_play_white;
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectedIndex(helper);
                        if (onSelectListener != null) {
                            onSelectListener.onSelected(v, helper.getAdapterPosition());
                        }
                    }
                });
                boolean isSelected = selectedIndex == helper.getAdapterPosition();
                helper.setText(R.id.tvTitle,
                        TextUtils.isEmpty(item.getName()) ? "铃声" : item.getName())
                        .setProgress(R.id.playProgress,
                                isSelected ? playState == PlayState.STATE_LOADING ? 0
                                        : item.getProgress() : 0)
                        .setImageResource(R.id.btnPausePlay, res)
                        .setGone(R.id.loading, showLoading && playState == PlayState.STATE_LOADING)
                        .setGone(R.id.btnPausePlay,
                                !showLoading || playState != PlayState.STATE_LOADING)
                        .setText(R.id.tvDesc, desc)
                        .setVisible(R.id.setLayout, isSelected)
                        .addOnClickListener(R.id.btnAction)
                        .addOnClickListener(R.id.btnSet)
                        .addOnClickListener(R.id.btnPausePlay);
                break;
        }
    }

    private String transSize(long size) {
        String resultString;
        DecimalFormat df = new DecimalFormat("###.##");
        float f;
        if (size < 1048576L) {
            f = (float) size / 1024.0F;
            resultString = df.format((new Float(f)).doubleValue()) + "KB";
        } else {
            f = (float) size / 1048576.0F;
            resultString = df.format((new Float(f)).doubleValue()) + "MB";
        }

        return resultString;
    }

    public void resetSelect() {
        selectedIndex = -1;
        notifyDataSetChanged();
    }

    private OnSelectListener onSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface OnSelectListener {

        void onSelected(View view, int index);
    }

    private void setSelectedIndex(final BaseViewHolder helper) {
        final int nowIndex = helper.getAdapterPosition();
        if (selectedIndex != nowIndex) {
            if (selectedIndex != -1 && selectedIndex < mData.size()) {
                RingsBean last = mData.get(selectedIndex);
                last.setProgress(0);
                last.setPlayState(PlayState.STATE_UN_PLAYING);
                mData.set(selectedIndex, last);
                notifyItemChanged(selectedIndex);
            }
            selectedIndex = nowIndex;
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_left_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    helper.getView(R.id.setLayout).setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    helper.getView(R.id.setLayout).setVisibility(View.VISIBLE);
                    RingsBean last = mData.get(nowIndex);
                    last.setProgress(0);
                    last.setPlayState(PlayState.STATE_LOADING);
                    mData.set(nowIndex, last);
                    notifyItemChanged(nowIndex);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            helper.getView(R.id.setLayout).startAnimation(animation);
        }
    }

    public void updateItem(RingsBean ringsBean) {
        if (selectedIndex < 0 || selectedIndex >= mData.size()) {
            return;
        }
        mData.set(selectedIndex, ringsBean);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(selectedIndex);
            }
        });
    }
}
