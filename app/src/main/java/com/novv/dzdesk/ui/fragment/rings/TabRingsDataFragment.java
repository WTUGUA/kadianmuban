package com.novv.dzdesk.ui.fragment.rings;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Toast;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.player.MediaManager;
import com.novv.dzdesk.player.PlayState;
import com.novv.dzdesk.res.model.RingsBean;
import com.novv.dzdesk.res.model.RingsRespons;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.adapter.rings.AdapterRingsRes;
import com.novv.dzdesk.ui.fragment.nav.NavRingsFragment;
import com.novv.dzdesk.util.APPAlertUtils;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.RingsUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TabRingsDataFragment extends XLazyFragment {

  private int request_type = 0;
  private SmartRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private AdapterRingsRes mAdapter;
  private boolean isLoading;
  private String cid = "";
  private MediaManager mediaManager;
  private int progress;
  private ProgressDialog mProgressDialog;
  private boolean reDownload;
  private int skip = 0;

  public static TabRingsDataFragment getInstance(int request_type, Bundle bundle) {
    bundle.putInt("request_type", request_type);
    TabRingsDataFragment fragment = new TabRingsDataFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public int getLayoutId() {
    return R.layout.fragment_data_rings;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    refreshLayout = rootView.findViewById(R.id.refreshLayout);
    recyclerView = rootView.findViewById(R.id.recyclerView);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    Bundle argument = getArguments();
    if (argument != null) {
      request_type = argument.getInt("request_type");
      if (request_type == 2) {
        cid = argument.getString("cid", "590163dc31f6136f13660bb3");
      }
    }
    mProgressDialog = new ProgressDialog(context);
    mProgressDialog.setMessage("下载中...");
    mProgressDialog.setIndeterminate(true);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mProgressDialog.setCancelable(true);
    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        FileDownloader.getImpl().clearAllTaskData();
      }
    });
    NavRingsFragment parent = ((NavRingsFragment) getParentFragment());
    if (parent != null) {
      mediaManager = parent.getMediaManager();
    }
    if (mediaManager == null) {
      mediaManager = new MediaManager(getActivity());
    }
    mAdapter = new AdapterRingsRes(new ArrayList<RingsBean>());
    recyclerView
        .setLayoutManager(
            new LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
                false));
    recyclerView.setAdapter(mAdapter);
    mAdapter.setEnableLoadMore(false);
    refreshLayout.setEnableAutoLoadMore(true);
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadDatas(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoading = false;
        loadDatas(true);
      }
    });
    ((SimpleItemAnimator) recyclerView.getItemAnimator())
        .setSupportsChangeAnimations(false);
    mAdapter.setOnSelectListener(new AdapterRingsRes.OnSelectListener() {
      @Override
      public void onSelected(View view, int index) {
        resetPath(index);
      }
    });
    mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override
      public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        switch (view.getId()) {
          case R.id.btnPausePlay:
            startOrStopPlay();
            break;
          case R.id.btnAction:
            UmUtil.anaEvent(context, UmConst.CLICK_DOWNLOAD_RINGS);
            download(mAdapter.getData().get(position), false);
            break;
          case R.id.btnSet:
            download(mAdapter.getData().get(position), true);
            break;
        }
      }
    });
    loadDatas(true);
  }

  private void loadDatas(final boolean isPull) {
    if (isLoading) {
      return;
    }
    if (isPull) {
      skip = 0;
    }
    String mSkip = String.valueOf(skip);
    skip += 15;
    isLoading = true;
    LogUtil.i("logger", "request_type--->" + request_type + ",cid--->" + cid);
    ServerApi.getRings(request_type, mSkip, cid)
        .compose(DefaultScheduler.<RingsRespons>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<RingsRespons>() {
          @Override
          public void onFailure(Throwable throwable) {
            isLoading = false;
          }

          @Override
          public void onSuccess(RingsRespons result) {
            isLoading = false;
            List<RingsBean> mList = new ArrayList<>();
            if (result != null && result.getResp() != null) {
              if (result.getResp().getMsg() != null) {
                mList.addAll(result.getResp().getMsg());
              }
              if (!LoginContext.getInstance().isVip() && !mList.isEmpty()) {
                RingsBean ringsBean = new RingsBean();
                ringsBean.setAdFlag(true);
                mList.add(ringsBean);
              }
            }
            if (isPull) {
              onRefresh(mList);
            } else {
              onLoadMore(mList);
            }
          }
        });
  }

  private void onRefresh(@NonNull List<RingsBean> newData) {
    mAdapter.addData(newData);
    refreshLayout.finishRefresh();
  }

  private void onLoadMore(@NonNull List<RingsBean> newData) {
    mAdapter.addData(newData);
    if (newData.isEmpty()) {
      refreshLayout.finishLoadMore(0, true, true);
    } else {
      refreshLayout.finishLoadMore();
    }
  }

  private void resetPath(int index) {
    progress = 0;
    final RingsBean ringsBean = mAdapter.getData().get(index);
    if (ringsBean != null && mediaManager != null) {
      mediaManager.resetPath(ringsBean.getDurl())
          .setOnMediaPlayerListener(new MediaManager.OnMediaPlayerListener() {
            @Override
            public void onMediaPlayerProgress(int p) {
              if (progress != p) {
                uiStateProgress(PlayState.STATE_PLAYING, ringsBean, p);
              }
            }

            @Override
            public void onMediaPlayerComplete() {
              uiStateProgress(PlayState.STATE_UN_PLAYING, ringsBean, 0);
            }

            @Override
            public void onMediaPlayerError() {
              uiStateProgress(PlayState.STATE_UN_PLAYING, ringsBean, 0);
            }

            @Override
            public void onMediaPlayerPause() {
              uiStateProgress(PlayState.STATE_UN_PLAYING, ringsBean, progress);
            }

            @Override
            public void onMediaPlayerStop() {
              uiStateProgress(PlayState.STATE_UN_PLAYING, ringsBean, progress);
            }

            @Override
            public void onMediaPlayerBuffering(int percent) {
              uiStateProgress(PlayState.STATE_LOADING, ringsBean, progress);
            }

            @Override
            public void onMediaPlayerPrepared() {
              uiStateProgress(PlayState.STATE_PLAYING, ringsBean, progress);
            }
          }).start();
    }
  }

  private void startOrStopPlay() {
    try {
      if (mediaManager.isPlaying()) {
        mediaManager.pause();
      } else {
        mediaManager.seekTo(progress);
        UmUtil.anaEvent(context, UmConst.CLICK_RINGS_PLAY);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void download(final RingsBean ringsBean, final boolean set) {
    new APPAlertUtils().handleRings(context, new APPAlertUtils.OnAlertListener() {
      @Override
      public void onAlertNext() {
        File dir = new File(Const.Dir.RINGS_DIR);
        File ringFile = new File(dir, ringsBean.getId() + ".aar");
        String path = ringFile.getAbsolutePath();
        ringsBean.setFilePath(path);
        FileDownloader.getImpl()
            .create(ringsBean.getDurl())
            .setPath(path)
            .setListener(new FileDownloadSampleListener() {

              @Override
              protected void pending(BaseDownloadTask task, int soFarBytes,
                  int totalBytes) {
                mProgressDialog.show();
              }

              @Override
              protected void progress(BaseDownloadTask task, int soFarBytes,
                  int totalBytes) {
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(soFarBytes / totalBytes * 100);
              }

              @Override
              protected void completed(BaseDownloadTask task) {
                reDownload = false;
                mProgressDialog.dismiss();
                if (set) {
                  RingsUtil.setCalling(context, ringsBean);
                  Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
                } else {
                  Toast.makeText(context, ringsBean.getName() + "已下载",
                      Toast.LENGTH_SHORT).show();
                }
              }

              @Override
              protected void error(BaseDownloadTask task, Throwable e) {
                mProgressDialog.dismiss();
                reDownload = true;
                if (set) {
                  Toast.makeText(context, "设置失败,请稍后重试", Toast.LENGTH_SHORT)
                      .show();
                } else {
                  Toast.makeText(context, "下载失败,请稍后重试", Toast.LENGTH_SHORT)
                      .show();
                }
              }
            }).start();
      }
    });
  }

  private void uiStateProgress(int playState, RingsBean ringsBean, int progress) {
    this.progress = progress;
    ringsBean.setProgress(progress);
    ringsBean.setPlayState(playState);
    mAdapter.updateItem(ringsBean);
  }

  @Override
  protected void onStopLazy() {
    super.onStopLazy();
    if (mediaManager != null) {
      mediaManager.stop();
    }
    if (mAdapter != null) {
      mAdapter.resetSelect();
    }
  }

  @Override
  protected void onPauseLazy() {
    super.onPauseLazy();
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    try {
      if (mediaManager != null) {
        mediaManager.release();
        mediaManager = null;
      }
    } catch (Exception e) {
      //
    }
  }
}
