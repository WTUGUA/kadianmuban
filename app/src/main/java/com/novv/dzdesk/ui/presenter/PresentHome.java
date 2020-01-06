package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.ui.HomeActivity;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.tencent.bugly.beta.Beta;

public class PresentHome extends XPresent<HomeActivity> {

  public void downloadPatch() {
    Beta.downloadPatch();
  }

  public void checkLogin() {
    if (!hasV()) {
      return;
    }
    ServerApi.getUserInfo()
        .compose(DefaultScheduler.<BaseResult<UserModel>>getDefaultTransformer())
        .subscribe(new BaseObserver<UserModel>() {
          @Override
          public void onSuccess(@NonNull UserModel userModel) {
            if (hasV()) {
              UmUtil.anaOp(getV(), UmConst.UM_LOGIN_USER_ACTIVE);
            }
          }

          @Override
          public void onFailure(int code, @NonNull String message) {

          }
        });
  }

  public void checkSet() {
    if (!hasV()) {
      return;
    }
    if (LwPrefUtil.isLwpVoiceOpened(getV())) {
      LwVideoLiveWallpaper.voiceNormal(getV().getApplicationContext());
    } else {
      // 静音
      LwVideoLiveWallpaper.voiceSilence(getV().getApplicationContext());
    }
  }
}
