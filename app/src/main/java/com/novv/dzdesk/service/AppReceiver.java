package com.novv.dzdesk.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.dict.Utils;
import com.umeng.analytics.MobclickAgent;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getData() != null) {
            if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogUtils.i("安装" + packageName);
                if (TextUtils.equals(packageName, "com.adesk.wpplugin")) {
                    if (Utils.getContext().getAssets() != null) {
                        MobclickAgent.onEvent(Utils.getContext(), "install_plugin");
                    }
                    try {
                        Intent intent1 = new Intent(Intent.ACTION_MAIN);
                        ComponentName componentName = new ComponentName(packageName,
                                packageName + ".ui.SchemeActivity");
                        intent1.setComponent(componentName);
                        intent1.setPackage(context.getPackageName());
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogUtils.i("替换" + packageName);
            } else if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REMOVED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                LogUtils.i("卸载" + packageName);
            }
        }
    }
}
