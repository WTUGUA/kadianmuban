package com.novv.dzdesk.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.ark.adkit.basics.utils.AppUtils;

/**
 * Created by lijianglong on 2018/8/30.
 */

public class APPAlertUtils {

    private static final String pkgAvatar = "com.ants.avatar";
    private static final String pkgRings = "com.adesk.ring";

    public interface OnAlertListener {

        void onAlertNext();
    }

    public void handleAvatar(final Context context, OnAlertListener onAlertListener) {
        if (AppUtils.checkApkExist(context, pkgAvatar)) {
            onAlertListener.onAlertNext();
            return;
        }
        new AlertDialog.Builder(context)
                .setMessage("是否下载头像体验完整功能?")
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UmUtil.anaEvent(context.getApplicationContext(), UmConst.GOTO_STORE_AVATAR);
                        goToPlayStore(context, pkgAvatar);
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    public void handleRings(final Context context, OnAlertListener onAlertListener) {
        if (AppUtils.checkApkExist(context, pkgRings)) {
            onAlertListener.onAlertNext();
            return;
        }
        new AlertDialog.Builder(context)
                .setMessage("是否下载铃声来了体验完整功能?")
                .setPositiveButton("下载", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UmUtil.anaEvent(context.getApplicationContext(), UmConst.GOTO_STORE_RINGS);
                        goToPlayStore(context, pkgRings);
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    public static void goToPlayStore(Context context, String pkg) {
        try {
            Intent storeIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + pkg));
            storeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(storeIntent);
        } catch (android.content.ActivityNotFoundException anfe) {
            try {
                Intent storeIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + pkg));
                storeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(storeIntent);
            } catch (Exception e) {
                Toast.makeText(context, "没有检查到应用市场", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
