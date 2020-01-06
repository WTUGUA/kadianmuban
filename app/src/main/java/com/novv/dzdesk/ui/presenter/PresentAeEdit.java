package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.lansosdk.videoeditor.LanSoEditor;
import com.novv.dzdesk.ui.activity.ae.AEDialogActivity;
import com.tencent.mmkv.MMKV;

public class PresentAeEdit extends XPresent<AEDialogActivity> {

  @Override
  public void attachV(@NonNull AEDialogActivity view) {
    super.attachV(view);
    LanSoEditor.initSDK(view, "gd_LanSongSDK_android4.key");
    int level = LanSoEditor.getCPULevel();
    if (level < -1) {
      if (!MMKV.defaultMMKV().decodeBool("toast_cpu_level")) {
        getV().getVDelegate().toastShort("您的手机性能较差，制作过程出现卡顿请耐心等待");
        MMKV.defaultMMKV().encode("toast_cpu_level", true);
      }
    }
  }

  @Override
  public void detachV() {
    super.detachV();
    LanSoEditor.unInitSDK();
  }
}
