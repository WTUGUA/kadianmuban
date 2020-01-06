package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.fragment.ae.TabAEResFragment;
import java.util.List;

public class PresentAERes extends XPresent<TabAEResFragment> {

  private int limit = 15, skip = 0;

  public void loadAERes(final boolean isLoadMore) {
    if (!hasV()) {
      return;
    }
    if (!isLoadMore) {
      skip = 0;
    }
    int mSkip = skip;
    skip += limit;
    ServerApi.getDiyRes(mSkip, limit)
        .compose(DefaultScheduler.<BaseResult<List<VModel>>>getDefaultTransformer())
        .subscribe(new BaseObserver<List<VModel>>() {
          @Override
          public void onSuccess(@NonNull List<VModel> vModels) {
            if (hasV()) {
              if (isLoadMore) {
                getV().addMoreData(vModels);
              } else {
                getV().setNewData(vModels);
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
}
