package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.ark.tools.medialoader.InterfaceContract;
import com.ark.tools.medialoader.MediaSearch;
import com.ark.tools.medialoader.VideoItem;
import com.novv.dzdesk.ui.activity.local.VideoSelectActivity;
import com.novv.dzdesk.util.PermissionsUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PresentVideoSelect extends XPresent<VideoSelectActivity> {

  public void loadLocalVideos() {
    if (hasV()) {
      PermissionsUtils.checkStorage(getV(), new PermissionsUtils.OnPermissionBack() {
        @Override public void onResult(boolean success) {
          if (!success) {
            if (hasV()) {
              getV().hideProgress();
            }
            return;
          }
          new MediaSearch(getV())
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
                public void onFinish(@NonNull List<VideoItem> list) {
                  List<VideoItem> newList = new ArrayList<>();
                  for (int i = 0; i < list.size(); i++) {
                    VideoItem item = list.get(i);
                    File file = new File(item.path);
                    long time = item.timeLong;
                    if (file.exists()) {
                      if (time >= 1000 && time <= 60000 * 15) {
                        newList.add(item);
                      }
                    }
                  }
                  if (hasV()) {
                    getV().onLoad(newList);
                  }
                }
              });
        }
      });
    }
  }
}
