package com.novv.dzdesk.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.novv.dzdesk.live.LwVideoLiveWallpaper;

import com.umeng.commonsdk.utils.UMUtils;
import java.io.File;
import java.util.List;

/**
 * Created by lingyfh on 2017/5/31.
 */
public class CtxUtil {

    public static boolean isLwServiceRun(Context context) {
        return isServiceRunning(context, LwVideoLiveWallpaper.class);
    }

    public static boolean isServiceRun(Context mContext, String className) {
        boolean isRun = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(40);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRun = true;
                break;
            }
        }
        return isRun;
    }

    public static boolean isServiceRunning(Context context,
            Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void launchHome(Context context) {
        Intent intent = getLauncherIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        launchApp(context, intent);
    }

    /**
     * 获取桌面intent
     *
     * @return Intent
     */
    public static Intent getLauncherIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        return intent;
    }

    /**
     * Intent启动Activity
     */
    public static void launchApp(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前应用程序的PackageInfo
     *
     * @return PackageInfo
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo sPkgInfo = null;
        if (sPkgInfo == null) {
            try {
                sPkgInfo = context.getPackageManager().getPackageInfo(
                        context.getPackageName(),
                        PackageManager.GET_ACTIVITIES
                                | PackageManager.GET_RECEIVERS
                                | PackageManager.GET_SERVICES
                                | PackageManager.GET_PROVIDERS
                                | PackageManager.GET_INSTRUMENTATION
                                | PackageManager.GET_SIGNATURES
                                | PackageManager.GET_GIDS
                                | PackageManager.GET_PERMISSIONS
                                | PackageManager.GET_CONFIGURATIONS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sPkgInfo;
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return version code
     */
    public static int getVersionCode(Context context) {
        PackageInfo pi = getPackageInfo(context);
        return pi == null ? 0 : pi.versionCode;
    }

    /**
     * 获取当前应用程序的版本名称
     *
     * @return version name
     */
    public static String getVersionName(Context context) {
        PackageInfo pi = getPackageInfo(context);
        return pi == null ? null : pi.versionName;
    }

    public static String getPackageName(Context context) {
        if (context == null) {
            return "";
        }
        return context.getPackageName();
    }

    /**
     * 获取当前应用程序的友盟渠道
     *
     * @return umeng channel
     */
    public static String getUmengChannel(Context context) {
        String channel= UMUtils.getChannel(context);
        if (!TextUtils.isEmpty(channel)){
            return channel;
        }
        return "adesk";
    }

    /**
     * 获取application中指定的meta-data
     *
     * @return 如果没有获取成功(没有对应值或者异常)，则返回值为空
     */
    public static String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager
                        .getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.get(key) + "";
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return resultData;
    }

    /**
     * 获取当前应用程序metaData中指定键对应的值
     *
     * @return meta data
     */
    public static String getMetaData(Context context, String key) {
        ApplicationInfo ai = getApplicationInfo(context);
        return ai == null ? null : (ai.metaData.getString(key) == null
                ? (ai.metaData.getInt(key) + "")
                : ai.metaData.getString(key));
    }

    /**
     * 获取当前应用程序的ApplicationInfo
     *
     * @return ApplicationInfo
     */
    public static ApplicationInfo getApplicationInfo(Context context) {
        ApplicationInfo sAppInfo = null;
        try {
            sAppInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(),
                    PackageManager.GET_META_DATA
                            | PackageManager.GET_SHARED_LIBRARY_FILES);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sAppInfo;
    }

    public static int getVersionCode(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath)) {
            return 0;
        }
        try {
            PackageManager pm = context.getPackageManager();
            if (pm == null) {
                return 0;
            }
            PackageInfo info = pm.getPackageArchiveInfo(filePath, 0);
            if (info == null) {
                return 0;
            }
            return info.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 安装apk
     */
    public static void installApk(Context context, File file) {
        try {
            Uri fileUri = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 防止打不开应用
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }
}
