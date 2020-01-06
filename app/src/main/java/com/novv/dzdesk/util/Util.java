package com.novv.dzdesk.util;

import android.os.Looper;

/**
 * Created by lingyfh on 15/11/9.
 */
public class Util {

    /**
     * 判断当前线程是否为主线程
     */
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
