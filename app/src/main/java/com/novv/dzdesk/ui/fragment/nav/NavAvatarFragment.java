package com.novv.dzdesk.ui.fragment.nav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import com.ark.baseui.XLazyFragment;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.adapter.avatar.AdapterNavAvatar;
import com.novv.dzdesk.ui.view.NavTabTitleAvatarBar;

public class NavAvatarFragment extends XLazyFragment {

    private ViewPager mPager;
    private NavTabTitleAvatarBar mTabTitleBar;

    @Override
    public int getLayoutId() {
        return R.layout.nav_avatar_fragment;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        mPager = rootView.findViewById(R.id.viewpager);
        mTabTitleBar = rootView.findViewById(R.id.nav_tab_title_bar);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeNavTabState(position);
            }
        });
        mTabTitleBar.setOnItemClickListener(new NavTabTitleAvatarBar.OnItemClickListener() {
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
        AdapterNavAvatar adapterNavAvatar = new AdapterNavAvatar(getChildFragmentManager());
        mPager.setAdapter(adapterNavAvatar);
        mPager.setOffscreenPageLimit(3);
        mTabTitleBar.performClickNav(NavTabTitleAvatarBar.NavType.Second);
    }

    private void changeNavTabState(int position) {
        if (position == 0) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleAvatarBar.NavType.First);
        } else if (position == 1) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleAvatarBar.NavType.Second);
        } else if (position == 2) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleAvatarBar.NavType.Third);
        } else if (position == 3) {
            mTabTitleBar.setNavTitleSelected(NavTabTitleAvatarBar.NavType.Forth);
        }
    }
}
