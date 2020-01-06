package com.novv.dzdesk.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.novv.dzdesk.live.PrefUtil;
import com.umeng.analytics.MobclickAgent;

public class RateTLUtils {


    private static final String tag = RateTLUtils.class.getSimpleName();
    private static long RateTime = 10 * 1000;
    private static long RateMaxTime = 600 * 1000;

    public static void ana(Context context, String event) {
        MobclickAgent.onEvent(context, event);
    }

    public static void init(Context context) {
        PrefUtil.putInt(context, "rate_max_count_session", 0);
    }

    public static void recordTime(Context context) {
        PrefUtil.putLong(context, "start_rate_time", System.currentTimeMillis());
    }

    public static boolean checkSuccess(Context context) {
        long diff = System.currentTimeMillis() - PrefUtil
                .getLong(context, "start_rate_time", System.currentTimeMillis());
        LogUtil.i(tag, "checkSuccess diff = " + diff);
        String rate_check_time = AdeskOnlineConfigUtil.getConfigParams(context, "rate_check_time");
        LogUtil.i(tag, "checkSuccess rate_check_time = " + rate_check_time);
        if (!TextUtils.isEmpty(rate_check_time) && TextUtils.isDigitsOnly(rate_check_time)) {
            RateTime = Integer.parseInt(rate_check_time) * 1000;
            LogUtil.i(tag, "checkSuccess RateTime = " + RateTime);
        }
        if (diff > RateTime && diff < RateMaxTime) {
            PrefUtil.putBoolean(context, "rate_successed", true);
            ana(context, "rate_successed");
        } else {
            PrefUtil.putBoolean(context, "rate_successed", false);
            ana(context, "rate_failed");
        }
        return diff > RateTime;
    }

    public static boolean isRateSuccess(Context context) {
        return PrefUtil.getBoolean(context, "rate_successed", false);
    }

    public static boolean showRateDialog(final Context context) {

        if (context == null) {
            return false;
        }
        if (!(context instanceof Activity)) {
            return false;
        }

        if (!"true"
                .equalsIgnoreCase(AdeskOnlineConfigUtil.getConfigParams(context, "rate_enable"))) {
            LogUtil.i(tag, "rate_enable not true");
            return false;
        }

        if (isRateSuccess(context)) {
            LogUtil.i(tag, "is rate success");
            return false;
        }

        String rate_show_count = AdeskOnlineConfigUtil.getConfigParams(context, "rate_show_cout");
        if (TextUtils.isEmpty(rate_show_count)) {
            return false;
        }
        if (!TextUtils.isDigitsOnly(rate_show_count)) {
            return false;
        }
        int showCount = PrefUtil.getInt(context, "rate_show_count", 1);
        PrefUtil.putInt(context, "rate_show_count", (showCount + 1));
        if (showCount < Integer.parseInt(rate_show_count)) {
            return false;
        }
        LogUtil.i(tag, "showCount = " + showCount);

        String rate_split_count = AdeskOnlineConfigUtil
                .getConfigParams(context, "rate_split_count");
        if (TextUtils.isEmpty(rate_split_count)) {
            return false;
        }

        if (!TextUtils.isDigitsOnly(rate_split_count)) {
            return false;
        }

        if (Integer.parseInt(rate_split_count) <= 0) {
            rate_split_count = "2";
        }

        int splitCount = PrefUtil.getInt(context, "rate_split_count", 0);
        PrefUtil.putInt(context, "rate_split_count", (splitCount + 1));
        if (splitCount % Integer.parseInt(rate_split_count) != 0) {
            return false;
        }

        LogUtil.i(tag, "rate_show_count = " + rate_show_count);

        String rate_max_count_session = AdeskOnlineConfigUtil
                .getConfigParams(context, "rate_max_count_session");
        if (TextUtils.isEmpty(rate_max_count_session)) {
            return false;
        }
        if (!TextUtils.isDigitsOnly(rate_max_count_session)) {
            return false;
        }
        int maxCountSession = PrefUtil.getInt(context, "rate_max_count_session", 0);
        PrefUtil.putInt(context, "rate_max_count_session", (maxCountSession + 1));
        LogUtil.i(tag, "max count session = " + maxCountSession + " rate_max_count_session = "
                + rate_max_count_session);
        if (maxCountSession >= Integer.parseInt(rate_max_count_session)) {
            return false;
        }

        ana(context, "rate_dialog_show");

        String title = AdeskOnlineConfigUtil.getConfigParams(context, "rate_title");
        String desc = AdeskOnlineConfigUtil.getConfigParams(context, "rate_desc");
        String ok = AdeskOnlineConfigUtil.getConfigParams(context, "rate_ok");
        String cancel = AdeskOnlineConfigUtil.getConfigParams(context, "rate_cancel");

        SweetAlertDialog dialog = new SweetAlertDialog(context,
                SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(desc)
                .setConfirmText(ok)
                .setConfirmClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                launchAppDetail(context, context.getPackageName(), "");
                                ana(context, "rate_dialog_open_market");
                            }
                        })
                .setCancelText(cancel)
                .setCancelClickListener(
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                ana(context, "rate_dialog_open_cancel");
                            }
                        });
        dialog.show();

        return true;
    }

    public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) {
                return;
            }
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            recordTime(context);
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof ActivityNotFoundException) {
                Toast.makeText(context, "您还没有安装 Google Play", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
