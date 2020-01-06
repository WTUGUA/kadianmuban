package com.novv.dzdesk.ui.activity.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.ark.tools.medialoader.AudioItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.adapter.local.AdapterLocalAudios;
import com.novv.dzdesk.ui.presenter.PresentAudioSelect;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioSelectActivity extends XAppCompatActivity<PresentAudioSelect> {

  private SmartRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private View btnBack;
  private AdapterLocalAudios mAdapter;

  @Override
  public int getLayoutId() {
    return R.layout.layout_audio_select;
  }

  @Nullable
  @Override
  public PresentAudioSelect newP() {
    return new PresentAudioSelect();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRecyclerView = findViewById(R.id.recyclerView);
    mRefreshLayout = findViewById(R.id.refreshLayout);
    btnBack = findViewById(R.id.btn_back);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    ImmersionBar.with(this)
        .titleBar(findViewById(R.id.ll_top))
        .navigationBarColor(R.color.color_primary_dark)
        .init();
    btnBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    mAdapter = new AdapterLocalAudios(new ArrayList<AudioItem>());
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    mRecyclerView.setAdapter(mAdapter);
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent();
        AudioItem entity = mAdapter.getData().get(position);
        String time = mAdapter.timeParse(entity.timeLong);
        File file = new File(entity.path);
        if (TextUtils.isEmpty(time) || !file.exists()) {
          ToastUtil.showGeneralToast(context, "当前音频不可用");
          if (file.exists()) {
            file.delete();
            mAdapter.notifyItemRemoved(position);
          }
          return;
        }
        intent.putExtra("data", entity.path);
        setResult(RESULT_OK, intent);
        finish();
      }
    });
    mRefreshLayout.setEnableAutoLoadMore(true);
    mAdapter.setEnableLoadMore(false);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadLocalAudios(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadLocalAudios(false);
      }
    });
    getP().loadLocalAudios(false);
  }

  public void showProgress() {

  }

  public void hideProgress() {

  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdapter.replaceData(new ArrayList<AudioItem>());
      View view = mAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(this) ? R.mipmap.ic_empty_favor :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("没有音频资源~");
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

  public void setNewData(List<AudioItem> newData) {
    mAdapter.replaceData(newData);
    mRefreshLayout.finishRefresh();
    if (newData.isEmpty()) {
      setError(false);
    }
  }

  public void addMoreData(List<AudioItem> moreData) {
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
