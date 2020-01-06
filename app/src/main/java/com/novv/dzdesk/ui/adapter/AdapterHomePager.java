package com.novv.dzdesk.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.novv.dzdesk.ui.fragment.nav.NavAvatarFragment;
import com.novv.dzdesk.ui.fragment.nav.NavHomeFragment;
import com.novv.dzdesk.ui.fragment.nav.NavPersonFragment;
import com.novv.dzdesk.ui.fragment.nav.NavRingsFragment;

public class AdapterHomePager extends SmartFragmentStatePagerAdapter {

    public AdapterHomePager(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = getRegisteredFragment(position);
        if (fragment != null) {
            return fragment;
        }
        switch (position) {
            case 0:
                fragment = new NavHomeFragment();
                break;
            case 1:
                fragment = new NavAvatarFragment();
                break;
            case 2:
                fragment = new Fragment();
                break;
            case 3:
                fragment = new NavRingsFragment();
                break;
            case 4:
                fragment = new NavPersonFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 5;
    }
}