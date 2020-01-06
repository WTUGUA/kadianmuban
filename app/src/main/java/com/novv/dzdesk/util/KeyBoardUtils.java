package com.novv.dzdesk.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lijianglong on 2017/9/4.
 */

public class KeyBoardUtils {

    /**
     * 隐藏软键盘
     */
    public static void hideInputSoftKeybord(Context context, View v) {
        Context applicationContext = context.getApplicationContext();
        InputMethodManager inputManager = (InputMethodManager) applicationContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 显示软键盘。
     */
    public static void showInputSoftKeybord(Context context, final View v) {
        Context applicationContext = context.getApplicationContext();
        final InputMethodManager inputManager = (InputMethodManager) applicationContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        Timer timer = new Timer();
        // 延迟300毫秒是为了让界面加载完毕，否则弹出软键盘无效
        timer.schedule(new TimerTask() {
            public void run() {
                inputManager.showSoftInput(v, 0);
            }
        }, 300);
    }
}
