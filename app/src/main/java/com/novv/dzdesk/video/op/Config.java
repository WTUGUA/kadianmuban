package com.novv.dzdesk.video.op;

import com.novv.dzdesk.util.PathUtils;

public class Config {

    public static final String VIDEO_STORAGE_DIR = PathUtils.getVideoEditTempDir();
    public static final String EDITED_FILE_PATH = VIDEO_STORAGE_DIR + "/edited.mp4";
    public static final String EDITED_FILE_PATH_9 = VIDEO_STORAGE_DIR + "/edited_9.mp4";
    public static final String TRIM_FILE_PATH = VIDEO_STORAGE_DIR + "/trimmed.mp4";
}
