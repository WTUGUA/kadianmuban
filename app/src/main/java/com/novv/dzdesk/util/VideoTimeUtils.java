package com.novv.dzdesk.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 视频时间转换类 Created by lijianglong on 2017/8/10.
 */

@SuppressWarnings("ALL")
public class VideoTimeUtils {

    public static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "99:59:59";
                }
                minute = minute % 60;
                second = (int) (seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String convertSecondsToTimeSimple(long seconds) {
        String timeStr = null;
        int minute = 0;
        int second = 0;
        if (seconds <= 0) {
            return "00:00";
        } else {
            minute = (int) seconds / 60;
            if (minute < 60) {
                second = (int) seconds % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                return "59:59";
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }
        return retStr;
    }

    /**
     * 生成随机文件名：当前年月日时分秒+五位随机数
     *
     * @return name
     */
    public static String getRandomFileName() {

        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

        return rannum + str;// 当前时间
    }
}
