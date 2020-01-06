package com.novv.dzdesk.ui.adapter.user;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.novv.dzdesk.ui.adapter.SmartFragmentStatePagerAdapter;
import com.novv.dzdesk.ui.fragment.vwp.TabUserResFragment;

public class AdapterUserDetail extends SmartFragmentStatePagerAdapter {

  private String userId;

  public AdapterUserDetail(FragmentManager fragmentManager, String userId) {
    super(fragmentManager);
    this.userId = userId;
  }

  @Override
  public Fragment getItem(int position) {
    Fragment fragment = getRegisteredFragment(position);
    if (fragment != null) {
      return fragment;
    }
    switch (position) {
      case 0:
        fragment = TabUserResFragment.newInstance(0, userId);
        break;
      case 1:
        fragment = TabUserResFragment.newInstance(1, userId);
        break;
      default:
        break;
    }

    return fragment;
  }

  @Override
  public int getCount() {
    return 2;
  }
}
