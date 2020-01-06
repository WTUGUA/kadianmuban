package com.novv.dzdesk.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by LingYFH on 3/28/14.
 */

public class ToastUtil {

    private static final String tag = "ToastUtil";

    private static Toast showNoMoreToast;

    public static void showToast(Context context, CharSequence msg) {
        try {
            if (showNoMoreToast == null) {
                showNoMoreToast = Toast.makeText(context, msg,
                        Toast.LENGTH_SHORT);
                showNoMoreToast.show();
            } else {
                showNoMoreToast.setText(msg);
                showNoMoreToast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public static void showToast(Context context, int rsID) {
        try {
            showToast(context, context.getResources().getString(rsID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showLongTimeToast(Context context, CharSequence msg) {
        try {
            if (showNoMoreToast == null) {
                showNoMoreToast = Toast.makeText(context, msg,
                        Toast.LENGTH_LONG);
                showNoMoreToast.show();
            } else {
                showNoMoreToast.setText(msg);
                showNoMoreToast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public static void showLongTimeToast(Context context, int rsID) {
        try {
            showLongTimeToast(context, context.getResources().getString(rsID));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showGeneralToast(Context context, int resid) {
        try {
            showGeneralToastWorker(context,
                    context.getResources().getString(resid), Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public static void showGeneralToastLongTime(Context context, int resid) {
        try {
            showGeneralToastWorker(context,
                    context.getResources().getString(resid), Toast.LENGTH_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public static void showGeneralToast(Context context, final CharSequence msg) {
        try {
            showGeneralToastWorker(context, msg, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    public static void showGeneralToastLongTime(Context context,
            final CharSequence msg) {
        try {
            showGeneralToastWorker(context, msg, Toast.LENGTH_LONG);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private static void showGeneralToastWorker(final Context context,
            final CharSequence msg, final int duration) {
        if (context == null) {
            return;
        }
        try {
            if (Util.isOnMainThread()) {
                LogUtil.i(tag, "showGeneralToast isOnMainThread");
                Toast.makeText(context, msg, duration).show();
                return;
            }
            LogUtil.i(tag, "showGeneralToast is not OnMainThread");
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, duration).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

}
