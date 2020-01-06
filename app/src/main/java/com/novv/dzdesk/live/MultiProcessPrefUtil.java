package com.novv.dzdesk.live;

import android.annotation.SuppressLint;
import android.content.Context;

public class MultiProcessPrefUtil {

    private static final String TAG = MultiProcessPrefUtil.class
            .getSimpleName();

    @SuppressLint("InlinedApi")
    public synchronized static MultiProcessPreferences.MultiProcessSharedPreferences getPref(
            Context context) {
        return MultiProcessPreferences.getDefaultSharedPreferences(context);
    }
}
