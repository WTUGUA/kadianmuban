package com.novv.dzdesk.ui.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.VideoEntity;
import com.novv.dzdesk.ui.activity.ae.AEVideoShareActivity;
import com.novv.dzdesk.ui.adapter.user.AdapterUserMake;
import com.novv.dzdesk.ui.presenter.PresentLocalAE;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.NetUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserAEMakeListActivity extends XAppCompatActivity<PresentLocalAE> implements
    View.OnClickListener {

  private SmartRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private AdapterUserMake mAdapter;
  private TextView mBtnSort;
  private LinearLayout mLlSort;
  private CheckBox mAllCheck;

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_make;
  }

  @Nullable
  @Override
  public PresentLocalAE newP() {
    return new PresentLocalAE();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRefreshLayout = findViewById(R.id.refreshLayout);
    mRecyclerView = findViewById(R.id.recyclerView);
    ImageView btnBack = findViewById(R.id.btn_back);
    mBtnSort = findViewById(R.id.btn_sort);
    mLlSort = findViewById(R.id.ll_sort);
    mAllCheck = findViewById(R.id.all_check);
    btnBack.setOnClickListener(this);
    mBtnSort.setOnClickListener(this);
    mLlSort.setVisibility(View.GONE);
    mAllCheck.setChecked(false);
    mBtnSort.setSelected(false);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    ImmersionBar.with(this)
        .titleBar(findViewById(R.id.ll_top))
        .navigationBarColor(R.color.color_primary_dark)
        .init();
    GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(Color.parseColor("#07c0e9"));
    drawable.setCornerRadius(DeviceUtil.dip2px(this, 2));
    mBtnSort.setBackground(drawable);
    mAllCheck.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView,
              boolean isChecked) {
            if (mBtnSort.isSelected()) {
              mAdapter.setAllChecked(isChecked);
            }
          }
        });
    mAdapter = new AdapterUserMake(new ArrayList<VideoEntity>());
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    mRecyclerView.setAdapter(mAdapter);
    mRefreshLayout.setEnableLoadMore(true);
    mAdapter.setEnableLoadMore(false);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserMake(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserMake(false);
      }
    });
    mAdapter.setOnCheckedChangeListener(new AdapterUserMake.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(boolean check, int size) {
        if (mBtnSort.isSelected()) {
          mBtnSort.setText(String.format("删除(%d)", size));
        } else {
          mBtnSort.setText("整理");
        }
      }

      @Override
      public void onItemClick(int position) {
        Intent intent = new Intent(context, AEVideoShareActivity.class);
        intent.putExtra("mp4Path", mAdapter.getData().get(position).getVideoPath());
        intent.putExtra("isFromMake", true);
        intent.putExtra("mp4Id", mAdapter.getData().get(position).getId());
        startActivity(intent);
      }
    });
    getP().loadUserMake(false);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_back:
        finish();
        break;
      case R.id.btn_sort:
        if (mAdapter.getData().isEmpty()) {
          getVDelegate().toastShort("暂无资源");
          return;
        }
        mBtnSort.setSelected(!mBtnSort.isSelected());
        if (mBtnSort.isSelected()) {
          mAllCheck.setChecked(false);
          mLlSort.setVisibility(View.VISIBLE);
          mAdapter.setShowCheckBox(true);
          mAdapter.notifyDataSetChanged();
          mBtnSort.setText(String.format("删除(%d)", 0));
        } else {
          mAllCheck.setChecked(false);
          mLlSort.setVisibility(View.GONE);
          mAdapter.setShowCheckBox(false);
          mAdapter.notifyDataSetChanged();
          mBtnSort.setText("整理");
          final List<VideoEntity> list = mAdapter.getChecked();
          Run.onBackground(new Action() {
            @Override
            public void call() {
              for (VideoEntity videoEntity : list) {
                new File(videoEntity.getVideoPath()).delete();
              }
              Run.onUiAsync(new Action() {
                @Override
                public void call() {
                  getP().loadUserMake(false);
                }
              });
            }
          });
        }
        break;
    }
  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdapter.replaceData(new ArrayList<VideoEntity>());
      View view = mAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(this) ? R.mipmap.ic_empty_nores :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("没有已制作视频~");
        mAdapter.setEmptyView(view);
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mRefreshLayout.autoRefresh();
          }
        });
      }
    }
  }

  public void setNewData(List<VideoEntity> newData) {
    mAdapter.replaceData(newData);
    mRefreshLayout.finishRefresh();
    if (newData.isEmpty()) {
      setError(false);
    }
  }

  public void addMoreData(List<VideoEntity> moreData) {
    mAdapter.addData(moreData);
    if (moreData.isEmpty()) {
      mRefreshLayout.finishLoadMore(0, true, true);
    } else {
      mRefreshLayout.finishLoadMore();
    }
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ImmersionBar.with(this).destroy();
  }
}
