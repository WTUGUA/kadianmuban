package com.novv.dzdesk.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.longyun.adsdk.utils.FileProvider7;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;

import java.io.File;
import java.util.List;

public class ShareUtils {

    public static void shareOriginal(Activity activity, String imgPath, ResourceBean resourceBean) {

        String title =
                TextUtils.isEmpty(resourceBean.getName()) ? activity.getString(R.string.app_name)
                        : resourceBean.getName() + "-视频壁纸";
        String summary = activity.getString(R.string.share_desc);
        String appName = activity.getString(R.string.app_name);

        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("textList/plain");
        } else {
            File f = new File(imgPath);
            if (f.exists() && f.isFile()) {
                intent.setType("imageList/jpg");
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider7.getUriForFile(activity,f));
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, summary);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, appName));
    }

    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }
}
