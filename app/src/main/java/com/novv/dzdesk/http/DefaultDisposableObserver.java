package com.novv.dzdesk.http;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public abstract class DefaultDisposableObserver<T> extends DisposableObserver<T> {

    @Override
    final public void onNext(T t) {
        if (t != null) {
            onSuccess(t);
        } else {
            onFailure(new RuntimeException("数据解析异常"));
        }
    }

    @Override
    final public void onError(Throwable e) {
        onFailure(e == null ? new RuntimeException("请求失败") : e);
    }

    @Override
    public void onComplete() {

    }

    public abstract void onFailure(@NonNull Throwable throwable);

    public abstract void onSuccess(@NonNull T result);
}
