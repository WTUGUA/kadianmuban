package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XPresent;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.DownloadInfo;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.activity.user.UserDownloadActivity;
import com.novv.dzdesk.util.PathUtils;
import com.novv.dzdesk.util.PermissionsUtils;
import com.novv.dzdesk.util.ToastUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresentUserDownload extends XPresent<UserDownloadActivity> {

  private int skip = 0, limit = 30;
  private int successCount;
  private int allTaskSize;
  private int failCount;
  private FileDownloadListener downloadListener = new FileDownloadSampleListener() {

    @Override
    protected void started(BaseDownloadTask task) {
      super.started(task);
      LogUtils.e("download start");
      getV().showProgressDialog();
      getV().setProgress(successCount + failCount + 1, allTaskSize);
    }

    @Override
    protected void completed(BaseDownloadTask task) {
      super.completed(task);
      successCount++;
      LogUtils.e("download completed :" + successCount);
      if (hasV() && successCount + failCount == allTaskSize) {
        getV().dismissDialog();
      }
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
      super.error(task, e);
      failCount++;
      LogUtils.e("download error :" + failCount);
    }
  };

  private static List<String> getLocalVideos() {
    List<String> videoEntities = new ArrayList<>();
    File file = new File(PathUtils.getLwpDownloadDir());
    File[] files = file.listFiles();
    if (files != null) {
      for (File subFile : files) {
        if (!subFile.isDirectory()) {
          String filename = subFile.getName();
          if (filename.length() == "597b29c9e7bce756b71ca58c.mp4".length() && filename
              .endsWith(".mp4")) {
            String id = filename.substring(0, filename.lastIndexOf("."));
            videoEntities.add(id);
          } else {
            subFile.delete();
          }
        }
      }
    }
    return videoEntities;
  }

  public void loadUserDownload(final boolean isLoadMore) {
    if (!isLoadMore) {
      skip = 0;
    }
    ServerApi.getDownload(limit, skip)
        .compose(DefaultScheduler.<BaseResult<List<DownloadInfo>>>getDefaultTransformer())
        .subscribe(new BaseObserver<List<DownloadInfo>>() {

          @Override
          public void onSuccess(@NonNull List<DownloadInfo> downloadInfos) {
            skip += limit;
            List<ResourceBean> list = new ArrayList<>();
            for (DownloadInfo downloadInfo : downloadInfos) {
              list.add(downloadInfo.getInfo());
            }
            if (hasV()) {
              if (isLoadMore) {
                getV().addMoreData(list);
              } else {
                getV().setNewData(list);
              }
            }
          }

          @Override
          public void onFailure(int code, @NonNull String message) {
            if (hasV()) {
              getV().setError(isLoadMore);
            }
          }
        });
  }

  public void loadLocalDownload() {
    if (!hasV()) {
      return;
    }
    getV().showProgressDialog();
    Run.onBackground(new Action() {
      @Override
      public void call() {
        final List<String> mStringList = getLocalVideos();
        Run.onUiAsync(new Action() {
          @Override
          public void call() {
            combDownload(mStringList);
          }
        });
      }
    });
  }

  private void combDownload(final List<String> localDownloads) {
    if (!hasV() || getV().isFinishing()) {
      return;
    }
    LogUtils.e("合并下载，本地文件" + localDownloads.size());
    ServerApi.upDownloads(localDownloads)
        .compose(DefaultScheduler.<BaseResult<String>>getDefaultTransformer())
        .subscribe(new BaseObserver<String>() {
          @Override
          public void onSuccess(@NonNull String s) {
            LogUtils.e("上传下载记录success：" + s);
            if (hasV()) {
              PermissionsUtils.checkStorage(getV(), new PermissionsUtils.OnPermissionBack() {
                @Override public void onResult(boolean success) {
                  if (success) {
                    loadAllUserDownload(localDownloads);
                  }
                }
              });
            }
          }

          @Override
          public void onFailure(int code, @NonNull String message) {
            LogUtils.e("上传下载记录error：" + message);
            if (hasV()) {
              ToastUtil.showToast(getV(), "同步失败");
              getV().dismissDialog();
            }
          }
        });
  }

  public void loadAllUserDownload(final List<String> localDownloads) {
    ServerApi.getDownload(999, 0)
        .compose(DefaultScheduler.<BaseResult<List<DownloadInfo>>>getDefaultTransformer())
        .subscribe(new BaseObserver<List<DownloadInfo>>() {

          @Override
          public void onSuccess(@NonNull final List<DownloadInfo> downloadInfos) {
            Run.onBackground(new Action() {
              @Override
              public void call() {
                final List<ResourceBean> needDownloadList = new ArrayList<>();
                final List<ResourceBean> allUserList = new ArrayList<>();
                LogUtils.e("downloadInfos size:" + downloadInfos.size());
                for (DownloadInfo downloadInfo : downloadInfos) {
                  ResourceBean resourceBean = downloadInfo.getInfo();
                  allUserList.add(resourceBean);
                  LogUtils.e("allUserList add:" + resourceBean.get_id());
                  if (!localDownloads.contains(resourceBean.get_id())) {
                    needDownloadList.add(downloadInfo.getInfo());
                    LogUtils.e("needDownloadList add:" + resourceBean.get_id());
                  }
                }
                LogUtils.e("needDownloadList/allUserList" + needDownloadList.size()
                    + "/" + allUserList.size());
                Run.onUiAsync(new Action() {
                  @Override
                  public void call() {
                    if (hasV()) {
                      getV().getVDelegate().toastShort("已经同步下载记录");
                      loadUserDownload(false);
                      if (!needDownloadList.isEmpty()) {
                        successCount = 0;
                        failCount = 0;
                        allTaskSize = needDownloadList.size();
                        final FileDownloadQueueSet queueSet = new FileDownloadQueueSet(
                            downloadListener);
                        final List<BaseDownloadTask> tasks = new ArrayList<>();
                        for (ResourceBean resourceBean : needDownloadList) {
                          tasks.add(FileDownloader.getImpl()
                              .create(resourceBean.getLinkMp4())
                              .setPath(resourceBean.getMp4File()
                                  .getAbsolutePath()));
                        }
                        queueSet.disableCallbackProgressTimes();
                        queueSet.setAutoRetryTimes(0);
                        queueSet.downloadSequentially(tasks);
                        queueSet.start();
                      } else {
                        getV().dismissDialog();
                      }
                    }
                  }
                });
              }
            });
          }

          @Override
          public void onFailure(int code, @NonNull String message) {
            if (hasV()) {
              getV().getVDelegate().toastShort(message);
              getV().dismissDialog();
            }
          }
        });
  }

  public void delDownload(final List<ResourceBean> delDatas) {
    Run.onBackground(new Action() {
      @Override
      public void call() {
        final List<String> ids = new ArrayList<>();
        File dir = new File(PathUtils.getLwpDownloadDir());
        for (int i = 0; i < delDatas.size(); i++) {
          ids.add(delDatas.get(i).get_id());
          try {
            new File(dir, delDatas.get(i).get_id() + ".mp4").delete();
          } catch (Exception e) {
            //
          }
        }
        Run.onUiAsync(new Action() {
          @Override
          public void call() {
            ServerApi.delDownloads(ids)
                .compose(
                    DefaultScheduler.<BaseResult<String>>getDefaultTransformer())
                .subscribe(new BaseObserver<String>() {
                  @Override
                  public void onSuccess(@NonNull String s) {
                    if (hasV()) {
                      getV().onResReload();
                    }
                  }

                  @Override
                  public void onFailure(int code, @NonNull String message) {
                    if (hasV()) {
                      getV().onResReload();
                    }
                  }
                });
          }
        });
      }
    });
  }

  public void cancel() {
    FileDownloader.getImpl().pauseAll();
  }
}
