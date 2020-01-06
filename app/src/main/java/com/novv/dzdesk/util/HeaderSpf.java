package com.novv.dzdesk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ark.dict.Utils;

/**
 * Created by lijianglong on 2017/9/5.
 */

public class HeaderSpf {

    private static final String PREF_SESSION_ID = "pref_session_id";
    private static final String PREF_NAME = "pref_name";

    private static SharedPreferences getDefaultPreferences() {
        return Utils.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void clear() {
        getDefaultPreferences().edit().clear().apply();
    }

    public static void saveSessionId(String sessionId) {
        getDefaultPreferences().edit().putString(PREF_SESSION_ID, sessionId).apply();
    }

    public static String getSessionId() {
        return getDefaultPreferences().getString(PREF_SESSION_ID, "");
    }
}
