package com.novv.dzdesk.ui.activity.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.ark.tools.medialoader.VideoItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.adapter.local.AdapterLocalVideos;
import com.novv.dzdesk.ui.presenter.PresentVideoSelect;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.video.op.VideoTrimActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoSelectActivity extends XAppCompatActivity<PresentVideoSelect> {

  private RecyclerView recyclerView;
  private SmartRefreshLayout refreshLayout;
  private AdapterLocalVideos mAdapterLocalVideos;
  private View btnBack;

  @Override
  public int getLayoutId() {
    return R.layout.activity_video_select;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    recyclerView = findViewById(R.id.recyclerView);
    refreshLayout = findViewById(R.id.refreshLayout);
    btnBack = findViewById(R.id.btn_back);
  }

  @Nullable
  @Override
  public PresentVideoSelect newP() {
    return new PresentVideoSelect();
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
    mAdapterLocalVideos = new AdapterLocalVideos(new ArrayList<VideoItem>());
    recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
    recyclerView.setAdapter(mAdapterLocalVideos);
    mAdapterLocalVideos.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(context, VideoTrimActivity.class);
        VideoItem entity = mAdapterLocalVideos.getData().get(position);
        File file = new File(entity.path);
        if (!file.exists()) {
          ToastUtil.showGeneralToast(VideoSelectActivity.this, "当前视频已被删除");
          mAdapterLocalVideos.notifyItemRemoved(position);
          return;
        }
        intent.putExtra("data", entity.path);
        startActivity(intent);
      }
    });
    refreshLayout.setEnableAutoLoadMore(true);
    refreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadLocalVideos();
      }
    });
    getP().loadLocalVideos();
  }

  public void onLoad(List<VideoItem> list) {
    mAdapterLocalVideos.replaceData(list);
    refreshLayout.finishRefresh();
    if (list.isEmpty()) {
      View emptyView = mAdapterLocalVideos.getEmptyView();
      if (emptyView == null) {
        emptyView = LayoutInflater.from(context)
            .inflate(R.layout.empty_view, null);
        ImageView imageView = emptyView.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(context) ? R.mipmap.ic_empty_nores
                : R.mipmap.ic_empty_nonet);
        TextView textView = emptyView.findViewById(R.id.tvEmpty);
        textView.setText("暂无视频文件,相册中的云端视频\n须下载到本地才能使用~");
        mAdapterLocalVideos.setEmptyView(emptyView);
        emptyView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            refreshLayout.autoRefresh();
          }
        });
      }
    }
  }

  public void showProgress() {

  }

  public void hideProgress() {

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
