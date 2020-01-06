package com.novv.dzdesk.util;

import com.ark.adkit.basics.utils.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.novv.dzdesk.http.utils.Urls;

public class DiyEvent {

  public static void onEvent(final String id, TYPE type) {
    String url = Urls.defaultUrl + "v1/custom/analytics";
    OkGo.<String>post(url)
        .params("id", id)
        .params("type", type.name())
        .execute(new StringCallback() {
          @Override public void onSuccess(Response<String> response) {
            LogUtils.v("ana--->" + id + "," + response.body());
          }
        });
  }

  public enum TYPE {
    click, play, use, set, save, share
  }
}
