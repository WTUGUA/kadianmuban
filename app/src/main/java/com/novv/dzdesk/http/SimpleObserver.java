package com.novv.dzdesk.http;

import com.novv.dzdesk.res.model.BaseResult;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by lijianglong on 2017/9/6.
 */

public abstract class SimpleObserver implements Observer<BaseResult> {

    private static final int RESPONSE_CODE_OK = 0;       //成功返回积极数据
    private static final int RESPONSE_CODE_FAILED = -1;  //返回数据失败
    private static final int RESPONSE_CODE_LOGOUT = -9;  //未登陆

    public abstract void onSuccess();

    public abstract void onFailure(int code, String message);

    @Override
    public final void onNext(BaseResult result) {
        switch (result.getCode()) {
            case RESPONSE_CODE_OK:
                onSuccess();
                break;
            case RESPONSE_CODE_FAILED:
                onFailure(result.getCode(), result.getMsg());
                break;
            case RESPONSE_CODE_LOGOUT:
                onLogout();
                break;
            default:
                onFailure(result.getCode(), result.getMsg());
                break;
        }
    }

    @Override
    public final void onError(Throwable t) {
        onFailure(RESPONSE_CODE_FAILED, t.getMessage());
    }

    public void onLogout() {

    }

    /**
     * 用的比较少,自行实现
     */
    @Override
    public final void onComplete() {

    }

    /**
     * 用的比较少,自行实现
     *
     * @param d Disposable
     */
    @Override
    public final void onSubscribe(Disposable d) {

    }
}
