package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.fragment.vwp.TabUserResFragment;

import java.util.List;

public class PresentUserRes extends XPresent<TabUserResFragment> {

    public static final int TYPE_FAVOR = 0;
    public static final int TYPE_UPLOAD = 1;
    private int limit = 15, skip = 0;

    public void loadUserRes(int requestType, String uid, final boolean isLoadMore) {
        if (!hasV()) {
            return;
        }
        if (TextUtils.isEmpty(uid)) {
            getV().getVDelegate().toastShort("该用户信息不存在");
            return;
        }
        if (!isLoadMore) {
            skip = 0;
        }
        if (requestType == TYPE_FAVOR) {
            ServerApi.getFavorList(uid, skip, limit)
                    .compose(
                            DefaultScheduler.<BaseResult<List<ResourceBean>>>getDefaultTransformer())
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
        } else if (requestType == TYPE_UPLOAD) {
            ServerApi.getUploadList(uid, skip, limit)
                    .compose(
                            DefaultScheduler.<BaseResult<List<ResourceBean>>>getDefaultTransformer())
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
}
