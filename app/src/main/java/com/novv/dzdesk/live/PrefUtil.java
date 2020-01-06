package com.novv.dzdesk.live;

import android.annotation.SuppressLint;
import android.content.Context;
import com.novv.dzdesk.util.LogUtil;

public class PrefUtil {

    private static final String TAG = PrefUtil.class.getSimpleName();

    private static final String PREF_NAME = "adk_pref";

    @SuppressLint("InlinedApi")
    public static MultiProcessPreferences.MultiProcessSharedPreferences getPref(
            Context context) {
        return MultiProcessPrefUtil.getPref(context);
    }

    public static boolean getBoolean(Context context, String key, boolean def) {
        if (context == null || key == null) {
            return def;
        }
        return getPref(context).getBoolean(key, def);
    }

    public static int getInt(Context context, String key, int def) {
        if (context == null || key == null) {
            return def;
        }
        return getPref(context).getInt(key, def);
    }

    public static long getLong(Context context, String key, long def) {
        if (context == null || key == null) {
            return def;
        }
        return getPref(context).getLong(key, def);
    }

    public static float getFloat(Context context, String key, float def) {
        if (context == null || key == null) {
            return def;
        }
        return getPref(context).getFloat(key, def);
    }

    public static String getString(Context context, String key, String def) {
        if (context == null || key == null) {
            return def;
        }
        return getPref(context).getString(key, def);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        if (context != null && key != null) {
            getPref(context).edit().putBoolean(key, value).commit();
        } else {
            LogUtil.w(TAG, "putBoolean", "invalid argument");
        }
    }

    public static void putInt(Context context, String key, int value) {
        if (context != null && key != null) {
            getPref(context).edit().putInt(key, value).commit();
        } else {
            LogUtil.w(TAG, "putInt", "invalid argument");
        }
    }

    public static void putLong(Context context, String key, long value) {
        if (context != null && key != null) {
            getPref(context).edit().putLong(key, value).commit();
        } else {
            LogUtil.w(TAG, "putLong", "invalid argument");
        }
    }

    public static void putFloat(Context context, String key, float value) {
        if (context != null && key != null) {
            getPref(context).edit().putFloat(key, value).commit();
        } else {
            LogUtil.w(TAG, "putFloat", "invalid argument");
        }
    }

    public static void putString(Context context, String key, String value) {
        if (context != null && key != null) {
            getPref(context).edit().putString(key, value).commit();
        } else {
            LogUtil.w(TAG, "putString", "invalid argument");
        }
    }
}
