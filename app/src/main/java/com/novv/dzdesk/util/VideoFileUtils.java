package com.novv.dzdesk.util;

import com.novv.dzdesk.Const;

import java.io.File;

public class VideoFileUtils {

    public static String getLocalFinishVideoDir() {
        File file = new File(Const.Dir.LOCAL_FINISH_VIDEO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        if (!file.exists()) {
            return "";
        }

        return Const.Dir.LOCAL_FINISH_VIDEO_PATH;
    }

    public static String getLocalFinishVideoPath(String name) {
        return getLocalFinishVideoDir() + File.separator + name + ".mp4";
    }

    public static boolean existsLocalFinishVideo(String path) {
        File file = new File(path);
        return file.exists();
    }
}
