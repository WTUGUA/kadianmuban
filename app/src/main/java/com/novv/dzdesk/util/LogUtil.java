package com.novv.dzdesk.util;

import android.util.Log;
import com.novv.dzdesk.Const;

public class LogUtil {

    public static final int UNKNOWN = 0;
    public static final int DEFAULT = 1;
    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int FATAL = 7;
    public static final int SILENT = 8;

    private static int logLevel = DEFAULT;

    private LogUtil() {
    }

    public static native void setNativeLogLevel(int level);

    public static void setLogLevel(int level) {
        logLevel = level;
    }

    /**
     * Send a {@link #DEBUG} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg) {
        if (logLevel <= DEBUG && Const.PARAMS.DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * Send a {@link #DEBUG} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void d(String tag, String msg, Throwable tr) {
        if (logLevel <= DEBUG && Const.PARAMS.DEBUG) {
            Log.d(tag, msg, tr);
        }
    }

    public static void d(Object object, String method) {
        d(object, method, "called");
    }

    public static void d(Object object, String method, String info) {
        d(object.getClass().getSimpleName(), "[" + method + "]:" + info);
    }

    /**
     * Send an {@link #INFO} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg) {
        if (logLevel <= INFO && Const.PARAMS.DEBUG) {
            Log.i(tag, msg);
        }
    }

    /**
     * Send a {@link #INFO} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void i(String tag, String msg, Throwable tr) {
        if (logLevel <= INFO && Const.PARAMS.DEBUG) {
            Log.i(tag, msg, tr);
        }
    }

    public static void i(Object object, String method) {
        i(object, method, "called");
    }

    public static void i(Object object, String method, String info) {
        i(object.getClass().getSimpleName(), "[" + method + "]:" + info);
    }

    /**
     * Send a {@link #WARN} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg) {
        if (logLevel <= WARN && Const.PARAMS.DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void w(String tag, String msg, Throwable tr) {
        if (logLevel <= WARN && Const.PARAMS.DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    public static void w(Object object, String method) {
        w(object, method, "called");
    }

    public static void w(Object object, String method, String info) {
        w(object.getClass().getSimpleName(), "[" + method + "]:" + info);
    }

    /**
     * Send an {@link #ERROR} log message.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg) {
        if (logLevel <= ERROR && Const.PARAMS.DEBUG) {
            Log.e(tag, msg);
        }
    }

    /**
     * Send a {@link #ERROR} log message and log the exception.
     *
     * @param tag Used to identify the source of a log message. It usually identifies the class or
     *            activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr  An exception to log
     */
    public static void e(String tag, String msg, Throwable tr) {
        if (logLevel <= ERROR && Const.PARAMS.DEBUG) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(Object object, String method) {
        e(object, method, "called");
    }

    public static void e(Object object, String method, String info) {
        e(object.getClass().getSimpleName(), "[" + method + "]:" + info);
    }

}
