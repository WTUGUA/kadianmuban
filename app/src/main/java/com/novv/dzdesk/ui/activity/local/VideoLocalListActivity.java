package com.novv.dzdesk.ui.activity.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.adapter.local.AdapterLocalRes;
import com.novv.dzdesk.ui.fragment.picker.LocalVideosFragment;
import com.novv.dzdesk.ui.fragment.picker.VwpLocalFragment;
import com.novv.dzdesk.ui.view.MyTabLayout;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;

public class VideoLocalListActivity extends XAppCompatActivity implements
    View.OnClickListener {

  private MyTabLayout myTabLayout;
  private ViewPager viewPager;
  private View btnBack;
  private TextView btnApply;
  private ProgressBar progressBar;
  private List<String> mLocalVideos = null;
  private List<String> mVwpList = null;
  private ArrayList<String> mInputList = new ArrayList<>();

  @Override
  public int getLayoutId() {
    return R.layout.activity_video_local_list;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    myTabLayout = findViewById(R.id.mytablayout);
    viewPager = findViewById(R.id.viewpager);
    progressBar = findViewById(R.id.progress_bar);
    btnBack = findViewById(R.id.btn_back);
    btnApply = findViewById(R.id.btn_apply);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    Intent intent = getIntent();
    if (intent != null) {
      ArrayList<String> list = intent.getStringArrayListExtra("data");
      if (list != null) {
        mInputList.addAll(list);
      }
    }
    LocalVideosFragment localVideosFragment = new LocalVideosFragment();
    VwpLocalFragment vwpLocalFragment = new VwpLocalFragment();
    Bundle b1 = new Bundle();
    b1.putStringArrayList("data", mInputList);
    localVideosFragment.setArguments(b1);
    Bundle b2 = new Bundle();
    b2.putStringArrayList("data", mInputList);
    vwpLocalFragment.setArguments(b2);
    ArrayList<Fragment> fragments = new ArrayList<>();
    fragments.add(vwpLocalFragment);
    fragments.add(localVideosFragment);
    AdapterLocalRes adapterLocalRes = new AdapterLocalRes(getSupportFragmentManager(),
        fragments);
    viewPager.setOffscreenPageLimit(2);
    viewPager.setAdapter(adapterLocalRes);
    myTabLayout.setupWithViewPager(viewPager);
    btnBack.setOnClickListener(this);
    btnApply.setOnClickListener(this);
    refreshSelect();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.btn_back:
        finish();
        break;
      case R.id.btn_apply:
        onSelected();
        break;
    }
  }

  private void onSelected() {
    onShowProgress(false);
    Intent intent = new Intent();
    ArrayList<String> data = new ArrayList<>();
    if (mLocalVideos != null) {
      data.addAll(mLocalVideos);
    }
    if (mLocalVideos != null) {
      data.addAll(mVwpList);
    }
    intent.putStringArrayListExtra("data", data);
    setResult(RESULT_OK, intent);
    finish();
  }

  public void onShowProgress(final boolean show) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
      }
    });
  }

  public void onVwpLocalSelected(List<String> selectVideo) {
    Log.e("logger", "onVwpLocalSelected:" + selectVideo.size());
    mVwpList = selectVideo;
    refreshSelect();
  }

  public void onLocalVideosSelected(List<String> selectVideo) {
    Log.e("logger", "onLocalVideosSelected:" + selectVideo.size());
    mLocalVideos = selectVideo;
    refreshSelect();
  }

  private void refreshSelect() {
    int size;
    if (mVwpList != null && mLocalVideos != null) {
      size = mVwpList.size() + mLocalVideos.size();
    } else {
      size = mInputList.size();
    }
    btnApply.setText(String.format("确定添加(%d)", size));
    btnApply.setVisibility(size == 0 ? View.GONE : View.VISIBLE);
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);
    super.onResume();
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
  }
}
