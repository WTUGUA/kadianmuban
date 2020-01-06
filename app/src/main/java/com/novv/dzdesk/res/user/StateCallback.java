package com.novv.dzdesk.res.user;

public interface StateCallback<T> {
    void onSuccess(T t);

    void onError(String msg);
}
