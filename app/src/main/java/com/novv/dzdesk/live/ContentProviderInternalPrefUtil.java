package com.novv.dzdesk.live;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class ContentProviderInternalPrefUtil {

    private static final String PREF_NAME = "adk_pref";

    @SuppressLint("InlinedApi")
    public synchronized static SharedPreferences getPref(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
    }
}
