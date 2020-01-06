package com.novv.dzdesk.ui.adapter.avatar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.novv.dzdesk.ui.adapter.SmartFragmentStatePagerAdapter;
import com.novv.dzdesk.ui.fragment.avatar.TabAvatarCatFragment;
import com.novv.dzdesk.ui.fragment.avatar.TabAvatarRcmdFragment;
import com.novv.dzdesk.ui.fragment.avatar.TabAvatarResFragment;

public class AdapterNavAvatar extends SmartFragmentStatePagerAdapter {

    public AdapterNavAvatar(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment=getRegisteredFragment(position);
        if (fragment!=null){
            return fragment;
        }
        switch (position){
            case 0:
                fragment=TabAvatarResFragment.getInstance(0,new Bundle());
                break;
            case 1:
                fragment=new TabAvatarRcmdFragment();
                break;
            case 2:
                fragment=new TabAvatarCatFragment();
                break;
            case 3:
                fragment=TabAvatarResFragment.getInstance(1, new Bundle());
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
