package com.novv.dzdesk.video.op.model;

import android.content.Context;
import android.util.DisplayMetrics;

public class DeviceUtil {

    /**
     * 获取DisplayMetrics
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null) {
            return null;
        }
        return context.getResources().getDisplayMetrics();
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
}
