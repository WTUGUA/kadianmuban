package com.novv.dzdesk.ui.presenter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.JsonUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.auth.Auth;
import com.ark.auth.AuthCallback;
import com.ark.baseui.XPresent;
import com.ark.dict.ConfigMapLoader;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.http.callback.JsonCallback;
import com.novv.dzdesk.http.utils.Urls;
import com.novv.dzdesk.res.event.EventCode;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.OrderInfo;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.res.model.VipDesc;
import com.novv.dzdesk.res.model.VipOrder;
import com.novv.dzdesk.res.model.VipPrice;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.rxbus2.RxBus;
import com.novv.dzdesk.secret.AesCbcWithIntegrity;
import com.novv.dzdesk.ui.activity.user.UserVipActivity;
import com.novv.dzdesk.util.HeaderSpf;
import com.novv.dzdesk.util.UmUtil;
import com.novv.dzdesk.video.op.PLBuiltinFilterImpl;
import com.qiniu.pili.droid.shortvideo.PLBuiltinFilter;
import com.qiniu.pili.droid.shortvideo.PLShortVideoEditor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PresentUserVip extends XPresent<UserVipActivity> {

    private PLShortVideoEditor mShortVideoEditor;
    private GLSurfaceView glSurfaceView;

    /**
     * 加载VIP描述
     */
    public void loadVipDesc() {
        Run.onBackground(new Action() {
            @Override
            public void call() {
                if (!hasV()) {
                    return;
                }
                String result = JsonUtils.getJson(getV(), "vip.json");
                final List<VipDesc> list = new GsonBuilder().create()
                        .fromJson(result, new TypeToken<List<VipDesc>>() {
                        }.getType());
                for (int i = 0; i < list.size(); i++) {
                    VipDesc vipDesc = list.get(i);
                    if (i == 0) {
                        vipDesc.resId = R.mipmap.ic_vip_muban;
                    } else if (i == 1) {
                        vipDesc.resId = R.mipmap.ic_vip_xiazaijilu;
                    } else if (i == 2) {
                        vipDesc.resId = R.mipmap.ic_vip_noad;
                    } else if (i == 3) {
                        vipDesc.resId = R.mipmap.ic_vip_tag;
                    } else if (i == 4) {
                        vipDesc.resId = R.mipmap.ic_vip_nowatermark;
                    } else {
                        vipDesc.resId = R.mipmap.ic_vip_first;
                    }
                }
                if (hasV()) {
                    Run.onUiAsync(new Action() {
                        @Override
                        public void call() {
                            if (hasV()) {
                                getV().setDescData(list);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 加载VIP价格表
     */
    public void loadVipPrice() {
        String url = Urls.vipUrl + "v1/price/list";
        if (!hasV()) {
            return;
        }
        if (!isNetworkConnected(getV())) {
            Toast.makeText(getV().getApplicationContext(), "网络已断开", Toast.LENGTH_SHORT).show();
            getV().setPriceData(new ArrayList<VipPrice>());
            return;
        }
        OkGo.<BaseResult<List<VipPrice>>>get(url)
                .params("platform", "android")
                .params("package", getV().getPackageName())
                .execute(new JsonCallback<BaseResult<List<VipPrice>>>() {
                    @Override
                    public void onSuccess(final Response<BaseResult<List<VipPrice>>> response) {
                        final List<VipPrice> list = new ArrayList<>();
                        Run.onBackground(new Action() {
                            @Override
                            public void call() {
                                BaseResult<List<VipPrice>> result = response.body();
                                if (result != null) {
                                    List<VipPrice> resultRes = result.getRes();
                                    if (resultRes != null) {
                                        list.addAll(resultRes);
                                    }
                                }
                                Run.onUiAsync(new Action() {
                                    @Override
                                    public void call() {
                                        if (hasV()) {
                                            getV().setPriceData(list);
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
    }

    private boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo;
            if (mConnectivityManager != null
                    && (mNetworkInfo = mConnectivityManager.getActiveNetworkInfo()) != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 拉取支付宝订单
     *
     * @param vipPrice vip价格信息
     * @param userId   用户唯一id
     */
    public void loadAlipayOrder(final VipPrice vipPrice, final String userId) {
        if (!hasV()) {
            return;
        }
        getV().showNormal();
        if (!isNetworkConnected(getV())) {
            Toast.makeText(getV().getApplicationContext(), "网络已断开", Toast.LENGTH_SHORT).show();
            getV().hideLoading();
            return;
        }
        String url = Urls.vipUrl + "v1/alipay/sign";
        OkGo.<BaseResult<String>>get(url)
                .params("platform", "android")
                .params("package", getV().getPackageName())
                .params("openid", userId)
                .params("subject", "彩蛋视频壁纸会员服务")
                .params("price_id", vipPrice.getId())
                .execute(new JsonCallback<BaseResult<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResult<String>> response) {
                        final BaseResult<String> baseResult = response.body();
                        if (!hasV()) {
                            return;
                        }
                        if (baseResult != null) {
                            String data = baseResult.getRes();
                            Auth.withZFB(getV())
                                    .setAction(Auth.Pay)
                                    .payIsShowLoading(false)
                                    .payOrderInfo(data)
                                    .build(new AuthCallback() {
                                        @Override
                                        public void onSuccessForPay(@NonNull String code, @NonNull String result) {
                                            super.onSuccessForPay(code, result);
                                            if (hasV()) {
                                                onPaySuccess(vipPrice.getType(), userId, baseResult.getOid());
                                            }
                                        }

                                        @Override
                                        public void onCancel() {
                                            super.onCancel();
                                            if (hasV()) {
                                                getV().hideLoading();
                                                Toast.makeText(getV().getApplicationContext(), "已取消", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailed(@NonNull String code, @NonNull String msg) {
                                            super.onFailed(code, msg);
                                            loopCheckOrder(vipPrice.getType(), userId, baseResult.getOid());
                                        }
                                    });
                        } else {
                            getV().hideLoading();
                            Toast.makeText(getV().getApplicationContext(), "订单拉取失败，请稍后重试", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    private void onPaySuccess(String type, String userId, String oid) {
        UmUtil.anaVIPClickBuySuccess(getV(), type);
        notifyPaySuccess(userId, oid);
    }

    private void onPayFailed(@NonNull String msg) {
        getV().showWarn();
        getV().getVDelegate().toastShort(msg);
    }

    /**
     * 拉取微信支付订单
     *
     * @param vipPrice vip价格
     * @param userId   用户唯一id
     */
    public void loadWXOrder(final VipPrice vipPrice, final String userId) {
        if (!hasV()) {
            return;
        }
        getV().showNormal();
        if (!isNetworkConnected(getV())) {
            Toast.makeText(getV().getApplicationContext(), "网络已断开", Toast.LENGTH_SHORT).show();
            getV().hideLoading();
            return;
        }
        String url = Urls.vipUrl + "v1/wechat/sign";
        OkGo.<BaseResult<VipOrder>>get(url)
                .headers("Session-Id", HeaderSpf.getSessionId())
                .params("platform", "android")
                .params("package", getV().getPackageName())
                .params("openid", userId)
                .params("subject", "彩蛋视频壁纸会员服务")
                .params("price_id", vipPrice.getId())
                .execute(new JsonCallback<BaseResult<VipOrder>>() {
                    @Override
                    public void onSuccess(Response<BaseResult<VipOrder>> response) {
                        final BaseResult<VipOrder> baseResult = response.body();
                        final VipOrder vipOrder = baseResult.getRes();
                        if (!hasV()) {
                            return;
                        }
                        if (vipOrder != null) {
                            Auth.withWX(getV())
                                    .setAction(Auth.Pay)
                                    .payNonceStr(vipOrder.getNoncestr())
                                    .payPackageValue(vipOrder.getPackageStr())
                                    .payPartnerId(vipOrder.getPartnerid())
                                    .payPrepayId(vipOrder.getPrepayid())
                                    .paySign(vipOrder.getSign())
                                    .payTimestamp(String.valueOf(vipOrder.getTimestamp()))
                                    .build(new AuthCallback() {
                                        @Override
                                        public void onSuccessForPay(@NonNull String code, @NonNull String result) {
                                            super.onSuccessForPay(code, result);
                                            if (hasV()) {
                                                onPaySuccess(vipPrice.getType(), userId, baseResult.getOid());
                                            }
                                        }

                                        @Override
                                        public void onCancel() {
                                            super.onCancel();
                                            if (hasV()) {
                                                getV().hideLoading();
                                                Toast.makeText(getV().getApplicationContext(), "已取消", Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailed(@NonNull String code, @NonNull String msg) {
                                            super.onFailed(code, msg);
                                            loopCheckOrder(vipPrice.getType(), userId, baseResult.getOid());
                                        }
                                    });
                        } else {
                            getV().hideLoading();
                            Toast.makeText(getV().getApplicationContext(), "订单拉取失败，请稍后重试", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    /**
     * 支付失败,查询订单三次,只要查询到已付款就停止查询
     */
    private void loopCheckOrder(final String payType, final String userid, final String oid) {
        if (TextUtils.isEmpty(oid)) {
            if (hasV()) {
                onPayFailed("支付失败");
            }
            return;
        }
        final int[] count = {0};
        final CompositeDisposable compositeDisposable = new CompositeDisposable();
        Disposable disposable = ServerApi.checkOrder(oid)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) {
                        count[0]++;
                        Log.i("loopCheckOrder", "doOnSubscribe");
                    }
                })
                .delay(1500, TimeUnit.MILLISECONDS, true)
                .repeat(3)
                .retry(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResult<OrderInfo>>() {
                    @Override
                    public void accept(BaseResult<OrderInfo> result) {
                        if (result != null && result.getRes() != null
                                && result.getRes().status == 1) {
                            onPaySuccess(payType, userid, oid);
                            if (!compositeDisposable.isDisposed())
                                compositeDisposable.dispose();
                        } else if (count[0] == 3) {
                            onPayFailed("支付失败");
                            if (!compositeDisposable.isDisposed())
                                compositeDisposable.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (count[0] == 3) {
                            onPayFailed("支付失败");
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }


    private GLSurfaceView getGLSurface() {
        if (glSurfaceView == null) {
            glSurfaceView = new GLSurfaceView(getV().getApplicationContext());
        }
        return glSurfaceView;
    }

    /**
     * 加载VIP滤镜
     */
    public void loadVipFilters() {
        if (!hasV()) {
            return;
        }
        if (mShortVideoEditor == null) {
            mShortVideoEditor = new PLShortVideoEditor(getGLSurface());
        }
        PLBuiltinFilter[] plBuiltinFilters = mShortVideoEditor.getBuiltinFilterList();
        String freeLimit = ConfigMapLoader.getInstance().getResponseMap()
                .get("filters_free_limit");
        int limit = 0;
        try {
            limit = Integer.parseInt(freeLimit);
        } catch (Exception e) {
            //
        }

        List<PLBuiltinFilterImpl> newList = new ArrayList<>();
        for (int i = 0; i < plBuiltinFilters.length; i++) {
            PLBuiltinFilter plBuiltinFilter = plBuiltinFilters[i];
            PLBuiltinFilterImpl impl = new PLBuiltinFilterImpl(plBuiltinFilter);
            if (limit == -1 || i >= limit) {
                impl.setVip(true);
                newList.add(impl);
            }
        }
        getV().setNewFilters(newList);
    }

    /**
     * 加载VIP制作模板
     */
    public void loadVipDiys() {
        if (!hasV()) {
            return;
        }
        ServerApi.getVipRcmdDiys()
                .compose(DefaultScheduler.<BaseResult<List<VModel>>>getDefaultTransformer())
                .subscribe(new BaseObserver<List<VModel>>() {
                    @Override
                    public void onSuccess(@NonNull final List<VModel> vModels) {
                        Run.onBackground(new Action() {
                            @Override
                            public void call() {
                                final List<VModel> newList = new ArrayList<>();
                                for (VModel vModel : vModels) {
                                    if (vModel.needVip) {
                                        newList.add(vModel);
                                    }
                                }
                                Run.onUiAsync(new Action() {
                                    @Override
                                    public void call() {
                                        if (hasV()) {
                                            getV().setNewDiys(newList);
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {
                        if (hasV()) {
                            getV().setEmptyDiys();
                        }
                    }
                });
    }

    /**
     * 通知支付结果
     *
     * @param openid openid
     * @param oid    订单id
     */
    private void notifyPaySuccess(String openid, String oid) {
        if (!hasV()) {
            return;
        }
        //向服务端发送回调通知
        String encryptStr = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOpt("openid", openid);
            jsonObject.putOpt("oid", oid);
            jsonObject.putOpt("platform", "android");
            jsonObject.putOpt("package", getV().getPackageName());
            String textToEncrypt = jsonObject.toString();
            encryptStr = AesCbcWithIntegrity.getInstance().encrypt(textToEncrypt, "utf-8",
                    AesCbcWithIntegrity.sKey, AesCbcWithIntegrity.ivParameter);
            LogUtils.i("Encrypt: " + encryptStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OkGo.<BaseResult<String>>post(Urls.vipUrl + "v1/user/paid")
                .tag(System.currentTimeMillis())
                .upString(encryptStr)
                .execute(new JsonCallback<BaseResult<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResult<String>> response) {
                        BaseResult<String> baseResult = response.body();
                        LogUtils.i(baseResult.getMsg());
                        if (hasV()) {
                            int code = baseResult.getCode();
                            if (code != 0) {
                                getV().getVDelegate().toastShort(baseResult.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(Response<BaseResult<String>> response) {
                        super.onError(response);
                    }
                });
        refreshUser();
    }

    private void refreshUser() {
        if (!hasV()) {
            return;
        }
        //拉取个人信息
        ServerApi
                .getUserInfo()
                .compose(
                        DefaultScheduler
                                .<BaseResult<UserModel>>getDefaultTransformer())
                .subscribe(new BaseObserver<UserModel>() {
                    @Override
                    public void onSuccess(@NonNull UserModel userModel) {
                        if (hasV()) {
                            LoginContext.getInstance().login(userModel);
                            if (userModel.isVip()) {
                                RxBus.getDefault().send(EventCode.VIEW_VIP_LOGIN, userModel);
                            }
                            getV().showSuccess();
                        }
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {
                        if (hasV()) {
                            getV().showWarn();
                            getV().getVDelegate().toastShort(message);
                        }
                    }
                });
    }

    public void release() {
        if (mShortVideoEditor != null) {
            mShortVideoEditor.stopPlayback();
            mShortVideoEditor = null;
            if (glSurfaceView != null) {
                glSurfaceView.onPause();
                glSurfaceView = null;
            }
        }
    }
}
