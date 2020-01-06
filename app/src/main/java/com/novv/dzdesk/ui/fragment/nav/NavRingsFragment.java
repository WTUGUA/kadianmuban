package com.novv.dzdesk.ui.fragment.nav;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

import com.ark.baseui.XLazyFragment;
import com.novv.dzdesk.R;
import com.novv.dzdesk.player.MediaManager;
import com.novv.dzdesk.ui.adapter.rings.AdapterNavRings;
import com.novv.dzdesk.ui.fragment.rings.TabRingsWebFragment;
import com.novv.dzdesk.ui.view.NavTabTitleRingsBar;
import com.novv.dzdesk.ui.view.NavTabTitleRingsBar.NavType;

public class NavRingsFragment extends XLazyFragment implements OnClickListener {

    private ViewPager mPager;
    private NavTabTitleRingsBar mTabTitleBar;
    private AdapterNavRings mAdapterNavRings;
    private MediaManager mediaManager;

    public MediaManager getMediaManager() {
        return mediaManager;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mediaManager = new MediaManager(getActivity());
    }

    @Override
    public int getLayoutId() {
        return R.layout.content_rings_fragment;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        mPager = rootView.findViewById(R.id.viewpager);
        mTabTitleBar = rootView.findViewById(R.id.nav_tab_title_bar);
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeNavTabState(position);
            }
        });
        mTabTitleBar.setOnItemClickListener(new NavTabTitleRingsBar.OnItemClickListener() {
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
        mAdapterNavRings = new AdapterNavRings(getChildFragmentManager());
        mPager.setAdapter(mAdapterNavRings);
        mPager.setOffscreenPageLimit(3);
        mTabTitleBar.performClickNav(NavType.First);
    }

    private void changeNavTabState(int position) {
        if (position == 0) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleRingsBar.NavType.First);
        } else if (position == 1) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleRingsBar.NavType.Second);
        } else if (position == 2) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleRingsBar.NavType.Third);
        } else if (position == 3) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleRingsBar.NavType.Forth);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void reload(){
        if (mAdapterNavRings != null) {
            Fragment fragment = mAdapterNavRings.getItem(0);
            if (fragment instanceof TabRingsWebFragment) {
                ((TabRingsWebFragment) fragment).reload();
            }
        }
    }

    public void pausePlay() {
        if (mAdapterNavRings != null) {
            Fragment fragment = mAdapterNavRings.getItem(0);
            if (fragment instanceof TabRingsWebFragment) {
                ((TabRingsWebFragment) fragment).stopPlay();
            }
        }
        if (mediaManager != null && mediaManager.isPlaying()) {
            mediaManager.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mediaManager != null) {
            mediaManager.stop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            mediaManager.release();
            mediaManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
