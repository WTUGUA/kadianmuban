package com.novv.dzdesk.res.model;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.novv.dzdesk.util.AppManageHelper;
import com.novv.dzdesk.util.LogUtil;

/**
 * 生命周期管理 Created by lijianglong on 2017/9/19.
 */

public class SwitchBackgroundCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        AppManageHelper.pushActivity(activity);
        LogUtil.i("Lifecycle", "push--->" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (null == AppManageHelper.mActivityList || AppManageHelper.mActivityList.isEmpty()) {
            return;
        }
        if (AppManageHelper.mActivityList.contains(activity)) {
            LogUtil.i("Lifecycle", "pop--->" + activity.getClass().getSimpleName());
            AppManageHelper.popActivity(activity);
        }
    }
}
