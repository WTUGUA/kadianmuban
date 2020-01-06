package com.novv.dzdesk.util;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.*;
import android.os.storage.StorageManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.lang.Process;

public class DeviceUtil {

    /**
     * 1.判断sd卡是否挂载 {@link #isSDCardMounted()} 2.获取sd卡根路径 {@link #getSSCardAbsolutePath()}
     * 3.获取可用sd卡空间 {@link #getAvailableSpace()} 4.判断可用空间是否足够 {@link #hasEnoughSpace(long threshold)}
     * 5.获取DisplayMetrics {@link #getDisplayMetrics(Context context)} 6.获取DisplayMetrics的density
     * {@link #getDisplayDensity(Context context)} 7.获取显示器 宽度 {@link #getDisplayW(Context context)}
     * 8.获取显示器 高度 {@link #getDisplayH(Context context)} 9.px转dp {@link #px2dip(Context context,
     * float pxValue)} 10.dp转px {@link #dip2px(Context context, float dipValue)} 11.px转sp {@link
     * #px2sp(Context context, float pxValue)} 12.sp转px {@link #sp2px(Context context, float
     * spValue)} 13.文字像素的转换 {@link #px2pxByHVGA(Context context, float pxValue)} 14.震动手机 {@link
     * #vibrate(Context context, int milliseconds)} 15.判断系统安全锁屏是否开启 {@link #isKeyguardSecure(Context
     * context)} 16.去除系统锁屏 {@link #newKeyguardLock(Context context, String tag, KeyguardLock
     * lastLock)} 17.获取设备独立DevicesID,平板电脑通过获取MAC地址 {@link #getUniqueID(Context ctx)} 18.获取语言 {@link
     * #getLanguage()} 19.获取国家 {@link #getCountry()}
     */

    private static final String TAG = DeviceUtil.class.getSimpleName();
    private static String deviceId;

    /**
     * 判断sd卡是否挂载
     *
     * @return sd卡是否挂载
     */
    public static boolean isSDCardMounted() {
        boolean result = false;
        try {
            result = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取sd卡根路径
     *
     * @return SD卡的根路径
     */
    public static String getSSCardAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取可用sd卡空间
     *
     * @return 可用空间，单位：字节
     */
    @SuppressWarnings("deprecation")
    public static long getAvailableSpace() {
        String sdcard = Environment.getExternalStorageDirectory().toString();
        long space = 0;
        try {
            StatFs stat = new StatFs(sdcard);
            space = ((long) stat.getAvailableBlocks() * (long) stat
                    .getBlockSize());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return space;
    }

    /**
     * 判断可用空间是否足够
     *
     * @param threshold 需要的空间
     * @return 是否足够
     */
    public static boolean hasEnoughSpace(long threshold) {
        return getAvailableSpace() >= threshold;
    }

    /**
     * 获取DisplayMetrics
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null) {
            LogUtil.w(TAG, "getDisplayMetrics", "context is null");
            return null;
        }
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取DisplayMetricsde的density
     */
    public static float getDisplayDensity(Context context) {
        if (context == null) {
            LogUtil.w(TAG, "getDisplayDensity", "context is null");
            return -1;
        }
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 获取显示器 宽度
     *
     * @return int
     */
    public static int getDisplayW(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取显示器 高度
     */
    public static int getDisplayH(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * px转dp
     *
     * @return dip/dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转sp
     *
     * @return sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * sp转px
     *
     * @return px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 文字像素的转换
     *
     * @return px
     */
    public static float px2pxByHVGA(Context context, float pxValue) {
        final float density = getDisplayMetrics(context).density;
        return ((pxValue + 0.5f) * density + 0.5f);
    }

    /**
     * 震动手机
     *
     * @param milliseconds 震动时间，单位：毫秒
     */
    public static void vibrate(Context context, int milliseconds) {
        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(milliseconds);
    }

    /**
     * 获取通话状态
     */
    public static int getCallState(Context context) {
        TelephonyManager tm = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE));
        return tm.getCallState();
    }

    public static boolean hasNoCall(Context context) {
        return getCallState(context) == TelephonyManager.CALL_STATE_IDLE;
    }

    public static boolean hasNewCall(Context context) {
        return getCallState(context) == TelephonyManager.CALL_STATE_RINGING;
    }

    public static boolean isOffHook(Context context) {
        return getCallState(context) == TelephonyManager.CALL_STATE_OFFHOOK;
    }

    /**
     * 判断系统安全锁屏是否开启
     *
     * @return 是否开启系统锁屏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isKeyguardSecure(Context context) {
        if (VerUtil.sdkSupport(VerUtil.JELLY_BEAN)) {
            KeyguardManager km = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
            return km.isKeyguardSecure();
        }
        Object var1 = invokeMethodOfLockPatternUtils(context, "isSecure");
        return var1 != null ? ((Boolean) var1).booleanValue() : false;
    }

    private static Object invokeMethodOfLockPatternUtils(Context context,
            String method) {
        try {
            Class<?> cls = Class
                    .forName("com.android.internal.widget.LockPatternUtils");
            Object lockPatternUtils = cls.getConstructor(
                    new Class[]{Context.class}).newInstance(
                    new Object[]{context});
            Object retVal = cls.getMethod(method, (Class[]) null).invoke(
                    lockPatternUtils, (Object[]) null);
            return retVal;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 去除系统锁屏
     *
     * @param tag      新的锁屏标识
     * @param lastLock 上次获取到的KeyguardLock，可以为null，若不为null，则需要reenableKeyguard
     * @return KeyguardLock
     */
    @SuppressWarnings("deprecation")
    public static KeyguardLock newKeyguardLock(Context context, String tag,
            KeyguardLock lastLock) {
        if (lastLock != null) {
            lastLock.reenableKeyguard();
        }
        KeyguardManager km = (KeyguardManager) context
                .getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardLock lock = km.newKeyguardLock(tag);
        lock.disableKeyguard();
        return lock;
    }

    /**
     * 获取设备独立DevicesID,平板电脑通过获取MAC地址
     */
    public static String getUniqueID(Context ctx) {
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        String device_id = "";
        try {
            TelephonyManager tm = (TelephonyManager) ctx
                    .getSystemService(Context.TELEPHONY_SERVICE);
            device_id = tm.getDeviceId();
            deviceId = device_id;
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
        if (device_id == null) {
            try {
                WifiManager wifi = (WifiManager) ctx
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifi.getConnectionInfo();
                MessageDigest md = MessageDigest.getInstance("MD5");
                device_id = new BigInteger(1, md.digest(info.getMacAddress()
                        .getBytes())).toString(16).toUpperCase();

                deviceId = device_id;
            } catch (Exception e) {
                device_id = "";
            }
        }

        return device_id;
    }

    /**
     * 获取语言
     */
    public static String getLanguage() {
        String language = null;
        try {
            language = Locale.getDefault().getLanguage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return language;
    }

    /**
     * 获取国家
     */
    public static String getCountry() {
        String country = null;
        try {
            country = Locale.getDefault().getCountry();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return country;
    }

    public static boolean isMzPhone() {
        if ("meizu".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return false;
    }

    public static boolean isXiaoMiPhone() {
        if ("xiaomi".equalsIgnoreCase(Build.BRAND)) {
            return true;
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        int sbar = 0;
        try {
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                sbar = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbar;
    }

    public static int getDpiHeight(Context context) {
        int dpi = 0;
        // Display display = context.getWindowManager().getDefaultDisplay();
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    public static float getBatteryStatus(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED);
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                }
            };
            Intent batteryStatus = context.getApplicationContext()
                    .registerReceiver(receiver, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL,
                    -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,
                    -1);
            return ((float) level / (float) scale) * 100.0f;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return 20;
    }

    public static int getRealDisplayH(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17) {
            // new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            // reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                realHeight = display.getHeight();
            }

        } else {
            realHeight = display.getHeight();
        }
        return realHeight;
    }

    public static int getRealDisplayW(Context context) {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int realWidth;

        if (Build.VERSION.SDK_INT >= 17) {
            // new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            // reflection for this weird in-between time
            try {
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
            } catch (Exception e) {
                realWidth = display.getWidth();
            }

        } else {
            realWidth = display.getWidth();
        }
        return realWidth;
    }

    public static String ping(String url, int count) {
        String str = "";
        try {
            Process process = Runtime.getRuntime().exec(
                    "ping -c " + count + " " + url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((i = reader.read(buffer)) > 0) {
                output.append(buffer, 0, i);
            }
            reader.close();

            // body.append(output.toString()+"\n");
            str = parseIpFromPing(output.toString());
            // Log.d(TAG, str);
        } catch (IOException e) {
            // body.append("Error\n");
            e.printStackTrace();
        }
        return str;
    }

    public static List<String> getDomainAddresss(String url) {
        List<String> hostIps = new ArrayList<String>();
        try {
            InetAddress[] inetAddresss = InetAddress.getAllByName(url);
            for (InetAddress temp : inetAddresss) {
                hostIps.add(temp.getHostAddress());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hostIps;
    }

    private static String parseIpFromPing(String ping) {
        String ip = null;
        try {
            if (ping.contains("ping")) {
                int indexOpen = ping.indexOf("(");
                int indexClose = ping.indexOf(")");
                ip = ping.substring(indexOpen + 1, indexClose);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ip = ping;
        }
        return ip;
    }


    public static boolean isAppInstallSdcard(Context context) {
        try {
            String path = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), 0).sourceDir;
            LogUtil.i(TAG, "app sdcard path = " + path);
            // 安装在SDcard
            if (path.startsWith("/data")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getModel() {
        return getDeviceModel();
    }

    private static String getDeviceModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceMode;
        if (model.startsWith(manufacturer)) {
            deviceMode = metaDeviceModel(model);
        } else {
            deviceMode = metaDeviceModel(manufacturer) + " " + model;
        }
        return deviceMode;
    }

    private static String metaDeviceModel(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }

        char first = string.charAt(0);
        if (Character.isUpperCase(first)) {
            return string;
        } else {
            if (string.length() > 1) {
                return Character.toUpperCase(first) + string.substring(1);
            } else {
                return string;
            }
        }
    }

    /**
     * 获取当前的日期yyyyMMdd ex: 20150625
     */
    public static int currentTimeYYYYMMDD() {
        int currentTime = 0;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date curDate = new Date(System.currentTimeMillis());
            String str = format.format(curDate);
            currentTime = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        return currentTime;
    }

    /**
     * 外置sdcard根路径
     */
    public static String getExternalStorage(Context context) {
        ArrayList<StorageInfo> storageInfos = listAvaliableStorage(context);
        if (storageInfos == null || storageInfos.isEmpty()) {
            return "";
        }

        for (StorageInfo info : storageInfos) {
            if (info.isRemoveable && info.isMounted()) {
                String exStoragePath = info.path;
                if (TextUtils.isEmpty(exStoragePath)) {
                    return "";
                }
                File exFile = new File(exStoragePath);
                if (exFile.isDirectory() && exFile.canRead() && exFile.canWrite()) {
                    return exStoragePath;
                }
            }
        }

        return "";
    }

    /**
     * 列出所有挂载的存储设备
     */
    public static ArrayList<StorageInfo> listAvaliableStorage(Context context) {
        ArrayList<StorageInfo> storagges = new ArrayList<StorageInfo>();
        StorageManager storageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Class<?>[] paramClasses = {};
            Method getVolumeList = StorageManager.class.getMethod("getVolumeList", paramClasses);
            getVolumeList.setAccessible(true);
            Object[] params = {};
            Object[] invokes = (Object[]) getVolumeList.invoke(storageManager, params);
            if (invokes != null) {
                StorageInfo info = null;
                for (int i = 0; i < invokes.length; i++) {
                    Object obj = invokes[i];
                    Method getPath = obj.getClass().getMethod("getPath", new Class[0]);
                    String path = (String) getPath.invoke(obj, new Object[0]);
                    info = new StorageInfo(path);
                    File file = new File(info.path);
                    if ((file.exists()) && (file.isDirectory()) && (file.canWrite())) {
                        Method isRemovable = obj.getClass().getMethod("isRemovable", new Class[0]);
                        String state = null;
                        try {
                            Method getVolumeState = StorageManager.class
                                    .getMethod("getVolumeState", String.class);
                            state = (String) getVolumeState.invoke(storageManager, info.path);
                            info.state = state;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (info.isMounted()) {
                            info.isRemoveable = ((Boolean) isRemovable.invoke(obj, new Object[0]))
                                    .booleanValue();
                            storagges.add(info);
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
        storagges.trimToSize();

        return storagges;
    }

    public static class StorageInfo {

        public String path;
        public String state;
        public boolean isRemoveable;

        public StorageInfo(String path) {
            this.path = path;
        }

        public boolean isMounted() {
            return Environment.MEDIA_MOUNTED.equals(state);
        }

        @Override
        public String toString() {
            return "path = " + path + "\n" + "state =" + state + "\n" + "isRemoveable = "
                    + isRemoveable;
        }
    }
}
