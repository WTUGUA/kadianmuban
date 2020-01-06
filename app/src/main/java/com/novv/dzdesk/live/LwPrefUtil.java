package com.novv.dzdesk.live;

import android.content.Context;

public class LwPrefUtil {

    public static final String PREFS = "adk_pref";// "picassoLw";
    public static final String PREF_LWP_CHANGED = "LwpChanged"; // 引擎应用的资源路径是否改变
    public static final String PREF_LWP_STARTED = "LwpStarted"; // 引擎是否启动
    public static final String PREF_LWP_PREVIEW_VOICE = "lwp_preview_voice"; // 引擎是否启动
    public static final String PREF_LWP_VOICE = "lwp_voice"; // 引擎是否启动
    public static final String PREF_LWP_PATH = "LwpPath"; // 引擎引用的资源路径
    public static final String PREF_NEWEST = "newest"; // 服务器新上的动态壁纸
    public static final String PREF_AUTO_CHANGE_LiveWALLPAPER = "autoChangeLiveWallpaper"; // 自动切换壁纸
    public static final String PREF_REFRESH_DATA_TIME = "refreshDataTime"; // 更新动态壁纸列表时间
    public static final String PREF_FLOATWINDOW_VISIBLE = "floatWindow"; // 桌面悬浮框是否可见
    public static final String PREF_COMPATIBLE = "key_pref_compatible"; // 兼容配置
    public static final String PREF_VOLUME = "key_pref_volume"; // 兼容配置

    public static boolean needCompatibleProperties(Context context) {
        return PrefUtil.getBoolean(context, PREF_COMPATIBLE, true);
    }

    public static void setCompatibleProperties(Context context, boolean value) {
        PrefUtil.putBoolean(context, PREF_COMPATIBLE, value);
    }

    public static void setLwpChanged(Context context, boolean lwpChanged) {
        PrefUtil.putBoolean(context.getApplicationContext(), PREF_LWP_CHANGED,
                lwpChanged);
    }

    public static boolean isLwpChanged(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(),
                PREF_LWP_CHANGED, true);
    }

    public static void setVideoVolume(Context context, int volume) {
        PrefUtil.putInt(context.getApplicationContext(), PREF_VOLUME,
                volume);
    }

    public static int getVideoVolume(Context context) {
        return PrefUtil.getInt(context, PREF_VOLUME, 98);
    }


    /**
     * 设置预览声音
     */
    public static void setPreviewVoice(Context context, boolean isOpen) {
        PrefUtil.putBoolean(context.getApplicationContext(), PREF_LWP_PREVIEW_VOICE,
                isOpen);
    }

    /**
     * 获取预览声音设置,默认开启
     */
    public static boolean getPreviewVoice(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(),
                PREF_LWP_PREVIEW_VOICE, true);
    }

    public static void setLwpStarted(Context context, boolean lwpStarted) {
        PrefUtil.putBoolean(context.getApplicationContext(), PREF_LWP_STARTED,
                lwpStarted);
    }

    public static boolean isLwpStarted(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(),
                PREF_LWP_STARTED, false);
    }


    public static void setLwpVoiceOpened(Context context, boolean lwpStarted) {
        PrefUtil.putBoolean(context.getApplicationContext(), PREF_LWP_VOICE,
                lwpStarted);
    }

    public static boolean isLwpVoiceOpened(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(),
                PREF_LWP_VOICE, false);
    }


    public static boolean getAutoChangeLiveWallpaper(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(),
                PREF_AUTO_CHANGE_LiveWALLPAPER, false);
    }

    public static void setAutoChangeLiveWallpaper(Context context, boolean b) {
        PrefUtil.putBoolean(context.getApplicationContext(),
                PREF_AUTO_CHANGE_LiveWALLPAPER, b);
    }

    public static void setLiveWallpaperFocre(Context context, boolean b) {
        PrefUtil.putBoolean(context.getApplicationContext(), "lw_focre", b);
    }

    public static boolean getLiveWallpaperFocre(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(), "lw_focre",
                true);
    }

    public static void setNewestCount(Context context, int newest) {
        PrefUtil.putInt(context.getApplicationContext(), PREF_NEWEST, newest);
    }

    public static boolean getFloatWindowVisible(Context context) {
        return PrefUtil.getBoolean(context.getApplicationContext(),
                PREF_FLOATWINDOW_VISIBLE, true);
    }

    public static int getNewestCount(Context context) {
        return PrefUtil.getInt(context.getApplicationContext(), PREF_NEWEST, 0);
    }

    public static long getRefreshDataTime(Context context) {
        return PrefUtil.getLong(context.getApplicationContext(),
                PREF_REFRESH_DATA_TIME, 0l);
    }
}
