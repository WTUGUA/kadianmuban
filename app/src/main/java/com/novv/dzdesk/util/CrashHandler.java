package com.novv.dzdesk.util;

import android.content.Context;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    volatile private static CrashHandler sCrashHandler;

    private CrashHandler(Context context) {
        mContext = context;
    }

    public static CrashHandler getInstance(Context context){
        if (sCrashHandler == null) {
            synchronized (CrashHandler.class){
                if (sCrashHandler == null) {
                    //使用Application Context
                    sCrashHandler=new CrashHandler(context.getApplicationContext());
                }
            }
        }
        return sCrashHandler;
    }


    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //可根据情况选择是否干掉当前的进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}