package com.novv.dzdesk.res.manager;

import com.novv.dzdesk.ui.fragment.nav.NavAvatarFragment;
import com.novv.dzdesk.ui.fragment.nav.NavHomeFragment;
import com.novv.dzdesk.ui.fragment.nav.NavPersonFragment;
import com.novv.dzdesk.ui.fragment.nav.NavRingsFragment;

/**
 * Created by lingyfh on 15/7/11.
 */
public class NavFragmentManager {

    private NavHomeFragment mNavHomeFragment;
    private NavAvatarFragment mNavAvatarFragment;
    private NavRingsFragment mNavRingsFragment;
    private NavPersonFragment mNavPersonFragment;

    public static NavFragmentManager createInstance() {
        return new NavFragmentManager();
    }

    public NavHomeFragment getNavHomeFragment() {
        if (mNavHomeFragment == null) {
            mNavHomeFragment = new NavHomeFragment();
        }
        return mNavHomeFragment;
    }

    public NavAvatarFragment getNavAvatarFragment() {
        if (mNavAvatarFragment == null) {
            mNavAvatarFragment = new NavAvatarFragment();
        }
        return mNavAvatarFragment;
    }

    public NavRingsFragment getNavLocalFragment() {
        if (mNavRingsFragment == null) {
            mNavRingsFragment = new NavRingsFragment();
        }
        return mNavRingsFragment;
    }

    public NavPersonFragment getNavPersonFragment() {
        if (mNavPersonFragment == null) {
            mNavPersonFragment = new NavPersonFragment();
        }
        return mNavPersonFragment;
    }

    public void destoryFragments() {
        try {
            if (mNavHomeFragment != null) {
                mNavHomeFragment.getFragmentManager().beginTransaction()
                        .remove(mNavHomeFragment).commitAllowingStateLoss();
            }

            if (mNavAvatarFragment != null) {
                mNavAvatarFragment.getFragmentManager().beginTransaction()
                        .remove(mNavAvatarFragment).commitAllowingStateLoss();
            }

            if (mNavRingsFragment != null) {
                mNavRingsFragment.getFragmentManager().beginTransaction()
                        .remove(mNavRingsFragment).commitAllowingStateLoss();
            }

            if (mNavPersonFragment != null) {
                mNavPersonFragment.getFragmentManager().beginTransaction()
                        .remove(mNavPersonFragment).commitAllowingStateLoss();
            }

            mNavHomeFragment = null;
            mNavAvatarFragment = null;
            mNavRingsFragment = null;
            mNavPersonFragment = null;

        } catch (Exception e) {
            e.printStackTrace();

        } catch (Error e) {
            e.printStackTrace();

        }
    }
}
