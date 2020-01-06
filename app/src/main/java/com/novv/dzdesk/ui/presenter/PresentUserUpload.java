package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.activity.user.UserUploadActivity;

import java.util.List;

public class PresentUserUpload extends XPresent<UserUploadActivity> {

    private int limit = 15, skip = 0;

    public void loadUserUpload(final boolean isLoadMore) {
        if (!hasV()) {
            return;
        }
        if (!isLoadMore) {
            skip = 0;
        }
        ServerApi.getUploadList(skip, limit)
                .compose(DefaultScheduler.<BaseResult<List<ResourceBean>>>getDefaultTransformer())
                .subscribe(new BaseObserver<List<ResourceBean>>() {
                    @Override
                    public void onSuccess(@NonNull List<ResourceBean> resourceBeans) {
                        skip += limit;
                        if (hasV()) {
                            if (isLoadMore) {
                                getV().addMoreData(resourceBeans);
                            } else {
                                getV().setNewData(resourceBeans);
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
