package com.novv.dzdesk.util;

/**
 * Created by lijianglong on 2017/8/24.
 */

public class VideoTypeUtils {

    public static Integer checkVideoType(String PATH) {
        String type = "";
        try {
            type = PATH.substring(PATH.lastIndexOf(".") + 1, PATH.length())
                    .toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
        switch (type) {
            case "avi":
                return 0;
            case "mpg":
                return 0;
            case "wmv":
                return 0;
            case "3gp":
                return 0;
            case "mov":
                return 0;
            case "mp4":
                return 9;//本身是MP4格式不用转换

            case "asf":
                return 0;
            case "asx":
                return 0;
            case "flv":
                return 0;

            // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
            case "wmv9":
                return 1;
            case "rm":
                return 1;
            case "rmvb":
                return 1;

            case "":
                return 1;
        }
        return 9;
    }

    public static String getVideoType(String PATH) {
        try {
            return PATH.substring(PATH.lastIndexOf("."), PATH.length())
                    .toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ".mp4";
    }
}
