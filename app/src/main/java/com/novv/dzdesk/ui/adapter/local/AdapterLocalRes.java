package com.novv.dzdesk.ui.adapter.local;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class AdapterLocalRes extends FragmentPagerAdapter {

    private String[] titles = {"我的下载", "本机视频"};
    private ArrayList<Fragment> fragmentlist = new ArrayList<Fragment>();

    public AdapterLocalRes(FragmentManager fm, ArrayList<Fragment> fragmentlist) {
        super(fm);
        this.fragmentlist = fragmentlist;
    }

    public ArrayList<Fragment> getFragmentlist() {
        return fragmentlist;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentlist.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }
}
