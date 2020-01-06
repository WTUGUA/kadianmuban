package com.novv.dzdesk.ui.fragment.picker;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.baseui.XPresent;
import com.ark.tools.medialoader.InterfaceContract;
import com.ark.tools.medialoader.MediaSearch;
import com.ark.tools.medialoader.VideoItem;
import com.novv.dzdesk.util.PathUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalVideosPresenter extends XPresent<LocalVideosFragment> {

  public void loadLocalVideos(final boolean isLoadMore) {
    if (hasV()) {
      if (isLoadMore) {
        getV().addMoreData(new ArrayList<String>());
        return;
      }
      new MediaSearch(getV().requireActivity())
          .setLoadingCallback(new InterfaceContract.LoadingCallBack() {
            @Override
            public void showLoading() {
              if (hasV()) {
                getV().showProgress();
              }
            }

            @Override
            public void hideLoading() {
              if (hasV()) {
                getV().hideProgress();
              }
            }
          })
          .setErrorCallback(new InterfaceContract.ErrorCallBack() {
            @Override
            public void dealError(@NonNull String message) {
              if (hasV()) {
                getV().getVDelegate().toastShort(message);
              }
            }
          })
          .setSelectionByFormat(new String[] { "MP4", "mp4" })
          .searchVideos(new InterfaceContract.DataImpl<VideoItem>() {
            @Override
            public void onFinish(@NonNull final List<VideoItem> list) {
              Run.onBackground(new Action() {
                @Override
                public void call() {
                  final List<String> newList = new ArrayList<>();
                  File file = new File(PathUtils.getLwpDownloadDir());
                  for (VideoItem item : list) {
                    File video = new File(item.path);
                    File parent = new File(item.path).getParentFile();
                    if (video.exists() && parent != null && !TextUtils.equals(
                        file.getAbsolutePath(),
                        parent.getAbsolutePath())) {
                      newList.add(item.path);
                    }
                  }
                  if (hasV()) {
                    Run.onUiAsync(new Action() {
                      @Override
                      public void call() {
                        getV().setNewData(newList);
                      }
                    });
                  }
                }
              });
            }
          });
    }
  }
}
