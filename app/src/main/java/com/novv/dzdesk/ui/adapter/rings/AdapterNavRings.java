package com.novv.dzdesk.ui.adapter.rings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.novv.dzdesk.ui.adapter.SmartFragmentStatePagerAdapter;
import com.novv.dzdesk.ui.fragment.rings.TabRingsCatFragment;
import com.novv.dzdesk.ui.fragment.rings.TabRingsDataFragment;
import com.novv.dzdesk.ui.fragment.rings.TabRingsWebFragment;

public class AdapterNavRings extends SmartFragmentStatePagerAdapter {

    public AdapterNavRings(FragmentManager fragmentManager) {
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
                fragment = new TabRingsWebFragment();
                break;
            case 1:
                fragment = TabRingsDataFragment.getInstance(0, new Bundle());
                break;
            case 2:
                fragment = TabRingsDataFragment.getInstance(1, new Bundle());
                break;
            case 3:
                fragment = new TabRingsCatFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
