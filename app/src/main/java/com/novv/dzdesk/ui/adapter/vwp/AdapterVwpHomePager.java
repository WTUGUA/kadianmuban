package com.novv.dzdesk.ui.adapter.vwp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.novv.dzdesk.ui.adapter.SmartFragmentStatePagerAdapter;
import com.novv.dzdesk.ui.fragment.ae.TabAEResFragment;
import com.novv.dzdesk.ui.fragment.vwp.TabVwpCatFragment;
import com.novv.dzdesk.ui.fragment.vwp.TabVwpResFragment;

import static com.novv.dzdesk.ui.fragment.vwp.TabVwpResFragment.TYPE_NEW;
import static com.novv.dzdesk.ui.fragment.vwp.TabVwpResFragment.TYPE_RECOMMEND;

public class AdapterVwpHomePager extends SmartFragmentStatePagerAdapter {

    public AdapterVwpHomePager(FragmentManager fragmentManager) {
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
                fragment = TabVwpResFragment.newInstance(TYPE_RECOMMEND, null);
                break;
            case 1:
                fragment = TabVwpResFragment.newInstance(TYPE_NEW, null);
                break;
            case 2:
                fragment = new TabVwpCatFragment();
                break;
            case 3:
                fragment = new TabAEResFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}