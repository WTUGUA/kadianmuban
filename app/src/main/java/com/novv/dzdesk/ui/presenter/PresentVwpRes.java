package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.*;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.fragment.vwp.TabVwpResFragment;
import com.novv.dzdesk.util.LogUtil;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

import java.util.ArrayList;
import java.util.List;

import static com.novv.dzdesk.ui.fragment.vwp.TabVwpResFragment.*;

public class PresentVwpRes extends XPresent<TabVwpResFragment> {

    private int limit = 9, skip = 0;

    private void loadDiyRes() {
        Observable<BaseResult<List<VModel>>> diyOb = ServerApi.getHomeRcmdDiys();
        Observable<BaseResult<List<ResourceBean>>> vwpOb = ServerApi.getRecommend(0, limit);
        skip = limit;
        Observable.zip(diyOb, vwpOb, new BiFunction<BaseResult<List<VModel>>,
                BaseResult<List<ResourceBean>>, List<ResourceBean>>() {
            @Override
            public List<ResourceBean> apply(BaseResult<List<VModel>> result1,
                                            BaseResult<List<ResourceBean>> result2)
                    throws Exception {
                List<ResourceBean> resourceBeanList = new ArrayList<>();
                if (result1 != null && result1.getRes() != null) {
                    for (VModel vModel : result1.getRes()) {
                        ResourceBean resourceBean = new ResourceBean();
                        resourceBean.setvModel(vModel);
                        resourceBeanList.add(resourceBean);
                    }
                }
                if (result2 != null && result2.getRes() != null) {
                    resourceBeanList.addAll(result2.getRes());
                }
                return resourceBeanList;
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (hasV()) {
                    getV().getVDelegate().toastShort(throwable.getMessage());
                }
            }
        }).compose(DefaultScheduler.<List<ResourceBean>>getDefaultTransformer())
                .subscribe(new DisposableObserver<List<ResourceBean>>() {
                    @Override
                    public void onNext(List<ResourceBean> resourceBeans) {
                        if (hasV()) {
                            getV().setNewData(resourceBeans);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (hasV()) {
                            getV().setError(false);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void loadVwpRes(final boolean isLoadMore) {
        if (!hasV()) {
            return;
        }
        int type = getV().getDataType();
        if (!isLoadMore) {
            skip = 0;
            if (type == TYPE_RECOMMEND) {
                loadDiyRes();
                return;
            }
        }
        Observable<BaseResult<List<ResourceBean>>> observable;
        switch (type) {
            case TYPE_CATEGORY:
                observable = ServerApi.getCategoryList(getV().getCategoryId(), skip, limit);
                break;
            case TYPE_HOT:
                observable = ServerApi.getHot(true, skip, limit);
                break;
            case TYPE_NEW:
                observable = ServerApi.getNew(skip, limit);
                break;
            case TYPE_AMUSEMENT:
                observable = ServerApi.getCategoryList("59b25abbe7bce76bc834198a", skip, limit);
                break;
            case TYPE_RECOMMEND:
            default:
                observable = ServerApi.getRecommend(skip, limit);
                break;
        }
        skip += limit;
        observable.compose(DefaultScheduler.<BaseResult<List<ResourceBean>>>getDefaultTransformer())
                .subscribe(new BaseObserver<List<ResourceBean>>() {
                    @Override
                    public void onSuccess(@NonNull List<ResourceBean> resourceBeans) {
                        if (!LoginContext.getInstance().isVip()) {
                            if (resourceBeans.size() == limit) {
                                ResourceBean bean = (ResourceBean) resourceBeans.get(0)
                                        .deepClone();
                                bean.setContentType(Const.OnlineKey.CONTENT_TYPE_AD);
                                resourceBeans.add(bean);
                            }
                        }
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

    public void loadBannerData() {
        if (!hasV()) {
            return;
        }
        if (getV().getDataType() == TYPE_NEW) {
            ServerApi.getBanner()
                    .compose(DefaultScheduler.<BannerResult>getDefaultTransformer())
                    .subscribe(new DefaultDisposableObserver<BannerResult>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            if (hasV()) {
                                getV().setNoBanner();
                            }
                        }

                        @Override
                        public void onSuccess(final BannerResult result) {
                            Run.onBackground(new Action() {
                                @Override
                                public void call() {
                                    final List<String> titles = new ArrayList<>();
                                    final List<String> images = new ArrayList<>();
                                    final List<DataEntity> datas = new ArrayList<>();
                                    if (result.getCode() == 0) {
                                        List<DataEntity> data = result.getData();
                                        if (data != null && !data.isEmpty()) {
                                            for (DataEntity dataEntity : data) {
                                                if (!TextUtils.isEmpty(dataEntity.getImg())) {
                                                    images.add(dataEntity.getImg());
                                                    LogUtil.e("banner", dataEntity.getImg());
                                                }
                                                if (!TextUtils.isEmpty(dataEntity.getTitle())) {
                                                    titles.add(dataEntity.getTitle());
                                                    LogUtil.e("banner", dataEntity.getTitle());
                                                }
                                                datas.add(dataEntity);
                                            }
                                        }
                                    }
                                    Run.onUiAsync(new Action() {
                                        @Override
                                        public void call() {
                                            if (hasV()) {
                                                getV().setBannerData(images, titles, datas);
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
        }
    }
}
