package com.novv.dzdesk.ui.fragment.nav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import com.adesk.tool.plugin.PSetUtils;
import com.ark.baseui.XLazyFragment;
import com.ark.uikit.envelope.RedEnvelopeDialog;
import com.ark.uikit.envelope.RedEnvelopeTool;
import com.novv.dzdesk.R;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.res.event.EventCode;
import com.novv.dzdesk.rxbus2.RxBus;
import com.novv.dzdesk.service.UriObserver;
import com.novv.dzdesk.ui.activity.WebUrlActivity;
import com.novv.dzdesk.ui.activity.vwp.VwpSearchActivity;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpHomePager;
import com.novv.dzdesk.ui.view.GuideView;
import com.novv.dzdesk.ui.view.NavTabTitleHomeBar;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.tencent.mmkv.MMKV;

public class NavHomeFragment extends XLazyFragment {

    private static final String tag = NavHomeFragment.class.getSimpleName();
    private ViewPager mPager;
    private NavTabTitleHomeBar mTabTitleBar;
    private ImageView searchIv;
    private CheckBox cbVoice;
    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            UriObserver.Voice voiceEvent = new UriObserver.Voice(true, b);
            RxBus.getDefault().send(EventCode.VOICE_CODE, voiceEvent);
        }
    };
    private ImageView ivAd;
    private GuideView guideView1;

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRedEnvelope();
            }
        });
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeNavTabState(position);
            }
        });
        mTabTitleBar.setOnItemClickListener(new NavTabTitleHomeBar.OnItemClickListener() {
            @Override
            public void onFirstClick(View view) {
                mPager.setCurrentItem(0, false);
            }

            @Override
            public void onSecondClick(View view) {
                mPager.setCurrentItem(1, false);
            }

            @Override
            public void onThirdClick(View view) {
                mPager.setCurrentItem(2, false);
            }

            @Override
            public void onForthClick(View view) {
                mPager.setCurrentItem(3, false);
            }

        });
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VwpSearchActivity.launch(v.getContext());
            }
        });
        AdapterVwpHomePager mainFragmentAdapter = new AdapterVwpHomePager(getChildFragmentManager());
        mPager.setAdapter(mainFragmentAdapter);
        mPager.setOffscreenPageLimit(3);
        mTabTitleBar.setNavTitleSelected(NavTabTitleHomeBar.NavType.First);
        cbVoice.setOnCheckedChangeListener(changeListener);
        mainFragmentAdapter.notifyDataSetChanged();
        RedEnvelopeTool.newBuilder()
                .withDismissType(0)
                //.withDebug()
                .withDefaultEnable(false)
                .withLaunch(new RedEnvelopeTool.OnLaunchListener() {
                    @Override
                    public void onShouldLaunch(@NonNull View view) {
                        launchRedEnvelope();
                    }
                })
                .build().preload(context, new RedEnvelopeTool.OnPreloadListener() {
            @Override
            public void onLoadFailed(@NonNull String mUrl) {
                //ivAd.setVisibility(View.GONE);
            }

            @Override
            public void onStatus(boolean isEnable, boolean isShowEveryday,
                                 boolean isTodayShow, @NonNull String mUrl, @NonNull String[] links) {
                ivAd.setVisibility(isEnable ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onResourceReady(@NonNull RedEnvelopeDialog redEnvelopeDialog) {
                redEnvelopeDialog.show();
            }
        });
        mTabTitleBar.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    mTabTitleBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    if (!MMKV.defaultMMKV().decodeBool("diy_guide")) {
                        try {
                            View hintView = LayoutInflater.from(context)
                                .inflate(R.layout.view_guide_layout_diy, null);
                            guideView1 = new GuideView.Builder(context)
                                .setTargetView(findViewById(R.id.search_iv))
                                .setHintView(hintView)
                                .setHintViewDirection(GuideView.Direction.LEFT)
                                .setmForm(1000)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        guideView1.hide();
                                        MMKV.defaultMMKV().encode("diy_guide", true);
                                    }
                                })
                                .create();
                            guideView1.show();
                        } catch (Exception e) {
                            //
                        }
                    }
                }
            });
    }

    public void setDIYSelect() {
        if (mPager != null) {
            mPager.setCurrentItem(3, false);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.nav_home_fragment;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        searchIv = rootView.findViewById(R.id.search_iv);
        mPager = rootView.findViewById(R.id.viewpager);
        mTabTitleBar = rootView.findViewById(R.id.nav_tab_title_bar);
        cbVoice = rootView.findViewById(R.id.cb_voice);
        ivAd = rootView.findViewById(R.id.ad_iv);
    }

    private void launchRedEnvelope() {
        WebUrlActivity.launch(context, RedEnvelopeTool.getNextLink(), "彩蛋视频壁纸");
    }

    public void refreshVoice() {
        cbVoice.setOnCheckedChangeListener(null);
        boolean isOn = PSetUtils.isVolume(context);
        if (isOn && !LwPrefUtil.isLwpVoiceOpened(context)) {
            LwPrefUtil.setLwpVoiceOpened(context, true);
        } else {
            isOn = LwPrefUtil.isLwpVoiceOpened(context);
        }
        cbVoice.setChecked(isOn);
        cbVoice.setOnCheckedChangeListener(changeListener);
    }

    private void changeNavTabState(int position) {
        LogUtil.i(tag, "changeNavTabState---->" + position);
        if (position == 0) {
            UmUtil.anaTitleTopOp(context, UmConst.HOME_TITLE_RECOMMEND);
            mTabTitleBar.setNavTitleSelected(NavTabTitleHomeBar.NavType.First);
        } else if (position == 1) {
            UmUtil.anaTitleTopOp(context, UmConst.HOME_TITLE_NEW);
            mTabTitleBar.setNavTitleSelected(NavTabTitleHomeBar.NavType.Second);
        } else if (position == 2) {
            UmUtil.anaTitleTopOp(context, UmConst.HOME_TITLE_CAT);
            mTabTitleBar.setNavTitleSelected(NavTabTitleHomeBar.NavType.Third);
        } else if (position == 3) {
            UmUtil.anaTitleTopOp(context, UmConst.HOME_TITLE_DIY);
            UmUtil.anaDIYPageView(context,"定制");
            mTabTitleBar.setNavTitleSelected(NavTabTitleHomeBar.NavType.Forth);
        }
    }

    @Override
    protected void onStartLazy() {
        super.onStartLazy();
        refreshVoice();
    }
}
